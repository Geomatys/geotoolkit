/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.sql.DataSource;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.sql.DerbySqlScriptRunner;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SQLRtreeManager extends AbstractRtreeManager {

    public static synchronized Tree<NamedEnvelope> get(final File directory, final Object owner) {
        Tree<NamedEnvelope> tree = CACHED_TREES.get(directory);
        if (tree == null || tree.isClosed()) {
            final File treeFile   = new File(directory, "tree.bin");
            if (treeFile.exists()) {
                final String dbUrl = "jdbc:derby:" + directory.getPath() + "/treemap-db;";
                LOGGER.log(Level.INFO, "connecting to datasource {0}", dbUrl);
                final DataSource source = new DefaultDataSource(dbUrl);
                try {
                    tree = new FileStarRTree<>(treeFile, new LuceneSQLTreeEltMapper(DEFAULT_CRS, source));
                } catch (ClassNotFoundException | IllegalArgumentException | StoreIndexException | IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    return null;
                }
            } else {
                tree = buildNewTree(directory);
            }
            final List<Object> owners = new ArrayList<>();
            owners.add(owner);
            TREE_OWNERS.put(directory, owners);
            CACHED_TREES.put(directory, tree);
        } else {
            //look if the owner is already registered
            final List<Object> owners = TREE_OWNERS.get(directory);
            if (!owners.contains(owner)) {
                owners.add(owner);
            }
        }
        return tree;
    }

    private static Tree buildNewTree(final File directory) {
        if (directory.exists()) {
            try {
                //creating tree (R-Tree)------------------------------------------------
                final File treeFile       = new File(directory, "tree.bin");
                treeFile.createNewFile();
                final String dbUrl = "jdbc:derby:" + directory.getPath() + "/treemap-db;create=true;";
                LOGGER.log(Level.INFO, "creating datasource {0}", dbUrl);
                final DataSource source = new DefaultDataSource(dbUrl);
                // Establish connection and create schema if does not exist.
                Connection con = null;
                try {
                    con = source.getConnection();

                    if (!schemaExists(con, "treemap")) {
                        // Load database schema SQL stream.
                        final InputStream stream = getResourceAsStream("org/geotoolkit/index/tree/create-treemap-db.sql");

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
                return new FileStarRTree(treeFile, 5, DEFAULT_CRS, new LuceneSQLTreeEltMapper(DEFAULT_CRS, source));

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unable to create file to write Tree", ex);
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception while creating treemap database", ex);
            } catch (org.geotoolkit.index.tree.StoreIndexException ex) {
                LOGGER.log(Level.WARNING, "Unable to create Tree", ex);
            }
        }
        return null;
    }

    public static Tree resetTree(final File directory, final Tree tree, final Object owner) throws StoreIndexException, IOException {
        if (tree != null) {
            close(directory, tree, owner);
        }
        final File treeFile   = new File(directory, "tree.bin");
        if (treeFile.exists()) {
            treeFile.delete();
        }
        final File mapperDir  = new File(directory, "treemap-db");
        if (mapperDir.exists()) {
            final String dbUrl = "jdbc:derby:" + directory.getPath() + "/treemap-db;create=true;";
            final DefaultDataSource source = new DefaultDataSource(dbUrl);
            final LuceneSQLTreeEltMapper mapper = new LuceneSQLTreeEltMapper(DEFAULT_CRS, source);
            mapper.clear();
            mapper.close();
        }
        return get(directory, owner);
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

    /**
     * Return an input stream of the specified resource.
     */
    private static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }
}
