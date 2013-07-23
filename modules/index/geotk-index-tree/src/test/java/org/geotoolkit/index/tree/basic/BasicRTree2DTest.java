/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.index.tree.basic;

import java.io.IOException;
import org.geotoolkit.index.tree.SpatialTreeTest;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 * Create (Basic) R-Tree test suite in 2D.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicRTree2DTest extends SpatialTreeTest {

    public BasicRTree2DTest() throws StoreIndexException, IOException {
        super( DefaultEngineeringCRS.CARTESIAN_2D);
        tree = new BasicRTree(4, crs, SplitCase.QUADRATIC, tEM);
    }
}
