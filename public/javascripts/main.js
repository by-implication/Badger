var app = angular.module('budget', ['ui']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

///////////////////////////////////////////// directives ///////////////////////////////

app.directive('starRating', function(){
	return {
		restrict: 'E',
		scope: { item: '=' },
		templateUrl: '/assets/templates/rateStub.html',
		transclude: true,
		controller: function($scope, $http, loggedIn){

			$scope.loggedIn = loggedIn;
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

app.factory('Filters', function($rootScope){
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
		visible: function(){ return $rootScope.specialView.current == 'filters'; },
		current: null,
		toggleVisibility: function(){ $rootScope.specialView.toggle('filters'); },
		clear: function(){ this.current = null; },
		toggle: function(filter){
			this.current = (this.current == filter) ? null : filter;
		},
		appearsInCurrent: function(item){
			return !this.current || this.categories[this.current].indexOf(item.dptDsc) >= 0;
		}
	};
});

app.factory('Click', function($rootScope, loggedIn, Region){
	return {
		active: function(){ return $rootScope.specialView.current == 'click'; },
		listener: function(e){
			map.off('click', this.listener);
			var lat = e.latlng.lat;
			var lng = e.latlng.lng;
			$http.post('/click/' + Focus.value.id + '/' + lat + '/' + lng).success(function(r){
				var m = $scope.marker[Focus.value.id];
				if(m) map.removeLayer(m);
				$scope.marker[Focus.value.id] = new L.marker([lat, lng]).addTo(map)
	  			.bindPopup('Thanks for your contribution, ' + loggedIn + '! :)')
	  			.openPopup();
  			Focus.value.userClick = {lat: lat, lng: lng};
  			$rootScope.specialView.deactivate();
			});
		},
		toggle: function(){
			$rootScope.specialView.toggle('click');
			var active = this.active();
			map[active ? 'on' : 'off']('click', this.listener);
			if(active) Region.features.clear();
		},
		deactivate: function(){
			$rootScope.specialView.deactivate();
			map.off('click', this.listener);
		},
		text: function(){ return this.active() ? 'Cancel' : 'Point this out!'; }
	}
});

app.factory('Comments', function($rootScope, $http, Focus, loggedIn){
	return c = {
		cache: [],
		visible: function(){ return $rootScope.specialView.current == 'comments'; },
		current: function(){ return this.cache[Focus.value.id]; },
		toggle: function(){
			$rootScope.specialView.toggle('comments');
			var fid = Focus.value.id;
			if(this.visible() && Focus.value.kind == 'leaf' && !this.cache[fid]){
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
					user: loggedIn,
					content: comment,
					timestamp: parseInt(r)
				});
			});
		}
	};
});

app.factory('Focus', function(){
	return {
		value: null,
		parentLink: function(){
			return '/app?' + $.param({id: this.value.parent.id, kind: 'loc'});
		}
	};
});

app.factory('Region', function($rootScope, $http, $location){

	var COLORS = [
	  {rating:   0, hsl: [ 0, 59, 50]},
	  {rating:  25, hsl: [24, 60, 50]},
	  {rating:  50, hsl: [48, 82, 50]},
	  {rating:  75, hsl: [72, 54, 50]},
	  {rating: 100, hsl: [96, 50, 50]}
	];

	var getColor = function(rating){

	  var rating = (rating * 100)%100;

	  var end;
	  COLORS.some(function(c, i){ return c.rating >= rating && (end = i); });
	  var start = COLORS[end-1];
	  end = COLORS[end];

	  var percent = (rating - start.rating)/(end.rating - start.rating);
	  var hsl = start.hsl.map(function(e, i){
	  	return e + (end.hsl[i] - e)*percent;
	  });

	  return 'hsl(' + hsl[0] + ', ' + hsl[1] + '%, ' + hsl[2] + '%)';

	};

	var list = [];
	list[4] = 'Region VI';
	list[6] = 'Region III';
	list[7] = 'Region IV-A';
	list[8] = 'Region VII';
	list[9] = 'Region V';
	list[10] = 'Region VIII';
	list[12] = 'Region I';
	list[13] = 'Region II';
	list[14] = 'Region X';
	list[15] = 'Region IX';
	list[16] = 'Region XII';
	list[17] = 'Region XI';
	list[18] = 'Region XIII';
	list[19] = 'Region IV-B';
	list[20] = 'Cordillera Administrative Region';
	list[21] = 'Autonomous Region in Muslim Mindanao';
	list[23] = 'Metro Manila';

	var r = {
		sets: {
			'Luzon': ['Region III', 'Region V', 'National Capital Region', 'Region I', 'Region II', 'Cordillera Administrative Region', 'Region IV', 'Metro Manila'],
			'Visayas': ['Region VI', 'Region VII', 'Region VIII'],
			'Mindanao': ['Region X', 'Region IX', 'Region XII', 'Region XI', 'Region XIII', 'Autonomous Region in Muslim Mindanao'],
			'Local': ['Nationwide', 'CO'],
			'Nationwide': ['Mindanao', 'Luzon', 'Visayas'],
			'Region IV': ['Region IV-A', 'Region IV-B']
		},
		list: list,
		highlight: function(region){
			var id = this.list.indexOf(region);
			if(id != -1){
				this.features.show(id);
			} else if(this.sets[region]){
				this.sets[region].forEach(function(subregion){ r.highlight(subregion); });
			}
		},
		features: {
			list: [],
			active: [],
			map: [21, 9, 7, 13, 18, 6, 8, 20, 17, 10, 12, 19, 23, 14, 16, 4, 15],
			clear: function(){
				this.active.forEach(function(f){ map.removeLayer(f); });
			},
			style: function(rating){
				return {style: {color: getColor(rating) } };
			},
			_show: function(id){
				this.active.push(L.geoJson(this.list[this.map.indexOf(id)], this.style(r.rating.cache[id])).addTo(map)
					.on('click', function(){
						$rootScope.$apply(function(){
							$location.search({id: id, kind: 'loc'});
						});
					})
				);
			},
			show: function(id){
				if(r.rating.cache[id]){
					this._show(id);
				} else {
					var w = $rootScope.$watch(
						function(){ return r.rating.cache[id]; },
						function(v){
							if(v){
								r.features._show(id);
								w();
							}
						}
					);
					$http.get('/meta?' + $.param({id: id, kind: 'loc'})).success(function(response){
						r.rating.cache[response.id] = r.rating.forItem(response);
					});
				}
			}
		},
		rating: {
			forItem: function(item){ return (item.stars/5) / item.ratings; },
			cache: []
		}
	};

	$http.get('/assets/javascripts/ph-regions.json').success(function(response){
		r.features.list = response.features;
	});

	return r;

});

//////////////////////////////////////////// init ////////////////////////////////////////

app.run(function($rootScope, Click, Comments, Filters){

  $rootScope.specialView = {
  	current: null,
  	deactivate: function(){ this.current = null; },
  	toggle: function(view){ this.current = (this.current == view ? null : view); }
  }

});

//////////////////////////////////////////// controllers /////////////////////////////////

app.controller('Main', function($scope, loggedIn){
	$scope.loggedIn = loggedIn;
	$scope.timeago = $.timeago;
	$scope.getDate = function(time){ return new Date(time).toString(); }
});

app.controller('Explore', function($scope, $http, $location, Click, Comments, Filters, Focus, Region){

	$scope.click = Click;
  $scope.comments = Comments;
  $scope.filters = Filters;
	$scope.focus = Focus;

	if(!$location.search().id || isNaN($location.search().id)){
		$location.search({id: 3, kind: 'loc'});
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
		$http.get('/meta', {params: searchParams}).success(function(r){

			Region.features.clear();
			
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

				switch(Focus.value.kind){
					case 'loc': Region.highlight(Focus.value.name); break;
					case 'leaf': Region.highlight(Focus.value.parent.name); break;
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

});

app.controller('Breakdown', function($scope){

});
