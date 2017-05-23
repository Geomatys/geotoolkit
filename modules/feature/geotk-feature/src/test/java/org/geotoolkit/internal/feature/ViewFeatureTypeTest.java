/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.internal.feature;

import java.util.Collection;
import java.util.Collections;
import org.apache.sis.feature.FeatureOperations;
import org.geotoolkit.feature.ViewFeatureType;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ViewFeatureTypeTest {

    @Test
    public void filterAttributeTest(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(String.class).setName("attString");
        ftb.addAttribute(Double.class).setName("attDouble");
        final FeatureType baseType = ftb.build();

        //test view type
        final ViewFeatureType viewType = new ViewFeatureType(baseType, "attDouble");
        final Collection<? extends PropertyType> properties = viewType.getProperties(true);
        assertEquals(1,properties.size());
        final PropertyType attDouble = properties.iterator().next();
        assertEquals(baseType.getProperty("attDouble"), attDouble);

        //test feature
        final Feature baseFeature = baseType.newInstance();
        baseFeature.setPropertyValue("attString", "hello world");
        baseFeature.setPropertyValue("attDouble", 123.456);

        final Feature viewFeature = viewType.newInstance(baseFeature);
        assertEquals(123.456, (Double)viewFeature.getPropertyValue("attDouble"), 0);
        try{
            viewFeature.getPropertyValue("attString");
            fail("Property attString should not have been accessible");
        }catch(PropertyNotFoundException ex){/*ok*/}

    }

    @Test
    public void filterOperationTest(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        final AttributeType<String> attString = ftb.addAttribute(String.class).setName("attString").build();
        ftb.addProperty(FeatureOperations.link(Collections.singletonMap("name", "attRef"), attString));
        final FeatureType baseType = ftb.build();

        //test view type
        final ViewFeatureType viewType = new ViewFeatureType(baseType, "attRef");
        final Collection<? extends PropertyType> properties = viewType.getProperties(true);
        assertEquals(1,properties.size());
        final PropertyType attRef = properties.iterator().next();
        assertTrue(attRef instanceof Operation);
        assertNotEquals(baseType.getProperty("attRef"), attRef);

        //test feature
        final Feature baseFeature = baseType.newInstance();
        baseFeature.setPropertyValue("attString", "hello world");

        final Feature viewFeature = viewType.newInstance(baseFeature);
        assertEquals("hello world", viewFeature.getPropertyValue("attRef"));
        assertEquals("hello world", ((Operation)attRef).apply(viewFeature, null).getValue());
        try{
            viewFeature.getPropertyValue("attString");
            fail("Property attString should not have been accessible");
        }catch(PropertyNotFoundException ex){/*ok*/}

    }

}
