/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.client.util;

import org.geotoolkit.geometry.GeneralEnvelope;

import org.opengis.geometry.Envelope;

// Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Legal Guilhem (Geomatys)
 */
public class RequestsUtilitiesTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

     /**
     * @throws java.lang.Exception
     */
    @Test
    public void toBboxValueListTest() throws Exception {
        double[] min = {20.0, 35.0};
        double[] max = {110.0, 80.0};
        Envelope envelope = new GeneralEnvelope(min, max);

        String result    = RequestsUtilities.toBboxValue(envelope);
        String expResult = "20.0,35.0,110.0,80.0";
        assertEquals(expResult, result);
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void toBooleanTest() throws Exception {

        assertFalse(RequestsUtilities.toBoolean(null));
        assertFalse(RequestsUtilities.toBoolean("whatever"));
        assertFalse(RequestsUtilities.toBoolean("FALSE"));
        assertFalse(RequestsUtilities.toBoolean("false"));

        assertTrue(RequestsUtilities.toBoolean("true"));
        assertTrue(RequestsUtilities.toBoolean("TRUE"));
    }
}
