app.directive('tooltip', function(){
    return {
        restrict : 'A',
        template : '<span>{{ label }}</span>',
        link : function(scope, el, attrs){
            scope.label = attrs.tooltipLabel;
        }
    }
});

app.directive('validFile',function(){
  return {
    require:'ngModel',
    link:function(scope,el,attrs,ngModel){
      //change event is fired when file is selected
      el.bind('change',function(){
        scope.$apply(function(){
          ngModel.$setViewValue(el.val());
          ngModel.$render();
        })
      })
    }
  }
})