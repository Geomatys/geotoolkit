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
package org.geotoolkit.wps.converters.outputs.complex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.ext.GeoJSONType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class FeatureToComplexConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testJSONConversion() throws DataStoreException, IOException, URISyntaxException, FactoryException {
        // Get test resource
        final Object testResource = ConvertersTestUtils.loadTestResource("/inputs/feature.json");

        final ComplexDataType complex = ConvertersTestUtils.initAndRunOutputConversion(
                                                            Feature.class,
                                                            ComplexDataType.class,
                                                            testResource,
                                                            WPSMimeType.APP_GEOJSON.val(),
                                                            WPSEncoding.UTF8.getValue());

        // Test complex
        assertEquals(1, complex.getContent().size());
        assertEquals(WPSMimeType.APP_GEOJSON.val(), complex.getMimeType());
        assertEquals(WPSEncoding.UTF8.getValue(), complex.getEncoding());
        assertEquals(null, complex.getSchema());
        assertNotNull(complex.getContent());
        assertTrue(complex.getContent().get(0) instanceof GeoJSONType);

        final String geoJson = ((GeoJSONType) complex.getContent().get(0)).getContent();

        // Write the json in a tmp file in order to be able to read it
        final Path tmpFilePath = WPSConvertersUtils.writeTempJsonFile(geoJson);

        final Feature readFeature = WPSConvertersUtils.readFeatureFromJson(tmpFilePath.toUri());
        ConvertersTestUtils.assertFeatureIsValid(readFeature);
    }
}
