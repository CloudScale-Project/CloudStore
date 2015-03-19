from distutils.core import setup
from setuptools import find_packages

setup(
    name='cloudscale-deployment-scripts',
    version='0.1.0',
    author='Simon Ivansek',
    author_email='simon.ivansek@xlab.si',
    packages=find_packages(),
    package_data={'' : ['*.cfg', '*.sh', '*.conf']},
    url='http://www.cloudscale-project.eu',
    license='LICENSE.txt',
    description='Deployment scripts for CloudScale project',
    long_description=open('README.txt').read(),
    include_package_data=True,
    install_requires=[
        "boto==2.36.0",
        "python-novaclient==2.22.0",
    ],
)