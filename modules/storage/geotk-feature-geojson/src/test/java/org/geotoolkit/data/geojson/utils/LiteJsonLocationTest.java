package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import org.geotoolkit.data.geojson.GeoJSONReadTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class LiteJsonLocationTest {

    @Test
    public void testEquality () throws URISyntaxException, IOException {
        URL fcFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/featurecollection.json");
        Path fcPath = Paths.get(fcFile.toURI());

        JsonLocation streamLocation = null;
        JsonLocation readerLocation = null;

        //get Location from stream
        try (InputStream stream = Files.newInputStream(fcPath);
             JsonParser parser = GeoJSONParser.FACTORY.createParser(stream)) {
            streamLocation = moveAndReturnPos(parser);
        }

        //get Location from reader
        try (BufferedReader reader = Files.newBufferedReader(fcPath, Charset.forName("UTF-8"));
             JsonParser parser = GeoJSONParser.FACTORY.createParser(reader)) {
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

    private JsonLocation moveAndReturnPos(JsonParser parser) throws IOException {
        JsonLocation streamLocation;
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        return parser.getCurrentLocation();
    }
}
