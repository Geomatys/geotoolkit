/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.basic;

import org.geotoolkit.internal.tree.TreeAccessMemory;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link BasicRTree} implementation which store all Nodes in memory.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class MemoryBasicRTree<E> extends BasicRTree<E> {

    /**
     * Create a new {@link BasicRTree} implementation which store Tree architecture in memory.
     * 
     * @param maxElements maximum children value permit per Node.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param choice split made choice.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException StoreIndexException in this tree implementation exception never occur.
     */
    public MemoryBasicRTree(final int maxElements, final CoordinateReferenceSystem crs, final SplitCase choice, final TreeElementMapper treeEltMap) throws StoreIndexException {
        super(new TreeAccessMemory(maxElements, choice, crs), treeEltMap);
    }
}
