import os
import shutil

from plotter import Plot
from cloudscale.distributed_jmeter.scripts.visualization.parser import Parse


class Visualize:

    def __init__(self, num_threads, duration, r_file, main_file, autoscaling_file):
        base_filename = main_file[:-4]
        path = os.path.dirname(main_file)

        self.main_file = main_file
        self.parsed_file = base_filename + ".parsed.csv"
        self.merged_file = base_filename + ".merged.csv"
        self.slo_violations_non_agg_file = base_filename + ".slo_non_agg.csv"
        self.slo_violations_agg = base_filename + ".slo_agg.csv"
        self.slo_violations_agg_1second = base_filename + ".slo_agg_1second.csv"
        self.slo_violations_agg_5seconds = base_filename + ".slo_agg_5seconds.csv"
        self.slo_violations_agg_10seconds = base_filename + ".slo_agg_10seconds.csv"
        self.ec2_file = path + "/ec2-cpu.csv"
        self.rds_cpu_file = path + "/rds-cpu.csv"

        data = Parse()
        data.parse(self.parsed_file, self.main_file)
        data.to_dataframe(self.parsed_file, autoscaling_file)
        data.merge(self.merged_file, autoscaling_file, self.parsed_file)
        data.delete_records_that_violates_slo(self.slo_violations_non_agg_file, self.parsed_file)
        data.slo_agg_seconds(self.parsed_file, self.slo_violations_agg, 60)
        data.slo_agg_seconds(self.parsed_file, self.slo_violations_agg_1second, 1)
        data.slo_agg_seconds(self.parsed_file, self.slo_violations_agg_5seconds, 5)
        data.slo_agg_seconds(self.parsed_file, self.slo_violations_agg_10seconds, 10)

        plotter = Plot(num_threads, duration, r_file,
                       self.main_file,
                       self.parsed_file,
                       self.merged_file,
                       self.slo_violations_agg,
                       self.slo_violations_non_agg_file,
                       autoscaling_file,
                       self.slo_violations_agg_1second,
                       self.slo_violations_agg_5seconds,
                       self.slo_violations_agg_10seconds,
                       self.ec2_file,
                       self.rds_cpu_file)

    def save(self):
        return ""

