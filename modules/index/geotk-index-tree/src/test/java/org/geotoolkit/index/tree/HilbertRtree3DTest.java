/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.index.tree.calculator.DefaultCalculator;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.nodefactory.TreeNodeFactory;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author rmarech
 */
public class HilbertRtree3DTest extends TreeTest{

    public HilbertRtree3DTest() throws TransformException {
        super(new HilbertRTree(4, 2, DefaultEngineeringCRS.CARTESIAN_3D, DefaultCalculator.CALCULATOR_3D, TreeNodeFactory.DEFAULT_FACTORY),
                                     DefaultEngineeringCRS.CARTESIAN_3D);
    }


    /**
     * Some elements inserted in Hilbert R-Tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsert() throws IllegalArgumentException, TransformException {
        super.insertTest();
    }

    /**
     * Verify all node boundary from its subnode boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testCheckBoundary() throws IllegalArgumentException, TransformException {
        super.checkBoundaryTest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkBoundaryNode(final Node node) {
        final List<Envelope> lS = new ArrayList<Envelope>();
        if (node.isLeaf()) {
            for (Node no : node.getChildren()) {
                if (!no.isEmpty()) {
                    lS.addAll(no.getEntries());
                }
            }
        } else {
            for (Node no : node.getChildren()) {
                lS.add(no.getBoundary());
            }
        }
        return (DefaultTreeUtils.getEnveloppeMin(lS).equals(node.getBoundary()));
    }

    /**
     * Test search query inside tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryInside() throws IllegalArgumentException, TransformException {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOutside() throws IllegalArgumentException, TransformException {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOnBorder() throws IllegalArgumentException, TransformException {
        super.queryOnBorderTest();
    }

    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsertDelete() throws IllegalArgumentException, TransformException {
        super.insertDelete();
    }
}
