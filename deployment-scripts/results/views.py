import json
from django.http import HttpResponse
from django.shortcuts import render, get_object_or_404
from common.models import Task, Log


def task(request, task_id):
    if request.is_ajax():
        task = get_object_or_404(Task, task_id=task_id)

        logs = Log.objects.filter(task=task)
        fin = False
        for log in logs:
            if log.fin:
                fin = log.fin

        return HttpResponse(json.dumps({'logs' : [log.log for log in logs], 'fin' : fin}), content_type='application/json')
    return render(request, "results.html", {'path' : request.path})