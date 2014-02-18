--------------------------------------------------------------------------------------------------
-- Test data for the catalog of coverages.                                                      --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';
SET search_path = coverages, postgis;



--------------------------------------------------------------------------------------------------
-- Additional Coordinate Reference Systems                                                      --
--------------------------------------------------------------------------------------------------
INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) VALUES
-- Intentionally different SRID.
 (6000, 'EPSG', 57150, 'VERT_CS["mean sea level depth",VERT_DATUM["Mean Sea Level",2005],UNIT["m",1.0],AXIS["Gravity-related depth",DOWN]]', NULL),
 (6001, 'EPSG(km)', 3395,
  'PROJCS["WGS 84 / World Mercator",GEOGCS["WGS 84",DATUM["WGS_1984",SPHEROID["WGS 84",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["degree",0.01745329251994328],AUTHORITY["EPSG","4326"]],PROJECTION["Mercator_1SP"],UNIT["km",1000]]',
  '+proj=merc +lon_0=0 +k=1.000000 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=km +no_defs');



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
 ('SST (World - 8 days)', 8);

INSERT INTO "Series" ("identifier", "layer", "pathname", "extension", "format") VALUES
 (100, 'SST (World - 8 days)', 'World/SST/8-days', 'png', 'PNG Temperature [-3 … 32.25]°C');

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
 ('Mars (u,v)', 1, 'U component', 1, 255, -1.925, 0.015, 'white-cyan-red'),
 ('Mars (u,v)', 2, 'V component', 1, 255, -1.925, 0.015, 'white-cyan-red');

INSERT INTO "Layers" ("name", "period") VALUES
 ('Mars (u,v)', 0.0104166666666667);

INSERT INTO "Series" ("identifier", "layer", "pathname", "extension", "format") VALUES
 (210, 'Mars (u,v)', 'Iroise', 'nc', 'Mars (u,v)');

INSERT INTO "GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "horizontalSRID") VALUES
 (210, 273, 423, 0.00404610002742094, -0.00269910622547023, -5.34111768883817, 48.7947150316284, 4326);

INSERT INTO "GridCoverages" ("series", "filename", "index", "startTime", "endTime", "extent") VALUES
 (210, 'champs.r3_23-05-2007', 1, '2007-05-21 23:52:30', '2007-05-22 00:07:30', 210),
 (210, 'champs.r3_23-05-2007', 2, '2007-05-22 00:07:30', '2007-05-22 00:22:30', 210),
 (210, 'champs.r3_23-05-2007', 3, '2007-05-22 00:22:30', '2007-05-22 00:37:30', 210),
 (210, 'champs.r3_23-05-2007', 4, '2007-05-22 00:37:30', '2007-05-22 00:52:30', 210);



--------------------------------------------------------------------------------------------------
-- Coriolis data from IFREMER                                                                   --
--------------------------------------------------------------------------------------------------
INSERT INTO "Formats" ("name", "plugin", "packMode") VALUES
 ('Coriolis (temperature)', 'NetCDF', 'packed'),
 ('Coriolis (salinity)',    'NetCDF', 'packed');

INSERT INTO "SampleDimensions" ("format", "name", "units") VALUES
 ('Coriolis (temperature)', 'Temperature', '°C'),
 ('Coriolis (salinity)',    'Salinity',    '');

INSERT INTO "Categories" ("format", "name", "lower", "upper", "c0", "c1", "colors") VALUES
 ('Coriolis (temperature)', 'Missing value', 0, 0, NULL, NULL, NULL),
 ('Coriolis (temperature)', 'Temperature', 1, 43001, -3.001, 0.001, 'rainbow-t'),
 ('Coriolis (salinity)',    'Missing value', 0, 0, NULL, NULL, NULL),
 ('Coriolis (salinity)',    'Salinity', 1, 60001, -0.001, 0.001, 'rainbow');

INSERT INTO "Layers" ("name", "period") VALUES
 ('Coriolis (temperature)', 7);

INSERT INTO "Series" ("identifier", "layer", "pathname", "extension", "format") VALUES
 (200, 'Coriolis (temperature)', 'World/Coriolis', 'nc', 'Coriolis (temperature)');

INSERT INTO "GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "horizontalSRID", "verticalSRID", "verticalOrdinates") VALUES
 (200, 720, 499, 55659.7453966368, -55381.103129, -20037508.342789, 13817585.230755, 3395, 5714,
 '{5,10,20,30,40,50,60,80,100,120,140,160,180,200,220,240,260,280,300,320,360,400,440,480,520,560,600,640,680,720,760,800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1650,1700,1750,1800,1850,1900,1950}');

INSERT INTO "GridCoverages" ("series", "filename", "startTime", "endTime", "extent") VALUES
 (200, 'OA_RTQCGL01_20070606_FLD_TEMP', '2007-06-03', '2007-06-10', 200),
 (200, 'OA_RTQCGL01_20070613_FLD_TEMP', '2007-06-10', '2007-06-17', 200),
 (200, 'OA_RTQCGL01_20070620_FLD_TEMP', '2007-06-17', '2007-06-24', 200);



--------------------------------------------------------------------------------------------------
-- Blue Marble from NASA                                                                        --
--------------------------------------------------------------------------------------------------
INSERT INTO "Layers" ("name") VALUES
 ('BlueMarble');

INSERT INTO "Series" ("identifier", "layer", "pathname", "extension", "format") VALUES
 (300, 'BlueMarble', 'World/BlueMarble', 'png', 'PNG');

INSERT INTO "GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY") VALUES
 (313,  480,  240, 0.75,   -0.75,   -180, 90),
 (312,  480,  480, 0.375,  -0.375,  -180, 90),
 (311,  480,  480, 0.1875, -0.1875, -180, 90),
 (310,  480,  480, 0.125,  -0.125,  -180, 90),
 (300, 2880, 1440, 0.125,  -0.125,  -180, 90),
 --- The following line describes the finest resolution that we
 --- would had if every tiles were included in our test directory.
 (333, 86400, 43200, 0.00416666666666667, -0.00416666666666667, -180, 90);

INSERT INTO "GridCoverages" ("series", "filename", "extent") VALUES
 (300, 'BlueMarble', 300);

INSERT INTO "Tiles" ("series", "filename", "extent", "dx", "dy") VALUES
 (300, 'L13_A1', 313,    0,   0),
 (300, 'L12_B1', 312, 480,    0),
 (300, 'L12_A1', 312,    0,   0),
 (300, 'L11_D2', 311, 1440, 480),
 (300, 'L11_D1', 311, 1440,   0),
 (300, 'L11_C2', 311,  960, 480),
 (300, 'L11_C1', 311,  960,   0),
 (300, 'L11_B2', 311,  480, 480),
 (300, 'L11_B1', 311,  480,   0),
 (300, 'L11_A2', 311,    0, 480),
 (300, 'L11_A1', 311,    0,   0),
 (300, 'L10_F3', 310, 2400, 960),
 (300, 'L10_F2', 310, 2400, 480),
 (300, 'L10_F1', 310, 2400,   0),
 (300, 'L10_E3', 310, 1920, 960),
 (300, 'L10_E2', 310, 1920, 480),
 (300, 'L10_E1', 310, 1920,   0),
 (300, 'L10_D3', 310, 1440, 960),
 (300, 'L10_D2', 310, 1440, 480),
 (300, 'L10_D1', 310, 1440,   0),
 (300, 'L10_C3', 310,  960, 960),
 (300, 'L10_C2', 310,  960, 480),
 (300, 'L10_C1', 310,  960,   0),
 (300, 'L10_B3', 310,  480, 960),
 (300, 'L10_B2', 310,  480, 480),
 (300, 'L10_B1', 310,  480,   0),
 (300, 'L10_A3', 310,    0, 960),
 (300, 'L10_A2', 310,    0, 480),
 (300, 'L10_A1', 310,    0,   0);



--------------------------------------------------------------------------------------------------
-- Initially empty layer for testing write operations                                           --
--------------------------------------------------------------------------------------------------
INSERT INTO "Layers" ("name") VALUES
 ('WriterTest');
