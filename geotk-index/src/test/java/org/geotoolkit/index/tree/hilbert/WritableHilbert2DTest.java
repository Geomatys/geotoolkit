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
import org.geotoolkit.referencing.crs.PredefinedCRS;

/**
 * Create a HilbertRTree Test suite stored on hard drive in a Cartesian 2D space.
 *
 * @author Remi Marechal (Geomatys).
 * @see PredefinedCRS#CARTESIAN_2D
 */
public final class WritableHilbert2DTest extends WritableHilbertRTreeTest {

    /**
     * Create a HilbertRTree Test suite in a Cartesian 2D space stored on hard drive.
     *
     * @throws StoreIndexException should never thrown.
     * @throws IOException should never thrown.
     */
    public WritableHilbert2DTest() throws StoreIndexException, IOException {
        super(PredefinedCRS.CARTESIAN_2D);
    }
}
