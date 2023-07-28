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
package org.geotoolkit.wps.converters.outputs.reference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class FeatureCollectionToReferenceConverterTest {

    @Test
    public void testJSONConversion() throws DataStoreException, MalformedURLException, IOException, URISyntaxException, FactoryException {

        // Get the test resource
        final Object testResource = ConvertersTestUtils.loadTestResource("/inputs/featurecollection.json");

        final Reference reference = ConvertersTestUtils.initAndRunOutputConversion(FeatureCollection.class,
                Reference.class,
                testResource,
                WPSMimeType.APP_GEOJSON.val(),
                WPSEncoding.UTF8.getValue());

        // Test reference
        assertEquals(WPSMimeType.APP_GEOJSON.val(), reference.getMimeType());
        assertEquals(WPSEncoding.UTF8.getValue(), reference.getEncoding());
        assertNull(reference.getSchema());
        assertNotNull(reference.getHref());

        final FeatureSet featureCollection = WPSConvertersUtils.readFeatureCollectionFromJson(URI.create(reference.getHref()));
        ConvertersTestUtils.assertFeatureCollectionIsValid((FeatureSet) testResource);
        ConvertersTestUtils.assertFeatureCollectionIsValid(featureCollection);
    }
}
