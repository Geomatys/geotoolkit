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

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.TransactionType;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.apache.sis.util.Utilities;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OSMXMLReaderTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000001;

    public OSMXMLReaderTest() {
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
    public void testReading() throws FileNotFoundException, XMLStreamException, IOException, ParseException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/sampleOSM.osm");
        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(testFile);

        //check that the bound is correctly read
        Envelope env = reader.getEnvelope();
        assertNotNull(env);
        assertTrue(Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), CommonCRS.WGS84.normalizedGeographic()));
        assertEquals(-0.108157396316528d, env.getMinimum(0),DELTA);
        assertEquals(-0.107599496841431d, env.getMaximum(0),DELTA);
        assertEquals(51.5073601795557d, env.getMinimum(1),DELTA);
        assertEquals(51.5076406454029d, env.getMaximum(1),DELTA);

        final List<Object> elements = new ArrayList<>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.dispose();

        //while raise an error if the order is wrong or if types doesnt match
        final Feature n1 = (Feature) elements.get(0);
        final Feature n2 = (Feature) elements.get(1);
        final Feature way = (Feature) elements.get(2);
        final Feature rel = (Feature) elements.get(3);


        //check first node
        Feature user = (Feature) n1.getPropertyValue("user");
        List<Feature> tags = (List) n1.getPropertyValue("tags");

        assertEquals(319408586l, n1.getPropertyValue("id"));
        assertEquals(440330, n1.getPropertyValue("changeset"));
        assertEquals(1, n1.getPropertyValue("version"));
        assertEquals(6871, user.getPropertyValue("uid"));
        assertEquals("smsm1", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2008-12-17T01:18:42Z").getTime(), n1.getPropertyValue("timestamp"));
        assertEquals(51.5074089d, ((Point)n1.getPropertyValue("point")).getCoordinate().y, DELTA);
        assertEquals(-0.1080108d,((Point)n1.getPropertyValue("point")).getCoordinate().x, DELTA);
        assertEquals(0, tags.size());

        //check second node
        user = (Feature) n2.getPropertyValue("user");
        tags = (List) n2.getPropertyValue("tags");

        assertEquals(275452090l, n2.getPropertyValue("id"));
        assertEquals(2980587, n2.getPropertyValue("changeset"));
        assertEquals(3, n2.getPropertyValue("version"));
        assertEquals(1697, user.getPropertyValue("uid"));
        assertEquals("nickb", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-10-29T12:14:35Z").getTime(), n2.getPropertyValue("timestamp"));
        assertEquals(51.5075933d, ((Point)n2.getPropertyValue("point")).getCoordinate().y, DELTA);
        assertEquals(-0.1076186d,((Point)n2.getPropertyValue("point")).getCoordinate().x, DELTA);
        assertEquals(2, tags.size());
        assertEquals("name",tags.get(0).getPropertyValue("k"));
        assertEquals("Jam's Sandwich Bar",tags.get(0).getPropertyValue("v"));
        assertEquals("amenity",tags.get(1).getPropertyValue("k"));
        assertEquals("cafe",tags.get(1).getPropertyValue("v"));

        //check the way
        user = (Feature) way.getPropertyValue("user");
        tags = (List) way.getPropertyValue("tags");
        List<Long> nodes = (List) way.getPropertyValue("nd");

        assertEquals(27776903l, way.getPropertyValue("id"));
        assertEquals(1368552, way.getPropertyValue("changeset"));
        assertEquals(3, way.getPropertyValue("version"));
        assertEquals(70, user.getPropertyValue("uid"));
        assertEquals("Matt", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), way.getPropertyValue("timestamp"));
        assertEquals(2, tags.size());
        assertEquals("access",tags.get(0).getPropertyValue("k"));
        assertEquals("private",tags.get(0).getPropertyValue("v"));
        assertEquals("highway",tags.get(1).getPropertyValue("k"));
        assertEquals("service",tags.get(1).getPropertyValue("v"));
        assertEquals(2, nodes.size());
        assertEquals(319408586, nodes.get(0).longValue());
        assertEquals(275452090, nodes.get(1).longValue());

        //check the relation
        user = (Feature) rel.getPropertyValue("user");
        tags = (List) rel.getPropertyValue("tags");
        List<Feature> members = (List) rel.getPropertyValue("members");

        assertEquals(33368911l, rel.getPropertyValue("id"));
        assertEquals(152, rel.getPropertyValue("changeset"));
        assertEquals(3, rel.getPropertyValue("version"));
        assertEquals(77, user.getPropertyValue("uid"));
        assertEquals("Georges", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), rel.getPropertyValue("timestamp"));
        assertEquals(1, tags.size());
        assertEquals("space",tags.get(0).getPropertyValue("k"));
        assertEquals("garden",tags.get(0).getPropertyValue("v"));
        assertEquals(2, members.size());
        assertEquals(27776903l, members.get(0).getPropertyValue("ref"));
        assertEquals("border", members.get(0).getPropertyValue("role"));
        assertEquals(MemberType.WAY, members.get(0).getPropertyValue("type"));
        assertEquals(319408586l, members.get(1).getPropertyValue("ref"));
        assertEquals("center", members.get(1).getPropertyValue("role"));
        assertEquals(MemberType.NODE, members.get(1).getPropertyValue("type"));

    }

    @Test
    public void testMoveTo() throws FileNotFoundException, XMLStreamException, IOException, ParseException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/sampleOSM.osm");
        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(testFile);

        //check that the bound is correctly read
        Envelope env = reader.getEnvelope();
        assertNotNull(env);
        assertTrue(Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), CommonCRS.WGS84.normalizedGeographic()));
        assertEquals(-0.108157396316528d, env.getMinimum(0),DELTA);
        assertEquals(-0.107599496841431d, env.getMaximum(0),DELTA);
        assertEquals(51.5073601795557d, env.getMinimum(1),DELTA);
        assertEquals(51.5076406454029d, env.getMaximum(1),DELTA);

        //move to the way node
        reader.moveTo(27776903l);

        final List<Object> elements = new ArrayList<>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.dispose();

        //while raise an error if the order is wrong or if types doesnt match
        final Feature way = (Feature) elements.get(0);
        final Feature rel = (Feature) elements.get(1);

        //check the way
        Feature user = (Feature) way.getPropertyValue("user");
        List<Feature> tags = (List) way.getPropertyValue("tags");
        List<Long> nodes = (List) way.getPropertyValue("nd");

        assertEquals(27776903l, way.getPropertyValue("id"));
        assertEquals(1368552, way.getPropertyValue("changeset"));
        assertEquals(3, way.getPropertyValue("version"));
        assertEquals(70, user.getPropertyValue("uid"));
        assertEquals("Matt", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), way.getPropertyValue("timestamp"));
        assertEquals(2, tags.size());
        assertEquals("access",tags.get(0).getPropertyValue("k"));
        assertEquals("private",tags.get(0).getPropertyValue("v"));
        assertEquals("highway",tags.get(1).getPropertyValue("k"));
        assertEquals("service",tags.get(1).getPropertyValue("v"));
        assertEquals(2, nodes.size());
        assertEquals(319408586, nodes.get(0).longValue());
        assertEquals(275452090, nodes.get(1).longValue());

        //check the relation
        user = (Feature) rel.getPropertyValue("user");
        tags = (List) rel.getPropertyValue("tags");
        List<Feature> members = (List) rel.getPropertyValue("members");

        assertEquals(33368911l, rel.getPropertyValue("id"));
        assertEquals(152, rel.getPropertyValue("changeset"));
        assertEquals(3, rel.getPropertyValue("version"));
        assertEquals(77, user.getPropertyValue("uid"));
        assertEquals("Georges", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), rel.getPropertyValue("timestamp"));
        assertEquals(1, tags.size());
        assertEquals("space",tags.get(0).getPropertyValue("k"));
        assertEquals("garden",tags.get(0).getPropertyValue("v"));
        assertEquals(2, members.size());
        assertEquals(27776903l, members.get(0).getPropertyValue("ref"));
        assertEquals("border", members.get(0).getPropertyValue("role"));
        assertEquals(MemberType.WAY, members.get(0).getPropertyValue("type"));
        assertEquals(319408586l, members.get(1).getPropertyValue("ref"));
        assertEquals("center", members.get(1).getPropertyValue("role"));
        assertEquals(MemberType.NODE, members.get(1).getPropertyValue("type"));

    }

    @Test
    public void testReadingDiff() throws FileNotFoundException, XMLStreamException, IOException, ParseException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/diffOSM.osm");
        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(testFile);

        //check that the bound is null
        Envelope env = reader.getEnvelope();
        assertNull(env);

        final List<Object> elements = new ArrayList<>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.dispose();

        //while raise an error if the order is wrong or if types doesnt match
        final Transaction create = (Transaction) elements.get(0);
        final Transaction modify = (Transaction) elements.get(1);
        final Transaction delete = (Transaction) elements.get(2);
        assertEquals("0.3", create.getVersion());
        assertEquals("0.3", modify.getVersion());
        assertEquals("0.3", delete.getVersion());
        assertEquals("puzzle-gis", create.getGenerator());
        assertEquals("puzzle-gis", modify.getGenerator());
        assertEquals("puzzle-gis", delete.getGenerator());

        assertEquals(TransactionType.CREATE, create.getType());
        assertEquals(TransactionType.MODIFY, modify.getType());
        assertEquals(TransactionType.DELETE, delete.getType());

        assertEquals(2, create.getElements().size());
        assertEquals(1, modify.getElements().size());
        assertEquals(1, delete.getElements().size());


        final Feature n1 = create.getElements().get(0);
        final Feature n2 = create.getElements().get(1);
        final Feature way = modify.getElements().get(0);
        final Feature rel = delete.getElements().get(0);


        //check first node
        Feature user = (Feature) n1.getPropertyValue("user");
        List<Feature> tags = (List) n1.getPropertyValue("tags");

        assertEquals(319408586l, n1.getPropertyValue("id"));
        assertEquals(440330, n1.getPropertyValue("changeset"));
        assertEquals(1, n1.getPropertyValue("version"));
        assertEquals(6871, user.getPropertyValue("uid"));
        assertEquals("smsm1", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2008-12-17T01:18:42Z").getTime(), n1.getPropertyValue("timestamp"));
        assertEquals(51.5074089d, ((Point)n1.getPropertyValue("point")).getCoordinate().y, DELTA);
        assertEquals(-0.1080108d,((Point)n1.getPropertyValue("point")).getCoordinate().x, DELTA);
        assertEquals(0, tags.size());

        //check second node
        user = (Feature) n2.getPropertyValue("user");
        tags = (List) n2.getPropertyValue("tags");

        assertEquals(275452090l, n2.getPropertyValue("id"));
        assertEquals(2980587, n2.getPropertyValue("changeset"));
        assertEquals(3, n2.getPropertyValue("version"));
        assertEquals(1697, user.getPropertyValue("uid"));
        assertEquals("nickb", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-10-29T12:14:35Z").getTime(), n2.getPropertyValue("timestamp"));
        assertEquals(51.5075933d, ((Point)n2.getPropertyValue("point")).getCoordinate().y, DELTA);
        assertEquals(-0.1076186d,((Point)n2.getPropertyValue("point")).getCoordinate().x, DELTA);
        assertEquals(2, tags.size());
        assertEquals("name",tags.get(0).getPropertyValue("k"));
        assertEquals("Jam's Sandwich Bar",tags.get(0).getPropertyValue("v"));
        assertEquals("amenity",tags.get(1).getPropertyValue("k"));
        assertEquals("cafe",tags.get(1).getPropertyValue("v"));

        //check the way
        user = (Feature) way.getPropertyValue("user");
        tags = (List) way.getPropertyValue("tags");
        List<Long> nodes = (List) way.getPropertyValue("nd");

        assertEquals(27776903l, way.getPropertyValue("id"));
        assertEquals(1368552, way.getPropertyValue("changeset"));
        assertEquals(3, way.getPropertyValue("version"));
        assertEquals(70, user.getPropertyValue("uid"));
        assertEquals("Matt", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), way.getPropertyValue("timestamp"));
        assertEquals(2,tags.size());
        assertEquals("access",tags.get(0).getPropertyValue("k"));
        assertEquals("private",tags.get(0).getPropertyValue("v"));
        assertEquals("highway",tags.get(1).getPropertyValue("k"));
        assertEquals("service",tags.get(1).getPropertyValue("v"));
        assertEquals(2, nodes.size());
        assertEquals(319408586, nodes.get(0).longValue());
        assertEquals(275452090, nodes.get(1).longValue());

        //check the relation
        user = (Feature) rel.getPropertyValue("user");
        tags = (List) rel.getPropertyValue("tags");
        List<Feature> members = (List) rel.getPropertyValue("members");

        assertEquals(33368911l, rel.getPropertyValue("id"));
        assertEquals(152, rel.getPropertyValue("changeset"));
        assertEquals(3, rel.getPropertyValue("version"));
        assertEquals(77, user.getPropertyValue("uid"));
        assertEquals("Georges", user.getPropertyValue("user"));
        assertEquals(TemporalUtilities.parseDate("2009-05-31T13:39:15Z").getTime(), rel.getPropertyValue("timestamp"));
        assertEquals(1, tags.size());
        assertEquals("space",tags.get(0).getPropertyValue("k"));
        assertEquals("garden",tags.get(0).getPropertyValue("v"));
        assertEquals(2, members.size());
        assertEquals(27776903l, members.get(0).getPropertyValue("ref"));
        assertEquals("border", members.get(0).getPropertyValue("role"));
        assertEquals(MemberType.WAY, members.get(0).getPropertyValue("type"));
        assertEquals(319408586l, members.get(1).getPropertyValue("ref"));
        assertEquals("center", members.get(1).getPropertyValue("role"));
        assertEquals(MemberType.NODE, members.get(1).getPropertyValue("type"));

    }

    @Test
    public void testReadingCapabilities() throws FileNotFoundException, XMLStreamException, IOException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/capabilities.osm");
        OSMXMLReader reader = new OSMXMLReader();
        reader.setInput(testFile);

        final List<Object> elements = new ArrayList<>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.dispose();

        assertEquals(1, elements.size());

        //while raise an error if type dont match
        final Api a = (Api) elements.get(0);

        assertEquals("0.3", a.getVersionMinimum());
        assertEquals("0.6", a.getVersionMaximum());
        assertEquals(0.25d, a.getAreaMaximum(),DELTA);
        assertEquals(5000, a.getTracePointsPerPage());
        assertEquals(2000, a.getWayNodeMaximum());
        assertEquals(50000, a.getChangesetMaximum());
        assertEquals(300, a.getTimeout());

    }

}
