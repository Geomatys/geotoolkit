/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;

import org.geotoolkit.test.referencing.WKT;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;

import org.junit.*;

import static org.junit.Assume.*;
import static org.junit.Assert.*;
import static java.util.Collections.singletonMap;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Same tests than {@link COFactoryUsingMolodenskyTest}, but with the EPSG factory enabled.
 * This class tests whatever {@link CoordinateOperationFactory} implementation is found on the
 * classpath, provided it is backed by an {@link AuthorityBackedFactory} instance.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.16 (derived from 3.07)
 */
public final strictfp class AuthorityBackedFactoryTest extends COFactoryUsingMolodenskyTest {
    /**
     * Creates a new test suite.
     */
    public AuthorityBackedFactoryTest() {
        super(null);
    }

    /**
     * Returns {@code true} since this test suite is using {@link AuthorityBackedFactory}.
     *
     * @since 3.18
     */
    @Override
    protected boolean useAuthorityFactory() {
        return true;
    }

    /**
     * Returns the datum shift method used by this test. This method returns {@code null}
     * because {@link AuthorityBackedFactory} does not declare directly a datum shift method.
     */
    @Override
    protected String getDatumShiftMethod() {
        return null;
    }

    /**
     * Returns the expected result for a transformation using the given sample point.
     * Disables also the inverse transform tests, because they do not have the precision
     * requested by this method.
     */
    @Override
    SamplePoints.Target getExpectedResult(final SamplePoints sample, final boolean withHeight) {
        tolerance = withHeight ? SamplePoints.TOLERANCE : SamplePoints.NOHEIGHT_TOLERANCE;
        isInverseTransformSupported = false;
        return sample.epsg;
    }

    /**
     * Ensures that the factory used is the one expected by this test suite. If the database
     * is not available, then we will skip the tests silently. We don't cause a test failure
     * because the EPSG database is not expected to be installed when Geotk is built for the
     * first time on a new machine.
     */
    @Before
    @Override
    public void ensureProperFactory() {
        assumeTrue(!isUsingDefaultFactory(opFactory));
    }

    /**
     * Tests a transformation from a 2D projection to an other 2D projection which imply a
     * change of prime meridian. The purpose of this test is to isolate the two-dimensional
     * part of the transform tested by {@link #testProjected4D_to2D_withMeridianShift()}.
     * <p>
     * This tests requires the EPSG database, because it requires the coordinate operation
     * path which is defined there.
     *
     * @throws Exception Should never happen.
     *
     * @since 3.16
     */
    @Test
    @Override
    public void testProjected2D_withMeridianShift() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertFalse(transform.isIdentity());
        tolerance = 0.02;
        // Test using the location of Paris (48.856578°N, 2.351828°E)
        // Only after, test using a coordinate different than the prime meridian.
        verifyTransform(new double[] {601124.99, 2428693.45}, new double[] {261804.30, 6218365.72});
        verifyTransform(new double[] {600000.00, 2420000.00}, new double[] {260098.74, 6205194.95});
    }

    /**
     * Tests a transformation from a 4D projection to a 2D projection which imply a change of
     * prime meridian. This is the same test than {@link #testProjected2D_withMeridianShift()},
     * with extra dimension which should be just dropped.
     * <p>
     * This tests requires the EPSG database, because it requires the coordinate operation
     * path which is defined there.
     *
     * @throws Exception Should never happen.
     *
     * @since 3.16
     */
    @Test
    @Ignore
    public void testProjected4D_to2D_withMeridianShift() throws Exception {
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 3D"), sourceCRS, CommonCRS.Vertical.ELLIPSOIDAL.crs());
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 4D"), sourceCRS, CommonCRS.Temporal.MODIFIED_JULIAN.crs());
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertFalse(transform.isIdentity());
        tolerance = 0.02;
        isInverseTransformSupported = false;
        // Same coordinates than testProjected2D_withMeridianShift(),
        // but with random elevation and time which should be dropped.
        verifyTransform(new double[] {601124.99, 2428693.45, 400, 1000}, new double[] {261804.30, 6218365.72});
        verifyTransform(new double[] {600000.00, 2420000.00, 400, 1000}, new double[] {260098.74, 6205194.95});
    }
}
