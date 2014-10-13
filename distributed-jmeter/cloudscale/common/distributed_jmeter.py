import logging
from cloudscale import models
import time

class DistributedJmeter(object):

    def __init__(self, scenario_path):
        self.pid = str(scenario_path.split('/')[-1][:-4])

    def log(self, msg, fin=0):
        db_log = models.Log()
        db_log.process_id = self.pid
        db_log.log = "[%s] %s" % (time.strftime("%H:%M:%S"), msg)
        db_log.finished = fin
        db_log.save()

    def clear(self):
        msgs = models.Log.objects.filter(process_id=self.pid)
        for obj in msgs:
            obj.delete()
