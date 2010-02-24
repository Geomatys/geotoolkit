package org.geotoolkit.data.osm.xml;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import static org.junit.Assert.*;

/**
 *
 * @author sorel
 */
public class OSMXMLReaderTest {

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
    public void testReading() throws FileNotFoundException, XMLStreamException, IOException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/sampleOSM.osm");
        OSMXMLReader reader = new OSMXMLReader(testFile);

        //check that the bound is correctly read
        Envelope env = reader.getEnvelope();
        assertNotNull(env);
        assertTrue(CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84));
        assertEquals(-0.108157396316528d, env.getMinimum(0),DELTA);
        assertEquals(-0.107599496841431d, env.getMaximum(0),DELTA);
        assertEquals(51.5073601795557d, env.getMinimum(1),DELTA);
        assertEquals(51.5076406454029d, env.getMaximum(1),DELTA);

        final List<IdentifiedElement> elements = new ArrayList<IdentifiedElement>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.close();

        //while raise an error if the order is wrong or if types doesnt match
        final Node n1 = (Node) elements.get(0);
        final Node n2 = (Node) elements.get(1);
        final Way way = (Way) elements.get(2);
        final Relation rel = (Relation) elements.get(3);


        //check first node
        assertEquals(319408586, n1.getId());
        assertEquals(440330, n1.getChangeset());
        assertEquals(1, n1.getVersion());
        assertEquals(6871, n1.getUser().getId());
        assertEquals("smsm1", n1.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2008-12-17T01:18:42Z").getTime(), n1.getTimestamp());
        assertEquals(51.5074089d, n1.getLatitude(), DELTA);
        assertEquals(-0.1080108d,n1.getLongitude(), DELTA);
        assertEquals(0, n1.getTags().size());

        //check second node
        assertEquals(275452090, n2.getId());
        assertEquals(2980587, n2.getChangeset());
        assertEquals(3, n2.getVersion());
        assertEquals(1697, n2.getUser().getId());
        assertEquals("nickb", n2.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2009-10-29T12:14:35Z").getTime(), n2.getTimestamp());
        assertEquals(51.5075933d, n2.getLatitude(), DELTA);
        assertEquals(-0.1076186d,n2.getLongitude(), DELTA);
        assertEquals(2, n2.getTags().size());
        assertEquals("Jam's Sandwich Bar", n2.getTags().get("name"));
        assertEquals("cafe", n2.getTags().get("amenity"));

        //check the way
        assertEquals(27776903, way.getId());
        assertEquals(1368552, way.getChangeset());
        assertEquals(3, way.getVersion());
        assertEquals(70, way.getUser().getId());
        assertEquals("Matt", way.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2009-05-31T13:39:15Z").getTime(), way.getTimestamp());
        assertEquals(2, way.getTags().size());
        assertEquals("private", way.getTags().get("access"));
        assertEquals("service", way.getTags().get("highway"));
        assertEquals(2, way.getNodesIds().size());
        assertEquals(319408586, way.getNodesIds().get(0).longValue());
        assertEquals(275452090, way.getNodesIds().get(1).longValue());

        //check the relation
        assertEquals(33368911, rel.getId());
        assertEquals(152, rel.getChangeset());
        assertEquals(3, rel.getVersion());
        assertEquals(77, rel.getUser().getId());
        assertEquals("Georges", rel.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2009-05-31T13:39:15Z").getTime(), rel.getTimestamp());
        assertEquals(1, rel.getTags().size());
        assertEquals("garden", rel.getTags().get("space"));
        assertEquals(2, rel.getMembers().size());
        assertEquals(27776903, rel.getMembers().get(0).getReference());
        assertEquals("border", rel.getMembers().get(0).getRole());
        assertEquals(MemberType.WAY, rel.getMembers().get(0).getType());
        assertEquals(319408586, rel.getMembers().get(1).getReference());
        assertEquals("center", rel.getMembers().get(1).getRole());
        assertEquals(MemberType.NODE, rel.getMembers().get(1).getType());

    }

    @Test
    public void testMoveTo() throws FileNotFoundException, XMLStreamException, IOException {
        File testFile = new File("src/test/resources/org/geotoolkit/test-data/osm/sampleOSM.osm");
        OSMXMLReader reader = new OSMXMLReader(testFile);

        //check that the bound is correctly read
        Envelope env = reader.getEnvelope();
        assertNotNull(env);
        assertTrue(CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84));
        assertEquals(-0.108157396316528d, env.getMinimum(0),DELTA);
        assertEquals(-0.107599496841431d, env.getMaximum(0),DELTA);
        assertEquals(51.5073601795557d, env.getMinimum(1),DELTA);
        assertEquals(51.5076406454029d, env.getMaximum(1),DELTA);

        //move to the way node
        reader.moveTo(27776903l);

        final List<IdentifiedElement> elements = new ArrayList<IdentifiedElement>();
        while(reader.hasNext()){
            elements.add(reader.next());
        }
        reader.close();

        //while raise an error if the order is wrong or if types doesnt match
        final Way way = (Way) elements.get(0);
        final Relation rel = (Relation) elements.get(1);

        //check the way
        assertEquals(27776903, way.getId());
        assertEquals(1368552, way.getChangeset());
        assertEquals(3, way.getVersion());
        assertEquals(70, way.getUser().getId());
        assertEquals("Matt", way.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2009-05-31T13:39:15Z").getTime(), way.getTimestamp());
        assertEquals(2, way.getTags().size());
        assertEquals("private", way.getTags().get("access"));
        assertEquals("service", way.getTags().get("highway"));
        assertEquals(2, way.getNodesIds().size());
        assertEquals(319408586, way.getNodesIds().get(0).longValue());
        assertEquals(275452090, way.getNodesIds().get(1).longValue());

        //check the relation
        assertEquals(33368911, rel.getId());
        assertEquals(152, rel.getChangeset());
        assertEquals(3, rel.getVersion());
        assertEquals(77, rel.getUser().getId());
        assertEquals("Georges", rel.getUser().getName());
        assertEquals(TemporalUtilities.createDate("2009-05-31T13:39:15Z").getTime(), rel.getTimestamp());
        assertEquals(1, rel.getTags().size());
        assertEquals("garden", rel.getTags().get("space"));
        assertEquals(2, rel.getMembers().size());
        assertEquals(27776903, rel.getMembers().get(0).getReference());
        assertEquals("border", rel.getMembers().get(0).getRole());
        assertEquals(MemberType.WAY, rel.getMembers().get(0).getType());
        assertEquals(319408586, rel.getMembers().get(1).getReference());
        assertEquals("center", rel.getMembers().get(1).getRole());
        assertEquals(MemberType.NODE, rel.getMembers().get(1).getType());

    }
    
}
