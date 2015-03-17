import logging
logging.basicConfig(filename='deployment_scripts.log', level=logging.INFO)

class Logger:
    def __init__(self):
        pass

    def info(self, msg):
        logging.info(msg)

    def log(self, msg, level=logging.INFO, append_to_last=False, fin=False):
        logging.log(level, msg)

    def debug(self, msg):
        logging.debug(msg)

    def error(self, msg):
        logging.error(msg)

    def warning(self, msg):
        logging.warning(msg)