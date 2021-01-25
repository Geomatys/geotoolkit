/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.index.tree.manager.postgres;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.LuceneSQLTreeEltMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.util.AeSimpleSHA1;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class LuceneSGBDTreeEltMapper extends LuceneSQLTreeEltMapper implements TreeElementMapper<NamedEnvelope> {

    public static final String SCHEMA = "index";

    protected String schemaName;

    public LuceneSGBDTreeEltMapper(CoordinateReferenceSystem crs, DataSource source, Path directory) throws SQLException {
        super(crs, source);
        try {
            final String absolutePath = directory.getFileName().toString();
            ensureNonNull("absolutePath", absolutePath);
            this.schemaName = getSchemaName(absolutePath);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("could not get schema name from directory name");
        }
    }

    protected static String getSchemaName(String absolutePath) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final String sha1 = AeSimpleSHA1.SHA1(absolutePath);
        return SCHEMA + sha1;
    }

    public static boolean treeExist(final DataSource source, Path directory) {
        try {
            boolean exist;
            try (Connection conn = source.getConnection()) {
                exist = schemaExist(conn, directory.getFileName().toString());
            }
            return exist;

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error while looking for postgres tree existence", ex);
            return false;
        }
    }

    protected static boolean schemaExist(Connection connection, String absolutePath) throws SQLException {
        ensureNonNull("absolutePath", absolutePath);
        try (final ResultSet schemas = connection.getMetaData().getSchemas()) {
            final String schemaName = getSchemaName(absolutePath);
            while (schemas.next()) {
                if (schemaName.equals(schemas.getString(1))) {
                    return true;
                }
            }
            return false;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to verify treemap database schema.", ex);
        }
    }

    public static void resetDB(Path directory) throws SQLException, IOException {
        final DataSource dataSource = PGDataSource.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            if (schemaExist(connection, directory.getFileName().toString())) {
                dropSchema(connection, directory.getFileName().toString());
            }
        }
    }

    private static void dropSchema(Connection connection, String absolutePath) throws SQLException, IOException {
        try {
            ensureNonNull("absolutePath", absolutePath);
            final String schemaName = getSchemaName(absolutePath);
            final ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.run("DROP SCHEMA \"" + schemaName + "\" CASCADE;");
            scriptRunner.close(true);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to create treemap database schema.", ex);
        }

    }

    @Override
    public int getTreeIdentifier(final NamedEnvelope env) throws IOException {
        int result = -1;
        try (Connection conn = source.getConnection()){
            try (PreparedStatement stmt = conn.prepareStatement("SELECT \"id\" FROM \"" + schemaName + "\".\"records\" WHERE \"identifier\"=?")) {
                stmt.setString(1, env.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
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
        try (Connection conn = source.getConnection()){
            if (env != null) {
                final boolean exist;
                try (PreparedStatement existStmt = conn.prepareStatement("SELECT \"id\" FROM \"" + schemaName + "\".\"records\" WHERE \"id\"=?")) {
                    existStmt.setInt(1, treeIdentifier);
                    try (ResultSet rs = existStmt.executeQuery()) {
                        exist = rs.next();
                    }
                }
                if (exist) {
                    try (final PreparedStatement stmt = conn.prepareStatement("UPDATE \"" + schemaName + "\".\"records\" "
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
                    try (final PreparedStatement stmt = conn.prepareStatement("INSERT INTO \"" + schemaName + "\".\"records\" "
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
                try (PreparedStatement remove = conn.prepareStatement("DELETE FROM \"" + schemaName + "\".\"records\" WHERE \"id\"=?")) {
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

        try (Connection conn = source.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM \"" + schemaName + "\".\"records\" WHERE \"id\"=?")) {
            stmt.setInt(1, treeIdentifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    final String identifier = rs.getString("identifier");
                    final int nbEnv = rs.getInt("nbenv");
                    final double minx = rs.getDouble("minx");
                    final double maxx = rs.getDouble("maxx");
                    final double miny = rs.getDouble("miny");
                    final double maxy = rs.getDouble("maxy");
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
        try (Connection conn = source.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM \"" + schemaName + "\".\"records\"");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final String identifier = rs.getString("identifier");
                final int treeId = rs.getInt("id");
                final int nbEnv = rs.getInt("nbenv");
                final double minx = rs.getDouble("minx");
                final double maxx = rs.getDouble("maxx");
                final double miny = rs.getDouble("miny");
                final double maxy = rs.getDouble("maxy");
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
        try (Connection conn = source.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM \"" + schemaName + "\".\"records\"")) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException("Error while removing all records", ex);
        }
    }

    @Override
    public void flush() throws IOException {
        // do nothing in this implementation
    }

    private boolean closed = false;

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
