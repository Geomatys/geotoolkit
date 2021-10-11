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
package org.geotoolkit.index.tree.star;

import java.io.IOException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.referencing.crs.PredefinedCRS;

/**
 * Create a StarRTree Test suite in computer memory in a Cartesian 2D space.
 *
 * @author Remi Marechal (Geomatys).
 * @see PredefinedCRS#CARTESIAN_2D
 */
public final class MemoryStar2DTest extends MemoryStarRTreeTest {

    /**
     * Create a memory StarRTree Test suite in a Cartesian 2D space.
     *
     * @throws StoreIndexException should never thrown.
     * @throws IOException should never thrown.
     */
    public MemoryStar2DTest() throws StoreIndexException, IOException {
        super(PredefinedCRS.CARTESIAN_2D);
    }
}
