# --- !Ups

CREATE TABLE locations (
	location_id serial PRIMARY KEY,
  location_name text NOT NULL,
  location_areas text[] NOT NULL,
	location_lat numeric(10,7) NOT NULL,
	location_lng numeric(10,7) NOT NULL
);;

INSERT INTO locations (location_id, location_name, location_areas, location_lat, location_lng) VALUES
	(1, 'Foreign', '{"Indonesia, Jakarta", "Saudi Arabia, Riyadh", "Switzerland, Geneva", "U.S.A., New York", "China, Beijing", "Caracas, Venezuela", "France, Paris", "Saudi Arabia, Jeddah", "United Kingdom, London", "Japan, Tokyo", "U.S.A., Washington, D.C.", "Phomn Penh, Cambodia", "U.A.E., Abu Dhabi", "Jordan, Amman", "U.S.A., Honolulu", "Laos, Vientianne", "India, New Delhi", "Italy, Rome", "Pretoria, South Africa", "Malaysia, Kuala Lumpur", "Japan, Kobe", "Vatican, Holy See", "Israel, Tel-Aviv", "C.I.S., Moscow", "Argentina, Buenos Aires", "Canada, Toronto", "Hungary, Budapest", "Austria, Vienna", "Belgium, Brussels", "Brazil, Brasilia", "U.S.A., San Francisco", "U.S.A., Agana", "Qatar, Doha", "Nigeria, Lagos", "Pakistan, Islamabad", "U.S.A., Chicago", "Kuwait, Kuwait", "Mexico, Mexico", "Iraq, Baghdad", "Singapore, Singapore", "Switzerland, Berne", "New Zealand, Wellington", "Libya, Tripoli", "Lebanon, Beirut", "Chile, Santiago", "Papua New Guinea, P. Moresby", "Hongkong, Hongkong", "Germany, Bonn", "Canada, Ottawa", "Korea South, Seoul", "Bangladesh, Dhaka", "Kenya, Nairobi", "Xiamen, China", "Brunei, Bandar Seri Begawan", "Iran, Teheran", "Oman, Muscat", "Australia, Sydney", "Thailand, Bangkok", "Egypt, Cairo", "Australia, Canberra", "Turkey, Ankara", "Republic of Czechoslovakia, Prague", "Myanmar, Yangon", "Vietnam, Hanoi", "Netherlands, The Hague", "Canada, Vancouver", "Greece, Athens", "Indonesia, Manado", "Milan, Italy", "Guangzhou, China", "U.S.A., Los Angeles", "Bahrain, Manama", "Spain, Madrid", "Sweden, Stockholm", "Vietnam, Ho Chi Minh", "Cuba, Havana", "Micronesia, Pohnpei", "Vladivostok, Russia", "Saipan, Saipan", "Republic of Palau, Koror", "Germany, Hamburg", "Romania, Bucharest"}', 90, 0),
	(2, 'Nationwide', '{"Nationwide"}', 12.879721, 121.774017),
	(3, 'Region 6', '{"Region VI"}', 11.0049836, 122.5372741),
	(4, 'Cenral Office', '{"CO"}', 12.879721, 121.774017),
	(5, 'Region 3', '{"Region III"}', 15.4827722, 120.7120023),
	(6, 'Region 7', '{"Region VII"}', 10.2968562, 123.8886774),
	(7, 'Region 5', '{"Region V"}', 13.4209885, 123.4136736),
	(8, 'Region 8', '{"Region VIII"}', 12.2445533, 125.0388164),
	(9, 'NCR', '{"National Capital Region", "Metro Manila"}', 14.6090537, 121.0222565),
	(10, 'Region 1', '{"Region I", "3rd District, Pangasinan, San Carlos City"}', 16.0832144, 120.6199895),
	(11, 'Region 2', '{"Region II", "Cagayan"}', 16.9753758, 121.8107079),
	(12, 'Region 10', '{"Region X"}', 8.0201635, 124.6856509),
	(13, 'Region 9', '{"Region IX"}', 8.1540770, 123.258793),
	(14, 'Region 12', '{"Region XII"}', 6.2706918, 124.6856509),
	(15, 'Region 11', '{"Region XI"}', 7.3041622, 126.0893406),
	(16, 'Region 13', '{"Region XIII"}', 8.8014562, 125.7406882),
	(17, 'CAR', '{"Cordillera Administrative Region"}', 17.3512542, 121.1718851),
	(18, 'ARMM', '{"Autonomous Region in Muslim Mindanao"}', 6.9568313, 124.2421597),
	(19, 'Region 4', '{"Region IV", "Region IV-A", "Region IV-B"}', 14.1007803, 121.0793705),
	(20, 'Mindanao', '{"Mindanao", "Region X", "Region IX", "Region XII", "Region XI", "Region XIII", "Autonomous Region in Muslim Mindanao"}', 8.4961299, 123.3034062),
	(21, 'Luzon', '{"Luzon", "Region III", "Region V", "National Capital Region", "Region I", "Region II", "Cordillera Administrative Region", "Region IV", "Metro Manila"}', 16.5662318, 121.2626366),
	(22, 'Visayas', '{"Visayas", "Region VI", "Region VII", "Region VIII"}', 11.0049836, 122.5372741)
;;

# --- !Downs

DROP TABLE IF EXISTS locations;;
