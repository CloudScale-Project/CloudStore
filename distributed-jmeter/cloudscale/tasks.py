from __future__ import absolute_import
from celery import shared_task, task
import os, subprocess
import logging
from .aws_distributed_jmeter import CreateInstance
from .aws_distributed_jmeter import read_config
from .openstack_distributed_jmeter import OpenStackDistributedJmeter
from math import floor, ceil
import novaclient.v1_1 as novaclient

logger = logging.getLogger(__name__)

@shared_task
def run_tests(scenario_path, instance_type, num_threads, host):
    num_jmeter_slaves, startup_threads, rest_threads = calculate(num_threads)

    cfg = write_config('EC2', instance_type, num_jmeter_slaves, startup_threads, rest_threads, host)
    run_aws_test(scenario_path, cfg)

#    cfg = write_config('OPENSTACK', instance_type, num_jmeter_slaves, startup_threads, rest_threads, host)
#    run_openstack_test(scenario_path, cfg)

def calculate(num_threads):
    num_users_per_jmeter_instance = 300
    num_threads = int(num_threads)
    num_jmeter_slaves = int(ceil(num_threads/(num_users_per_jmeter_instance*1.0)))

    startup_threads = int((num_threads/10)/num_jmeter_slaves)
    threads_per_slave = int(num_threads/num_jmeter_slaves)

    if (num_jmeter_slaves*num_users_per_jmeter_instance) - num_threads > 0:
        rest_threads = int(threads_per_slave - startup_threads)
    else:
        rest_threads = int(num_users_per_jmeter_instance-startup_threads)
    return num_jmeter_slaves, startup_threads, rest_threads

def write_config(section, instance_type, num_jmeter_slaves, startup_threads, rest_threads, host):
    basedir = os.path.abspath(os.path.dirname(__file__))
    config_path = '%s/../conf/config.ini' % basedir
    cfg = read_config(config_path)

    cfg.save_option(config_path, section, 'instance_type', instance_type)
    cfg.save_option(config_path, section, 'num_jmeter_slaves', str(num_jmeter_slaves))
    cfg.save_option(config_path, section, 'startup_threads', str(startup_threads))
    cfg.save_option(config_path, section, 'rest_threads', str(rest_threads))
    cfg.save_option(config_path, section, 'host', str(host))

    return cfg

def run_openstack_test(scenario_path, cfg):
    OpenStackDistributedJmeter(scenario_path, cfg)

def run_aws_test(scenario_path, cfg):
    CreateInstance(cfg, scenario_path)
