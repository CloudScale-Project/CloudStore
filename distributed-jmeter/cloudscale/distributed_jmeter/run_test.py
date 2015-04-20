import os
import uuid
import shutil
from math import ceil
from cloudscale.distributed_jmeter.aws import AWS

from cloudscale.distributed_jmeter.helpers import read_config
from cloudscale.distributed_jmeter.openstack import OpenStack

def run_test(infrastructure, config_path, scenario_path, results_directory, logger):
    if infrastructure != 'aws':
        raise Exception("Not supported!")


    new_scenario_name = uuid.uuid4()
    userpath = "{0}/{1}".format(results_directory, new_scenario_name)

    try:
        os.makedirs(userpath)
    except OSError as e:
        if e.errno != 17:
            raise
        pass

    shutil.copy2('%s/scripts/visualization/r_visualization.R' % os.path.abspath(os.path.dirname(__file__)), userpath)

    r_path = "%s/r_visualization.R" % userpath
    run_tests(config_path, userpath, r_path, scenario_path, infrastructure, logger)
    return userpath

def run_tests(config_path, user_path, r_path, scenario_path, infrastructure, logger):

    cfg = read_config(config_path)
    num_threads = int(cfg.get('SCENARIO', 'num_threads'))
    num_jmeter_slaves, startup_threads, rest_threads = calculate(num_threads)

    try:
        if infrastructure == 'aws':
            cfg = write_config(config_path, user_path, 'TEST', num_threads, num_jmeter_slaves, startup_threads, rest_threads)
            run_aws_test(r_path, user_path, scenario_path, cfg, logger)
        elif infrastructure == 'openstack':
            cfg = write_config(config_path, user_path, 'TEST', num_threads, num_jmeter_slaves, startup_threads, rest_threads)
            run_openstack_test(r_path, user_path, scenario_path, cfg, logger)
    except Exception as e:
        import traceback
        logger.log(traceback.format_exc())
        raise Exception(e)

def calculate(num_threads):
    num_users_per_jmeter_instance = 2000
    num_threads = int(num_threads)
    num_jmeter_slaves = int(ceil(num_threads/(num_users_per_jmeter_instance*1.0)))

    startup_threads = int((num_threads/10)/num_jmeter_slaves)
    threads_per_slave = int(num_threads/num_jmeter_slaves)

    if (num_jmeter_slaves*num_users_per_jmeter_instance) - num_threads > 0:
        rest_threads = int(threads_per_slave - startup_threads)
    else:
        rest_threads = int(num_users_per_jmeter_instance-startup_threads)
    return num_jmeter_slaves, startup_threads, rest_threads

def write_config(config_path, user_path, section, num_threads, num_jmeter_slaves, startup_threads, rest_threads):
    cfg = read_config(config_path)

    cfg.save_option(config_path, section, 'num_threads', str(num_threads))
    cfg.save_option(config_path, section, 'num_jmeter_slaves', str(num_jmeter_slaves))
    cfg.save_option(config_path, section, 'startup_threads', str(startup_threads))
    cfg.save_option(config_path, section, 'rest_threads', str(rest_threads))

    return cfg

def run_openstack_test(r_path, user_path, scenario_path, cfg, logger):
    OpenStack(r_path, scenario_path, user_path, cfg, logger)

def run_aws_test(r_path, output_path, scenario_path, cfg, logger):
    AWS(cfg, scenario_path, r_path, output_path, logger)