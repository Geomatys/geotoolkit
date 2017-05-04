/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.apache.sis.feature;

import java.util.Collections;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureTypeExtText {

    @Test
    public void toPropertyTypeTest(){
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

        //sanity test
        final GeneralParameterDescriptor att1 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att1"), 1, 1, String.class, null, null, "testString");
        final PropertyType prop1 = FeatureTypeExt.toPropertyType(att1);

        atb.reset();
        atb.setName("att1");
        atb.setValueClass(String.class);
        atb.setMinimumOccurs(1);
        atb.setMaximumOccurs(1);
        atb.setDefaultValue("testString");
        assertEquals(atb.build(),prop1);


        //check min/max
        final GeneralParameterDescriptor att2 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att2"), 0, 21, Integer.class, null, null, null);
        final PropertyType prop2 = FeatureTypeExt.toPropertyType(att2);

        atb.reset();
        atb.setName("att2");
        atb.setValueClass(Integer.class);
        atb.setMinimumOccurs(0);
        atb.setMaximumOccurs(21);
        atb.setDefaultValue(null);
        assertEquals(atb.build(),prop2);
    }

    @Test
    public void toFeatureTypeTest(){

        final GeneralParameterDescriptor att1 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att1"), 1, 1, String.class, null, null, "testString");
        final GeneralParameterDescriptor att2 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att2"), 0, 21, Integer.class, null, null, null);
        final ParameterDescriptorGroup group = new DefaultParameterDescriptorGroup(
                Collections.singletonMap("name", "group"), 1, 1, att1,att2);
        final FeatureType featureType = FeatureTypeExt.toFeatureType(group);


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("group");
        ftb.addAttribute(String.class).setName("att1").setDefaultValue("testString");
        ftb.addAttribute(Integer.class).setName("att2").setMinimumOccurs(0).setMaximumOccurs(21);
        final FeatureType expectedType = ftb.build();

        assertEquals(expectedType, featureType);


    }

}
