/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.shape;

import java.awt.Shape;
import java.awt.geom.Path2D;

import org.geotoolkit.test.gui.ShapeTestBase;

import org.junit.*;


/**
 * Tests the shape implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public final strictfp class ShapeTest extends ShapeTestBase {
    /**
     * Tests the {@link Arrow2D} shape.
     */
    @Test
    public void testArrow2D() {
        final Shape shape = new Arrow2D(SHAPE_X, SHAPE_Y, SHAPE_WIDTH, SHAPE_HEIGHT);
        final Path2D reference = new Path2D.Double(shape);
        testContainsAndIntersectsMethods(reference, shape);
        show(shape, reference, true);
    }
}
