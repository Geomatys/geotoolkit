/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.feature.FeatureSetWrapper;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import static org.geotoolkit.wps.converters.WPSObjectConverter.TMP_DIR_PATH;
import static org.geotoolkit.wps.converters.WPSObjectConverter.TMP_DIR_URL;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.WPSUtilities;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.Reference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.util.FactoryException;

/**
 *
 * @author Quentin Boileau (Geoamtys)
 */
public final class ConvertersTestUtils {

    public static final double DELTA = 1E-12;

    private ConvertersTestUtils() {
    }

    public static RenderedImage makeRendredImage() {
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        final GradientPaint gp = new GradientPaint(0, 0, Color.BLACK,
        500, 500, Color.WHITE, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 500, 500);
        g2d.dispose();
        return img;
    }

    public static GridCoverage makeCoverage() {

        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        final GradientPaint gp = new GradientPaint(0, 0, Color.RED,
        500, 500, Color.BLUE, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 500, 500);
        g2d.dispose();


        //set it's envelope
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);

        //create the coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(env);
        gcb.setValues(img);
        return gcb.build();
    }

    /**
     * Helper method to initialize a converter and run with its parameters.
     *
     * The converters are the ones that convert Complex or reference to Geometry
     * or Feature types.
     *
     * The method is in charge of setting up the parameters map, the resource
     * (either a java URL or String containg json datas) and calling conversion
     * method.
     *
     * @param <SourceType> type of the input data in the conversion
     * @param <TargetType> type of the output data in the conversion
     * @param sourceClass class of the input data in the conversion
     * @param targetClass class of the output dat in the conversion
     * @param resourcePath url to the resource file to read
     * @param mimeType mime type of the input data
     * @param encoding encoding of the input data
     * @param schema schema for the input data (only arguments that can be null)
     * @return an instance of TargetType which is the result of a call to the converter
     * @throws IOException on reading the test resource
     * @throws IllegalArgumentException when one of the String arguments is null
     * @throws NullPointerException when one of the arguments is null
     */
    public static <SourceType, TargetType> TargetType initAndRunInputConversion(
                                                 Class sourceClass, Class targetClass,
                                                 String resourcePath, String mimeType,
                                                 String encoding, String schema) throws IOException {
        ArgumentChecks.ensureNonNull("sourceClass",  sourceClass);
        ArgumentChecks.ensureNonNull("targetClass",  targetClass);
        ArgumentChecks.ensureNonEmpty("resourcePath", resourcePath);
        ArgumentChecks.ensureNonEmpty("mimeType", mimeType);
        ArgumentChecks.ensureNonEmpty("encoding", encoding);

        // Get the converter to test
        final WPSObjectConverter<SourceType, TargetType> converter =
                WPSConverterRegistry.getInstance().getConverter(sourceClass, targetClass);

        // Setup parameters map
        final Map<String, Object> param = ConvertersTestUtils.createParameters(
                                                                        mimeType,
                                                                        encoding);

        // Setup the resource and run conversion
        if (sourceClass.equals(Data.class)) {
            final String resource = ConvertersTestUtils.getTestResource(ConvertersTestUtils.class, resourcePath);
            final Data complex = ConvertersTestUtils.createComplex(mimeType, encoding, schema, resource);

            return converter.convert((SourceType) complex, param);
        } else if (sourceClass.equals(Reference.class)) {
            final URL resource = ConvertersTestUtils.class.getResource(resourcePath);
            final Reference reference = ConvertersTestUtils.createReference("1.0.0", mimeType, encoding, schema, resource);

            return converter.convert((SourceType) reference, param);
        } else {
            fail();
            return null;
        }
    }

    /**
     * Load a test resource.
     *
     * The resourcePath argument have to be one of the following :
     * - "/inputs/geometry.json"
     * - "/inputs/geometrycollection.json"
     * - "/inputs/feature.json"
     * - "/inputs/featurecollection.json"
     *
     * @param resourcePath path to the resource to load
     * @return an Object that can be casted to one of the following types :
     * Geometry, Geometry[], Feature, FeatureCollection
     *
     * @throws org.apache.sis.storage.DataStoreException when reading errors occur
     * @throws java.net.URISyntaxException when reading errors occur
     * @throws java.io.IOException when reading errors occur
     * @throws org.opengis.util.FactoryException when reading errors occur
     * @throws NullPointerException when resourcePath is null
     * @throws IllegalArgumentException when resourcePath is empty
     */
    public static Object loadTestResource(String resourcePath) throws DataStoreException, URISyntaxException, IOException, FactoryException {
        ArgumentChecks.ensureNonEmpty("resourcePath", resourcePath);

        Object testResource = null;
        if ("/inputs/geometry.json".equals(resourcePath)) {
            final Feature feature = WPSConvertersUtils.readFeatureFromJson(ConvertersTestUtils.class.getResource(resourcePath).toURI());
            final Optional<Geometry> geometryValue = FeatureExt.getDefaultGeometryValue(feature)
                            .filter(Geometry.class::isInstance)
                            .map(Geometry.class::cast);

            if (!geometryValue.isPresent())
                fail();

            testResource = geometryValue.get();
        }
        else if ("/inputs/geometrycollection.json".equals(resourcePath))
            testResource = ConvertersTestUtils.getGeometryArrayFromTestFolder(resourcePath);
        else if ("/inputs/feature.json".equals(resourcePath))
            testResource = WPSConvertersUtils.readFeatureFromJson(ConvertersTestUtils.class.getResource(resourcePath).toURI());
        else if ("/inputs/featurecollection.json".equals(resourcePath)) {
            FeatureSet fs = WPSConvertersUtils.readFeatureCollectionFromJson(ConvertersTestUtils.class.getResource(resourcePath).toURI());
            testResource = new FeatureSetWrapper(fs, null);
        } else
            fail("Unknown test resource : " + resourcePath);

        return testResource;
    }

    /**
     * Helper method to initialize a converter and run with its parameters.
     *
     * The converters are the one that convert Geometry or Feature types to
 Complex or Reference.

 The method is in charge of setting up the parameters map, the resource
 (either a java URL or String containg json datas) and calling conversion
 method.
     *
     * @param <SourceType> type of the input data in the conversion
     * @param <TargetType> type of the output data in the conversion
     * @param sourceClass class of the input data in the conversion
     * @param targetClass class of the output dat in the conversion
     * @param resource resource to give as input to the converter (must be of the
     * same type as SourceType)
     * @param mimeType mime type of the input data
     * @param encoding encoding of the input data
     *
     * @return an instance of TargetType which is the result of a call to the converter
     *
     * @throws IOException on reading the test resource
     * @throws IllegalArgumentException when mimeType or encoding is empty
     * @throws NullPointerException when an argument is null
     */
    public static <SourceType, TargetType> TargetType initAndRunOutputConversion(
                                                 Class sourceClass, Class targetClass,
                                                 Object resource, String mimeType,
                                                 String encoding) throws IOException {
        ArgumentChecks.ensureNonNull("sourceClass", sourceClass);
        ArgumentChecks.ensureNonNull("targetClass", targetClass);
        ArgumentChecks.ensureNonNull("resource", resource);
        ArgumentChecks.ensureNonEmpty("mimeType", mimeType);
        ArgumentChecks.ensureNonEmpty("encoding", encoding);

        // Get converter
        final WPSObjectConverter<SourceType, TargetType> converter =
        WPSConverterRegistry.getInstance().getConverter(sourceClass, targetClass);

        // Setup the parameters map
        Path tmpDirPath;
        Map<String, Object> parametersMap = null;
        if (targetClass.equals(Data.class))
            parametersMap = ConvertersTestUtils.createParameters(mimeType, encoding);
        else if (targetClass.equals(Reference.class)) {
            tmpDirPath = Files.createTempDirectory(UUID.randomUUID().toString());
            parametersMap = ConvertersTestUtils.createParameters(mimeType, encoding, tmpDirPath.toUri().toString(), WPSIO.IOType.OUTPUT.toString());
        }
        else
            fail();

        return converter.convert((SourceType) resource, parametersMap);
    }

    /**
     * Helper method that creates an output parameter map
     * @param mimeType mime-type property to define in the map
     * @param encoding encoding property to define in the map
     * @return the map the two above property
     */
    public static final Map<String, Object> createParameters(String mimeType, String encoding) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put(WPSObjectConverter.MIME, mimeType);
        map.put(WPSObjectConverter.ENCODING, encoding);
        return map;
    }

    /**
     * Helper method that creates an output parameter map
     * @param mimeType mime-type property to define in the map
     * @param encoding encoding property to define in the map
     * @param tmpDirPath url to the temporary directory where to write the content of a reference
     * @param ioType ioType that helps to know which type of reference instanciate
     * @return the map the two above property
     */
    public static final Map<String, Object> createParameters(final String mimeType, final String encoding, final String tmpDirPath, final String ioType) {
        final Map<String, Object> map = createParameters(mimeType, encoding);
        map.put(TMP_DIR_PATH, tmpDirPath);
        map.put(TMP_DIR_URL, tmpDirPath);
        map.put(WPSObjectConverter.IOTYPE, ioType);
        return map;
    }

    /**
     * Helper method that creates a complex with all its field filled
     * @param mimeType can be null
     * @param encoding can be null
     * @param schema can be null
     * @param content can be null
     * @return the complex with its field filled using the above parameters
     */
    public static final Data createComplex(String mimeType, String encoding, String schema, String content) {
        final Format myFormat = new Format(encoding, mimeType, schema, null);
        return new Data(myFormat, content);
    }

    /**
     * Helper method that creates a Reference with all its field filled
     * @param mimeType can be null
     * @param encoding can be null
     * @param schema can be null
     * @param url must be set
     * @return the reference with its field filled using the above parameters
     */
    public static final Reference createReference(String version, String mimeType, String encoding, String schema, URL url) {
        final Reference reference = new Reference();
        reference.setMimeType(mimeType);
        reference.setEncoding(encoding);
        reference.setSchema(schema);
        reference.setHref(url.toString());
        return reference;
    }

    /**
     * Helper method that creates a String containg all the content of a given
     * test resource
     *
     * @param clazz class that wants to get the resource
     * @param url url to the resource (relative to the class)
     * @return a String containing the content of the file
     * @throws IOException
     */
    public static final String getTestResource(Class clazz, String url) throws IOException {
        ArgumentChecks.ensureNonNull("class", clazz);
        ArgumentChecks.ensureNonNull("url", url);

        final InputStream inputStream = clazz.getResourceAsStream(url);
        assertNotNull(inputStream);
        try {
            final String content = IOUtilities.toString(inputStream);
            return content;
        }
        finally {
            inputStream.close();
        }
    }


    /**
     * Helper method that loads a geometry array from a json file
     * @param jsonURL url to the json file (the root is considered to be src/test/resources
     * so you can reference items in this folder by using /inputs for example
     * @return a geometry array
     * @throws IOException
     * @throws FactoryException
     */
    public static final Geometry[] getGeometryArrayFromTestFolder(final String jsonURL) throws IOException, FactoryException {
        return getGeometryArrayFromInputStream(ConvertersTestUtils.class.getResourceAsStream(jsonURL));
    }

    /**
     * Helper method that loads a geometry array from a json file
     * @param inputStream inputStream on the json file
     * @return a geometry array
     * @throws IOException
     * @throws FactoryException
     */
    public static Geometry[] getGeometryArrayFromInputStream(final InputStream inputStream) throws IOException, FactoryException {
        final GeoJSONObject geoJsonObject = GeoJSONParser.parse(inputStream);
        if (!(geoJsonObject instanceof GeoJSONGeometry.GeoJSONGeometryCollection))
            fail();

        final GeoJSONGeometry.GeoJSONGeometryCollection geometryCollection = (GeoJSONGeometry.GeoJSONGeometryCollection) geoJsonObject;
        final Geometry parentGeometry = WPSConvertersUtils.convertGeoJSONGeometryToGeometry(geometryCollection);
        final Geometry[] geometryArray = new Geometry[parentGeometry.getNumGeometries()];
        for (int i = 0; i < geometryArray.length; i++)
            geometryArray[i] = parentGeometry.getGeometryN(i);

        return geometryArray;
    }

    /**
     * Helper method that read a geometry in a json file
     * @param path path of the file containing the geometry
     * @return the read geometry
     * @throws IOException on IO errors when reading the file
     * @throws FactoryException when the crs of the geometry cannot be retrieved
     */
    public static Geometry getGeometryFromGeoJsonContent(final Path path) throws IOException, FactoryException {
        final GeoJSONObject geoJsonObject = GeoJSONParser.parse(path);
        GeoJSONGeometry geoJsonGeometry = null;

        if (geoJsonObject instanceof GeoJSONFeature) {
            GeoJSONFeature jsonFeature = (GeoJSONFeature) geoJsonObject;
            geoJsonGeometry = jsonFeature.getGeometry();
        } else if (geoJsonObject instanceof GeoJSONGeometry)
            geoJsonGeometry = (GeoJSONGeometry) geoJsonObject;
        else
            fail();

        return WPSConvertersUtils.convertGeoJSONGeometryToGeometry(geoJsonGeometry);
    }

    /**
     * Helper method to test that a given feature collection as the same values
     * as in the test file featurecollection.json
     *
     * @param featureCollection the FeatureCollection to test
     */
    public static void assertFeatureCollectionIsValid(final FeatureSet featureCollection) throws DataStoreException {
        assertNotNull(featureCollection);

        // Ensure the feature collection contains 7 features
        assertEquals(FeatureStoreUtilities.getCount(featureCollection).longValue(), 7l);

        try (Stream<Feature> stream = featureCollection.features(false)) {
            Iterator<Feature> iterator = stream.iterator();

            boolean[] isDefinedProperties = new boolean[7];
            for (int i = 0; i < isDefinedProperties.length; i++)
                isDefinedProperties[i] = false;

            // Check that each property defined in the geojson file is in the
            // feature collection
            while (iterator.hasNext()) {
                final Feature feature = iterator.next();
                final Optional<Geometry> geometryValue = FeatureExt.getDefaultGeometryValue(feature)
                            .filter(Geometry.class::isInstance)
                            .map(Geometry.class::cast);
                assertTrue(geometryValue.isPresent());

                final Geometry currentGeometry = geometryValue.get();
                final String propertyValue = ((String)feature.getPropertyValue("name"));

                final Coordinate pointCoordinates = currentGeometry.getCoordinate();
                final Coordinate[] polygonCoordinates = currentGeometry.getCoordinates();

                if (propertyValue.equals("ABBOTT NEIGHBORHOOD PARK")) {
                    isDefinedProperties[0] = !isDefinedProperties[0];
                    assertEquals(-80.87088507656375, pointCoordinates.x, DELTA);
                    assertEquals(35.21515162500578, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("DOUBLE OAKS CENTER")) {
                    isDefinedProperties[1] = !isDefinedProperties[1];
                    assertEquals(-80.83775386582222, pointCoordinates.x, DELTA);
                    assertEquals(35.24980190252168, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("DOUBLE OAKS NEIGHBORHOOD PARK")) {
                    isDefinedProperties[2] = !isDefinedProperties[2];
                    assertEquals(-80.83827000459532, pointCoordinates.x, DELTA);
                    assertEquals(35.25674709224663, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("DOUBLE OAKS POOL")) {
                    isDefinedProperties[3] = !isDefinedProperties[3];
                    assertEquals(-80.83697759172735, pointCoordinates.x, DELTA);
                    assertEquals(35.25751734669229, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("DAVID B. WAYMER FLYING REGIONAL PARK")) {
                    isDefinedProperties[4] = !isDefinedProperties[4];
                    assertEquals(-80.81647652154736, pointCoordinates.x, DELTA);
                    assertEquals(35.40148708491418, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("DAVID B. WAYMER COMMUNITY PARK")) {
                    isDefinedProperties[5] = !isDefinedProperties[5];
                    assertEquals(-80.83556459443902, pointCoordinates.x, DELTA);
                    assertEquals(35.39917224760999, pointCoordinates.y, DELTA);
                } else if (propertyValue.equals("Plaza Road Park")) {
                    isDefinedProperties[6] = !isDefinedProperties[6];
                    assertEquals(-80.72487831115721, polygonCoordinates[0].x, DELTA);
                    assertEquals(35.26545403190955, polygonCoordinates[0].y, DELTA);

                    assertEquals(-80.72135925292969, polygonCoordinates[1].x, DELTA);
                    assertEquals(35.26727607954368, polygonCoordinates[1].y, DELTA);

                    assertEquals(-80.71517944335938, polygonCoordinates[2].x, DELTA);
                    assertEquals(35.26769654625573, polygonCoordinates[2].y, DELTA);
                } else
                    fail();
            }

            for (boolean isDefined : isDefinedProperties)
                assertTrue(isDefined);
        }
    }

    /**
     * Helper method to test that a given feature as the same values
     * as in the test file feature.json
     * @param feature the feature to test
     */
    public static void assertFeatureIsValid(final Feature feature) {
        final String propertyValue = ((String)feature.getPropertyValue("name"));
        assertEquals(propertyValue, "Plaza Road Park");

        Optional<Polygon> value = FeatureExt.getDefaultGeometryValue(feature)
                            .filter(Polygon.class::isInstance)
                            .map(Polygon.class::cast);

        assertTrue(value.isPresent());
        Polygon polygon = value.get();
        Coordinate[] coordinates = polygon.getCoordinates();

        // Test the value of the first three coordinates of the polygon
        assertEquals(-80.72487831115721, coordinates[0].x, DELTA);
        assertEquals(35.26545403190955, coordinates[0].y, DELTA);

        assertEquals(-80.72135925292969, coordinates[1].x, DELTA);
        assertEquals(35.26727607954368, coordinates[1].y, DELTA);

        assertEquals(-80.71517944335938, coordinates[2].x, DELTA);
        assertEquals(35.26769654625573, coordinates[2].y, DELTA);
    }

    /**
     * Helper method to test that a given geometry array as the same values as in
     * the test file geometrycollection.jso,
     * @param geometryArray the geometry array to test
     */
    public static void assertGeometryArrayIsValid(Geometry[] geometryArray) {
        assertNotNull(geometryArray);
        assertEquals(3, geometryArray.length);
        for (Geometry geometry : geometryArray) {
            if (geometry.getGeometryType().equals("Point")) {
                assertEquals(-80.66080570220947, geometry.getCoordinate().x, DELTA);
                assertEquals(35.04939206472683, geometry.getCoordinate().y, DELTA);
            }
            else if (geometry.getGeometryType().equals("Polygon")) {
                assertEquals(-80.66458225250244, geometry.getCoordinates()[0].x, DELTA);
                assertEquals(35.04496519190309, geometry.getCoordinates()[0].y, DELTA);

                assertEquals(-80.66344499588013, geometry.getCoordinates()[1].x, DELTA);
                assertEquals(35.04603679820616, geometry.getCoordinates()[1].y, DELTA);
            }
            else if (geometry.getGeometryType().equals("LineString")) {
                assertEquals(-80.66237211227417, geometry.getCoordinates()[0].x, DELTA);
                assertEquals(35.05950973022538, geometry.getCoordinates()[0].y, DELTA);

                assertEquals(-80.66269397735596, geometry.getCoordinates()[1].x, DELTA);
                assertEquals(35.0592638296087, geometry.getCoordinates()[1].y, DELTA);
            }
            else // Fail, the geometrycollection.json file contains only 3 geometry
                 // of the above types.
                fail();
        }
    }

    /**
     * Helper method to test that a given Geometry as the same values as in the
     * test file geometry.json.
     *
     * @param geometry the geometry to test
     */
    public static void assertGeometryIsValid(final Geometry geometry) {
        assertNotNull(geometry);
        assertEquals(-80.87088507656375, geometry.getCoordinate().x, DELTA);
        assertEquals(35.21515162500578, geometry.getCoordinate().y, DELTA);
    }

    /**
     * Verify that given complex data has exactly one format available, and that
     * its parameters match given ones. If a given parameter is null, we expect
     * it to be null in tested format.
     *
     * @param toTest The complex data to test.
     * @param encoding The expected format encoding (Example: UTF-8, US-ASCII, etc.)
     * @param mime Expected format mime-type (Example: image/png, application/geo+json, etc.)
     * @param schema Expected schema information.
     */
    public static void assertSingleFormat(final ComplexData toTest, final String encoding, final String mime, final String schema) {
        assertNotNull("Complex data to test", toTest);
        final List<Format> formats = toTest.getFormat();
        assertNotNull("No format list associated to the complex data", formats);
        assertEquals("Only one format should be available", 1, formats.size());
        final Format f = formats.get(0);
        assertEquals("Data mime type", mime, f.getMimeType());
        assertEquals("Data encoding", encoding, f.getEncoding());
        assertEquals("Data schema", schema, f.getSchema());
    }

    /**
     * Verify that given data attributes match input format specifications.
     * @param data The data to test.
     * @param encoding The expected format encoding (Example: UTF-8, US-ASCII, etc.)
     * @param mime Expected format mime-type (Example: image/png, application/geo+json, etc.)
     * @param schema Expected schema information.
     */
    public static void assertFormatMatch(final Data data, final String encoding, final String mime, final String schema) {
        assertNotNull("Data to test", data);
        assertEquals("Data mime type", mime, data.getMimeType());
        assertEquals("Data encoding", encoding, data.getEncoding());
        assertEquals("Data schema", schema, data.getSchema());
    }

    public static void useDataContentAsFile(final Data input, Consumer<Path> fileUser) throws IOException {
        final List<Object> content = input.getContent();
        assertEquals(1, content.size());
        assertNotNull(content);
        assertFalse("No content available in test data", content.isEmpty());
        assertTrue(content.get(0) instanceof String);

        String geoJson = (String) content.get(0);
        if (geoJson.startsWith(WPSUtilities.CDATA_START_TAG)) {
            geoJson = geoJson.substring(WPSUtilities.CDATA_START_TAG.length(), geoJson.length() - WPSUtilities.CDATA_END_TAG.length());
        }

        // Write the json in a tmp file in order to be able to read it
        Path tmpFilePath = WPSConvertersUtils.writeTempJsonFile(geoJson);
        try {
            fileUser.accept(tmpFilePath);
        } finally {
            Files.delete(tmpFilePath);
        }
    }
}
