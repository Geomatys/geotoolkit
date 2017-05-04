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

import org.geotoolkit.referencing.crs.PredefinedCRS;


/**
 * Test open close without any data and try to re-open.
 *
 * @author Remi Marechal (Geomatys)
 */
public final class EmptyReadeableStarRTree2D extends ReadeableStarRTreeTest {
    public EmptyReadeableStarRTree2D() throws Exception {
        super(PredefinedCRS.CARTESIAN_2D, false);
    }
}
