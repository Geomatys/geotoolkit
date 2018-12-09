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
package org.geotoolkit.coverage;

import java.awt.image.BufferedImage;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ParameterListDescriptor;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link GridSampleDimension} implementation. Since {@code GridSampleDimension}
 * rely on {@link CategoryList} for many of its work, many {@code GridSampleDimension}
 * tests are actually {@code CategoryList} tests.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final strictfp class SampleDimensionTest extends org.geotoolkit.test.TestBase {
    /**
     * The categories making the sample dimension to test.
     */
    private static final String[] CATEGORIES = {
        "No data",
        "Clouds",
        "Lands"
    };

    /**
     * The "no data" values making the sample dimension to test.
     * There is one for each category in {@link #CATEGORIES}.
     */
    private static final int[] NO_DATA = {0, 1, 255};

    /**
     * The minimal value for the geophysics category, inclusive.
     */
    private static final int minimum = 10;

    /**
     * The maximal value for the geophysics category, exclusive.
     */
    private static final int maximum = 200;

    /**
     * The scale factor for the sample dimension to test.
     */
    private static final double scale  = 0.1;

    /**
     * The offset value for the sample dimension to test.
     */
    private static final double offset = 5.0;

    /**
     * Small number for comparison.
     */
    private static final double EPS = 1E-7;

    /**
     * The sample dimension to test.
     */
    private GridSampleDimension test;

    /**
     * Sets up common objects used for all tests.
     */
    @Before
    public void setUp() {
        assertEquals("setUp", CATEGORIES.length, NO_DATA.length);
        final Category[] categories = new Category[CATEGORIES.length+1];
        for (int i=0; i<CATEGORIES.length; i++) {
            categories[i] = new Category(CATEGORIES[i], null, NO_DATA[i]);
        }
        categories[CATEGORIES.length] = new Category("SST", null, minimum, maximum, scale, offset);
        test = new GridSampleDimension("Temperature" ,categories, null);
    }

    /**
     * Tests the creation of an {@link SampleTranscoder} using the image operation registry.
     * This allow to apply the operation in the same way than other JAI operations, without
     * any need for a direct access to package-private method.
     */
    @Test
    public void testSampleTranscoderCreation() {
        final OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        assertNotNull(registry.getDescriptor("rendered", SampleTranscoder.OPERATION_NAME));

        final BufferedImage       dummy = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        final ParameterBlockJAI   param = new ParameterBlockJAI(SampleTranscoder.OPERATION_NAME);
        final ParameterListDescriptor d = param.getParameterListDescriptor();
        assertTrue(d.getParamClasses()[0].equals(GridSampleDimension[].class));

        try {
            JAI.create(SampleTranscoder.OPERATION_NAME, param);
            fail();
        } catch (IllegalArgumentException expected) {
            // This is the expected exception: source required.
        }

        param.addSource(dummy);
        try {
            JAI.create(SampleTranscoder.OPERATION_NAME, param);
            fail();
        } catch (IllegalArgumentException expected) {
            // This is the expected exception: parameter required.
        }

        param.setParameter("sampleDimensions", new GridSampleDimension[] {test});
        final RenderedOp op = JAI.create(SampleTranscoder.OPERATION_NAME, param);
        assertTrue(op.getRendering() instanceof SampleTranscoder);
    }

    /**
     * Tests the {@link GridSampleDimension#rescale} method.
     */
    @Test
    public void testRescale() {
        GridSampleDimension scaled;
        scaled = test.geophysics(false).rescale(2, -6);
        assertEquals("Incorrect scale",     2, scaled.getScale(),        EPS);
        assertEquals("Incorrect offset",   -6, scaled.getOffset(),       EPS);
        assertEquals("Incorrect minimum",   0, scaled.getMinimumValue(), EPS);
        assertEquals("Incorrect maximum", 255, scaled.getMaximumValue(), EPS);

        scaled = test.geophysics(true).rescale(2, -6).geophysics(false);
        assertEquals("Incorrect scale",   scale*2,   scaled.getScale(),        EPS);
        assertEquals("Incorrect offset", offset*2-6, scaled.getOffset(),       EPS);
        assertEquals("Incorrect minimum",   0,       scaled.getMinimumValue(), EPS);
        assertEquals("Incorrect maximum", 255,       scaled.getMaximumValue(), EPS);
    }
}
