/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.index.tree.manager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.util.sql.DerbySqlScriptRunner;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneDerbySQLTreeEltMapper implements TreeElementMapper<NamedEnvelope>{

     /**
     * Mutual Coordinate Reference System from all stored NamedEnvelopes.
     */
    private final CoordinateReferenceSystem crs;

    private final DataSource source;

    private Connection conRO;
    private Connection conT;

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.index.tree.manager");

    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Unable to load Derby JDBC driver. Derby jar is missing from classpath.");
        }
    }

    public LuceneDerbySQLTreeEltMapper(final CoordinateReferenceSystem crs, final DataSource source) throws IOException {
        this.crs    = crs;
        this.source = source;
        try {
            this.conRO = source.getConnection();
            this.conRO.setReadOnly(true);

            this.conT  = source.getConnection();
            this.conT.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new IOException("Error while trying to connect the treemap datasource", ex);
        }
    }

    static DataSource getDataSource(Path directory) {

        final String dbUrl = "jdbc:derby:" + directory.toString()+ "/treemap-db;";
        LOGGER.log(Level.INFO, "connecting to datasource {0}", dbUrl);
        return new DefaultDataSource(dbUrl);

    }

    public static TreeElementMapper createTreeEltMapperWithDB(Path directory) throws SQLException, IOException {
        final String dbUrl = "jdbc:derby:" + directory.toString() + "/treemap-db;create=true;";
        LOGGER.log(Level.INFO, "creating datasource {0}", dbUrl);
        final DataSource source = new DefaultDataSource(dbUrl);
        // Establish connection and create schema if does not exist.
        Connection con = null;
        try {
            con = source.getConnection();

            if (!schemaExists(con, "treemap")) {
                // Load database schema SQL stream.
                final InputStream stream = getResourceAsStream("org/geotoolkit/index/tree/create-derby-treemap-db.sql");

                // Create schema.
                final DerbySqlScriptRunner runner = new DerbySqlScriptRunner(con);
                runner.run(stream);
                runner.close(false);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to create treemap database schema.", ex);
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return new LuceneDerbySQLTreeEltMapper(SQLRtreeManager.DEFAULT_CRS, source);
    }

    private static boolean schemaExists(final Connection connect, final String schemaName) throws SQLException {
        ensureNonNull("schemaName", schemaName);
        final ResultSet schemas = connect.getMetaData().getSchemas();
        while (schemas.next()) {
            if (schemaName.equals(schemas.getString(1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return an input stream of the specified resource.
     */
    private static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }

    /**
     * Obtain the Thread Context ClassLoader.
     */
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }



    @Override
    public int getTreeIdentifier(final NamedEnvelope env) throws IOException {
        int result = -1;
        try {
            final PreparedStatement stmt = conRO.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"identifier\"=?");
            stmt.setString(1, env.getId());
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new IOException("Error while getting tree identifier for envelope", ex);
        }
        return result;
    }

    @Override
    public Envelope getEnvelope(final NamedEnvelope object) throws IOException {
        return object;
    }

    @Override
    public void setTreeIdentifier(final NamedEnvelope env, final int treeIdentifier) throws IOException {
        try {
            if (env != null) {
                final PreparedStatement existStmt = conRO.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"id\"=?");
                existStmt.setInt(1, treeIdentifier);
                final ResultSet rs = existStmt.executeQuery();
                final boolean exist = rs.next();
                rs.close();
                existStmt.close();
                if (exist) {
                    final PreparedStatement stmt = conT.prepareStatement("UPDATE \"treemap\".\"records\" "
                                                                           + "SET \"identifier\"=?, \"nbenv\"=?, \"minx\"=?, \"maxx\"=?, \"miny\"=?, \"maxy\"=? "
                                                                           + "WHERE \"id\"=?");
                    stmt.setString(1, env.getId());
                    stmt.setInt(2, env.getNbEnv());
                    stmt.setDouble(3, env.getMinimum(0));
                    stmt.setDouble(4, env.getMaximum(0));
                    stmt.setDouble(5, env.getMinimum(1));
                    stmt.setDouble(6, env.getMaximum(1));
                    stmt.setInt(7, treeIdentifier);
                    try {
                        stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                    conT.commit();
                } else {
                    final PreparedStatement stmt = conT.prepareStatement("INSERT INTO \"treemap\".\"records\" "
                                                                       + "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    stmt.setInt(1, treeIdentifier);
                    stmt.setString(2, env.getId());
                    stmt.setInt(3, env.getNbEnv());
                    stmt.setDouble(4, env.getMinimum(0));
                    stmt.setDouble(5, env.getMaximum(0));
                    stmt.setDouble(6, env.getMinimum(1));
                    stmt.setDouble(7, env.getMaximum(1));
                    try {
                    stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                    conT.commit();
                }
            } else {
                final PreparedStatement remove = conT.prepareStatement("DELETE FROM \"treemap\".\"records\" WHERE \"id\"=?");
                remove.setInt(1, treeIdentifier);
                try {
                    remove.executeUpdate();
                } finally {
                    remove.close();
                }
                conT.commit();
            }
        } catch (SQLException ex) {
            throw new IOException("Error while setting tree identifier for envelope :" + env, ex);
        }
    }

    @Override
    public NamedEnvelope getObjectFromTreeIdentifier(final int treeIdentifier) throws IOException {
        NamedEnvelope result = null;
        try {
            final PreparedStatement stmt = conRO.prepareStatement("SELECT * FROM \"treemap\".\"records\" WHERE \"id\"=?");
            stmt.setInt(1, treeIdentifier);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                final String identifier = rs.getString("identifier");
                final int nbEnv         = rs.getInt("nbenv");
                final double minx       = rs.getDouble("minx");
                final double maxx       = rs.getDouble("maxx");
                final double miny       = rs.getDouble("miny");
                final double maxy       = rs.getDouble("maxy");
                result = new NamedEnvelope(crs, identifier, nbEnv);
                result.setRange(0, minx, maxx);
                result.setRange(1, miny, maxy);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new IOException("Error while getting envelope", ex);
        }
        return result;
    }

    @Override
    public Map<Integer, NamedEnvelope> getFullMap() throws IOException {
        Map<Integer, NamedEnvelope> result = new HashMap<>();
        try {
            final PreparedStatement stmt = conRO.prepareStatement("SELECT * FROM \"treemap\".\"records\"");
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final String identifier = rs.getString("identifier");
                final int treeId        = rs.getInt("id");
                final int nbEnv         = rs.getInt("nbenv");
                final double minx       = rs.getDouble("minx");
                final double maxx       = rs.getDouble("maxx");
                final double miny       = rs.getDouble("miny");
                final double maxy       = rs.getDouble("maxy");
                final NamedEnvelope env = new NamedEnvelope(crs, identifier, nbEnv);
                env.setRange(0, minx, maxx);
                env.setRange(1, miny, maxy);
                result.put(treeId, env);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            throw new IOException("Error while getting envelope", ex);
        }
        return result;
    }


    @Override
    public void clear() throws IOException {
        try {
            final PreparedStatement stmt = conT.prepareStatement("DELETE FROM \"treemap\".\"records\"");
            stmt.executeUpdate();
            stmt.close();
            conT.commit();
        } catch (SQLException ex) {
            throw new IOException("Error while removing all records", ex);
        }
    }

    @Override
    public void flush() throws IOException {
        // do nothing in this implementation
    }

    @Override
    public void close() throws IOException {
        if (conRO != null) try {
            conRO.close();
            conT.close();
            conRO = null;
            conT  = null;
            if (source instanceof DefaultDataSource) {
                ((DefaultDataSource)source).shutdown();
            }
        } catch (SQLException ex) {
            throw new IOException("SQL exception while closing SQL tree mapper", ex);
        }
    }

    @Override
    public boolean isClosed() {
        return conRO == null;
    }

}
