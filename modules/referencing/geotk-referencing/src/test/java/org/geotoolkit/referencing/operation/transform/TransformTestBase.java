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

import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.referencing.operation.transform.MathTransformTestCase;

import static org.geotoolkit.test.Assert.*;


/**
 * Base class for tests of {@link MathTransform} implementations. This base class inherits
 * the convenience methods defined in GeoAPI and adds a few {@code asserts} statements.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public abstract strictfp class TransformTestBase extends MathTransformTestCase {
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
        this(FactoryFinder.getDatumFactory(hints),
             FactoryFinder.getCRSFactory(hints),
             FactoryFinder.getMathTransformFactory(hints));
        assertTrue("Tests should be run with assertions enabled.", type.desiredAssertionStatus());
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private TransformTestBase(
            final DatumFactory            datumFactory,
            final CRSFactory                crsFactory,
            final MathTransformFactory       mtFactory)
    {
        super(datumFactory, crsFactory, mtFactory);
        this.mtFactory    = mtFactory;
        this.crsFactory   = crsFactory;
    }
}
