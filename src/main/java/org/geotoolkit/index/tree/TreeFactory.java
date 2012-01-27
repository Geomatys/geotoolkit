/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.star.StarRTree;

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
    public static Tree createStarRTree2D(final int maxElements_per_cells) {
        return new StarRTree(maxElements_per_cells);
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
