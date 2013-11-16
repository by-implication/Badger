var app = angular.module('budget', ['ui']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

function App($scope, $http, $location){

	$scope.$on('$locationChangeSuccess', function(e, newPath, oldPath){
		if(newPath == oldPath) return;
		$http.get('/meta?' + newPath.split('?')[1])
		.success(function(r){ $scope.state = r.node; });
	})

	$scope.nodeLink = function(nodeId){
		return '/meta?id=' + nodeId;
	}

	$scope.commentState = function(){
		return $scope.state.comments.length ? 'on' : 'off';
	}
	
	$scope.state = {
		id: 2,
		parent: 1,
		comments: []
	};

}