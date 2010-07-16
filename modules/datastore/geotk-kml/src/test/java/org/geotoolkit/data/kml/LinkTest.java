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

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.NetworkLink;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
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
public class LinkTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/link.kml";

    public LinkTest() {
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
    public void linkReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof NetworkLink);
        NetworkLink networkLink = (NetworkLink) feature;
        assertEquals("NE US Radar", networkLink.getName());
        assertTrue(networkLink.getFlyToView());

        assertTrue(networkLink.getLink() instanceof Link);
        Link link = networkLink.getLink();
        assertEquals("http://www.example.com/geotiff/NE/MergedReflectivityQComposite.kml", link.getHref());
        assertEquals(RefreshMode.ON_INTERVAL, link.getRefreshMode());
        assertEquals(30, link.getRefreshInterval(), DELTA);
        assertEquals(ViewRefreshMode.ON_STOP, link.getViewRefreshMode());
        assertEquals(7, link.getViewRefreshTime(), DELTA);
        String text = "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth];CAMERA=\\\n"+
"      [lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading];VIEW=\\\n"+
"      [horizFov],[vertFov],[horizPixels],[vertPixels],[terrainEnabled]";
        assertEquals(text, link.getViewFormat());

    }

    @Test
    public void linkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final NetworkLink networkLink = kmlFactory.createNetworkLink();
        networkLink.setName("NE US Radar");
        networkLink.setFlyToView(true);

        final Link link = kmlFactory.createLink();
        link.setHref("http://www.example.com/geotiff/NE/MergedReflectivityQComposite.kml");
        link.setRefreshMode(RefreshMode.ON_INTERVAL);
        link.setRefreshInterval(30);
        link.setViewRefreshMode(ViewRefreshMode.ON_STOP);
        link.setViewRefreshTime(7);
        String text = "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth];CAMERA=\\\n"+
"      [lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading];VIEW=\\\n"+
"      [horizFov],[vertFov],[horizPixels],[vertPixels],[terrainEnabled]";
        link.setViewFormat(text);
        networkLink.setLink(link);

        final Kml kml = kmlFactory.createKml(null, networkLink, null, null);

        File temp = File.createTempFile("testLink",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }

}