---
--- Migrates a database from the legacy PostGrid schema to the new
--- schema defined in the geotk-coverage-sql module version 3.16.
---
--- The "pom.xml" configuration excludes this SQL script from the JAR file, because the
--- legacy PostGrid schema was not in wide use enough. This file is keep in the source
--- code repository for historical purpose.
---
--- ------------------------------------------------------------------------------------------------
---
--- USAGE:
---
--- 1) Create an initially empty database using the new schema, as described below:
---    http://www.geotoolkit.org/modules/display/geotk-wizards-swing/CoverageDatabaseInstaller.html
---
--- 2) Copy the old data in the new database, in a schema called "postgrid". This copy can be
---    performed by dumping the "postgrid" schema from the old database (ONLY "postgrid" schema;
---    if the schema containing the "GridCoverages" table is not named that way, rename it), then
---    restoring the dump in the new database.
---
--- 3) Execute this script.
---
--- 4) Delete the temporary "postgrid" schema from the new database.
---

--- Formats ----------------------------------------------------------------------------------------
INSERT INTO coverages."Formats" ("name", "plugin", "packMode", "comments")
SELECT "name", "mime", "encoding"::coverages."PackMode", "comment" FROM postgrid."Formats" WHERE "name" <> 'PNG' AND "name" <> 'TIFF';

UPDATE coverages."Formats" SET "plugin"='PNG'    WHERE "plugin" LIKE 'image/png%';
UPDATE coverages."Formats" SET "plugin"='RAW'    WHERE "plugin" LIKE 'image/raw%';
UPDATE coverages."Formats" SET "plugin"='NetCDF' WHERE "plugin" LIKE 'image/x-netcdf%';

--- SampleDimensions -------------------------------------------------------------------------------
INSERT INTO coverages."SampleDimensions" ("format", "band", "name", "units")
SELECT "format", "band", "identifier", "units" FROM postgrid."SampleDimensions";

--- Categories -------------------------------------------------------------------------------------
INSERT INTO coverages."Categories" ("format", "band", "name", "lower", "upper", "c0", "c1", "function", "colors")
(SELECT "format", "SampleDimensions"."band", "name", "lower", "upper", "c0", "c1", "function"::metadata."MI_TransferFunctionTypeCode", "colors" FROM postgrid."Categories"
JOIN postgrid."SampleDimensions" ON "Categories"."band" = "SampleDimensions"."identifier");

--- Layers -----------------------------------------------------------------------------------------
INSERT INTO coverages."Layers" ("name", "period", "minScale", "maxScale", "fallback", "comments")
SELECT "name", "period", "minScale", "maxScale", "fallback", "description" FROM postgrid."Layers";

--- Series -----------------------------------------------------------------------------------------
CREATE TABLE postgrid."SeriesID" (
    "name" character varying NOT NULL PRIMARY KEY,
    "identifier" serial NOT NULL UNIQUE
);

INSERT INTO postgrid."SeriesID" ("name") SELECT "identifier" FROM postgrid."Series";

INSERT INTO coverages."Series" ("identifier", "layer", "pathname", "extension", "format", "quicklook")
SELECT main."identifier", "layer", "pathname", "extension", "format", fb."identifier" FROM postgrid."Series"
JOIN postgrid."SeriesID" AS main ON "Series"."identifier" = main."name"
LEFT JOIN postgrid."SeriesID" AS fb ON "quicklook" = fb."name";

--- GridGeometries ---------------------------------------------------------------------------------
CREATE TABLE postgrid."GridGeometriesID" (
    "name" character varying NOT NULL PRIMARY KEY,
    "identifier" serial NOT NULL UNIQUE
);

INSERT INTO postgrid."GridGeometriesID" ("name") SELECT "identifier" FROM postgrid."GridGeometries";

INSERT INTO coverages."GridGeometries" ("identifier", "width", "height", "scaleX", "shearY", "shearX", "scaleY", "translateX", "translateY", "horizontalSRID", "verticalSRID", "verticalOrdinates")
SELECT "GridGeometriesID"."identifier", "width", "height", "scaleX", "shearY", "shearX", "scaleY", "translateX", "translateY", "horizontalSRID", "verticalSRID", "verticalOrdinates"
FROM postgrid."GridGeometries" JOIN postgrid."GridGeometriesID" ON "GridGeometries"."identifier" = "GridGeometriesID"."name";

--- GridCoverages ----------------------------------------------------------------------------------
INSERT INTO coverages."GridCoverages" ("series", "filename", "index", "startTime", "endTime", "extent")
SELECT "SeriesID"."identifier", "filename", "index", "startTime", "endTime", "GridGeometriesID"."identifier" FROM ONLY postgrid."GridCoverages"
JOIN postgrid."SeriesID" ON "GridCoverages"."series" = "SeriesID"."name"
JOIN postgrid."GridGeometriesID" ON "GridCoverages"."extent" = "GridGeometriesID"."name";

--- Tiles ------------------------------------------------------------------------------------------
INSERT INTO coverages."Tiles" ("series", "filename", "index", "startTime", "endTime", "extent", "dx", "dy")
SELECT "SeriesID"."identifier", "filename", "index", "startTime", "endTime", "GridGeometriesID"."identifier", "dx", "dy" FROM postgrid."Tiles"
JOIN postgrid."SeriesID" ON "Tiles"."series" = "SeriesID"."name"
JOIN postgrid."GridGeometriesID" ON "Tiles"."extent" = "GridGeometriesID"."name";
