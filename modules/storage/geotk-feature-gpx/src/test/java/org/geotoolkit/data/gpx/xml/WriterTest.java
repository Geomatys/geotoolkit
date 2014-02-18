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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @moduel pending
 */
public class WriterTest {

    private static final GeometryFactory GF = new GeometryFactory();

    public WriterTest() {
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
    public void testWritingMetadata() throws Exception{
        final File f = new File("output.xml");
        if(f.exists())f.delete();
        final GPXWriter110 writer = new GPXWriter110("Geotoolkit.org");
        writer.setOutput(f);

        final MetaData metaData = new MetaData("name", "description",
                new Person("Jean-Pierre", "jean-pierre@test.com", new URI("http://son-site.com")),
                new CopyRight("GNU", 2010, new URI("http://gnu.org")),
                Arrays.asList(new URI("http://adress1.org"),new URI("http://adress2.org")),
                new Date(), "test,sample", GPXModelConstants.createEnvelope(-10, 20, -30, 40));

        writer.writeStartDocument();
        writer.write(metaData, null, null, null);
        writer.writeEndDocument();
        writer.dispose();

        final GPXReader reader = new GPXReader();
        reader.setInput(f);

        assertEquals(metaData, reader.getMetadata());
        reader.dispose();

        if(f.exists())f.delete();
    }

    @Test
    public void testWritingFeatures() throws Exception{

        final File f = new File("output.xml");
        if(f.exists())f.delete();
        final GPXWriter110 writer = new GPXWriter110("Geotoolkit.org");
        writer.setOutput(f);

        //way points -----------------------------------------------------------
        Feature point1 = GPXModelConstants.createWayPoint(0, GF.createPoint(new Coordinate(-10, 10)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);
        Feature point2 = GPXModelConstants.createWayPoint(1, GF.createPoint(new Coordinate(-15, 15)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);
        Feature point3 = GPXModelConstants.createWayPoint(2, GF.createPoint(new Coordinate(-20, 20)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);

        final List<Feature> wayPoints = new ArrayList<Feature>();
        wayPoints.add(point1);
        wayPoints.add(point2);
        wayPoints.add(point3);

        //routes ---------------------------------------------------------------
        final Feature route1 = GPXModelConstants.createRoute(0, "tt", "cc", "des", "src",
                Collections.singletonList(new URI("http://test.com")), 15, "test", wayPoints);
        final Feature route2 = GPXModelConstants.createRoute(1, "tt2", "cc2", "des2", "src2",
                Collections.singletonList(new URI("http://test2.com")), 15, "test2", wayPoints);

        final List<Feature> routes = new ArrayList<Feature>();
        routes.add(route1);
        routes.add(route2);

        //tracks ---------------------------------------------------------------
        final List<ComplexAttribute> segments = new ArrayList<ComplexAttribute>();
        segments.add(GPXModelConstants.createTrackSegment(0, wayPoints));
        segments.add(GPXModelConstants.createTrackSegment(1, wayPoints));
        segments.add(GPXModelConstants.createTrackSegment(2, wayPoints));

        final Feature track1 = GPXModelConstants.createTrack(0, "tc", "cc", "des", "src",
                Collections.singletonList(new URI("http://test4.com")), 15, "test", segments);
        final Feature track2 = GPXModelConstants.createTrack(1, "tc2", "cc2", "des2", "src2",
                Collections.singletonList(new URI("http://test5.com")), 15, "test2", segments);

        final List<Feature> tracks = new ArrayList<Feature>();
        tracks.add(track1);
        tracks.add(track2);


        writer.writeStartDocument();
        writer.write(null, wayPoints, routes, tracks);
        writer.writeEndDocument();
        writer.dispose();

        final GPXReader reader = new GPXReader();
        reader.setInput(f);

        //testing on toString since JTS geometry always fail on equals method.
        assertEquals(point1.toString(), reader.next().toString());
        assertEquals(point2.toString(), reader.next().toString());
        assertEquals(point3.toString(), reader.next().toString());
        assertEquals(route1.toString(), reader.next().toString());
        assertEquals(route2.toString(), reader.next().toString());
        assertEquals(track1.toString(), reader.next().toString());
        assertEquals(track2.toString(), reader.next().toString());
        assertFalse(reader.hasNext());
        
        reader.dispose();

        if(f.exists())f.delete();
    }


}
