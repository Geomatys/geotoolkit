--------------------------------------------------------------------------------------------------
-- Delete unused entries.                                                                       --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';
SET search_path = coverages, postgis;

DELETE FROM "GridGeometries" WHERE "identifier" IN
  (SELECT "identifier" FROM "GridGeometries" EXCEPT SELECT "extent" FROM "GridCoverages");
