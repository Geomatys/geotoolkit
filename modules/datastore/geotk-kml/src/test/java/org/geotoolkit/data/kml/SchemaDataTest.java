/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class SchemaDataTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/schemaData.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public SchemaDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void schemaDataReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals(2, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark0 = (Feature) object;
            assertTrue(placemark0.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("Easy trail", placemark0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            final Object dataContainer0 = placemark0.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            assertTrue(dataContainer0 instanceof ExtendedData);
            final ExtendedData extendedData0 = (ExtendedData) dataContainer0;
            assertEquals(1, extendedData0.getSchemaData().size());
            final SchemaData schemaData0 = extendedData0.getSchemaData().get(0);
            assertEquals("#TrailHeadTypeId", schemaData0.getSchemaURL().toString());

            assertEquals(3, schemaData0.getSimpleDatas().size());

            final SimpleData simpelData00 = schemaData0.getSimpleDatas().get(0);
            assertEquals("TrailHeadName", simpelData00.getName());
            assertEquals("Pi in the sky", simpelData00.getContent());

            final SimpleData simpelData01 = schemaData0.getSimpleDatas().get(1);
            assertEquals("TrailLength", simpelData01.getName());
            assertEquals("3.14159", simpelData01.getContent());

            final SimpleData simpelData02 = schemaData0.getSimpleDatas().get(2);
            assertEquals("ElevationGain", simpelData02.getName());
            assertEquals("10", simpelData02.getContent());

            assertTrue(placemark0.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
            final Point point0 = (Point) placemark0.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            final CoordinateSequence coordinates0 = point0.getCoordinateSequence();
            assertEquals(1, coordinates0.size());

            final Coordinate coordinate0 = coordinates0.getCoordinate(0);
            assertEquals(-122, coordinate0.x, DELTA);
            assertEquals(37.002, coordinate0.y, DELTA);
        }

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark1 = (Feature) object;
            assertTrue(placemark1.getType().equals(KmlModelConstants.TYPE_PLACEMARK));

            assertEquals("Difficult trail", placemark1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            final Object dataContainer1 = placemark1.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            assertTrue(dataContainer1 instanceof ExtendedData);
            ExtendedData extendedData1 = (ExtendedData) dataContainer1;
            assertEquals(1, extendedData1.getSchemaData().size());
            final SchemaData schemaData1 = extendedData1.getSchemaData().get(0);
            assertEquals("#TrailHeadTypeId", schemaData1.getSchemaURL().toString());

            assertEquals(3, schemaData1.getSimpleDatas().size());

            final SimpleData simpelData10 = schemaData1.getSimpleDatas().get(0);
            assertEquals("TrailHeadName", simpelData10.getName());
            assertEquals("Mount Everest", simpelData10.getContent());

            final SimpleData simpelData11 = schemaData1.getSimpleDatas().get(1);
            assertEquals("TrailLength", simpelData11.getName());
            assertEquals("347.45", simpelData11.getContent());

            final SimpleData simpelData12 = schemaData1.getSimpleDatas().get(2);
            assertEquals("ElevationGain", simpelData12.getName());
            assertEquals("10000", simpelData12.getContent());

            assertTrue(placemark1.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
            final Point point1 = (Point) placemark1.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            final CoordinateSequence coordinates1 = point1.getCoordinateSequence();
            assertEquals(1, coordinates1.size());

            final Coordinate coordinate1 = coordinates1.getCoordinate(0);
            assertEquals(-122, coordinate1.x, DELTA);
            assertEquals(37.002, coordinate1.y, DELTA);
        }
    }

    @Test
    public void schemaDataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate0 = kmlFactory.createCoordinate(-122, 37.002);
        final CoordinateSequence coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate0));
        final Point point0 = kmlFactory.createPoint(coordinates0);

        final SimpleData simpleData00 = kmlFactory.createSimpleData("TrailHeadName", "Pi in the sky");
        final SimpleData simpleData01 = kmlFactory.createSimpleData("TrailLength", "3.14159");
        final SimpleData simpleData02 = kmlFactory.createSimpleData("ElevationGain", "10");

        final SchemaData schemaData0 = kmlFactory.createSchemaData();
        schemaData0.setSimpleDatas(Arrays.asList(simpleData00, simpleData01, simpleData02));
        schemaData0.setSchemaURL(new URI("#TrailHeadTypeId"));

        final ExtendedData extendedData0 = kmlFactory.createExtendedData();
        extendedData0.setSchemaData(Arrays.asList(schemaData0));

        final Feature placemark0 = kmlFactory.createPlacemark();
        final Collection<Property> placemark0Properties = placemark0.getProperties();
        placemark0Properties.add(FF.createAttribute(extendedData0, KmlModelConstants.ATT_EXTENDED_DATA, null));
        placemark0Properties.add(FF.createAttribute("Easy trail", KmlModelConstants.ATT_NAME, null));
        placemark0Properties.add(FF.createAttribute(point0, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Coordinate coordinate1 = kmlFactory.createCoordinate(-122, 37.002);
        final CoordinateSequence coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate1));
        final Point point1 = kmlFactory.createPoint(coordinates1);

        final SimpleData simpleData10 = kmlFactory.createSimpleData("TrailHeadName", "Mount Everest");
        final SimpleData simpleData11 = kmlFactory.createSimpleData("TrailLength", "347.45");
        final SimpleData simpleData12 = kmlFactory.createSimpleData("ElevationGain", "10000");

        final SchemaData schemaData1 = kmlFactory.createSchemaData();
        schemaData1.setSimpleDatas(Arrays.asList(simpleData10, simpleData11, simpleData12));
        schemaData1.setSchemaURL(new URI("#TrailHeadTypeId"));

        final ExtendedData extendedData1 = kmlFactory.createExtendedData();
        extendedData1.setSchemaData(Arrays.asList(schemaData1));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final Collection<Property> placemark1Properties = placemark1.getProperties();
        placemark1Properties.add(FF.createAttribute(extendedData1, KmlModelConstants.ATT_EXTENDED_DATA, null));
        placemark1Properties.add(FF.createAttribute("Difficult trail", KmlModelConstants.ATT_NAME, null));
        placemark1Properties.add(FF.createAttribute(point1, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        documentProperties.add(FF.createAttribute(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testSchemaData", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
