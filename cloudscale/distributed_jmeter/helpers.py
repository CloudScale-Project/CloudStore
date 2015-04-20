import os
import boto

def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(os.path.abspath(config_file))

    return cfg