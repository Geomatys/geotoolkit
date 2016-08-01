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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AbstractRtreeManager {

    protected static final Map<Path, Tree<NamedEnvelope>> CACHED_TREES = new HashMap<>();
    protected static final Map<Path, List<Object>> TREE_OWNERS = new HashMap<>();

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.index.tree.manager");

    public static final CoordinateReferenceSystem DEFAULT_CRS = CommonCRS.defaultGeographic();

    public static void close(final Path directory, final Tree rTree, final Object owner) throws StoreIndexException, IOException {
        final List<Object> owners = TREE_OWNERS.get(directory);
        if (owners != null) {
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
        } else {
            throw new StoreIndexException("Trying to close a R-Tree not managed by the RTreeManager system");
        }
    }

}
