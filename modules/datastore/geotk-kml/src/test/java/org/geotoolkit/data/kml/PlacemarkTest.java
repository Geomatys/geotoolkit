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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Point;
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
public class PlacemarkTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/placemark.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public PlacemarkTest() {
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
    public void placemarkReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));
        assertEquals("Google Earth - New Placemark", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals("Some Descriptive text.", placemark.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());

        final AbstractView view = (AbstractView) placemark.getProperty(KmlModelConstants.ATT_VIEW.getName()).getValue();
        assertTrue(view instanceof LookAt);
        LookAt lookAt = (LookAt) view;
        assertEquals(-90.86879847669974, lookAt.getLongitude(), DELTA);
        assertEquals(48.25330383601299, lookAt.getLatitude(), DELTA);
        assertEquals(2.7, lookAt.getHeading(), DELTA);
        assertEquals(8.3, lookAt.getTilt(), DELTA);
        assertEquals(440.8, lookAt.getRange(), DELTA);

        final AbstractGeometry geometry = (AbstractGeometry) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
        assertTrue(geometry instanceof Point);
        Point point = (Point) geometry;
        final CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-90.86948943473118, coordinate.x, DELTA);
        assertEquals(48.25450093195546, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
    }

    @Test
    public void placemarkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-90.86948943473118, 48.25450093195546, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-90.86879847669974);
        lookAt.setLatitude(48.25330383601299);
        lookAt.setHeading(2.7);
        lookAt.setTilt(8.3);
        lookAt.setRange(440.8);

        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("Google Earth - New Placemark", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute("Some Descriptive text.", KmlModelConstants.ATT_DESCRIPTION, null));
        placemarkProperties.add(FF.createAttribute(lookAt, KmlModelConstants.ATT_VIEW, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("testPlacemark",".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }
}
