The easiest way to install the database is to run the graphical wizard:

    http://www.geotoolkit.org/modules/display/geotk-wizards-swing/CoverageDatabaseInstaller.html

The database can also be created manually by running the SQL scripts in the following order:

    * prepare.sql
    * postgis-update.sql
    * metadata-create.sql
    * coverages-create.sql
