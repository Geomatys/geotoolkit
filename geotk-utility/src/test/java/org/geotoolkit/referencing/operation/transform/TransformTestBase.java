/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import org.apache.sis.referencing.factory.GeodeticObjectFactory;
import org.apache.sis.referencing.operation.transform.DefaultMathTransformFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.test.referencing.TransformTestCase;

import org.geotoolkit.factory.Hints;

import static org.junit.Assert.*;


/**
 * Base class for tests of {@link MathTransform} implementations. This base class inherits
 * the convenience methods defined in GeoAPI and adds a few {@code asserts} statements.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public abstract class TransformTestBase extends TransformTestCase {
    /**
     * The coordinate reference system factory to use for testing.
     */
    protected final CRSFactory crsFactory;

    /**
     * The math transform factory to use for testing.
     */
    protected final MathTransformFactory mtFactory;

    /**
     * Creates a new test case using the given hints for fetching the factories.
     *
     * @param type  The base class of the transform being tested.
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected TransformTestBase(final Class<? extends MathTransform> type, final Hints hints) {
        crsFactory = GeodeticObjectFactory.provider();
        mtFactory  = DefaultMathTransformFactory.provider();
        assertTrue("Tests should be run with assertions enabled.", type.desiredAssertionStatus());
    }
}
