/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
 *    
 *    Created on August 16, 2003, 5:10 PM
 */
package org.geotoolkit.data;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.DefaultName;

import org.opengis.filter.Filter;

/**
 *
 * @author jamesm
 * @module pending
 */
public class DefaultQueryTest extends TestCase {

    public DefaultQueryTest(java.lang.String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DefaultQueryTest.class);
        return suite;
    }

    public void testFullConstructor() {
        Query query = new QueryBuilder()
                .setTypeName(new DefaultName("mytype"))
                .setFilter(Filter.INCLUDE)
                .setMaxFeatures(10)
                .setProperties(new String[]{"foo"})
                .setHandle("myquery")
                .buildQuery();
        assertNotNull(query);
    }

    /** Test of getPropertyNames method, of class org.geotoolkit.data.DefaultQuery. */
    public void testPropertyNames() {
        System.out.println("testPropertyNames");
        Query query = new QueryBuilder().setTypeName(new DefaultName("test")).buildQuery();
        assertNull(query.getPropertyNames());
        query = new QueryBuilder().copy(query).setProperties(new String[]{"foo","bar"}).buildQuery();
        String names[] = query.getPropertyNames();
        assertNotNull(names);
        assertEquals("foo", names[0]);
        assertEquals("bar", names[1]);
        
        query = new QueryBuilder().copy(query).setProperties(null).buildQuery();
        assertNull(query.getPropertyNames());

        query = new QueryBuilder()
                .setTypeName(new DefaultName("Test"))
                .setFilter(Filter.INCLUDE)
                .setProperties(new String[]{"foo","wibble"})
                .buildQuery();
        assertNotNull(query.getPropertyNames());
    }

    /** Test of retrieveAllProperties method, of class org.geotoolkit.data.DefaultQuery. */
    public void testRetrieveAllProperties() {
        System.out.println("testRetrieveAllProperties");

        Query query = new QueryBuilder().setTypeName(new DefaultName("test")).buildQuery();
        assertTrue(query.retrieveAllProperties());

        query = new QueryBuilder().copy(query).setProperties(new String[]{"foo", "bar"}).buildQuery();
        assertFalse(query.retrieveAllProperties());

        query = new QueryBuilder().copy(query).setProperties(null).buildQuery();
        assertTrue(query.retrieveAllProperties());

        query = new QueryBuilder().copy(query).setProperties(new String[]{"foo", "bar"}).buildQuery();
        query = new QueryBuilder().copy(query).setProperties(null).buildQuery();
        assertTrue(query.retrieveAllProperties());
    }

    /** Test of getMaxFeatures method, of class org.geotoolkit.data.DefaultQuery. */
    public void testMaxFeatures() {
        System.out.println("testMaxFeatures");
        Query query = new QueryBuilder().setTypeName(new DefaultName("test")).buildQuery();
        assertEquals(null, query.getMaxFeatures());

        query = new QueryBuilder().copy(query).setMaxFeatures(5).buildQuery();
        assertEquals(new Integer(5), query.getMaxFeatures());
    }

    /** Test of getFilter method, of class org.geotoolkit.data.DefaultQuery. */
    public void testFilter() {
        System.out.println("testGetFilter");
        Query query = new QueryBuilder()
                .setTypeName(new DefaultName("test"))
                .setFilter(Filter.EXCLUDE)
                .buildQuery();
        assertEquals(Filter.EXCLUDE, query.getFilter());

        query = new QueryBuilder()
                .setTypeName(new DefaultName("test"))
                .setFilter(Filter.INCLUDE)
                .buildQuery();
        assertEquals(Filter.INCLUDE, query.getFilter());
    }

    /** Test of getTypeName method, of class org.geotoolkit.data.DefaultQuery. */
    public void testTypeName() {
        Query query = new QueryBuilder()
                .setTypeName(new DefaultName("foobar"))
                .buildQuery();

        assertEquals("foobar", query.getTypeName().getLocalPart());

        query = new QueryBuilder()
                .setTypeName(new DefaultName("mytype"))
                .buildQuery();
        assertEquals("mytype", query.getTypeName().getLocalPart());
    }

    /** Test of getHandle method, of class org.geotoolkit.data.DefaultQuery. */
    public void testHandle() {
        System.out.println("testGetHandle");
        Query query = new QueryBuilder()
                .setTypeName(new DefaultName("test"))
                .buildQuery();
        assertNull(query.getHandle());

        query = new QueryBuilder()
                .copy(query)
                .setHandle("myquery")
                .buildQuery();
        assertEquals("myquery", query.getHandle());
    }


    /** Test of toString method, of class org.geotoolkit.data.DefaultQuery. */
    public void testToString() {
        System.out.println("testToString");
        Query query = new QueryBuilder()
                .setTypeName(new DefaultName("test"))
                .buildQuery();
        assertNotNull(query.toString());

        query = new QueryBuilder()
                .copy(query)
                .setHandle("myquery")
                .buildQuery();
        assertNotNull(query.toString());

        query = new QueryBuilder()
                .copy(query)
                .setFilter(Filter.EXCLUDE)
                .buildQuery();
        assertNotNull(query.toString());

        query = new QueryBuilder()
                .copy(query)
                .setProperties(new String[]{"foo", "bar"})
                .buildQuery();
        assertNotNull(query.toString());

    }
}
