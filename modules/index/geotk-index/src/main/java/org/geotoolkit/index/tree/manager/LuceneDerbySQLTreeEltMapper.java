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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;

import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.DerbySqlScriptRunner;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneDerbySQLTreeEltMapper extends LuceneSQLTreeEltMapper implements TreeElementMapper<NamedEnvelope>{

    private Connection connection;

    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Unable to load Derby JDBC driver. Derby jar is missing from classpath.");
        }
    }

    public LuceneDerbySQLTreeEltMapper(final CoordinateReferenceSystem crs, final DataSource source) throws IOException {
        super(crs, source);
        try {
            this.connection = source.getConnection();
        } catch (SQLException ex) {
            throw new IOException("Error while trying to connect the treemap datasource", ex);
        }
    }

    static DataSource getDataSource(Path directory) {

        final String dbUrl = "jdbc:derby:" + directory.toAbsolutePath().toString()+ "/treemap-db;";
        LOGGER.log(Level.INFO, "connecting to datasource {0}", dbUrl);
        return new DefaultDataSource(dbUrl);

    }

    public static TreeElementMapper createTreeEltMapperWithDB(Path directory) throws SQLException, IOException {
        final String dbUrl = "jdbc:derby:" + directory.toAbsolutePath().toString() + "/treemap-db;create=true;";
        LOGGER.log(Level.INFO, "creating datasource {0}", dbUrl);
        final DataSource source = new DefaultDataSource(dbUrl);
        // Establish connection and create schema if does not exist.
        try (Connection con = source.getConnection()){
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

    @Override
    public int getTreeIdentifier(final NamedEnvelope env) throws IOException {
        int result = -1;
        try (final PreparedStatement stmt = connection.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"identifier\"=?")) {
            stmt.setString(1, env.getId());
            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
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
                boolean exist = false;
                try (final PreparedStatement existStmt = connection.prepareStatement("SELECT \"id\" FROM \"treemap\".\"records\" WHERE \"id\"=?")) {
                    existStmt.setInt(1, treeIdentifier);
                    try (final ResultSet rs = existStmt.executeQuery()) {
                        exist = rs.next();
                    }
                }
                if (exist) {
                    try (final PreparedStatement stmt = connection.prepareStatement("UPDATE \"treemap\".\"records\" "
                                                                           + "SET \"identifier\"=?, \"nbenv\"=?, \"minx\"=?, \"maxx\"=?, \"miny\"=?, \"maxy\"=? "
                                                                           + "WHERE \"id\"=?")) {
                        stmt.setString(1, env.getId());
                        stmt.setInt(2, env.getNbEnv());
                        stmt.setDouble(3, env.getMinimum(0));
                        stmt.setDouble(4, env.getMaximum(0));
                        stmt.setDouble(5, env.getMinimum(1));
                        stmt.setDouble(6, env.getMaximum(1));
                        stmt.setInt(7, treeIdentifier);
                        stmt.executeUpdate();
                    }
                } else {
                    try (final PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"treemap\".\"records\" "
                                                                       + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        stmt.setInt(1, treeIdentifier);
                        stmt.setString(2, env.getId());
                        stmt.setInt(3, env.getNbEnv());
                        stmt.setDouble(4, env.getMinimum(0));
                        stmt.setDouble(5, env.getMaximum(0));
                        stmt.setDouble(6, env.getMinimum(1));
                        stmt.setDouble(7, env.getMaximum(1));
                        stmt.executeUpdate();
                    }
                }
            } else {
                try (final PreparedStatement remove = connection.prepareStatement("DELETE FROM \"treemap\".\"records\" WHERE \"id\"=?")) {
                    remove.setInt(1, treeIdentifier);
                    remove.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new IOException("Error while setting tree identifier for envelope :" + env, ex);
        }
    }

    @Override
    public NamedEnvelope getObjectFromTreeIdentifier(final int treeIdentifier) throws IOException {
        NamedEnvelope result = null;
        try (final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM \"treemap\".\"records\" WHERE \"id\"=?")) {
            stmt.setInt(1, treeIdentifier);
            try (final ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (SQLException ex) {
            throw new IOException("Error while getting envelope", ex);
        }
        return result;
    }

    @Override
    public Map<Integer, NamedEnvelope> getFullMap() throws IOException {
        Map<Integer, NamedEnvelope> result = new HashMap<>();
        try (final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM \"treemap\".\"records\"");
             final ResultSet rs = stmt.executeQuery()) {
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
        } catch (SQLException ex) {
            throw new IOException("Error while getting envelope", ex);
        }
        return result;
    }


    @Override
    public void clear() throws IOException {
        try (final PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"treemap\".\"records\"")) {
            stmt.executeUpdate();
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
        if (connection != null) try {
            connection.close();
            connection = null;
            if (source instanceof DefaultDataSource) {
                ((DefaultDataSource)source).shutdown();
            }
        } catch (SQLException ex) {
            throw new IOException("SQL exception while closing SQL tree mapper", ex);
        }
    }

    @Override
    public boolean isClosed() {
        return connection == null;
    }

}
