import datetime
import os
import pandas as pd
import csv
import sys
from cloudscale.distributed_jmeter.scripts.visualization.SLO import SLO
from cloudscale.distributed_jmeter.scripts.visualization.converters import Converters


class Parse:
    
    def __init__(self):
        self.max_time = SLO

    def does_violate_slo(self, url, estimated_time, response_code):
        try:
            return not (int(estimated_time) < self.max_time[url] and response_code == 200)
        except KeyError as e:
            print "There's no SLO for %s" % url


    def get_instances_lifetime(self, as_file):
        instance_ids = []
        instances = []

        with open(as_file) as fp:
            next(fp) # skip the header
            for line in fp:
                line = line[:-1]
                instance_id, start_time, end_time, action = line.split(',')

                if action == 'launch' and instance_id not in instance_ids:
                    instance = {}
                    instance['id'] = instance_id
                    instance['start_time'] = datetime.datetime.strptime(end_time, "%Y-%m-%d %H:%M:%S")
                    instances.append(instance)
                    instance_ids.append(instance_id)

                if action == 'terminate' and instance_id in instance_ids:
                    i=0
                    for instance in instances:
                        if instance['id'] == instance_id and not instance.has_key('end_time'):
                            instances[i]['end_time'] = datetime.datetime.strptime(start_time, "%Y-%m-%d %H:%M:%S")
                            break
                        i+=1
        for instance in instances:
            min_date = self.data['date'].min().to_datetime()
            if instance['start_time'] < min_date:
                instance['start_time'] = min_date

            if not instance.has_key('end_time'):
                instance['end_time'] = self.data['date'].max().to_datetime()
        return instances

    def delete_records_that_violates_slo(self, output_file, input_file):
        converters = Converters()
        with open(output_file, 'w') as slo_fp:
            slo_fp.write('"date","response_time","url","response_code","status","autoscalable","instance_id"\n')
            with open(input_file) as fp:
                next(fp) # skip header
                for line in fp:
                    timestamp, estimated_time, url, response_code, status, attr1, attr2  = line.split(",")
                    response_code = converters.response_code_converter(response_code)
                    if self.does_violate_slo(url, estimated_time, response_code):
                        slo_fp.write('%s,%s,%s,%s,%s,%s,%s' % (timestamp, estimated_time, url, response_code, status, attr1, attr2))


    def slo_agg_1minute(self, output_file, file):
        converters = Converters()
        with open(output_file, 'w') as slo_fp:
            with open(file) as fp:
                next(fp) # skip header
                timestamps = {}
                for line in fp:
                    timestamp, estimated_time, url, response_code, status, attr1, attr2  = line.split(",")
                    #df = datetime.datetime.strptime(datetime.datetime.fromtimestamp(float(timestamp)/1000).strftime("%Y-%m-%d %H:%M"), "%Y-%m-%d %H:%M") # remove microseconds from time
                    response_code = converters.response_code_converter(response_code)
                    new_timestamp = int(float(timestamp)/60000)*60000
                    if not timestamps.has_key(new_timestamp):
                        timestamps[new_timestamp] = {'num' : 0, 'num_all_requests': 0}

                    timestamps[new_timestamp]['num_all_requests'] += 1

                    i=0
                    if self.does_violate_slo(url, estimated_time, response_code):
                        i = 1

                    timestamps[new_timestamp]['num'] += i


            slo_fp.write('"date","num","num_all_requests"\n')
            for timestamp in timestamps.keys():
                slo_fp.write('%s,%s,%s\n' % (timestamp, timestamps[timestamp]['num'],timestamps[timestamp]['num_all_requests']))

    def slo_agg_seconds(self, parsed_file, output_file, seconds):
        print "Seconds: %s" % seconds
        converters = Converters()
        with open(output_file, 'w') as slo_fp:
            with open(parsed_file) as fp:
                next(fp) # skip header
                parsed_file_data = csv.reader(fp)
                sorted_data = sorted(parsed_file_data, key = lambda row: int(row[0]))

                timestamps = {}
                ref_timestamp, _, _, _, _, _, _  = sorted_data[0]
                ref_timestamp = (int(ref_timestamp)/1000)
                timestamps[ref_timestamp] = {'num' : 0, 'num_all_requests': 0}

                min_date = sys.maxint
                for line in sorted_data:
                    timestamp, estimated_time, url, response_code, status, attr1, attr2 = line
                    timestamp = (int(timestamp)/1000)
                    if timestamp < min_date:
                        min_date = timestamp
                    response_code = converters.response_code_converter(response_code)

                    time_delta = datetime.datetime.fromtimestamp(timestamp) - datetime.datetime.fromtimestamp(ref_timestamp)
                    # print "time_delta: %s" % time_delta.seconds
                    # print "time_delta: %s" % datetime.datetime.fromtimestamp(timestamp)
                    if time_delta.seconds >= seconds:
                        # print "new ref timestamp: %s" % datetime.datetime.fromtimestamp(timestamp)
                        ref_timestamp = timestamp
                        if not timestamps.has_key(ref_timestamp):
                            timestamps[ref_timestamp] = {'num' : 0, 'num_all_requests': 0}

                    timestamps[ref_timestamp]['num_all_requests'] += 1

                    i=0
                    if self.does_violate_slo(url, estimated_time, response_code):
                        i = 1

                    timestamps[ref_timestamp]['num'] += i

                print min_date
                slo_fp.write('"date","num","num_all_requests"\n')
                slo_fp.write('%s,%s,%s\n' % (0, 0, 0))
                for timestamp in timestamps.keys():
                    timestamp_subtract = (timestamp - min_date)+seconds
                    slo_fp.write('%s,%s,%s\n' % (timestamp_subtract*1000, timestamps[timestamp]['num'],timestamps[timestamp]['num_all_requests']))

    def _find_min_date(self, data):
        min_date = sys.maxint
        for row in data:
            if int(row[0]) < min_date:
                min_date = min_date

        return min_date

    def merge(self, output_file, as_file, file):

        response_time_stack = []
        epoch = datetime.datetime(1970,1,1)
        with open(output_file, 'w') as m_fp:
            m_fp.write('"date","response_time","url","response_code","status","attr1","attr2","instance_id","y"\n')
            if os.path.exists(as_file):
                instances = self.get_instances_lifetime(as_file)
                i = -5
                for instance in instances:
                    instance_id = instance['id']
                    with open(file) as fp:
                        next(fp) # skip the header
                        already_got_max_response_time = False
                        for line in fp:
                            # if already_got_max_response_time is False: # do this only the first time in loop
                            #     start_time = instance['start_time']
                            #     end_time = instance['end_time'] if instance.has_key('end_time') and instance['end_time'] < self.data['date'].max().to_datetime() else self.get_end_time(instance_id, instances)
                            #     max_response_time_to_as_dt = self.data_indexed.between_time(start_time, end_time).max()['response_time']
                            #     already_got_max_response_time = True

                            timestamp, estimated_time, url, response_code, status, attr1, attr2  = line.split(",")
                            dt = datetime.datetime.fromtimestamp(int(timestamp)/1000.0)
                            if dt >= instance['start_time'] and dt <= instance['end_time']:
                                m_fp.write('%s,%s,%s,%s,%s,%s,%s,%s,%s\n' % (timestamp, estimated_time, url, response_code, status, attr1, attr2[:-1], instance_id, i))
                    i-=5

    def timestamp_to_datetime_file(self):
        with open('files/response-times-over-time.trans.tab', 'w') as fp_out:
            fp_out.write('timestamp\tdate\tresponse_time\turl\tresponse_code\tstatus\n')
            fp_out.write('c\td\tc\ts\td\td\n')
            with open(self.file) as fp:
                next(fp) # skip the header
                for line in fp:
                    ts, curr_dt, response_time, url, response_code, status = self.parse_line(line)
                    fp_out.write('%s\t%s\t%s\t%s\t%s\t%s\n' % (ts, curr_dt.strftime('%H:%M'), response_time, url, response_code, status))

    def get_end_time(self, instance_id, instances):
        i = 0
        for instance in instances:
            if instance['id'] == instance_id:
                break
            i+=1
        try:
            end_time =  instances[i+1]['start_time']
            return end_time
        except IndexError as e:
            return self.data['date'].max().to_datetime()

    def parse_line(self, line):
        ts, response_time, url, response_code, status, _, _ = line.split(",")
        dt = datetime.datetime.fromtimestamp(int(ts)/1000.0)
        rc = self.parse_response_code(response_code)

        return ts, dt, int(response_time), str(url), rc, str(status)

    def parse_response_code(self, rc):
        try:
            return int(rc)
        except:
            return 500

    def parse(self, output_file, file):
        with open(file) as fp:
            next(fp) # skip the header
            with open(output_file, "w") as parsed_fp:
                parsed_fp.write("date,response_time,url,response_code,status,attr1,attr2\n")
                converters = Converters()
                for line in fp:
                    timestamp, estimated_time, url, response_code, status, attr1, attr2  = line.split(",")
                    response_code = converters.response_code_converter(response_code)
                    url = converters.url_converter(url)
                    if url is not None:
                        parsed_fp.write('%s,%s,%s,%s,%s,%s,%s' % (timestamp, estimated_time, url, response_code, status, attr1, attr2))

    def to_dataframe(self, file, as_file):


        print "Parsing " + file
        converters = Converters()
        self.data_indexed = pd.read_csv(file, index_col='date', converters={
            'date' : converters.timestamp_converter,
            'response_code' : converters.response_code_converter,
            'url' : converters.url_converter
        })

        self.data = pd.read_csv(file, converters={
            'date' : converters.timestamp_converter,
            'response_code' : converters.response_code_converter,
            'url' : converters.url_converter
        })


        # print "Parsing " + as_file
        # self.autoscalability_data = pd.read_csv(as_file, converters={
        #     'start_time' : converters.datetime_to_timestamp,
        #     'end_time' : converters.datetime_to_timestamp,
        #     'action' : converters.action_to_number
        # })
        return

    def merge_autoscaling(self, file1, file2):
        pass





