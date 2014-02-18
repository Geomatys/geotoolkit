--
-- Update the PostGIS schema.
--
--
-- ==========================================================
-- ============  NOTE ON APPARENT POSTGIS ERROR  ============
-- ==========================================================
-- The PostGIS "spatial_ref_sys" table seems to have an error in declaration of Lambert projections
-- used in France.  The prime meridian (Paris) should be declared in gradians, not degrees, because
-- gradians is the unit declared in the UNIT["grad",0.01570796326794897] element.  Consequently the
-- value for PRIMEM["Paris"] should be 2.5969213, not 2.33722917. This apply to EPSG:27572 and other
-- CRS similar to it.
--

SET client_encoding = 'UTF8';
SET search_path = postgis, public;


--
-- Fixes the Paris prime meridian (see comment above). Value relative to Greenwich is
-- converted from degrees to gradians everywhere the unit is expected to be gradians.
--
UPDATE spatial_ref_sys SET srtext = REPLACE(srtext,
    'PRIMEM["Paris",2.33722917,',
    'PRIMEM["Paris",2.5969213,')
  WHERE srtext LIKE
    '%PRIMEM["Paris",2.33722917,%UNIT["grad"%';


--
-- Adds an optional column for specifying whatever or not a PostGIS
-- definition should have precedence over the EPSG definition.
--
ALTER TABLE spatial_ref_sys ADD COLUMN "override" boolean DEFAULT FALSE;
UPDATE spatial_ref_sys SET "override" = FALSE;
ALTER TABLE spatial_ref_sys ALTER COLUMN "override" SET NOT NULL;
COMMENT ON COLUMN spatial_ref_sys."override" IS
    'Set to TRUE if the definition in the "srtext" column should override the definition found in the EPSG database.';


--
-- Some vertical CRS from the EPSG database.
--
INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext) VALUES
  (5798, 'EPSG', 5798, 'VERT_CS["EGM84 geoid",VERT_DATUM["EGM84 geoid",2005,AUTHORITY["EPSG","5203"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5798"]]'),
  (5773, 'EPSG', 5773, 'VERT_CS["EGM96 geoid",VERT_DATUM["EGM96 geoid",2005,AUTHORITY["EPSG","5171"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5773"]]'),
  (5714, 'EPSG', 5714, 'VERT_CS["mean sea level height",VERT_DATUM["Mean Sea Level",2005,AUTHORITY["EPSG","5100"]],UNIT["m",1.0],AXIS["Gravity-related height",UP],AUTHORITY["EPSG","5714"]]'),
  (5715, 'EPSG', 5715, 'VERT_CS["mean sea level depth",VERT_DATUM["Mean Sea Level",2005,AUTHORITY["EPSG","5100"]],UNIT["m",1.0],AXIS["Gravity-related depth",DOWN],AUTHORITY["EPSG","5715"]]');


--
-- Grants read-only access to everyone.
--
GRANT SELECT ON TABLE spatial_ref_sys TO public;
