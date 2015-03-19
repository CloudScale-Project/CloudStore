import os
import uuid
from cloudscale.deployment_scripts.backend import Backend
from cloudscale.deployment_scripts.config import Config
from cloudscale.deployment_scripts.frontend import Frontend
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_remove_all


def deploy(infrastructure, config_path, results_dir, logger):
    config = Config(infrastructure, results_dir, config_path)
    _setup_backend(config, logger)
    return _setup_frontend(config, logger)

def _setup_backend(config, logger):
    backend = Backend(config, logger)

    if config.provider == 'aws':
        backend.setup_rds()
    else:
        openstack_remove_all.RemoveAll(config, logger)
        if config.db_provider == 'mysql':
            backend.setup_openstack_mysql()
        else:
            backend.setup_openstack_mongodb()


def _setup_frontend(config, logger):
    frontend = Frontend(config, logger)

    showcase_url = None
    if config.provider == 'aws':
        showcase_url = frontend.setup_aws_frontend()
    elif config.provider == 'openstack':
        showcase_url = frontend.setup_openstack_frontend()

    return showcase_url

