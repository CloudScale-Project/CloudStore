import logging
logging.basicConfig(filename='deployment_scripts.log', level=logging.INFO)

class Logger:
    def __init__(self):
        pass

    def log(self, msg, level=logging.INFO, append_to_last=False, fin=False):
        logging.log(level, msg)
