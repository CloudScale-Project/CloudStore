from django.shortcuts import render, redirect, HttpResponse
from django.contrib import messages
import forms
import os
import uuid
import logging
from cloudscale import models
import json
from django.core.mail import send_mail
from django.conf import settings

logger = logging.getLogger(__name__)

def home(request):
    return render(request, 'home.html', {'form' : forms.UploadScenarioForm(), 'url_prefix' : settings.URL_PREFIX})

def upload(request):
    errors = False
    filename = ""
    if request.method == 'POST':
        form = forms.UploadScenarioForm(request.POST, request.FILES)
        if form.is_valid():
            file = request.FILES['scenario']
            max_file_size = 5242880

            if not str(file.name).endswith('.jmx'):
                messages.error(request, "File it's not .jmx")
                errors = True
            elif file.size > max_file_size:
                messages.error(request, "File is larger than %sMB" % (max_file_size/1048576))
                errors = True
            else:
                filename = handle_uploaded_file(request.FILES['scenario'])
                start_test(filename, request.POST['instance_type'], request.POST['num_threads'], request.POST['host'])
        else:
            messages.error(request, "You didn't fill in the form!")
            errors = True
    else:
        return redirect('/')

    messages.success(request, "Your scenario was successfully uploaded and started. Results are available <a href=\"{1}/report/{0}\">here</a>".format(os.path.basename(filename)[:-4], settings.URL_PREFIX))
    return render(request, 'home.html', {'form' : form, 'errors' : errors})

def handle_uploaded_file(file):
    basedir = os.path.abspath(os.path.dirname(__file__))
    filename = "%s.jmx" % uuid.uuid4()
    scenario_path = basedir + '/../uploads/%s' % filename
    with open(scenario_path, 'w') as destination:
        for chunk in file.chunks():
            destination.write(chunk)
    destination.close()
    return scenario_path

def start_test(scenario_path, instance_type, num_threads, host):
    from tasks import run_tests
    userpath = "{0}/../static/results/{1}".format(os.path.abspath(os.path.dirname(__file__)), os.path.basename(scenario_path)[:-4])
    try:
        os.makedirs(userpath)
    except OSError as e:
        if e.errno != 17:
            raise
        pass
    run_tests.delay(scenario_path, instance_type, num_threads, host)

def report(request, id):
    dir = "{0}/../static/results/{1}".format(os.path.abspath(os.path.dirname(__file__)), id)
    error = None
    if not os.path.exists(dir):
        error = "Request with id {0} doesn't exist!"
    return render(request, 'report.html', {'error' : error, 'id' : id, 'url_prefix' : settings.URL_PREFIX})

def check(request, id):
    response = {}
    if request.is_ajax():

        dir = "{0}/../static/results/{1}".format(os.path.abspath(os.path.dirname(__file__)), id)
        if os.path.exists(dir):
            response['finished'] = False
            response['log_msgs'] = []
            msgs = models.Log.objects.filter(process_id=id)
            for msg in msgs:
                response['log_msgs'].append(msg.log)
                if msg.finished == 1:
                    response['finished'] = True
        else:
            response['error'] = 'Request with id {0} doesn\'t exist'.format(id)
    else:
        response['error'] = 'Only AJAX request are allowed!'
    return HttpResponse(json.dumps(response), content_type="application/json")

def about(request):
    return render(request, 'about.html', {'url_prefix' : settings.URL_PREFIX})

def contact(request):
    if request.method == 'POST':
        if request.POST['your_email'] == '' or request.POST['message'] == '':
            messages.error(request, "You didn't fill all fields!")
        else:
            send_mail("[CloudScale] Query for distributed JMeter", request.POST['message'], request.POST['your_email'],
                  ['simon.ivansek@xlab.si'])
            messages.success(request, "Email was successfully sent")
    return render(request, 'contact.html')
