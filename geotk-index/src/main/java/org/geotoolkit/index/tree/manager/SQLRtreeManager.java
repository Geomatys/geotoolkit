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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.sql.DataSource;

import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.postgres.LuceneHSQLTreeEltMapper;
import org.geotoolkit.index.tree.manager.postgres.LucenePostgresSQLTreeEltMapper;
import org.geotoolkit.index.tree.manager.postgres.LuceneSGBDTreeEltMapper;
import org.geotoolkit.index.tree.manager.postgres.PGDataSource;
import org.geotoolkit.index.tree.manager.postgres.PGTreeWrapper;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.internal.tree.TreeAccessSQLByteArray;
import org.geotoolkit.nio.IOUtilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SQLRtreeManager extends AbstractRtreeManager {

    public static synchronized Tree<NamedEnvelope> get(final Path directory, final Object owner) {
        Tree<NamedEnvelope> tree = CACHED_TREES.get(directory);
        if (tree == null || tree.isClosed()) {
            if (existTree(directory)) {

                TreeElementMapper treeMapper = null;
                try {
                    if (PGDataSource.isSetPGDataSource()) {
                        if (PGDataSource.isPostgres) {
                                DataSource ds = PGDataSource.getDataSource();
                                treeMapper = new LucenePostgresSQLTreeEltMapper(DEFAULT_CRS, ds, directory);
                                byte[] data = TreeAccessSQLByteArray.getData(directory, ds);
                                tree = new PGTreeWrapper(data, directory, ds, treeMapper);
                        } else {
                                DataSource ds = PGDataSource.getDataSource();
                                treeMapper = new LuceneHSQLTreeEltMapper(DEFAULT_CRS, ds, directory);
                                byte[] data = TreeAccessSQLByteArray.getData(directory, ds);
                                tree = new PGTreeWrapper(data, directory, ds, treeMapper);
                        }
                    } else {
                        // derby DB as default
                            final Path treeFile = directory.resolve("tree.bin");
                            DataSource ds = LuceneDerbySQLTreeEltMapper.getDataSource(directory);
                            treeMapper = new LuceneDerbySQLTreeEltMapper(DEFAULT_CRS, ds);
                            tree = new FileStarRTree<>(treeFile.toFile().toPath(), treeMapper);
                    }
                } catch (Exception e) {
                    if (treeMapper != null) {
                        try{
                            treeMapper.close();
                        } catch (Exception bis) {
                            e.addSuppressed(bis);
                        }
                    }
                    LOGGER.log(Level.SEVERE, "Cannot re-open RTRee", e);
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

    private static Tree buildNewTree(final Path directory) {
        if (Files.exists(directory)) {
            TreeElementMapper treeMapper = null;
            try {
                //creating tree (R-Tree)------------------------------------------------
                final Path treeFile = directory.resolve("tree.bin");
                Files.createFile(treeFile);
                // postgres
                if (PGDataSource.isSetPGDataSource()) {
                     if (PGDataSource.isPostgres) {
                         treeMapper = LucenePostgresSQLTreeEltMapper.createTreeEltMapperWithDB(directory);
                         return new PGTreeWrapper(directory, PGDataSource.getDataSource(), treeMapper, DEFAULT_CRS);
                     } else {
                         treeMapper = LuceneHSQLTreeEltMapper.createTreeEltMapperWithDB(directory);
                        return new PGTreeWrapper(directory, PGDataSource.getDataSource(), treeMapper, DEFAULT_CRS);
                     }

                } else {
                    treeMapper = LuceneDerbySQLTreeEltMapper.createTreeEltMapperWithDB(directory);
                    return new FileStarRTree(treeFile.toFile().toPath(), 5, DEFAULT_CRS, treeMapper);
                }

            } catch (SQLException | IOException | StoreIndexException ex) {
                if (treeMapper != null) {
                    try {
                        treeMapper.close();
                    } catch (Exception bis) {
                        ex.addSuppressed(bis);
                    }
                }
                LOGGER.log(Level.WARNING, "Unable to create RTree", ex);
            }
        }
        return null;
    }

    public static Tree resetTree(final Path directory, final Tree tree, final Object owner) throws StoreIndexException, IOException, SQLException {
        if (tree != null) {
            tree.flush();
            tree.clear();
        }
        if (PGDataSource.isSetPGDataSource()) {
            if (LuceneSGBDTreeEltMapper.treeExist(PGDataSource.getDataSource(), directory)) {
                tree.getTreeElementMapper().clear();
            }
        } else {
            final Path mapperDir = directory.resolve("treemap-db");
            if (Files.exists(mapperDir)) {
                tree.getTreeElementMapper().clear();
            }
        }
        return tree;
    }

    private static boolean existTree(final Path directory) {
        if (PGDataSource.isSetPGDataSource()) {
            DataSource ds = PGDataSource.getDataSource();
            return LuceneSGBDTreeEltMapper.treeExist(ds, directory);
        } else {
            // derby DB as default
            final Path treeFile = directory.resolve("tree.bin");
            return Files.exists(treeFile);
        }
    }

    public static void removeTree(final Path directory) throws SQLException, IOException {
        if (PGDataSource.isSetPGDataSource()) {
            LuceneSGBDTreeEltMapper.resetDB(directory);
        }
        IOUtilities.deleteRecursively(directory);
    }
}
