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
import org.geotoolkit.index.tree.StoreIndexException;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;

/**
 * Create a BasicRTree Test suite in computer memory in a Geographic 3D space.
 *
 * @author Remi Marechal (Geomatys).
 * @see DefaultGeographicCRS#
 */
public final class MemoryBasicGeo3DTest extends MemoryBasicRTreeTest {

    /**
     * Create a memory BasicRTree Test suite in a Geographic 3D space.
     *
     * @throws StoreIndexException should never thrown.
     * @throws IOException should never thrown.
     */
    public MemoryBasicGeo3DTest() throws StoreIndexException, IOException {
        super(AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic3D()).forConvention(AxesConvention.RIGHT_HANDED));
    }
}
