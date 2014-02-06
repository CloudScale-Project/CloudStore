import boto
import sys, os

def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(os.path.abspath(config_file))

    return cfg

def usage(args):
    print 'Usage:\n $ python %s %s' % (sys.argv[0].split("/")[-1], args)

def check_args(num_args, args_desc):
    if len(sys.argv) < num_args+1:
        usage(args_desc)
        exit(0)

def parse_args():
    config_file = sys.argv[1]

    if not os.path.isfile(config_file):
        print config_file + ' doesn\'t exist!'
        exit(0)

    cfg = read_config(config_file)
    key_name = cfg.get('EC2', 'key_name')
    key_pair = os.path.abspath(cfg.get('EC2', 'key_pair'))
    if not os.path.isfile(key_pair):
        print key_pair + ' doesn\'t exist!'
        exit(0)

    return config_file, cfg, key_name, key_pair

