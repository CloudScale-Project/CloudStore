from django.conf.urls import patterns, url, include
from django.views.generic import TemplateView
from form import views

angular_patterns = patterns('',
    url(r'^form-step1.html$', TemplateView.as_view(template_name='form/form-step1.html'), name="form-step1"),
    url(r'^form-step2.html$', TemplateView.as_view(template_name='form/form-step2.html'), name="form-step2"),
    url(r'^form-step3.html$', TemplateView.as_view(template_name='form/form-step3.html'), name="form-step3"),
    url(r'^form-step4.html$', TemplateView.as_view(template_name='form/form-step4.html'), name="form-step4"),
    url(r'^form-step5.html$', TemplateView.as_view(template_name='form/form-step5.html'), name="form-step5"),
    url(r'^form-step6.html$', TemplateView.as_view(template_name='form/form-step6.html'), name="form-step6"),
    url(r'^form-step7.html$', TemplateView.as_view(template_name='form/form-step7.html'), name="form-step7")
)

urlpatterns = patterns('',
    url(r'^$', views.form, name='form'),
    url(r'^', include(angular_patterns), name='angular-patterns'),
    url(r'^finish$', views.form_finish, name='form-finish'),
    url(r'^ec2-instance-types$', views.ec2_instance_types, name='ec2-instance-types'),
    url(r'^rds-instance-types$', views.rds_instance_types, name='rds-instance-types'),
    url(r'^get-openstack-data$', views.get_openstack_data, name='get-openstack-data'),
)