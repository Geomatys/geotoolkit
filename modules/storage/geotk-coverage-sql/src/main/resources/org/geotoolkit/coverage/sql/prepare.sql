--
-- Creates standard users expected by the "coverages" schema.  Those names can be changed
-- by searching and replacing every occurrences of 'geoadmin' and 'geouser' in prepare.sql,
-- postgis-update.sql and coverages-create.sql.
--

CREATE EXTENSION postgis;

CREATE ROLE geoadmin LOGIN
  SUPERUSER NOINHERIT CREATEDB CREATEROLE;

CREATE ROLE geouser LOGIN
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;
