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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class ChangeTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/change.kml";

    public ChangeTest() {
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
    public void changeReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://www/~sam/January14Data/Point.kml", targetHref.toString());

        assertEquals(1, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Change);
        Change change = (Change) update.getUpdates().get(0);

        assertEquals(1, change.getObjects().size());
        assertTrue(change.getObjects().get(0) instanceof Point);

        Point point = (Point) change.getObjects().get(0);
        point.getIdAttributes();
        assertEquals("point123", point.getIdAttributes().getTargetId());

        CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, point.getCoordinateSequence().size());
        Coordinate coordinate = point.getCoordinateSequence().getCoordinate(0);
        assertEquals(-95.48, coordinate.x, DELTA);
        assertEquals(40.43, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);

    }

    @Test
    public void changeWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-95.48, 40.43, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));

        final Point point = kmlFactory.createPoint(coordinates);
        point.setIdAttributes(kmlFactory.createIdAttributes(null, "point123"));

        final Change change = kmlFactory.createChange(Arrays.asList((Object) point));

        final Update update = kmlFactory.createUpdate();
        update.setUpdates(Arrays.asList((Object) change));
        update.setTargetHref(new URI("http://www/~sam/January14Data/Point.kml"));

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        final File temp = File.createTempFile("testChange", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
