var app = angular.module('budget', ['ui'])
	.directive('starRating', function(){
		return {
			restrict: 'E',
			scope: {
				item: '=item'
			},
			templateUrl: '/assets/templates/rateStub.html',
			transclude: true,
			controller: function($scope, $http){
				$scope.stars = [1,2,3,4,5];

				$scope.roundFix = function(n){
				  var decimal_places = 2;
				  var pow = Math.pow(10, decimal_places);
				  return (Math.round(n*pow)/pow).toFixed(decimal_places);
				}
				$scope.round = function(num, deg){
				  var coeff = Math.pow(10, deg);
				  return Math.round(num * coeff)/coeff;
				}

				$scope.starClass = function(item, star){
				  var rating = item.rating && $scope.round(item.rating * 2, 0) / 2;
				  if(rating >= star){
				    return "fa-star";
				  } else if(rating >= star - 0.5){
				    return "fa-star-half-o";
				  } else {
				    return "fa-star-o";
				  }
				}
				$scope.rateProject = function(item, star) {
					$scope.item.userrating = star
				}
			}
		}
	})

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

function App($scope, $http, $location){

	$scope.focus = {
		id: 0,
		kind: 'loc',
		parent: null,
		children: []
	}

	$scope.nodeLink = function(node){
		return '/app?id='
			+ node.id
			+ '&kind=' + node.kind;
	}

	$scope.parentLink = function(node){
		return '/app?id='
			+ node.parent.id
			+ '&kind=loc';
	}

	if(!$location.search().id || isNaN($location.search().id)){
		$location.search({id: 0, kind: 'loc'});
	}

	$scope.commentsShowHide = function(){
		return $scope.commentsVisible ? 'Hide' : 'Show';
	}

	$scope.currentComments = function(){
		return $scope.commentCache[$scope.focus.id];
	}

	$scope.commentCache = [];
	$scope.toggleComments = function(){
		$scope.commentsVisible = !$scope.commentsVisible;
		var fid = $scope.focus.id;
		if($scope.commentsVisible &&
				$scope.focus.kind == 'leaf' &&
				!$scope.commentCache[fid]){
			$scope.commentCache[fid] = [{content: 'Loading...'}];
			$http.get('/comments?id=' + fid)
				.success(function(r){ 
					$scope.commentCache[fid] = r;

					// I'M SO SORRY IT HAD TO BE DONE DON'T JUDGE ME
					setTimeout(function(){
						var commentHeaderHeight = $("#comment-view .header").css('height');
						var userCommentHeight = $(".user-comment").css('height');
						var height = parseInt(commentHeaderHeight) + parseInt(userCommentHeight);
						console.log(height);
						$(".comments ul").css('top', height+'px');
					}, 50)
					
				});
		}
	}

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		$http.get('/meta', {params: $location.search()})
		.success(function(r){ $scope.focus = r; });
	});

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
