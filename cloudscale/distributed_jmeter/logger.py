import logging

class Logger:

    def __init__(self, filename):
        logging.basicConfig(filename=filename,level=logging.DEBUG)

    def log(self, msg, level=logging.DEBUG, append_to_last=False, fin=False):
        logging.log(level, msg)