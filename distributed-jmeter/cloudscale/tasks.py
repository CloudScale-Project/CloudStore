from __future__ import absolute_import
from celery import shared_task, task
import time, threading
import os, subprocess
import logging
import time
import sys
import redis
from cloudscale.aws_distributed_jmeter import CreateInstance
from cloudscale.aws_distributed_jmeter import read_config
from cloudscale.models import Log
logger = logging.getLogger(__name__)

@shared_task
def run_tests(scenario_path, vu):
    basedir = os.path.abspath(os.path.dirname(__file__))
    config_path = '%s/../config.ini' % basedir
    cfg = read_config(config_path)
    key_name = cfg.get('EC2', 'key_name')
    key_pair = cfg.get('EC2', 'key_pair')
    CreateInstance(config_path, cfg, key_pair, key_name, scenario_path, vu, 2)
