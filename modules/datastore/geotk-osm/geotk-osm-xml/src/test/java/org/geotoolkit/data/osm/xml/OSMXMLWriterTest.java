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

package org.geotoolkit.data.osm.xml;

import java.io.File;
import java.util.Collections;
import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLWriterTest {

    private static final double DELTA = 0.000000001;
    
    public OSMXMLWriterTest() {
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
    public void testChangeSetWrite() throws Exception {
        final File file = File.createTempFile("osmwrite", ".xml");

        ChangeSet cs = new ChangeSet(13, User.create(23, "jean"), 123465l, Boolean.TRUE, 
                Bound.create(10, 30, 5, 45),
                Collections.singletonMap("created_by", "geotoolkit"));

        final OSMXMLWriter writer = new OSMXMLWriter();
        writer.setOutput(file);
        writer.writeStartDocument();
        writer.writeOSMTag();
        writer.writeChangeSet(cs);
        writer.writeEndDocument();
        writer.dispose();

        final OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(file);
        Object ob = reader.next();
        assertFalse(reader.hasNext());

        assertTrue(ob instanceof ChangeSet);
        ChangeSet csRead = (ChangeSet) ob;

    }

}