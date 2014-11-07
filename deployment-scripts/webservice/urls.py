from django.conf.urls import patterns, include, url

from django.contrib import admin
from django.views.generic import TemplateView
import form

admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', TemplateView.as_view(template_name='form/home.html'), name='home'),
    url(r'^form/', include('form.urls'), name='form'),
    url(r'^results/(?P<task_id>.+)$', 'results.views.task', name='task'),
    url(r'^admin/', include(admin.site.urls)),
)
