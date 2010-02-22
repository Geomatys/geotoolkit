--------------------------------------------------------------------------------------------------
-- Test data for the catalog of coverages.                                                      --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';
SET search_path = coverages, postgis;



--------------------------------------------------------------------------------------------------
-- Temperature data                                                                             --
--------------------------------------------------------------------------------------------------
INSERT INTO "Formats" ("name", "plugin", "packMode") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 'PNG', 'native');

INSERT INTO "SampleDimensions" ("format", "band", "name", "units") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 1, 'SST [-3 … 32.25°C]', '°C');

INSERT INTO "Categories" ("format", "band", "name", "lower", "upper", "c0", "c1", "colors") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 1, 'Missing value', 0, 0, NULL, NULL, NULL),
 ('PNG Temperature [-3 … 32.25]°C', 1, 'Temperature', 1, 255, -3, 0.15, 'rainbow');

INSERT INTO "Layers" ("name", "period") VALUES
 ('SST (World - weekly)', 8);

INSERT INTO "Series" ("identifier", "layer", "pathname", "extension", "format") VALUES
 (100, 'SST (World - weekly)', 'World/SST/8-days', 'png', 'PNG Temperature [-3 … 32.25]°C');

INSERT INTO "GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "horizontalSRID") VALUES
 (100, 4096, 2048, 0.087890625, -0.087890625, -180, 90, 4326);

INSERT INTO "GridCoverages" ("series", "filename", "startTime", "endTime", "extent") VALUES
 (100, '198601', '1986-01-01', '1986-01-09', 100),
 (100, '198602', '1986-01-09', '1986-01-17', 100),
 (100, '198603', '1986-01-17', '1986-01-25', 100),
 (100, '198604', '1986-01-25', '1986-02-02', 100),
 (100, '198605', '1986-02-02', '1986-02-10', 100),
 (100, '198606', '1986-02-10', '1986-02-18', 100),
 (100, '198607', '1986-02-18', '1986-02-26', 100);



--------------------------------------------------------------------------------------------------
-- Geostrophic current data                                                                     --
--------------------------------------------------------------------------------------------------
INSERT INTO "Formats" ("name", "plugin", "packMode") VALUES
 ('Mars (u,v)', 'NetCDF', 'geophysics');

INSERT INTO "SampleDimensions" ("format", "band", "name", "units") VALUES
 ('Mars (u,v)', 1, 'U', 'm/s'),
 ('Mars (u,v)', 2, 'V', 'm/s');

INSERT INTO "Categories" ("format", "band", "name", "lower", "upper", "c0", "c1", "colors") VALUES
 ('Mars (u,v)', 1, 'Missing value', 0, 0, NULL, NULL, NULL),
 ('Mars (u,v)', 2, 'Missing value', 0, 0, NULL, NULL, NULL),
 ('Mars (u,v)', 1, 'U component', 1, 255, -1.75, 0.01, 'white-cyan-red'),
 ('Mars (u,v)', 2, 'V component', 1, 255, -1.75, 0.01, 'white-cyan-red');
