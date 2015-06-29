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
package org.geotoolkit.index.tree.star;

import java.io.IOException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.referencing.crs.PredefinedCRS;

/**
 * Create a generic StarRTree Test suite where Tree is store into byte array in 3D Cartesian space.<br/>
 * Test is effectuate with byte array already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 * @see PredefinedCRS#CARTESIAN_3D
 */
public final class ReadChannelAccessStarTest3D extends ReadChannelAccessStarTest {

    /**
     * Create a generic StarRTree Test suite with byte array already filled by tree architecture in 3D cartesian space.
     *
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link StarRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public ReadChannelAccessStarTest3D() throws IOException, StoreIndexException, ClassNotFoundException {
        super(PredefinedCRS.CARTESIAN_3D, true);
    }
}
