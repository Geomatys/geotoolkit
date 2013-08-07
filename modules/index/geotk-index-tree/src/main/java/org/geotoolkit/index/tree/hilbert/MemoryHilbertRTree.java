/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class MemoryHilbertRTree<E> extends HilbertRTree<E> {

    public MemoryHilbertRTree(int maxElements, final int hilbertOrder, CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) throws StoreIndexException {
        super(new HilbertTreeAccessMemory(maxElements, hilbertOrder, crs), treeEltMap);
    }
}
