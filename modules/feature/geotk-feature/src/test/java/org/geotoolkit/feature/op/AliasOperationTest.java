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
package org.geotoolkit.feature.op;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.type.DefaultOperationDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.OperationType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Alias operation property test
 *
 * @author Johann Sorel (Geomatys)
 */
public class AliasOperationTest {

    private static final FeatureType TYPE;
    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("TypeWithAlias");
        final AttributeDescriptor attDesc = ftb.add("name",String.class);

        final OperationType opType = new AliasOperation(NamesExt.valueOf("substitute"), NamesExt.valueOf("name"), attDesc);
        final PropertyDescriptor pd = new DefaultOperationDescriptor(opType, NamesExt.valueOf("substitute"), 1, 1, true);
        ftb.add(pd);

        TYPE = ftb.buildFeatureType();
    }

    @Test
    public void readTest(){

        final Feature f = FeatureUtilities.defaultFeature(TYPE, "id-0");

        assertNull(f.getProperty("name").getValue());
        assertNull(f.getPropertyValue("name"));
        //operations do not have associated properties
        assertNull(f.getProperty("substitute"));
        //but if the operation does not have any parameter we can get it's value
        assertNull(f.getPropertyValue("substitute"));

        f.getProperty("name").setValue("test");
        assertEquals("test", f.getProperty("name").getValue());
        assertEquals("test", f.getPropertyValue("name"));
        //operations do not have associated properties
        assertNull(f.getProperty("substitute"));
        //but if the operation does not have any parameter we can get it's value
        assertEquals("test", f.getPropertyValue("substitute"));

    }

    @Test
    public void writeTest(){
        final Feature f = FeatureUtilities.defaultFeature(TYPE, "id-0");

        assertNull(f.getProperty("name").getValue());
        assertNull(f.getPropertyValue("name"));
        //operations do not have associated properties
        assertNull(f.getProperty("substitute"));
        //but if the operation does not have any parameter we can get it's value
        assertNull(f.getPropertyValue("substitute"));

        f.setPropertyValue("substitute","test");
        assertEquals("test", f.getProperty("name").getValue());
        assertEquals("test", f.getPropertyValue("name"));
        //operations do not have associated properties
        assertNull(f.getProperty("substitute"));
        //but if the operation does not have any parameter we can get it's value
        assertEquals("test", f.getPropertyValue("substitute"));

    }

}
