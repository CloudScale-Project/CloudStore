import time as t
import sys

max_time = {}
max_time['/'] = 3000
max_time['/?SHOPPING_ID'] = 3000
max_time['/best-sellers'] = 5000
max_time['/new-products'] = 5000
max_time['/product-detail'] = 3000
max_time['/search?searchField=&keyword=&C_ID='] = 10000
max_time['/search?C_ID'] = 3000
max_time['/search'] = 3000
max_time['/shopping-cart?ADD_FLAG=N'] =  3000
max_time['/shopping-cart?I_ID=&QTY=1&ADD_FLAG=Y'] = 3000
max_time['/customer-registration?SHOPPING_ID='] = 3000
max_time['/buy-confirm'] = 5000
max_time['/buy?RETURNING_FLAG=Y'] = 3000
max_time['/buy?RETURNING_FLAG=N'] = 3000
max_time['/order-inquiry'] = 3000

def check(file_path):
    output = ""
    urls = {}
    unsuccessfull = 0
    all_requests = 0
    fp = open(file_path)
    for line in fp:
        all_requests+=1
        try:
            timestamp, estimated_time, url, response_code, _, _, _  = line.split(",")
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

        if count_succ >= (all * 90) / 100:
            output += "%-50s VREDU\n" % k
        else:
            p = (count_succ*100)/all 
            output += "%-50s NI VREDU [all = %s, succ = %s (%s%%) ]\n" % (k, all, count_succ, p)
    fp.close()
    output += "--------------------------------------------------\n"
    output += "ALL = %s, UNSUCCESSFULL = %s\n" % (all_requests, unsuccessfull)

    return output

if __name__ == "__main__":
    print check(sys.argv[1])

