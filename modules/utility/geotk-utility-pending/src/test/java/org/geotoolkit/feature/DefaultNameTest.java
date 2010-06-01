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
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.type.Name;

/**
 * Test Name.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultNameTest {

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

    @Test
    public void testEquals(){
        Name n1 = new DefaultName("http://test.com", "test");
        Name n2 = new DefaultName("http://test.com", "test");
        assertEquals(n1, n2);

        n1 = new DefaultName("http://test.com", ":", "test1");
        n2 = new DefaultName("http://test.com", ":", "test2");
        assertFalse( n1.equals(n2) );

        n1 = new DefaultName("http://test.com1", ":", "test");
        n2 = new DefaultName("http://test.com2", ":", "test");
        assertFalse( n1.equals(n2) );

        //separator must not be used for equals
        n1 = new DefaultName("http://test.com", ":", "test");
        n2 = new DefaultName("http://test.com", "/", "test");
        assertEquals(n1, n2);
    }

}