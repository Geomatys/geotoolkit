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
package org.geotoolkit.data.gx;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
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
public class BalloonVisibilityTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/balloonVisibility.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public BalloonVisibilityTest() {
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
    public void balloonVisibilityReadTest()
            throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));
        assertEquals("Eiffel Tower", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        final String description = "\n        Located in Paris, France.\n"+
"\n        This description balloon opens\n"+
"        when the Placemark is loaded.\n"+"    ";
        assertEquals(description, placemark.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());
        assertEquals(1, ((Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).simples(Extensions.Names.FEATURE).size());
        assertTrue(((Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).simples(Extensions.Names.FEATURE).get(0) instanceof SimpleTypeContainer);
        final SimpleTypeContainer balloonVisibility = ((Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).simples(Extensions.Names.FEATURE).get(0);

        assertEquals(GxConstants.URI_GX, balloonVisibility.getNamespaceUri());
        assertEquals(GxConstants.TAG_BALLOON_VISIBILITY, balloonVisibility.getTagName());
        assertFalse((Boolean) balloonVisibility.getValue());

        assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
        final Point point = (Point) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();

        assertEquals(1, point.getCoordinateSequence().size());
        final Coordinate coordinate = point.getCoordinateSequence().getCoordinate(0);
        assertEquals(2.294785, coordinate.x, DELTA);
        assertEquals(48.858093, coordinate.y, DELTA);
        assertEquals(0.0, coordinate.z, DELTA);

    }

    @Test
    public void balloonVisibilityWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(
                kmlFactory.createCoordinate("2.294785,48.858093,0.0")));

        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("Eiffel Tower", KmlModelConstants.ATT_NAME, null));
        final String description = "\n        Located in Paris, France.\n"+
"\n        This description balloon opens\n"+
"        when the Placemark is loaded.\n"+"    ";
        placemarkProperties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        ((Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).
                simples(Extensions.Names.FEATURE).add(kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_BALLOON_VISIBILITY, false));

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testBalloonVisibility", ".kml");
        //temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        final GxWriter gxWriter = new GxWriter(writer);
        writer.setOutput(temp);
        writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }

}