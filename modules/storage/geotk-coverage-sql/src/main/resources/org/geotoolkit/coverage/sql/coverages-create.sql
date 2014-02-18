--------------------------------------------------------------------------------------------------
-- A schema for a catalog of coverages.                                                         --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--                                                                                              --
-- The "geoadmin", "geouser" and "coverages" words shall be used only for the roles and the     --
-- schema names. They can be replaced by different roles or schema names with a simple search   --
-- and replace.                                                                                 --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';

CREATE SCHEMA coverages;
ALTER SCHEMA coverages OWNER TO geoadmin;
GRANT ALL ON SCHEMA coverages TO geoadmin;
GRANT USAGE ON SCHEMA coverages TO PUBLIC;

COMMENT ON SCHEMA coverages IS 'Metadata for grid coverages';

SET search_path = coverages, metadata, postgis;




--------------------------------------------------------------------------------------------------
-- Creates the "Formats" table.                                                                 --
-- Dependencies: (none)                                                                         --
--------------------------------------------------------------------------------------------------

CREATE TYPE "PackMode" AS ENUM ('native', 'packed', 'geophysics', 'photographic');
CREATE CAST (character varying AS "PackMode") WITH INOUT AS ASSIGNMENT;
COMMENT ON TYPE "PackMode" IS
'Indicates the relationship between the coefficients declared in the "Categories" table and the sample values in the file for a given format:

''native'' means that the "Categories" table describes precisely the transfer function to be used when scaling a physical value for a given element. Those values will overwrite any values found in the image metadata.

''packed'' is similar to ''native'', except that the image metadata will be checked. If negative values exist according image metadata while the "Categories" table declares only ranges of positive values, then the sample values will be shifted to a range of positive values during the reading process. The result is a more compact color model.

''geophysics'' means that the sample values are already geophysics values. The inverse of the transfer function need to be applied in order to produce the packed image.

''photographics'' means that the sample values have no other meaning than visual colors.';


CREATE TABLE "Formats" (
    "name"     character varying NOT NULL PRIMARY KEY,
    "plugin"   character varying NOT NULL,
    "packMode" "PackMode"        NOT NULL DEFAULT 'native',
    "comments" character varying
);

ALTER TABLE "Formats" OWNER TO geoadmin;
GRANT ALL ON TABLE "Formats" TO geoadmin;
GRANT SELECT ON TABLE "Formats" TO PUBLIC;

COMMENT ON TABLE "Formats" IS
    'Image formats.  Each format is associated with an aribtrary number of SampleDimensions.';
COMMENT ON COLUMN "Formats"."name" IS
    'Unique name of the format to be used as an identifier.';
COMMENT ON COLUMN "Formats"."plugin" IS
    'Name of the Java plugin to use for decoding the images.  Examples: PNG, JPEG, TIFF, NetCDF.';
COMMENT ON COLUMN "Formats"."packMode" IS
    'Storage mode of image data: either "geophysics" or "native".';
COMMENT ON COLUMN "Formats"."comments" IS
    'Free text for comments.';




--------------------------------------------------------------------------------------------------
-- Fills the "Formats" table with a few pre-defined records.                                    --
--------------------------------------------------------------------------------------------------

INSERT INTO "Formats" ("name", "plugin", "packMode") VALUES
  ('PNG',  'PNG',  'photographic'),
  ('TIFF', 'TIFF', 'photographic');




--------------------------------------------------------------------------------------------------
-- Creates the "SampleDimensions" table.                                                        --
-- Dependencies: Formats                                                                        --
--------------------------------------------------------------------------------------------------

CREATE TABLE "SampleDimensions" (
    "format" character varying NOT NULL REFERENCES "Formats" ON UPDATE CASCADE ON DELETE CASCADE,
    "band"   smallint          NOT NULL DEFAULT 1 CHECK (band >= 1),
    "name"   character varying NOT NULL,
    "units"  character varying,
    PRIMARY KEY ("format", "band")
);

ALTER TABLE "SampleDimensions" OWNER TO geoadmin;
GRANT ALL ON TABLE "SampleDimensions" TO geoadmin;
GRANT SELECT ON TABLE "SampleDimensions" TO PUBLIC;

CREATE INDEX "SampleDimensions_index" ON "SampleDimensions" ("format", "band");

COMMENT ON TABLE "SampleDimensions" IS
    'Descriptions of the bands included in each image format.';
COMMENT ON COLUMN "SampleDimensions"."format" IS
    'Format having this band.';
COMMENT ON COLUMN "SampleDimensions"."band" IS
    'Band number (starting at 1).';
COMMENT ON COLUMN "SampleDimensions"."name" IS
    'The name of the band.  In a NetCDF file, this is typically the name of the variable stored in this band.';
COMMENT ON COLUMN "SampleDimensions"."units" IS
    'Geophysical measurement units.  May be left blank if not applicable.';
COMMENT ON CONSTRAINT "SampleDimensions_format_fkey" ON "SampleDimensions" IS
    'Each band forms part of the description of the image.';
COMMENT ON CONSTRAINT "SampleDimensions_band_check" ON "SampleDimensions" IS
    'The band number must be positive.';
COMMENT ON INDEX "SampleDimensions_index" IS
    'Index of the bands in order of appearance.';




--------------------------------------------------------------------------------------------------
-- Creates the "Categories" table.                                                              --
-- Dependencies: "SampleDimensions"                                                             --
--------------------------------------------------------------------------------------------------

CREATE TABLE "Categories" (
    "format"   character varying NOT NULL,
    "band"     smallint          NOT NULL DEFAULT 1,
    "name"     character varying NOT NULL,
    "lower"    integer           NOT NULL,
    "upper"    integer           NOT NULL,
    "c0"       double precision,
    "c1"       double precision,
    "function" "MI_TransferFunctionTypeCode",
    "colors"   character varying,
    PRIMARY KEY ("format", "band", "name"),
    FOREIGN KEY ("format", "band") REFERENCES "SampleDimensions" ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "Categories_range" CHECK ("lower" <= "upper"),
    CONSTRAINT "Categories_coefficients" CHECK
                    ((("c0" IS     NULL) AND ("c1" IS     NULL)) OR
                     (("c0" IS NOT NULL) AND ("c1" IS NOT NULL) AND ("c1" <> 0)))
);

ALTER TABLE "Categories" OWNER TO geoadmin;
GRANT ALL ON TABLE "Categories" TO geoadmin;
GRANT SELECT ON TABLE "Categories" TO PUBLIC;

CREATE INDEX "Categories_index" ON "Categories" ("band", "lower");

COMMENT ON TABLE "Categories" IS
    'Categories classify the ranges of values and scaling information for interpreting geophysical measurements from pixel values and for rendering (coloring) the image.';
COMMENT ON COLUMN "Categories"."format" IS
    'Name of the format to which this range of values applies.';
COMMENT ON COLUMN "Categories"."band" IS
    'Number of the band to which this range of values applies.';
COMMENT ON COLUMN "Categories"."name" IS
    'Name of the category represented by this range of values.';
COMMENT ON COLUMN "Categories"."lower" IS
    'Minimum pixel value (inclusive) for this category.';
COMMENT ON COLUMN "Categories"."upper" IS
    'Maximum pixel value (inclusive) for this category.';
COMMENT ON COLUMN "Categories"."c0" IS
    'Coefficient C0 of the equation y=C0+C1*x, where x is the pixel value and y is the value of the geophysical measurement.  May be left blank if not applicable.';
COMMENT ON COLUMN "Categories"."c1" IS
    'Coefficient C1 of the equation y=C0+C1*x, where x is the pixel value and y is the value of the geophysical measurement.  May be left blank if not applicable.';
COMMENT ON COLUMN "Categories"."function" IS
    'Transform function to be used when scaling a physical value: "linear" (or omitted) for y=C0+C1*x, or "expentional" for y=10^(C0+C1*x).';
COMMENT ON COLUMN "Categories"."colors" IS
    'This field can be either a color code or the name of a color pallet.';
COMMENT ON CONSTRAINT "Categories_format_fkey" ON "Categories" IS
    'Each category is an element of the band description.';
COMMENT ON CONSTRAINT "Categories_coefficients" ON "Categories" IS
    'Both coefficients C0 and C1 must be either null or non-null.';
COMMENT ON INDEX "Categories_index" IS
    'Index of categories belonging to a band.';




--------------------------------------------------------------------------------------------------
-- Creates the "RangeOfFormats" view.                                                           --
-- Dependencies: "Categories", "SampleDimensions", "Format"                                     --
--------------------------------------------------------------------------------------------------
CREATE VIEW "RangeOfFormats" AS
 SELECT "SampleDimensions"       ."format",
        "SampleDimensions"       ."name" AS "band",
        "RangeOfSampleDimensions"."fillValue",
        "RangeOfSampleDimensions"."lower",
        "RangeOfSampleDimensions"."upper",
        "RangeOfSampleDimensions"."minimum",
        "RangeOfSampleDimensions"."maximum",
        "SampleDimensions"       ."units",
        "Formats"                ."packMode"
   FROM "SampleDimensions" JOIN (
 SELECT "format", "band", count("band") AS "numCategories",
        min("lower") AS "lower",
        max("upper") AS "upper",
        min(CASE WHEN "c1" IS NULL THEN "lower" ELSE NULL END) AS "fillValue",
        min((CASE WHEN "c1" < 0 THEN "upper" ELSE "lower" END) * "c1" + "c0") AS "minimum",
        max((CASE WHEN "c1" < 0 THEN "lower" ELSE "upper" END) * "c1" + "c0") AS "maximum"
   FROM "Categories" GROUP BY "format", "band") AS "RangeOfSampleDimensions"
     ON "SampleDimensions"."format" = "RangeOfSampleDimensions"."format" AND
        "SampleDimensions"."band"   = "RangeOfSampleDimensions"."band"
   JOIN "Formats" ON "SampleDimensions"."format" = "Formats"."name"
  ORDER BY "SampleDimensions"."format", "SampleDimensions"."band";

ALTER TABLE "RangeOfFormats" OWNER TO geoadmin;
GRANT ALL ON TABLE "RangeOfFormats" TO geoadmin;
GRANT SELECT ON TABLE "RangeOfFormats" TO PUBLIC;
COMMENT ON VIEW "RangeOfFormats" IS
    'Value range of each image format.';




--------------------------------------------------------------------------------------------------
-- Creates the "Layers" table.                                                                  --
-- Dependencies: (none)                                                                         --
--------------------------------------------------------------------------------------------------

CREATE TABLE "Layers" (
    "name"      character varying NOT NULL PRIMARY KEY,
    "period"    double precision,
    "minScale"  double precision CHECK ("minScale" >= 1),
    "maxScale"  double precision CHECK ("maxScale" >= 1),
    "fallback"  character varying REFERENCES "Layers" ON UPDATE CASCADE ON DELETE RESTRICT,
    "comments"  character varying
);

ALTER TABLE "Layers" OWNER TO geoadmin;
GRANT ALL ON TABLE "Layers" TO geoadmin;
GRANT SELECT ON TABLE "Layers" TO PUBLIC;

COMMENT ON TABLE "Layers" IS
    'Set of a series of images belonging to the same category.';
COMMENT ON COLUMN "Layers"."name" IS
    'Name of the layer.';
COMMENT ON COLUMN "Layers"."period" IS
    'Number of days between images.  Can be approximate or left blank if not applicable.';
COMMENT ON COLUMN "Layers"."minScale" IS
    'Minimum scale to request this Layer.';
COMMENT ON COLUMN "Layers"."maxScale" IS
    'Maximum scale to request this Layer.';
COMMENT ON COLUMN "Layers"."fallback" IS
    'Fallback layer which is suggested if no data is available for the current layer.';
COMMENT ON COLUMN "Layers"."comments" IS
    'Free text for comments.';
COMMENT ON CONSTRAINT "Layers_minScale_check" ON "Layers" IS
    'The minimum scale shall be equals or greater than 1.';
COMMENT ON CONSTRAINT "Layers_maxScale_check" ON "Layers" IS
    'The maximum scale shall be equals or greater than 1.';
COMMENT ON CONSTRAINT "Layers_fallback_fkey" ON "Layers" IS
    'Each fallback layer must exist.';




--------------------------------------------------------------------------------------------------
-- Creates the "Series" table.                                                                  --
-- Dependencies: "Layers", "Formats"                                                            --
--------------------------------------------------------------------------------------------------

CREATE SEQUENCE "seq_Series" START 1000;

CREATE TABLE "Series" (
    "identifier" integer           NOT NULL PRIMARY KEY DEFAULT nextval('coverages."seq_Series"'),
    "layer"      character varying NOT NULL REFERENCES "Layers"  ON UPDATE CASCADE ON DELETE CASCADE,
    "pathname"   character varying NOT NULL,
    "extension"  character varying, -- Accepts NULL since some file formats have no extension
    "format"     character varying NOT NULL REFERENCES "Formats" ON UPDATE CASCADE ON DELETE RESTRICT,
    "quicklook"  integer           UNIQUE   REFERENCES "Series"  ON UPDATE CASCADE ON DELETE RESTRICT,
    "comments"   character varying
);

ALTER SEQUENCE "seq_Series" OWNED BY "Series"."identifier";

ALTER TABLE "Series" OWNER TO geoadmin;
GRANT ALL ON TABLE "Series" TO geoadmin;
GRANT SELECT ON TABLE "Series" TO PUBLIC;

CREATE INDEX "Series_index" ON "Series" ("layer");

COMMENT ON SEQUENCE "seq_Series" IS
    'Primary keys generator for the Series table.';
COMMENT ON TABLE "Series" IS
    'Series of images.  Each image belongs to a series.';
COMMENT ON COLUMN "Series"."identifier" IS
    'Unique identifier.';
COMMENT ON COLUMN "Series"."layer" IS
    'The layer to which the images in the series belong.';
COMMENT ON COLUMN "Series"."pathname" IS
    'Relative path to the files in the group.  The root path should not be specified if it is machine-dependent.';
COMMENT ON COLUMN "Series"."extension" IS
    'File extention of the images in the series.';
COMMENT ON COLUMN "Series"."format" IS
    'Format of the images in the series.';
COMMENT ON COLUMN "Series"."quicklook" IS
    'Series of overview images.';
COMMENT ON COLUMN "Series"."comments" IS
    'Free text for comments.';
COMMENT ON CONSTRAINT "Series_quicklook_key" ON "Series" IS
    'Each series has only one overview series.';
COMMENT ON CONSTRAINT "Series_layer_fkey" ON "Series" IS
    'Each series belongs to a layer.';
COMMENT ON CONSTRAINT "Series_format_fkey" ON "Series" IS
    'All the images of a series use the same series.';
COMMENT ON CONSTRAINT "Series_quicklook_fkey" ON "Series" IS
    'The overviews apply to another series of images.';
COMMENT ON INDEX "Series_index" IS
    'Index of series belonging to a layer.';




--------------------------------------------------------------------------------------------------
-- Creates the "GridGeometries" table.                                                          --
-- Dependencies: (none)                                                                         --
--------------------------------------------------------------------------------------------------

CREATE SEQUENCE "seq_GridGeometries" START 1000;

CREATE TABLE "GridGeometries" (
    "identifier"        integer           NOT NULL PRIMARY KEY DEFAULT nextval('coverages."seq_GridGeometries"'),
    "width"             integer           NOT NULL,
    "height"            integer           NOT NULL,
    "scaleX"            double precision  NOT NULL DEFAULT 1,
    "shearY"            double precision  NOT NULL DEFAULT 0,
    "shearX"            double precision  NOT NULL DEFAULT 0,
    "scaleY"            double precision  NOT NULL DEFAULT 1,
    "translateX"        double precision  NOT NULL DEFAULT 0,
    "translateY"        double precision  NOT NULL DEFAULT 0,
    "horizontalSRID"    integer           NOT NULL DEFAULT 4326,
    CONSTRAINT "GridGeometries_size" CHECK (width > 0 AND height > 0)
);

ALTER SEQUENCE "seq_GridGeometries" OWNED BY "GridGeometries"."identifier";

SELECT AddGeometryColumn('GridGeometries', 'horizontalExtent', 4326, 'POLYGON', 2);
ALTER TABLE "GridGeometries" ALTER COLUMN "horizontalExtent" SET NOT NULL;
ALTER TABLE "GridGeometries" ADD COLUMN "verticalSRID" integer;
ALTER TABLE "GridGeometries" ADD COLUMN "verticalOrdinates" double precision[];
ALTER TABLE "GridGeometries"
  ADD CONSTRAINT "enforce_srid_verticalOrdinates" CHECK
            (((("verticalSRID" IS     NULL) AND ("verticalOrdinates" IS     NULL)) OR
              (("verticalSRID" IS NOT NULL) AND ("verticalOrdinates" IS NOT NULL))));

ALTER TABLE "GridGeometries" OWNER TO geoadmin;
GRANT ALL ON TABLE "GridGeometries" TO geoadmin;
GRANT SELECT ON TABLE "GridGeometries" TO PUBLIC;

ALTER TABLE ONLY "GridGeometries"
    ADD CONSTRAINT "fk_SRID" FOREIGN KEY ("horizontalSRID") REFERENCES spatial_ref_sys(srid)
    ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY "GridGeometries"
    ADD CONSTRAINT "fk_VERT_SRID" FOREIGN KEY ("verticalSRID") REFERENCES spatial_ref_sys(srid)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE INDEX "HorizontalExtent_index" ON "GridGeometries" USING gist ("horizontalExtent");
COMMENT ON INDEX "HorizontalExtent_index" IS
    'Index of geometries intersecting a geographical area.';

COMMENT ON SEQUENCE "seq_GridGeometries" IS
    'Primary keys generator for the GridGeometries table.';
COMMENT ON TABLE "GridGeometries" IS
    'Spatial referencing parameters for a Grid Coverage.  Defines the spatial envelope of the images, as well as their grid dimensions.';
COMMENT ON COLUMN "GridGeometries"."identifier" IS
    'Unique identifier.';
COMMENT ON COLUMN "GridGeometries"."width" IS
    'Number of pixels wide.';
COMMENT ON COLUMN "GridGeometries"."height" IS
    'Number of pixels high.';
COMMENT ON COLUMN "GridGeometries"."scaleX" IS
    'Element (0,0) of the affine transform.  Usually corresponds to the x size of the pixels.';
COMMENT ON COLUMN "GridGeometries"."shearY" IS
    'Element (1,0) of the affine transform.  Always 0 if there is no rotation.';
COMMENT ON COLUMN "GridGeometries"."shearX" IS
    'Element (0,1) of the affine transform.  Always 0 if there is no rotation.';
COMMENT ON COLUMN "GridGeometries"."scaleY" IS
    'Element (1,1) of the affine transform.  Usually corresponds to the y size of the pixels.  This value is often negative since the numbering of the lines of an image increases downwards.';
COMMENT ON COLUMN "GridGeometries"."translateX" IS
    'Element (0,2) of the affine transform.  Usually corresponds to the x-coordinate of the top left corner.';
COMMENT ON COLUMN "GridGeometries"."translateY" IS
    'Element (1,2) of the affine transform.  Usually corresponds to the y-coordinate of the top left corner.';
COMMENT ON COLUMN "GridGeometries"."horizontalSRID" IS
    'Horizontal coordinate system code.';
COMMENT ON COLUMN "GridGeometries"."horizontalExtent" IS
    'Horizontal spatial extent. (Computed automatically if none is explicitly defined).';
COMMENT ON COLUMN "GridGeometries"."verticalSRID" IS
    'Vertical coordinate system code.';
COMMENT ON COLUMN "GridGeometries"."verticalOrdinates" IS
    'Z values of each of the layers of a 3D image.';
COMMENT ON CONSTRAINT "GridGeometries_size" ON "GridGeometries" IS
    'The dimensions of the images must be positive.';
COMMENT ON CONSTRAINT "enforce_srid_verticalOrdinates" ON "GridGeometries" IS
    'The vertical coordinates and their SRID must both either be null or non-null.';




--------------------------------------------------------------------------------------------------
-- Function to be applied on new records in the "GridGeometries" table.                         --
--------------------------------------------------------------------------------------------------

CREATE FUNCTION "ComputeDefaultExtent"() RETURNS "trigger"
    AS $$
  BEGIN
    IF NEW."horizontalExtent" IS NULL THEN
      NEW."horizontalExtent" := st_Transform(st_Affine(st_GeometryFromText(
        'POLYGON((0 0,0 ' || NEW."height" || ',' || NEW."width" || ' ' || NEW."height" || ',' || NEW."width" || ' 0,0 0))',
        NEW."horizontalSRID"), NEW."scaleX", NEW."shearX", NEW."shearY", NEW."scaleY", NEW."translateX", NEW."translateY"), 4326);
    END IF;
    RETURN NEW;
  END;
$$
    LANGUAGE plpgsql;

ALTER FUNCTION "ComputeDefaultExtent"() OWNER TO geoadmin;
GRANT ALL ON FUNCTION "ComputeDefaultExtent"() TO geoadmin;
GRANT EXECUTE ON FUNCTION "ComputeDefaultExtent"() TO PUBLIC;

CREATE TRIGGER "addDefaultExtent"
    BEFORE INSERT OR UPDATE ON "GridGeometries"
    FOR EACH ROW
    EXECUTE PROCEDURE "ComputeDefaultExtent"();

COMMENT ON TRIGGER "addDefaultExtent" ON "GridGeometries" IS
    'Add an envelope by default if none is explicitly defined.';




--------------------------------------------------------------------------------------------------
-- Creates the "BoundingBoxes" view.                                                            --
-- Dependencies:  "GridGeometries"                                                              --
-- Inner queries: "Corners" contains 4 (x,y) corners as 4 rows for each grid geometries.        --
--                "NativeBoxes" contains the minimum and maximum values of "Corners".           --
--------------------------------------------------------------------------------------------------

CREATE VIEW "BoundingBoxes" AS
    SELECT "GridGeometries"."identifier", "width", "height",
           "horizontalSRID" AS "crs", "minX", "maxX", "minY", "maxY",
           st_xmin("horizontalExtent") AS "west",
           st_xmax("horizontalExtent") AS "east",
           st_ymin("horizontalExtent") AS "south",
           st_ymax("horizontalExtent") AS "north"
      FROM "GridGeometries"
 LEFT JOIN (SELECT "identifier",
       min("x") AS "minX",
       max("x") AS "maxX",
       min("y") AS "minY",
       max("y") AS "maxY"
FROM (SELECT "identifier",
             "translateX" AS "x",
             "translateY" AS "y" FROM "GridGeometries"
UNION SELECT "identifier",
             "width"*"scaleX" + "translateX" AS "x",
             "width"*"shearY" + "translateY" AS "y" FROM "GridGeometries"
UNION SELECT "identifier",
             "height"*"shearX" + "translateX" AS "x",
             "height"*"scaleY" + "translateY" AS "y" FROM "GridGeometries"
UNION SELECT "identifier",
             "width"*"scaleX" + "height"*"shearX" + "translateX" AS "x",
             "width"*"shearY" + "height"*"scaleY" + "translateY" AS "y" FROM "GridGeometries") AS "Corners"
    GROUP BY "identifier") AS "NativeBoxes"
          ON "GridGeometries"."identifier" = "NativeBoxes"."identifier"
    ORDER BY "identifier";

ALTER TABLE "BoundingBoxes" OWNER TO geoadmin;
GRANT ALL ON TABLE "BoundingBoxes" TO geoadmin;
GRANT SELECT ON TABLE "BoundingBoxes" TO PUBLIC;

COMMENT ON VIEW "BoundingBoxes" IS
    'Comparison between the calculated envelopes and the declared envelopes.';




--------------------------------------------------------------------------------------------------
-- Creates the "GridCoverages" table.                                                           --
-- Dependencies: "Series", "GridGeometries"                                                     --
--------------------------------------------------------------------------------------------------

CREATE TABLE "GridCoverages" (
    "series"    integer           NOT NULL REFERENCES "Series" ON UPDATE CASCADE ON DELETE CASCADE,
    "filename"  character varying NOT NULL,
    "index"     smallint          NOT NULL DEFAULT 1 CHECK ("index" >= 1),
    "startTime" timestamp without time zone,
    "endTime"   timestamp without time zone,
    "extent"    integer           NOT NULL REFERENCES "GridGeometries" ON UPDATE CASCADE ON DELETE RESTRICT,
    PRIMARY KEY ("series", "filename", "index"),
    CHECK ((("startTime" IS     NULL) AND ("endTime" IS     NULL)) OR
           (("startTime" IS NOT NULL) AND ("endTime" IS NOT NULL) AND ("startTime" <= "endTime")))
);

ALTER TABLE "GridCoverages" OWNER TO geoadmin;
GRANT ALL ON TABLE "GridCoverages" TO geoadmin;
GRANT SELECT ON TABLE "GridCoverages" TO PUBLIC;

-- The unique constraint shall use the same order than the index.
ALTER TABLE coverages."GridCoverages"
  ADD CONSTRAINT "GridCoverages_series_key" UNIQUE(series, "endTime", "startTime");

-- Index "endTime" before "startTime" because we most frequently sort by
-- end time, since we are often interrested in the latest image available.
CREATE INDEX "GridCoverages_index"        ON "GridCoverages" ("series", "endTime", "startTime");
CREATE INDEX "GridCoverages_extent_index" ON "GridCoverages" ("series", "extent");

COMMENT ON TABLE "GridCoverages" IS
    'List of all the images available.  Each listing corresponds to an image file.';
COMMENT ON COLUMN "GridCoverages"."series" IS
    'Series to which the image belongs.';
COMMENT ON COLUMN "GridCoverages"."filename" IS
    'File name of the image.';
COMMENT ON COLUMN "GridCoverages"."index" IS
    'Index of the image in the file (for files containing multipal images).  Numbered from 1.';
COMMENT ON COLUMN "GridCoverages"."startTime" IS
    'Date and time of the image acquisition start, in UTC.  In the case of averages, the time corresponds to the beginning of the interval used to calculate the average.';
COMMENT ON COLUMN "GridCoverages"."endTime" IS
    'Date and time of the image acquisition end, in UTC.  This time must be greater than or equal to the acquisition start time.';
COMMENT ON COLUMN "GridCoverages"."extent" IS
    'Grid Geomerty ID that defines the spatial footprint of this coverage.';
COMMENT ON CONSTRAINT "GridCoverages_series_key" ON "GridCoverages" IS
    'The time range of the image must be unique in each series.';
COMMENT ON CONSTRAINT "GridCoverages_series_fkey" ON "GridCoverages" IS
    'Each image belongs to a series.';
COMMENT ON CONSTRAINT "GridCoverages_extent_fkey" ON "GridCoverages" IS
    'Each image must have a spatial extent.';
COMMENT ON CONSTRAINT "GridCoverages_check" ON "GridCoverages" IS
    'The start and end times must be both null or both non-null, and the end time must be greater than or equal to the start time.';
COMMENT ON CONSTRAINT "GridCoverages_index_check" ON "GridCoverages" IS
    'The image index must be positive.';
COMMENT ON INDEX "GridCoverages_index" IS
    'Index of all the images within a certain time range.';
COMMENT ON INDEX "GridCoverages_extent_index" IS
    'Index of all the images in a geographic region.';




--------------------------------------------------------------------------------------------------
-- Creates the "Tiles" table.                                                                   --
-- Dependencies: "Series", "GridGeometries", "GridCoverages"                                    --
--------------------------------------------------------------------------------------------------

CREATE TABLE "Tiles" (
  "dx" INTEGER NOT NULL DEFAULT 0,
  "dy" INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY ("series", "filename", "index"),
  FOREIGN KEY ("extent") REFERENCES "GridGeometries" ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY ("series") REFERENCES "Series" ON UPDATE CASCADE ON DELETE CASCADE
) INHERITS ("GridCoverages");

ALTER TABLE "Tiles" OWNER TO geoadmin;
GRANT ALL ON TABLE "Tiles" TO geoadmin;
GRANT SELECT ON TABLE "Tiles" TO PUBLIC;

CREATE INDEX "Tiles_index" ON "Tiles" ("series", "endTime", "startTime");

COMMENT ON TABLE "Tiles" IS
    'List of all images that are actually tiles in a image mosaic.';
COMMENT ON COLUMN "Tiles"."dx" IS 'Amount of pixels to translate along the x axis before to apply the affine transform.';
COMMENT ON COLUMN "Tiles"."dy" IS 'Amount of pixels to translate along the y axis before to apply the affine transform.';



--------------------------------------------------------------------------------------------------
-- Creates the "Tiling" view.                                                                   --
-- Dependencies: "Tiles", "GridGeometries", "Series"                                            --
--------------------------------------------------------------------------------------------------

CREATE VIEW "Tiling" AS
 SELECT "layer",
        "startTime",
        "endTime",
        count("filename") AS "numTiles",
        (max("dx") - min("dx")) / max("width") + 1 AS "numXTiles",
        (max("dy") - min("dy")) / max("height") + 1 AS "numYTiles",
        max("width") = min("width") AND max("height") = min("height") AS "uniformSize",
        max("width") AS "width",
        max("height") AS "height",
        sqrt("scaleX" * "scaleX" + "shearX" * "shearX") AS "scaleX",
        sqrt("scaleY" * "scaleY" + "shearY" * "shearY") AS "scaleY",
        CASE WHEN "scaleX" >= 0 THEN min("translateX") ELSE max("translateX") END AS "xOrigin",
        CASE WHEN "scaleY" >= 0 THEN min("translateY") ELSE max("translateY") END AS "yOrigin",
        "horizontalSRID"
   FROM "Tiles"
   JOIN "GridGeometries" ON "Tiles"."extent" = "GridGeometries".identifier
   JOIN "Series" ON "Tiles".series = "Series".identifier
  GROUP BY "layer", "endTime", "startTime", "horizontalSRID", "scaleX", "scaleY", "shearX", "shearY"
  ORDER BY "layer", "endTime", "scaleX"*"scaleX" + "shearX"*"shearX" + "scaleY"*"scaleY" + "shearY"*"shearY" DESC;

ALTER TABLE "Tiling" OWNER TO geoadmin;
GRANT ALL ON TABLE "Tiling" TO geoadmin;
GRANT SELECT ON TABLE "Tiling" TO PUBLIC;

COMMENT ON VIEW "Tiling" IS
    'Summary of tiling by series inferred from the tiles table content.';



--------------------------------------------------------------------------------------------------
-- Creates the "DomainOfTiles" view.                                                            --
-- Dependencies: "Tiles", "GridGeometries"                                                      --
--------------------------------------------------------------------------------------------------

CREATE VIEW "DomainOfTiles" AS
    SELECT "series", "extent", "count", "tmin", "tmax",
           st_xmin("horizontalExtent") AS "xmin",
           st_xmax("horizontalExtent") AS "xmax",
           st_ymin("horizontalExtent") AS "ymin",
           st_ymax("horizontalExtent") AS "ymax",
           st_xmin("geographic")       AS "west",
           st_xmax("geographic")       AS "east",
           st_ymin("geographic")       AS "south",
           st_ymax("geographic")       AS "north"
      FROM
   (SELECT *, st_Transform("horizontalExtent", 4326) AS "geographic"
      FROM
   (SELECT "series", "extent", "count", "tmin", "tmax", st_Affine(st_GeometryFromText('POLYGON((' ||
           "xmin" || ' ' || "ymin" || ',' ||
           "xmax" || ' ' || "ymin" || ',' ||
           "xmax" || ' ' || "ymax" || ',' ||
           "xmin" || ' ' || "ymax" || ',' ||
           "xmin" || ' ' || "ymin" || '))',
           "horizontalSRID"), "scaleX", "shearX", "shearY", "scaleY", "translateX", "translateY")
           AS "horizontalExtent"
      FROM
   (SELECT "series", "extent", "count", "tmin", "tmax",
           "xmin", "xmax" + "width"  AS "xmax",
           "ymin", "ymax" + "height" AS "ymax",
           "horizontalSRID", "scaleX", "shearX", "shearY", "scaleY", "translateX", "translateY"
      FROM
   (SELECT "series", "extent",
           count("extent")  AS "count",
           min("startTime") AS "tmin",
           max("endTime")   AS "tmax",
           min("dx")        AS "xmin",
           max("dx")        AS "xmax",
           min("dy")        AS "ymin",
           max("dy")        AS "ymax"
           FROM "Tiles"
  GROUP BY "series", "extent") AS "Boxes"
      JOIN "GridGeometries" ON "Boxes"."extent" = "GridGeometries"."identifier")
        AS "Extents") AS "Transformed") AS "Projected"
  ORDER BY "series", "extent";

ALTER TABLE "DomainOfTiles" OWNER TO geoadmin;
GRANT ALL ON TABLE "DomainOfTiles" TO geoadmin;
GRANT SELECT ON TABLE "DomainOfTiles" TO PUBLIC;

COMMENT ON VIEW "DomainOfTiles" IS
    'Geographic bounding boxes of tiles for each extent.';




--------------------------------------------------------------------------------------------------
-- Creates the "DomainOfSeries" view.                                                           --
-- Dependencies: "GridCoverages", "BoundingBoxes"                                               --
--------------------------------------------------------------------------------------------------

CREATE VIEW "DomainOfSeries" AS
    SELECT "TimeRanges"."series", "count", "startTime", "endTime",
           "west", "east", "south", "north", "xResolution", "yResolution"
      FROM
   (SELECT "series",
           count("extent")  AS "count",
           min("startTime") AS "startTime",
           max("endTime")   AS "endTime"
      FROM ONLY "GridCoverages" GROUP BY "series") AS "TimeRanges"
      JOIN
   (SELECT "series",
           min("west")  AS "west",
           max("east")  AS "east",
           min("south") AS "south",
           max("north") AS "north",
           min(("east"  - "west" ) / "width" ) AS "xResolution",
           min(("north" - "south") / "height") AS "yResolution"
      FROM (SELECT DISTINCT "series", "extent" FROM ONLY "GridCoverages") AS "Extents"
 LEFT JOIN "BoundingBoxes" ON "Extents"."extent" = "BoundingBoxes"."identifier"
  GROUP BY "series") AS "BoundingBoxRanges" ON "TimeRanges".series = "BoundingBoxRanges".series
  ORDER BY "series";

ALTER TABLE "DomainOfSeries" OWNER TO geoadmin;
GRANT ALL ON TABLE "DomainOfSeries" TO geoadmin;
GRANT SELECT ON TABLE "DomainOfSeries" TO PUBLIC;

COMMENT ON VIEW "DomainOfSeries" IS
    'List of geographical areas used by each sub-series.';




--------------------------------------------------------------------------------------------------
-- Creates the "DomainOfLayers" view.                                                           --
-- Dependencies: "DomainOfSeries", "Series"                                                     --
--------------------------------------------------------------------------------------------------

CREATE VIEW "DomainOfLayers" AS
 SELECT "layer",
        sum("count")       AS "count",
        min("startTime")   AS "startTime",
        max("endTime")     AS "endTime",
        min("west")        AS "west",
        max("east")        AS "east",
        min("south")       AS "south",
        max("north")       AS "north",
        min("xResolution") AS "xResolution",
        min("yResolution") AS "yResolution"
   FROM "DomainOfSeries"
   JOIN "Series" ON "DomainOfSeries"."series" = "Series"."identifier"
  GROUP BY "layer"
  ORDER BY "layer";

ALTER TABLE "DomainOfLayers" OWNER TO geoadmin;
GRANT ALL ON TABLE "DomainOfLayers" TO geoadmin;
GRANT SELECT ON TABLE "DomainOfLayers" TO PUBLIC;

COMMENT ON VIEW "DomainOfLayers" IS
    'Number of images and geographical area for each layer used.';
