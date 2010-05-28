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

package org.geotoolkit.feature.calculated;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import static org.junit.Assert.*;

/**
 * Test calculated expression attribut.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedExpressionAttributeTest {

    private final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public CalculatedExpressionAttributeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetValue() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("att1", Integer.class);
        ftb.add("att2", Integer.class);
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        sfb.set("att1", 45);
        sfb.set("att2", 12);
        final SimpleFeature sf = sfb.buildFeature("id");

        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeDescriptor desc = adb.create(new DefaultName("calc"), Long.class, 1, 1, false, null);
        final Expression exp = FF.add(FF.property("att1"), FF.property("att2"));
        CalculatedExpressionAttribute att = new CalculatedExpressionAttribute(desc, exp);

        //test related correctly set
        assertNull(att.getRelated());
        att.setRelated(sf);
        assertNotNull(att.getRelated());

        Object val = att.getValue();
        assertTrue(val instanceof Long);
        assertEquals(57l, ((Long)val).longValue());
    }


}