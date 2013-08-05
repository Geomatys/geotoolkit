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

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.io.AbstractTreeTest;
import org.geotoolkit.index.tree.io.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

/**
 * Create R-Tree test suite in geographic 2D space.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class BasicRTreeGeo2DTest extends AbstractTreeTest {

    public BasicRTreeGeo2DTest() throws StoreIndexException, IOException {
        super(DefaultGeographicCRS.WGS84);
        tEM  = new FileTreeElementMapperTest(crs, File.createTempFile("test", "mapper"));
        tree = new FileBasicRTree(File.createTempFile("test", "tree"), 3, crs, SplitCase.QUADRATIC, tEM);
        tAF  = ((BasicRTree)tree).getTreeAccess();
    }
}
