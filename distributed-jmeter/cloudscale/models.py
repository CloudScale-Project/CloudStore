from django.db import models

class Log(models.Model):
    process_id = models.CharField(max_length=255)
    log = models.TextField()
    finished = models.IntegerField()
