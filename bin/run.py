import os
import uuid
import sys
from cloudscale.deployment_scripts import deploy
from cloudscale.deployment_scripts.logger import Logger

if __name__ == "__main__":
    if len(sys.argv) == 3:
        config_path = sys.argv[2]
        infrastructure = sys.argv[1]
        logger = Logger()
        deploy(infrastructure, config_path, os.path.abspath(os.path.dirname(__file__)), logger)
    else:
        print """Usage: python run.py <aws|openstack> <path_to_config>"""