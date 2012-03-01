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
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.nodefactory.NodeFactory;
import org.geotoolkit.index.tree.star.StarRTree;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**Create chosen tree.
 *
 * @author Rémi Marechal (Géomatys).
 */
public final class TreeFactory {

    private TreeFactory() {
    }

    /**Create a Basic R-Tree ({@linkplain BasicRTree).
     * 
     * @param maxElements_per_leaf : max elements number within each tree Node.
     * @param crs                   : associate coordinate system.
     * @param splitMade             : made to split.
     * @param calculator            : calculator associate to define internal computing.
     * @param nodefactory           : made to create tree {@code Node}. 
     * @return Basic RTree.
     */
    public static Tree createBasicRTree(final int maxElements_per_leaf, final CoordinateReferenceSystem crs, 
                        final SplitCase splitMade, final Calculator calculator, final NodeFactory nodefactory) {
        return new BasicRTree(maxElements_per_leaf, crs, splitMade, calculator, nodefactory);
    }

    /**Create a R*Tree ({@linkplain StarRTree).
     * 
     * @param maxElements_per_leaf : max elements number within each tree Node.
     * @param crs                   : associate coordinate system.
     * @param calculator            : calculator associate to define internal computing.
     * @param nodefactory           : made to create tree {@code Node}. 
     * @return R*Tree.
     */
    public static Tree createStarRTree(final int maxElements_per_leaf, final CoordinateReferenceSystem crs,
                                                final Calculator calculator, final NodeFactory nodefactory) {
        return new StarRTree(maxElements_per_leaf, crs, calculator, nodefactory);
    }

    /**Create Hilbert R-Tree.
     *
     * <blockquote><font size=-1> <strong>
     * NOTE: In HilbertRTree each leaf contains some sub-{@code Node} called cells.
     * {@code Envelope} entries are contains in their cells.
     * Cells number per leaf = 2 ^ (dim*hilbertOrder). 
     * Moreother there are maxElements_per_cells 2 ^(dim*hilbertOrder) elements per leaf.
     * </strong> </font></blockquote>
     *
     * @param maxElements_per_cells : max elements number within each tree leaf cells.
     * @param crs                   : associate coordinate system.
     * @param calculator            : calculator associate to define internal computing.
     * @param nodefactory           : made to create tree {@code Node}.
     * @return Hilbert R-Tree.
     */
    public static Tree createHilbertRTree(final int maxElements_per_cells, final int hilbertOrder,
            final CoordinateReferenceSystem crs, final Calculator calculator, final NodeFactory nodefactory) {
        return new HilbertRTree(maxElements_per_cells, hilbertOrder, crs, calculator, nodefactory);
    }
}
