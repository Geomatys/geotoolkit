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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.util.FactoryException;

/**
 *
 * @author Theo Zozime
 */
public class GeometryArrayToComplexConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testJSONConversion() throws IOException, FactoryException, DataStoreException, URISyntaxException {

        // Get test resource
        final Geometry[] geometryArrayResource = (Geometry[]) ConvertersTestUtils.loadTestResource("/inputs/geometrycollection.json");

        final Data complex = ConvertersTestUtils.initAndRunOutputConversion(Geometry[].class,
                                                    Data.class,
                                                    geometryArrayResource,
                                                    WPSMimeType.APP_GEOJSON.val(),
                                                    WPSEncoding.UTF8.getValue());

        // Test complex
        ConvertersTestUtils.assertFormatMatch(complex, WPSEncoding.UTF8.getValue(), WPSMimeType.APP_GEOJSON.val(), null);
        ConvertersTestUtils.useDataContentAsFile(complex, file -> {
            try {
                final Geometry[] geometryArrayToTest = ConvertersTestUtils.getGeometryArrayFromInputStream(Files.newInputStream(file));

                assertEquals(geometryArrayResource.length, geometryArrayToTest.length);
                ConvertersTestUtils.assertGeometryArrayIsValid(geometryArrayResource);
                ConvertersTestUtils.assertGeometryArrayIsValid(geometryArrayToTest);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
