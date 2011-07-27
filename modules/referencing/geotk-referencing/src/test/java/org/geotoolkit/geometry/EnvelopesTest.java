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
package org.geotoolkit.geometry;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.CRS_Test;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the utility methods in the {@link Envelopes} class. Note that this requires
 * proper working of coordinate transformations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.19
 *
 * @since 3.19 (derived from 3.00)
 */
@Depend(CRS_Test.class)
public final class EnvelopesTest extends ReferencingTestBase {
    /**
     * Tests the transformations of an envelope.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEnvelopeTransformation() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.PROJCS_UTM_10N);
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
        final MathTransform2D crsTransform = (MathTransform2D) CRS.findMathTransform(WGS84, mapCRS, true);
        assertFalse(crsTransform.isIdentity());

        final GeneralEnvelope originalEnvelope, transformedEnvelope, confirmEnvelope;
        originalEnvelope = new GeneralEnvelope(new double[] {-124, 42}, new double[] {-122, 43});
        originalEnvelope.setCoordinateReferenceSystem(WGS84);

        transformedEnvelope = Envelopes.transform(crsTransform, originalEnvelope);
        transformedEnvelope.setCoordinateReferenceSystem(mapCRS);

        confirmEnvelope = Envelopes.transform(crsTransform.inverse(), transformedEnvelope);
        confirmEnvelope.setCoordinateReferenceSystem(WGS84);

        assertTrue("The transformed envelope should not be smaller than the original one.",
                   confirmEnvelope.contains(originalEnvelope, true));
        assertTrue("The transformed envelope should be almost equals to the original one, maybe sligtly bigger.",
                   confirmEnvelope.equals(originalEnvelope, 0.02, true));
        /*
         * Comparison with the API working on Rectangle2D instead than Envelope.
         */
        final Rectangle2D originalRect    = originalEnvelope.toRectangle2D();
        final Rectangle2D transformedRect = Envelopes.transform(crsTransform, originalRect, null);
        final Rectangle2D confirmRect     = Envelopes.transform(crsTransform.inverse(), transformedRect, null);
        assertEquals(   originalEnvelope.toRectangle2D(),    originalRect);
        assertEquals(transformedEnvelope.toRectangle2D(), transformedRect);
        assertEquals(    confirmEnvelope.toRectangle2D(),     confirmRect);
    }

    /**
     * Tests the transformations of a rectangle using a coordinate operation.
     * With assertions enabled, this also test the transformation of an envelope.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransformationOverPole() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.PROJCS_POLAR_STEREOGRAPHIC);
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
        final CoordinateOperation operation =
                CRS.getCoordinateOperationFactory(false).createOperation(mapCRS, WGS84);
        final MathTransform transform = operation.getMathTransform();
        assertTrue(transform instanceof MathTransform2D);
        /*
         * The rectangle to test, which contains the South pole.
         */
        Rectangle2D envelope = XRectangle2D.createFromExtremums(
                -3943612.4042124213, -4078471.954436003,
                 3729092.5890516187,  4033483.085688618);
        /*
         * This is what we get without special handling of singularity point.
         * Note that is doesn't include the South pole as we would expect.
         */
        Rectangle2D expected = XRectangle2D.createFromExtremums(
                -178.49352310409273, -88.99136583196398,
                 137.56220967463082, -40.905775004205864);
        /*
         * Tests what we actually get.
         */
        Rectangle2D actual = Envelopes.transform((MathTransform2D) transform, envelope, null);
        assertTrue("Transform without axes information.", XRectangle2D.equalsEpsilon(expected, actual));
        assertEquals("Same transform, using the API on Envelope objects.", actual,
                Envelopes.transform(transform, new GeneralEnvelope(envelope)).toRectangle2D());
        /*
         * Using the transform(CoordinateOperation, ...) method,
         * the singularity at South pole is taken in account.
         */
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -40.905775004205864);
        actual = Envelopes.transform(operation, envelope, actual);
        assertTrue("Transform with axes information.", XRectangle2D.equalsEpsilon(expected, actual));
        assertEquals("Same transform, using the API on Envelope objects.", actual,
                Envelopes.transform(operation, new GeneralEnvelope(envelope)).toRectangle2D());
        /*
         * Another rectangle containing the South pole, but this time the south
         * pole is almost in a corner of the rectangle
         */
        envelope = XRectangle2D.createFromExtremums(-4000000, -4000000, 300000, 30000);
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -41.03163170198091);
        actual = Envelopes.transform(operation, envelope, actual);
        assertTrue("South pole in corner.", XRectangle2D.equalsEpsilon(expected, actual));
        assertEquals("Same transform, using the API on Envelope objects.", actual,
                Envelopes.transform(operation, new GeneralEnvelope(envelope)).toRectangle2D());
        /*
         * Another rectangle with the South pole close to the border.
         * This test should execute the step #3 in the transform method code.
         */
        envelope = XRectangle2D.createFromExtremums(-2000000, -1000000, 200000, 2000000);
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -64.3861643256928);
        actual = Envelopes.transform(operation, envelope, actual);
        assertTrue("South pole close to a border.", XRectangle2D.equalsEpsilon(expected, actual));
        assertEquals("Same transform, using the API on Envelope objects.", actual,
                Envelopes.transform(operation, new GeneralEnvelope(envelope)).toRectangle2D());
    }

    /**
     * Tests the transformations of an envelope from a 4D CRS to a 2D CRS
     * where the ordinates in one dimension are NaN.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransformation4to2D() throws TransformException {
        final CoordinateReferenceSystem crs = new DefaultCompoundCRS("4D CRS",
                DefaultGeographicCRS.WGS84,
                DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
                DefaultTemporalCRS.JAVA);

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -170, 170);
        env.setRange(1, -80,   80);
        env.setRange(2, -50,  -50);
        env.setRange(3, Double.NaN, Double.NaN);
        assertFalse(env.isNull());
        assertTrue(env.isEmpty());
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);
        assertSame(DefaultGeographicCRS.WGS84, crs2D);
        final Envelope env2D = Envelopes.transform(env, crs2D);
        /*
         * If the referencing framework has selected the CopyTransform implementation
         * as expected, then the envelope ordinates should not be NaN.
         */
        assertEquals(-170, env2D.getMinimum(0), 0);
        assertEquals( 170, env2D.getMaximum(0), 0);
        assertEquals( -80, env2D.getMinimum(1), 0);
        assertEquals(  80, env2D.getMaximum(1), 0);
    }
}
