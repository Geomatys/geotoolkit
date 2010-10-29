--
-- Creates standard users expected by the "coverages" schema.  Those names can be changed
-- by searching and replacing every occurrences of 'geoadmin' and 'geouser' in prepare.sql,
-- postgis-update.sql and coverages-create.sql.
--

CREATE ROLE geoadmin LOGIN
  SUPERUSER NOINHERIT CREATEDB CREATEROLE;

CREATE ROLE geouser LOGIN
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;

-- Following instruction shall be executed only on PostgreSQL prior version 9.
CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'
  HANDLER plpgsql_call_handler
  VALIDATOR plpgsql_validator;

CREATE SCHEMA postgis
  AUTHORIZATION geoadmin;
GRANT ALL ON SCHEMA postgis TO geoadmin;
GRANT USAGE ON SCHEMA postgis TO public;
COMMENT ON SCHEMA postgis IS 'PostGIS functions and type definitions';


---
--- Install the PostGIS schema after this point by executing MODIFIED versions of the
--- following scripts, which are provided with PostGIS installation. The modification
--- to apply before to run those scripts is to add the following line at the begining
--- of each file:
---
---   SET search_path = postgis;
---
--- The intend is to create all PostGIS functions and tables in the "postgis" schema
--- rather than the "public" schema. The files to execute are:
---
---   1) postgis.sql
---   2) spatial_ref_sys.sql
