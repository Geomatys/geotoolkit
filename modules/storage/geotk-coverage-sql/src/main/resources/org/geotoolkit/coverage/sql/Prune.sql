--
-- Delete unused entries.
--
DELETE FROM rasters."Formats" WHERE "name" IN
  (SELECT "name" FROM rasters."Formats" EXCEPT SELECT "format" FROM rasters."Series");

DELETE FROM rasters."GridGeometries" WHERE "identifier" IN
  (SELECT "identifier" FROM rasters."GridGeometries" EXCEPT
    (SELECT "grid" FROM rasters."GridCoverages" UNION
     SELECT "exportedGrid" FROM rasters."Products"));

DELETE FROM rasters."AdditionalAxes" WHERE "name" IN
  (SELECT "name" FROM rasters."AdditionalAxes" EXCEPT
   SELECT UNNEST("additionalAxes") FROM rasters."GridGeometries");
