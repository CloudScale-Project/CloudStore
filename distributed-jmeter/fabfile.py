from __future__ import with_statement
from fabric.api import sudo, cd, run, settings, require, env, put, local, prefix, task
from fabric.contrib.files import exists

env.hosts = ['host'] 
env.user = 'distributedjmeter'
env.django_app = 'webapp'
# tasks
@task
def new():
    env.process_name = 'distributed_jmeter'
    env.celery_process_name = 'celery'
    env.user = 'distributedjmeter'
    env.project_name = 'webapp'
    env.postfix=''
    env.path = '/home/%(user)s/%(project_name)s' % env
    env.virtualhost_path = '%s%s' % (env.path, '/env')

@task
def deploy(install=False, migrate_db=False, branch=None):
    """
    Deploy the latest version (from env.project_branch or specified branch) of the site
    to the servers, install any required third party modules,
    install the virtual host (if forced with 'install=True', collect the static files,
    symlink the media files and then restart the webserver.
    """
    import time
    with settings(release = time.strftime('%Y%m%d%H%M%S')):
        upload_tar_from_git(branch)
        install_requirements()
        symlink_current_release()
        sudo('touch %(path)s/releases/current/scraper_app.log' % env)
        sudo('chmod 777 %(path)s/releases/current/scraper_app.log' % env)

        with cd('%(path)s/releases/current/%(project_name)s/conf/' % env):
            run('cp confi.ini.production config.ini')

        if install:
            install_site()
            migrate(install)
        if migrate_db:
            migrate(True)
    #collect_static()
    graceful_restart()
    if install:
        restart_webserver()

@task
def rollback():
    """
    Limited rollback capability. Simple loads the previously current
    version of the code. Rolling back again will swap between the two.
    """
    require('path')
    with cd('%(path)s/releases' % env):
        run('mv current _previous;')
        run('mv previous current;')
        run('mv _previous previous;')
    collect_static()
    #symlink_media()
    restart_webserver()


# Helpers. These are called by other functions rather than directly
def upload_tar_from_git(branch=None):
    "Create an archive from the specified Git branch and upload it"
    require('release')
    require('path')
    require('project_name')

    with settings(archive_name = '%(release)s.tar.gz' % env):
        local('/usr/bin/tar czf ../%(archive_name)s .' % env)

        run('mkdir -p %(path)s/releases/%(release)s/%(project_name)s' % env)
        put(('../%(archive_name)s' % env), ('%(path)s/packages/' % env))
        with cd('%(path)s/releases/%(release)s/%(project_name)s' % env):
            run('tar zxf ../../../packages/%(archive_name)s' % env)
            with cd('%(path)s/releases/%(release)s/%(project_name)s/%(django_app)s' % env):
                run('cp local_settings.py.production%(postfix)s local_settings.py' % env)
        local('/bin/rm ../%(archive_name)s' % env)

def install_site():
    "Add the virtualhost file to nginx"
    require('release')
    require('project_name')
    with cd('%(path)s/releases/%(release)s/%(project_name)s' % env):
        sudo('cp conf/nginx.conf%(postfix)s /etc/nginx/sites-available/%(project_name)s' % env)
        #sudo('cp conf/nginx.static.conf /etc/nginx/sites-available/%(project_name)s.static' % env)
    if not exists('/etc/nginx/sites-enabled/%(project_name)s' % env):
        with cd('/etc/nginx/sites-enabled'):
            sudo('ln -s ../sites-available/%(project_name)s ./' % env)
            #sudo('ln -s ../sites-available/%(user)s.static ./' % env)
    with cd('%(path)s/releases/%(release)s/%(project_name)s/conf' % env):
        sudo('cp supervisor.conf%(postfix)s /etc/supervisor/conf.d/%(project_name)s.conf' % env)
        sudo('cp celeryd.conf%(postfix)s /etc/supervisor/conf.d/%(project_name)s.celeryd.conf' % env)


def install_requirements():
    "Install the required packages from the requirements file using pip"
    require('release')
    require('project_name')
    with cd('%(virtualhost_path)s' % env):
        with prefix('source %(virtualhost_path)s/bin/activate' % env):
            run('%(virtualhost_path)s/bin/pip install -r %(path)s/releases/%(release)s/%(project_name)s/requirements.txt' % env)

def symlink_current_release():
    "Symlink our current release"
    require('release')
    with cd('%(path)s/releases' % env):
        if exists('previous'):
            run('rm previous')
        if exists('current'):
            run('mv current previous')
        run('ln -s %(release)s current' % env)

def collect_static():
    "Collect static files in its folder"
    require('project_name')
    with cd('%(path)s/releases/current/%(project_name)s' % env):
        with prefix('source %(virtualhost_path)s/bin/activate' % env):
            run('%(virtualhost_path)s/bin/python manage.py collectstatic' % env)

@task
def migrate(install=False):
    "Update the database"
    require('project_name')
    with cd('%(path)s/releases/current/%(project_name)s' % env):
        with prefix('source %(virtualhost_path)s/bin/activate' % env):
            if install:
                run('%(virtualhost_path)s/bin/python manage.py syncdb --all' % env)
                run('%(virtualhost_path)s/bin/python manage.py migrate --fake' % env)
            else:
                run('%(virtualhost_path)s/bin/python manage.py syncdb --all' % env)
                run('%(virtualhost_path)s/bin/python manage.py migrate' % env)

@task
def restart_webserver():
    "Restart the web server"
    sudo('/etc/init.d/nginx reload')

@task
def graceful_restart():
    "Restart the gunicorn processes"
    sudo('supervisorctl restart %(process_name)s' % env)
    sudo('supervisorctl restart %(celery_process_name)s' % env)
