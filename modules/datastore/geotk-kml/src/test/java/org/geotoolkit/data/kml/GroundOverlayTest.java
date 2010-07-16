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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.GroundOverlay;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.RefreshMode;
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
public class GroundOverlayTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/groundOverlay.kml";

    public GroundOverlayTest() {
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
    public void groundOverlayReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof GroundOverlay);
        final GroundOverlay groundOverlay = (GroundOverlay) feature;
        assertEquals("GroundOverlay.kml", groundOverlay.getName());
        assertEquals("7fffffff",KmlUtilities.toKmlColor(groundOverlay.getColor()));
        assertEquals(1,groundOverlay.getDrawOrder());
        final Icon icon = groundOverlay.getIcon();
            assertEquals("http://www.google.com/intl/en/images/logo.gif",icon.getHref());
            assertEquals(RefreshMode.ON_INTERVAL, icon.getRefreshMode());
            assertEquals(86400, icon.getRefreshInterval(),DELTA);
            assertEquals(0.75, icon.getViewBoundScale(),DELTA);

        final LatLonBox latLonBox = groundOverlay.getLatLonBox();
            assertEquals(37.83234, latLonBox.getNorth(), DELTA);
            assertEquals(37.832122, latLonBox.getSouth(), DELTA);
            assertEquals(-122.373033, latLonBox.getEast(), DELTA);
            assertEquals(-122.373724, latLonBox.getWest(), DELTA);
            assertEquals(45, latLonBox.getRotation(), DELTA);

    }

    @Test
    public void groundOverlayWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final double north = 37.83234;
        final double south = 37.832122;
        final double east = -122.373033;
        final double west = -122.373724;
        final double rotation = 45;

        final LatLonBox latLonBox = kmlFactory.createLatLonBox(null, null, north, south, east, west, null, null, rotation, null, null);

        final String href = "http://www.google.com/intl/en/images/logo.gif";
        final RefreshMode refreshMode = RefreshMode.ON_INTERVAL;
        final double refreshInterval = 86400;
        final double viewBoundScale = 0.75;
        final Link link = kmlFactory.createLink();
        link.setHref(href);
        link.setRefreshMode(refreshMode);
        link.setRefreshInterval(refreshInterval);
        link.setViewBoundScale(viewBoundScale);
        final Icon icon = kmlFactory.createIcon(link);

        //final Icon icon = kmlFactory.cre

        final String name = "GroundOverlay.kml";
        final Color color = new Color(255,255,255,127);
        final int drawOrder = 1;
        
        final GroundOverlay groundOverlay = kmlFactory.createGroundOverlay();
        groundOverlay.setName(name);
        groundOverlay.setColor(color);
        groundOverlay.setDrawOrder(drawOrder);
        groundOverlay.setIcon(icon);
        groundOverlay.setLatLonBox(latLonBox);

        final Kml kml = kmlFactory.createKml(null, groundOverlay, null, null);

        File temp = File.createTempFile("testGroundOverlay",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}