--------------------------------------------------------------------------------------------------
-- A schema for ISO 19115-2 metadata.                                                           --
--  (C) 2010, Open Source Geospatial Foundation (OSGeo)                                         --
--  (C) 2010, Geomatys                                                                          --
--                                                                                              --
-- The "geoadmin", "geouser" and "coverages" words shall be used only for the roles and the     --
-- schema names. They can be replaced by different roles or schema names with a simple search   --
-- and replace.                                                                                 --
--------------------------------------------------------------------------------------------------

SET client_encoding = 'UTF8';

CREATE SCHEMA metadata;
ALTER SCHEMA metadata OWNER TO geoadmin;
GRANT ALL ON SCHEMA metadata TO geoadmin;
GRANT USAGE ON SCHEMA metadata TO PUBLIC;

COMMENT ON SCHEMA metadata IS 'ISO 19115-2 metadata';

SET search_path = metadata;

CREATE TYPE "MI_TransferFunctionTypeCode" AS ENUM ('linear', 'logarithmic', 'exponential');
CREATE CAST (character varying AS "MI_TransferFunctionTypeCode") WITH INOUT AS ASSIGNMENT;
COMMENT ON TYPE "MI_TransferFunctionTypeCode" IS
 'Transform function to be used when scaling a physical value for a given element.';
