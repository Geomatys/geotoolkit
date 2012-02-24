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
import org.geotoolkit.index.tree.calculator.DefaultCalculator;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;

/**
 * Create (Basic) R-Tree test suite in 2D.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicRTree2DTest extends TreeTest {

    public BasicRTree2DTest() throws TransformException {
        super(new BasicRTree(4, DefaultEngineeringCRS.CARTESIAN_2D, SplitCase.QUADRATIC, DefaultCalculator.CALCULATOR_2D), DefaultEngineeringCRS.CARTESIAN_2D);
    }

    /**
     * Some elements inserted Tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsert() throws TransformException {
        super.insertTest();
    }

    /**
     * Verify all boundary Node from its "children" Node.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testCheckBoundary() throws TransformException {
        super.checkBoundaryTest();
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
