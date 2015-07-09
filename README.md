## CloudStore

CloudStore is an e-commerce web application developed by using [TPC-W](http://www.tpc.org/tpcw/) standard and it is used as a Showcase application to validate the CloudScale tools that developed during the project. The application was developed using the Java Springframework, and its source code can be found at the [CloudStore](https://github.com/CloudScale-Project/CloudStore) GitHub directory. 

We also created [deployment scripts](https://github.com/CloudScale-Project/Deployment-Scripts) to deploy the showcase on the Amazon Web Services public cloud and on any OpenStack private cloud. In order to load-test the application we developed [distributed JMeter](https://github.com/CloudScale-Project/Distributed-Jmeter) tool, which helps deploying distributed JMeter instances on AWS or OpenStack.

In this repository you will also find the [response generator](https://github.com/CloudScale-Project/Response-Generator) application which we use to simulate the payment gateway of showcase application.
