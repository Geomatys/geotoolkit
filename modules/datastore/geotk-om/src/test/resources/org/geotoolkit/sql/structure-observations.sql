CREATE SCHEMA "sos";
CREATE SCHEMA "observation";




CREATE TABLE "observation"."Distributions" (
    "name"   character varying(40) NOT NULL,
    "scale"  real,
    "offset" real,
    "log"    smallint
);

CREATE TABLE "observation"."any_results" (
    "id_result"  integer NOT NULL,
    "reference"  character varying(40),
    "values"     character varying(300),
    "definition" character varying(40)
);




CREATE TABLE "observation"."any_scalars" (
    "id_datablock"  character varying(40) NOT NULL,
    "id_datarecord" character varying(40) NOT NULL,
    "name"          character varying(40) NOT NULL,
    "definition"    character varying(50),
    "type"          character varying(40),
    "uom_code"      character varying(40),
    "uom_href"      character varying(40),
    "value"         smallint
);



CREATE TABLE "observation"."components" (
    "composite_phenomenon" character varying(40) NOT NULL,
    "component"            character varying(40) NOT NULL
);




CREATE TABLE "observation"."composite_phenomenons" (
    "id"          character varying(40) NOT NULL,
    "name"        character varying(40),
    "description" character varying(50),
    "dimension"   integer
);




CREATE TABLE "observation"."data_array_definition" (
    "id_array_definition" character varying(40) NOT NULL,
    "element_count"       smallint NOT NULL,
    "elementType"         character varying(40),
    "encoding"            character varying(40)
);




CREATE TABLE "observation"."data_block_definitions" (
    "id"       character varying(40) NOT NULL,
    "encoding" character varying(40)
);




CREATE TABLE "observation"."measurements" (
    "name"                        character varying(50) NOT NULL,
    "description"                 character varying(50),
    "feature_of_interest"         character varying(40),
    "procedure"                   character varying(40),
    "sampling_time_begin"         timestamp,
    "sampling_time_end"           timestamp,
    "result_definition"           character varying(40),
    "observed_property"           character varying(40),
    "result"                      character varying(40),
    "distribution"                character varying(40),
    "feature_of_interest_point"   character varying(40),
    "observed_property_composite" character varying(40),
    "feature_of_interest_curve"   character varying(40)
);




CREATE TABLE "observation"."measures" (
    "name"  character varying(40) NOT NULL,
    "uom"   character varying(40),
    "value" real
);


CREATE TABLE "observation"."observations" (
    "name"                        character varying(50) NOT NULL,
    "description"                 character varying(50),
    "feature_of_interest"         character varying(40),
    "procedure"                   character varying(40),
    "sampling_time_begin"         timestamp,
    "sampling_time_end"           timestamp,
    "result_definition"           character varying(40),
    "observed_property"           character varying(40),
    "result"                      integer,
    "distribution"                character varying(40),
    "feature_of_interest_point"   character varying(40),
    "observed_property_composite" character varying(40),
    "feature_of_interest_curve"   character varying(40)
);



CREATE TABLE "observation"."phenomenons" (
    "id"          character varying(40) NOT NULL,
    "name"        character varying(40),
    "description" character varying(40)
);



CREATE TABLE "observation"."process" (
    "name"        character varying(40) NOT NULL,
    "description" character varying(40)
);



CREATE TABLE "observation"."references" (
    "id_reference" character varying(40) NOT NULL,
    "actuate"      character varying(40),
    "arcrole"      character varying(40),
    "href"         character varying(40),
    "role"         character varying(40),
    "show"         character varying(40),
    "title"        character varying(40),
    "type"         character varying(40),
    "owns"         smallint
);




CREATE TABLE "observation"."sampling_features" (
    "id"              character varying(40) NOT NULL,
    "description"     character varying(40),
    "name"            character varying(40),
    "sampled_feature" character varying(40)
);




CREATE TABLE "observation"."sampling_points" (
    "id"                 character varying(40) NOT NULL,
    "description"        character varying(50),
    "name"               character varying(40),
    "sampled_feature"    character varying(40),
    "point_id"           character varying(40),
    "point_srsname"      character varying(40),
    "point_srsdimension" integer,
    "x_value"            double precision,
    "y_value"            double precision
);

CREATE TABLE "observation"."sampling_curves" (
    "id"                 character varying(40) NOT NULL,
    "description"        character varying(50),
    "name"               character varying(40),
    "boundedby"          character varying(40),
    "sampled_feature"    character varying(40),
    "length_uom"         character varying(40),
    "length_value"       double precision,
    "shape_id"           character varying(40),
    "shape_srsname"      character varying(40)
);

CREATE TABLE "observation"."linestring" (
    "id"                 character varying(40) NOT NULL,
    "x"                  double precision,
    "y"                  double precision,
    "z"                  double precision
);


CREATE TABLE "observation"."simple_data_records" (
    "id_datablock"  character varying(40) NOT NULL,
    "id_datarecord" character varying(40) NOT NULL,
    "definition"    character varying(40),
    "fixed"         smallint
);




CREATE TABLE "observation"."text_block_encodings" (
    "id_encoding"       character varying(40) NOT NULL,
    "token_separator"   character varying(3),
    "block_separator"   character varying(3),
    "decimal_separator" char
);



CREATE TABLE "observation"."unit_of_measures" (
    "id"            character varying(40) NOT NULL,
    "name"          character varying(40),
    "quantity_type" character varying(40),
    "unit_system"   character varying(40)
);



CREATE TABLE "sos"."envelopes" (
    "id"             character varying(40) NOT NULL,
    "srs_name"       character varying(40),
    "lower_corner_x" double precision,
    "lower_corner_y" double precision,
    "upper_corner_x" double precision,
    "upper_corner_y" double precision
);




CREATE TABLE "sos"."geographic_localisations" (
    "id"       character varying(40) NOT NULL,
    "the_geom" character varying(40)
);


CREATE TABLE "sos"."observation_offerings" (
    "id"                     character varying(40) NOT NULL,
    "name"                   character varying(40),
    "srs_name"               character varying(40),
    "description"            character varying(40),
    "event_time_begin"       timestamp,
    "event_time_end"         timestamp,
    "bounded_by"             character varying(40),
    "response_format"        character varying(40),
    "response_mode"          character varying(40),
    "result_model_namespace" character varying(40),
    "result_model_localpart" character varying(40)
);



CREATE TABLE "sos"."offering_phenomenons" (
    "id_offering"          character varying(40) NOT NULL,
    "phenomenon"           character varying(40) NOT NULL,
    "composite_phenomenon" character varying(40) NOT NULL
);




CREATE TABLE "sos"."offering_procedures" (
    "id_offering" character varying(40) NOT NULL,
    "procedure"   character varying(40) NOT NULL
);




CREATE TABLE "sos"."offering_response_modes" (
    "id_offering" character varying(40) NOT NULL,
    "mode"        character varying(40) NOT NULL
);




CREATE TABLE "sos"."offering_sampling_features" (
    "id_offering"      character varying(40) NOT NULL,
    "sampling_feature" character varying(40) NOT NULL
);




CREATE TABLE "sos"."projected_localisations" (
    "id"       character varying(40) NOT NULL,
    "the_geom" character varying(40)
);

ALTER TABLE "observation"."any_results" ADD CONSTRAINT any_pkey PRIMARY KEY ("id_result");

ALTER TABLE "observation"."components" ADD CONSTRAINT composite_phenomenons_pk PRIMARY KEY ("composite_phenomenon", "component");

ALTER TABLE "observation"."composite_phenomenons" ADD CONSTRAINT composite_phenomenons_pkey PRIMARY KEY ("id");

ALTER TABLE "observation"."data_array_definition" ADD CONSTRAINT data_array_pk PRIMARY KEY ("id_array_definition");

ALTER TABLE "observation"."data_block_definitions" ADD CONSTRAINT data_block_definitions_pkey PRIMARY KEY ("id");

ALTER TABLE "observation"."any_scalars" ADD CONSTRAINT data_record_fields_pkey PRIMARY KEY ("id_datarecord", "name", "id_datablock");

ALTER TABLE "observation"."Distributions" ADD CONSTRAINT distributions_pkey PRIMARY KEY ("name");

ALTER TABLE "observation"."measurements" ADD CONSTRAINT measurements_pkey PRIMARY KEY ("name");

ALTER TABLE "observation"."measures" ADD CONSTRAINT measures_pkey PRIMARY KEY ("name");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_pkey PRIMARY KEY ("name");

ALTER TABLE "observation"."phenomenons" ADD CONSTRAINT phenomenons_pk PRIMARY KEY ("id");

ALTER TABLE "observation"."process" ADD CONSTRAINT process_pkey PRIMARY KEY ("name");

ALTER TABLE "observation"."references" ADD CONSTRAINT references_pkey PRIMARY KEY ("id_reference");

ALTER TABLE "observation"."sampling_features" ADD CONSTRAINT sampling_features_pk PRIMARY KEY ("id");

ALTER TABLE "observation"."sampling_points" ADD CONSTRAINT sampling_points_pk PRIMARY KEY ("id");

ALTER TABLE "observation"."sampling_curves" ADD CONSTRAINT sampling_curves_pk PRIMARY KEY ("id");



ALTER TABLE "observation"."simple_data_records" ADD CONSTRAINT simple_data_record_pkey PRIMARY KEY ("id_datablock", "id_datarecord");

ALTER TABLE "observation"."text_block_encodings" ADD CONSTRAINT text_block_encoding_pkey PRIMARY KEY ("id_encoding");

ALTER TABLE "observation"."unit_of_measures"  ADD CONSTRAINT unit_of_measures_pkey PRIMARY KEY ("id");

ALTER TABLE "sos"."envelopes"  ADD CONSTRAINT envelopes_pkey PRIMARY KEY ("id");

ALTER TABLE "sos"."geographic_localisations" ADD CONSTRAINT geographic_pk PRIMARY KEY ("id");

ALTER TABLE "sos"."observation_offerings" ADD CONSTRAINT observation_offerings_pkey PRIMARY KEY ("id");

ALTER TABLE "sos"."offering_procedures" ADD CONSTRAINT offering_procedures_pkey PRIMARY KEY ("id_offering", "procedure");

ALTER TABLE "sos"."offering_phenomenons" ADD CONSTRAINT offering_phenomenons_pk PRIMARY KEY("id_offering", "phenomenon", "composite_phenomenon");

ALTER TABLE "sos"."offering_response_modes" ADD CONSTRAINT offering_response_modes_pkey PRIMARY KEY ("id_offering", "mode");

ALTER TABLE "sos"."offering_sampling_features" ADD CONSTRAINT offering_sampling_feature PRIMARY KEY ("id_offering", "sampling_feature");

ALTER TABLE "sos"."projected_localisations" ADD CONSTRAINT projected_pk PRIMARY KEY ("id");

ALTER TABLE "observation"."components" ADD CONSTRAINT components_component_fkey FOREIGN KEY ("component") REFERENCES "observation"."phenomenons"("id");

ALTER TABLE "observation"."components" ADD CONSTRAINT components_composite_phenomenon_fkey FOREIGN KEY ("composite_phenomenon") REFERENCES "observation"."composite_phenomenons"("id");

ALTER TABLE "observation"."data_array_definition" ADD CONSTRAINT data_array_encoding_fk FOREIGN KEY ("encoding") REFERENCES "observation"."text_block_encodings"("id_encoding");

ALTER TABLE "observation"."data_block_definitions" ADD CONSTRAINT data_block_definitions_encoding_fkey FOREIGN KEY ("encoding") REFERENCES "observation"."text_block_encodings"("id_encoding");

ALTER TABLE "observation"."any_scalars" ADD CONSTRAINT data_record_fields_id_datablock_fkey FOREIGN KEY ("id_datablock", "id_datarecord") REFERENCES "observation"."simple_data_records"("id_datablock", "id_datarecord");

ALTER TABLE "observation"."measurements" ADD CONSTRAINT measurements_feature_of_interest_fkey FOREIGN KEY ("feature_of_interest") REFERENCES "observation"."sampling_features"("id");

ALTER TABLE "observation"."measurements" ADD CONSTRAINT measurements_observed_property_fkey FOREIGN KEY ("observed_property") REFERENCES "observation"."phenomenons"("id");

ALTER TABLE "observation"."measurements" ADD CONSTRAINT measurements_procedure_fkey FOREIGN KEY ("procedure") REFERENCES "observation"."process"("name");

ALTER TABLE "observation"."measurements" ADD CONSTRAINT measurements_result_fkey FOREIGN KEY ("result") REFERENCES "observation"."measures"("name");

ALTER TABLE "observation"."measures" ADD CONSTRAINT measures_uom_fkey FOREIGN KEY ("uom") REFERENCES "observation"."unit_of_measures"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_distribution_fkey FOREIGN KEY ("distribution") REFERENCES "observation"."Distributions"("name");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_feature_of_interest_fkey FOREIGN KEY ("feature_of_interest") REFERENCES "observation"."sampling_features"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_feature_of_interest_point_fkey FOREIGN KEY ("feature_of_interest_point") REFERENCES "observation"."sampling_points"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_observed_property_composite_fkey FOREIGN KEY ("observed_property_composite") REFERENCES "observation"."composite_phenomenons"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_observed_property_fkey FOREIGN KEY ("observed_property") REFERENCES "observation"."phenomenons"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_procedure_fkey FOREIGN KEY ("procedure") REFERENCES "observation"."process"("name");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_result_definition_fkey FOREIGN KEY ("result_definition") REFERENCES "observation"."data_block_definitions"("id");

ALTER TABLE "observation"."observations" ADD CONSTRAINT observations_result_fkey FOREIGN KEY ("result") REFERENCES "observation"."any_results"("id_result");

ALTER TABLE "observation"."any_results" ADD CONSTRAINT reference_pk FOREIGN KEY ("reference") REFERENCES "observation"."references"("id_reference");

ALTER TABLE "sos"."observation_offerings" ADD CONSTRAINT observation_offerings_bounded_by_fkey FOREIGN KEY ("bounded_by") REFERENCES "sos"."envelopes"("id");

ALTER TABLE "sos"."offering_phenomenons" ADD CONSTRAINT offering_phenomenons_composite_phenomenon_fkey FOREIGN KEY ("composite_phenomenon") REFERENCES "observation"."composite_phenomenons"("id");

ALTER TABLE "sos"."offering_phenomenons" ADD CONSTRAINT offering_phenomenons_id_offering_fkey FOREIGN KEY ("id_offering") REFERENCES "sos"."observation_offerings"("id");

ALTER TABLE "sos"."offering_phenomenons" ADD CONSTRAINT offering_phenomenons_phenomenon_fkey FOREIGN KEY ("phenomenon") REFERENCES "observation"."phenomenons"("id");

ALTER TABLE "sos"."offering_procedures" ADD CONSTRAINT offering_procedures_id_offering_fkey FOREIGN KEY ("id_offering") REFERENCES "sos"."observation_offerings"("id");

ALTER TABLE "sos"."offering_sampling_features" ADD CONSTRAINT offering_sampling_features_id_offering_fkey FOREIGN KEY ("id_offering") REFERENCES "sos"."observation_offerings"("id");

INSERT INTO "observation"."phenomenons" VALUES ('', '', 'phenomenon null');
INSERT INTO "observation"."composite_phenomenons" VALUES ('', '', 'composite phenomenon null', 0);