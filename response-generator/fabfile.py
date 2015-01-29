from fabric.context_managers import cd, settings, prefix
from fabric.contrib.files import exists
from fabric.decorators import task
from fabric.operations import require, sudo, run, put, local
from fabric.state import env

"""
Base configuration
"""
env.user = 'cloudscale'
env.path = '/home/%(user)s' % env
env.virtualenvwrapper = '/usr/local/bin/virtualenvwrapper.sh'
env.branch = 'master'
env.hosts = ['127.0.0.1']
env.project = 'response_generator'

@task
def setup():
    setup_os()
    setup_directories()
    setup_virtualenv()

@task
def deploy():
    env.path = '%(path)s/%(project)s' % env

    package()
    upload()
    symlink()
    permissions()
    install_requirements()

#    staticfiles()
#    django_settings()
    config()

    try:
        for callback in env.callbacks:
            callback()
    except:
        pass


def permissions():
    with cd('%(path)s/releases/current/' % env):
        if not exists('debug.log'):
            run('touch debug.log')

        sudo('chown %(user)s:%(user)s debug.log' % env)
        sudo('chmod 777 debug.log')

def setup_os():
    sudo('apt-get install -y python-setuptools')
    sudo('easy_install pip')
    sudo('apt-get install supervisor')
    sudo('apt-get install python-dev')
    sudo('pip install virtualenv')
    sudo('apt-get install libmysqlclient-dev')
    sudo('apt-get install -y git')
    sudo('apt-get install -y nginx')
    sudo('aptitude install -y build-essential')
    sudo('apt-get install -y openjdk-7-jdk')
    sudo('apt-get install -y maven')

def setup_directories():
    run("mkdir -p %(path)s/%(project)s" % env)
    with cd('%(path)s/%(project)s' % env):
        run("mkdir -p logs" % env)
        run("mkdir -p releases" % env)
        run("mkdir -p packages" % env)

def setup_virtualenv():
    with cd('%(path)s/%(project)s' % env):
        run('virtualenv --no-site-packages env')

def symlink():
    require('release')

    with cd('%(path)s/releases' % env):
        if exists('previous'):
            run('rm previous')
        if exists('current'):
            run('mv current previous')
        run('ln -s %(release)s current' % env)

def upload():
    require('archive_name')
    require('release')

    run('mkdir -p %(path)s/releases/%(release)s/' % env)
    put(('%(archive_name)s' % env), ('%(path)s/packages/' % env))
    with cd('%(path)s/releases/%(release)s/' % env):
        run('tar zxf ../../packages/%(archive_name)s' % env)
    local('/bin/rm %(archive_name)s' % env)

def package():
    import time
    env.release = time.strftime('%Y%m%d%H%M%S')
    env.archive_name = '%(release)s.tar.gz' % env
    local('/usr/bin/git archive --format=tar %(branch)s | /bin/gzip > %(archive_name)s' % env)

def install_requirements():
    require('release')
    require('project')

    with settings(virtualhost_path = '%(path)s/env' % env):
        with cd('%(virtualhost_path)s' % env):
            with prefix('source %(virtualhost_path)s/bin/activate' % env):
                run('%(virtualhost_path)s/bin/pip install -r %(path)s/releases/%(release)s/requirements.txt' % env)

def staticfiles():
    with cd('%(path)s/releases/current/' % env):
        with prefix('source %(path)s/env/bin/activate' % env):
            sudo('%(path)s/env/bin/python manage.py collectstatic --noinput' % env)



def django_settings():
    require('project')
    sudo('rm %(path)s/releases/current/webservice/settings_local.py*' % env)
    sudo('rm %(path)s/releases/current/webservice/settings.pyc*' % env)


def config():
    require('project')

    config_flask()
    config_supervisor()
 #   config_nginx()

def config_flask():
    with cd('%(path)s/releases/current/' % env):
        run('cp config.production.py config.py')

def config_supervisor():
    require('project')

    supervisor_conf_path = '%(path)s/releases/current/configs/supervisor.conf' % env
    #supervisor_celery_conf_path = '%(path)s/releases/current/configs/supervisor_celery.conf' % env

    sudo('cp %s /etc/supervisor/conf.d/%s.conf' % (supervisor_conf_path, env.project))
    #sudo('cp %s /etc/supervisor/conf.d/%s_celery.conf' % (supervisor_celery_conf_path, env.project))
    sudo('supervisorctl -c /etc/supervisor/supervisord.conf restart %(project)s' % env)
    sudo('supervisorctl update')

def config_nginx():
    require('project')

    nginx_conf_path = '%(path)s/releases/current/configs/nginx.conf' % env
    sudo('cp %s /etc/nginx/sites-available/%s.conf' % (nginx_conf_path, env.project))
    sudo('ln -sf /etc/nginx/sites-available/%(project)s.conf /etc/nginx/sites-enabled/' % env)
    sudo('service nginx restart')