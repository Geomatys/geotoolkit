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
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class FeatureToComplexConverterTest {

    @Test
    public void testJSONConversion() throws DataStoreException, IOException, URISyntaxException, FactoryException {
        // Get test resource
        final Object testResource = ConvertersTestUtils.loadTestResource("/inputs/feature.json");

        final Data complex = ConvertersTestUtils.initAndRunOutputConversion(Feature.class,
                                                            Data.class,
                                                            testResource,
                                                            WPSMimeType.APP_GEOJSON.val(),
                                                            WPSEncoding.UTF8.getValue());

        ConvertersTestUtils.assertFormatMatch(complex, WPSEncoding.UTF8.getValue(), WPSMimeType.APP_GEOJSON.val(), null);
        ConvertersTestUtils.useDataContentAsFile(complex, file -> {
            try {
                final Feature readFeature = WPSConvertersUtils.readFeatureFromJson(file.toUri());
                ConvertersTestUtils.assertFeatureIsValid(readFeature);
            } catch (DataStoreException | URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
