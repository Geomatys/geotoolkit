--------------------------------------------------------------------------------------------------
-- Deletes the tables in the coverages database schema.                                         --
-- Intentionally fails if the schema contains objects which                                     --
-- were not created by the "coverages-create.sql" script.                                       --
--                                                                                              --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';
SET search_path = coverages, metadata, postgis;

DROP VIEW     "DomainOfLayers";
DROP VIEW     "DomainOfSeries";
DROP VIEW     "DomainOfTiles";
DROP VIEW     "Tiling";
DROP TABLE    "Tiles";
DROP TABLE    "GridCoverages";
DROP VIEW     "BoundingBoxes";
DROP FUNCTION "ComputeDefaultExtent"() CASCADE;
SELECT DropGeometryColumn('GridGeometries', 'horizontalExtent');
DROP TABLE    "GridGeometries";
DROP TABLE    "Series";
DROP TABLE    "Layers";
DROP VIEW     "RangeOfFormats";
DROP TABLE    "Categories";
DROP TYPE     "TransferFunctionType";
DROP TABLE    "SampleDimensions";
DROP TABLE    "Formats";
DROP TYPE     "PackMode";
DROP SCHEMA   coverages;
