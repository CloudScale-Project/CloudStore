import os
import sys
import uuid
import boto
from cloudscale.deployment_scripts.logger import Logger


def usage(args):
    print 'Usage:\n $ python %s %s' % (sys.argv[0].split("/")[-1], args)

def check_args(num_args, args_desc):
    if len(sys.argv) < num_args+1:
        usage(args_desc)
        exit(0)

def create_user_path(output_directory):
    user_id = uuid.uuid4()
    path = "%s/%s" % (output_directory, user_id)

    if not os.path.exists(path):
        os.makedirs(path)

    return path


def get_cfg_logger(output_directory, config_path):
    cfg = read_config(config_path)

    path = create_user_path(output_directory)

    logger = Logger()
    return path, cfg, logger


def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(os.path.abspath(config_file))

    return cfg