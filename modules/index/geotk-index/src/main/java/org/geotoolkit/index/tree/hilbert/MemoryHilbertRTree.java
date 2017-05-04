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
package org.geotoolkit.index.tree.hilbert;

import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link HilbertRTree} implementation which store all Nodes in memory.
 *
 * @author Remi Marechal (Geomatys).
 */
public final class MemoryHilbertRTree<E> extends HilbertRTree<E> {

    /**
     * Create a new {@link HilbertRTree} implementation which store Tree architecture in memory.
     *
     * @param maxElements maximum children value permit per Node.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException StoreIndexException in this tree implementation exception never occur.
     */
    public MemoryHilbertRTree(int maxElements, final int hilbertOrder, CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) throws StoreIndexException {
        super(new HilbertTreeAccessMemory(maxElements, hilbertOrder, crs), treeEltMap);
    }
}
