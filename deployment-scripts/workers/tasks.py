from scripts.infrastructure.aws.aws_create_keypair import CreateKeyPair
from scripts.infrastructure.openstack import openstack_remove_all
from webservice.celery import app
from workers.backend import Backend
from workers.config import Config
from workers.frontend import Frontend
import xml.etree.ElementTree as ET

@app.task
def setup(config, user_path, logger):
        setup_backend(config, logger)
        setup_frontend(config, logger)

def setup_backend(config, logger):
    backend = Backend(config, logger)

    if config.provider == 'aws':
        backend.setup_rds()
    else:
        openstack_remove_all.RemoveAll(config, logger)
        if config.db_provider == 'mysql':
            backend.setup_openstack_mysql()
        else:
            backend.setup_openstack_mongodb()


def setup_frontend(config, logger):
    frontend = Frontend(config, logger)

    showcase_url = None
    if config.provider == 'aws':
        showcase_url = frontend.setup_aws_frontend()
    elif config.provider == 'openstack':
        showcase_url = frontend.setup_openstack_frontend()

    logger.log('You can view your showcase on <a href="http://%s/showcase-1-a">http://%s/showcase-1-a</a>' % (showcase_url, showcase_url), fin=True)
