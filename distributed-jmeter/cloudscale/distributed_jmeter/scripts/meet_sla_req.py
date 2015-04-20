import sys
from cloudscale.distributed_jmeter.scripts.visualization.SLO import SLO
from cloudscale.distributed_jmeter.scripts.visualization.converters import Converters

max_time = SLO

def check(file_path):
    output = ""
    urls = {}
    unsuccessfull = 0
    all_requests = 0
    fp = open(file_path)
    for line in fp:
        converters = Converters()
        try:

            timestamp, estimated_time, url, response_code, _, _, _  = line.split(",")
            url = converters.url_converter(url)

            if max_time.has_key(url):
                all_requests+=1
                if not urls.has_key(url):
                    urls[url] = {}
                    urls[url]['times'] = []

                urls[url]['times'].append([estimated_time, response_code])

                if response_code != "200":
                    unsuccessfull += 1

        except Exception as e:
            output += "Exception occured\n"
            output += e.message + "\n"
            pass

    for k in urls:
        count_succ = 0
        all = len(urls[k]['times'])

        for time, response_code in urls[k]['times']:
            if int(time) <= max_time[k] and response_code == "200":
                count_succ += 1

        dist = (all*100.0)/all_requests
        if count_succ >= (all * 90) / 100:
            output += "%-50s %-50s prob = %.2f%%\n" % (k, "OK [no. requests = %s]" % all, dist)
        else:
            p = (count_succ*100)/all
            output += "%-50s %-50s prob = %.2f%%\n" % (k, "NOT OK [all = %s, succ = %s (%s%%) ]" % (all, count_succ, p), dist)

    fp.close()
    output += "--------------------------------------------------\n"
    output += "ALL = %s, UNSUCCESSFULL = %s\n" % (all_requests, unsuccessfull)

    return output

if __name__ == "__main__":
    print check(sys.argv[1])

