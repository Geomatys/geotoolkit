--
-- Update PostGIS schema with some vertical CRS from the EPSG database.
--
INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext) VALUES
  (5798, 'EPSG', 5798, 'VERT_CS["EGM84 geoid",VERT_DATUM["EGM84 geoid",2005,AUTHORITY["EPSG","5203"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5798"]]'),
  (5773, 'EPSG', 5773, 'VERT_CS["EGM96 geoid",VERT_DATUM["EGM96 geoid",2005,AUTHORITY["EPSG","5171"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5773"]]'),
  (5714, 'EPSG', 5714, 'VERT_CS["mean sea level height",VERT_DATUM["Mean Sea Level",2005,AUTHORITY["EPSG","5100"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5714"]]'),
  (5715, 'EPSG', 5715, 'VERT_CS["mean sea level depth",VERT_DATUM["Mean Sea Level",2005,AUTHORITY["EPSG","5100"]],UNIT["m",1.0],AXIS["Gravity-related depth",DOWN],AUTHORITY["EPSG","5715"]]');
