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

package org.geotoolkit.feature.type;

import org.opengis.util.GenericName;
import javax.xml.namespace.QName;
import org.junit.Test;
import static org.geotoolkit.test.Assert.*;

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
        final String local = "localpart";
        GenericName name;

        name = NamesExt.create(new QName(uri, local));
        assertEquals(NamesExt.getNamespace(name), uri);
        assertEquals(name.tip().toString(), local);

        name = NamesExt.create(local);
        assertEquals(NamesExt.getNamespace(name), null);
        assertEquals(name.tip().toString(), local);

        name = NamesExt.create(uri, local);
        assertEquals(NamesExt.getNamespace(name), uri);
        assertEquals(name.tip().toString(), local);

        name = NamesExt.create(uri, local);
        assertEquals(NamesExt.getNamespace(name), uri);
        assertEquals(name.tip().toString(), local);
        
    }

    /**
     * test parsing different forms
     */
    @Test
    public void testValueOf() {
        final String uri = "http://test.com";
        final String local = "localpart";
        GenericName name;

        name = NamesExt.valueOf("{"+uri+"}"+local);
        assertEquals(NamesExt.getNamespace(name), uri);
        assertEquals(name.tip().toString(), local);

        name = NamesExt.valueOf(uri+":"+local);
        assertEquals(NamesExt.getNamespace(name), uri);
        assertEquals(name.tip().toString(), local);

        name = NamesExt.valueOf(local);
        assertEquals(NamesExt.getNamespace(name), null);
        assertEquals(name.tip().toString(), local);

    }

    @Test
    public void testEquals(){
        GenericName n1 = NamesExt.create("http://test.com", "test");
        GenericName n2 = NamesExt.create("http://test.com", "test");
        assertEquals(n1, n2);

        n1 = NamesExt.create("http://test.com", "test1");
        n2 = NamesExt.create("http://test.com", "test2");
        assertFalse( n1.equals(n2) );

        n1 = NamesExt.create("http://test.com1", "test");
        n2 = NamesExt.create("http://test.com2", "test");
        assertFalse( n1.equals(n2) );

    }

    @Test
    public void testSerialize(){
        GenericName name = NamesExt.valueOf("{geotk}test");
        assertSerializedEquals(name);
    }

}
