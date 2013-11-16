function Main($scope, $http){
	
	$scope.status = "working";
	$scope.focus = {
		"name": "Pepe Bawagan",
		"rating": 3.5,
		"amount": "huge sum",
		"parent": "pepe's dad"
	}
	$scope.children = [
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
	var deps = [
		"Department of the Interior and Local Government",
		"Department of Transportation and Communications",
		"Pension and Gratuity Fund",
		"Department of Science and Technology",
		"Allocations to Local Government Units",
		"State Universities and Colleges",
		"Compensation Adjustment Fund",
		"Department of Budget and Management",
		"Other Executive Offices",
		"Department of Environment and Natural Resources",
		"Budgetary Support to Government Corporations",
		"Department of Energy",
		"Department of Health",
		"DepEd - School Building Program",
		"Presidential Communications Operations Office",
		"Office of the Ombudsman",
		"Congress of the Philippines",
		"National Economic and Development Authority",
		"Autonomous Region in Muslim Mindanao",
		"Office of the Press Secretary",
		"Department of Agriculture",
		"Department of Education",
		"Department of Foreign Affairs",
		"Department of Social Welfare and Development",
		"Commission on Elections",
		"National Unification Fund",
		"AFP  Modernization Program",
		"Department of Agrarian Reform",
		"Retirement Benefits Fund (Pension and Gratuity Fund)",
		"Commission on Human Rights",
		"Calamity Fund",
		"Office of the Vice-President",
		"Civil Service Commission",
		"Agriculture and Fisheries Modernization Program",
		"Tax Expenditures Fund",
		"Department of Justice",
		"Department of Labor and Employment",
		"Priority Development Assistance Fund",
		"Priority Social and Economic Projects Fund",
		"Department of Public Works and Highways",
		"Department of Education - School Building Program",
		"Commission on Audit",
		"General Fund Adjustments",
		"Economic Stimulus Fund",
		"Department of Tourism",
		"Department of Finance",
		"E-Government Fund",
		"The Judiciary",
		"Miscellaneous Personnel Benefits Fund",
		"Department of National Defense",
		"Payapa at Masaganang Pamayanan Fund",
		"Debt Service Fund-Interest Payment",
		"Unprogrammed Fund",
		"International Commitments Fund",
		"Joint Legislative-Executive Councils",
		"Agrarian Reform Fund",
		"Department of Trade and Industry",
		"Office of the President",
		"Contingent Fund"
	]
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