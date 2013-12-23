var app = angular.module('budget', ['ui']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

///////////////////////////////////////////// directives ///////////////////////////////

app.directive('starRating', function(){
	return {
		restrict: 'E',
		scope: {
			item: '=',
			loggedIn: '='
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
});

//////////////////////////////////////////// services ////////////////////////////////////

app.factory('Filters', function(){
	return {
		categories: {
			"academia": ["Department of Science and Technology", "State Universities and Colleges", "DepEd - School Building Program", "Department of Education", "Department of Education - School Building Program"],
			"defence": ["AFP  Modernization Program", "Civil Service Commission", "Department of National Defense"],
			"agriculture": ["Department of Agrarian Reform", "Agriculture and Fisheries Modernization Program", "Agrarian Reform Fund", "Department of Agriculture"],
			"economy": ["Commission on Audit", "General Fund Adjustments", "Economic Stimulus Fund", "Department of Finance", "Debt Service Fund-Interest Payment", "Department of Budget and Management", "Compensation Adjustment Fund", "National Economic and Development Authority", "Tax Expenditures Fund", "Budgetary Support to Government Corporations", "Priority Social and Economic Projects Fund"],
			"public services": ["Pension and Gratuity Fund", "Department of Social Welfare and Development", "Retirement Benefits Fund (Pension and Gratuity Fund)", "Department of Public Works and Highways", "E-Government Fund", "Miscellaneous Personnel Benefits Fund", "Payapa at Masaganang Pamayanan Fund", "Department of Health", "Priority Development Assistance Fund"],
			"industry": ["Department of Labor and Employment", "Department of Trade and Industry", "Department of Energy", "Department of Environment and Natural Resources"],
			"communications": ["Department of Transportation and Communications", "Presidential Communications Operations Office", "Office of the Press Secretary"],
			"local government": ["Department of the Interior and Local Government", "Allocations to Local Government Units", "Autonomous Region in Muslim Mindanao"],
			"central government": ["Congress of the Philippines", "Commission on Elections", "National Unification Fund", "Office of the Vice-President", "Joint Legislative-Executive Councils", "Office of the President", "Contingent Fund", "Other Executive Offices", "Unprogrammed Fund", "Calamity Fund"],
			"justice": ["Office of the Ombudsman", "Department of Justice", "The Judiciary", "Commission on Human Rights"],
			"foreign affairs": ["Department of Foreign Affairs", "Department of Tourism", "International Commitments Fund"]
		},
		visible: false,
		current: null,
		toggleVisibility: function(){ this.visible = !this.visible; },
		clear: function(){ this.current = null; },
		toggle: function(filter){
			this.current = (this.current == filter) ? null : filter;
		},
		appearsInCurrent: function(item){
			return !this.current || this.categories[this.current].indexOf(item.dptDsc) >= 0;
		}
	};
});

app.factory('Click', function(){
	return {
		active: false,
		listener: function(e){
			map.off('click', this.listener);
			var lat = e.latlng.lat;
			var lng = e.latlng.lng;
			$http.post('/click/' + Focus.value.id + '/' + lat + '/' + lng).success(function(r){
				var m = $scope.marker[Focus.value.id];
				if(m) map.removeLayer(m);
				$scope.marker[Focus.value.id] = new L.marker([lat, lng]).addTo(map)
	  			.bindPopup('Thanks for your contribution, ' + $scope.loggedIn + '! :)')
	  			.openPopup();
  			Focus.value.userClick = {lat: lat, lng: lng};
  			Click.active = false;
			});
		},
		toggle: function(){
			this.active = !this.active;
			var action = this.active ? 'on' : 'off';
			map[action]('click', this.listener);
			if(this.active) $scope.activeFeatures.forEach(function(f){ map.removeLayer(f); });
		},
		deactivate: function(){
			this.active = false;
			map.off('click', this.listener);
		},
		text: function(){ return this.active ? 'Cancel' : 'Point this out!'; }
	}
});

app.factory('Comments', function(Focus){
	return c = {
		cache: [],
		visible: false,
		current: function(){ return this.cache[Focus.value.id]; },
		toggle: function(){
			console.log('wee');
			this.visible = !this.visible;
			var fid = Focus.value.id;
			if(this.visible && Focus.value.kind == 'leaf' && !this.cache[fid]){
				this.cache[fid] = [{content: 'Loading...'}];
				$http.get('/comments?' + $.param({id: fid})).success(function(r){ 
					c.cache[fid] = r;
				});
			}
		},
		input: null,
		submit: function(){
			var focusId = Focus.value.id;
			var comment = this.input;
			this.input = null;
			$http.post('/comment/' + Focus.value.id, {comment: comment})
			.success(function(r){
				if(!c.cache[focusId]) c.cache[focusId] = [];
				c.cache[focusId].push({
					user: $scope.loggedIn,
					content: comment,
					timestamp: parseInt(r)
				});
			});
		}
	};
});

app.factory('Focus', function(){
	return {
		value: {
			id: 0,
			kind: 'loc',
			parent: null,
			children: {
				locs: [],
				leaves: []
			}
		},
		parentLink: function(){
			return '/app?' + $.param({id: this.value.parent.id, kind: 'loc'});
		}
	};
});

app.factory('Features', function(Region, Rating){
	return {
		list: [],
		active: [],
		show: function(i){
			var id = Region.ids[Region.list[i]];
			var l = L.geoJson(this.list[i], Region.style(Rating.cache[i]))
				.addTo(map)
				.on('click', function(){
					$scope.$apply(function(){
						$location.search({
							id: id,
							kind: 'loc'
						});
					});
				});
			this.active.push(l);
		}
	}
});

app.factory('Region', function(){

	var RATING_COLORS = [
	  {h:  0, s: 59, l: 50, rating:   0},
	  {h: 24, s: 60, l: 50, rating:  25},
	  {h: 48, s: 82, l: 50, rating:  50},
	  {h: 72, s: 54, l: 50, rating:  75},
	  {h: 96, s: 50, l: 50, rating: 100}
	];

	var multiColor = function(rating){
	  
	  var colors = RATING_COLORS;
	  var start = colors[0];
	  var next  = colors[1];

	  for (var i = 0; i < colors.length; i++){
	    var color = colors[i];
	    if(color.rating < rating) {
	      start = color;
	      next = colors[i + 1];
	    }
	  }

	  return {
	    hsl: [[start.h, next.h], [start.s, next.s], [start.l, next.l]],
	    min: start.rating, max: next.rating
	  };

	}

	var transition = function(value, maximum, startPoint, endPoint){
	  return startPoint + (endPoint - startPoint)*value/maximum;
	}

	var transitionN = function(value, maximum, pairs){
	  var results = [];
	  for(var i in pairs){
	    results.push(transition(value, maximum, pairs[i][0], pairs[i][1]));
	  }
	  return results;
	}

	var getBackgroundStyle = function(rating){
	  var rating = (rating * 100)%100;
	  var valueRange = multiColor(rating);
	  var newValues = transitionN(
	    rating - valueRange.min,
	    valueRange.max - valueRange.min,
	    valueRange.hsl
	  );
	  return 'hsl(' + newValues[0] + ', ' + newValues[1] + '%, ' + newValues[2] + '%)';
	};

	return {
		ids: {
			'Autonomous Region in Muslim Mindanao': 21,
			'Region V': 9,
			'Region IV-A': 7,
			'Region II': 13,
			'Region XIII': 18,
			'Region III': 6,
			'Region VII': 8,
			'Cordillera Administrative Region': 20,
			'Region XI': 17,
			'Region VIII': 10,
			'Region I': 12,
			'Region IV-B': 19,
			'Metro Manila': 23,
			'Region X': 14,
			'Region XII': 16,
			'Region VI': 4,
			'Region IX': 15
		},
		sets: {
			'Luzon': ['Region III', 'Region V', 'National Capital Region', 'Region I', 'Region II', 'Cordillera Administrative Region', 'Region IV', 'Metro Manila'],
			'Visayas': ['Region VI', 'Region VII', 'Region VIII'],
			'Mindanao': ['Region X', 'Region IX', 'Region XII', 'Region XI', 'Region XIII', 'Autonomous Region in Muslim Mindanao'],
			'Local': ['Nationwide', 'CO'],
			'Nationwide': ['Mindanao', 'Luzon', 'Visayas'],
			'Region IV': ['Region IV-A', 'Region IV-B']
		},
		list: [
			'Autonomous Region in Muslim Mindanao',
			'Region V',
			'Region IV-A',
			'Region II',
			'Region XIII',
			'Region III',
			'Region VII',
			'Cordillera Administrative Region',
			'Region XI',
			'Region VIII',
			'Region I',
			'Region IV-B',
			'Metro Manila',
			'Region X',
			'Region XII',
			'Region VI',
			'Region IX'
		],
		style: function(v){
			return {style: {color: getBackgroundStyle(v) } };
		}
	};
});

app.factory('Rating', function($http, Region){
	
	var r = {
		forItem: function(item){ return (item.stars/5) / item.ratings; },
		cache: [],
	};

	for(var name in Region.ids){
		var id = Region.ids[name];
		$http.get('/meta?' + $.param({id: id, kind: 'loc'})).success(function(response){
			r.cache[response.id] = r.forItem(response);
		});
	}

	return r;

});

//////////////////////////////////////////// controllers /////////////////////////////////

app.controller('Main', function($scope){
	$scope.timeago = $.timeago;
	$scope.getDate = function(time){ return new Date(time).toString(); }
});

app.controller('App', function($scope, $http, $location, Click, Comments, Features, Filters, Focus, Region){

	$scope.click = Click;
	$scope.comments = Comments;
	$scope.filters = Filters;
	$scope.focus = Focus;

	if(!$location.search().id || isNaN($location.search().id)){
		$location.search({id: 0, kind: 'loc'});
	}

	$scope.nodeLink = function(node){
		return '/app?' + $.param({id: node.id, kind: node.kind});
	}

	$scope.comatose = function(num){
		return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}
	
	var zoomLevel = 6;

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		var searchParams = $location.search();
		$http.get('/meta', {params: searchParams})
		.success(function(r){

			$scope.activeFeatures.forEach(function(f){ map.removeLayer(f); });
			
			$scope.lastRetrieval = r.children ? r.children.leaves.length : 0;
			if(searchParams.offset){
				Focus.value.children.leaves = Focus.value.children.leaves.concat(r.children.leaves);
			} else {

				Focus.value = r;

				if(r.userClick){
					map.setView([r.userClick.lat, r.userClick.lng], 10);
				} else if(r.lat && r.lng){
					map.setView([r.lat, r.lng], zoomLevel);
				}

				// region highlighting
				
				function recursiveHighlight(region){
					var i = Region.list.indexOf(region);
					if(i != -1){
						Features.show(i);
					} else {
						var a = Region.sets[region];
						if(a){
							for(var i in a){
								recursiveHighlight(a[i]);
							}
						}
					}
				}

				if(Focus.value.parent){
					var i = Region.list.indexOf(Focus.value.parent.name);
					if(i != -1) Features.show(i);
				}

				var i = Region.list.indexOf(Focus.value.name);
				if(i != -1){
					Features.show(i);
				}

				if(Region.sets[Focus.value.name]){
					recursiveHighlight(Focus.value.name);
				}

			}

			Click.deactivate();

		});
	});

	$scope.navUp = function(){
		if(Focus.value.parent.id == 24 || Focus.value.parent.id == 26 || Focus.value.parent.id == 28){
			zoomLevel = 7;	
		}{
			zoomLevel = 6;
		}
	}
	
	$scope.navDown = function(){
		if(Focus.value.id == 24 || Focus.value.id == 26 || Focus.value.id == 28){
			zoomLevel = 9;
		}{
			zoomLevel = 6;
		}
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

	$scope.marker = [];

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

	$scope.totalAmount = function(item){
		if(!item.total) item.total = item.ps + item.mooe + item.co;
		return item.total;
	}

	$scope.activeFeatures = [];
	$http.get('/assets/javascripts/ph-regions.json')
	.success(function(r){ Features.list = r.features; });

});
