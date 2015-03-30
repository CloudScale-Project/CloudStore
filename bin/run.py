import logging
import os
import uuid
import sys
from cloudscale.deployment_scripts import deploy
from cloudscale.deployment_scripts.logger import Logger

class MyLogger(Logger):

    def log(self, msg, level=logging.INFO, append_to_last=False, fin=False):
        print msg

if __name__ == "__main__":
    if len(sys.argv) == 3:
        config_path = sys.argv[2]
        infrastructure = sys.argv[1]
        logger = MyLogger()
        results_dir = "%s/results" % os.path.abspath(os.path.dirname(__file__))
        url = deploy(infrastructure, config_path, results_dir, logger)
        print "Showcase is deployed on http://%s/showcase-1-a\n" \
              "\n" \
              "NOTE: You can copy above url to our application for distributed jmeter" % url
    else:
        print """Usage: python run.py <aws|openstack> <path_to_config>"""
