# --- !Ups

CREATE TABLE categorys(
	category_id serial PRIMARY KEY,
	category_name text NOT NULL,
	category_subcats text[] NOT NULL
);;

INSERT INTO categorys (category_id, category_name, category_subcats) VALUES
	(DEFAULT, 'academia', '{"Department of Science and Technology", "State Universities and Colleges", "DepEd - School Building Program", "Department of Education", "Department of Education - School Building Program"}'),
	(DEFAULT, 'defence', '{"AFP  Modernization Program", "Civil Service Commission", "Department of National Defense"}'),
	(DEFAULT, 'agriculture', '{"Department of Agrarian Reform", "Agriculture and Fisheries Modernization Program", "Agrarian Reform Fund", "Department of Agriculture"}'),
	(DEFAULT, 'economy', '{"Commission on Audit", "General Fund Adjustments", "Economic Stimulus Fund", "Department of Finance", "Debt Service Fund-Interest Payment", "Department of Budget and Management", "Compensation Adjustment Fund", "National Economic and Development Authority", "Tax Expenditures Fund", "Budgetary Support to Government Corporations", "Priority Social and Economic Projects Fund"}'),
	(DEFAULT, 'public services', '{"Pension and Gratuity Fund", "Department of Social Welfare and Development", "Retirement Benefits Fund (Pension and Gratuity Fund)", "Department of Public Works and Highways", "E-Government Fund", "Miscellaneous Personnel Benefits Fund", "Payapa at Masaganang Pamayanan Fund", "Department of Health", "Priority Development Assistance Fund"}'),
	(DEFAULT, 'industry', '{"Department of Labor and Employment", "Department of Trade and Industry", "Department of Energy", "Department of Environment and Natural Resources"}'),
	(DEFAULT, 'communications', '{"Department of Transportation and Communications", "Presidential Communications Operations Office", "Office of the Press Secretary"}'),
	(DEFAULT, 'local government', '{"Department of the Interior and Local Government", "Allocations to Local Government Units", "Autonomous Region in Muslim Mindanao"}'),
	(DEFAULT, 'central government', '{"Congress of the Philippines", "Commission on Elections", "National Unification Fund", "Office of the Vice-President", "Joint Legislative-Executive Councils", "Office of the President", "Contingent Fund", "Other Executive Offices", "Unprogrammed Fund", "Calamity Fund"}'),
	(DEFAULT, 'justice', '{"Office of the Ombudsman", "Department of Justice", "The Judiciary", "Commission on Human Rights"}'),
	(DEFAULT, 'foreign affairs', '{"Department of Foreign Affairs", "Department of Tourism", "International Commitments Fund"}')
;;

# --- !Downs

DROP TABLE IF EXISTS categorys;;
