import random
from flask import abort, request, Flask, Response, url_for, render_template, jsonify
from flask.ext.triangle import Triangle
import time
from ReverseProxied import ReverseProxied
from config import APPLICATION_PATH

app = Flask(__name__)
app.config['DEBUG'] = True
app.wsgi_app = ReverseProxied(app.wsgi_app)
Triangle(app)

class FakeService:

    def __init__(self):
        self.MIN = 0
        self.MAX = 1800

    def validate(self, args):
        for k, v in args.iteritems():
            if v is None:
                abort(500, '%s parameter is missing' % k)

    def clamp(self, value):
        return min(self.MAX, max(self.MIN, value))

def get_response(req, USAGE_TEXT, func, validate, **kwargs):
    service = FakeService()

    if len(req.args) == 0:
        return USAGE_TEXT

    service.validate(kwargs)

    if validate is not None:
        validate(kwargs)

    dist = service.clamp(func(kwargs))

    if req.args.get('test') == 'false':
        time.sleep(dist/1.0)

    return jsonify(delay="%.3f" % dist)

@app.route("/")
def response_generator():
    return render_template('index.html', app_path=APPLICATION_PATH)

@app.route("/expo")
def expo():
    EXPO_USAGE = 'Usage: ' + url_for('.expo') + '?lambda=&lt;integer&gt;&amp;[k=&lt;ingeter&gt;test=true|false]'

    func = lambda args: random.expovariate(lambd=float(args['lambd'])) + float(args['k'])

    lambd = request.args.get('lambda')
    k = request.args.get('k') if request.args.get('k') is not None else 0

    return get_response(request, EXPO_USAGE, func, None, lambd=lambd, k=k)

@app.route("/gamma")
def gamma():
    GAMMA_USAGE = 'Usage: ' + url_for('.gamma') + '?alpha=&lt;integer&gt;&beta=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'
    func = lambda args: random.gammavariate(alpha=float(args['alpha']), beta=float(args['beta'])) + float(request.args.get('k'))
    validate = lambda args: abort(500, 'alpha and beta must be greater than zero') if float(args['alpha']) <= 0 or float(args['beta']) <= 0 else True

    alpha = request.args.get('alpha')
    beta = request.args.get('beta')

    return get_response(request, GAMMA_USAGE, func,  validate, alpha=alpha, beta=beta)

@app.route("/gauss")
def gauss():
    GAUSS_USAGE = 'Usage:' + url_for('.gauss') + '?mu=&lt;integer&gt;&amp;sigma=&lt;integer&gt;[test=true|false]'

    func = lambda args: random.gauss(mu=float(args['mu']), sigma=float(args['sigma'])) + float(request.args.get('k'))

    mu = request.args.get('mu')
    sigma = request.args.get('sigma')

    return get_response(request, GAUSS_USAGE, func, None, mu=mu, sigma=sigma)

@app.route("/log")
def log():
    LOG_USAGE = 'Usage:' + url_for('log') + '?mu=&lt;integer&gt;&amp;sigma=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'

    func = lambda args: random.lognormvariate(mu=float(args['mu']), sigma=float(args['sigma'])) + float(request.args.get('k'))
    validate = lambda args: abort(500, 'sigma must be greater than zero') if float(args['sigma']) <= 0 else True

    mu = request.args.get('mu')
    sigma = request.args.get('sigma')

    return get_response(request, LOG_USAGE, func, validate, mu=mu, sigma=sigma)

@app.route("/vonmises")
def vonmises():
    VONMISES_USAGE = 'Usage: ' + url_for('.vonmises') + '?mu=&lt;integer&gt;&amp;kappa=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'

    func = lambda args: random.vonmisesvariate(mu=float(args['mu']), kappa=float(args['kappa'])) + float(request.args.get('k'))
    validate = lambda args: abort(500, 'kappa must be greater or equal to zero') if float(args['kappa']) < 0 else True

    mu = request.args.get('mu')
    kappa = request.args.get('kappa')

    return get_response(request, VONMISES_USAGE, func, validate, mu=mu, kappa=kappa)

@app.route("/pareto")
def pareto():
    PARETO_USAGE = 'Usage: ' + url_for('.pareto') + '?alpha=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'

    func = lambda args: random.paretovariate(alpha=float(args['alpha'])) + float(request.args.get('k'))

    alpha = request.args.get('alpha')

    return get_response(request, PARETO_USAGE, func, None, alpha=alpha)

@app.route("/weibull")
def weibull():
    WEIBULL_USAGE = '''Usage: /weibull?alpha=&lt;integer&gt;&beta=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'''

    func = lambda args: random.weibullvariate(alpha=float(args['alpha']), beta=float(args['beta'])) + float(request.args.get('k'))

    alpha = request.args.get('alpha')
    beta = request.args.get('beta')

    return get_response(request, WEIBULL_USAGE, func, None, alpha=alpha, beta=beta)

@app.route("/uniform")
def uniform():
    UNIFORM_USAGE = '''Usage: /uniform?a=&lt;integer&gt;&b=&lt;integer&gt;[k=&lt;integer&gt;&amp;test=true|false]'''

    func = lambda args: random.uniform(a=float(args['a']), b=float(args['b'])) + float(request.args.get('k'))

    a = request.args.get('a')
    b = request.args.get('b')

    return get_response(request, UNIFORM_USAGE, func, None, a=a, b=b)

@app.route("/constant")
def constant():
    CONSTANT_USAGE = '''Usage: /constant?c=&lt;integer&gt;'''

    func = lambda args: float(args['c'])

    c = request.args.get('c')

    return get_response(request, CONSTANT_USAGE, func, None, c=c)

if __name__ == "__main__":
    app.run(debug=True, port=5000)
