var app = angular.module('budget', ['ui']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

function App($scope, $http, $location){

	$scope.node = {
		id: 1,
		parent: null,
		children: [],
		comments: []
	}

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		$http.get('/meta', {params: $location.search()})
		.success(function(r){ $scope.node = r.node; });
	});

	$scope.nodeLink = function(nodeId){
		return '/app?id=' + nodeId + (!$location.search().comments ? '' : '&comments=true');
	}

	$scope.toggleCommentsLink = function(){
		return '/app?id=' + $scope.node.id + ($location.search().comments ? '' : '&comments=true');
	}

	$scope.commentState = function(){
		return $location.search().comments ? 'Hide' : 'Show';
	}

	if(!$location.search().id) $location.search({id: 1});

}