from django.conf.urls import patterns, include, url
from django.conf import settings
from django.contrib import admin
admin.autodiscover()

print 'url prefix:', settings.URL_PREFIX
urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'webapp.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^$',  'cloudscale.views.home', name='home'),
    url(r'^upload/?', 'cloudscale.views.upload', name='upload'),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^report/(?P<id>(.*))$', 'cloudscale.views.report', name='report'),
    url(r'^check/(?P<id>(.*))$', 'cloudscale.views.check', name="check"),
    url(r'^about$', 'cloudscale.views.about', name='about'),
    url(r'^contact$', 'cloudscale.views.contact', name='contact'),
)
