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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.sql.DataSource;

import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.postgres.LucenePostgresSQLTreeEltMapper;
import org.geotoolkit.index.tree.manager.postgres.PGDataSource;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.internal.sql.DefaultDataSource;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SQLRtreeManager extends AbstractRtreeManager {

    public final static String JDBC_TYPE_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.type";


    public static synchronized Tree<NamedEnvelope> get(final File directory, final Object owner) {
        Tree<NamedEnvelope> tree = CACHED_TREES.get(directory);
        if (tree == null || tree.isClosed()) {
            final File treeFile   = new File(directory, "tree.bin");
            if (treeFile.exists()) {
                DataSource ds=null;
                TreeElementMapper treeElementMapper = null;
                if (System.getProperty(JDBC_TYPE_KEY)!= null){

                    // postgres
                    if (System.getProperty(JDBC_TYPE_KEY).equals("postgres")) {
                        ds = PGDataSource.getDataSource();
                        try {
                            treeElementMapper = new LucenePostgresSQLTreeEltMapper(DEFAULT_CRS, ds, directory);
                        } catch ( SQLException e) {
                            LOGGER.log(Level.SEVERE, null, e);
                            return null;
                        }
                    }
                    // other DB
                    // not yet implemented

                } else {
                    // derby DB as default
                    ds = LuceneDerbySQLTreeEltMapper.getDataSource(directory);
                    try {
                        treeElementMapper = new LuceneDerbySQLTreeEltMapper(DEFAULT_CRS, ds);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                        return null;
                    }
                }
                try {
                    tree = new FileStarRTree<>(treeFile, treeElementMapper);
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
                TreeElementMapper treeEltMapper = null;
                // postgres
                if ("postgres".equals(System.getProperty(JDBC_TYPE_KEY))) {
                    treeEltMapper = LucenePostgresSQLTreeEltMapper.createTreeEltMapperWithDB(directory);
                } else {
                    treeEltMapper = LuceneDerbySQLTreeEltMapper.createTreeEltMapperWithDB(directory);
                }
                return new FileStarRTree(treeFile, 5, SQLRtreeManager.DEFAULT_CRS, treeEltMapper);

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



    public static Tree resetTree(final File directory, final Tree tree, final Object owner) throws StoreIndexException, IOException, SQLException {
        if (tree != null) {
            close(directory, tree, owner);
        }
        final File treeFile   = new File(directory, "tree.bin");
        if (treeFile.exists()) {
            treeFile.delete();
        }
        if ("postgres".equals(System.getProperty(JDBC_TYPE_KEY))) {
            LucenePostgresSQLTreeEltMapper.resetDB(directory);
        } else {
            final File mapperDir = new File(directory, "treemap-db");
            if (mapperDir.exists()) {
                final String dbUrl = "jdbc:derby:" + directory.getPath() + "/treemap-db;create=true;";
                final DefaultDataSource source = new DefaultDataSource(dbUrl);
                final LuceneDerbySQLTreeEltMapper mapper = new LuceneDerbySQLTreeEltMapper(SQLRtreeManager.DEFAULT_CRS, source);
                mapper.clear();
                mapper.close();
            }
        }
        return get(directory, owner);
    }













}
