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

import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
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
public class NetworkLinkControlTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/networkLinkControl.kml";

    public NetworkLinkControlTest() {
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
    public void networkLinkControlReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        assertEquals("cookie=sometext", networkLinkControl.getCookie());
        assertEquals("This is a pop-up message. You will only see this once", networkLinkControl.getMessage());
        assertEquals("New KML features", networkLinkControl.getLinkName());
        final Cdata text = new DefaultCdata("<span>KML now has new features available!</span>");
        assertEquals(text, (Cdata) networkLinkControl.getLinkDescription());

    }

    @Test
    public void networkLinkControlWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setCookie("cookie=sometext");
        networkLinkControl.setMessage("This is a pop-up message. You will only see this once");
        networkLinkControl.setLinkName("New KML features");
        final Cdata text = new DefaultCdata("<span>KML now has new features available!</span>");
        networkLinkControl.setLinkDescription(text);

        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        final File temp = File.createTempFile("testNetworkLinkControl", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
