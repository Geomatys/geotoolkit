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

package org.geotoolkit.data.gpx.xml;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.temporal.object.TemporalUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ReaderTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final double DELTA = 0.000001;
    public ReaderTest() {
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

    /**
     * Test metadata tag parsing.
     */
    @Test
    public void testMetadataRead() throws Exception{

        final GPXReader reader = new GPXReader();
        reader.setInput(ReaderTest.class.getResource(
                "/org/geotoolkit/gpx/sample_metadata.xml"));

        final MetaData data = reader.getMetadata();

        assertEquals("sample", data.getName());
        assertEquals("sample gpx test file", data.getDescription());
        assertEquals(TemporalUtilities.parseDate("2010-03-01"), data.getTime());
        assertEquals("sample,metadata", data.getKeywords());
        assertEquals(GPXModelConstants.createEnvelope(-20, 30, 10, 40), data.getBounds());

        assertEquals("Jean-Pierre", data.getPerson().getName());
        assertEquals("jean.pierre@test.com", data.getPerson().getEmail());
        assertEquals("http://someone-site.org", data.getPerson().getLink().toString());

        assertEquals("gnu", data.getCopyRight().getAuthor());
        assertEquals(2010, data.getCopyRight().getYear().intValue());
        assertEquals("http://www.gnu.org/licenses/lgpl-3.0-standalone.html", data.getCopyRight().getLicense().toString());

        assertEquals(3, data.getLinks().size());
        assertEquals("http://first-adress.org", data.getLinks().get(0).toString());
        assertEquals("http://second-adress.org", data.getLinks().get(1).toString());
        assertEquals("http://third-adress.org", data.getLinks().get(2).toString());
    }

    /**
     * Test way point tag parsing.
     */
    @Test
    public void testWayPointRead() throws Exception{

        final GPXReader reader = new GPXReader();
        reader.setInput(ReaderTest.class.getResource(
                "/org/geotoolkit/gpx/sample_waypoint.xml"));

        final MetaData data = reader.getMetadata();

        assertNull(data.getName());
        assertNull(data.getDescription());
        assertNull(data.getTime());
        assertNull(data.getKeywords());
        assertEquals(GPXModelConstants.createEnvelope(-20, 30, 10, 40), data.getBounds());
        assertNull(data.getPerson());
        assertNull(data.getCopyRight());
        assertEquals(0, data.getLinks().size());

        Feature f = reader.next();
        checkPoint(f, 0);
        f = reader.next();
        checkPoint(f, 1);
        f = reader.next();
        checkPoint(f, 2);
        assertFalse(reader.hasNext());

    }

    /**
     * Test route tag parsing.
     */
    @Test
    public void testRouteRead() throws Exception{

        final GPXReader reader = new GPXReader();
        reader.setInput(ReaderTest.class.getResource(
                "/org/geotoolkit/gpx/sample_route.xml"));

        final MetaData data = reader.getMetadata();

        assertNull(data.getName());
        assertNull(data.getDescription());
        assertNull(data.getTime());
        assertNull(data.getKeywords());
        assertEquals(GPXModelConstants.createEnvelope(-20, 30, 10, 40), data.getBounds());
        assertNull(data.getPerson());
        assertNull(data.getCopyRight());
        assertEquals(0, data.getLinks().size());

        Feature f = reader.next();
        assertEquals("route name",          f.getProperty("name").getValue());
        assertEquals("route comment",       f.getProperty("cmt").getValue());
        assertEquals("route description",   f.getProperty("desc").getValue());
        assertEquals("route source",        f.getProperty("src").getValue());
        assertEquals("route type",          f.getProperty("type").getValue());
        assertEquals(7,                     f.getProperty("number").getValue());

        List<Property> links = new ArrayList<Property>(f.getProperties("link"));
        assertEquals(3,links.size());
        assertEquals("http://route-adress1.org", links.get(0).getValue().toString());
        assertEquals("http://route-adress2.org", links.get(1).getValue().toString());
        assertEquals("http://route-adress3.org", links.get(2).getValue().toString());

        List<Property> points = new ArrayList<Property>(f.getProperties("rtept"));
        assertEquals(3,points.size());
        checkPoint((Feature) points.get(0).getValue(), 0);
        checkPoint((Feature) points.get(1).getValue(), 1);
        checkPoint((Feature) points.get(2).getValue(), 2);

        f = reader.next();
        assertEquals(null,                  f.getProperty("name"));
        assertEquals(null,                  f.getProperty("cmt"));
        assertEquals(null,                  f.getProperty("desc"));
        assertEquals(null,                  f.getProperty("src"));
        assertEquals(null,                  f.getProperty("type"));
        assertEquals(null,                  f.getProperty("number"));

        links = new ArrayList<Property>(f.getProperties("link"));
        assertEquals(0,links.size());

        points = new ArrayList<Property>(f.getProperties("rtept"));
        assertEquals(0,points.size());

        assertFalse(reader.hasNext());
    }

    /**
     * Test track tag parsing.
     */
    @Test
    public void testTrackRead() throws Exception{

        final GPXReader reader = new GPXReader();
        reader.setInput(ReaderTest.class.getResource(
                "/org/geotoolkit/gpx/sample_track.xml"));

        final MetaData data = reader.getMetadata();

        assertNull(data.getName());
        assertNull(data.getDescription());
        assertNull(data.getTime());
        assertNull(data.getKeywords());
        assertEquals(GPXModelConstants.createEnvelope(-20, 30, 10, 40), data.getBounds());
        assertNull(data.getPerson());
        assertNull(data.getCopyRight());
        assertEquals(0, data.getLinks().size());

        Feature f = reader.next();
        assertEquals("track name",          f.getProperty("name").getValue());
        assertEquals("track comment",       f.getProperty("cmt").getValue());
        assertEquals("track description",   f.getProperty("desc").getValue());
        assertEquals("track source",        f.getProperty("src").getValue());
        assertEquals("track type",          f.getProperty("type").getValue());
        assertEquals(7,                     f.getProperty("number").getValue());

        List<Property> links = new ArrayList<Property>(f.getProperties("link"));
        assertEquals(3,links.size());
        assertEquals("http://track-adress1.org", links.get(0).getValue().toString());
        assertEquals("http://track-adress2.org", links.get(1).getValue().toString());
        assertEquals("http://track-adress3.org", links.get(2).getValue().toString());

        List<Property> segments = new ArrayList<Property>(f.getProperties("trkseg"));
        assertEquals(2,segments.size());
        ComplexAttribute seg1 = (ComplexAttribute) segments.get(0).getValue();
        ComplexAttribute seg2 = (ComplexAttribute) segments.get(1).getValue();
        List<Property> points = new ArrayList<Property>(seg1.getProperties("trkpt"));
        assertEquals(3, points.size());
        checkPoint((Feature) points.get(0).getValue(), 0);
        checkPoint((Feature) points.get(1).getValue(), 1);
        checkPoint((Feature) points.get(2).getValue(), 2);
        points = new ArrayList<Property>(seg2.getProperties("trkpt"));
        assertEquals(0, points.size());

        f = reader.next();
        assertEquals(null,                  f.getProperty("name"));
        assertEquals(null,                  f.getProperty("cmt"));
        assertEquals(null,                  f.getProperty("desc"));
        assertEquals(null,                  f.getProperty("src"));
        assertEquals(null,                  f.getProperty("type"));
        assertEquals(null,                  f.getProperty("number"));

        links = new ArrayList<Property>(f.getProperties("link"));
        assertEquals(0,links.size());

        segments = new ArrayList<Property>(f.getProperties("trkseg"));
        assertEquals(0,segments.size());

        assertFalse(reader.hasNext());
    }

    private void checkPoint(Feature f, int num) throws Exception{
        if(num == 0){
            assertEquals(0,                     f.getProperty("index").getValue());
            assertEquals(15.0,                  ((Point)f.getProperty("geometry").getValue()).getX(), DELTA);
            assertEquals(10.0,                  ((Point)f.getProperty("geometry").getValue()).getY(), DELTA);
            assertEquals(140.0,                 f.getProperty("ele").getValue());
            assertEquals(TemporalUtilities.parseDate("2010-01-10"),f.getProperty("time").getValue());
            assertEquals(35.0,                  f.getProperty("magvar").getValue());
            assertEquals(112.32,                f.getProperty("geoidheight").getValue());
            assertEquals("first point",         f.getProperty("name").getValue());
            assertEquals("first comment",       f.getProperty("cmt").getValue());
            assertEquals("first description",   f.getProperty("desc").getValue());
            assertEquals("first source",        f.getProperty("src").getValue());
            assertEquals("first sym",           f.getProperty("sym").getValue());
            assertEquals("first type",          f.getProperty("type").getValue());
            assertEquals("first fix",           f.getProperty("fix").getValue());
            assertEquals(11,                    f.getProperty("sat").getValue());
            assertEquals(15.15,                 f.getProperty("hdop").getValue());
            assertEquals(14.14,                 f.getProperty("vdop").getValue());
            assertEquals(13.13,                 f.getProperty("pdop").getValue());
            assertEquals(55.55,                 f.getProperty("ageofdgpsdata").getValue());
            assertEquals(256,                   f.getProperty("dgpsid").getValue());

            final List<Property> links = new ArrayList<Property>(f.getProperties("link"));
            assertEquals(3,links.size());
            assertEquals("http://first-adress1.org", links.get(0).getValue().toString());
            assertEquals("http://first-adress2.org", links.get(1).getValue().toString());
            assertEquals("http://first-adress3.org", links.get(2).getValue().toString());
        }else if(num == 1){
            assertEquals(1,                     f.getProperty("index").getValue());
            assertEquals(25.0,                  ((Point)f.getProperty("geometry").getValue()).getX(), DELTA);
            assertEquals(20.0,                  ((Point)f.getProperty("geometry").getValue()).getY(), DELTA);
            assertEquals(null,                  f.getProperty("ele"));
            assertEquals(null,                  f.getProperty("time"));
            assertEquals(null,                  f.getProperty("magvar"));
            assertEquals(null,                  f.getProperty("geoidheight"));
            assertEquals(null,                  f.getProperty("name"));
            assertEquals(null,                  f.getProperty("cmt"));
            assertEquals(null,                  f.getProperty("desc"));
            assertEquals(null,                  f.getProperty("src"));
            assertEquals(null,                  f.getProperty("sym"));
            assertEquals(null,                  f.getProperty("type"));
            assertEquals(null,                  f.getProperty("fix"));
            assertEquals(null,                  f.getProperty("sat"));
            assertEquals(null,                  f.getProperty("hdop"));
            assertEquals(null,                  f.getProperty("vdop"));
            assertEquals(null,                  f.getProperty("pdop"));
            assertEquals(null,                  f.getProperty("ageofdgpsdata"));
            assertEquals(null,                  f.getProperty("dgpsid"));

            final List<Property> links = new ArrayList<Property>(f.getProperties("link"));
            assertEquals(0,links.size());
        }else if(num == 2){
            assertEquals(2,                     f.getProperty("index").getValue());
            assertEquals(35.0,                  ((Point)f.getProperty("geometry").getValue()).getX(), DELTA);
            assertEquals(30.0,                  ((Point)f.getProperty("geometry").getValue()).getY(), DELTA);
            assertEquals(150.0,                 f.getProperty("ele").getValue());
            assertEquals(TemporalUtilities.parseDate("2010-01-30"),f.getProperty("time").getValue());
            assertEquals(25.0,                  f.getProperty("magvar").getValue());
            assertEquals(142.32,                f.getProperty("geoidheight").getValue());
            assertEquals("third point",         f.getProperty("name").getValue());
            assertEquals("third comment",       f.getProperty("cmt").getValue());
            assertEquals("third description",   f.getProperty("desc").getValue());
            assertEquals("third source",        f.getProperty("src").getValue());
            assertEquals("third sym",           f.getProperty("sym").getValue());
            assertEquals("third type",          f.getProperty("type").getValue());
            assertEquals("third fix",           f.getProperty("fix").getValue());
            assertEquals(35,                    f.getProperty("sat").getValue());
            assertEquals(35.15,                 f.getProperty("hdop").getValue());
            assertEquals(34.14,                 f.getProperty("vdop").getValue());
            assertEquals(33.13,                 f.getProperty("pdop").getValue());
            assertEquals(85.55,                 f.getProperty("ageofdgpsdata").getValue());
            assertEquals(456,                   f.getProperty("dgpsid").getValue());

            final List<Property> links = new ArrayList<Property>(f.getProperties("link"));
            assertEquals(2,links.size());
            assertEquals("http://third-adress1.org", links.get(0).getValue().toString());
            assertEquals("http://third-adress2.org", links.get(1).getValue().toString());
        }else{
            fail("unexpected point number :" + num);
        }
    }

}