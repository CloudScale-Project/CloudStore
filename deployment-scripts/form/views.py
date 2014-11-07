import json
import uuid

from django.http import HttpResponse
from django.shortcuts import render
import shutil
from django.views.decorators.csrf import csrf_exempt
import novaclient.v1_1 as novaclient
from common.models import Task
from form.validate import Validate
import workers.tasks
from workers.config import Config

from models import Ec2InstanceTypes, RDSInstanceTypes
from scripts.common.Cloudscale import *
from webservice.settings import BASE_DIR
from common.logger import Logger


def home(request):
    return render(request, "form/home.html")


def form(request):
    if request.session.get('uid') == None:
        request.session['uid'] = str(uuid.uuid4())

    return render(request, "form/form.html", {'uid': request.session.get('uid')})


def ec2_instance_types(request):
    instance_types = Ec2InstanceTypes.objects.all()

    return HttpResponse(json.dumps([i.instance_type for i in instance_types]), content_type='application/json')


def rds_instance_types(request):
    instance_types = RDSInstanceTypes.objects.all()
    return HttpResponse(json.dumps([i.instance_type for i in instance_types]), content_type='application/json')

#@csrf_exempt
def get_openstack_data(request):
    if request.is_ajax():
        data = json.loads(request.POST['formData']) if request.POST.get('formData', None) else json.loads(request.body)
        errors = Validate().flavor(data)
        if len(errors) == 0:
            if data['fr']['own_infrastructure'] == 'yes':
                username = data['fr']['username']
                password = data['fr']['password']
                tenant = data['fr']['tenant']
                auth_url = data['fr']['auth_url']
            else:
                config_path = BASE_DIR + '/scripts/config.ini'
                cfg = read_config(config_path)
                username = cfg.get('OPENSTACK', 'username')
                password = cfg.get('OPENSTACK', 'password')
                tenant = cfg.get('OPENSTACK', 'tenant_name')
                auth_url = cfg.get('OPENSTACK', 'auth_url')
            nc = novaclient.Client(username, password, tenant, auth_url)
            response_data = {'flavors' : [f.name for f in nc.flavors.list()], 'images' : [i.name for i in nc.images.list()]}
            return HttpResponse(content_type="application/json", content=json.dumps(response_data))
        return HttpResponse(content_type="application/json", content=json.dumps({'errors' : errors}), status=500)
    return HttpResponse('Only AJAX request allowed!', status=400)

def form_finish(request):
    errors = []
    if request.is_ajax():
        data = json.loads(request.POST.get('formData')) if request.POST.get('formData', None) else json.loads(request.body)
        errors += Validate().finish(data)
        if len(errors) == 0:
            task = Task(task_id=uuid.uuid4())
            task.save()

            path = '%s/webservice/users/%s' % (BASE_DIR, task.task_id)
            if not os.path.exists(path):
                os.makedirs(path)

            # copy config.ini template to user dir
            shutil.copy2('%s/../scripts/config.ini' % os.path.abspath(os.path.dirname(__file__)), path)
            config = Config(data, path)
            if data['provider'] == 'aws':
                master_identifier = data['db']['master_identifier']
                replica_identifier = data['db']['replica_identifier']
                config.cfg.save_user_option('RDS', 'master_identifier', master_identifier)
                config.cfg.save_user_option('RDS', 'replica_identifier', replica_identifier)

            workers.tasks.setup.delay(config, path, Logger(task))

            return HttpResponse(json.dumps({'task_id' : str(task.task_id)}), status=200, content_type="application/json")
    else:
        errors.append("Only AJAX requests allowed")

    return HttpResponse(json.dumps({'errors': errors}), status=500, content_type='application/json')




