
INSERT INTO "observation"."phenomenons" ("id", "name", "description") VALUES ('depth', 'urn:ogc:def:phenomenon:GEOM:depth', 'profondeur');
INSERT INTO "observation"."phenomenons" ("id", "name", "description") VALUES ('temperature', 'urn:ogc:def:phenomenon:GEOM:temperature', 'temperature');

INSERT INTO "observation"."composite_phenomenons" ("id", "name", "description", "dimension") VALUES ('aggregatePhenomenon', 'urn:ogc:def:phenomenon:GEOM:aggregate', NULL, 2);
INSERT INTO "observation"."components" ("composite_phenomenon", "component") VALUES ('aggregatePhenomenon', 'depth');
INSERT INTO "observation"."components" ("composite_phenomenon", "component") VALUES ('aggregatePhenomenon', 'temperature');




INSERT INTO "sos"."observation_offerings" ("id", "name", "srs_name", "description", "event_time_begin", "event_time_end", "bounded_by", "response_format", "response_mode", "result_model_namespace", "result_model_localpart")
VALUES ('offering-allSensor', 'offering-allSensor', 'EPSG:4326', NULL, '2008-05-14 10:40:25.236', NULL, NULL, 'application/xml', NULL, 'http://www.opengis.net/om/1.0', 'Observation');



INSERT INTO "sos"."offering_phenomenons" ("id_offering", "phenomenon", "composite_phenomenon") VALUES ('offering-allSensor', '', 'aggregatePhenomenon');
INSERT INTO "sos"."offering_phenomenons" ("id_offering", "phenomenon", "composite_phenomenon") VALUES ('offering-allSensor', 'depth', '');
INSERT INTO "sos"."offering_phenomenons" ("id_offering", "phenomenon", "composite_phenomenon") VALUES ('offering-allSensor', 'temperature', '');


INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-001');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-002');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-003');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-004');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-007');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-008');
INSERT INTO "sos"."offering_procedures" ("id_offering", "procedure") VALUES ('offering-allSensor', 'reference-009');

INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:1', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:2', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:3', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:4', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:5', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:6', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:7', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:8', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:9', NULL);
INSERT INTO "observation"."process" ("name", "description") VALUES ('urn:ogc:object:sensor:GEOM:10', NULL);

INSERT INTO "sos"."offering_response_modes" ("id_offering", "mode") VALUES ('offering-allSensor', 'RESULT_TEMPLATE');
INSERT INTO "sos"."offering_response_modes" ("id_offering", "mode") VALUES ('offering-allSensor', 'INLINE');

INSERT INTO "sos"."offering_sampling_features" ("id_offering", "sampling_feature") VALUES ('offering-allSensor', 'reference-005');
INSERT INTO "sos"."offering_sampling_features" ("id_offering", "sampling_feature") VALUES ('offering-allSensor', 'reference-006');
INSERT INTO "sos"."offering_sampling_features" ("id_offering", "sampling_feature") VALUES ('offering-allSensor', 'reference-010');

INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-001', '', '', 'urn:ogc:object:sensor:GEOM:1', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-002', '', '', 'urn:ogc:object:sensor:GEOM:2', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-003', '', '', 'urn:ogc:object:sensor:GEOM:3', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-004', '', '', 'urn:ogc:object:sensor:GEOM:4', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-007', '', '', 'urn:ogc:object:sensor:GEOM:5', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-008', '', '', 'urn:ogc:object:sensor:GEOM:7', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-009', '', '', 'urn:ogc:object:sensor:GEOM:8', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-005', '', '', 'station-001', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-006', '', '', 'station-002', '', '', '', '', 0);
INSERT INTO "observation"."references" ("id_reference", "actuate", "arcrole", "href", "role", "show", "title", "type", "owns") VALUES ('reference-010', '', '', 'station-003', '', '', '', '', 0);

INSERT INTO "observation"."Distributions" ("name", "scale", "offset", "log") VALUES ('normale', 1, 0, 0);

INSERT INTO "observation"."sampling_points" ("id", "description", "name", "sampled_feature", "point_id", "point_srsname", "point_srsdimension", "x_value", "y_value") VALUES ('station-001', 'Point d''eau BSSA', '10972X0137-PONT' , 'urn:-sandre:object:bdrhf:124X', 'STATION-LOCALISATION', 'urn:ogc:def:crs:EPSG:27582', 2, 65400, 1731368);
INSERT INTO "observation"."sampling_points" ("id", "description", "name", "sampled_feature", "point_id", "point_srsname", "point_srsdimension", "x_value", "y_value") VALUES ('station-002', 'Point d''eau BSSB', '10972X0137-PLOUF', 'urn:-sandre:object:bdrhf:125X', 'STATION-LOCALISATION', 'urn:ogc:def:crs:EPSG:27582', 2, 65400, 1731368);

INSERT INTO "sos"."envelopes" ("id", "srs_name", "lower_corner_x", "lower_corner_y", "upper_corner_x", "upper_corner_y") VALUES ('bound-1', 'urn:ogc:def:crs:EPSG:6.8:4283', -30.711, 134.196, -30.702, 134.205);

INSERT INTO "observation"."sampling_curves" ("id", "description", "name", "boundedby", "sampled_feature", "length_uom", "length_value", "shape_id", "shape_srsname") VALUES ('station-003', 'Geology traverse', 'cycle1', 'bound-1', NULL, 'm', 750, 'pr1_ls1', 'urn:ogc:def:crs:EPSG:27582');
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.711, 134.205, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.710, 134.204, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.709, 134.203, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.708, 134.201, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.706, 134.196, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.703, 134.197, null);
INSERT INTO "observation"."linestring" ("id", "x", "y" , "z") VALUES ('pr1_ls1',-30.702, 134.199, null);

INSERT INTO "observation"."text_block_encodings" ("id_encoding", "token_separator", "block_separator", "decimal_separator") VALUES ('textblock', ',', '@@', '.');

INSERT INTO "observation"."simple_data_records" ("id_datablock", "id_datarecord", "definition", "fixed") VALUES ('dataArray-0', 'datarecord-0', NULL, 0);
INSERT INTO "observation"."simple_data_records" ("id_datablock", "id_datarecord", "definition", "fixed") VALUES ('dataArray-1', 'datarecord-0', NULL, 0);
INSERT INTO "observation"."simple_data_records" ("id_datablock", "id_datarecord", "definition", "fixed") VALUES ('dataArray-2', 'datarecord-0', NULL, 0);
INSERT INTO "observation"."simple_data_records" ("id_datablock", "id_datarecord", "definition", "fixed") VALUES ('dataArray-3', 'datarecord-1', NULL, 0);
INSERT INTO "observation"."simple_data_records" ("id_datablock", "id_datarecord", "definition", "fixed") VALUES ('dataArray-4', 'datarecord-1', NULL, 0);

INSERT INTO "observation"."data_array_definition" ("id_array_definition", "element_count", "elementType", "encoding") VALUES ('dataArray-0', 0, 'datarecord-0', 'textblock');
INSERT INTO "observation"."data_array_definition" ("id_array_definition", "element_count", "elementType", "encoding") VALUES ('dataArray-1', 0, 'datarecord-0', 'textblock');
INSERT INTO "observation"."data_array_definition" ("id_array_definition", "element_count", "elementType", "encoding") VALUES ('dataArray-2', 5, 'datarecord-0', 'textblock');
INSERT INTO "observation"."data_array_definition" ("id_array_definition", "element_count", "elementType", "encoding") VALUES ('dataArray-3', 0, 'datarecord-1', 'textblock');
INSERT INTO "observation"."data_array_definition" ("id_array_definition", "element_count", "elementType", "encoding") VALUES ('dataArray-4', 5, 'datarecord-1', 'textblock');


INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-0', 'datarecord-0', 'Time', 'urn:ogc:data:time:iso8601', 'Time', NULL, NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-0', 'datarecord-0', 'depth', 'urn:x-ogc:def:phenonmenon:GEOM:depth', 'Quantity', 'm', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-1', 'datarecord-0', 'Time', 'urn:ogc:data:time:iso8601', 'Time', NULL, NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-1', 'datarecord-0', 'depth', 'urn:x-ogc:def:phenonmenon:GEOM:depth', 'Quantity', 'm', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-2', 'datarecord-0', 'Time', 'urn:ogc:data:time:iso8601', 'Time', NULL, NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-2', 'datarecord-0', 'depth', 'urn:x-ogc:def:phenonmenon:GEOM:depth', 'Quantity', 'm', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-3', 'datarecord-1', 'Time', 'urn:ogc:data:time:iso8601', 'Time', NULL, NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-3', 'datarecord-1', 'depth', 'urn:x-ogc:def:phenonmenon:GEOM:depth', 'Quantity', 'm', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-3', 'datarecord-1', 'temperature', 'urn:x-ogc:def:phenonmenon:GEOM:temperature', 'Quantity', '°C', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-4', 'datarecord-1', 'Time', 'urn:ogc:data:time:iso8601', 'Time', NULL, NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-4', 'datarecord-1', 'depth', 'urn:x-ogc:def:phenonmenon:GEOM:depth', 'Quantity', 'm', NULL, NULL);
INSERT INTO "observation"."any_scalars" ("id_datablock", "id_datarecord", "name", "definition", "type", "uom_code", "uom_href", "value") VALUES ('dataArray-4', 'datarecord-1', 'temperature', 'urn:x-ogc:def:phenonmenon:GEOM:temperature', 'Quantity', '°C', NULL, NULL);

INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (1, NULL, '', 'dataArray-0');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (2, NULL, '', 'dataArray-1');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (9, NULL, '', 'dataArray-3');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (3, NULL, '2007-05-01T12:59:00,6.560@@2007-05-01T13:59:00,6.560@@2007-05-01T14:59:00,6.560@@2007-05-01T15:59:00,6.560@@2007-05-01T16:59:00,6.560@@', 'dataArray-2');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (5, NULL, '2007-05-01T02:59:00,6.560@@2007-05-01T03:59:00,6.560@@2007-05-01T04:59:00,6.560@@2007-05-01T05:59:00,6.560@@2007-05-01T06:59:00,6.560@@', 'dataArray-2');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (6, NULL, '2007-05-01T07:59:00,6.560@@2007-05-01T08:59:00,6.560@@2007-05-01T09:59:00,6.560@@2007-05-01T10:59:00,6.560@@2007-05-01T11:59:00,6.560@@', 'dataArray-2');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (7, NULL, '2007-05-01T17:59:00,6.560@@2007-05-01T18:59:00,6.550@@2007-05-01T19:59:00,6.550@@2007-05-01T20:59:00,6.550@@2007-05-01T21:59:00,6.550@@', 'dataArray-2');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (8, NULL, '2007-05-01T12:59:00,6.560@@2007-05-01T13:59:00,6.560@@2007-05-01T14:59:00,6.560@@2007-05-01T15:59:00,6.560@@2007-05-01T16:59:00,6.560@@', 'dataArray-2');
INSERT INTO "observation"."any_results" ("id_result", "reference", "values", "definition") VALUES (10, NULL, '2007-05-01T12:59:00,6.560@@2007-05-01T13:59:00,6.560@@2007-05-01T14:59:00,6.560@@2007-05-01T15:59:00,6.560@@2007-05-01T16:59:00,6.560@@', 'dataArray-4');

INSERT INTO "observation"."unit_of_measures" ("id", "name", "quantity_type", "unit_system") VALUES ('degrees', 'degree celcius', NULL, '°C');
INSERT INTO "observation"."measures" ("name", "uom", "value") VALUES ('measure-001', 'degrees', NULL);

INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:template:GEOM:3', NULL, NULL, 'urn:ogc:object:sensor:GEOM:3', NULL, NULL, NULL, 'depth', 1, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:template:GEOM:4', NULL, NULL, 'urn:ogc:object:sensor:GEOM:4', NULL, NULL, NULL, 'depth', 2, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:template:GEOM:5', NULL, NULL, 'urn:ogc:object:sensor:GEOM:5', NULL, NULL, NULL, NULL, 2, 'normale', 'station-001', 'aggregatePhenomenon', NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:template:GEOM:8', NULL, NULL, 'urn:ogc:object:sensor:GEOM:8', NULL, NULL, NULL, NULL, 9, 'normale', NULL, 'aggregatePhenomenon', 'station-003');
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:406', NULL, NULL, 'urn:ogc:object:sensor:GEOM:4', '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', NULL, 'depth', 3, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:304', NULL, NULL, 'urn:ogc:object:sensor:GEOM:3', '2007-05-01 02:59:00.0', '2007-05-01 06:59:00.0', NULL, 'depth', 5, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:305', NULL, NULL, 'urn:ogc:object:sensor:GEOM:3', '2007-05-01 07:59:00.0', '2007-05-01 11:59:00.0', NULL, 'depth', 6, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:307', NULL, NULL, 'urn:ogc:object:sensor:GEOM:3', '2007-05-01 17:59:00.0', '2007-05-01 21:59:00.0', NULL, 'depth', 7, 'normale', 'station-001', NULL, NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:507', NULL, NULL, 'urn:ogc:object:sensor:GEOM:5', '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', NULL, NULL, 8, 'normale', 'station-002', 'aggregatePhenomenon', NULL);
INSERT INTO "observation"."observations" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:GEOM:801', NULL, NULL, 'urn:ogc:object:sensor:GEOM:8', '2007-05-01 12:59:00.0', '2007-05-01 16:59:00.0', NULL, NULL, 10, 'normale', NULL, 'aggregatePhenomenon', 'station-003');

INSERT INTO "observation"."measurements" ("name", "description", "feature_of_interest", "procedure", "sampling_time_begin", "sampling_time_end", "result_definition", "observed_property", "result", "distribution", "feature_of_interest_point", "observed_property_composite", "feature_of_interest_curve") VALUES ('urn:ogc:object:observation:template:GEOM:7', NULL, NULL, 'urn:ogc:object:sensor:GEOM:7', NULL, NULL, NULL, 'temperature', 'measure-001', 'normale', 'station-002', NULL,NULL);