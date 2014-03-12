from __future__ import absolute_import
from celery import shared_task, task
import os, subprocess
import logging
from cloudscale.aws_distributed_jmeter import CreateInstance
from cloudscale.aws_distributed_jmeter import read_config
from cloudscale.openstack_distributed_jmeter import OpenStackDistributedJmeter
logger = logging.getLogger(__name__)

@shared_task
def run_tests(scenario_path):
#    run_aws_test(scenario_path)
    run_openstack_test(scenario_path)

def run_openstack_test(scenario_path):
    OpenStackDistributedJmeter('10.32.11.102', ['10.32.11.103:8557', '10.32.11.104:8557'], scenario_path)

def run_aws_test(scenario_path):
    basedir = os.path.abspath(os.path.dirname(__file__))
    config_path = '%s/../conf/config.ini' % basedir
    cfg = read_config(config_path)
    key_name = cfg.get('EC2', 'key_name')
    key_pair = cfg.get('EC2', 'key_pair')
    CreateInstance(config_path, cfg, key_pair, key_name, scenario_path, 2)
