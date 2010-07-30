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
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.ColorMode;
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
public class LabelStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/labelStyle.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public LabelStyleTest() {
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
    public void labelStyleReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();

        if (i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            Style style = (Style) object;
            assertEquals("randomLabelColor", style.getIdAttributes().getId());
            LabelStyle labelStyle = style.getLabelStyle();
            assertEquals(new Color(204, 0, 0, 255), labelStyle.getColor());
            assertEquals(ColorMode.RANDOM, labelStyle.getColorMode());
            assertEquals(1.5, labelStyle.getScale(), DELTA);
        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark = (Feature) object;
            assertEquals("LabelStyle.kml", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#randomLabelColor"), placemark.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
            Point point = (Point) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            CoordinateSequence coordinates = point.getCoordinateSequence();
            assertEquals(1, coordinates.size());
            Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(-122.367375, coordinate.x, DELTA);
            assertEquals(37.829192, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);

        }
    }

    @Test
    public void labelStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-122.367375, 37.829192, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("LabelStyle.kml", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(new URI("#randomLabelColor"), KmlModelConstants.ATT_STYLE_URL, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Style style = kmlFactory.createStyle();
            LabelStyle labelStyle = kmlFactory.createLabelStyle();
            BasicLink icon = kmlFactory.createBasicLink();
            labelStyle.setScale(1.5);
            labelStyle.setColor(new Color(204, 0, 0, 255));
            labelStyle.setColorMode(ColorMode.RANDOM);
        style.setLabelStyle(labelStyle);
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("randomLabelColor", null);
        style.setIdAttributes(idAttributes);

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testLabelStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}