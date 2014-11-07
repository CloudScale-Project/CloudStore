from django.db import models

class Ec2InstanceTypes(models.Model):
    instance_type = models.CharField(max_length=100)

    def __str__(self):
        return self.instance_type

class RDSInstanceTypes(models.Model):
    instance_type = models.CharField(max_length=100)

    def __str__(self):
        return self.instance_type


