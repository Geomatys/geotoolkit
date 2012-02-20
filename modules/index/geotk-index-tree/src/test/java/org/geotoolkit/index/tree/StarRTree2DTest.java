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

import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;

/**Create R*Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class StarRTree2DTest extends TreeTest{

    public StarRTree2DTest() throws TransformException {
        super(new StarRTree(4, DefaultEngineeringCRS.CARTESIAN_2D), DefaultEngineeringCRS.CARTESIAN_2D);
    }
    
    /**
     * Some elements inserted in Hilbert R-Tree.
     */
    @Test
    public void testInsert() throws TransformException {
        super.insertTest();
    }

    @Test
    public void testCheckBoundary() throws TransformException {
        super.checkBoundaryTest();
    }

    /**
     * Test search query inside tree.
     */
    @Test
    public void testQueryInside() throws TransformException {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     */
    @Test
    public void testQueryOutside() throws TransformException {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border. 
     */
    @Test
    public void testQueryOnBorder() throws TransformException {
        super.queryOnBorderTest();
    }

    /**
     * Test insertion and deletion in tree.
     */
    @Test
    public void testInsertDelete() throws TransformException {
        super.insertDelete();
    }
}
