from django.db import models

class Task(models.Model):
    task_id = models.CharField(max_length=50)
    is_running = models.BooleanField(default=False)

class Log(models.Model):
    task = models.ForeignKey(Task)
    log = models.TextField()
    fin = models.BooleanField(default=False)
