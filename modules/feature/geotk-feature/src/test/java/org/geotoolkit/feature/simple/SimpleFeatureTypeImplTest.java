/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.feature.simple;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;

/**
 * Tests for {@link SimpleFeatureImpl}.
 * 
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * @module pending
 */
public class SimpleFeatureTypeImplTest extends TestCase {

    /**
     * Test that properties returned by getAttributeDescriptors() and getDescriptors() are the same
     * and in the same order.
     */
    public void testConsistentIterationOrder() {
        SimpleFeatureType type = buildLocationCountType();
        assertEquals(
                "FeatureType and SimpleFeatureType APIs must return the same descriptors in the same order",
                new ArrayList<PropertyDescriptor>(type.getAttributeDescriptors()),
                new ArrayList<PropertyDescriptor>(type.getDescriptors()));
    }

    /**
     * Test that property order is significant to equals().
     */
    public void testOrderSignificantEquals() {
        SimpleFeatureType type1 = buildLocationCountType();
        SimpleFeatureType type2 = buildCountLocationType();
        assertFalse("Simple feature types with properties in a different order must not be equal",
                type1.equals(type2));
    }

    /**
     * Test that distinct instances identically constructed are equal (location/count version).
     */
    public void testLocationCountEquals() {
        assertEquals("Identical simple feature types must be equal", buildLocationCountType(),
                buildLocationCountType());
    }

    /**
     * Test that distinct instances identically constructed are equal (count/location version).
     */
    public void testCountLocationEquals() {
        assertEquals("Identical simple feature types must be equal", buildCountLocationType(),
                buildCountLocationType());
    }

    /**
     * @return a simple feature type with location before count
     */
    private SimpleFeatureType buildLocationCountType() {
        SimpleFeatureTypeBuilder builder = buildPartialBuilder();
        builder.add("location", Point.class);
        builder.add("count", Integer.class);
        return builder.buildFeatureType();
    }

    /**
     * @return a simple feature type with count before location
     */
    private SimpleFeatureType buildCountLocationType() {
        SimpleFeatureTypeBuilder builder = buildPartialBuilder();
        builder.add("count", Integer.class);
        builder.add("location", Point.class);
        return builder.buildFeatureType();
    }

    /**
     * @return a simple feature type builder that is ready for the addition of location and count
     *         properties
     */
    private SimpleFeatureTypeBuilder buildPartialBuilder() {
        String uri = "http://example.org/things";
        DefaultFeatureTypeFactory typeFactory = new DefaultFeatureTypeFactory();
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder(typeFactory);
        builder.addBinding(typeFactory.createGeometryType(new DefaultName(uri, "description"),
                String.class, null, false, false, Collections.EMPTY_LIST, null, null));
        builder.addBinding(typeFactory.createGeometryType(new DefaultName(uri, "location"),
                Point.class, null, false, false, Collections.EMPTY_LIST, null, null));
        builder.addBinding(typeFactory.createAttributeType(new DefaultName(uri, "count"),
                Integer.class, false, false, Collections.EMPTY_LIST, null, null));
        builder.setName("ThingsType");
        builder.setNamespaceURI(uri);
        builder.add("description", String.class);
        return builder;
    }

}
