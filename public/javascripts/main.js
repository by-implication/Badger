var app = angular.module('budget', ['ui']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

function App($scope, $http, $location){

	$scope.focus = {
		id: 1,
		parent: null,
		children: [],
		comments: []
	}

	// $scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
	// 	$http.get('/meta', {params: $location.search()})
	// 	.success(function(r){ $scope.focus = r.node; });
	// });

	$scope.nodeLink = function(nodeId){
		return '/app?id=' + nodeId + (!$location.search().comments ? '' : '&comments=true');
	}

	$scope.toggleCommentsLink = function(){
		return '/app?id=' + $scope.focus.id + ($location.search().comments ? '' : '&comments=true');
	}

	$scope.commentState = function(){
		return $location.search().comments ? 'Hide' : 'Show';
	}

	// if(!$location.search().id) $location.search({id: 1});
	
	$scope.focus = {
		"name": "Pepe Bawagan",
		"rating": 3.5,
		"amount": "huge sum",
		"parent": "pepe's dad",
		"children": [
			{
				"name": "Daniel Fordan",
				"amount": 20000000,
				"date": "Jan 12, 2013",
				"rating": 3.5,
				"numrates": 1000
			},
			{
				"name": "Philip Cheang",
				"amount": 20000000,
				"date": "Jan 12, 2013",
				"rating": 3.5,
				"numrates": 1000
			}
		]
	}

	$scope.cats = {
		"academia": [
			"Department of Science and Technology",
			"State Universities and Colleges",
			"DepEd - School Building Program",
			"Department of Education",
			"Department of Education - School Building Program"
		],
		"defence": [
			"AFP  Modernization Program",
			"Civil Service Commission",
			"Department of National Defense"
		],
		"agriculture": [
			"Department of Agrarian Reform",
			"Agriculture and Fisheries Modernization Program",
			"Agrarian Reform Fund",
			"Department of Agriculture"
		],
		"economy": [
			"Commission on Audit",
			"General Fund Adjustments",
			"Economic Stimulus Fund",
			"Department of Finance",
			"Debt Service Fund-Interest Payment",
			"Department of Budget and Management",
			"Compensation Adjustment Fund",
			"National Economic and Development Authority",
			"Tax Expenditures Fund",
			"Budgetary Support to Government Corporations",
			"Priority Social and Economic Projects Fund"
		],
		"public services": [
			"Pension and Gratuity Fund",
			"Department of Social Welfare and Development",
			"Retirement Benefits Fund (Pension and Gratuity Fund)",
			"Department of Public Works and Highways",
			"E-Government Fund",
			"Miscellaneous Personnel Benefits Fund",
			"Payapa at Masaganang Pamayanan Fund",
			"Department of Health",
			"Priority Development Assistance Fund"
		],
		"industry": [
			"Department of Labor and Employment",
			"Department of Trade and Industry",
			"Department of Energy",
			"Department of Environment and Natural Resources"
		],
		"communications": [
			"Department of Transportation and Communications",
			"Presidential Communications Operations Office",
			"Office of the Press Secretary"
		],
		"local government": [
			"Department of the Interior and Local Government",
			"Allocations to Local Government Units",
			"Autonomous Region in Muslim Mindanao"
		],
		"central government": [
			"Congress of the Philippines",
			"Commission on Elections",
			"National Unification Fund",
			"Office of the Vice-President",
			"Joint Legislative-Executive Councils",
			"Office of the President",
			"Contingent Fund",
			"Other Executive Offices",
			"Unprogrammed Fund",
			"Calamity Fund"
		],
		"justice": [
			"Office of the Ombudsman",
			"Department of Justice",
			"The Judiciary",
			"Commission on Human Rights"
		],
		"foreign affairs": [
			"Department of Foreign Affairs",
			"Department of Tourism",
			"International Commitments Fund"
		]
	}
	
}

function Ratings($scope, $http){
	$scope.stars = [1,2,3,4,5];
	$scope.starClass = function(item, star){
	  var rating = item.rating && Math.round(item.rating * 2, 0) / 2;
	  if(rating >= star){
	    return "fa-star";
	  } else if(rating >= star - 0.5){
	    return "fa-star-half-o";
	  } else {
	    return "fa-star-o";
	  }
	}
}