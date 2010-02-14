--
-- Creates standard users expected by the "coverages" schema.  Those names can be changed
-- by searching and replacing every occurences of 'geoadmin' and 'geouser' in prepare.sql,
-- postgis-update.sql and coverages.sql.
--

CREATE ROLE geoadmin LOGIN
  SUPERUSER NOINHERIT CREATEDB CREATEROLE;

CREATE ROLE geouser LOGIN
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;

CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'
  HANDLER plpgsql_call_handler
  VALIDATOR plpgsql_validator;

CREATE SCHEMA postgis
  AUTHORIZATION geoadmin;
GRANT ALL ON SCHEMA postgis TO geoadmin;
GRANT USAGE ON SCHEMA postgis TO public;
COMMENT ON SCHEMA postgis IS 'PostGIS functions and type definitions';
