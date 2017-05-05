/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
 * Create a generic HilbertRTree Test suite where Tree architecture is stored into byte array in 3D Cartesian space.
 *
 * @author Remi Marechal (Geomatys).
 * @see PredefinedCRS#CARTESIAN_3D
 * @see HilbertAccessByteArray
 */
public final class WriteChannelAccessHilbert3DTest extends WriteChannelAccessHilbertTest {

    /**
     * Create a HilbertRTree Test suite in a Cartesian 3D space stored into byte array.
     *
     * @throws StoreIndexException
     * @throws IOException
     */
    public WriteChannelAccessHilbert3DTest() throws StoreIndexException, IOException {
        super(PredefinedCRS.CARTESIAN_3D);
    }
}
