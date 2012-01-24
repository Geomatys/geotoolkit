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
 *
 * @author rmarech
 */
public final class TreeFactory {

    private TreeFactory() {
    }
    
    public static Tree createBasicRTree2D(SplitCase splitMade, int maxElements_per_cells){
        return new BasicRTree(maxElements_per_cells, splitMade);
    }
    public static Tree createStarRTree2D(int maxElements_per_cells){
        return new StarRTree(maxElements_per_cells);
    }
    public static Tree createHilbertRTree2D(int maxElements_per_cells, int hilbertOrder){
        return new HilbertRTree(maxElements_per_cells, hilbertOrder);
    }
}
