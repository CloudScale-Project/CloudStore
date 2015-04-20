from distutils.core import setup
from setuptools import find_packages
setup(
    name='cloudscale-distributed-jmeter',
    version='0.1.0',
    author='Simon Ivansek',
    author_email='simon.ivansek@xlab.si',
    packages=find_packages(),
    package_data={'cloudscale.distributed_jmeter.scripts.visualization' : ['r_visualization.R']},
    url='http://pypi.python.org/pypi/TowelStuff/',
    license='LICENSE.txt',
    description='Distributed JMeter for CloudScale project',
    long_description=open('README.txt').read(),
    include_package_data=True,
    install_requires=[
        "boto==2.36.0",
        "python-novaclient==2.21.0",
        "paramiko==1.15.2",
        "pandas==0.15.2"
    ],
)
