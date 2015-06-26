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

package org.geotoolkit.index.tree.manager.postgres;

import org.apache.commons.io.IOUtils;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.index.tree.manager.util.AeSimpleSHA1;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.sql.DataSource;

import java.io.*;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LucenePostgresSQLTreeEltMapper implements TreeElementMapper<NamedEnvelope> {

    public static final String SCHEMA = "index";
    /**
     * Mutual Coordinate Reference System from all stored NamedEnvelopes.
     */
    private final CoordinateReferenceSystem crs;

    private final DataSource source;

    private Connection conn;


    private String schemaName;

    protected static final Logger LOGGER = Logging.getLogger(LucenePostgresSQLTreeEltMapper.class);

    public LucenePostgresSQLTreeEltMapper(final CoordinateReferenceSystem crs, final DataSource source, File directory) throws SQLException {
        try {
            final String absolutePath = directory.getAbsolutePath();
            ensureNonNull("absolutePath", absolutePath);
            this.schemaName = getSchemaName(absolutePath);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex){
            throw new IllegalStateException("could not get schema name from directory name");
        }

        this.crs    = crs;
        this.source = source;

        this.conn = source.getConnection();
        //this.conn.setAutoCommit(false);
    }

    public static TreeElementMapper createTreeEltMapperWithDB(File directory) throws SQLException, IOException {
        final DataSource dataSource = PGDataSource.getDataSource();
        final Connection connection = dataSource.getConnection();

        if (!schemaExist(connection,directory.getAbsolutePath())){
            createSchema(connection,directory.getAbsolutePath());
        }
        connection.close();
        return new LucenePostgresSQLTreeEltMapper(SQLRtreeManager.DEFAULT_CRS,dataSource, directory);
    }

    public static void resetDB(File directory) throws SQLException, IOException {
        final DataSource dataSource = PGDataSource.getDataSource();
        final Connection connection = dataSource.getConnection();

        if (schemaExist(connection,directory.getAbsolutePath())){
            dropSchema(connection,directory.getAbsolutePath());
        }
        connection.close();
    }

    private static void dropSchema(Connection connection, String absolutePath) throws SQLException, IOException {
        try {
            ensureNonNull("absolutePath", absolutePath);
            final String schemaName = getSchemaName(absolutePath);
            final ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.run("DROP SCHEMA \""+schemaName+"\" CASCADE;");
            scriptRunner.close(true);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to create treemap database schema.", ex);
        }

    }

    private static void createSchema(Connection connection, String absolutePath) throws SQLException, IOException {
        try {
            ensureNonNull("absolutePath", absolutePath);
            final String schemaName = getSchemaName(absolutePath);
            final InputStream stream = getResourceAsStream("org/geotoolkit/index/tree/create-postgres-treemap-db.sql");
            final ScriptRunner scriptRunner = new ScriptRunner(connection);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "ISO-8859-1");
            String sqlQuery = writer.toString();
            sqlQuery = sqlQuery.replaceAll("µSCHEMANAMEµ",schemaName);
            scriptRunner.run(sqlQuery);
            scriptRunner.close(false);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unexpected error occurred while trying to create treemap database schema.", ex);
        }
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
    
    public static boolean treeExist(final DataSource source, File directory) {
        try {
            Connection conn = source.getConnection();
            boolean exist = schemaExist(conn, directory.getAbsolutePath());
            conn.close();
            return exist;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error sxhile looking for postgres tree existence", ex);
            return false;
        }
    }

    private static boolean schemaExist(Connection connection, String absolutePath) throws SQLException {
        try {
            ensureNonNull("absolutePath", absolutePath);
            final String schemaName = getSchemaName(absolutePath);
            final ResultSet schemas = connection.getMetaData().getSchemas();
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

    private static String getSchemaName(String absolutePath) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final String sha1 = AeSimpleSHA1.SHA1(absolutePath);
        return SCHEMA+sha1;
    }




    @Override
    public int getTreeIdentifier(final NamedEnvelope env) throws IOException {
        int result = -1;
        try {
            final PreparedStatement stmt = conn.prepareStatement("SELECT \"id\" FROM \""+schemaName+"\".\"records\" WHERE \"identifier\"=?");
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
                final PreparedStatement existStmt = conn.prepareStatement("SELECT \"id\" FROM \""+schemaName+"\".\"records\" WHERE \"id\"=?");
                existStmt.setInt(1, treeIdentifier);
                final ResultSet rs = existStmt.executeQuery();
                final boolean exist = rs.next();
                rs.close();
                existStmt.close();
                if (exist) {
                    final PreparedStatement stmt = conn.prepareStatement("UPDATE \""+schemaName+"\".\"records\" "
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
//                    conn.commit();
                } else {
                    final PreparedStatement stmt = conn.prepareStatement("INSERT INTO \""+schemaName+"\".\"records\" "
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
//                    conn.commit();
                }
            } else {
                final PreparedStatement remove = conn.prepareStatement("DELETE FROM \""+schemaName+"\".\"records\" WHERE \"id\"=?");
                remove.setInt(1, treeIdentifier);
                try {
                    remove.executeUpdate();
                } finally {
                    remove.close();
                }
//                conn.commit();
            }
        } catch (SQLException ex) {
            throw new IOException("Error while setting tree identifier for envelope :" + env, ex);
        }
    }

    @Override
    public NamedEnvelope getObjectFromTreeIdentifier(final int treeIdentifier) throws IOException {
        NamedEnvelope result = null;
        try {
            final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM \""+schemaName+"\".\"records\" WHERE \"id\"=?");
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
            final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM \""+schemaName+"\".\"records\"");
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
            final PreparedStatement stmt = conn.prepareStatement("DELETE FROM \""+schemaName+"\".\"records\"");
            stmt.executeUpdate();
            stmt.close();
//            conn.commit();
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
        if (conn != null) try {
            conn.close();
            conn = null;

        } catch (SQLException ex) {
            throw new IOException("SQL exception while closing SQL tree mapper", ex);
        }
    }

    @Override
    public boolean isClosed() {
        return conn == null;
    }



}
