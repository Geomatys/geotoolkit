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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;

import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
        final GPXWriter writer = new GPXWriter();
        writer.setOutput(f);

        final MetaData metaData = new MetaData("name", "description",
                new Person("Jean-Pierre", "jean-pierre@test.com", new URI("http://son-site.com")),
                new CopyRight("GNU", 2010, new URI("http://gnu.org")),
                Arrays.asList(new URI("http://adress1.org"),new URI("http://adress2.org")),
                new Date(), "test,sample", GPXModelConstants.create(-10, 20, -30, 40));

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
    public void testWritingPoints() throws Exception{

        final File f = new File("output.xml");
        if(f.exists())f.delete();
        final GPXWriter writer = new GPXWriter();
        writer.setOutput(f);

        Feature point1 = GPXModelConstants.createWayPoint(0, GF.createPoint(new Coordinate(-10, 10)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);
        Feature point2 = GPXModelConstants.createWayPoint(0, GF.createPoint(new Coordinate(-15, 15)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);
        Feature point3 = GPXModelConstants.createWayPoint(0, GF.createPoint(new Coordinate(-20, 20)),
                15.6, new Date(), 31.7, 45.1, "fds", "fdrt", "ffe", "aaz",
                Collections.singletonList(new URI("http://test.com")),
                "fdsg", "klj", "yy", 12, 45.2, 16.7, 14.3, 78.9, 6);

        FeatureCollection wayPoints = DataUtilities.collection("id", point1.getType());
        wayPoints.add(point1);
        wayPoints.add(point2);
        wayPoints.add(point3);


        writer.writeStartDocument();
        writer.write(null, wayPoints, null, null);
        writer.writeEndDocument();
        writer.dispose();

        final GPXReader reader = new GPXReader();
        reader.setInput(f);

        System.out.println(reader.next());

        assertEquals(point1, reader.next());
        assertEquals(point2, reader.next());
        assertEquals(point3, reader.next());
        assertFalse(reader.hasNext());
        
        reader.dispose();

        if(f.exists())f.delete();
    }

}