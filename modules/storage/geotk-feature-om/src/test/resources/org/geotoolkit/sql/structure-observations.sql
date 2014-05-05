CREATE TABLE "version" (
    "number"   character varying(10) NOT NULL
);

INSERT INTO "version" VALUES ('1.0.0');
CREATE SCHEMA "om";


CREATE TABLE "om"."observations" (
    "identifier"        character varying(200) NOT NULL,
    "id"                integer NOT NULL,
    "time_begin"        timestamp,
    "time_end"          timestamp,
    "observed_property" character varying(200),
    "procedure"         character varying(200),
    "foi"               character varying(200)
);

CREATE TABLE "om"."mesures" (
    "id_observation"    integer NOT NULL,
    "id"                integer NOT NULL,
    "time"              timestamp,
    "value"             character varying(100),
    "uom"               character varying(20),
    "field_type"        character varying(30),
    "field_name"        character varying(30),
    "field_definition"  character varying(200)
);

CREATE TABLE "om"."offerings" (
    "identifier"       character varying(100) NOT NULL,
    "description"      character varying(200),
    "name"             character varying(200),
    "time_begin"       timestamp,
    "time_end"         timestamp,
    "procedure"        character varying(200)
);

CREATE TABLE "om"."offering_observed_properties" (
    "id_offering" character varying(100) NOT NULL,
    "phenomenon"  character varying(200) NOT NULL
);

CREATE TABLE "om"."offering_foi" (
    "id_offering" character varying(100) NOT NULL,
    "foi"         character varying(200) NOT NULL
);

CREATE TABLE "om"."observed_properties" (
    "id" character varying(200) NOT NULL
);

CREATE TABLE "om"."procedures" (
    "id"     character varying(200) NOT NULL,
    "shape"  varchar (200) for bit data,
    "crs"    integer
);

CREATE TABLE "om"."sampling_features" (
    "id"               character varying(200) NOT NULL,
    "name"             character varying(200),
    "description"      character varying(200),
    "sampledfeature"   character varying(200),
    "shape"            varchar (200) for bit data,
    "crs"              integer
);


-- USED ONLY FOR V100 SOS --

CREATE TABLE "om"."components" (
    "phenomenon" character varying(200) NOT NULL,
    "component"  character varying(200) NOT NULL
);

ALTER TABLE "version" ADD CONSTRAINT version_pk PRIMARY KEY ("number");

ALTER TABLE "om"."observations" ADD CONSTRAINT observation_pk PRIMARY KEY ("id");

ALTER TABLE "om"."mesures" ADD CONSTRAINT mesure_pk PRIMARY KEY ("id_observation", "id");

ALTER TABLE "om"."offerings" ADD CONSTRAINT offering_pk PRIMARY KEY ("identifier");

ALTER TABLE "om"."offering_observed_properties" ADD CONSTRAINT offering_op_pk PRIMARY KEY ("id_offering", "phenomenon");

ALTER TABLE "om"."offering_foi" ADD CONSTRAINT offering_foi_pk PRIMARY KEY ("id_offering", "foi");

ALTER TABLE "om"."observed_properties" ADD CONSTRAINT observed_properties_pk PRIMARY KEY ("id");

ALTER TABLE "om"."procedures" ADD CONSTRAINT procedure_pk PRIMARY KEY ("id");

ALTER TABLE "om"."sampling_features" ADD CONSTRAINT sf_pk PRIMARY KEY ("id");

ALTER TABLE "om"."components" ADD CONSTRAINT components_op_pk PRIMARY KEY ("phenomenon", "component");

ALTER TABLE "om"."observations" ADD CONSTRAINT observation_op_fk FOREIGN KEY ("observed_property") REFERENCES "om"."observed_properties"("id");

ALTER TABLE "om"."observations" ADD CONSTRAINT observation_procedure_fk FOREIGN KEY ("procedure") REFERENCES "om"."procedures"("id");

ALTER TABLE "om"."observations" ADD CONSTRAINT observation_foi_fk FOREIGN KEY ("foi") REFERENCES "om"."sampling_features"("id");

ALTER TABLE "om"."mesures" ADD CONSTRAINT mesure_obs_fk FOREIGN KEY ("id_observation") REFERENCES "om"."observations"("id");

ALTER TABLE "om"."offerings" ADD CONSTRAINT offering_procedure_fk FOREIGN KEY ("procedure") REFERENCES "om"."procedures"("id");

ALTER TABLE "om"."offering_observed_properties" ADD CONSTRAINT offering_op_off_fk FOREIGN KEY ("id_offering") REFERENCES "om"."offerings"("identifier");

ALTER TABLE "om"."offering_observed_properties" ADD CONSTRAINT offering_op_op_fk FOREIGN KEY ("phenomenon") REFERENCES "om"."observed_properties"("id");

ALTER TABLE "om"."offering_foi" ADD CONSTRAINT offering_foi_off_fk FOREIGN KEY ("id_offering") REFERENCES "om"."offerings"("identifier");

ALTER TABLE "om"."offering_foi" ADD CONSTRAINT offering_foi_foi_fk FOREIGN KEY ("foi") REFERENCES "om"."sampling_features"("id");

ALTER TABLE "om"."components" ADD CONSTRAINT component_base_fk FOREIGN KEY ("phenomenon") REFERENCES "om"."observed_properties"("id");

ALTER TABLE "om"."components" ADD CONSTRAINT component_child_fk FOREIGN KEY ("component") REFERENCES "om"."observed_properties"("id");
