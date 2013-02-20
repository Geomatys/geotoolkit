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
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.nodefactory.DefaultNodeFactory;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.operation.TransformException;

/**
 * Create R-Tree (basic) test suite in 3D.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicRTree3DTest extends SpatialTreeTest {

    public BasicRTree3DTest() throws IllegalArgumentException, TransformException {
        super(new BasicRTree(4, DefaultEngineeringCRS.CARTESIAN_3D, SplitCase.QUADRATIC, DefaultNodeFactory.INSTANCE));
    }
}
