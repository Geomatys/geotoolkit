/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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
 *    Created on July 21, 2003, 4:00 PM
 */
package org.geotoolkit.feature.type;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import java.util.Date;

/**
 * Test the different feature type and attribute classes serialization.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class EqualityTest {

    /**
     * Custom factory where types can be modified after they are created.
     */
    private static final FeatureTypeFactory FTF = new ModifiableFeatureTypeFactory();

    @Test
    public void testComplexEquality() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureType ft1 = createDefaultComplexType();
        final FeatureType ft2 = createModifiableComplexType();
        Assert.assertEquals(ft1, ft2);
    }

    private static FeatureType createDefaultComplexType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final String ns = "http://test.com";

        //track point type
        ftb.setName(ns,"FishTrackPoint");
        ftb.add(new DefaultName(ns,"location"), Point.class, CRS.decode("EPSG:3395"));
        ftb.add(new DefaultName(ns,"time"), Date.class);
        final ComplexType trackPointType = ftb.buildType();

        //fish type
        ftb.reset();
        ftb.setName(ns,"Fish");
        ftb.add(new DefaultName(ns,"name"), String.class);
        ftb.add(new DefaultName(ns,"code"), String.class);
        final ComplexType fishType = ftb.buildType();

        //fish track type
        ftb.reset();
        ftb.setName(ns,"FishTrack");
        ftb.add(new DefaultName(ns,"trackNumber"), Long.class);
        AttributeDescriptor fishDesc = adb.create(fishType, new DefaultName(ns,"fish"),0,1,true,null);
        AttributeDescriptor trackpointsDesc = adb.create(trackPointType, new DefaultName(ns,"trackpoints"),0,Integer.MAX_VALUE,true,null);
        ftb.add(fishDesc);
        ftb.add(trackpointsDesc);
        final FeatureType ft = ftb.buildFeatureType();
        return ft;
    }

    private static FeatureType createModifiableComplexType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(FTF);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
        final String ns = "http://test.com";

        //track point type
        ftb.setName(ns,"FishTrackPoint");
        ftb.add(new DefaultName(ns,"location"), Point.class, CRS.decode("EPSG:3395"));
        ftb.add(new DefaultName(ns,"time"), Date.class);
        final ComplexType trackPointType = ftb.buildType();

        //fish type
        ftb.reset();
        ftb.setName(ns,"Fish");
        ftb.add(new DefaultName(ns,"name"), String.class);
        ftb.add(new DefaultName(ns,"code"), String.class);
        final ComplexType fishType = ftb.buildType();

        //fish track type
        ftb.reset();
        ftb.setName(ns,"FishTrack");
        ftb.add(new DefaultName(ns,"trackNumber"), Long.class);
        AttributeDescriptor fishDesc = adb.create(fishType, new DefaultName(ns,"fish"),0,1,true,null);
        AttributeDescriptor trackpointsDesc = adb.create(trackPointType, new DefaultName(ns,"trackpoints"),0,Integer.MAX_VALUE,true,null);
        ftb.add(fishDesc);
        ftb.add(trackpointsDesc);
        final FeatureType ft = ftb.buildFeatureType();
        return ft;
    }

}
