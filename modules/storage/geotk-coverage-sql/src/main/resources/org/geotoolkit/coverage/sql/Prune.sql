--
-- Delete unused entries.
--
DELETE FROM rasters."GridGeometries" WHERE "identifier" IN
  (SELECT "identifier" FROM rasters."GridGeometries" EXCEPT SELECT "extent" FROM rasters."GridCoverages");
