# --- !Ups

CREATE TABLE locations (
	location_id serial PRIMARY KEY,
  location_name text NOT NULL,
  location_parent_id int REFERENCES locations(location_id),
	location_lat numeric(10,7) NOT NULL,
	location_lng numeric(10,7) NOT NULL,
	location_ps int NOT NULL,
	location_mooe int NOT NULL,
	location_co int NOT NULL
);;

INSERT INTO locations (location_id, location_name, location_parent_id, location_lat, location_lng, location_ps, location_mooe, location_co) VALUES
	(0, 'All', NULL, 0, 0, -1567637629, 272739156, 1567669729),
	(1, 'Foreign', 0, 90, 0, 24260047, 8892725, 439916),
	-- (2, 'Local', 0 12.879721, 121.774017, -1591897676, 263846431, 1567229813),
	(3, 'Nationwide', 0, 12.879721, 121.774017, -2059776659, -1781410282, 1401177701),
	(4, 'Region VI', 28, 11.0049836, 122.5372741, 95480988, 23786219, 40438974),
	(5, 'CO', 0, 12.879721, 121.774017, 467878983, 2045256713, 166052112),
	(6, 'Region III', 26, 15.4827722, 120.7120023, 99481872, 23168975, 54880160),
	(7, 'Region IV-A', 22, 14.1007803, 121.0793705, 94584905, 18913403, 37743743),
	(8, 'Region VII', 28, 10.2968562, 123.8886774, 72488212, 19635086, 36658163),
	(9, 'Region V', 26, 13.4209885, 123.4136736, 77289627, 23041331, 37986129),
	(10, 'Region VIII', 28, 12.2445533, 125.0388164, 64892777, 18682816, 31921091),
	(11, 'National Capital Region', 26, 14.6090537, 121.0222565, 126269103, 50459741, 70386048),
	(12, 'Region I', 26, 16.0832144, 120.6199895, 68810300, 14604383, 34160682),
	(13, 'Region II', 26, 16.9753758, 121.8107079, 50409050, 13627713, 30468352),
	(14, 'Region X', 24, 8.0201635, 124.6856509, 53531842, 16802863, 39737202),
	(15, 'Region IX', 24, 8.1540770, 123.258793, 48262441, 16132081, 21716130),
	(16, 'Region XII', 24, 6.2706918, 124.6856509, 46670199, 16142296, 23991215),
	(17, 'Region XI', 24, 7.3041622, 126.0893406, 49052532, 16129830, 27023670),
	(18, 'Region XIII', 24, 8.8014562, 125.7406882, 33634103, 12288092, 28734946),
	(19, 'Region IV-B', 22, 9.8432065, 118.7364783, 37047998, 10452326, 33381377),
	(20, 'Cordillera Administrative Region', 26, 17.3512542, 121.1718851, 30688108, 11575038, 28904242),
	(21, 'Autonomous Region in Muslim Mindanao', 24, 6.9568313, 124.2421597, 49413028, 22313952, 12824237),
	(22, 'Region IV', 26, 14.1007803, 121.0793705, 7544250, 7598406, 5106245),
	(23, 'Metro Manila', 26, 14.6090537, 121.0222565, 0, 2664698, 0),
	(24, 'Mindanao', 3, 8.4961299, 123.3034062, 280921945, 99851871, 154582400),
	(25, 'Indonesia, Jakarta', 1, -6.211544, 106.845172, 1223176, 551122, 20854),
	(26, 'Luzon', 3, 16.5662318, 121.2626366, 592563961, 176165931, 333046878),
	(27, 'Saudi Arabia, Riyadh', 1, 24.7116667, 46.7241667, 1528122, 515831, 16161),
	(28, 'Visayas', 3, 11.0049836, 122.5372741, 233229074, 62157279, 109018228),
	(29, 'Cagayan', 13, 18.2489629, 121.8787833, 0, 349303, 4007700),
	(30, 'Switzerland, Geneva', 1, 46.1983922, 6.1422961, 817564, 285732, 9708),
	-- (31, '', 0, 0, 0, 0, 0, 0),
	(32, 'U.S.A., New York', 1, 40.7143528, -74.0059731, 836740, 313638, 10846),
	(33, 'China, Beijing', 1, 39.90403, 116.407526, 639132, 271416, 6459),
	(34, 'Caracas, Venezuela', 1, 10.491016, -66.902061, 355475, 195613, 9712),
	(35, 'France, Paris', 1, 48.856614, 2.3522219, 359049, 94004, 4351),
	(36, 'Saudi Arabia, Jeddah', 1, 21.5433333, 39.1727778, 523303, 128306, 4415),
	(37, 'United Kingdom, London', 1, 51.5112139, -0.1198244, 527660, 299618, 5118),
	(38, 'Japan, Tokyo', 1, 35.6894875, 139.6917064, 810640, 135291, 5468),
	(39, 'U.S.A., Washington, D.C.', 1, 38.9072309, -77.0364641, 534415, 166758, 105572),
	(40, 'Phomn Penh, Cambodia', 1, 11.558831, 104.917445, 151793, 59947, 2887),
	(41, 'U.A.E., Abu Dhabi', 1, 24.4666667, 54.3666667, 355553, 110239, 3064),
	(42, 'Jordan, Amman', 1, 31.9565783, 35.9456951, 217668, 85673, 2504),
	(43, 'U.S.A., Honolulu', 1, 21.3069444, -157.8583333, 273858, 74430, 2712),
	(44, 'Laos, Vientianne', 1, 17.962769, 102.614429, 126340, 49280, 238),
	(45, 'India, New Delhi', 1, 28.635308, 77.22496, 191642, 62762, 1262),
	(46, 'Italy, Rome', 1, 41.8929163, 12.4825199, 428091, 106101, 4593),
	(47, 'Pretoria, South Africa', 1, -25.73134, 28.21837, 197724, 69095, 2738),
	(48, 'Malaysia, Kuala Lumpur', 1, 3.139003, 101.686855, 369246, 90655, 1484),
	(49, 'Japan, Kobe', 1, 34.690083, 135.1955112, 397699, 170649, 7023),
	(50, 'Vatican, Holy See', 1, 41.9036111, 12.4502778, 185090, 103964, 889),
	(51, 'Israel, Tel-Aviv', 1, 32.066158, 34.777819, 305634, 127290, 3739),
	(52, 'C.I.S., Moscow', 1, 55.755826, 37.6173, 297320, 142222, 485),
	(53, 'Argentina, Buenos Aires', 1, -34.6037232, -58.3815931, 176250, 93945, 3605),
	(54, 'Canada, Toronto', 1, 43.653226, -79.3831843, 252788, 101251, 7179),
	(55, 'Hungary, Budapest', 1, 47.497912, 19.040235, 141228, 60371, 3619),
	(56, 'Austria, Vienna', 1, 48.2081743, 16.3738189, 299939, 131398, 4672),
	(57, 'Belgium, Brussels', 1, 50.8503396, 4.3517103, 312937, 111554, 3251),
	(58, 'Brazil, Brasilia', 1, -15.7801482, -47.9291698, 143486, 47083, 3830),
	(59, 'U.S.A., San Francisco', 1, 37.7749295, -122.4194155, 449703, 139299, 4248),
	(60, 'U.S.A., Agana', 1, 13.4708908, 144.7512782, 203316, 70207, 3285),
	(61, 'Qatar, Doha', 1, 25.280282, 51.522476, 292120, 65049, 6776),
	(62, 'Nigeria, Lagos', 1, 6.441158, 3.417977, 206397, 86881, 3900),
	(63, 'Pakistan, Islamabad', 1, 33.718151, 73.060547, 165181, 61816, 5842),
	(64, 'U.S.A., Chicago', 1, 41.8781136, -87.6297982, 283714, 85950, 3415),
	(65, 'Kuwait, Kuwait', 1, 29.36972219999999, 47.9783333, 304322, 81314, 3634),
	(66, 'Mexico, Mexico', 1, 19.4326077, -99.133208, 221791, 81012, 852),
	(67, 'Iraq, Baghdad', 1, 33.325, 44.422, 177418, 55750, 6381),
	(68, 'Singapore, Singapore', 1, 1.2800945, 103.8509491, 351586, 201537, 5380),
	(69, 'Switzerland, Berne', 1, 46.9479222, 7.444608499999999, 261950, 73820, 3678),
	(70, 'New Zealand, Wellington', 1, -41.2864603, 174.776236, 203796, 62472, 3478),
	(71, 'Libya, Tripoli', 1, 32.876174, 13.187507, 194878, 74414, 4281),
	(72, 'Lebanon, Beirut', 1, 33.8886289, 35.4954794, 182151, 81506, 2651),
	(73, '3rd District, Pangasinan, San Carlos City', 12, 15.9280556, 120.3488889, 0, 0, 0),
	(74, 'Chile, Santiago', 1, -33.4691199, -70.641997, 141801, 61346, 3212),
	(75, 'Papua New Guinea, P. Moresby', 1, -10.6264041, 151.0122058, 132348, 39355, 6224),
	(76, 'Hongkong, Hongkong', 1, 22.396428, 114.109497, 542573, 388017, 2275),
	(77, 'Germany, Bonn', 1, 50.73743, 7.0982068, 421906, 131626, 2423),
	(78, 'Canada, Ottawa', 1, 45.4215296, -75.69719309999999, 242402, 105734, 8003),
	(79, 'Korea South, Seoul', 1, 37.566535, 126.9779692, 320121, 148448, 4284),
	(80, 'Bangladesh, Dhaka', 1, 23.709921, 90.40714299999999, 137149, 50074, 0),
	(81, 'Kenya, Nairobi', 1, -1.2920659, 36.8219462, 139495, 71907, 997),
	(82, 'Xiamen, China', 1, 24.479834, 118.089425, 193417, 57122, 3470),
	(83, 'Brunei, Bandar Seri Begawan', 1, 4.8902778, 114.9422222, 236301, 58394, 3456),
	(84, 'Iran, Teheran', 1, 35.696216, 51.422945, 170991, 71657, 2122),
	(85, 'Oman, Muscat', 1, 23.6100000, 58.5400000, 229243, 58654, 6219),
	(86, 'Australia, Sydney', 1, -33.8674869, 151.2069902, 174237, 75068, 3270),
	(87, 'Thailand, Bangkok', 1, 13.7278956, 100.5241235, 261237, 57887, 6710),
	(88, 'Egypt, Cairo', 1, 30.0444196, 31.2357116, 209667, 65568, 444),
	(89, 'Australia, Canberra', 1, -35.2819998, 149.1286843, 243636, 92257, 3193),
	(90, 'Turkey, Ankara', 1, 39.92077, 32.85411, 196273, 94790, 3715),
	(91, 'Republic of Czechoslovakia, Prague', 1, 50.094367, 14.4434423, 133410, 83968, 3422),
	(92, 'Myanmar, Yangon', 1, 16.8000000, 96.1500000, 149178, 49087, 975),
	(93, 'Vietnam, Hanoi', 1, 21.0333333, 105.850000, 156436, 62260, 3540),
	(94, 'Netherlands, The Hague', 1, 52.0704978, 4.3006999, 277052, 68268, 5940),
	(95, 'Canada, Vancouver', 1, 49.261226, -123.1139268, 227385, 104042, 3847),
	(96, 'Greece, Athens', 1, 37.9837155, 23.7293097, 268960, 86915, 2809),
	(97, 'Indonesia, Manado', 1, 1.4917014, 124.842843, 120002, 37525, 3777),
	(98, 'Milan, Italy', 1, 45.4654542, 9.186515999999999, 350043, 109192, 5153),
	(99, 'Guangzhou, China', 1, 23.129163, 113.264435, 215625, 65396, 3640),
	(100, 'U.S.A., Los Angeles', 1, 34.0522342, -118.2436849, 466662, 171260, 3042),
	(101, 'Bahrain, Manama', 1, 26.2166667, 50.5833333, 244542, 64521, 5937),
	(102, 'Spain, Madrid', 1, 40.4167754, -3.7037902, 335262, 86176, 5606),
	(103, 'Sweden, Stockholm', 1, 59.32893000000001, 18.06491, 247567, 93391, 473),
	(104, 'Vietnam, Ho Chi Minh', 1, 10.8230989, 106.6296638, 8587, 0, 0),
	(105, 'Cuba, Havana', 1, 23.1168, -82.38855699999999, 94053, 38566, 3394),
	(106, 'Micronesia, Pohnpei', 1, 6.8541254, 158.2623822, 6818, 0, 0),
	(107, 'Vladivostok, Russia', 1, 43.1666667, 131.9333333, 8081, 0, 0),
	(108, 'Saipan, Saipan', 1, 15.177801, 145.750967, 139512, 40540, 3610),
	(109, 'Republic of Palau, Koror', 1, 7.3410628, 134.4771596, 95504, 24150, 3324),
	(110, 'Germany, Hamburg', 1, 53.5510846, 9.993681799999999, 142028, 52724, 2338),
	(111, 'Romania, Bucharest', 1, 44.4325, 26.1038889, 102658, 50575, 2833)
;;

ALTER TABLE locations
  ADD location_stars int NOT NULL DEFAULT 0,
  ADD location_ratings int NOT NULL DEFAULT 0
;;

# --- !Downs

DROP TABLE IF EXISTS locations;;
