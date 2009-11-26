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
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(new DefaultName("mytype"));
        builder.setFilter(Filter.INCLUDE);
        builder.setMaxFeatures(10);
        builder.setProperties(new String[]{"foo"});
        builder.setHandle("myquery");
        Query query = builder.buildQuery();

        assertNotNull(query);
    }

    /** Test of getPropertyNames method, of class org.geotoolkit.data.DefaultQuery. */
    public void testPropertyNames() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testPropertyNames");
        builder.setTypeName(new DefaultName("test"));
        Query query = builder.buildQuery();
        assertNull(query.getPropertyNames());

        builder.reset();
        builder.copy(query);
        builder.setProperties(new String[]{"foo","bar"});
        query = builder.buildQuery();
        String names[] = query.getPropertyNames();
        assertNotNull(names);
        assertEquals("foo", names[0]);
        assertEquals("bar", names[1]);

        builder.reset();
        builder.copy(query);
        builder.setProperties(null);
        query = builder.buildQuery();
        assertNull(query.getPropertyNames());

        builder.reset();
        builder.setTypeName(new DefaultName("Test"));
        builder.setFilter(Filter.INCLUDE);
        builder.setProperties(new String[]{"foo","wibble"});
        query = builder.buildQuery();
        assertNotNull(query.getPropertyNames());
    }

    /** Test of retrieveAllProperties method, of class org.geotoolkit.data.DefaultQuery. */
    public void testRetrieveAllProperties() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testRetrieveAllProperties");

        builder.setTypeName(new DefaultName("test"));
        Query query = builder.buildQuery();
        assertTrue(query.retrieveAllProperties());

        builder.setProperties(new String[]{"foo", "bar"});
        query = builder.buildQuery();
        assertFalse(query.retrieveAllProperties());

        builder.setProperties(null);
        query = builder.buildQuery();
        assertTrue(query.retrieveAllProperties());

    }

    /** Test of getMaxFeatures method, of class org.geotoolkit.data.DefaultQuery. */
    public void testMaxFeatures() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testMaxFeatures");
        builder.setTypeName(new DefaultName("test"));
        Query query = builder.buildQuery();
        assertEquals(null, query.getMaxFeatures());

        builder.setMaxFeatures(5);
        query = builder.buildQuery();
        assertEquals(new Integer(5), query.getMaxFeatures());
    }

    /** Test of getFilter method, of class org.geotoolkit.data.DefaultQuery. */
    public void testFilter() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testGetFilter");
        builder.setTypeName(new DefaultName("test"));
        builder.setFilter(Filter.EXCLUDE);
        Query query = builder.buildQuery();
        assertEquals(Filter.EXCLUDE, query.getFilter());

        builder.setFilter(Filter.INCLUDE);
        query = builder.buildQuery();
        assertEquals(Filter.INCLUDE, query.getFilter());
    }

    /** Test of getTypeName method, of class org.geotoolkit.data.DefaultQuery. */
    public void testTypeName() {
        final QueryBuilder builder = new QueryBuilder();

        builder.setTypeName(new DefaultName("foobar"));

        Query query = builder.buildQuery();
        assertEquals("foobar", query.getTypeName().getLocalPart());

        builder.setTypeName(new DefaultName("mytype"));
        query = builder.buildQuery();
        assertEquals("mytype", query.getTypeName().getLocalPart());
    }

    /** Test of getHandle method, of class org.geotoolkit.data.DefaultQuery. */
    public void testHandle() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testGetHandle");
        builder.setTypeName(new DefaultName("test"));
        Query query = builder.buildQuery();
        assertNull(query.getHandle());

        builder.setHandle("myquery");
        query = builder.buildQuery();
        assertEquals("myquery", query.getHandle());
    }


    /** Test of toString method, of class org.geotoolkit.data.DefaultQuery. */
    public void testToString() {
        final QueryBuilder builder = new QueryBuilder();

        System.out.println("testToString");
        builder.setTypeName(new DefaultName("test"));
        Query query = builder.buildQuery();
        assertNotNull(query.toString());

        builder.setHandle("myquery");
        query = builder.buildQuery();
        assertNotNull(query.toString());

        builder.setFilter(Filter.EXCLUDE);
        query = builder.buildQuery();
        assertNotNull(query.toString());

        builder.setProperties(new String[]{"foo", "bar"});
        query = builder.buildQuery();
        assertNotNull(query.toString());

    }
}
