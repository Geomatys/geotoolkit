/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.awt.geom.Point2D;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.test.Depend;
import org.junit.*;


/**
 * Tests the {@link AffineTransform2D} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
@Depend(AbstractMathTransformTest.class)
public final class AffineTransform2DTest extends TransformTestBase {
    /**
     * Creates a new test suite.
     */
    public AffineTransform2DTest() {
        super(AffineTransform2D.class, null);
    }

    /**
     * Tests the {@link AffineTransform2D#derivative(Point2D)} method.
     * Actually this is more a test for the {@link TransformTestBase}
     * {@code checkDerivative} methods than a test of affine transforms...
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDerivative() throws TransformException {
        /*
         * Create any kind of transform having different coefficients for every values.
         */
        final AffineTransform2D tr = new AffineTransform2D();
        tr.mutable = true;
        tr.scale(-8, 12);
        tr.translate(42, 50);
        tr.rotate(0.2);
        tr.mutable = false;
        /*
         * Tests derivative.
         */
        transform = tr;
        tolerance = 1E-10;
        derivativeDeltas = new double[] {3, 3};
        verifyDerivative(-10, 5);
    }
}
