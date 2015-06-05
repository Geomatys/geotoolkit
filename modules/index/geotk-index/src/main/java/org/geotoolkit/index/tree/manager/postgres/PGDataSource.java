package org.geotoolkit.index.tree.manager.postgres;

import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;

/**
 * Created by christophem on 05/06/15.
 */
public class PGDataSource {
    private static DataSource ds;

    public final static String POSTGRES_USER_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.user";
    public final static String POSTGRES_PASSWORD_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.password";
    public final static String POSTGRES_DATABASE_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.database";
    public final static String POSTGRES_HOST_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.host";

    static {
        PGPoolingDataSource source = new PGPoolingDataSource();
        final String user = System.getProperty(POSTGRES_USER_KEY);
        final String password = System.getProperty(POSTGRES_PASSWORD_KEY);
        final String db = System.getProperty(POSTGRES_DATABASE_KEY);
        final String host = System.getProperty(POSTGRES_HOST_KEY);
        source.setServerName(host);
        source.setDatabaseName(db);
        source.setUser(user);
        source.setPassword(password);
//        source.setMaxConnections(10);
        ds = source;
    }

    public static DataSource getDataSource() {
        return ds;
    }


}