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

import org.geotoolkit.index.tree.Node;
import org.geotoolkit.internal.tree.TreeAccessMemory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation adapted for {@link HilberRTree} which store data and Tree identifier in computer memory.
 *
 * @author Remi Marechal (Geomatys)
 */
final class HilbertTreeAccessMemory extends TreeAccessMemory {

    /**
     * Build a TreeAccess adapted for HilbertRTree, and store tree information in memory.
     * 
     * @param maxElements max element permit in each tree cells.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @see HilbertRTree
     * @see HilbertNode
     */
    HilbertTreeAccessMemory(final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs) {
        super(maxElements, crs);
        super.hilbertOrder = hilbertOrder;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized Node createNode(double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        final int currentID = (recycleID.isEmpty()) ? nodeId++ : recycleID.remove(0);
            return new HilbertNode(this, currentID, boundary, properties, parentId, siblingId, childId);
    }
}
