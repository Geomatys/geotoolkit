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

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @moduel pending
 */
public class WriterTest {

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

        if(f.exists())f.delete();
    }

}