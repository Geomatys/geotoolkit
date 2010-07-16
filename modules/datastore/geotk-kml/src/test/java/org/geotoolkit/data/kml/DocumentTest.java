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
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class DocumentTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/document.kml";

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

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        Document document = (Document) feature;
        assertEquals("Document.kml",document.getName());
        assertTrue(document.getOpen());

        List<AbstractStyleSelector> styleSelectors = document.getStyleSelectors();
        assertEquals(1,styleSelectors.size());
        assertTrue(styleSelectors.get(0) instanceof Style);
        Style style = (Style) styleSelectors.get(0);
        assertEquals("exampleStyleDocument", style.getIdAttributes().getId());
        assertEquals(new Color(204,0,0,255), style.getLabelStyle().getColor());

        assertEquals(2,document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        assertTrue(document.getAbstractFeatures().get(1) instanceof Placemark);
        Placemark placemark0 = (Placemark) document.getAbstractFeatures().get(0);
        Placemark placemark1 = (Placemark) document.getAbstractFeatures().get(1);

        assertEquals("Document Feature 1",placemark0.getName());
        assertEquals(new URI("#exampleStyleDocument"),placemark0.getStyleUrl());
        AbstractGeometry abstractGeometry0 = placemark0.getAbstractGeometry();
        assertTrue(abstractGeometry0 instanceof Point);
        Point point0 = (Point) abstractGeometry0;
        Coordinates coordinates0 = point0.getCoordinateSequence();
        assertEquals(1, coordinates0.size());
        Coordinate coordinate00 = coordinates0.getCoordinate(0);
        assertEquals(-122.371, coordinate00.x, DELTA);
        assertEquals(37.816, coordinate00.y, DELTA);
        assertEquals(0, coordinate00.z, DELTA);

        assertEquals("Document Feature 2",placemark1.getName());
        assertEquals(new URI("#exampleStyleDocument"),placemark1.getStyleUrl());
        AbstractGeometry abstractGeometry1 = placemark1.getAbstractGeometry();
        assertTrue(abstractGeometry1 instanceof Point);
        Point point1 = (Point) abstractGeometry1;
        Coordinates coordinates1 = point1.getCoordinateSequence();
        assertEquals(1, coordinates1.size());
        Coordinate coordinate10 = coordinates1.getCoordinate(0);
        assertEquals(-122.370, coordinate10.x, DELTA);
        assertEquals(37.817, coordinate10.y, DELTA);
        assertEquals(0, coordinate10.z, DELTA);
    }
    
    @Test
    public void documentWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException{
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark0 = kmlFactory.createPlacemark();
        final double longitude00 = -122.371;
        final double latitude00 = 37.816;
        final double altitude00 = 0;
        final Coordinate coordinate00 = kmlFactory.createCoordinate(longitude00, latitude00, altitude00);
        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00));
        final Point point0 = kmlFactory.createPoint(coordinates0);
        placemark0.setAbstractGeometry(point0);
        placemark0.setName("Document Feature 1");
        placemark0.setStyleUrl(new URI("#exampleStyleDocument"));

        final Placemark placemark1 = kmlFactory.createPlacemark();
        final double longitude10 = -122.370;
        final double latitude10 = 37.817;
        final double altitude10 = 0;
        final Coordinate coordinate10 = kmlFactory.createCoordinate(longitude10, latitude10, altitude10);
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10));
        final Point point1 = kmlFactory.createPoint(coordinates1);
        placemark1.setAbstractGeometry(point1);
        placemark1.setName("Document Feature 2");
        placemark1.setStyleUrl(new URI("#exampleStyleDocument"));

        Style style = kmlFactory.createStyle();
        Color color = new Color(204,0,0,255);
        LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setColor(color);
        style.setLabelStyle(labelStyle);

        IdAttributes idAttributes = kmlFactory.createIdAttributes("exampleStyleDocument", null);
        style.setIdAttributes(idAttributes);

        Document document = kmlFactory.createDocument();
        document.setName("Document.kml");
        document.setOpen(true);
        List<AbstractStyleSelector> styleSelectors = Arrays.asList((AbstractStyleSelector) style);
        document.setStyleSelectors(styleSelectors);
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0, (AbstractFeature) placemark1));

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