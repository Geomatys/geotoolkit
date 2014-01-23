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

package org.geotoolkit.index.tree.manager.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RtreeManager {

    private static final Map<File, Tree<NamedEnvelope>> CACHED_TREES = new HashMap<>();
    private static final Map<File, List<Object>> TREE_OWNERS = new HashMap<>();

    private static final Logger LOGGER = Logging.getLogger(RtreeManager.class);

    public static void close(final File directory, final Tree rTree, final Object owner) throws StoreIndexException, IOException {
        final List<Object> owners = TREE_OWNERS.get(directory);
        owners.remove(owner);

        if (owners.isEmpty()) {
            if (rTree != null) {
                if (!rTree.isClosed()) {
                    rTree.close();
                    if (rTree.getTreeElementMapper() != null) {
                        rTree.getTreeElementMapper().close();
                    }
                }
            }
        } else {
            LOGGER.config("R-tree is used by another object. Not closing");
        }
    }

    public static Tree<NamedEnvelope> get(final File directory, final Object owner) {
        Tree<NamedEnvelope> tree = CACHED_TREES.get(directory);
        if (tree == null || tree.isClosed()) {
            final File treeFile   = new File(directory, "tree.bin");
            final File mapperFile = new File(directory, "mapper.bin");
            if (treeFile.exists()) {

                try {
                    tree = new FileStarRTree<>(treeFile, new LuceneFileTreeEltMapper(mapperFile));//ecrire crs dans constructeur
                } catch (ClassNotFoundException | IllegalArgumentException | StoreIndexException | IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            } else {
                tree = buildNewTree(directory);
            }
            final List<Object> owners = new ArrayList<>();
            owners.add(owner);
            TREE_OWNERS.put(directory, owners);
            CACHED_TREES.put(directory, tree);
        } else {
            //look if the owner is already registred
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
                final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
                //creating tree (R-Tree)------------------------------------------------
                final File treeFile   = new File(directory, "tree.bin");
                final File mapperFile = new File(directory, "mapper.bin");
                treeFile.createNewFile();
                mapperFile.createNewFile();
                return new FileStarRTree(treeFile, 5, crs, new LuceneFileTreeEltMapper(crs, mapperFile));

            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "Unable to get the CRS:84 CRS", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unable to create file to write Tree", ex);
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
        final File mapperFile = new File(directory, "mapper.bin");
        if (treeFile.exists() && mapperFile.exists()) {
            treeFile.delete();
            mapperFile.delete();
        }
        return get(directory, owner);
    }
}
