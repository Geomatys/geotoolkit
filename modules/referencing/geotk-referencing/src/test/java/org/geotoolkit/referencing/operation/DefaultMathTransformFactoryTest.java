/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.util.Set;

import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.NoSuchIdentifierException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.provider.Mercator1SP;

import org.junit.*;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Projection;
import static org.junit.Assert.*;


/**
 * Tests the regitration of transforms in {@link DefaultMathTransformFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class DefaultMathTransformFactoryTest {
    /**
     * The factory being tested.
     */
    private DefaultMathTransformFactory factory;

    /**
     * Sets the {@link #factory} value.
     */
    @Before
    public void fetchFactory() {
        factory = (DefaultMathTransformFactory) FactoryFinder.getMathTransformFactory(
                new Hints(Hints.MATH_TRANSFORM_FACTORY, DefaultMathTransformFactory.class));
    }

    /**
     * Tests the {@link DefaultMathTransformFactory#getOperationMethod} method.
     *
     * @throws NoSuchIdentifierException Should never happen.
     */
    @Test
    public void testGetOperationMethod() throws NoSuchIdentifierException {
        // A projection
        OperationMethod method = factory.getOperationMethod("Mercator_1SP");
        assertTrue(method instanceof Mercator1SP);

        // A conversion which is not a projection.
        method = factory.getOperationMethod("Affine");
        assertTrue(method instanceof Affine);
    }

    /**
     * Tests the {@link DefaultMathTransformFactory#getAvailableMethods} method.
     */
    @Test
    public void testGetAvailableMethods() {
        final Set<OperationMethod> transforms  = factory.getAvailableMethods(null);
        final Set<OperationMethod> conversions = factory.getAvailableMethods(Conversion.class);
        final Set<OperationMethod> projections = factory.getAvailableMethods(Projection.class);

        assertFalse(transforms .isEmpty());
        assertFalse(conversions.isEmpty());
        assertFalse(projections.isEmpty());

        assertTrue("Conversions should be a subset of transforms.",  conversions.size() < transforms .size());
        assertTrue("Projections should be a subset of conversions.", projections.size() < conversions.size());

        assertTrue(transforms .containsAll(conversions));
        assertTrue(conversions.containsAll(projections));
    }
}
