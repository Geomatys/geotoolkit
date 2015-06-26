CREATE SCHEMA "µSCHEMANAMEµ";

CREATE TABLE "µSCHEMANAMEµ"."records"(
  "id"          INTEGER NOT NULL,
  "identifier"  VARCHAR(1024) NOT NULL UNIQUE,
  "nbenv"       INTEGER NOT NULL,
  "minx"        DOUBLE PRECISION,
  "maxx"        DOUBLE PRECISION,
  "miny"        DOUBLE PRECISION,
  "maxy"        DOUBLE PRECISION
);

ALTER TABLE "µSCHEMANAMEµ"."records" ADD CONSTRAINT records_pk PRIMARY KEY ("id");


CREATE TABLE "µSCHEMANAMEµ"."tree"(
  "data"      BYTEA NOT NULL
);

ALTER TABLE "µSCHEMANAMEµ"."tree" ADD CONSTRAINT tree_pk PRIMARY KEY ("data");

