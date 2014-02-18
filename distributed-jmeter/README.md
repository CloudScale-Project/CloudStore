##Introduction
This is the first version of application that runs on [http://cloudscale.xlab.si/distributed-jmeter/](http://cloudscale.xlab.si/distributed-jmeter/). 

It's a Django 1.6 app so you will need a support for Python 2.7 on your web server. We already provide you with configurations for Gunicorn, Supervisor and Nginx. This is default stack for deploying Django applications.

##Server setup
Install Nginx and Supervisor with:

```
$ sudo apt-get install nginx supervisor
```

You will also need to install Virtualenv. You can do this with:

```
$ pip install virtualenv
```

We shall assume that we are deploying application into `/home/distributedjmeter` directory. Change to this directory and run:

```
$ virtualenv env
```

This command should create directory `env`. Beside that directory also create directories `app, conf, packages and releases`. 

##Installation
On your computer you will need to install Fabric. You can install it with:

```
$ pip install fabric
```

Then edit `fabfile.py` and set:

* `env.hosts` - Hosts where you want to deploy your app.
* `env.user` - User that will run this application.<br />
It's common practice that we create a new user for each application deployment. In our case this would be `distributedjmeter` user.

Leave other properties as they are!

Now run from shell command:

```
$ fab new deploy:install=True
```
