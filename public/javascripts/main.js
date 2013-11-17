var app = angular.module('budget', ['ui'])
	.directive('starRating', function(){
		return {
			restrict: 'E',
			scope: {
				item: '=item',
				loggedIn: '=loggedIn'
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
				  var rating = item.userRating || (item.ratings && $scope.round((item.stars / item.ratings) * 2, 0) / 2) || 0;
				  if(rating >= star){
				    return "fa-star";
				  } else if(rating >= star - 0.5){
				    return "fa-star-half-o";
				  } else {
				    return "fa-star-o";
				  }
				}
				
				$scope.rateProject = function(item, stars) {
					$http.post('/rate/' + item.id + '/' + stars)
					.success(function(r){
						if(!item.userRating) item.ratings++;
						item.userRating = stars;
					});
				}
			}
		}
	})

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

function Main($scope){
	$scope.timeago = $.timeago;
	$scope.getDate = function(time){ return new Date(time).toString(); }
}

function App($scope, $http, $location){

	$scope.focus = {
		id: 0,
		kind: 'loc',
		parent: null,
		children: {
			locs: [],
			leaves: []
		}
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

	$scope.comatose = function(num){
		return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
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
		$scope.filtersVisible = false;
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
						// console.log(height);
						$(".comments ul").css('top', height+'px');
					}, 50)
					
				});
		}
	}

	$scope.activeFilter = null;

	$scope.toggleFilters = function(){
		$scope.commentsVisible = false;
		$scope.filtersVisible = !$scope.filtersVisible;
	}

	$scope.clearFilter = function(){
		$scope.activeFilter = null;
	}

	$scope.toggleFilter = function(filter){
		if($scope.activeFilter == filter){
			$scope.activeFilter = null;
		} else {
			$scope.activeFilter = filter;
		}
	}
	$scope.appearsInActiveFilter = function(item){
		if(!$scope.activeFilter) return true;
		return $scope.cats[$scope.activeFilter].indexOf(item.dptDsc) >= 0
	}
	
	$scope.zoomLevel = 6;

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		var searchParams = $location.search();
		$http.get('/meta', {params: searchParams})
		.success(function(r){
			$scope.lastRetrieval = r.children.leaves.length;
			if(searchParams.offset){
				$scope.focus.children.leaves = $scope.focus.children.leaves.concat(r.children.leaves);
			} else {
				$scope.focus = r;
				if(r.userClick){
					map.setView([r.userClick.lat, r.userClick.lng], 10);
				} else if(r.lat && r.lng){
					map.setView([r.lat, r.lng], $scope.zoomLevel);
				}
			}
		});
	});
	
	$scope.navUp = function(){
		if($scope.focus.parent.id == 24 || $scope.focus.parent.id == 26 || $scope.focus.parent.id == 28){
			$scope.zoomLevel = 7;	
		}else{
			if($scope.focus.lat && $scope.focus.lng){
				$scope.zoomLevel--;	
			}
		}
		// $scope.zoomLevel--;
	}
	
	$scope.navDown = function(){
		if($scope.focus.id == 24 || $scope.focus.id == 26 || $scope.focus.id == 28){
			$scope.zoomLevel = 9;
		}else{
			if($scope.focus.lat && $scope.focus.lng){
				$scope.zoomLevel++;	
			}
		}
		// $scope.zoomLevel++;
	}

	$scope.comment = {input: null};
	$scope.submitComment = function(){
		var curFocus = $scope.focus.id;
		var comment = $scope.comment.input;
		$scope.comment.input = null;
		$http.post('/comment/' + $scope.focus.id, {comment: comment})
		.success(function(r){
			console.log(r);
			$scope.commentCache[curFocus].push({
				user: $scope.loggedIn,
				content: comment,
				timestamp: r
			});
		});
	}

	$scope.share = {
		facebook: function(){
			return 'http://www.facebook.com/sharer.php?s=100&p[title]='
				+ encodeURIComponent('BudgetBadger')
				+ '&p[summary]='
				+ encodeURIComponent('Keep track of where your taxes go!')
				+ '&p[url]='
				+ encodeURIComponent($location.absUrl())
				+ '&p[images][0]='
				+ encodeURIComponent('http://budgetbadger.com/preview.png');
		},
		twitter: function(){
			var s = 'Check out #BudgetBadger! ' + $location.absUrl()
			return 'http://twitter.com/home?status=' + escape(s);
		},
		googlePlus: function(){
			return 'https://plus.google.com/share?url=' + escape($location.absUrl());
		}
	}

	$scope.click = {
		listener: function(e){
			map.off('click', this.listener);
			var lat = e.latlng.lat;
			var lng = e.latlng.lng;
			var f = $scope.focus;
			$http.post('/click/' + $scope.focus.id + '/' + lat + '/' + lng)
			.success(function(r){
				L.marker([lat, lng]).addTo(map)
  			.bindPopup('Thanks for your contribution, ' + $scope.loggedIn + '! :)')
  			.openPopup();
  			f.userClick = {lat: lat, lng: lng};
			});
		},
		active: false,
		toggle: function(){
			this.active = !this.active;
			var action = this.active ? 'on' : 'off';
			map[action]('click', this.listener);
		},
		text: function(){ return this.active ? 'Cancel' : 'Point this out!'; }
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

	$scope.lastRetrieval = 0;
	$scope.showMoreLink = function(){
		var params = $location.search();
		var offset = parseInt(params.offset);
		var q = [];
		var hasOffset = false;
		for(var i in params){
			if(i == 'offset'){
				q.push('offset=' + (offset + 30));
				hasOffset = true;
			} else {
				q.push(i + "=" + params[i]);
			}
		}
		if(!hasOffset) q.push('offset=30');
		return '/app?' + q.join('&');
	}
	
}
