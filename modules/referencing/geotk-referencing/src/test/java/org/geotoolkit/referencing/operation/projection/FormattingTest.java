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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.junit.*;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform2D;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests WKT formatting. This test suite do not execute any coordinate transformation.
 * However it relies extensively on {@link AbstractMathTransform2D} implementation,
 * which should be intelligent enough for recognizing that the concatenated transform
 * was created from a single set of parameters.
 *
 * @author Martin Desruisseaux (Geomatys)
 *
 * @since 3.00
 */
public final strictfp class FormattingTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public FormattingTest() {
        super(AbstractMathTransform2D.class, null);
    }

    /**
     * Tests using Mercator projections. We use the same transforms than the one defined
     * in {@link MercatorTest#testMercator1SP} and {@link MercatorTest#testMercator2SP}
     * except that the semi-minor axis length is defined explicitly for safety.
     *
     * @throws FactoryException Should never happen.
     * @throws NoninvertibleTransformException Should never happen.
     */
    @Test
    @Ignore
    public void testMercator() throws FactoryException, NoninvertibleTransformException {
        ParameterValueGroup parameters = mtFactory.getDefaultParameters("Mercator_1SP");
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6356078.9626186555);
        parameters.parameter("Longitude of natural origin").setValue(110.0);
        parameters.parameter("Scale factor at natural origin").setValue(0.997);
        parameters.parameter("False easting").setValue(3900000.0);
        parameters.parameter("False northing").setValue(900000.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertTrue(isInverseTransformSupported);
        String wkt;
        wkt = "PARAM_MT[“Mercator_1SP”,\n" +
              "  PARAMETER[“semi_major”, 6377397.155],\n" +
              "  PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
              "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "  PARAMETER[“central_meridian”, 110.0],\n" +
              "  PARAMETER[“scale_factor”, 0.997],\n" +
              "  PARAMETER[“false_easting”, 3900000.0],\n" +
              "  PARAMETER[“false_northing”, 900000.0]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "INVERSE_MT[PARAM_MT[“Mercator_1SP”,\n" +
                  "    PARAMETER[“semi_major”, 6377397.155],\n" +
                  "    PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
                  "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "    PARAMETER[“central_meridian”, 110.0],\n" +
                  "    PARAMETER[“scale_factor”, 0.997],\n" +
                  "    PARAMETER[“false_easting”, 3900000.0],\n" +
                  "    PARAMETER[“false_northing”, 900000.0]]]";
            assertWktEquals(wkt);
            assertEquals(transform, mtFactory.createFromWKT(wkt));
            transform = transform.inverse();
        }
        /*
         * Add axis swap from (latitude, longitude) order to (longitude, latitude) order.
         */
        final AffineTransform2D swap = new AffineTransform2D(0, 1, 1, 0, 0, 0);
        transform = MathTransforms.concatenate(swap, transform);
        wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.0],\n" +
              "    PARAMETER[“elt_0_1”, 1.0],\n" +
              "    PARAMETER[“elt_1_0”, 1.0],\n" +
              "    PARAMETER[“elt_1_1”, 0.0]],\n" +
              "  PARAM_MT[“Mercator_1SP”,\n" +
              "    PARAMETER[“semi_major”, 6377397.155],\n" +
              "    PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
              "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "    PARAMETER[“central_meridian”, 110.0],\n" +
              "    PARAMETER[“scale_factor”, 0.997],\n" +
              "    PARAMETER[“false_easting”, 3900000.0],\n" +
              "    PARAMETER[“false_northing”, 900000.0]]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "CONCAT_MT[INVERSE_MT[PARAM_MT[“Mercator_1SP”,\n" +
                  "      PARAMETER[“semi_major”, 6377397.155],\n" +
                  "      PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
                  "      PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "      PARAMETER[“central_meridian”, 110.0],\n" +
                  "      PARAMETER[“scale_factor”, 0.997],\n" +
                  "      PARAMETER[“false_easting”, 3900000.0],\n" +
                  "      PARAMETER[“false_northing”, 900000.0]]],\n" +
                  "  PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 0.0],\n" +
                  "    PARAMETER[“elt_0_1”, 1.0],\n" +
                  "    PARAMETER[“elt_1_0”, 1.0],\n" +
                  "    PARAMETER[“elt_1_1”, 0.0]]]";
            assertWktEquals(wkt);
            assertEquals(transform, mtFactory.createFromWKT(wkt));
            transform = transform.inverse();
        }
        /*
         * Add unit conversions from metres to kilometres.
         */
        final AffineTransform2D convert = new AffineTransform2D(0.001, 0, 0, 0.001, 0, 0);
        transform = MathTransforms.concatenate(transform, convert);
        wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.0],\n" +
              "    PARAMETER[“elt_0_1”, 1.0],\n" +
              "    PARAMETER[“elt_1_0”, 1.0],\n" +
              "    PARAMETER[“elt_1_1”, 0.0]],\n" +
              "  PARAM_MT[“Mercator_1SP”,\n" +
              "    PARAMETER[“semi_major”, 6377397.155],\n" +
              "    PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
              "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "    PARAMETER[“central_meridian”, 110.0],\n" +
              "    PARAMETER[“scale_factor”, 0.997],\n" +
              "    PARAMETER[“false_easting”, 3900000.0],\n" +
              "    PARAMETER[“false_northing”, 900000.0]],\n" +
              "  PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.001],\n" +
              "    PARAMETER[“elt_1_1”, 0.001]]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 1000.0],\n" +
                  "    PARAMETER[“elt_1_1”, 1000.0]],\n" +
                  "  INVERSE_MT[PARAM_MT[“Mercator_1SP”,\n" +
                  "      PARAMETER[“semi_major”, 6377397.155],\n" +
                  "      PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
                  "      PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "      PARAMETER[“central_meridian”, 110.0],\n" +
                  "      PARAMETER[“scale_factor”, 0.997],\n" +
                  "      PARAMETER[“false_easting”, 3900000.0],\n" +
                  "      PARAMETER[“false_northing”, 900000.0]]],\n" +
                  "  PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 0.0],\n" +
                  "    PARAMETER[“elt_0_1”, 1.0],\n" +
                  "    PARAMETER[“elt_1_0”, 1.0],\n" +
                  "    PARAMETER[“elt_1_1”, 0.0]]]";
            assertWktEquals(wkt);
            // Can not test the full object because of slight rounding error
            // (last digit in the translateY term of an affine transform).
            assertMultilinesEquals(wkt, mtFactory.createFromWKT(wkt).toWKT());
            transform = transform.inverse();
        }
        /*
         * At this point the first transform has been fully created. Save it for future reference
         * and now process to the same tests for Mercator2SP (except that we will concatenate the
         * "swap" and "convert" affines in the opposite order).
         */
        final MathTransform first = transform;
        parameters = mtFactory.getDefaultParameters("Mercator_2SP");
        parameters.parameter("semi-major axis").setValue(6378245.0);
        parameters.parameter("semi-minor axis").setValue(6356863.018773047);
        parameters.parameter("Latitude of 1st standard parallel").setValue(42.0);
        parameters.parameter("Longitude of natural origin").setValue(51.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        wkt = "PARAM_MT[“Mercator_2SP”,\n" +
              "  PARAMETER[“semi_major”, 6378245.0],\n" +
              "  PARAMETER[“semi_minor”, 6356863.018773047],\n" +
              "  PARAMETER[“standard_parallel_1”, 42.0],\n" +
              "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "  PARAMETER[“central_meridian”, 51.0],\n" +
              "  PARAMETER[“false_easting”, 0.0],\n" +
              "  PARAMETER[“false_northing”, 0.0]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "INVERSE_MT[PARAM_MT[“Mercator_2SP”,\n" +
                  "    PARAMETER[“semi_major”, 6378245.0],\n" +
                  "    PARAMETER[“semi_minor”, 6356863.018773047],\n" +
                  "    PARAMETER[“standard_parallel_1”, 42.0],\n" +
                  "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "    PARAMETER[“central_meridian”, 51.0],\n" +
                  "    PARAMETER[“false_easting”, 0.0],\n" +
                  "    PARAMETER[“false_northing”, 0.0]]]";
            assertWktEquals(wkt);
            assertEquals(transform, mtFactory.createFromWKT(wkt));
            transform = transform.inverse();
        }
        /*
         * Add unit conversions from metres to kilometres.
         */
        transform = MathTransforms.concatenate(transform, convert);
        wkt = "CONCAT_MT[PARAM_MT[“Mercator_2SP”,\n" +
              "    PARAMETER[“semi_major”, 6378245.0],\n" +
              "    PARAMETER[“semi_minor”, 6356863.018773047],\n" +
              "    PARAMETER[“standard_parallel_1”, 42.0],\n" +
              "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "    PARAMETER[“central_meridian”, 51.0],\n" +
              "    PARAMETER[“false_easting”, 0.0],\n" +
              "    PARAMETER[“false_northing”, 0.0]],\n" +
              "  PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.001],\n" +
              "    PARAMETER[“elt_1_1”, 0.001]]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 1000.0],\n" +
                  "    PARAMETER[“elt_1_1”, 1000.0]],\n" +
                  "  INVERSE_MT[PARAM_MT[“Mercator_2SP”,\n" +
                  "      PARAMETER[“semi_major”, 6378245.0],\n" +
                  "      PARAMETER[“semi_minor”, 6356863.018773047],\n" +
                  "      PARAMETER[“standard_parallel_1”, 42.0],\n" +
                  "      PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "      PARAMETER[“central_meridian”, 51.0],\n" +
                  "      PARAMETER[“false_easting”, 0.0],\n" +
                  "      PARAMETER[“false_northing”, 0.0]]]]";
            assertWktEquals(wkt);
            assertEquals(transform, mtFactory.createFromWKT(wkt));
            transform = transform.inverse();
        }
        /*
         * Add axis swap from (latitude, longitude) order to (longitude, latitude) order.
         */
        transform = MathTransforms.concatenate(swap, transform);
        wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.0],\n" +
              "    PARAMETER[“elt_0_1”, 1.0],\n" +
              "    PARAMETER[“elt_1_0”, 1.0],\n" +
              "    PARAMETER[“elt_1_1”, 0.0]],\n" +
              "  PARAM_MT[“Mercator_2SP”,\n" +
              "    PARAMETER[“semi_major”, 6378245.0],\n" +
              "    PARAMETER[“semi_minor”, 6356863.018773047],\n" +
              "    PARAMETER[“standard_parallel_1”, 42.0],\n" +
              "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "    PARAMETER[“central_meridian”, 51.0],\n" +
              "    PARAMETER[“false_easting”, 0.0],\n" +
              "    PARAMETER[“false_northing”, 0.0]],\n" +
              "  PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.001],\n" +
              "    PARAMETER[“elt_1_1”, 0.001]]]";
        assertWktEquals(wkt);
        assertEquals(transform, mtFactory.createFromWKT(wkt));
        if (isInverseTransformSupported) {
            assertSame(transform, transform.inverse().inverse());
            transform = transform.inverse();
            wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 1000.0],\n" +
                  "    PARAMETER[“elt_1_1”, 1000.0]],\n" +
                  "  INVERSE_MT[PARAM_MT[“Mercator_2SP”,\n" +
                  "      PARAMETER[“semi_major”, 6378245.0],\n" +
                  "      PARAMETER[“semi_minor”, 6356863.018773047],\n" +
                  "      PARAMETER[“standard_parallel_1”, 42.0],\n" +
                  "      PARAMETER[“latitude_of_origin”, 0.0],\n" +
                  "      PARAMETER[“central_meridian”, 51.0],\n" +
                  "      PARAMETER[“false_easting”, 0.0],\n" +
                  "      PARAMETER[“false_northing”, 0.0]]],\n" +
                  "  PARAM_MT[“Affine”,\n" +
                  "    PARAMETER[“num_row”, 3],\n" +
                  "    PARAMETER[“num_col”, 3],\n" +
                  "    PARAMETER[“elt_0_0”, 0.0],\n" +
                  "    PARAMETER[“elt_0_1”, 1.0],\n" +
                  "    PARAMETER[“elt_1_0”, 1.0],\n" +
                  "    PARAMETER[“elt_1_1”, 0.0]]]";
            assertWktEquals(wkt);
            assertEquals(transform, mtFactory.createFromWKT(wkt));
            transform = transform.inverse();
        }
        /*
         * Now the interesting part: concatenate the two transforms. We ignore the change of
         * ellipsoid, which is completely wrong but our intend here is to test concatenation,
         * not datum change. The referencing framework just concatenates what we said without
         * question. Note that while the WKT we get here seems rather straightforward, the
         * internal storage is actually quite different (invoke "printInternalWKT() to see).
         */
        final MathTransform second = transform;
        transform = MathTransforms.concatenate(first.inverse(), second);
        wkt = "CONCAT_MT[PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 1000.0],\n" +
              "    PARAMETER[“elt_1_1”, 1000.0]],\n" +
              "  INVERSE_MT[PARAM_MT[“Mercator_1SP”,\n" +
              "      PARAMETER[“semi_major”, 6377397.155],\n" +
              "      PARAMETER[“semi_minor”, 6356078.9626186555],\n" +
              "      PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "      PARAMETER[“central_meridian”, 110.0],\n" +
              "      PARAMETER[“scale_factor”, 0.997],\n" +
              "      PARAMETER[“false_easting”, 3900000.0],\n" +
              "      PARAMETER[“false_northing”, 900000.0]]],\n" +
              "  PARAM_MT[“Mercator_2SP”,\n" +
              "    PARAMETER[“semi_major”, 6378245.0],\n" +
              "    PARAMETER[“semi_minor”, 6356863.018773047],\n" +
              "    PARAMETER[“standard_parallel_1”, 42.0],\n" +
              "    PARAMETER[“latitude_of_origin”, 0.0],\n" +
              "    PARAMETER[“central_meridian”, 51.0],\n" +
              "    PARAMETER[“false_easting”, 0.0],\n" +
              "    PARAMETER[“false_northing”, 0.0]],\n" +
              "  PARAM_MT[“Affine”,\n" +
              "    PARAMETER[“num_row”, 3],\n" +
              "    PARAMETER[“num_col”, 3],\n" +
              "    PARAMETER[“elt_0_0”, 0.001],\n" +
              "    PARAMETER[“elt_1_1”, 0.001]]]";
        assertWktEquals(wkt);
        // Can not test the full object because of slight rounding error
        assertMultilinesEquals(wkt, mtFactory.createFromWKT(wkt).toWKT());
        /*
         * Sets the axis lengths to the same value than the first projection, so the concatenation
         * can recognize the two unitary projections as equivalent.  The referencing module should
         * be able to simplify all this big stuff to a single affine transform. Note that the
         * "Longitude of natural origin" still differents. In the particular case of the Mercator
         * projection, a concatenation still possible.
         */
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6356078.9626186555);
        transform = mtFactory.createParameterizedTransform(parameters);
        transform = MathTransforms.concatenate(swap, transform, convert);
        transform = MathTransforms.concatenate(first.inverse(), transform);
        assertInstanceOf("Concatenation should have been optimized.", AffineTransform.class, transform);
        final AffineTransform at = (AffineTransform) transform;
        tolerance = 1E-10;
        assertEquals(0.0,             at.getShearX(),     tolerance);
        assertEquals(0.0,             at.getShearY(),     tolerance);
        assertEquals(0.7464972023333, at.getScaleX(),     tolerance);
        assertEquals(0.7464972023333, at.getScaleY(),     tolerance);
        assertEquals(1976.2668705583, at.getTranslateX(), tolerance);
        assertEquals(-671.8474821000, at.getTranslateY(), tolerance);
    }

    /**
     * Tests a WKT involving axis swapping. This is a way to verify that the
     * {@link org.geotoolkit.internal.referencing.Semaphores#PROJCS} special case
     * is correctly handled.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    @Ignore
    public void testAxisSwapping() throws FactoryException {
        String wkt = decodeQuotes(
                "PROJCS[“OSGB 1936 / British National Grid”,\n" +
                "  GEOGCS[“OSGB 1936”,DATUM[“OSGB_1936”,\n" +
                "    SPHEROID[“Airy 1830”,6377563.396,299.3249646,AUTHORITY[“EPSG”, “7001”]],\n" +
                "    TOWGS84[375,-111,431,0,0,0,0],AUTHORITY[“EPSG”, “6277”]],\n" +
                "    PRIMEM[“Greenwich”,0,AUTHORITY[“EPSG”, “8901”]],\n" +
                "    UNIT[“DMSH”,0.0174532925199433,AUTHORITY[“EPSG”, “9108”]],\n" +
                "    AXIS[“Lat”,NORTH],AXIS[“Long”,EAST],AUTHORITY[“EPSG”, “4277”]],\n" +
                "  PROJECTION[“Transverse_Mercator”],\n" +
                "  PARAMETER[“latitude_of_origin”,49],\n" +
                "  PARAMETER[“central_meridian”,-2],\n" +
                "  PARAMETER[“scale_factor”,0.999601272],\n" +
                "  PARAMETER[“false_easting”,400000],\n" +
                "  PARAMETER[“false_northing”,-100000],\n" +
                "  UNIT[“metre”,1,AUTHORITY[“EPSG”, “9001”]],\n" +
                "  AXIS[“E”,EAST],AXIS[“N”,NORTH],AUTHORITY[“EPSG”, “27700”]]");

        final ProjectedCRS parsedCRS = (ProjectedCRS) crsFactory.createFromWKT(wkt);
        wkt = parsedCRS.toWKT();
        final ProjectedCRS parsedAgain = (ProjectedCRS) crsFactory.createFromWKT(wkt);
        /*
         * In an older version, the WKT was not properly formatted because the axis swapping
         * inside the inner GEOCS element was causing confusion in ConcatenatedTransform and
         * DefaultSingleOperation. This bug could be reproduced by parsing a WKT, formatting
         * it and reparsing it again.
         *
         * Set the following condition to "true" if debugging is needed.
         */
        if (false) {
            transform = parsedCRS  .getConversionFromBase().getMathTransform(); printInternalWKT();
            transform = parsedAgain.getConversionFromBase().getMathTransform(); printInternalWKT();
        }
        assertEquals(parsedCRS, parsedAgain);
        assertSame(parsedCRS, parsedAgain);
    }
}
