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
package org.geotoolkit.referencing.operation.projection;

import java.awt.geom.AffineTransform;
import org.geotoolkit.referencing.operation.provider.Mercator1SP;
import org.junit.*;

import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.projection.ProjectionTestBase.parameters;


/**
 * Tests the {@link ProjectionParameters} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class ProjectionParametersTest extends Assert {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Tests a {@link ProjectionParameters} initialized for a sphere with no other parameter.
     * Values for other parameters are assigned manually, and affine transforms created from
     * them.
     */
    @Test
    public void testSphere() {
        UnitaryProjection.Parameters parameters = parameters(Mercator1SP.PARAMETERS, false);
        assertTrue(parameters.isSpherical());
        assertEquals(6371007.0, parameters.semiMajor,  0.0);
        assertEquals(6371007.0, parameters.semiMinor,  0.0);
        assertEquals(0.0, parameters.latitudeOfOrigin, 0.0);
        assertEquals(0.0, parameters.centralMeridian,  0.0);
        assertEquals(1.0, parameters.scaleFactor,      0.0);
        assertEquals(0.0, parameters.falseNorthing,    0.0);
        assertEquals(0.0, parameters.falseEasting,     0.0);
        assertEquals(0,   parameters.standardParallels.length);

        parameters.centralMeridian = -8;
        parameters.falseNorthing = 1600;
        parameters.falseEasting  = 4000;
        parameters.scaleFactor   = 0.25;

        final AffineTransform   normalize = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        assertEquals(AffineTransform.TYPE_IDENTITY,   normalize.getType());
        assertEquals(AffineTransform.TYPE_IDENTITY, denormalize.getType());

        parameters.validate();
        final int expectedType = AffineTransform.TYPE_UNIFORM_SCALE | AffineTransform.TYPE_TRANSLATION;
        assertEquals(expectedType, normalize.getType());
        assertEquals(1,  toDegrees(normalize.getScaleX()),     TOLERANCE);
        assertEquals(1,  toDegrees(normalize.getScaleY()),     TOLERANCE);
        assertEquals(8,  toDegrees(normalize.getTranslateX()), TOLERANCE);
        assertEquals(0,  toDegrees(normalize.getTranslateY()), 0.0);

        final double a = parameters.semiMajor;
        assertEquals(expectedType, denormalize.getType());
        assertEquals(0.25, denormalize.getScaleX() / a, TOLERANCE);
        assertEquals(0.25, denormalize.getScaleY() / a, TOLERANCE);
        assertEquals(4000, denormalize.getTranslateX(), 0.0);
        assertEquals(1600, denormalize.getTranslateY(), 0.0);
    }
}
