/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.data.internal.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.query.JoinType;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.data.ArrayFeatureSet;
import org.geotoolkit.internal.data.JoinFeatureSet;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel
 */
public class JoinFeatureSetTest {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final String ATT_ID = AttributeConvention.IDENTIFIER_PROPERTY.toString();

    private final FeatureSet featureSet1;
    private final FeatureSet featureSet2;
    private final GenericName name1;
    private final GenericName name2;

    private final String fid_1_0;
    private final String fid_1_1;
    private final String fid_1_2;
    private final String fid_1_3;
    private final String fid_1_4;
    private final String fid_2_0;
    private final String fid_2_1;
    private final String fid_2_2;
    private final String fid_2_3;
    private final String fid_2_4;
    private final String fid_2_5;


    public JoinFeatureSetTest() throws Exception {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        //----------------------------------------------------------------------
        name1 = NamesExt.create("http://type1.com", "Type1");
        builder.setName(name1);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("http://type1.com", "att1");
        builder.addAttribute(Integer.class).setName("http://type1.com", "att2");
        final FeatureType sft1 = builder.build();

        final List<Feature> features1 = new ArrayList<>();

        Feature sf = sft1.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_1_0");
        sf.setPropertyValue("att1", "str1");
        sf.setPropertyValue("att2", 1);
        fid_1_0 = FeatureExt.getId(sf).getID();
        features1.add(sf);

        sf = sft1.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_1_1");
        sf.setPropertyValue("att1", "str2");
        sf.setPropertyValue("att2", 2);
        fid_1_1 = FeatureExt.getId(sf).getID();
        features1.add(sf);

        sf = sft1.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_1_2");
        sf.setPropertyValue("att1", "str3");
        sf.setPropertyValue("att2", 3);
        fid_1_2 = FeatureExt.getId(sf).getID();
        features1.add(sf);

        sf = sft1.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_1_3");
        sf.setPropertyValue("att1", "str50");
        sf.setPropertyValue("att2", 50);
        fid_1_3 = FeatureExt.getId(sf).getID();
        features1.add(sf);

        sf = sft1.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_1_4");
        sf.setPropertyValue("att1", "str51");
        sf.setPropertyValue("att2", 51);
        fid_1_4 = FeatureExt.getId(sf).getID();
        features1.add(sf);

        featureSet1 = new ArrayFeatureSet(sft1, features1, null);


        //----------------------------------------------------------------------
        name2 = NamesExt.create("http://type2.com", "Type2");
        builder = new FeatureTypeBuilder();
        builder.setName(name2);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Integer.class).setName("http://type2.com", "att3");
        builder.addAttribute(Double.class).setName("http://type2.com", "att4");
        final FeatureType sft2 = builder.build();

        final List<Feature> features2 = new ArrayList<>();

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_0");
        sf.setPropertyValue("att3", 1);
        sf.setPropertyValue("att4", 10d);
        fid_2_0 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_1");
        sf.setPropertyValue("att3", 2);
        sf.setPropertyValue("att4", 20d);
        fid_2_1 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_2");
        sf.setPropertyValue("att3", 2);
        sf.setPropertyValue("att4", 30d);
        fid_2_2 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_3");
        sf.setPropertyValue("att3", 3);
        sf.setPropertyValue("att4", 40d);
        fid_2_3 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_4");
        sf.setPropertyValue("att3", 60);
        sf.setPropertyValue("att4", 60d);
        fid_2_4 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        sf = sft2.newInstance();
        sf.setPropertyValue(ATT_ID, "fid_2_5");
        sf.setPropertyValue("att3", 61);
        sf.setPropertyValue("att4", 61d);
        fid_2_5 = FeatureExt.getId(sf).getID();
        features2.add(sf);

        featureSet2 = new ArrayFeatureSet(sft2, features2, null);

    }

    /**
     * Test that cross featurestore queries works correctly.
     */
    @Test
    public void testInnerJoinQuery() throws Exception{

        final PropertyIsEqualTo condition = FF.equals(FF.property("att2"), FF.property("att3"));
        final FeatureSet col = new JoinFeatureSet(featureSet1, "s1", featureSet2, "s2", JoinType.INNER, condition);

        try (Stream<Feature> stream = col.features(false)) {
            final Iterator<Feature> ite = stream.iterator();
            Feature f = null;
            Feature c1 = null;
            Feature c2 = null;

            int count = 0;
            while(ite.hasNext()){
                count++;
                f = ite.next();
                if(FeatureExt.getId(f).getID().equals(fid_1_0 +" "+fid_2_0)){
                    c1 = (Feature) f.getPropertyValue("s1");
                    c2 = (Feature) f.getPropertyValue("s2");
                    assertEquals("str1", c1.getProperty("att1").getValue());
                    assertEquals(1, c1.getProperty("att2").getValue());
                    assertEquals(1, c2.getProperty("att3").getValue());
                    assertEquals(10d, c2.getProperty("att4").getValue());
                }else if(FeatureExt.getId(f).getID().equals(fid_1_1 +" "+fid_2_1)){
                    c1 = (Feature) f.getPropertyValue("s1");
                    c2 = (Feature) f.getPropertyValue("s2");
                    assertEquals("str2", c1.getProperty("att1").getValue());
                    assertEquals(2, c1.getProperty("att2").getValue());
                    assertEquals(2, c2.getProperty("att3").getValue());
                    assertEquals(20d, c2.getProperty("att4").getValue());
                }else if(FeatureExt.getId(f).getID().equals(fid_1_1 +" "+fid_2_2)){
                    c1 = (Feature) f.getPropertyValue("s1");
                    c2 = (Feature) f.getPropertyValue("s2");
                    assertEquals("str2", c1.getProperty("att1").getValue());
                    assertEquals(2, c1.getProperty("att2").getValue());
                    assertEquals(2, c2.getProperty("att3").getValue());
                    assertEquals(30d, c2.getProperty("att4").getValue());
                }else if(FeatureExt.getId(f).getID().equals(fid_1_2 +" "+fid_2_3)){
                    c1 = (Feature) f.getPropertyValue("s1");
                    c2 = (Feature) f.getPropertyValue("s2");
                    assertEquals("str3", c1.getProperty("att1").getValue());
                    assertEquals(3, c1.getProperty("att2").getValue());
                    assertEquals(3, c2.getProperty("att3").getValue());
                    assertEquals(40d, c2.getProperty("att4").getValue());
                }else{
                    fail("unexpected feature");
                }
            }

            assertEquals("Was expecting 4 features.",4, count);
        }
    }

    /**
     * Test that cross featurestore queries works correctly.
     */
    @Test
    public void testOuterLeftQuery() throws Exception{

        final PropertyIsEqualTo condition = FF.equals(FF.property("att2"), FF.property("att3"));
        final FeatureSet col = new JoinFeatureSet(featureSet1, "s1", featureSet2, "s2", JoinType.LEFT_OUTER, condition);

        try (Stream<Feature> stream = col.features(false)) {
            final Iterator<Feature> ite = stream.iterator();

            boolean foundStr1 = false;
            boolean foundStr20 = false;
            boolean foundStr21 = false;
            boolean foundStr3 = false;
            boolean foundStr50 = false;
            boolean foundStr51 = false;

            int count = 0;
            while(ite.hasNext()){
                final Feature f = ite.next();
                final Feature c1 = (Feature) f.getPropertyValue("s1");
                final Feature c2 = (Feature) f.getPropertyValue("s2");
                final String att1 = c1.getProperty("att1").getValue().toString();

                if(att1.equals("str1")){
                    foundStr1 = true;
                    assertEquals(c1.getProperty("att2").getValue(), 1);
                    assertEquals(c2.getProperty("att3").getValue(), 1);
                    assertEquals(c2.getProperty("att4").getValue(), 10d);
                }else if(att1.equals("str2")){
                    assertEquals(c1.getProperty("att2").getValue(), 2);
                    assertEquals(c2.getProperty("att3").getValue(), 2);
                    double att4 = (Double)c2.getProperty("att4").getValue();
                    if(att4 == 20d){
                        foundStr20 = true;
                    }else if(att4 == 30d){
                        foundStr21 = true;
                    }
                }else if(att1.equals("str3")){
                    foundStr3 = true;
                    assertEquals(c1.getProperty("att2").getValue(), 3);
                    assertEquals(c2.getProperty("att3").getValue(), 3);
                    assertEquals(c2.getProperty("att4").getValue(), 40d);
                }else if(att1.equals("str50")){
                    foundStr50 = true;
                    assertEquals(c1.getProperty("att2").getValue(), 50);
                    assertNull(c2);
                }else if(att1.equals("str51")){
                    foundStr51 = true;
                    assertEquals(c1.getProperty("att2").getValue(), 51);
                    assertNull(c2);
                }
                count++;
            }

            assertTrue(foundStr1);
            assertTrue(foundStr20);
            assertTrue(foundStr21);
            assertTrue(foundStr3);
            assertTrue(foundStr50);
            assertTrue(foundStr51);
            assertEquals(6, count);
        }
    }

    /**
     * Test that cross featurestore queries works correctly.
     */
    @Test
    public void testOuterRightQuery() throws Exception{

        final PropertyIsEqualTo condition = FF.equals(FF.property("att2"), FF.property("att3"));
        final FeatureSet col = new JoinFeatureSet(featureSet1, "s1", featureSet2, "s2", JoinType.RIGHT_OUTER, condition);

        try (Stream<Feature> stream = col.features(false)) {
            final Iterator<Feature> ite = stream.iterator();

            boolean foundStr1 = false;
            boolean foundStr20 = false;
            boolean foundStr21 = false;
            boolean foundStr3 = false;
            boolean foundStr60 = false;
            boolean foundStr61 = false;

            int count = 0;
            while(ite.hasNext()){
                final Feature f = ite.next();
                final Feature c1 = (Feature) f.getPropertyValue("s1");
                final Feature c2 = (Feature) f.getPropertyValue("s2");

                if(c1 != null){
                    final Object att1 = c1.getProperty("att1").getValue();
                    if("str1".equals(att1)){
                        foundStr1 = true;
                        assertEquals(c1.getProperty("att2").getValue(), 1);
                        assertEquals(c2.getProperty("att3").getValue(), 1);
                        assertEquals(c2.getProperty("att4").getValue(), 10d);
                    }else if("str2".equals(att1)){
                        assertEquals(c1.getProperty("att2").getValue(), 2);
                        assertEquals(c2.getProperty("att3").getValue(), 2);
                        double att4 = (Double)c2.getProperty("att4").getValue();
                        if(att4 == 20d){
                            foundStr20 = true;
                        }else if(att4 == 30d){
                            foundStr21 = true;
                        }
                    }else if("str3".equals(att1)){
                        foundStr3 = true;
                        assertEquals(c1.getProperty("att2").getValue(), 3);
                        assertEquals(c2.getProperty("att3").getValue(), 3);
                        assertEquals(c2.getProperty("att4").getValue(), 40d);
                    }
                }else{
                    int att3 = (Integer)c2.getProperty("att3").getValue();

                    if(att3 == 60){
                        assertEquals(c2.getProperty("att4").getValue(), 60d);
                        foundStr60 = true;
                    }else if(att3 == 61){
                        assertEquals(c2.getProperty("att4").getValue(), 61d);
                        foundStr61 = true;
                    }
                }

                count++;
            }

            assertTrue(foundStr1);
            assertTrue(foundStr20);
            assertTrue(foundStr21);
            assertTrue(foundStr3);
            assertTrue(foundStr60);
            assertTrue(foundStr61);
            assertEquals(6, count);

        }
    }


}
