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

/**
 * Create R*Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class StarRTreeTest extends TreeTest {

    public StarRTreeTest() {
        super(new StarRTree(4));
    }

    /**
     * Some elements inserted in Hilbert R-Tree.
     */
    public void testInsert() {
        super.insertTest();
    }

    public void testCheckBoundary(){
        super.checkBoundaryTest();
    }
    
    /**
     * Test search query inside tree.
     */
    public void testQueryInside() {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     */
    public void testQueryOutside() {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border. 
     */
    public void testQueryOnBorder() {
        super.queryOnBorderTest();
    }

    /**
     * Test query with search area contain all tree boundary. 
     */
    public void testQueryAll() {
        super.queryAllTest();
    }

    /**
     * Test insertion and deletion in tree.
     */
    public void testInsertDelete() {
        super.insertDelete();
    }
}
