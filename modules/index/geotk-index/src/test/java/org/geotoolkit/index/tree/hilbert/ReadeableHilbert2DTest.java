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
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 * Create a generic HilbertRTree Test suite where Tree is store on hard drive in 2D Cartesian space.<br/>
 * Test is effectuate with file already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 * @see DefaultEngineeringCRS#CARTESIAN_2D
 */
public final class ReadeableHilbert2DTest extends ReadeableHilbertRTreeTest {

    /**
     * Create a generic HilbertRTree Test suite with file already filled by tree architecture in 2D cartesian space.
     * 
     * @param crs
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public ReadeableHilbert2DTest() throws StoreIndexException, IOException, ClassNotFoundException {
        super(DefaultEngineeringCRS.CARTESIAN_2D);
    }
}
