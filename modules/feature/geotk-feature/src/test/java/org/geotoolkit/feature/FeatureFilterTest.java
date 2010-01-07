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
 */
package org.geotoolkit.feature;


import junit.framework.TestCase;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;



public class FeatureFilterTest extends TestCase {

    public FeatureFilterTest() {
    }

    /**
     * Test that we get acces attributs without knowing the namespace
     */
    public void testRetrieve() {

        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("testing");
        sftb.add(new DefaultName("http://test1.com", "att_string"), String.class);
        sftb.add(new DefaultName("http://test2.com", "att_string"), String.class);
        sftb.add(new DefaultName(null, "att_double"), String.class);

        final SimpleFeatureType sft = sftb.buildFeatureType();

        //test a no namespace property
        PropertyAccessor accessor = Accessors.getAccessor(SimpleFeatureType.class, "att_double", AttributeDescriptor.class);
        assertNotNull(accessor);
        AttributeDescriptor desc = (AttributeDescriptor) accessor.get(sft, "att_double", AttributeDescriptor.class);

        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName(null, "att_double"));

        //test a namespace property without namespace
        accessor = Accessors.getAccessor(SimpleFeatureType.class, "att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "att_string", AttributeDescriptor.class);

        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test1.com", "att_string"));

        //test a namespace property with namespace
        accessor = Accessors.getAccessor(SimpleFeatureType.class, "http://test1.com:att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "http://test1.com:att_string", AttributeDescriptor.class);
        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test1.com", "att_string"));

        accessor = Accessors.getAccessor(SimpleFeatureType.class, "http://test2.com:att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "http://test2.com:att_string", AttributeDescriptor.class);
        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test2.com", "att_string"));

    }

}
