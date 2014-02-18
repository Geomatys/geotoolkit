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
package org.geotoolkit.index.tree.hilbert;

import java.io.IOException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapperTest;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create a generic HilbertRTree Test suite where Tree is store in computer memory.
 *
 * @author Remi Marechal (Geomatys).
 */
abstract class MemoryHilbertRTreeTest extends HilbertTest {

    /**
     * Create a generic HilbertRTree Test suite, stored in memory,  with {@link CoordinateReferenceSystem} define by user.
     * 
     * @param crs
     * @throws StoreIndexException should never thrown.
     * @throws IOException should never thrown.
     */
    protected MemoryHilbertRTreeTest(final CoordinateReferenceSystem crs) throws StoreIndexException, IOException {
        super(new MemoryHilbertRTree(4, 2, crs, new TreeElementMapperTest(crs)));
    }
}
