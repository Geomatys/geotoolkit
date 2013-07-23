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
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

/**Create R-Tree test suite in geographic 3D space.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicRTreeGeo3DTest extends SpatialTreeTest {

    public BasicRTreeGeo3DTest() throws StoreIndexException, IOException {
        super(DefaultGeographicCRS.WGS84_3D);
        tree = new BasicRTree(4, crs, SplitCase.QUADRATIC, tEM);
    }
}
