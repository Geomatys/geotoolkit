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
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.ConcatenatedOperation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.referencing.operation.transform.MathTransformTestCase;

import static org.geotoolkit.test.Assert.*;


/**
 * Base class for tests of {@link MathTransform} implementations. This base class inherits
 * the convenience methods defined in GeoAPI and adds a few {@code asserts} statements.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 */
public abstract strictfp class TransformTestBase extends MathTransformTestCase {
    /**
     * Ensures that the {@link TestBase} class has been initialized. We don't really
     * need to flush the output; this is just a lazy way to ensure class initialization.
     */
    static {
        TestBase.flushVerboseOutput();
    }

    /**
     * The datum factory to use for testing.
     */
    protected final DatumFactory datumFactory;

    /**
     * The coordinate reference system factory to use for testing.
     */
    protected final CRSFactory crsFactory;

    /**
     * The math transform factory to use for testing.
     */
    protected final MathTransformFactory mtFactory;

    /**
     * The transformation factory to use for testing.
     */
    protected final CoordinateOperationFactory opFactory;

    /**
     * Creates a new test case using the given hints for fetching the factories.
     *
     * @param type  The base class of the transform being tested.
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected TransformTestBase(final Class<? extends MathTransform> type, final Hints hints) {
        this(FactoryFinder.getDatumFactory(hints),
             FactoryFinder.getCRSFactory(hints),
             FactoryFinder.getMathTransformFactory(hints),
             FactoryFinder.getCoordinateOperationFactory(hints));
        assertTrue("Tests should be run with assertions enabled.", type.desiredAssertionStatus());
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private TransformTestBase(
            final DatumFactory            datumFactory,
            final CRSFactory                crsFactory,
            final MathTransformFactory       mtFactory,
            final CoordinateOperationFactory opFactory)
    {
        super(datumFactory, crsFactory, mtFactory, opFactory);
        this.datumFactory = datumFactory;
        this.mtFactory    = mtFactory;
        this.crsFactory   = crsFactory;
        this.opFactory    = opFactory;
    }

    /**
     * Returns {@code true} if the given operation is, directly or indirectly, a transformation.
     * This method returns {@code true} if the operation is either a {@link Transformation}, or
     * a {@link ConcatenatedOperation} in which at least one step is a transformation.
     *
     * @param  operation The operation to test.
     * @return {@code true} if the given operation is, directly or indirectly, a transformation.
     *
     * @since 3.16
     */
    protected static boolean isTransformation(final CoordinateOperation operation) {
        if (operation instanceof Transformation) {
            return true;
        }
        if (operation instanceof ConcatenatedOperation) {
            for (final SingleOperation step : ((ConcatenatedOperation) operation).getOperations()) {
                if (step instanceof Transformation) {
                    return true;
                }
            }
        }
        return false;
    }
}
