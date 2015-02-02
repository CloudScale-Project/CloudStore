### Description

[Response generator](https://arcane-meadow-6418.herokuapp.com/) is a web application that simulates response times according to chosen distribution.

Supported distributions are:
- [uniform](http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29)
- constant
- [gauss](http://en.wikipedia.org/wiki/Normal_distribution)
- [gamma](http://en.wikipedia.org/wiki/Gamma_distribution)
- [exponentional](http://en.wikipedia.org/wiki/Exponential_distribution)
- [logarithmic](http://en.wikipedia.org/wiki/Logarithmic_distribution)
- [pareto](http://en.wikipedia.org/wiki/Pareto_distribution)
- [weibull](http://en.wikipedia.org/wiki/Weibull_distribution)

Each distribution have specific parameters by which you can manipulate the end response time in seconds.
To each distribution we added a **k** parameter which shifts the end response time for **k**.

### Usage

Response generator is designed to be used inside real application as a API call. 
Each distribution has it's own URL and accepts it's own parameters, parameter **k** and **test=[true|false]** parameter:

* ```/uniform?a=1&b=2&k=3&test=true```
* ```/constant?c=1&test=true```
* ```/expo?lambda=1&k=0&test=true```
* ```/log?mu=1&sigma=2&k=0&test=true```
* ```/gamma?alpha=1&beta=2&k=0&test=true```
* ```/gauss?mu=10&sigma=2&k=0&test=true```
* ```/log?mu=1&sigma=2&k=0&test=true```
* ```/pareto?alpha=1&k=0&test=true```
* ```/weibull?alpha=1&beta=2&k=0&test=true```

Use ```test=true``` if you want to just print the value.

Use ```test=false``` if you want to actually make a delay.

### Examples

* Gauss distribution has **mu** and **sigma** parameters and we want to just get the value:

  ```
  /gauss?mu=1&sigma=2&k=0&test=true
  ```
  
* Uniform distribution has **a** and **b** parameters and we want to make a delay:
  
  ```
  /uniform?a=1&b=2&k=0&test=false
  ```
  
* Exponentional distribution has **lambda** parameter and we want to shift the end response time for 10 units and make a delay:

  ```
  /expo?lambda=1&k=10&test=false
  ```

### Screenshots

![Screenshot](https://raw.githubusercontent.com/CloudScale-Project/Showcase/master/response-generator/static/images/screenshot.png)
