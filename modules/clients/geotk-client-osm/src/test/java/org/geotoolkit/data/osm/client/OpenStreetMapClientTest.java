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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.xml.OSMXMLReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OpenStreetMapClientTest extends org.geotoolkit.test.TestBase {

    private final OpenStreetMapClient server;

    public OpenStreetMapClientTest() throws MalformedURLException {
        server = new OpenStreetMapClient(new URL("http://api.openstreetmap.org/"), OSMVersion.v060.getCode());
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
            Api api = server.getCapabilities(); //check the server respond
            valid = (api!=null);
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
    public void testReadNodeElement() throws XMLStreamException, IOException{
        final ReadElementRequest request = server.createReadElement();
        request.setElementType(OSMType.NODE);
        request.setId(310);
        request.setVersion(-1);

        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        assertTrue(reader.hasNext());
        Object candidate = reader.next();
        assertNotNull(candidate);
        assertTrue(candidate instanceof Feature);
        assertFalse(reader.hasNext());
        Feature n = (Feature) candidate;
        assertEquals(310l, n.getPropertyValue("id"));

        request.setVersion(1);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v1 = (Feature) reader.next();
        assertEquals(310l, n.getPropertyValue("id"));

        request.setVersion(3);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v3 = (Feature) reader.next();
        assertEquals(310l, n.getPropertyValue("id"));

        assertNotSame(v1, v3);
        assertNotSame(v1.getPropertyValue("point"), v3.getPropertyValue("point"));
    }

    @Test
    public void testReadWayElement() throws XMLStreamException, IOException{
        final ReadElementRequest request = server.createReadElement();
        request.setElementType(OSMType.WAY);
        request.setId(310l);
        request.setVersion(-1);

        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        assertTrue(reader.hasNext());
        Object candidate = reader.next();
        assertNotNull(candidate);
        assertTrue(candidate instanceof Feature);
        assertFalse(reader.hasNext());
        Feature n = (Feature) candidate;
        assertEquals(310l, n.getPropertyValue("id"));

        request.setVersion(1);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v1 = (Feature) reader.next();
        assertEquals(310l, n.getPropertyValue("id"));

        request.setVersion(3);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v3 = (Feature) reader.next();
        assertEquals(310l, n.getPropertyValue("id"));

        assertNotSame(v1, v3);
        assertNotSame(v1.toString(), v3.toString());
    }

    @Test
    public void testReadRelationElement() throws XMLStreamException, IOException{
        final ReadElementRequest request = server.createReadElement();
        request.setElementType(OSMType.RELATION);
        request.setId(410l);
        request.setVersion(-1);

        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        assertTrue(reader.hasNext());
        Object candidate = reader.next();
        assertNotNull(candidate);
        assertTrue(candidate instanceof Feature);
        assertFalse(reader.hasNext());
        Feature n = (Feature) candidate;
        assertEquals(410l, n.getPropertyValue("id"));

        request.setVersion(1);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v1 = (Feature) reader.next();
        assertEquals(410l, n.getPropertyValue("id"));

        request.setVersion(3);
        reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());
        Feature v3 = (Feature) reader.next();
        assertEquals(410l, n.getPropertyValue("id"));

        assertNotSame(v1, v3);
        assertNotSame(v1.toString(), v3.toString());
    }

    @Test
    public void testReadElementHistory() throws XMLStreamException, IOException{
        final ReadElementHistoryRequest request = server.createHistoryElement();
        request.setElementType(OSMType.NODE);
        request.setId(310);

        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(request.getURL().openStream());

        int nbVersion = 0;
        while(reader.hasNext()){
            Object candidate = reader.next();
            assertNotNull(candidate);
            assertTrue(candidate instanceof Feature);
            Feature n = (Feature) candidate;
            assertEquals(310l, n.getPropertyValue("id"));
            nbVersion++;
        }

        assertTrue(nbVersion > 1);
    }

}
