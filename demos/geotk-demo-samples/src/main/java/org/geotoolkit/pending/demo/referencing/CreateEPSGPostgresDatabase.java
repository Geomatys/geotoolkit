
package org.geotoolkit.pending.demo.referencing;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * Create an EPSG database on postgreSQL.
 *
 */
public class CreateEPSGPostgresDatabase {

    public static void main(String[] args) throws FactoryException {

        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("SpatialMetadata");

        final Map<String,Object> properties = new HashMap<>();
        properties.put("dataSource", ds);
        properties.put("scriptDirectory", new File("/path/to/the/sql/scripts"));
        try (EPSGFactory factory = new EPSGFactory(properties)) {
            System.out.println(factory.getAuthority());
        }
    }
}
