INSERT INTO "om"."observed_properties" VALUES ('urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."observed_properties" VALUES ('urn:ogc:def:phenomenon:GEOM:temperature');
INSERT INTO "om"."observed_properties" VALUES ('urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon');

-- v100 --
INSERT INTO "om"."components" ("phenomenon", "component") VALUES ('urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."components" ("phenomenon", "component") VALUES ('urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon', 'urn:ogc:def:phenomenon:GEOM:temperature');

---------

INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:1',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:2',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:3',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:4',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:5',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:6',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:7',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:8',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:9',  x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."procedures" VALUES ('urn:ogc:object:sensor:GEOM:10', x'000000000140efef0000000000413a6b2800000000', 27582);

INSERT INTO "om"."sampling_features" VALUES ('station-001', '10972X0137-PONT' , 'Point d''eau BSSS', 'urn:-sandre:object:bdrhf:123X', x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."sampling_features" VALUES ('station-002', '10972X0137-PLOUF', 'Point d''eau BSSS', 'urn:-sandre:object:bdrhf:123X', x'000000000140efef0000000000413a6b2800000000', 27582);
INSERT INTO "om"."sampling_features" VALUES ('station-003', '66685X4587-WARP',  'Station Thermale',  'urn:-sandre:object:bdrhf:123X', x'000000000140f1490000000000413cdd4b00000000', 27582);
INSERT INTO "om"."sampling_features" VALUES ('station-004', '99917X9856-FRAG',  'Puits',             'urn:-sandre:object:bdrhf:123X', x'000000000140e47b20000000004143979980000000', 27582);
INSERT INTO "om"."sampling_features" VALUES ('station-005', '44499X4517-TRUC',  'bouée ds le rhone', 'urn:-sandre:object:bdrhf:123X', x'000000000140ee8480000000004138cc9400000000', 27582);
INSERT INTO "om"."sampling_features" VALUES ('station-006', 'cycle1',           'Geology traverse',   NULL,                           x'000000000200000007c03eb604189374bc4060c68f5c28f5c3c03eb5c28f5c28f64060c6872b020c4ac03eb5810624dd2f4060c67ef9db22d1c03eb53f7ced91684060c66e978d4fdfc03eb4bc6a7ef9db4060c645a1cac083c03eb3f7ced916874060c64dd2f1a9fcc03eb3b645a1cac14060c65e353f7cee', 27582);


INSERT INTO "om"."offerings" VALUES ('offering-1',  NULL, 'offering-1',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:1');
INSERT INTO "om"."offerings" VALUES ('offering-2',  NULL, 'offering-2',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:2');
INSERT INTO "om"."offerings" VALUES ('offering-3',  NULL, 'offering-3',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:3');
INSERT INTO "om"."offerings" VALUES ('offering-4',  NULL, 'offering-4',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:4');
INSERT INTO "om"."offerings" VALUES ('offering-5',  NULL, 'offering-5',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:5');
INSERT INTO "om"."offerings" VALUES ('offering-6',  NULL, 'offering-6',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:6');
INSERT INTO "om"."offerings" VALUES ('offering-7',  NULL, 'offering-7',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:7');
INSERT INTO "om"."offerings" VALUES ('offering-8',  NULL, 'offering-8',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:8');
INSERT INTO "om"."offerings" VALUES ('offering-9',  NULL, 'offering-9',  NULL, NULL, 'urn:ogc:object:sensor:GEOM:9');
INSERT INTO "om"."offerings" VALUES ('offering-10', NULL, 'offering-10', NULL, NULL, 'urn:ogc:object:sensor:GEOM:10');

INSERT INTO "om"."offering_observed_properties" VALUES ('offering-3','urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-3','urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-3','urn:ogc:def:phenomenon:GEOM:temperature');

INSERT INTO "om"."offering_observed_properties" VALUES ('offering-4','urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-4','urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-4','urn:ogc:def:phenomenon:GEOM:temperature');

INSERT INTO "om"."offering_observed_properties" VALUES ('offering-5','urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-5','urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-5','urn:ogc:def:phenomenon:GEOM:temperature');

INSERT INTO "om"."offering_observed_properties" VALUES ('offering-8','urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-8','urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."offering_observed_properties" VALUES ('offering-8','urn:ogc:def:phenomenon:GEOM:temperature');

INSERT INTO "om"."offering_foi" VALUES ('offering-3','station-001');
INSERT INTO "om"."offering_foi" VALUES ('offering-4','station-001');
INSERT INTO "om"."offering_foi" VALUES ('offering-5','station-002');
INSERT INTO "om"."offering_foi" VALUES ('offering-8','station-006');

INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:406', 406, '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', 'urn:ogc:def:phenomenon:GEOM:depth',               'urn:ogc:object:sensor:GEOM:4', 'station-001');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:304', 304, '2007-05-01 02:59:00.0', '2007-05-01 06:59:00.0', 'urn:ogc:def:phenomenon:GEOM:depth',               'urn:ogc:object:sensor:GEOM:3', 'station-001');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:305', 305, '2007-05-01 07:59:00.0', '2007-05-01 11:59:00.0', 'urn:ogc:def:phenomenon:GEOM:depth',               'urn:ogc:object:sensor:GEOM:3', 'station-001');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:307', 307, '2007-05-01 17:59:00.0', '2007-05-01 21:59:00.0', 'urn:ogc:def:phenomenon:GEOM:depth',               'urn:ogc:object:sensor:GEOM:3', 'station-001');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:507', 507, '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', 'urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon', 'urn:ogc:object:sensor:GEOM:5', 'station-002');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:801', 801, '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', 'urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon', 'urn:ogc:object:sensor:GEOM:8', 'station-006');
INSERT INTO "om"."observations"  VALUES ('urn:ogc:object:observation:GEOM:802', 802,  NULL,                   NULL,                    'urn:ogc:def:phenomenon:GEOM:temperature',         'urn:ogc:object:sensor:GEOM:7', 'station-002');

INSERT INTO "om"."mesures" VALUES (406, 1, '2007-05-01 12:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (406, 2, '2007-05-01 13:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (406, 3, '2007-05-01 14:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (406, 4, '2007-05-01 15:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (406, 5, '2007-05-01 16:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');

INSERT INTO "om"."mesures" VALUES (304, 1, '2007-05-01 02:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (304, 2, '2007-05-01 03:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (304, 3, '2007-05-01 04:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (304, 4, '2007-05-01 05:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (304, 5, '2007-05-01 06:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');

INSERT INTO "om"."mesures" VALUES (305, 1, '2007-05-01 07:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (305, 2, '2007-05-01 08:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (305, 3, '2007-05-01 09:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (305, 4, '2007-05-01 10:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (305, 5, '2007-05-01 11:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');

INSERT INTO "om"."mesures" VALUES (307, 1, '2007-05-01 17:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (307, 2, '2007-05-01 18:59:00','6.55', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (307, 3, '2007-05-01 19:59:00','6.55', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (307, 4, '2007-05-01 20:59:00','6.55', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (307, 5, '2007-05-01 21:59:00','6.55', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');

INSERT INTO "om"."mesures" VALUES (507, 1, '2007-05-01 12:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (507, 2, '2007-05-01 13:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (507, 3, '2007-05-01 14:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (507, 4, '2007-05-01 15:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (507, 5, '2007-05-01 16:59:00','6.56', 'm', 'Quantity', 'depth', 'urn:ogc:def:phenomenon:GEOM:depth');

INSERT INTO "om"."mesures" VALUES (801, 1,  '2007-05-01 12:59:00','6.56', 'm',  'Quantity', 'depth',       'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (801, 2,  '2007-05-01 12:59:00','12.0', '°C', 'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');
INSERT INTO "om"."mesures" VALUES (801, 3,  '2007-05-01 13:59:00','6.56', 'm',  'Quantity', 'depth',       'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (801, 4,  '2007-05-01 13:59:00','13.0', '°C', 'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');
INSERT INTO "om"."mesures" VALUES (801, 5,  '2007-05-01 14:59:00','6.56', 'm',  'Quantity', 'depth',       'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (801, 6,  '2007-05-01 14:59:00','14.0', '°C', 'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');
INSERT INTO "om"."mesures" VALUES (801, 7,  '2007-05-01 15:59:00','6.56', 'm',  'Quantity', 'depth',       'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (801, 8,  '2007-05-01 15:59:00','15.0', '°C', 'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');
INSERT INTO "om"."mesures" VALUES (801, 9,  '2007-05-01 16:59:00','6.56', 'm',  'Quantity', 'depth',       'urn:ogc:def:phenomenon:GEOM:depth');
INSERT INTO "om"."mesures" VALUES (801, 10, '2007-05-01 16:59:00','16.0', '°C', 'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');

INSERT INTO "om"."mesures" VALUES (802, 1,  '2007-05-01 16:59:00','6.56', '°C',  'Quantity', 'temperature', 'urn:ogc:def:phenomenon:GEOM:temperature');
