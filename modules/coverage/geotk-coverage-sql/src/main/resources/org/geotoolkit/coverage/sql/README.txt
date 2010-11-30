The easiest way to install the database is to run the graphical wizard:

    http://www.geotoolkit.org/modules/display/geotk-wizards-swing/CoverageDatabaseInstaller.html

The database can also be created manually by running the SQL scripts in the following order:

    * prepare.sql
    * Modified versions of the postgis.sql and spatial_ref_sys.sql files provided
      with the PostGIS installation. See the comments in prepare.sql for details.
    * postgis-update.sql
    * metadata-create.sql
    * coverages-create.sql
