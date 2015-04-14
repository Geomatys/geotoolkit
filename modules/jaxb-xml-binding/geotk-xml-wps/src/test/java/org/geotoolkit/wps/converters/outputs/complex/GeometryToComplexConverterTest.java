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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.ext.GeoJSONType;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class GeometryToComplexConverterTest {

    @Test
    public void testJSONConversion() throws DataStoreException, IOException, FactoryException, URISyntaxException {

        // Get test resource
        Geometry geometryResource =  (Geometry) ConvertersTestUtils.loadTestResource("/inputs/geometry.json");

        ComplexDataType complex = ConvertersTestUtils.initAndRunOutputConversion(
                                                                Geometry.class,
                                                                ComplexDataType.class,
                                                                geometryResource,
                                                                WPSMimeType.APP_GEOJSON.val(),
                                                                WPSEncoding.UTF8.getValue());

        // Test complex
        ConvertersTestUtils.assertGeometryIsValid(geometryResource);
        assertEquals(WPSMimeType.APP_GEOJSON.val(), complex.getMimeType());
        assertEquals(WPSEncoding.UTF8.getValue(), complex.getEncoding());
        assertNull(complex.getSchema());
        assertNotNull(complex.getContent());
        assertEquals(1, complex.getContent().size());
        assertTrue(complex.getContent().get(0) instanceof GeoJSONType);

        String geoJsonContent = ((GeoJSONType) complex.getContent().get(0)).getContent();
        Path tmpFilePath = WPSConvertersUtils.writeTempJsonFile(geoJsonContent);
        Geometry geometry = getGeometry(tmpFilePath.toFile());

        ConvertersTestUtils.assertGeometryIsValid(geometry);
    }

    /**
     * Helper method that read a json file containing a single Geometry
     * @param jsonFile file to read
     * @return a geometry
     * @throws IOException
     * @throws FactoryException
     */
    private static Geometry getGeometry(File jsonFile) throws IOException, FactoryException {
        final GeoJSONParser parser = new GeoJSONParser();
        final GeoJSONObject geoJsonObject = parser.parse(jsonFile);

        if (!(geoJsonObject instanceof GeoJSONGeometry))
            fail();

        final GeoJSONGeometry jsonGeometry = (GeoJSONGeometry) geoJsonObject;
        return WPSConvertersUtils.convertGeoJSONGeometryToGeometry(jsonGeometry);
    }
}
