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
package org.geotoolkit.geometry;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

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
import org.geotoolkit.referencing.operation.DefaultConversion;
import org.geotoolkit.referencing.operation.transform.MathTransformNo2D;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the utility methods in the {@link Envelopes} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.20
 *
 * @since 3.19 (derived from 3.00)
 */
@Depend(CRS_Test.class)
public final strictfp class EnvelopesTest extends ReferencingTestBase {
    /**
     * Small tolerance factor when envelopes calculated in different ways are expected
     * to be almost identical.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests the conversions of a rectangle and compares with the conversions of an envelope.
     * This is a relatively simple test case working in the two-dimensional space only, with
     * a coordinate operation of type "conversion" (not a "transformation") and with no need
     * to adjust for poles.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void compareRectangleAndEnvelopeConversions() throws FactoryException, TransformException {
        final ProjectedCRS      targetCRS     = (ProjectedCRS) CRS.parseWKT(WKT.PROJCS_UTM_10N);
        final GeographicCRS     sourceCRS     = targetCRS.getBaseCRS();
        final Conversion        conversion    = targetCRS.getConversionFromBase();
        final MathTransform2D   transform     = (MathTransform2D) conversion.getMathTransform();
        final MathTransformNo2D transformNo2D = new MathTransformNo2D(transform);
        assertFalse(transform.isIdentity());
        /*
         * Transforms rectangles using MathTransform. Opportunistly check that the
         * transform using a CoordinateOperation object produces the same result.
         */
        final Rectangle2D rectλφ   = XRectangle2D.createFromExtremums(-126, -20, -120, 40);
        final Rectangle2D rectXY   = Envelopes.transform(transform,           rectλφ, null);
        final Rectangle2D rectBack = Envelopes.transform(transform.inverse(), rectXY, null);
        assertRectangleEquals(XRectangle2D.createFromExtremums(
            //  166021.57  -2214294.03   (values from empirical projection of many points)
                166021.56, -2214294.03,
            //  833978.43   4432069.06   (values from empirical projection of many points)
                833978.44,  4432069.06), rectXY, PROJECTED_CENTIMETRE, PROJECTED_CENTIMETRE);
        assertEquals(rectXY, Envelopes.transform(conversion, rectλφ, null));
        assertRectangleEquals(rectλφ, rectBack, 1.0, 0.05);
        /*
         * Transforms envelopes and compare each step with the transformation on rectangles.
         */
        final GeneralEnvelope envelopeλφ = new GeneralEnvelope(rectλφ);
        envelopeλφ.setCoordinateReferenceSystem(sourceCRS);
        assertRectangleEquals(rectλφ, envelopeλφ.toRectangle2D(), GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);

        final GeneralEnvelope envelopeXY = Envelopes.transform(transformNo2D, envelopeλφ);
        envelopeXY.setCoordinateReferenceSystem(targetCRS);
        assertRectangleEquals(rectXY, envelopeXY.toRectangle2D(), PROJECTED_CENTIMETRE, PROJECTED_CENTIMETRE);
        assertTrue(envelopeXY.equals(Envelopes.transform(conversion, envelopeλφ), EPS, true));

        final GeneralEnvelope envelopeBack = Envelopes.transform(transformNo2D.inverse(), envelopeXY);
        envelopeBack.setCoordinateReferenceSystem(sourceCRS);
        assertRectangleEquals(rectBack, envelopeBack.toRectangle2D(), GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);

        assertTrue("Transformed envelope should not be smaller than the original one.", envelopeBack.contains(envelopeλφ, true));
        assertTrue("Final envelope should be only slightly bigger than the original.",  envelopeBack.equals(envelopeλφ, 1.0, false));
    }

    /**
     * Tests the conversions of a rectangle over a pole using a coordinate operation.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testConversionOverPole() throws FactoryException, TransformException {
        final ProjectedCRS      sourceCRS      = (ProjectedCRS) CRS.parseWKT(WKT.PROJCS_POLAR_STEREOGRAPHIC);
        final GeographicCRS     targetCRS      = sourceCRS.getBaseCRS();
        final Conversion        conversion     = inverse(sourceCRS.getConversionFromBase());
        final MathTransform2D   transform      = (MathTransform2D) conversion.getMathTransform();
        final MathTransformNo2D transformNo2D  = new MathTransformNo2D(transform);
        final Conversion        conversionNo2D = new DefaultConversion(conversion, sourceCRS, targetCRS, transformNo2D);
        assertFalse(conversionNo2D.getMathTransform() instanceof MathTransform2D);
        /*
         * The rectangle to test, which contains the South pole.
         */
        Rectangle2D rectangle = XRectangle2D.createFromExtremums(
                -3943612.4042124213, -4078471.954436003,
                 3729092.5890516187,  4033483.085688618);
        /*
         * This is what we get without special handling of singularity point.
         * Note that is doesn't include the South pole as we would expect.
         * The commented out values are what we get by projecting an arbitrary
         * larger amount of points.
         */
        Rectangle2D expected = XRectangle2D.createFromExtremums(
            //  -178.4935231040927  -56.61747883535035
                -179.8650137390031, -88.99136583196396,
            //   178.8122742080059  -40.90577500420587]
                 137.9769431693009, -40.90577500420587);
        /*
         * Tests what we actually get. First, test using the method working on MathTransform.
         * Next, test again the same transform, but using the API on Envelope objects.   The
         * 'transformNo2D' wrapper is a trick for preventing the Envelopes class to delegate
         * the work to the transform(MathTransform2D, ...) method, since we already tested it
         * just before.
         */
        Rectangle2D actual = Envelopes.transform(transform, rectangle, null);
        assertRectangleEquals(expected, actual, GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);
        assertRectangleEquals(actual, Envelopes.transform(transformNo2D, new GeneralEnvelope(rectangle)).toRectangle2D(), EPS, EPS);
        /*
         * Using the transform(CoordinateOperation, ...) method,
         * the singularity at South pole is taken in account.
         */
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -40.905775004205864);
        actual   = Envelopes.transform(conversion, rectangle, actual);
        assertRectangleEquals(expected, actual, GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);
        assertRectangleEquals(actual, Envelopes.transform(conversionNo2D, new GeneralEnvelope(rectangle)).toRectangle2D(), EPS, EPS);
        /*
         * Another rectangle containing the South pole, but this time the south
         * pole is almost in a corner of the rectangle
         */
        rectangle = XRectangle2D.createFromExtremums(-4000000, -4000000, 300000, 30000);
        expected  = XRectangle2D.createFromExtremums(-180, -90, 180, -41.03163170198091);
        actual    = Envelopes.transform(conversion, rectangle, actual);
        assertRectangleEquals(expected, actual, GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);
        assertRectangleEquals(actual, Envelopes.transform(conversionNo2D, new GeneralEnvelope(rectangle)).toRectangle2D(), EPS, EPS);
        /*
         * Another rectangle with the South pole close to the border.
         * This test should execute the step #3 in the transform method code.
         */
        rectangle = XRectangle2D.createFromExtremums(-2000000, -1000000, 200000, 2000000);
        expected  = XRectangle2D.createFromExtremums(-180, -90, 180, -64.3861643256928);
        actual    = Envelopes.transform(conversion, rectangle, actual);
        assertRectangleEquals(expected, actual, GEOGRAPHIC_CENTIMETRE, GEOGRAPHIC_CENTIMETRE);
        assertRectangleEquals(actual, Envelopes.transform(conversionNo2D, new GeneralEnvelope(rectangle)).toRectangle2D(), EPS, EPS);
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
        assertFalse(env.isAllNaN());
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

    /**
     * Tests the conversions of a rectangle with a datum shift. The envelope is transformed twice:
     * once with CRS built from WKT, and once with the CRS fetched from the EPSG database. Those
     * two approaches use different datum shift methods, and experience show that it is worth to
     * check both of them.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDatumShift() throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(sourceCRS);
        env.setRange(0, -3980814, -3113802);
        env.setRange(1,    83461,  1166891);
        final Envelope ex = CRS.transform(env, targetCRS);
        assertEquals(-43.03947, ex.getMinimum(0), 1E-5);
        assertEquals(-31.39226, ex.getMaximum(0), 1E-5);
        assertEquals( 17.99971, ex.getMinimum(1), 1E-5);
        assertEquals( 29.44712, ex.getMaximum(1), 1E-5);
        /*
         * Same envelope transformation, but using the CRS fetched from the EPSG database.
         */
        sourceCRS = CRS.decode("EPSG:27572");
        targetCRS = CRS.decode("EPSG:4326");
        env.setCoordinateReferenceSystem(sourceCRS);
        final Envelope rs = CRS.transform(env, targetCRS);
        assertEquals(ex.getMinimum(1), rs.getMinimum(0), 0.01);
        assertEquals(ex.getMaximum(1), rs.getMaximum(0), 0.01);
        assertEquals(ex.getMinimum(0), rs.getMinimum(1), 0.01);
        assertEquals(ex.getMaximum(0), rs.getMaximum(1), 0.01);
    }

    /**
     * Returns the inverse of the given conversion. This method is not strictly correct since we
     * reuse the properties (name, aliases, etc.) from the given conversion, which is not correct.
     * However those properties are not significant for the purpose of this test.
     */
    private static Conversion inverse(final Conversion conversion) throws NoninvertibleTransformException {
        return new DefaultConversion(conversion, conversion.getTargetCRS(),
                conversion.getSourceCRS(), conversion.getMathTransform().inverse());
    }
}
