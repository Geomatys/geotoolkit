/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.geotoolkit.internal.coverage;

import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.CRS;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Arrays;
import java.util.Map;

/**
 * Date: 01/10/14
 * Time: 10:17
 *
 * @author Alexis Manin (Geomatys)
 */
public strictfp class CoverageUtilitiesTest extends org.geotoolkit.test.TestBase {

    private static final double EPSI = 1E-8;

    /**
     * See WMTS annex, Table E.3 — Definition of Well-known scale set GoogleCRS84Quad
     */
    private static final double[] DEGREE_SCALES = new double[]{
            1.40625000000000,
            0.703125000000000,
            0.351562500000000,
            0.175781250000000,
            8.78906250000000E-2,
            4.39453125000000E-2,
            2.19726562500000E-2,
            1.09863281250000E-2,
            5.49316406250000E-3,
            2.74658203125000E-3,
            1.37329101562500E-3,
            6.86645507812500E-4,
            3.43322753906250E-4,
            1.71661376953125E-4,
            8.58306884765625E-5,
            4.29153442382812E-5,
            2.14576721191406E-5,
            1.07288360595703E-5,
            5.36441802978516E-6
    };

    /**
     * See WMTS annex, Table E.4 — Definition of Well-known scale set GoogleMapsCompatible
     */
    private static final double[] METER_SCALES = new double[]{
            156543.0339280410,
            78271.51696402048,
            39135.75848201023,
            19567.87924100512,
            9783.939620502561,
            4891.969810251280,
            2445.984905125640,
            1222.992452562820,
            611.4962262814100,
            305.7481131407048,
            152.8740565703525,
            76.43702828517624,
            38.21851414258813,
            19.10925707129406,
            9.554628535647032,
            4.777314267823516,
            2.388657133911758,
            1.194328566955879,
            0.5971642834779395
    };

    @BeforeClass
    public static void sortArrays() {
        // Needed for binary search.
        Arrays.sort(METER_SCALES);
        Arrays.sort(DEGREE_SCALES);
    }

    /**
     * Check computing of WMTS compliant scales from input envelope.
     *
     * First tested envelopes are validity domain of CRS:84 and mercator CRS. We attempt out method to return same envelopes.
     * After that, a test is done with a little envelope, to ensure envelope adaptation is good.
     *
     * @throws Exception
     */
    @Test
    public void toWellKnownScaleTest() throws Exception {

        final NumberRange<Double> degreeRange = new NumberRange<>(Double.class, 0.005, true, 2d, true);
        final NumberRange<Double> meterRange = new NumberRange<>(Double.class, 200000d, true, 2d, true);

        final CoordinateReferenceSystem crs84 = CommonCRS.defaultGeographic();
        final Envelope domain84 = CRS.getDomainOfValidity(crs84);
        Map.Entry<Envelope,double[]> scales = CoverageUtilities.toWellKnownScale(domain84, degreeRange);
        Assert.assertEquals("Input and output envelope must be the same.", domain84, scales.getKey());
        for (final double scale : scales.getValue()) {
            ensureAlmostContains(scale, DEGREE_SCALES);
        }

        final CoordinateReferenceSystem crsGoogle = CRS.forCode("EPSG:3857");
        final Envelope domainGoogle = CRS.getDomainOfValidity(crsGoogle);
        scales = CoverageUtilities.toWellKnownScale(domainGoogle, meterRange);
        Assert.assertEquals("Input and output envelope must be the same.", domainGoogle, scales.getKey());
        for (final double scale : scales.getValue()) {
            ensureAlmostContains(scale, METER_SCALES);
        }

        final CoordinateReferenceSystem crsMercat = CRS.forCode("EPSG:3395");
        final Envelope domainMercat = CRS.getDomainOfValidity(crsMercat);
        scales = CoverageUtilities.toWellKnownScale(domainMercat, meterRange);
        Assert.assertEquals("Input and output envelope must be the same.", domainMercat, scales.getKey());
        for (final double scale : scales.getValue()) {
            ensureAlmostContains(scale, METER_SCALES);
        }

        final double minLimit = 2E-5;
        final double maxLimit = 0.36;
        final NumberRange<Double> ReducedRange = new NumberRange<>(Double.class, minLimit, true, maxLimit, true);
        final Envelope simple84 = new Envelope2D(crs84, 10, 10, 5, 5);
        GeneralEnvelope expectedResult = new GeneralEnvelope(new Envelope2D(crs84, 0, 0, 22.5, 22.5));
        scales = CoverageUtilities.toWellKnownScale(simple84, ReducedRange);
        Assert.assertTrue("Input envelope has not been adapted as expected.", expectedResult.equals(scales.getKey()));
        //-- we want to reach at least minLimit resolution, also the last scale level
        //-- may be smaller than it
        final double minExpectedResolution = 1.07288360595703E-5;
        for (final double scale : scales.getValue()) {
            Assert.assertTrue("Computed scale is outside input limits. Expected between : ["+minExpectedResolution+"; "+maxLimit+"[, found : "+scale, scale >= minExpectedResolution && scale < maxLimit);
            ensureAlmostContains(scale, DEGREE_SCALES);
        }
    }

    /**
     * Check that input double value is near (tolerance is defined with {@linkplain #EPSI}) a value of the given array.
     * @param toCheck
     * @param array
     */
    private static void ensureAlmostContains(final double toCheck, double[] array) {
        int insertPoint = Arrays.binarySearch(array, toCheck);
        if (insertPoint < 0) {
            insertPoint = ~insertPoint;
            if (insertPoint <= array.length) {
                if (array[insertPoint-1]+EPSI >= toCheck) {
                    return;
                } else if (insertPoint < array.length) {
                    if (array[insertPoint]-EPSI <= toCheck) return;
                }
            }

            Assert.fail("Computed scale is not part of Well-Known scales : " + toCheck);
        }
    }
}
