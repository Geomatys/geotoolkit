CREATE TABLE "Layer"(
  "id" serial NOT NULL,
  "name" character varying(1000) NOT NULL,
  CONSTRAINT layer_pk PRIMARY KEY (id),
  CONSTRAINT layer_unique_name UNIQUE (name)
);

CREATE TABLE "Band"(
  "id" serial NOT NULL,
  "layerId" integer NOT NULL,
  "indice" integer NOT NULL,
  "dataType" integer NOT NULL,
  CONSTRAINT band_pk PRIMARY KEY (id),
  CONSTRAINT band_fk_layer FOREIGN KEY ("layerId")
      REFERENCES "Layer" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT band_unique_indice UNIQUE ("layerId","indice")
);

CREATE TABLE "Pyramid"(
  "id" serial NOT NULL,
  "layerId" integer NOT NULL,
  "epsg" character varying(500) NOT NULL,
  CONSTRAINT pyramid_pk PRIMARY KEY (id),
  CONSTRAINT pyramid_fk_layer FOREIGN KEY ("layerId")
      REFERENCES "Layer" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "PyramidProperty"(
  "id" serial NOT NULL,
  "pyramidId" integer NOT NULL,
  "key" character varying(200) NOT NULL,
  "type" character varying(30) NOT NULL,
  "value" character varying(2000),
  CONSTRAINT pyramidproperty_pk PRIMARY KEY (id),
  CONSTRAINT pyramidproperty_fk_pyramid FOREIGN KEY ("pyramidId")
      REFERENCES "Pyramid" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "Mosaic"(
  "id" bigserial NOT NULL,
  "pyramidId" integer NOT NULL,
  "upperCornerX" double precision NOT NULL,
  "upperCornerY" double precision NOT NULL,
  "gridWidth" integer NOT NULL,
  "gridHeight" integer NOT NULL,
  "scale" double precision NOT NULL,
  "tileWidth" integer NOT NULL,
  "tileHeight" integer NOT NULL,
  CONSTRAINT mosaic_pk PRIMARY KEY (id),
  CONSTRAINT mosaic_fk_pyramid FOREIGN KEY ("pyramidId")
      REFERENCES "Pyramid" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "MosaicAxis"(
  "id" bigserial NOT NULL,
  "mosaicId" bigint NOT NULL,
  "indice" integer NOT NULL,
  "value" double precision NOT NULL,
  CONSTRAINT mosaicaxis_pk PRIMARY KEY ("id"),
  CONSTRAINT mosaicaxis_fk_mosaic FOREIGN KEY ("mosaicId")
      REFERENCES "Mosaic" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT mosaicaxis_unique_indice UNIQUE ("mosaicId","indice")
);

CREATE TABLE "Tile"(
  "id" bigserial NOT NULL,
  "mosaicId" bigint NOT NULL,
  "positionX" integer NOT NULL,
  "positionY" integer NOT NULL,
  "raster" raster NOT NULL,
  CONSTRAINT tile_pk PRIMARY KEY ("id"),
  CONSTRAINT tile_fk_mosaic FOREIGN KEY ("mosaicId")
      REFERENCES "Mosaic" (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT tile_unique_position UNIQUE ("mosaicId","positionX","positionY")
);

CREATE SEQUENCE epsg.extension_code_sequence
  INCREMENT 1
  MINVALUE 32768
  MAXVALUE 60000000
  START 32768
  CACHE 1;