function Main($scope, $http){
	
	$scope.status = 'working';
	$scope.focus = {
		'name': 'Pepe Bawagan',
		'date': 'Jan 12, 2013',
		'rating': 3.5,
		'amount': 'huge sum'
	}
	$scope.children = [
		{
			'name': 'Daniel Fordan',
			'amount': 20000000,
			'rating': 3.5,
			'numrates': 1000
		},
		{
			'name': 'Philip Cheang',
			'amount': 20000000,
			'rating': 3.5,
			'numrates': 1000
		}
	]
}

function Ratings($scope, $http){
	$scope.stars = [1,2,3,4,5];
	$scope.starClass = function(item, star){
	  var rating = item.rating && Math.round(item.rating * 2, 0) / 2;
	  if(rating >= star){
	    return 'fa-star';
	  } else if(rating >= star - 0.5){
	    return 'fa-star-half-o';
	  } else {
	    return 'fa-star-o';
	  }
	}
}