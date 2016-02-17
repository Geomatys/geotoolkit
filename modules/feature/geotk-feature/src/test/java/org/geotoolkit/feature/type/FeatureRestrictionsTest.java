/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.junit.Test;

import org.geotoolkit.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureRestrictionsTest extends org.geotoolkit.test.TestBase {

    private static final FilterFactory FF = new DefaultFilterFactory2();

    public FeatureRestrictionsTest() {
    }

    @Test
    public void testEqualsRestriction(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        ftb.setName("test");
        atb.setName("name");
        atb.setBinding(String.class);
        atb.addRestriction(FF.equals(FF.property("."), FF.literal("correct")));
        ftb.add(atb.buildType(), NamesExt.create("name"), null, 1, 1, false, null);

        final FeatureType ft = ftb.buildFeatureType();
        final Feature feature = FeatureUtilities.defaultFeature(ft, "-1");
        feature.getProperty("name").setValue("fail");

        try{
            feature.validate();
            fail("Validation should have failed.");
        }catch(Exception ex){
            //fails , ok
        }

        feature.getProperty("name").setValue("correct");
        feature.validate();
        //should be ok

    }

}
