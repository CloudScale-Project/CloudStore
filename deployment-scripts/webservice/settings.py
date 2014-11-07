"""
Django settings for webservice project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
import sys

BASE_DIR = os.path.dirname(os.path.dirname(__file__))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '_6dtw5%4bwz32*8^n#(+ywu%h#048u%czmw*y%no)cz&z5pdn6'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = False

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = ['dummy-host.com']


# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'form',
    'workers',
    'common',
    'workers',
    'results',
    'south',
    'gunicorn'
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'webservice.urls'

WSGI_APPLICATION = 'webservice.wsgi.application'


# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
STATIC_ROOT = '/path/to/webapp/static'

STATICFILES_DIRS = (
    os.path.join(BASE_DIR, "static"),
    '%s/static/' % BASE_DIR
)

#TEMPLATE_DIRS = [os.path.join(BASE_DIR, 'form/templates'),
#                 os.path.join(BASE_DIR, 'distributedjmeter/templates')]
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'handlers': {
        'file': {
            'level': 'INFO',
            'class': 'logging.FileHandler',
            'filename': 'debug.log',
        },
        'console' : {
            'level' : 'INFO',
            'class' : 'logging.StreamHandler',
            'stream' : sys.stdout
        }
    },
    'loggers': {
        # '' : {
        #   'handlers' : ['file', 'console'],
        #   'level' : 'INFO',
        #   'propagate' : True,
        # },
        # '' : {
        #   'handlers' : ['file', 'console'],
        #   'level' : 'DEBUG',
        #   'propagate' : True,
        # },
        '' : {
          'handlers' : ['file', 'console'],
          'level' : 'INFO',
          'propagate' : True,
        },
    },
}

CELERY_ALWAYS_EAGER = False
MUTE = False

try:
    from settings_local import *
except Exception as e:
    pass
