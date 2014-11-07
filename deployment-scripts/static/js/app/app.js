var app = angular.module('formApp', ['ngCookies', 'ui.bootstrap', 'ngAnimate', 'ngRoute', 'ngCookies'])

app.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {

    $httpProvider.defaults.xsrfCookieName = 'csrftoken';
    $httpProvider.defaults.xsrfHeaderName = 'X-CSRFToken';
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

    $routeProvider
        .when('/step1', {
            'templateUrl': 'form-step1.html',
            'controller': 'step1Controller'
        })
        .when('/step2', {
            'templateUrl': 'form-step2.html',
            'controller': 'step2Controller'
        })
        .when('/step3', {
            'templateUrl': 'form-step3.html',
            'controller': 'step3Controller'
        })
        .when('/step4', {
            'templateUrl': 'form-step4.html',
            'controller': 'step4Controller'
        })
        .when('/step5', {
            'templateUrl': 'form-step5.html',
            'controller': 'step5Controller'
        })
        .when('/step6', {
            'templateUrl': 'form-step6.html',
            'controller': 'step6Controller'
        })
        .when('/step7', {
            'templateUrl': 'form-step7.html',
            'controller': 'step7Controller'
        })
        .otherwise({
            redirectTo: '/step1'
        });
}]);


app.service("formDataService", ['$location', '$rootScope', '$q', function ($location, $rootScope, $q) {
    service = {
        'formData': {'db': {}, 'fr': {}}
    }

    return service
}]);

app.controller('myController', ['$scope', '$cookieStore', 'formDataService', function($scope, formDataService){
    $scope.formData = formDataService.formData;
}])

app.controller('step1Controller', ['$scope', '$location', 'formDataService', function ($scope, $location, formDataService) {
    $scope.formData = formDataService.formData;

    $scope.provider = function (provider) {
        formDataService.formData.provider = provider;
//        $cookieStore.put('provider', provider);
        $location.path('/step2');
    };

}]);

app.controller('step2Controller', ['$scope', '$location', '$http', 'formDataService', function ($scope, $location, $http, formDataService) {
    if (formDataService.formData.provider == undefined) {
        $location.path('/')
    }

    $scope.formData = formDataService.formData;

    $scope.db_provider = function (provider) {
        formDataService.formData.db_provider = provider;
        $location.path('/step3')
    }
    $scope.prevStep = function (step) {
        $location.path(step);
    }
}]);


app.controller('step3Controller', ['$scope', '$location', '$http', '$timeout', 'formDataService', function ($scope, $location, $http, $timeout,  formDataService) {

    if (formDataService.formData.provider == undefined || formDataService.formData.db_provider == undefined) {
        $location.path('/')
    }
    $scope.formData = formDataService.formData;

    $scope.nextStep = function (step, formController) {
        if (formController.$valid) {
            $location.path(step)
        }
        else {
            $scope.submitted = true;
        }
    }

    $scope.prevStep = function (step) {
        $location.path(step);
    }

    if (formDataService.formData.provider == 'aws')
    {
        $http.get('/form/ec2-instance-types').success(function (data) {
            $scope.ec2_instance_types = data;
        });
    }

    if ( formDataService.formData.provider == 'openstack')
    {
        formDataService.formData.fr.flavor = ''
        var typing;
        $scope.$watch('formData.fr.own_infrastructure', function(newValue, oldValue){
            if (newValue == 'no')
            {
                typing = $http.post('/form/get-openstack-data', formDataService.formData)
                            .success(function(response){
                                $scope.flavors = response.flavors
                                $scope.images = response.images
                            })
                            .error(function(response){
                                console.log(response)
                        })
            }
        }, true)
    }


}]);

app.controller('step4Controller', ['$scope', '$location', '$http', 'formDataService', function ($scope, $location, $http, formDataService) {
    if (formDataService.formData.provider == undefined || formDataService.formData.db_provider == undefined) {
        $location.path('/')
    }

    $http.post('/form/get-openstack-data', formDataService.formData)
        .success(function (response) {
            $scope.flavors = response.flavors;
            $scope.images = response.images;
        })
        .error(function (response) {
            console.log(response);
        });

    $scope.formData = formDataService.formData;

    $scope.$watch('formData', function(newValue, oldValue){
        $scope.formData.db['num_replicas'] = $scope.formData.db.num_instances -1;
    }, true)
    $scope.nextStep = function (step, formController) {
        if (formController.$valid) {
            $location.path(step)
        }
        else {
            $scope.submitted = true;
        }
    }

    $scope.prevStep = function (step) {
        $location.path(step);
    }

    $http.get('/form/rds-instance-types').success(function (data) {
        $scope.rds_instance_types = data;
    });

}]);

app.controller('step5Controller', ['$scope', '$location', '$cookies', '$q', 'formDataService', function ($scope, $location, $cookies, $q, formDataService) {
    if (formDataService.formData.provider == undefined || formDataService.formData.db_provider == undefined) {
        $location.path('/')
    }
    formDataService.myDropZone = new Dropzone("div.dropzone", {
        url: '/form/finish',
        headers : {'X-CSRFToken' : $cookies.csrftoken},
        acceptedFiles : '.jmx',
        addRemoveLinks: true,
        autoProcessQueue: false
    })
    .on('sending', function(file, xhr, formData){
        formData.append('formData', JSON.stringify(formDataService.formData));
    })


    .on('removedfile', function(file){
        $scope.uploaded = false;
        $scope.$apply()
    })
    $scope.nextStep = function (step, formController) {
        if (formController.$valid) {

            $location.path(step)
        }
        else {
            $scope.submitted = true;
        }
    }

    $scope.prevStep = function (step) {
        $location.path(step);
    }
}])

app.controller('step6Controller', ['$scope', 'formDataService', function ($scope, formDataService) {
    $scope.formData = formDataService.formData;
}])

app.controller('step7Controller', ['$scope', '$http', '$sce', '$cookies', '$location', 'formDataService', function ($scope, $http, $sce, $cookies, $location, formDataService) {
    if (formDataService.formData.provider == undefined || formDataService.formData.db_provider == undefined) {
        $location.path('/')
    }
    else
    {
        $http.post('/form/finish', formDataService.formData).success(function(response){
            window.location = '/results/' + response.task_id
        })
        .error(function(response){
            $scope.errors = response.errors
        })

        $scope.trustAsHtml = function(html)
        {
            return $sce.trustAsHtml(html)
        }
    }

}]);