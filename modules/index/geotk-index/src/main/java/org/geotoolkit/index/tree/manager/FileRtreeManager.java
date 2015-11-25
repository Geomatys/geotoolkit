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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.star.FileStarRTree;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileRtreeManager extends AbstractRtreeManager {

    public static Tree<NamedEnvelope> get(final Path directory, final Object owner) {
        Tree<NamedEnvelope> tree = CACHED_TREES.get(directory);
        if (tree == null || tree.isClosed()) {
            final Path treeFile   = directory.resolve("tree.bin");
            final Path mapperFile = directory.resolve("mapper.bin");
            if (Files.exists(treeFile)) {

                try {
                    tree = new FileStarRTree<>(treeFile.toFile().toPath(), new LuceneFileTreeEltMapper(mapperFile.toFile(), DEFAULT_CRS));//ecrire crs dans constructeur
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

    private static Tree buildNewTree(final Path directory) {
        if (Files.exists(directory)) {
            try {
                //creating tree (R-Tree)------------------------------------------------
                final Path treeFile   = directory.resolve("tree.bin");
                final Path mapperFile = directory.resolve("mapper.bin");
                Files.createFile(treeFile);
                Files.createFile(mapperFile);
                return new FileStarRTree(treeFile, 5, DEFAULT_CRS, new LuceneFileTreeEltMapper(DEFAULT_CRS, mapperFile.toFile()));

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unable to create file to write Tree", ex);
            } catch (org.geotoolkit.index.tree.StoreIndexException ex) {
                LOGGER.log(Level.WARNING, "Unable to create Tree", ex);
            }
        }
        return null;
    }

    public static Tree resetTree(final Path directory, final Tree tree, final Object owner) throws StoreIndexException, IOException {
        if (tree != null) {
            close(directory, tree, owner);
        }
        final Path treeFile   = directory.resolve("tree.bin");
        final Path mapperFile = directory.resolve("mapper.bin");
        Files.deleteIfExists(treeFile);
        Files.deleteIfExists(mapperFile);

        return get(directory, owner);
    }
}
