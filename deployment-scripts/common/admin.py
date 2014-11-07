from django.contrib import admin
from common import models

admin.site.register(models.Task)
admin.site.register(models.Log)