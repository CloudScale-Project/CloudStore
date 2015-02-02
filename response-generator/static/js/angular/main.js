var responseGeneratorApp = angular.module('responseGeneratorApp', []);

responseGeneratorApp.directive('jsxgraph', ['getDistributionService', 'buildUrlService', 'getBoundingBoxService', '$timeout',
    function (get_distribution, buildUrl, get_boundingbox, $timeout) {
        return {
            template: '<div id="box" class="jxgbox" style="width:300px; height:300px;"></div>',
            restrict: 'E',
            link: function (scope, element, attrs) {

                var create_board = function (bbox) {
                    var board = JXG.JSXGraph.initBoard('box', {boundingbox: bbox, axis: true});
                    var xAxPt0 = board.create('point', [0, 0], {needsRegularUpdate: false, visible: false});
                    var xAxPt1 = board.create('point', [1, 0], {needsRegularUpdate: false, visible: false});
                    var xaxis = board.create('axis',
                        [xAxPt0, xAxPt1], {
                            name: 's',
                            withLabel: true,
                            label: {position: 'bot',  // possible values are 'lft', 'rt', 'top', 'bot'
                                offset: [130, 10]   // (in pixels)
                            }
                        }
                    );
                    return board
                }
                var clear_board = function (board, bbox) {
                    JXG.JSXGraph.freeBoard(board)
                    return create_board(bbox)
                }
                var board = create_board([-5, 1, -5, 1])
                var timeoutPromise;
                var delayInMs = 300;
                scope.$watchGroup(['curr', 'attr1', 'attr2', 'attr3', 'test'], function () {
                    $timeout.cancel(timeoutPromise);
                    timeoutPromise = $timeout(function () {
                        var curr = scope.curr;
                        var attr1 = scope.attr1 == "" ? 1 : parseFloat(scope.attr1);
                        var attr2 = scope.attr2 == "" ? 1 : parseFloat(scope.attr2);
                        var attr3 = scope.attr3 == "" ? 0 : parseFloat(scope.attr3);

                        board = clear_board(board, get_boundingbox(curr, attr1, attr2, attr3))
                        board.create('functiongraph', [get_distribution(curr, attr1, attr2, attr3)])
                        scope.$apply(function () {
                            scope.url = buildUrl(curr, attr1, attr2, attr3)
                        });
                        document.getElementById('box_licenseText').remove()
                    }, delayInMs)

                })
            }
        }
    }]);

responseGeneratorApp.directive('selectAll', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.on('click', function () {
                this.select();
            })
        }
    }
})

responseGeneratorApp.service('getBoundingBoxService', function () {
    return function (dist, attr1, attr2, attr3) {
        if (dist == 'gauss')
            return [attr1 + attr3 - 5, attr2, attr1 + attr3 + 5, -attr2]
        if (dist == 'expo')
            return [attr3 - 2, attr1 + 1, attr3 + 2, -attr1 - 1]
        if (dist == 'gamma')
            return [attr1 + attr3 - 5, attr2, attr1 + attr3 + 5, -attr2]
        if ( dist == 'constant')
            return [-5, attr1+(attr1/2), 5, -(attr1/2)]
        return [attr1 + attr3 - 5, attr2, attr1 + attr3 + 5, -attr2]
    }
})

responseGeneratorApp.service('buildUrlService', function () {
    return function (dist, attr1, attr2, attr3) {
        var url = "";
        if (dist == 'gauss') {
            url = '?mu=' + attr1 + '&sigma=' + attr2 + '&k=0';
        }
        if (dist == 'expo') {
            url = '?lambda=' + attr1 + '&k=' + attr3;
        }
        if (dist == 'gamma') {
            url = '?alpha=' + attr1 + '&beta=' + attr2 + '&k=' + attr3;
        }
        if (dist == 'log') {
            url = '?mu=' + attr1 + '&sigma=' + attr2 + '&k=' + attr3;
        }
        if (dist == 'pareto') {
            url = '?alpha=' + attr1 + '&k=' + attr3
        }
        if (dist == 'weibull') {
            url = '?alpha=' + attr1 + '&beta=' + attr2 + '&k=' + attr3
        }
        if (dist == 'uniform'){
            url = '?a=' + attr1 + '&b=' + attr2 + '&k=' + attr3;
        }
        if (dist == 'constant')
        {
            url = '?c=' + attr1
        }
        return dist + url
    }
});

responseGeneratorApp.service('getDistributionService', function () {
    return function (dist, attr1, attr2, attr3) {
        if (dist == 'gauss') {
            return function (x) {
                var sigma = attr2
                var mu = attr1
                a = 1 / (sigma * Math.sqrt(2 * Math.PI))
                b = attr1
                c = attr2
                return a * Math.exp(-(Math.pow(x - b, 2) / (2 * c * c))) + parseFloat(attr3)
            }
        }
        if (dist == 'expo') {
            return function (x) {
                return jStat.exponential.pdf(x + parseFloat(attr3 * -1), attr1)
            }
        }
        if (dist == 'gamma') {
            return function (x) {
                return jStat.gamma.pdf(x + parseFloat(attr3 * -1), parseFloat(attr1), parseFloat(attr2))
            }
        }

        if (dist == 'log') {
            return function (x) {
                return jStat.lognormal.pdf(x + parseFloat(attr3 * -1), attr1, attr2)
            }
        }

        if (dist == 'pareto') {
            return function (x) {
                return jStat.pareto.pdf(x + parseFloat(attr3 * -1), attr1, attr2)
            }
        }

        if (dist == 'weibull') {
            return function (x) {
                return jStat.weibull.pdf(x + parseFloat(attr3 * -1), attr1, attr2)
            }
        }

        if (dist == 'uniform')
        {
            return function(x)
            {
                return jStat.uniform.pdf(x + parseFloat(attr3 * -1), attr1, attr2)
            }
        }

        if (dist == 'constant')
        {
            return function(x)
            {
                return attr1;
            }
        }
        return undefined
    }
});

responseGeneratorApp.controller('DistributionController', ['$scope', 'getDistributionService',
    function ($scope, get_distribution) {
        $scope.curr = 'gauss';
        $scope.dists = ['uniform', 'constant', 'expo', 'gamma', 'gauss', 'log', 'pareto', 'weibull'];
        $scope.url = 'gauss'
        $scope.attr1 = 1
        $scope.attr2 = 2
        $scope.attr3 = 0

    }
]);
