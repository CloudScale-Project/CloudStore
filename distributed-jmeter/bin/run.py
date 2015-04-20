#!/usr/bin/python

import sys
import os
from cloudscale.distributed_jmeter import run_test
from cloudscale.distributed_jmeter.logger import Logger
from cloudscale.distributed_jmeter.scripts import meet_sla_req

if __name__ == "__main__":

    if len(sys.argv) > 1:
        infrastructure = sys.argv[1]
        config_path = sys.argv[2]
        scenario_path = sys.argv[3]
        logger = Logger("distributed_jmeter.log")

        results_path = run_test.run_test(infrastructure, config_path, scenario_path, "%s/results" % os.path.abspath(os.path.dirname(__file__)), logger)

        with open("%s/SLO_violations" % results_path, "w") as fp:
            output = meet_sla_req.check("%s/response-times-over-time.csv" % results_path)
            fp.write(output)

        print "See results in %s" % results_path
    else:
        print """Usage: python run.py <aws|openstack> <path_to_config> <path_to_scenario>"""
