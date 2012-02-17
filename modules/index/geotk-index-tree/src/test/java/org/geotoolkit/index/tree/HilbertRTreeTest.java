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

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Create Hilbert R-Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class HilbertRTreeTest extends TreeTest {

    public HilbertRTreeTest() {
        super(new HilbertRTree(4, 2));
    }

    /**
     * Some elements inserted in Hilbert R-Tree.
     */
    @Test
    public void testInsert() {
        super.insertTest();
    }

    /**
     * Verify all node boundary from its subnode boundary.
     */
    @Test
    public void testCheckBoundary() {
        super.checkBoundaryTest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkBoundaryNode(final Node2D node) {
        final List<Shape> lS = new ArrayList<Shape>();
        if (node.isLeaf()) {
            for (Node2D no : (List<Node2D>) node.getUserProperty("cells")) {
                if (!no.isEmpty()) {
                    assertTrue(super.checkBoundaryNode(no));
                    lS.add(no.getBoundary());
                }
            }
        } else {
            for (Node2D no : node.getChildren()) {
                lS.add(no.getBoundary());
            }
        }
        return (TreeUtils.getEnveloppeMin(lS).getBounds2D().equals(node.getBoundary().getBounds2D()));
    }

    /**
     * Test search query inside tree.
     */
    @Test
    public void testQueryInside() {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     */
    @Test
    public void testQueryOutside() {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border. 
     */
    @Test
    public void testQueryOnBorder() {
        super.queryOnBorderTest();
    }

    /**
     * Test query with search area contain all tree boundary. 
     */
    @Test
    public void testQueryAll() {
        super.queryAllTest();
    }

    /**
     * Test insertion and deletion in tree.
     */
    @Test
    public void testInsertDelete() {
        super.insertDelete();
    }
}
