
package org.geotoolkit.pending.demo.referencing;

import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.opengis.util.FactoryException;

/**
 * Create an EPSG database on postgreSQL.
 * 
 */
public class CreateEPSGPostgresDatabase {
    
    public static void main(String[] args) throws FactoryException {
        
        final String dbURL = "jdbc:postgresql://localhost:5432/epsg";
        final String user = "user";
        final String password = "password";
        
        final EpsgInstaller installer = new EpsgInstaller();
        installer.setDatabase(dbURL, user, password);
        installer.call();
    }
    
}
