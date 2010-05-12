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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Member;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.TransactionType;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
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
        final File file = File.createTempFile("osmChangeSet", ".xml");
        file.deleteOnExit();
        final Date date = new Date();

        ChangeSet cs = new ChangeSet(13, User.create(23, "jean"), date.getTime(), Boolean.TRUE,
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
        reader.dispose();

        assertTrue(ob instanceof ChangeSet);
        ChangeSet csRead = (ChangeSet) ob;

        assertEquals(cs, csRead);
    }

    @Test
    public void testTransactionWrite() throws Exception {
        final File file = File.createTempFile("osmTransaction", ".xml");

        final List<IdentifiedElement> created = new ArrayList<IdentifiedElement>();
        created.add( new Node(45.3, 12.1, -1, 465, 789, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey", "tagValue")) );
        created.add( new Node(45.3, 12.1, -2, 465, 789, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey2", "tagValue3")) );
        created.add(new Way(UnmodifiableArrayList.wrap(new Long[]{-1l,-2l}),
                -1, 461, 786, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey4", "tagValue5")) );
        created.add(new Way(UnmodifiableArrayList.wrap(new Long[]{456l,329l}),
                -1, 467, 783, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey6", "tagValue7")) );
        created.add(new Relation(UnmodifiableArrayList.wrap( new Member[]{new Member(48, MemberType.NODE, "border")}),
                12, 89, 222, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey8", "tagValue9")));

        final Transaction trans1 = new Transaction(TransactionType.CREATE, created, "1.0.1", "GeotoolKit1.org");
        final Transaction trans2 = new Transaction(TransactionType.MODIFY, created, "1.0.2", "GeotoolKit2.org");
        final Transaction trans3 = new Transaction(TransactionType.DELETE, created, "1.0.3", "GeotoolKit3.org");



        final OSMXMLWriter writer = new OSMXMLWriter();
        writer.setOutput(file);
        writer.writeStartDocument();
        writer.writeOSMChangeTag("version", "generator");
        writer.writeTransaction(trans1);
        writer.writeTransaction(trans2);
        writer.writeTransaction(trans3);
        writer.writeEndDocument();
        writer.dispose();

        final OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(file);
        Transaction r1 = (Transaction) reader.next();
        Transaction r2 = (Transaction) reader.next();
        Transaction r3 = (Transaction) reader.next();
        assertFalse(reader.hasNext());
        reader.dispose();

        assertEquals(trans1, r1);
        assertEquals(trans2, r2);
        assertEquals(trans3, r3);
    }

}