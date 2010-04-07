/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.osm.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OpenStreetMapServerTest {

    private final OpenStreetMapServer server;

    public OpenStreetMapServerTest() throws MalformedURLException {
        server = new OpenStreetMapServer(new URL("http://api.openstreetmap.org/"), OSMVersion.v060);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        boolean valid = false;
        try {
            server.getURI().toURL().openConnection().connect();
            valid = true;
        } catch (Exception ex) {
            //server is not accessible
        }
        Assume.assumeTrue(valid);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetURI() {
        assertEquals(server.getURI().toString(),"http://api.openstreetmap.org/");
    }

    @Test
    public void testCapabilities(){
        Api api = server.getCapabilities();
        assertNotNull(api);
        assertNotNull(api.getVersionMaximum());
        assertNotNull(api.getVersionMinimum());
        assertTrue(api.getAreaMaximum() > 0);
        assertTrue(api.getChangesetMaximum() > 0);
        assertTrue(api.getTimeout() > 0);
        assertTrue(api.getTracePointsPerPage() > 0);
        assertTrue(api.getWayNodeMaximum() > 0);
    }

    @Test
    public void testReadElement() throws XMLStreamException, IOException{
        final ReadElementRequest request = server.createReadElement();
        request.setElementType(Node.class);
        request.setId(310);
        request.setVersion(-1);

        OSMXMLReader reader = new OSMXMLReader(request.getURL().openStream());
        assertTrue(reader.hasNext());
        Object candidate = reader.next();
        assertNotNull(candidate);
        assertTrue(candidate instanceof Node);
        Node n = (Node) candidate;
        assertEquals(310, n.getId());

    }

}