The following shall be run once in the database:

    CREATE EXTENSION postgis;

The database can be created manually by running the SQL scripts in the following order:

    * postgis-update.sql     (optional)
    * coverages-create.sql

TODO:
  Rename coverage-clean.sql  as Prune.sql
  Rename coverage-create.sql as Create.sql
  Rename postgis-update.sql  as PostGIS.sql
