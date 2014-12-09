/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.index.tree.star;

import java.io.IOException;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.referencing.crs.PredefinedCRS;

/**
 * Test open close without any data and try to re-open.
 *
 * @author Remi Marechal (Geomatys)
 */
public class EmptyWritableStarRTree2D extends WritableStarRTreeTest {

    public EmptyWritableStarRTree2D() throws StoreIndexException, IOException {
        super(PredefinedCRS.CARTESIAN_2D);
        tree.close();
        tEM.close();
        
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileStarRTree(inOutFile, 4, crs, tEM);
    }
}
