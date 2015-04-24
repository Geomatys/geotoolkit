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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.TransactionType;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.data.osm.model.OSMModelConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLWriterTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();

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

        final Feature user = OSMModelConstants.TYPE_USER.newInstance();
        user.setPropertyValue("uid", 23);
        user.setPropertyValue("user", "jean");
        ChangeSet cs = new ChangeSet(13, user, date.getTime(), Boolean.TRUE,
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
        file.deleteOnExit();

        final List<Feature> created = new ArrayList<>();
        final Feature n1 = OSMModelConstants.TYPE_NODE.newInstance();
        n1.setPropertyValue("point", GF.createPoint(new Coordinate(45.3, 12.1)));
        n1.setPropertyValue("id", -1l);
        n1.setPropertyValue("version", 465);
        n1.setPropertyValue("changeset", 789);
        n1.setPropertyValue("timestamp", new Date().getTime());
        n1.setPropertyValue("tags", Arrays.asList(createTag("tagKey", "tagValue")));
        final Feature n2 = OSMModelConstants.TYPE_NODE.newInstance();
        n2.setPropertyValue("point", GF.createPoint(new Coordinate(45.3, 12.1)));
        n2.setPropertyValue("id", -1l);
        n2.setPropertyValue("version", 465);
        n2.setPropertyValue("changeset", 789);
        n2.setPropertyValue("timestamp", new Date().getTime());
        n2.setPropertyValue("tags", Arrays.asList(createTag("tagKey2", "tagValue3")));
        final Feature way1 = OSMModelConstants.TYPE_WAY.newInstance();
        way1.setPropertyValue("id", -1l);
        way1.setPropertyValue("version", 461);
        way1.setPropertyValue("changeset", 786);
        way1.setPropertyValue("timestamp", new Date().getTime());
        way1.setPropertyValue("tags", Arrays.asList(createTag("tagKey4", "tagValue5")));
        way1.setPropertyValue("nd", Arrays.asList(-1l,-2l));
        final Feature way2 = OSMModelConstants.TYPE_WAY.newInstance();
        way2.setPropertyValue("id", -1l);
        way2.setPropertyValue("version", 461);
        way2.setPropertyValue("changeset", 783);
        way2.setPropertyValue("timestamp", new Date().getTime());
        way2.setPropertyValue("tags", Arrays.asList(createTag("tagKey6", "tagValue7")));
        way2.setPropertyValue("nd", Arrays.asList(-456l,-329l));
        final Feature rel = OSMModelConstants.TYPE_RELATION.newInstance();
        rel.setPropertyValue("id", 12l);
        rel.setPropertyValue("version", 89);
        rel.setPropertyValue("changeset", 222);
        rel.setPropertyValue("timestamp", new Date().getTime());
        rel.setPropertyValue("tags", Arrays.asList(createTag("tagKey8", "tagValue9")));
        rel.setPropertyValue("members", Arrays.asList(createMember(48, MemberType.NODE, "border")));
        created.add(n1);
        created.add(n2);
        created.add(way1);
        created.add(way2);
        created.add(rel);


//        created.add( new Node(45.3, 12.1, -1, 465, 789, null, new Date().getTime(), Collections.singletonMap("tagKey", "tagValue")) );
//        created.add( new Node(45.3, 12.1, -2, 465, 789, null, new Date().getTime(), Collections.singletonMap("tagKey2", "tagValue3")) );
//        created.add(new Way(UnmodifiableArrayList.wrap(new Long[]{-1l,-2l}),
//                -1, 461, 786, null, new Date().getTime(), Collections.singletonMap("tagKey4", "tagValue5")) );
//        created.add(new Way(UnmodifiableArrayList.wrap(new Long[]{456l,329l}),
//                -1, 467, 783, null, new Date().getTime(), Collections.singletonMap("tagKey6", "tagValue7")) );
//        created.add(new Relation(UnmodifiableArrayList.wrap( new Member[]{new Member(48, MemberType.NODE, "border")}),
//                12, 89, 222, User.NONE, new Date().getTime(), Collections.singletonMap("tagKey8", "tagValue9")));

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

    private static Feature createTag(String key, String value){
        final Feature tag = OSMModelConstants.TYPE_TAG.newInstance();
        tag.setPropertyValue("k", key);
        tag.setPropertyValue("v", value);
        return tag;
    }

    private static Feature createMember(long ref, MemberType type, String role){
        final Feature member = OSMModelConstants.TYPE_RELATION_MEMBER.newInstance();
        member.setPropertyValue("ref", ref);
        member.setPropertyValue("type", type);
        member.setPropertyValue("role", role);
        return member;
    }

}
