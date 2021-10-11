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
 * Create a generic Hilbert Test suite where Tree is store into byte array in 2D Cartesian space.<br/>
 * Test is effectuate with byte array already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 * @see PredefinedCRS#CARTESIAN_2D
 * @see HilbertChannelTreeAccess
 */
public final class ReadChannelAccessHilbert2DTest extends ReadChannelAccessHilbertTest {

    public ReadChannelAccessHilbert2DTest() throws IOException, StoreIndexException, ClassNotFoundException {
        super(PredefinedCRS.CARTESIAN_2D, true);
    }

}
