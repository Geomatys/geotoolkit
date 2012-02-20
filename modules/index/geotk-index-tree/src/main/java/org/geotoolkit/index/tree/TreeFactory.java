/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.star.StarRTree;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create chosen tree.
 * 
 * @author Rémi Marechal (Géomatys).
 */
public final class TreeFactory {

    private TreeFactory() {
    }

    /**
     * Create a Basic R-Tree.
     * 
     * @param splitMade made to split.
     * @param maxElements_per_cells
     * @return Basic RTree.
     */
    public static Tree createBasicRTree2D(final SplitCase splitMade, final int maxElements_per_cells) {
        return new BasicRTree(maxElements_per_cells, splitMade);
    }

    /**
     * Create a R*Tree.
     * 
     * @param maxElements_per_cells
     * @return R*Tree.
     */
    public static Tree createStarRTree(final int maxElements_per_cells, CoordinateReferenceSystem crs) {
        return new StarRTree(maxElements_per_cells, crs);
    }

    /**
     * Create Hilbert R-Tree.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: cells number per leaf = 2^(2*hilbertOrder).</strong> 
     * </font></blockquote>
     * 
     * @param maxElements_per_cells
     * @param hilbertOrder subdivision leaf order.
     * @return Hilbert R-Tree.
     */
    public static Tree createHilbertRTree2D(final int maxElements_per_cells, final int hilbertOrder) {
        return new HilbertRTree(maxElements_per_cells, hilbertOrder);
    }
}
