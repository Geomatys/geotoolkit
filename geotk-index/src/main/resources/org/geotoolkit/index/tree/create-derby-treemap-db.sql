CREATE SCHEMA "treemap";

CREATE TABLE "treemap"."records"(
  "id"          INTEGER NOT NULL,
  "identifier"  VARCHAR(1024) NOT NULL UNIQUE,
  "nbenv"       INTEGER NOT NULL,
  "minx"        DOUBLE,
  "maxx"        DOUBLE,
  "miny"        DOUBLE,
  "maxy"        DOUBLE
);

ALTER TABLE "treemap"."records" ADD CONSTRAINT records_pk PRIMARY KEY ("id");

