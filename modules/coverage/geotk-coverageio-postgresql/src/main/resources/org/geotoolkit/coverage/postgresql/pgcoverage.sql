CREATE TABLE pgcoverage."Layer"
(
  "layerId" character varying NOT NULL,
  CONSTRAINT "Layer_pkey" PRIMARY KEY ("layerId")
);

CREATE TABLE pgcoverage."Band"
(
  name character varying,
  datatype character varying,
  layer character varying, -- pointer on layer table
  index character varying NOT NULL,
  CONSTRAINT "Band_pkey" PRIMARY KEY (index),
  CONSTRAINT band_layer FOREIGN KEY (layer)
      REFERENCES pgcoverage."Layer" ("layerId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on layer table
);

CREATE TABLE pgcoverage."Pyramid"
(
  "pyramidId" character varying NOT NULL,
  layer character varying, -- pointer on layer table
  origin boolean,
  CONSTRAINT "Pyramid_pkey" PRIMARY KEY ("pyramidId"),
  CONSTRAINT pyramid_layer FOREIGN KEY (layer)
      REFERENCES pgcoverage."Layer" ("layerId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on layer table
);

CREATE TABLE pgcoverage."Mosaic"
(
  "mosaicId" character varying NOT NULL,
  pyramid character varying, -- pointer on pyramid table
  "upperCornerX" integer, -- upper corner X coordinate
  "upperCornerY" integer, -- upper corner Y coordinate
  width integer, -- mosaic width
  height integer, -- mosaic height
  level double precision, -- pyramid floor (niveau echelle)
  "tileWidth" integer, -- tile width within this mosaic
  "tileHeight" integer, -- tile height within mosaic
  CONSTRAINT "Mosaic_pkey" PRIMARY KEY ("mosaicId"),
  CONSTRAINT mosaic_pyramid FOREIGN KEY (pyramid)
      REFERENCES pgcoverage."Pyramid" ("pyramidId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on pyramid table
);

CREATE TABLE pgcoverage."Tuile"
(
  "tuileId" character varying NOT NULL,
  mosaic character varying, -- pointer on mosaic table
  "posX" integer, -- position in X direction
  "posY" integer, -- position in Y direction
  CONSTRAINT "Tuile_pkey" PRIMARY KEY ("tuileId"),
  CONSTRAINT tuile_mosaic FOREIGN KEY (mosaic)
      REFERENCES pgcoverage."Mosaic" ("mosaicId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on mosaic table
);

CREATE TABLE pgcoverage."Pyramid_Details"
(
  "pyramiddetailsId" character varying NOT NULL,
  pyramid character varying, -- pointer on pyramid table
  value double precision,
  CONSTRAINT "Pyramid_Details_pkey" PRIMARY KEY ("pyramiddetailsId"),
  CONSTRAINT details_pyramid FOREIGN KEY (pyramid)
      REFERENCES pgcoverage."Pyramid" ("pyramidId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on pyramid table
);

CREATE TABLE pgcoverage."Dimension_Mosaic"
(
  "dimension_mosaicId" character varying NOT NULL,
  mosaic character varying, -- pointer on mosaic table
  "axisOrder" integer,
  value double precision,
  CONSTRAINT "Dimension_Mosaic_pkey" PRIMARY KEY ("dimension_mosaicId"),
  CONSTRAINT dimensionmosaic_mosaic FOREIGN KEY (mosaic)
      REFERENCES pgcoverage."Mosaic" ("mosaicId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on mosaic table
);

CREATE TABLE pgcoverage."CRS_Dimension"
(
  "crsDimId" character varying NOT NULL,
  pyramid character varying, -- pointer on pyramid table
  "axisOrder" character varying,
  "code_EPSG" character varying,
  CONSTRAINT "CRS_Dimension_pkey" PRIMARY KEY ("crsDimId"),
  CONSTRAINT "crsDim_pyramid" FOREIGN KEY (pyramid)
      REFERENCES pgcoverage."Pyramid" ("pyramidId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION -- pointer on pyramid table
);
