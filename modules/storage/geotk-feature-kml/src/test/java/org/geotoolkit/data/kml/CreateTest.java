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
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class CreateTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/create.kml";

    @Test
    public void createReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://myserver.com/Point.kml", targetHref.toString());

        assertEquals(1, update.getUpdates().size());
        Create create = (Create) update.getUpdates().get(0);

        assertEquals(1, create.getContainers().size());
        final Feature document = create.getContainers().get(0);
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("region24",((IdAttributes) document.getPropertyValue(KmlConstants.ATT_ID)).getTargetId());

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        final Feature placemark = (Feature) i.next();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());

        assertEquals("placemark891", ((IdAttributes) placemark.getPropertyValue(KmlConstants.ATT_ID)).getId());
        final Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);

        final CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());

        final Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-95.48, coordinate.x, DELTA);
        assertEquals(40.43, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void createWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate = kmlFactory.createCoordinate(-95.48, 40.43, 0);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));

        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        final IdAttributes placemarkIdAttributes = kmlFactory.createIdAttributes("placemark891", null);
        placemark.setPropertyValue(KmlConstants.ATT_ID, placemarkIdAttributes);
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_FEATURES, placemark);
        final IdAttributes documentIdAttributes = kmlFactory.createIdAttributes(null, "region24");
        document.setPropertyValue(KmlConstants.ATT_ID, documentIdAttributes);

        final Create create = kmlFactory.createCreate();
        create.setContainers(Arrays.asList(document));

        final URI targetHref = new URI("http://myserver.com/Point.kml");

        final Update update = kmlFactory.createUpdate();
        update.setUpdates(Arrays.asList((Object) create));
        update.setTargetHref(targetHref);

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);

        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        final File temp = File.createTempFile("testCreate", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
