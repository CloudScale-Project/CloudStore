import subprocess
from novaclient.v2 import client as novaclient
import time
import boto


def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(config_file)
    return cfg


class Checker:
    def __init__(self):
        self.cfg = read_config('config.ini')

        self.user = self.cfg.get('OPENSTACK', 'username')
        self.pwd = self.cfg.get('OPENSTACK', 'password')
        self.url = self.cfg.get('OPENSTACK', 'auth_url')
        self.tenant = self.cfg.get('OPENSTACK', 'tenant_name')

        self.nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)

        self.showcase_server_ips = []

        while True:
            self.check()
            time.sleep(5)

    def get_ip(self, server):
        for address in server.addresses['private']:
            if address['OS-EXT-IPS:type'] == 'fixed':
                server_ip = address['addr']
                break
        else:
            server_ip = None
            print "Error: can not get IP address of this server"
        return server_ip

    def check(self):
        running_server_ips = []
        new_server_ips = []

        servers = self.nc.servers.findall(name='cloudscale-sc')
        ps = {}
        for server in servers:
            if server.status == 'ACTIVE':
                server_ip = self.get_ip(server)
                if server_ip in self.showcase_server_ips:
                    running_server_ips.append(server_ip)
                else:
                    new_server_ips.append(server_ip)
                    ps[server_ip] = subprocess.Popen('curl -I --silent %s > /dev/null' % server_ip, shell=True)
        for server_ip in new_server_ips:
            ps[server_ip].wait()
            if ps[server_ip].returncode == 0:
                running_server_ips.append(server_ip)

        if not set(self.showcase_server_ips) == set(running_server_ips):
            self.showcase_server_ips = running_server_ips
            self.update_config()
            self.reload_haproxy()

    def update_config(self):
        ha_proxy_config = open('/etc/haproxy/haproxy.cfg_NO_SERVERS', 'r').read()
        for server_ip in self.showcase_server_ips:
            ha_proxy_config += """
    server %s %s:80 check""" % (server_ip, server_ip)
        open('/etc/haproxy/haproxy.cfg', 'w').write(ha_proxy_config)

    def reload_haproxy(self):
        print "config changed... reloading haproxy"
        ps = subprocess.Popen('service haproxy reload', shell=True)
        ps.wait()


if __name__ == '__main__':
    checker = Checker()
