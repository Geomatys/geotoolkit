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
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
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
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class DocumentTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/document.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
    
    public DocumentTest() {
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
    public void documentReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals("Document.kml",document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(1,document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());
        Iterator i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            Style style = (Style) object;
            assertEquals("exampleStyleDocument", style.getIdAttributes().getId());
            assertEquals(new Color(204,0,0,255), style.getLabelStyle().getColor());

        }

        assertEquals(2,document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());
        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            System.out.println(object.getClass());
            assertTrue(object instanceof Feature);
            Feature placemark0 = (Feature) object;

            assertEquals("Document Feature 1",placemark0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#exampleStyleDocument"),placemark0.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
            AbstractGeometry abstractGeometry0 = (AbstractGeometry) placemark0.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            assertTrue(abstractGeometry0 instanceof Point);
            Point point0 = (Point) abstractGeometry0;
            Coordinates coordinates0 = point0.getCoordinateSequence();
            assertEquals(1, coordinates0.size());
            Coordinate coordinate00 = coordinates0.getCoordinate(0);
            assertEquals(-122.371, coordinate00.x, DELTA);
            assertEquals(37.816, coordinate00.y, DELTA);
            assertEquals(0, coordinate00.z, DELTA);
        }

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            System.out.println(object.getClass());
            assertTrue(object instanceof Feature);
            Feature placemark1 = (Feature) object;

            assertEquals("Document Feature 2",placemark1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#exampleStyleDocument"),placemark1.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
            AbstractGeometry abstractGeometry1 = (AbstractGeometry) placemark1.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            assertTrue(abstractGeometry1 instanceof Point);
            Point point1 = (Point) abstractGeometry1;
            Coordinates coordinates1 = point1.getCoordinateSequence();
            assertEquals(1, coordinates1.size());
            Coordinate coordinate10 = coordinates1.getCoordinate(0);
            assertEquals(-122.370, coordinate10.x, DELTA);
            assertEquals(37.817, coordinate10.y, DELTA);
            assertEquals(0, coordinate10.z, DELTA);
        }
    }

    @Test
    public void documentWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException{
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.371;
        final double latitude00 = 37.816;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point0 = kmlFactory.createPoint(coordinates0);
        Collection<Property> placemark0Properties = placemark0.getProperties();
        placemark0Properties.add(FF.createAttribute(point0, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        placemark0Properties.add(FF.createAttribute("Document Feature 1", KmlModelConstants.ATT_NAME, null));
        placemark0Properties.add(FF.createAttribute(new URI("#exampleStyleDocument"), KmlModelConstants.ATT_STYLE_URL, null));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final double longitude10 = -122.370;
        final double latitude10 = 37.817;
        final double altitude10 = 0;
        final Coordinate coordinate10 = kmlFactory.createCoordinate(longitude10, latitude10, altitude10);
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10));
        final Point point1 = kmlFactory.createPoint(coordinates1);
        Collection<Property> placemark1Properties = placemark1.getProperties();
        placemark1Properties.add(FF.createAttribute(point1, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        placemark1Properties.add(FF.createAttribute("Document Feature 2", KmlModelConstants.ATT_NAME, null));
        placemark1Properties.add(FF.createAttribute(new URI("#exampleStyleDocument"), KmlModelConstants.ATT_STYLE_URL, null));

        Style style = kmlFactory.createStyle();
        Color color = new Color(204,0,0,255);
        LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setColor(color);
        style.setLabelStyle(labelStyle);

        IdAttributes idAttributes = kmlFactory.createIdAttributes("exampleStyleDocument", null);
        style.setIdAttributes(idAttributes);

        Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("Document.kml", KmlModelConstants.ATT_NAME, null));
        documentProperties.add(FF.createAttribute(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        documentProperties.add(FF.createAttribute(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(true);
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testDocument",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }
}