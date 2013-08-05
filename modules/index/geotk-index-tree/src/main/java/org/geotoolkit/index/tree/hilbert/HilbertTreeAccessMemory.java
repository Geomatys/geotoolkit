/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.access.TreeAccessMemory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class HilbertTreeAccessMemory extends TreeAccessMemory {

    public HilbertTreeAccessMemory(int maxElements, int hilbertOrder, CoordinateReferenceSystem crs) {
        super(maxElements, crs);
        super.hilbertOrder = hilbertOrder;
    }

    @Override
    public Node createNode(double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        final int currentID = (recycleID.isEmpty()) ? nodeId++ : recycleID.remove(0);
        try {
            return new HilbertNode(this, currentID, boundary, properties, parentId, siblingId, childId);
        } catch (IOException ex) {
            Logger.getLogger(HilbertTreeAccessMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
