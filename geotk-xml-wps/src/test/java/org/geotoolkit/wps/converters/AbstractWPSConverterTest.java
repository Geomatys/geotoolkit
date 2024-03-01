/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters;

import java.awt.image.RenderedImage;
import org.apache.sis.coverage.grid.GridCoverage;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.geotoolkit.test.image.ImageTestBase.SAMPLE_TOLERANCE;


/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public abstract class AbstractWPSConverterTest {
    /**
     * Compares the rendered view of two coverages for equality.
     *
     * @param expected The coverage containing the expected pixel values.
     * @param actual   The coverage containing the actual pixel values.
     */
    protected static void assertRasterEquals(final GridCoverage expected, final GridCoverage actual) {
        assertNotNull(expected, "Expected coverage");
        assertNotNull(actual, "Actual coverage");
        org.opengis.test.Assertions.assertSampleValuesEqual(expected.render(null), actual.render(null), SAMPLE_TOLERANCE, null);
    }

    protected static void assertRasterEquals(final RenderedImage expected, final RenderedImage actual) {
        org.opengis.test.Assertions.assertSampleValuesEqual(expected, actual, SAMPLE_TOLERANCE, null);
    }
}
