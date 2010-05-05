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

import org.geotoolkit.data.gpx.model.Bound;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.temporal.object.TemporalUtilities;
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
public class ReaderTest {

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
    public void testWayPointRead() throws Exception{

        final GPXReader reader = new GPXReader();
        reader.setInput(ReaderTest.class.getResource(
                "/org/geotoolkit/gpx/sample_metadata.xml"));

        final MetaData data = reader.getMetadata();

        assertEquals("sample", data.getName());
        assertEquals("sample gpx test file", data.getDescription());
        assertEquals(TemporalUtilities.parseDate("2010-03-01"), data.getTime());
        assertEquals("sample,metadata", data.getKeywords());
        assertEquals(Bound.create(-20, 30, 10, 40), data.getBounds());

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

}