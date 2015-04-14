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

import com.vividsolutions.jts.geom.Geometry;
import java.io.FileInputStream;
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
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class GeometryArrayToComplexConverterTest {

    @Test
    public void testJSONConversion() throws IOException, FactoryException, DataStoreException, URISyntaxException {

        // Get test resource
        final Geometry[] geometryArrayResource = (Geometry[]) ConvertersTestUtils.loadTestResource("/inputs/geometrycollection.json");

        final ComplexDataType complex = ConvertersTestUtils.initAndRunOutputConversion(
                                                    Geometry[].class,
                                                    ComplexDataType.class,
                                                    geometryArrayResource,
                                                    WPSMimeType.APP_GEOJSON.val(),
                                                    WPSEncoding.UTF8.getValue());

        // Test complex
        assertNotNull(complex.getContent());
        assertEquals(1, complex.getContent().size());
        assertEquals(WPSMimeType.APP_GEOJSON.val(), complex.getMimeType());
        assertEquals(WPSEncoding.UTF8.getValue(), complex.getEncoding());
        assertEquals(null, complex.getSchema());
        assertTrue(complex.getContent().get(0) instanceof GeoJSONType);

        final String geoJson = ((GeoJSONType)complex.getContent().get(0)).getContent();
        final Path tmpFilePath = WPSConvertersUtils.writeTempJsonFile(geoJson);
        final Geometry[] geometryArrayToTest = ConvertersTestUtils.getGeometryArrayFromInputStream(new FileInputStream(tmpFilePath.toFile()));

        assertEquals(geometryArrayResource.length, geometryArrayToTest.length);
        ConvertersTestUtils.assertGeometryArrayIsValid(geometryArrayResource);
        ConvertersTestUtils.assertGeometryArrayIsValid(geometryArrayToTest);

    }
}
