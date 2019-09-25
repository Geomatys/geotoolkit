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
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.junit.Test;

/**
 *
 * @author Theo Zozime
 */
public class FeatureCollectionToComplexConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testJSONConversion() throws Exception {
        // Get test resource
        Object testResource = ConvertersTestUtils.loadTestResource("/inputs/featurecollection.json");

        Data complex = ConvertersTestUtils.initAndRunOutputConversion(FeatureCollection.class,
                Data.class,
                testResource,
                WPSMimeType.APP_GEOJSON.val(),
                WPSEncoding.UTF8.getValue());

        // Test complex
        ConvertersTestUtils.assertFormatMatch(complex, WPSEncoding.UTF8.getValue(), WPSMimeType.APP_GEOJSON.val(), null);
        ConvertersTestUtils.useDataContentAsFile(complex, file -> {
            // The main part of the test consist in retrieving the same feature collection
            // that we gave as input to the converter
            try {
                FeatureSet readFeatureCollection = WPSConvertersUtils.readFeatureCollectionFromJson(file.toUri());
                ConvertersTestUtils.assertFeatureCollectionIsValid(readFeatureCollection);
            } catch (DataStoreException | URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
