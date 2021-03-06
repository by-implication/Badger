var app = angular.module('budget', ['ui', 'ngAnimate']);

app.config(function($locationProvider) { $locationProvider.html5Mode(true); });

///////////////////////////////////////////// directives ///////////////////////////////

app
	.directive('starRating', function(){
		return {
			restrict: 'E',
			scope: { item: '=', fixed: '=' },
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
				    return "fa fa-star";
				  } else if(rating >= star - 0.5){
				    return "fa fa-star-half-o";
				  } else {
				    return "fa fa-star-o";
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
	.directive('biReqLogin', function(loggedIn){
		return {
			restrict: 'E',
			templateUrl: '/assets/templates/biReqLogin.html',
			transclude: true,
			scope: {
				biReqMessage: '=reqMsg'
			},
			controller: function($scope){
				$scope.loggedIn = loggedIn;
			}
		}
	})
	.directive('biCurtain', function($window){
		return {
			link: function(scope, elm, attrs){
				$("html, body").animate({scrollTop: "0px"});
			}
		}
	})
	.directive('biMap', function(Focus, loggedIn){
		return {
			link: function(scope, elm, attrs){

				scope.map = L.map('map', {
					scrollWheelZoom: false,
					center: [14.612209, 121.0527097],
					zoom: 7
				});

				// add an OpenStreetMap tile layer
				L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
				    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
				}).addTo(scope.map);

				if(Focus.value.userClick){
					var lat = Focus.value.userClick.lat;
					var lng = Focus.value.userClick.lng;
					scope.marker[Focus.value.id] = new L.marker([lat, lng], {color: '#000'}).addTo(scope.map)
		  			.bindPopup('Thanks for your contribution, ' + loggedIn + '! :)')
		  			.openPopup();
				}

			},

			controller: function($scope, $http, loggedIn, Regions, Focus){

				$scope.click = {};
				$scope.click.active = function(){ 
					return $scope.specialView.current == 'click'; 
				}
				$scope.click.listener = function(e){
					$scope.map.off('click', this.listener);
					var lat = e.latlng.lat;
					var lng = e.latlng.lng;
					$http.post('/click/' + Focus.value.id + '/' + lat + '/' + lng).success(function(r){
						var m = $scope.marker[Focus.value.id];
						if(m) $scope.map.removeLayer(m);
						$scope.marker[Focus.value.id] = new L.marker([lat, lng]).addTo($scope.map)
			  			.bindPopup('Thanks for your contribution, ' + loggedIn + '! :)')
			  			.openPopup();
		  			Focus.value.userClick = {lat: lat, lng: lng};
		  			$scope.specialView.deactivate();
		  			$scope.focus.value.clicks++;
					});
				}
				$scope.click.toggle = function(){
					$scope.specialView.toggle('click');
					var active = this.active();
					$scope.map[active ? 'on' : 'off']('click', this.listener);
					if(active) Regions.features.clear();
				}
				$scope.click.deactivate = function(){
					$scope.specialView.deactivate('click');
					$scope.map.off('click', this.listener);
				}
			}
		}
	});

//////////////////////////////////////////// services ////////////////////////////////////

app.factory('Loading', function(){
	return {
		pending: 0,
		on: function(){ this.pending++; },
		off: function(){ this.pending--; }
	};
});

app.factory('Years', function(years, $location){
	years.unshift(null);
	return {
		list: years,
		current: $location.search().year,
		label: function(year){ return year ? year : 'All Years'; },
		setCurrent: function(year){
			this.current = year;
			var s = $location.search();
			if(this.current){
				$location.search($.extend(s, {year: this.current, offset: 0}));
			} else {
				delete s.year;
				$location.search($.extend(s, {offset: 0}));
			}
		}
	};
})

app.factory('Focus', function(categories){
	return {
		value: {},
		set: function(v){ this.value = v; }
	};
});

app.factory('Categories', function($rootScope, categories, $location){
	categories.unshift({id: null, name: 'All', subcats: []});
	return {
		list: categories,
		current: categories[$location.search().category],
		label: function(){ return this.current ? this.current.name : 'All'; },
		setCurrent: function(cat){
			this.current = cat;
			var s = $location.search();
			if(this.current.id){
				$location.search($.extend(s, {category: this.current.id, offset: 0}));
			} else {
				delete s.category;
				$location.search($.extend(s, {offset: 0}));
			}
		}
	};
});

app.factory('Comments', function($rootScope, $http, loggedIn, Focus){
	return c = {
		cache: [],
		current: function(){
			var fid = Focus.value.id;
			if(!this.cache[fid]){
				this.cache[fid] = [{content: 'Loading...'}];
				$http.get('/focus?' + $.param({id: fid})).success(function(r){ 
					c.cache[fid] = r;
				});
			}
			return this.cache[fid];
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

app.factory('Sort', function($location){
	var fields = ['Amount', 'Year', 'Ratings', 'Latest Activity'];
	var field = fields.indexOf($location.search().sort)
	if(field == -1) field = 0;
	var orders = ['Ascending', 'Descending'];
	var order = orders.indexOf($location.search().sort)
	if(order == -1) order = 1;
	return {
		fields: fields,
		field: field,
		setField: function(field){
			this.field = field;
			$location.search($.extend($location.search(), {sort: this.fields[this.field]}));
		},
		orders: orders,
		order: order,
		setOrder: function(order){
			this.order = order;
			$location.search($.extend($location.search(), {order: this.orders[this.order]}));
		}
	};
});

app.factory('Regions', function($rootScope, $http, $location, regions){

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

	regions.unshift({id: null, name: 'Everywhere'});
	var r = {
		sets: {
			'Luzon': ['Region III', 'Region V', 'National Capital Region', 'Region I', 'Region II', 'Cordillera Administrative Region', 'Region IV', 'Metro Manila'],
			'Visayas': ['Region VI', 'Region VII', 'Region VIII'],
			'Mindanao': ['Region X', 'Region IX', 'Region XII', 'Region XI', 'Region XIII', 'Autonomous Region in Muslim Mindanao'],
			'Local': ['Nationwide', 'CO'],
			'Nationwide': ['Mindanao', 'Luzon', 'Visayas'],
			'Region IV': ['Region IV-A', 'Region IV-B']
		},
		list: regions,
		current: regions[$location.search().region],
		setCurrent: function(region){
			this.current = region;
			var s = $location.search();
			if(this.current.id){
				$location.search($.extend(s, {region: this.current.id, offset: 0}));
			} else {
				delete s.region;
				$location.search($.extend(s, {offset: 0}));
			}
		},
		label: function(){ return this.current ? this.current.name : 'Everywhere'; },
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
					$http.get('/explore/meta', {params: {id: id, kind: 'loc'}}).success(function(response){
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

app.run(function($rootScope, categories){

  $rootScope.specialView = {
  	current: null,
  	deactivate: function(view){
  		if(view){
  			if(this.current == view) this.current = null;
  		} else this.current = null;
  	},
  	toggle: function(view){ this.current = (this.current == view ? null : view); }
  }

  $rootScope.getCategory = function(leaf){
		for(var i in categories){
			var cat = categories[i];
			if(cat.subcats.indexOf(leaf.dptDsc) != -1) return cat.name;
		}
	}

});

//////////////////////////////////////////// controllers /////////////////////////////////

app.controller('Main', function($scope, loggedIn){
	$scope.loggedIn = loggedIn;
	$scope.timeago = $.timeago;
	$scope.getDate = function(time){ return new Date(time).toString(); }
	$scope.curtainHeight = function(){
		return $(document).height() + "px";
	}
});

app.controller('Explore', function($scope, $http, $location, Comments, Categories, Regions, Sort, Focus, Years, Loading){

	$scope.loading = Loading;
	$scope.focus = Focus;
  $scope.comments = Comments;
  $scope.categories = Categories;
	$scope.regions = Regions;
	$scope.sort = Sort;
	$scope.years = Years;
	$scope.leaves = [];
	$scope.ribbons = {
		'education': {
			'icon': 'fa-pencil',
			'color': '#006B80'
		}, 
		'defence': {
			'icon': 'fa-shield',
			'color': '#8f2f34'
		}, 
		'agriculture': {
			'icon': 'fa-leaf',
			'color': '#7CB28C'
		}, 
		'economy': {
			'icon': 'fa-money',
			'color': '#74886A'
		}, 
		'public services': {
			'icon': 'fa-group',
			'color': '#D0876D'
		}, 
		'industry': {
			'icon': 'fa-wrench',
			'color': '#FC9B35'
		}, 
		'communications': {
			'icon': 'fa-phone',
			'color': '#30587D'
		}, 
		'local government': {
			'icon': 'fa-building-o',
			'color': '#BC5E69'
		}, 
		'central government': {
			'icon': 'fa-flag',
			'color': '#5a9bd1'
		}, 
		'foreign affairs': {
			'icon': 'fa-plane',
			'color': '#B158A5'
		},
		'justice': {
			'icon': 'fa-gavel',
			'color': '#7B5D9C'
		}
	};

	var s = $location.search();
	var refresh = false;

	if(s.category == undefined){
		if(!Categories.current) Categories.current = Categories.list[0];
		if(Categories.current.id) s.category = Categories.current.id;
		refresh = true;
	}

	if(s.region == undefined){
		if(!Regions.current) Regions.current = Regions.list[0];
		if(Regions.current.id) s.region = Regions.current.id;
		refresh = true;
	}

	if(s.offset == undefined){
		s.offset = 0;
		refresh = true;
	}

	if(s.sort == undefined){
		s.sort = Sort.fields[Sort.field];
		refresh = true;
	}

	if(s.order == undefined){
		s.order = Sort.orders[Sort.order];
		refresh = true;
	}
	
	if(refresh) $location.search(s);

	$scope.nodeLink = function(node){
		var s = $.extend({}, $location.search());
		return '/explore?' + $.param($.extend(s, {focus: node.id}));
	}

	$scope.comatose = function(num){
		return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}

	$scope.truncate = function(str){
		return str.replace(/government/gi, 'gov\'t')
			.replace(/communications/gi, 'comm');
	}
	
	var zoomLevel = 6;
	
	function getSearchParams(path){
		return path.split("?")[1].split("&").map(function(s){
			var a = s.split("=");
			var o = {};
			o[a[0]] = a[1];
			return o;
		}).reduce(function(prev, cur){ return $.extend(prev, cur); }, {});
	}

	$scope.listView = function(view){
		$scope.view = view;
		var s = $.extend({}, $location.search());
		delete s.focus;
		$location.search(s);
	}

	function setFocusView(leafId){
	 	$scope.view = 'focus';
	 	for(var i in $scope.leaves){
	 		var leaf = $scope.leaves[i];
	 		if(leaf.id == leafId){
	 			Focus.set(leaf);
	 			break;
	 		}	
	 	}
	}

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		var searchParams = $location.search();
		if(!searchParams.focus || newPath == oldPath){
			var oldSearch = getSearchParams(oldPath);
			if(oldSearch.focus && !searchParams.focus) return;
			Loading.on();
			$http.get('/explore/meta', {params: searchParams}).success(function(r){
				$scope.lastRetrieval = r.list.length;
				$scope.total = r.count;
				$scope.leaves = (parseInt(searchParams.offset) > parseInt(oldSearch.offset))
					? $scope.leaves.concat(r.list)
					: r.list;
				if(searchParams.focus) setFocusView(searchParams.focus); else $scope.view = 'list';
				Loading.off();
			});
		} else setFocusView(searchParams.focus);
	});

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
		return '/explore?' + q.join('&');
	}

	$scope.totalAmount = function(item){
		if(!item.total) item.total = item.ps + item.mooe + item.co;
		return item.total;
	}

});

app.controller('Breakdown', function($scope, $http, $location){

	if(!$location.search().kind){ $location.search({
		kind: 'GAA',
		year: 2013,
		dpt: 0,
		owner: 0,
		fpap: 0
	}); }

	$scope.$watch(function(){ return $location.absUrl(); }, function(newPath, oldPath){
		var searchParams = $location.search();
		$http.get('/breakdown/meta', {params: searchParams}).success(function(r){
			console.log(r);
		});
	});

});
