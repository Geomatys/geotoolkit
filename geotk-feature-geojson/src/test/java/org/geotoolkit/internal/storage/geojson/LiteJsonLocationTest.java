/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.storage.geojson;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sis.test.TestCase;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.GeoJSONUtils;
import org.geotoolkit.internal.geojson.LiteJsonLocation;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class LiteJsonLocationTest extends TestCase {

    @Test
    public void testEquality () throws URISyntaxException, IOException {
        URL fcFile = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/featurecollection.json");
        Path fcPath = Paths.get(fcFile.toURI());

        JsonLocation streamLocation = null;
        JsonLocation readerLocation = null;

        //get Location from stream
        try (InputStream stream = Files.newInputStream(fcPath);
             JsonParser parser = GeoJSONParser.JSON_FACTORY.createParser(stream)) {
            streamLocation = moveAndReturnPos(parser);
        }

        //get Location from reader
        try (BufferedReader reader = Files.newBufferedReader(fcPath, Charset.forName("UTF-8"));
             JsonParser parser = GeoJSONParser.JSON_FACTORY.createParser(reader)) {
            readerLocation = moveAndReturnPos(parser);
        }

        Assert.assertFalse(streamLocation.equals(readerLocation));
        Assert.assertFalse(GeoJSONUtils.equals(streamLocation, readerLocation));

        LiteJsonLocation liteStreamLocation = new LiteJsonLocation(streamLocation);
        LiteJsonLocation liteReaderLocation = new LiteJsonLocation(readerLocation);

        Assert.assertTrue(liteStreamLocation.equals(liteReaderLocation));

        Assert.assertTrue(liteStreamLocation.equals(streamLocation));
        Assert.assertTrue(liteStreamLocation.equals(readerLocation));

        Assert.assertTrue(liteReaderLocation.equals(streamLocation));
        Assert.assertTrue(liteReaderLocation.equals(readerLocation));

    }


    @Test
    public void testBefore () throws URISyntaxException, IOException {
        URL fcFile = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/featurecollection.json");
        Path fcPath = Paths.get(fcFile.toURI());

        //get Location from stream
        try (InputStream stream = Files.newInputStream(fcPath);
             JsonParser parser = GeoJSONParser.JSON_FACTORY.createParser(stream)) {
            parser.nextToken();

            JsonLocation currentLocation = parser.getCurrentLocation();
            LiteJsonLocation liteJsonLocation = new LiteJsonLocation(currentLocation);
            Assert.assertFalse(liteJsonLocation.isBefore(currentLocation));

            parser.nextToken();
            currentLocation = parser.getCurrentLocation();
            Assert.assertTrue(liteJsonLocation.isBefore(currentLocation));
        }
    }


    private JsonLocation moveAndReturnPos(JsonParser parser) throws IOException {
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        return parser.getCurrentLocation();
    }
}
