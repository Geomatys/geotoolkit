/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.star.StarRTree;

/**
 *
 * @author rmarech
 */
public final class TreeFactory {

    private TreeFactory() {
    }
    
    public static Tree createTree(TreeCase userChoice, int maxElements_per_cells){
        switch(userChoice){
            case R_TREE2D_LINEAR_SPLIT : {
                return createBasicRTree2D(SplitCase.LINEAR, maxElements_per_cells);
            }
            case R_TREE2D_QUADRATIC_SPLIT : {
                return createBasicRTree2D(SplitCase.QUADRATIC, maxElements_per_cells);
            }
            case STAR_RTREE2D : {
                return createStarRTree2D(maxElements_per_cells);
            }
        }
        return null;
    }
    
    private static Tree createBasicRTree2D(SplitCase splitMade, int maxElements_per_cells){
        return new BasicRTree(maxElements_per_cells, splitMade);
    }
    private static Tree createStarRTree2D(int maxElements_per_cells){
        return new StarRTree(maxElements_per_cells);
    }
}
