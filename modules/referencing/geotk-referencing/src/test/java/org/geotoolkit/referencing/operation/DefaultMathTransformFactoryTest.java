/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import org.opengis.util.NoSuchIdentifierException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.provider.Mercator1SP;
import static org.geotoolkit.referencing.operation.DefaultMathTransformFactory.isDeprecated;

import org.junit.*;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Projection;
import static org.junit.Assert.*;


/**
 * Tests the registration of transforms in {@link DefaultMathTransformFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
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

        // Same than above, using EPSG code.
        assertSame(method, factory.getOperationMethod("EPSG:9804"));

        // A conversion which is not a projection.
        method = factory.getOperationMethod("Affine");
        assertTrue(method instanceof Affine);

        // Same than above, using EPSG code.
        assertSame(method, factory.getOperationMethod("EPSG:9624"));
    }

    /**
     * Tests non-existent operation method.
     *
     * @throws NoSuchIdentifierException The expected exception.
     *
     * @since 3.16
     */
    @Test(expected = NoSuchIdentifierException.class)
    public void testNonExistentCode() throws NoSuchIdentifierException {
        factory.getOperationMethod("EPXX:9624");
    }

    /**
     * Asks for names which are known to be duplicated. One of the duplicated elements
     * is deprecated. Geotk shall return the non-deprecated one.
     *
     * @throws NoSuchIdentifierException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testDuplicatedNames() throws NoSuchIdentifierException {
        final OperationMethod ellipsoidal = factory.getOperationMethod("EPSG:1028");
        final OperationMethod spherical   = factory.getOperationMethod("EPSG:1029");
        final OperationMethod deprecated  = factory.getOperationMethod("EPSG:9823");
        // Following should intentionally be tested immediately after EPSG:9823.
        assertSame(spherical, factory.getOperationMethod("Equidistant Cylindrical (Spherical)"));
        assertSame("EPSG:1028 and 1029 are implemented by the same class.", ellipsoidal, spherical);
        assertNotSame("Deprecated methods have their one implementation.", ellipsoidal, deprecated);

        assertFalse(isDeprecated(ellipsoidal, "Equidistant Cylindrical"));
        assertFalse(isDeprecated(spherical,   "Equidistant Cylindrical (Spherical)"));
        assertTrue (isDeprecated(deprecated,  "Equidistant Cylindrical (Spherical)"));

        assertSame(spherical,   factory.getOperationMethod("Equidistant Cylindrical (Spherical)"));
        assertSame(ellipsoidal, factory.getOperationMethod("Equidistant Cylindrical"));
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
