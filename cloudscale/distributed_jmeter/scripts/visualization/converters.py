import datetime


class Converters:

    def __init__(self):
        self.launch_count = 1

    def response_code_converter(self, c):
        try:
            return int(c)
        except Exception as e:
            return 500

    def timestamp_converter(self, t):
        try:
            return datetime.datetime.fromtimestamp(float(t)/1000.0)
        except Exception as e:
            raise e

    def datetime_to_timestamp(self, t):
        d = datetime.datetime.strptime(t, "%Y-%m-%d %H:%M:%S")
        return d

    def _totimestamp(self, dt, epoch=datetime.datetime(1970,1,1)):
        td = dt - epoch
        return ((td.microseconds + (td.seconds + td.days * 24 * 3600) * 10**6) / 1e6) * 1000

    def action_to_number(self, a):
        if a == 'launch':
            self.launch_count += 1
            return self.launch_count-1
        elif a == 'terminate':
            self.launch_count -= 1
            return self.launch_count+1
        else:
            return -10

    def url_converter(self, url):
        if url == '/search?C_ID':
            return '/search'
        if url == '/?SHOPPING_ID':
            return '/'
        if url == '/shopping-cart?ADD_FLAG=N':
            return '/shopping-cart'
        if url == '/shopping-cart?I_ID=&QTY=1&ADD_FLAG=Y':
            return '/shopping-cart'
        if url == '/customer-registration?SHOPPING_ID=':
            return '/customer-registration'
        if url == '/buy?RETURNING_FLAG=Y':
            return '/buy'
        if url == '/buy?RETURNING_FLAG=N':
            return '/buy'
        if url == '[BeanShell] probability':
            return None
        return url