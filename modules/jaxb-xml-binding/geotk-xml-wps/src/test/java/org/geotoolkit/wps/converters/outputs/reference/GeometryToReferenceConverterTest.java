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

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.Reference;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class GeometryToReferenceConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testJSONConversion() throws DataStoreException, IOException, URISyntaxException, FactoryException {

        // Get test resource
        final Object testResource = ConvertersTestUtils.loadTestResource("/inputs/geometry.json");

        Reference reference = ConvertersTestUtils.initAndRunOutputConversion(
                                                        Geometry.class,
                                                        Reference.class,
                                                        testResource,
                                                        WPSMimeType.APP_GEOJSON.val(),
                                                        WPSEncoding.UTF8.getValue());

        // Test reference
        assertEquals(WPSMimeType.APP_GEOJSON.val(), reference.getMimeType());
        assertEquals(WPSEncoding.UTF8.getValue(), reference.getEncoding());
        assertNull(reference.getSchema());
        assertNotNull(reference.getHref());

        Geometry geometry = ConvertersTestUtils.getGeometryFromGeoJsonContent(new URL(reference.getHref()).getPath());
        ConvertersTestUtils.assertGeometryIsValid((Geometry) testResource);
        ConvertersTestUtils.assertGeometryIsValid(geometry);
    }
}
