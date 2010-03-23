/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import junit.framework.TestCase;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureValidationUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;

public class TypesTest extends TestCase {
    public void testWithoutRestriction(){
        // used to prevent warning
        FilterFactory fac = FactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName("test");
        builder.add(attributeName, String.class);
        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Value"},
                null);
        
        assertNotNull( feature );        
    }
    /**
     * This utility class is used by Types to prevent attribute modification.
     */
    public void testRestrictionCheck() {
        FilterFactory fac = FactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        PropertyIsEqualTo filter = fac.equals(fac.property("string"), fac
                .literal("Value"));

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setBinding(String.class);
        atb.addRestriction(filter);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(attributeName);
        adb.setType(atb.buildType());

        builder.setName("test");
        builder.add(adb.buildDescriptor());
        
        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Value"},
                null);
        
        assertNotNull( feature );

    }
    
    public void testAssertNamedAssignable(){
        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        builder.reset();
        builder.setName("Test");
        builder.add("name", String.class );
        builder.add("age", Double.class );
        SimpleFeatureType test = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("age", Double.class );
        builder.add("name",String.class);
        SimpleFeatureType test2 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("name",String.class);
        SimpleFeatureType test3 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("name",String.class);
        builder.add("distance", Double.class );
        SimpleFeatureType test4 = builder.buildSimpleFeatureType();
     
        FeatureValidationUtilities.assertNameAssignable( test, test );
        FeatureValidationUtilities.assertNameAssignable( test, test2 );
        FeatureValidationUtilities.assertNameAssignable( test2, test );
        try {
            FeatureValidationUtilities.assertNameAssignable( test, test3 );
            fail("Expected assertNameAssignable to fail as age is not covered");
        }
        catch ( IllegalArgumentException expected ){            
        }
        
        FeatureValidationUtilities.assertOrderAssignable( test, test4 );
    }

    public void testComplexType(){
        
    }

}
