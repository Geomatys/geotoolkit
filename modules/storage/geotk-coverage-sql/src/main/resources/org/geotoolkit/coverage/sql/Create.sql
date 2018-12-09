--
-- Schema required by org.geotoolkit.coverage.sql package.
--

CREATE SCHEMA rasters;
COMMENT ON SCHEMA rasters IS 'Catalog of grid rasters';


--
-- General note: For all types in this file, VARCHAR(15) is a foreigner key to an entry in "metadata" schema.
-- The "metadata" schema is created by "sis-metadata" module. The VARCHAR(120) entries use an arbitrary length,
-- but that length should be consistent with the length used in the "metadata" schema.
--

--
-- Description of raster formats. Filled with a few pre-defined records for commonly used formats.
-- Requires the "metadata" schema to be created and populated before this "rasters" schema.
--
CREATE TABLE rasters."Formats" (
  "name"     VARCHAR(120) NOT NULL PRIMARY KEY,
  "driver"   VARCHAR(120) NOT NULL,
  "metadata" VARCHAR(15)  REFERENCES metadata."Format" ("ID") ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE  rasters."Formats"            IS 'Raster formats. Each format is associated with an aribtrary number of SampleDimensions.';
COMMENT ON COLUMN rasters."Formats"."name"     IS 'Unique name of the format to be used as an identifier.';
COMMENT ON COLUMN rasters."Formats"."driver"   IS 'Name of the driver to use for decoding the rasters. Examples: GeoTIFF, NetCDF.';
COMMENT ON COLUMN rasters."Formats"."metadata" IS 'Reference to additional information about the format.';

INSERT INTO rasters."Formats" ("name", "driver", "metadata") VALUES
  ('PNG',  'Image:PNG',  'PNG'),
  ('TIFF', 'Image:TIFF', 'GeoTIFF');



--
-- Description of the bands for each format. This table duplicates the information provided in rich formats
-- like netCDF. But we declare them in the database for supporting non-geospatial formats like PNG.
-- Note that the "units" column duplicates metadata."SampleDimension"."units".
--
CREATE TABLE rasters."SampleDimensions" (
  "format"     VARCHAR(120) NOT NULL REFERENCES rasters."Formats" ON UPDATE CASCADE ON DELETE CASCADE,
  "band"       SMALLINT     NOT NULL DEFAULT 1 CHECK (band >= 1),
  "identifier" VARCHAR(120),
  "units"      VARCHAR(20),
  "isPacked"   BOOLEAN      NOT NULL DEFAULT TRUE,
  "metadata"   VARCHAR(15)  REFERENCES metadata."SampleDimension" ("ID") ON UPDATE CASCADE ON DELETE RESTRICT,
  PRIMARY KEY ("format", "band")
);

COMMENT ON TABLE  rasters."SampleDimensions"              IS 'Descriptions of the bands in each raster format.';
COMMENT ON COLUMN rasters."SampleDimensions"."format"     IS 'Format having this band.';
COMMENT ON COLUMN rasters."SampleDimensions"."band"       IS 'Band sequence number (starting at 1).';
COMMENT ON COLUMN rasters."SampleDimensions"."identifier" IS 'If the raster format requires an identifier for accessing data (for example a variable name in a netCDF file), that identifier. Otherwise can be used as a label.';
COMMENT ON COLUMN rasters."SampleDimensions"."units"      IS 'Units of measurement. May be left blank if not applicable. Should be consistent with units declared in metadata.';
COMMENT ON COLUMN rasters."SampleDimensions"."isPacked"   IS 'Whether values are stored using a smaller data type, to be converted using an offset and scale factor.';
COMMENT ON COLUMN rasters."SampleDimensions"."metadata"   IS 'Reference to additional information about the band, including offset, scale factor and units of measurement.';
COMMENT ON CONSTRAINT "SampleDimensions_format_fkey" ON rasters."SampleDimensions" IS 'Each band forms part of the description of the image.';
COMMENT ON CONSTRAINT "SampleDimensions_band_check"  ON rasters."SampleDimensions" IS 'The band number must be positive.';



--
-- Description of categories in each band. There is no direct equivalence to ISO 19115,
-- but there is a loose relationship with MI_RangeElementDescription and a duplication
-- of "scale factor" and "offset" attributes from ISO 19115 MD_SampleDimension class.
-- This duplication exists because this rasters schema associate the transfer function
-- to categories instead than sample dimensions (i.e. we provide a finer grain control).
--
CREATE TABLE rasters."Categories" (
    "format"   VARCHAR(120) NOT NULL,
    "band"     SMALLINT     NOT NULL DEFAULT 1,
    "name"     VARCHAR(120) NOT NULL,
    "lower"    INTEGER      NOT NULL,
    "upper"    INTEGER      NOT NULL,
    "scale"    DOUBLE PRECISION,
    "offset"   DOUBLE PRECISION,
    "function" metadata."TransferFunctionTypeCode",
    "colors"   VARCHAR(80),
    PRIMARY KEY ("format", "band", "name"),
    FOREIGN KEY ("format", "band") REFERENCES rasters."SampleDimensions" ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "Categories_range" CHECK ("lower" <= "upper"),
    CONSTRAINT "Categories_coefficients" CHECK (("scale" IS NULL) = ("offset" IS NULL) AND "scale" <> 0)
);

CREATE INDEX "Categories_index" ON rasters."Categories" ("band", "lower");



COMMENT ON TABLE  rasters."Categories"            IS 'Categories classify the ranges of values and scaling information for interpreting measurements from cell values and for rendering (coloring) the raster.';
COMMENT ON COLUMN rasters."Categories"."format"   IS 'Name of the format to which this range of values applies.';
COMMENT ON COLUMN rasters."Categories"."band"     IS 'Number of the band to which this range of values applies.';
COMMENT ON COLUMN rasters."Categories"."name"     IS 'Name of the category represented by this range of values.';
COMMENT ON COLUMN rasters."Categories"."lower"    IS 'Minimum cell value (inclusive) for this category.';
COMMENT ON COLUMN rasters."Categories"."upper"    IS 'Maximum cell value (inclusive) for this category.';
COMMENT ON COLUMN rasters."Categories"."scale"    IS 'Coefficient C1 of the equation y=C0+C1*x, where x is the cell value and y is the value of the geophysical measurement. May be left blank if not applicable.';
COMMENT ON COLUMN rasters."Categories"."offset"   IS 'Coefficient C0 of the equation y=C0+C1*x, where x is the cell value and y is the value of the geophysical measurement. May be left blank if not applicable.';
COMMENT ON COLUMN rasters."Categories"."function" IS 'Transform function to be used when scaling a physical value: "linear" (or omitted) for y=C0+C1*x, or "expentional" for y=10^(C0+C1*x).';
COMMENT ON COLUMN rasters."Categories"."colors"   IS 'This field can be either a color code or the name of a color pallet.';
COMMENT ON INDEX  rasters."Categories_index"      IS 'Index of categories belonging to a band.';
COMMENT ON CONSTRAINT "Categories_format_fkey"  ON rasters."Categories" IS 'Each category is an element of the band description.';
COMMENT ON CONSTRAINT "Categories_coefficients" ON rasters."Categories" IS 'Both coefficients C0 and C1 must be either null or non-null.';
COMMENT ON CONSTRAINT "Categories_range"        ON rasters."Categories" IS 'Lower value shall not be greater than upper value.';


--
-- Creates a view of the sample dimensions which include scale factor and fill value.
-- This view is closer to the kind of information stored for example in netCDF files.
--
CREATE VIEW rasters."RangeOfFormats" AS
 SELECT "SampleDimensions"       ."format",
        "SampleDimensions"       ."identifier" AS "band",
        "RangeOfSampleDimensions"."fillValue",
        "RangeOfSampleDimensions"."lower",
        "RangeOfSampleDimensions"."upper",
        "RangeOfSampleDimensions"."minimum",
        "RangeOfSampleDimensions"."maximum",
        "SampleDimensions"       ."units"
   FROM rasters."SampleDimensions" JOIN (
 SELECT "format", "band", COUNT("band") AS "numCategories",
        MIN("lower") AS "lower",
        MAX("upper") AS "upper",
        MIN( CASE WHEN "scale" IS NULL THEN "lower" ELSE NULL END) AS "fillValue",
        MIN((CASE WHEN "scale" < 0 THEN "upper" ELSE "lower" END) * "scale" + "offset") AS "minimum",
        MAX((CASE WHEN "scale" < 0 THEN "lower" ELSE "upper" END) * "scale" + "offset") AS "maximum"
   FROM rasters."Categories" GROUP BY "format", "band") AS "RangeOfSampleDimensions"
     ON "SampleDimensions"."format" = "RangeOfSampleDimensions"."format" AND
        "SampleDimensions"."band"   = "RangeOfSampleDimensions"."band"
   JOIN rasters."Formats" ON "SampleDimensions"."format" = "Formats"."name"
  ORDER BY "SampleDimensions"."format", "SampleDimensions"."band";

COMMENT ON VIEW rasters."RangeOfFormats" IS 'Value range of each raster format.';



---
-- The main list of products. Example are "Sea surface temperature", "Sea surface height", "Wave period", etc.
-- Different products exist for different data producers, regardless if they are observing the same phenomenon.
-- The "spatialResolution" and "temporalResolution" columns duplicate metadata "DataIdentification" attributes,
-- but are repeated here for convenience.
--
CREATE TABLE rasters."Products" (
    "name"               VARCHAR(120) NOT NULL PRIMARY KEY,
    "parent"             VARCHAR(120) REFERENCES rasters."Products" ON UPDATE CASCADE ON DELETE RESTRICT,
    "spatialResolution"  DOUBLE PRECISION CHECK ("spatialResolution"  > 0),
    "temporalResolution" DOUBLE PRECISION CHECK ("temporalResolution" > 0),
    "metadata"           VARCHAR(15) REFERENCES metadata."Metadata" ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE  rasters."Products"                      IS 'Set of data series from the same producer observing the same phenomenon.';
COMMENT ON COLUMN rasters."Products"."name"               IS 'Name of the product.';
COMMENT ON COLUMN rasters."Products"."parent"             IS 'If this product is a sub-product of another product, the parent product.';
COMMENT ON COLUMN rasters."Products"."spatialResolution"  IS 'Approximative resolution in metres. May be blank if unknown.';
COMMENT ON COLUMN rasters."Products"."temporalResolution" IS 'Number of days between rasters. Can be approximate or left blank if not applicable.';
COMMENT ON COLUMN rasters."Products"."metadata"           IS 'Reference to additional information about the product.';
COMMENT ON CONSTRAINT "Products_spatialResolution_check"  ON rasters."Products" IS 'The spatial resolution shall be strictly positive.';
COMMENT ON CONSTRAINT "Products_temporalResolution_check" ON rasters."Products" IS 'The temporal resolution shall be strictly positive.';



--
-- A collection of rasters related by a common heritage adhering to a common specification.
-- Rasters in the same series have at least a common format and are located in a common directory.
--
CREATE TABLE rasters."Series" (
    "identifier" INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    "product"    VARCHAR(120) NOT NULL REFERENCES rasters."Products"  ON UPDATE CASCADE ON DELETE CASCADE,
    "directory"  VARCHAR(120) NOT NULL,
    "extension"  VARCHAR(40),
    "format"     VARCHAR(120) NOT NULL REFERENCES rasters."Formats" ON UPDATE CASCADE ON DELETE RESTRICT,
    "comments"   VARCHAR(1000)
);

CREATE INDEX "Series_index" ON rasters."Series" ("product");

COMMENT ON TABLE  rasters."Series"              IS 'A collection of rasters related by a common heritage adhering to a common specification.';
COMMENT ON COLUMN rasters."Series"."identifier" IS 'Unique identifier (auto-generated).';
COMMENT ON COLUMN rasters."Series"."product"    IS 'The product to which the rasters in the series belong.';
COMMENT ON COLUMN rasters."Series"."directory"  IS 'Relative path to the files in the series. The root path should not be specified if it is machine-dependent.';
COMMENT ON COLUMN rasters."Series"."extension"  IS 'File extention of the rasters in the series. May be blank if the files have no extension.';
COMMENT ON COLUMN rasters."Series"."format"     IS 'Format of the rasters in the series.';
COMMENT ON COLUMN rasters."Series"."comments"   IS 'Free text for comments.';
COMMENT ON INDEX  rasters."Series_index"        IS 'Index of series belonging to a product.';
COMMENT ON CONSTRAINT "Series_product_fkey"  ON rasters."Series" IS 'Each series belongs to a product.';
COMMENT ON CONSTRAINT "Series_format_fkey"   ON rasters."Series" IS 'All the images of a series use the same series.';



--
-- Definition of any grid axis other than the two main axes defined in "GridGeometries".
-- Those axes are usually vertical, but other directions are allowed.
--
CREATE TABLE rasters."AdditionalAxes" (
    "name"      VARCHAR(120)             NOT NULL PRIMARY KEY,
    "datum"     VARCHAR(120)             NOT NULL,
    "direction" metadata."AxisDirection" NOT NULL,
    "units"     VARCHAR(20),
    "bounds"    DOUBLE PRECISION[] NOT NULL
);

COMMENT ON TABLE  rasters."AdditionalAxes"             IS 'Coordinate values along axes other than the two main axes defined in the "GridGeometries" table.';
COMMENT ON COLUMN rasters."AdditionalAxes"."name"      IS 'Name of the grid axis definition.';
COMMENT ON COLUMN rasters."AdditionalAxes"."datum"     IS 'Name of the surface or epoch used as the origin of the coordinate reference system.';
COMMENT ON COLUMN rasters."AdditionalAxes"."direction" IS 'The direction of increasing coordinate values.';
COMMENT ON COLUMN rasters."AdditionalAxes"."units"     IS 'Units of measurement.';
COMMENT ON COLUMN rasters."AdditionalAxes"."bounds"    IS 'Limits of all layers. The array length is the number of layers + 1. The first and last values are the raster bounds along the axis. Other values are interstice between layers.';



--
-- Definition of grid envelopes together with the "grid to CRS" affine conversions.
-- The same grid geometry can be shared by many rasters, especially in time series.
-- Order of affine transform coefficients matches the order of lines in TFW files.
-- Affine transforms shall map cell corners, not centres.
--
CREATE TABLE rasters."GridGeometries" (
    "identifier"   INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    "width"        BIGINT            NOT NULL,
    "height"       BIGINT            NOT NULL,
    "scaleX"       DOUBLE PRECISION  NOT NULL DEFAULT 1,
    "shearY"       DOUBLE PRECISION  NOT NULL DEFAULT 0,
    "shearX"       DOUBLE PRECISION  NOT NULL DEFAULT 0,
    "scaleY"       DOUBLE PRECISION  NOT NULL DEFAULT 1,
    "translateX"   DOUBLE PRECISION  NOT NULL DEFAULT 0,
    "translateY"   DOUBLE PRECISION  NOT NULL DEFAULT 0,
    "approximate"  BOOLEAN           NOT NULL DEFAULT FALSE,
    "srid"         INTEGER           NOT NULL DEFAULT 4326 REFERENCES spatial_ref_sys(srid) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT "GridGeometries_size" CHECK ("width" > 0 AND "height" > 0)
);

SELECT AddGeometryColumn('rasters', 'GridGeometries', 'extent', 4326, 'POLYGON', 2);
ALTER TABLE rasters."GridGeometries" ALTER COLUMN "extent" SET NOT NULL;
ALTER TABLE rasters."GridGeometries" ADD COLUMN "additionalAxes" VARCHAR(120)[];

CREATE INDEX "Extent_index" ON rasters."GridGeometries" USING GIST("extent");


COMMENT ON TABLE  rasters."GridGeometries"                  IS 'Spatial referencing parameters for rasters. Defines the grid envelopes and "grid to CRS" affine conversions to cell corners.';
COMMENT ON COLUMN rasters."GridGeometries"."identifier"     IS 'Unique identifier (auto-generated).';
COMMENT ON COLUMN rasters."GridGeometries"."width"          IS 'Number of cells along the first grid axis.';
COMMENT ON COLUMN rasters."GridGeometries"."height"         IS 'Number of cells along the second grid axis.';
COMMENT ON COLUMN rasters."GridGeometries"."scaleX"         IS 'Element (0,0) of the affine transform. Usually corresponds to the cells resolution along the first grid axis.';
COMMENT ON COLUMN rasters."GridGeometries"."shearY"         IS 'Element (1,0) of the affine transform. Always 0 if there is no rotation.';
COMMENT ON COLUMN rasters."GridGeometries"."shearX"         IS 'Element (0,1) of the affine transform. Always 0 if there is no rotation.';
COMMENT ON COLUMN rasters."GridGeometries"."scaleY"         IS 'Element (1,1) of the affine transform. Usually corresponds to the cells resolution along the second grid axis. This value is often negative since numbering of lines in rasters often increases downwards.';
COMMENT ON COLUMN rasters."GridGeometries"."translateX"     IS 'Element (0,2) of the affine transform. Usually corresponds to the x-coordinate of the top left corner.';
COMMENT ON COLUMN rasters."GridGeometries"."translateY"     IS 'Element (1,2) of the affine transform. Usually corresponds to the y-coordinate of the top left corner.';
COMMENT ON COLUMN rasters."GridGeometries"."approximate"    IS 'Whether the affine transform coefficients are only approximations of a non-linear "grid to CRS" conversion.';
COMMENT ON COLUMN rasters."GridGeometries"."srid"           IS 'Two-dimensional coordinate reference system code.';
COMMENT ON COLUMN rasters."GridGeometries"."extent"         IS 'Two-dimensional shape expressed in a CRS common to all rasters. Computed automatically if none is explicitly defined.';
COMMENT ON COLUMN rasters."GridGeometries"."additionalAxes" IS 'Coordinates in dimensions other than the two main dimentions defined in this table.';
COMMENT ON INDEX  rasters."Extent_index"                    IS 'Index of geometries intersecting a geographical area.';
COMMENT ON CONSTRAINT "GridGeometries_size" ON rasters."GridGeometries" IS 'The dimensions of the rasters must be positive.';



--
-- Function to be applied on new records in the "GridGeometries" table.
-- It computes automatically a new extent from the affine transform coefficients.
--
CREATE FUNCTION rasters."ComputeDefaultExtent"() RETURNS "trigger"
    AS $BODY$
  BEGIN
    IF NEW."extent" IS NULL THEN
      NEW."extent" := st_Transform(st_Affine(st_GeometryFromText(
        'POLYGON((0 0,0 ' || NEW."height" || ',' || NEW."width" || ' ' || NEW."height" || ',' || NEW."width" || ' 0,0 0))',
        NEW."srid"), NEW."scaleX", NEW."shearX", NEW."shearY", NEW."scaleY", NEW."translateX", NEW."translateY"), 4326);
    END IF;
    RETURN NEW;
  END;
$BODY$
    LANGUAGE plpgsql;

CREATE TRIGGER "addDefaultExtent"
    BEFORE INSERT OR UPDATE ON rasters."GridGeometries"
    FOR EACH ROW
    EXECUTE PROCEDURE rasters."ComputeDefaultExtent"();

COMMENT ON TRIGGER "addDefaultExtent" ON rasters."GridGeometries" IS 'Add an envelope by default if none is explicitly defined.';



--
-- The bounding boxes of all rasters, for information purpose.
-- Inner queries: "Corners" contains 4 (x,y) corners as 4 rows for each grid geometries.
--                "NativeBoxes" contains the minimum and maximum values of "Corners".
--
CREATE VIEW rasters."BoundingBoxes" AS
    SELECT "GridGeometries"."identifier", "width", "height",
           "srid" AS "crs", "minX", "maxX", "minY", "maxY",
           st_xmin("extent") AS "west",
           st_xmax("extent") AS "east",
           st_ymin("extent") AS "south",
           st_ymax("extent") AS "north"
      FROM rasters."GridGeometries"
 LEFT JOIN (SELECT "identifier",
       MIN("x") AS "minX",
       MAX("x") AS "maxX",
       MIN("y") AS "minY",
       MAX("y") AS "maxY"
FROM (SELECT "identifier",
             "translateX" AS "x",
             "translateY" AS "y" FROM rasters."GridGeometries"
UNION SELECT "identifier",
             "width"*"scaleX" + "translateX" AS "x",
             "width"*"shearY" + "translateY" AS "y" FROM rasters."GridGeometries"
UNION SELECT "identifier",
             "height"*"shearX" + "translateX" AS "x",
             "height"*"scaleY" + "translateY" AS "y" FROM rasters."GridGeometries"
UNION SELECT "identifier",
             "width"*"scaleX" + "height"*"shearX" + "translateX" AS "x",
             "width"*"shearY" + "height"*"scaleY" + "translateY" AS "y" FROM rasters."GridGeometries") AS "Corners"
    GROUP BY "identifier") AS "NativeBoxes"
          ON "GridGeometries"."identifier" = "NativeBoxes"."identifier"
    ORDER BY "identifier";


COMMENT ON VIEW rasters."BoundingBoxes" IS 'Comparison between the calculated envelopes and the declared envelopes.';



--
-- The main table listing all rasters.
--
CREATE TABLE rasters."GridCoverages" (
    "series"    INTEGER           NOT NULL REFERENCES rasters."Series" ON UPDATE CASCADE ON DELETE CASCADE,
    "filename"  VARCHAR(200)      NOT NULL,
    "index"     SMALLINT          NOT NULL DEFAULT 1 CHECK ("index" >= 1),
    "time"      TSRANGE,
    "grid"      INTEGER           NOT NULL REFERENCES rasters."GridGeometries" ON UPDATE CASCADE ON DELETE RESTRICT,
    PRIMARY KEY ("series", "filename", "index")
);

CREATE INDEX "GridCoverages_search_index" ON rasters."GridCoverages" ("series", "grid");
CREATE INDEX "GridCoverages_time_index"   ON rasters."GridCoverages" USING GIST ("time");

COMMENT ON TABLE  rasters."GridCoverages"              IS 'List of all the rasters available. Each line corresponds to a raster file.';
COMMENT ON COLUMN rasters."GridCoverages"."series"     IS 'Series to which the raster belongs.';
COMMENT ON COLUMN rasters."GridCoverages"."filename"   IS 'File name of the raster, relative to the directory specified in the series.';
COMMENT ON COLUMN rasters."GridCoverages"."index"      IS 'Index of the raster in the file (for files containing multiple rasters). Numbered from 1.';
COMMENT ON COLUMN rasters."GridCoverages"."time"       IS 'Date and time of the raster acquisition, in UTC.';
COMMENT ON COLUMN rasters."GridCoverages"."grid"       IS 'Grid Geomerty that defines the spatial footprint of this coverage.';
COMMENT ON INDEX  rasters."GridCoverages_search_index" IS 'Index of all the rasters in a geographic region.';
COMMENT ON INDEX  rasters."GridCoverages_time_index"   IS 'Index of rasters temporal extent.';
COMMENT ON CONSTRAINT "GridCoverages_series_fkey" ON rasters."GridCoverages" IS 'Each raster belongs to a series.';
COMMENT ON CONSTRAINT "GridCoverages_grid_fkey"   ON rasters."GridCoverages" IS 'Each raster must have a spatial extent.';
COMMENT ON CONSTRAINT "GridCoverages_index_check" ON rasters."GridCoverages" IS 'The raster index must be positive.';



--
-- Creates the "DomainOfSeries" and "DomainOfProducts" views.
-- Dependencies: "GridCoverages", "BoundingBoxes", "Series".
--
CREATE VIEW rasters."DomainOfSeries" AS
    SELECT "TimeRanges"."series", "startTime", "endTime",
           "west", "east", "south", "north", "xResolution", "yResolution"
      FROM
   (SELECT "series",
           MIN(LOWER("time")) AS "startTime",
           MAX(UPPER("time")) AS "endTime"
      FROM rasters."GridCoverages" GROUP BY "series") AS "TimeRanges"
      JOIN
   (SELECT "series",
           MIN("west")  AS "west",
           MAX("east")  AS "east",
           MIN("south") AS "south",
           MAX("north") AS "north",
           MIN(("east"  - "west" ) / "width" ) AS "xResolution",
           MIN(("north" - "south") / "height") AS "yResolution"
      FROM (SELECT DISTINCT "series", "grid" FROM rasters."GridCoverages") AS "Extents"
 LEFT JOIN rasters."BoundingBoxes" ON "Extents"."grid" = "BoundingBoxes"."identifier"
  GROUP BY "series") AS "BoundingBoxRanges" ON "TimeRanges".series = "BoundingBoxRanges".series
  ORDER BY "series";

CREATE VIEW rasters."DomainOfProducts" AS
 SELECT "product",
        MIN("startTime")   AS "startTime",
        MAX("endTime")     AS "endTime",
        MIN("west")        AS "west",
        MAX("east")        AS "east",
        MIN("south")       AS "south",
        MAX("north")       AS "north",
        MIN("xResolution") AS "xResolution",
        MIN("yResolution") AS "yResolution"
   FROM rasters."DomainOfSeries"
   JOIN rasters."Series" ON "DomainOfSeries"."series" = "Series"."identifier"
  GROUP BY "product"
  ORDER BY "product";


COMMENT ON VIEW rasters."DomainOfSeries"   IS 'List of geographical areas used by each sub-series.';
COMMENT ON VIEW rasters."DomainOfProducts" IS 'Number of rasters and geographical area for each product used.';
