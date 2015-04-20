import datetime
import os
import subprocess


class Plot:

    def __init__(self, num_threads, duration, r_file, main_file, parsed_file, merged_file, slo_violations_file, slo_violations_file_non_agg, autoscaling_file, slo_agg_1second, slo_agg_5seconds, slo_agg_10seconds, ec2_file, rds_cpu_file):
        r_file_new = "%s/%s_new.R" % (os.path.dirname(main_file), os.path.basename(r_file)[:-2])
        with open(r_file) as fp :
            with open(r_file_new, "w") as fp_new:
                fp_new.write('num_threads <-%s\n' % num_threads)
                fp_new.write('scenario_duration_in_min <- %s\n' % duration)
                fp_new.write('slo_f <-"%s"\n' % slo_violations_file)
                fp_new.write('slo_f_non_aggregated <- "%s"\n' % slo_violations_file_non_agg)
                fp_new.write('as_f<-"%s"\n' % autoscaling_file)
                fp_new.write('m_f<-"%s"\n' % merged_file)
                fp_new.write('f <- "%s"\n' % parsed_file)
                fp_new.write('output_file <- "%s/graphs.png"\n' % os.path.dirname(main_file))
                fp_new.write('slo_agg_1second <- "%s"\n' % slo_agg_1second)
                fp_new.write('slo_agg_5seconds <- "%s"\n' % slo_agg_5seconds)
                fp_new.write('slo_agg_10seconds <- "%s"\n' % slo_agg_10seconds)
                fp_new.write('ec2_file <- "%s"\n' % ec2_file)
                fp_new.write('rds_cpu_file <- "%s"\n' % rds_cpu_file)

                for line in fp:
                    fp_new.write(line)

        escaped_r_filepath = r_file_new.replace(" ", "\\ ")
        print escaped_r_filepath
        subprocess.call(['Rscript', r_file_new])
