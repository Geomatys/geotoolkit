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
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
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
public class BalloonStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/balloonStyle.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public BalloonStyleTest() {
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
    public void balloonStyleReadTest() throws IOException, XMLStreamException, URISyntaxException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("BalloonStyle.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
        assertEquals(1, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());
        i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
        if (i.hasNext()) {
            Object styleSelector = ((Property) i.next()).getValue();
            assertTrue(styleSelector instanceof Style);
            final Style style = (Style) styleSelector;
            assertEquals("exampleBalloonStyle", style.getIdAttributes().getId());
            final BalloonStyle balloonStyle = style.getBalloonStyle();
            assertEquals(new Color(187, 255, 255, 255), balloonStyle.getBgColor());
            final Cdata text = new DefaultCdata("\n      <b><font color=\"#CC0000\" size=\"+3\">$[name]</font></b>\n"+
"      <br/><br/>\n"+
"      <font face=\"Courier\">$[description]</font>\n"+
"      <br/><br/>\n"+
"      Extra text that will appear in the description balloon\n"+
"      <br/><br/>\n"+
"      $[geDirections]\n"+
"      ");
            assertEquals(text, balloonStyle.getText());
        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());
        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
        if (i.hasNext()){
            final Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark = (Feature) object;
            assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));
            assertEquals("BalloonStyle", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals("An example of BalloonStyle", placemark.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());
            assertEquals(new URI("#exampleBalloonStyle"),placemark.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
            final Point point = (Point) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            final CoordinateSequence coordinates = point.getCoordinateSequence();
            assertEquals(1, coordinates.size());
            final Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(-122.370533, coordinate.x, DELTA);
            assertEquals(37.823842, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);
        }
    }

    @Test
    public void balloonStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-122.370533,37.823842,0.0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("BalloonStyle", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute("An example of BalloonStyle", KmlModelConstants.ATT_DESCRIPTION, null));
        placemarkProperties.add(FF.createAttribute(new URI("#exampleBalloonStyle"), KmlModelConstants.ATT_STYLE_URL, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final BalloonStyle balloonStyle = kmlFactory.createBalloonStyle();
        final Cdata text = new DefaultCdata("\n      <b><font color=\"#CC0000\" size=\"+3\">$[name]</font></b>\n"+
"      <br/><br/>\n"+
"      <font face=\"Courier\">$[description]</font>\n"+
"      <br/><br/>\n"+
"      Extra text that will appear in the description balloon\n"+
"      <br/><br/>\n"+
"      $[geDirections]\n"+
"      ");
        balloonStyle.setText(text);
        balloonStyle.setBgColor(new Color(187, 255, 255, 255));

        final IdAttributes idAttributes = kmlFactory.createIdAttributes("exampleBalloonStyle", null);

        final Style style = kmlFactory.createStyle();
        style.setIdAttributes(idAttributes);
        style.setBalloonStyle(balloonStyle);

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("BalloonStyle.kml", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testBalloonStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}
