/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.wps.converters.inputs.complex;

import java.io.IOException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.junit.Test;

/**
 *
 * @author Theo Zozime
 */
public class ComplexToFeatureConverterTest extends org.geotoolkit.test.TestBase {


    /**
     * Test that the converter converts Complex containing a geoJSON feature
     * into valid Feature
     */
    @Test
    public void testJSONConversion() throws IOException {
        Feature feature = ConvertersTestUtils.initAndRunInputConversion(
                                                        ComplexDataType.class,
                                                        Feature.class,
                                                        "/inputs/feature.json",
                                                        WPSMimeType.APP_GEOJSON.val(),
                                                        WPSEncoding.UTF8.getValue(),
                                                        null);

        // Test the result feature
        ConvertersTestUtils.assertFeatureIsValid(feature);
    }
}
