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

package org.geotoolkit.feature;

import javax.xml.namespace.QName;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultNameTest {

    public DefaultNameTest() {
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
     * Test of isGlobal method, of class DefaultName.
     */
    @Test
    public void testParameter() {
        final String uri = "http://test.com";
        final String separator = "!";
        final String local = "localpart";
        DefaultName name;

        name = new DefaultName(new QName(uri, local));
        assertEquals(name.getNamespaceURI(), uri);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), false);

        name = new DefaultName(local);
        assertEquals(name.getNamespaceURI(), null);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), true);

        name = new DefaultName(uri,local);
        assertEquals(name.getNamespaceURI(), uri);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), false);

        name = new DefaultName(uri,separator, local);
        assertEquals(name.getNamespaceURI(), uri);
        assertEquals(name.getSeparator(), separator);
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), false);
    }

    /**
     * test parsing different forms
     */
    @Test
    public void testValueOf() {
        final String uri = "http://test.com";
        final String local = "localpart";
        Name name;

        name = DefaultName.valueOf("{"+uri+"}"+local);
        assertEquals(name.getNamespaceURI(), uri);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), false);

        name = DefaultName.valueOf(uri+":"+local);
        assertEquals(name.getNamespaceURI(), uri);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), false);

        name = DefaultName.valueOf(local);
        assertEquals(name.getNamespaceURI(), null);
        assertEquals(name.getSeparator(), ":");
        assertEquals(name.getLocalPart(), local);
        assertEquals(name.isGlobal(), true);


    }


}