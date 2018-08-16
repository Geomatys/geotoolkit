--
-- Test data for the org.geotoolkit.coverage.sql package.
--

--
-- Temperature data from PNG images. Those images do not have any geospatial metadata in their header.
-- Consequently they can be used only if those information are encoded elsewhere, like in this database.
--
INSERT INTO rasters."Formats" ("name", "driver") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 'Image:PNG');

INSERT INTO rasters."SampleDimensions" ("format", "band", "identifier", "units", "isPacked") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 1, 'SST [-3 … 32.25°C]', '°C', TRUE);

INSERT INTO rasters."Categories" ("format", "band", "name", "lower", "upper", "scale", "offset", "colors", "function") VALUES
 ('PNG Temperature [-3 … 32.25]°C', 1, 'Missing value', 0, 0, NULL, NULL, NULL, NULL),
 ('PNG Temperature [-3 … 32.25]°C', 1, 'Temperature', 1, 255, 0.15, -3, 'rainbow', 'linear');

INSERT INTO rasters."Products" ("name", "temporalResolution") VALUES
 ('SST (World - 8 days)', 8);

INSERT INTO rasters."Series" ("identifier", "product", "directory", "extension", "format") VALUES
 (100, 'SST (World - 8 days)', 'World/SST/8-days', 'png', 'PNG Temperature [-3 … 32.25]°C');

INSERT INTO rasters."GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "srid") VALUES
 (100, 4096, 2048, 0.087890625, -0.087890625, -180, 90, 4326);

INSERT INTO rasters."GridCoverages" ("series", "filename", "startTime", "endTime", "grid") VALUES
 (100, '198601', '1986-01-01', '1986-01-09', 100),
 (100, '198602', '1986-01-09', '1986-01-17', 100),
 (100, '198603', '1986-01-17', '1986-01-25', 100),
 (100, '198604', '1986-01-25', '1986-02-02', 100),
 (100, '198605', '1986-02-02', '1986-02-10', 100),
 (100, '198606', '1986-02-10', '1986-02-18', 100),
 (100, '198607', '1986-02-18', '1986-02-26', 100);



--
-- Geostrophic current data from text files, where the same file contains data at different time.
-- The values are already "real" values and do not need application of a scale factor and offset.
--
INSERT INTO rasters."Formats" ("name", "driver") VALUES
 ('Mars (u,v)', 'NetCDF');

INSERT INTO rasters."SampleDimensions" ("format", "band", "identifier", "units", "isPacked") VALUES
 ('Mars (u,v)', 1, 'U', 'm/s', FALSE),
 ('Mars (u,v)', 2, 'V', 'm/s', FALSE);

INSERT INTO rasters."Categories" ("format", "band", "name", "lower", "upper", "scale", "offset", "colors", "function") VALUES
 ('Mars (u,v)', 1, 'Missing value', 0, 0, NULL, NULL, NULL, NULL),
 ('Mars (u,v)', 2, 'Missing value', 0, 0, NULL, NULL, NULL, NULL),
 ('Mars (u,v)', 1, 'U component', 1, 255, 0.015, -1.925, 'white-cyan-red', 'linear'),
 ('Mars (u,v)', 2, 'V component', 1, 255, 0.015, -1.925, 'white-cyan-red', 'linear');

INSERT INTO rasters."Products" ("name", "temporalResolution") VALUES
 ('Mars (u,v)', 0.0104166666666667);

INSERT INTO rasters."Series" ("identifier", "product", "directory", "extension", "format") VALUES
 (210, 'Mars (u,v)', 'Iroise', 'nc', 'Mars (u,v)');

INSERT INTO rasters."GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "srid") VALUES
 (210, 273, 423, 0.00404610002742094, -0.00269910622547023, -5.34111768883817, 48.7947150316284, 4326);

INSERT INTO rasters."GridCoverages" ("series", "filename", "index", "startTime", "endTime", "grid") VALUES
 (210, 'champs.r3_23-05-2007', 1, '2007-05-21 23:52:30', '2007-05-22 00:07:30', 210),
 (210, 'champs.r3_23-05-2007', 2, '2007-05-22 00:07:30', '2007-05-22 00:22:30', 210),
 (210, 'champs.r3_23-05-2007', 3, '2007-05-22 00:22:30', '2007-05-22 00:37:30', 210),
 (210, 'champs.r3_23-05-2007', 4, '2007-05-22 00:37:30', '2007-05-22 00:52:30', 210);



--
-- Coriolis data from IFREMER.
--
INSERT INTO rasters."Formats" ("name", "driver") VALUES
 ('Coriolis (temperature)', 'NetCDF'),
 ('Coriolis (salinity)',    'NetCDF');

INSERT INTO rasters."SampleDimensions" ("format", "identifier", "units", "isPacked") VALUES
 ('Coriolis (temperature)', 'Temperature', '°C', TRUE),
 ('Coriolis (salinity)',    'Salinity',    '',   TRUE);

INSERT INTO rasters."Categories" ("format", "name", "lower", "upper", "scale", "offset", "colors", "function") VALUES
 ('Coriolis (temperature)', 'Missing value', 0, 0, NULL, NULL, NULL, NULL),
 ('Coriolis (salinity)',    'Missing value', 0, 0, NULL, NULL, NULL, NULL),
 ('Coriolis (temperature)', 'Temperature',   1, 43001, 0.001, -3.001, 'rainbow-t', 'linear'),
 ('Coriolis (salinity)',    'Salinity',      1, 60001, 0.001, -0.001, 'rainbow',   'linear');

INSERT INTO rasters."Products" ("name", "temporalResolution") VALUES
 ('Coriolis (temperature)', 7);

INSERT INTO rasters."Series" ("identifier", "product", "directory", "extension", "format") VALUES
 (200, 'Coriolis (temperature)', 'World/Coriolis', 'nc', 'Coriolis (temperature)');

INSERT INTO rasters."AdditionalAxes" ("name", "datum", "direction", "units", "bounds") VALUES
 ('Coriolis depth', 'Mean Sea Level', 'down', 'm',
  '{5,10,20,30,40,50,60,80,100,120,140,160,180,200,220,240,260,280,300,320,360,400,440,480,520,560,600,640,680,720,760,800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1650,1700,1750,1800,1850,1900,1950}');
-- TODO: above values are at center instead than bounds. Need to be updated.

INSERT INTO rasters."GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY", "srid", "additionalAxes") VALUES
 (200, 720, 499, 55659.7453966368, -55381.103129, -20037508.342789, 13817585.230755, 3395, '{Coriolis depth}');

INSERT INTO rasters."GridCoverages" ("series", "filename", "startTime", "endTime", "grid") VALUES
 (200, 'OA_RTQCGL01_20070606_FLD_TEMP', '2007-06-03', '2007-06-10', 200),
 (200, 'OA_RTQCGL01_20070613_FLD_TEMP', '2007-06-10', '2007-06-17', 200),
 (200, 'OA_RTQCGL01_20070620_FLD_TEMP', '2007-06-17', '2007-06-24', 200);



--
-- Blue Marble from NASA.
--
INSERT INTO rasters."Products" ("name") VALUES
 ('BlueMarble');

INSERT INTO rasters."Series" ("identifier", "product", "directory", "extension", "format") VALUES
 (300, 'BlueMarble', 'World/BlueMarble', 'png', 'PNG');

INSERT INTO rasters."GridGeometries" ("identifier", "width", "height", "scaleX", "scaleY", "translateX", "translateY") VALUES
 (313,  480,  240, 0.75,   -0.75,   -180, 90),
 (312,  480,  480, 0.375,  -0.375,  -180, 90),
 (311,  480,  480, 0.1875, -0.1875, -180, 90),
 (310,  480,  480, 0.125,  -0.125,  -180, 90),
 (300, 2880, 1440, 0.125,  -0.125,  -180, 90),
 --- The following line describes the finest resolution that we
 --- would had if every tiles were included in our test directory.
 (333, 86400, 43200, 0.00416666666666667, -0.00416666666666667, -180, 90);

INSERT INTO rasters."GridCoverages" ("series", "filename", "grid") VALUES
 (300, 'BlueMarble', 300);



--
-- Initially empty product for testing write operations.
--
INSERT INTO rasters."Products" ("name") VALUES
 ('WriterTest');
