
import logging
import models
import uuid

logger = logging.getLogger(__name__)


class Logger(object):

    def __init__(self, task):
        self.task = task

    def log(self, msg, level=logging.INFO, append_to_last=False, fin=False):
        logger.log(msg=msg, level=level)
        if append_to_last:
            db_log = models.Log.objects.filter(task=self.task).last()
            db_log.log += msg
        else:
            db_log = models.Log()
            db_log.task = self.task
            db_log.log = msg

        if level == logging.INFO:
            db_log.fin = fin
            db_log.save()

    def clear(self):
        msgs = models.Log.objects.filter(task=self.task)
        for obj in msgs:
            obj.delete()