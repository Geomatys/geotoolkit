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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.index.tree.calculator.DefaultCalculator;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Create Hilbert R-Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class HilbertRTree2DTest extends TreeTest {

    public HilbertRTree2DTest() throws TransformException {
        super(new HilbertRTree(4, 1, DefaultEngineeringCRS.CARTESIAN_2D, DefaultCalculator.CALCULATOR_2D), DefaultEngineeringCRS.CARTESIAN_2D);
    }

    /**
     * Some elements inserted in Hilbert R-Tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsert() throws TransformException {
        super.insertTest();
    }

    /**
     * Verify all node boundary from its subnode boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testCheckBoundary() throws TransformException {
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
    public void testQueryInside() throws TransformException {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOutside() throws TransformException {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOnBorder() throws TransformException {
        super.queryOnBorderTest();
    }

    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsertDelete() throws TransformException {
        super.insertDelete();
    }
}
