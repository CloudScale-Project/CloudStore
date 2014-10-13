import os
from settings import BASE_DIR

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'distributedjmeter',
        'USER': 'user',
        'PASSWORD': 'password',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'cloudscale',
    'gunicorn',
)
FORCE_SCRIPT_NAME=''
URL_PREFIX = '/distributed-jmeter'
MEDIA_ROOT = '{0}/../media/'.format(BASE_DIR)
STATIC_ROOT = '{0}/../static/'.format(BASE_DIR)
STATIC_URL = '{0}/static/'.format(URL_PREFIX)

CELERY_ALWAYS_EAGER = False

