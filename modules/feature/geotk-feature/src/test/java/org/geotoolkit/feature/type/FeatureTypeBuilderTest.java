/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 Geomatys
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


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import org.apache.sis.internal.util.UnmodifiableArrayList;

import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.feature.simple.SimpleFeatureType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Feature type builder.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureTypeBuilderTest {

    @Test
    public void testFactoryUse(){

        //check it correctly use the given factory
        FeatureTypeFactory ftf = FeatureTypeFactory.INSTANCE;
        FeatureTypeBuilder ftb = new FeatureTypeBuilder(ftf);
        assertEquals(ftf, ftb.getFeatureTypeFactory());

        //check it creates one if needed
        ftb = new FeatureTypeBuilder();
        assertNotNull(ftb.getFeatureTypeFactory());
    }

    @Test
    public void testSimpleFeatureTypeCreation(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");

        //both should create a simple feature type
        FeatureType ft1 = ftb.buildFeatureType();
        FeatureType ft2 = ftb.buildFeatureType();

        assertTrue(ft1.isSimple());
        assertEquals(ft1, ft2);
    }

    @Test
    public void testComplexFeatureTypeCreation(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        FeatureType ft;

        ////////////////////////////////////////////////////////////////////////
        // if one attribut minimum is 0, it's not a simple type ////////////////
        ftb.reset();
        ftb.setName("test");
        ftb.add("att", String.class,0,1,true,null);

        ft = ftb.buildFeatureType();
        assertFalse(ft.isSimple());

        ////////////////////////////////////////////////////////////////////////
        // if one attribut maximum is more than 1 , it's not a simple type /////
        ftb.reset();
        ftb.setName("test");
        ftb.add("att", String.class,1,12,true,null);

        ft = ftb.buildFeatureType();
        assertFalse(ft.isSimple());

        ////////////////////////////////////////////////////////////////////////
        // if one attribut maximum is more than 1 , it's not a simple type /////
        ftb.reset();
        ftb.setName("test");
        ftb.add("att", String.class,1,12,true,null);

        ft = ftb.buildFeatureType();
        assertFalse(ft.isSimple());

        ////////////////////////////////////////////////////////////////////////
        // if one attribut is complex it's not a simple type ///////////////////
        ftb.reset();
        ftb.setName("cpxatt");
        ftb.add("att", String.class,1,12,true,null);
        ComplexType ct = ftb.buildType();

        ftb.reset();
        ftb.setName("test");
        ftb.add(ct,DefaultName.valueOf("att"),null,1,1,true,null);

        ft = ftb.buildFeatureType();
        assertFalse(ft.isSimple());

    }

    @Test
    public void testAttributeNamespaceConformance(){
        final String ns = "http://test.com";

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(ns,"test");
        SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        assertEquals(DefaultName.create(ns, "test"), sft.getName());

        ftb.setName("test");
        sft = ftb.buildSimpleFeatureType();
        assertEquals(DefaultName.create(null, "test"), sft.getName());

        ftb.setName(DefaultName.create(ns, "test"));
        sft = ftb.buildSimpleFeatureType();
        assertEquals(DefaultName.create(ns, "test"), sft.getName());

        ftb.setName(ns,":","test");
        sft = ftb.buildSimpleFeatureType();
        assertEquals(DefaultName.create(ns, "test"), sft.getName());


        ftb.add("att_String1", String.class);
        ftb.add(ns+":att_String2", String.class);
        ftb.add("{"+ns+"}att_String3", String.class);
        ftb.add(DefaultName.create(ns, "att_String4"), String.class);
        sft = ftb.buildSimpleFeatureType();

        assertEquals(DefaultName.create("att_String1"), sft.getDescriptor(0).getName());
        assertEquals(DefaultName.create(ns, "att_String2"), sft.getDescriptor(1).getName());
        assertEquals(DefaultName.create(ns, "att_String3"), sft.getDescriptor(2).getName());
        assertEquals(DefaultName.create(ns, "att_String4"), sft.getDescriptor(3).getName());

    }

    @Test
    public void testAttributeOrderConformance(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("{http://test.com}att_String", String.class);
        ftb.add("{http://test2.com}att_String", String.class);
        ftb.add("{http://test3.com}att_String", String.class);
        ftb.add("{http://test.com}att_Integer", Integer.class);
        ftb.add("{http://test.com}att_Double", Double.class);
        ftb.add("{http://test.com}att_Date", Date.class);
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        assertEquals(6, sft.getAttributeCount());
        assertEquals(6, sft.getDescriptors().size());

        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor(0).getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getDescriptor(1).getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getDescriptor(2).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor(3).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor(4).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor(5).getName());

        //check that the first att with this local part name is returned.
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor("att_String").getName());


        ////////////////////////////////////////////////////////////////////////
        //same test on complex type ////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        ftb.reset();
        ftb.setName("test");
        ftb.add("{http://test.com}att_String", String.class,0,1,true,null);
        ftb.add("{http://test2.com}att_String", String.class,0,1,true,null);
        ftb.add("{http://test3.com}att_String", String.class,0,1,true,null);
        ftb.add("{http://test.com}att_Integer", Integer.class,0,1,true,null);
        ftb.add("{http://test.com}att_Double", Double.class,0,1,true,null);
        ftb.add("{http://test.com}att_Date", Date.class,0,1,true,null);
        FeatureType ft = ftb.buildFeatureType();
        assertFalse(ft instanceof SimpleFeatureType);

        Collection<PropertyDescriptor> properties = ft.getDescriptors();
        assertEquals(6, properties.size());

        Iterator<PropertyDescriptor> ite = properties.iterator();
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ite.next().getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), ite.next().getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), ite.next().getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), ite.next().getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), ite.next().getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), ite.next().getName());

        //check that the first att with this local part name is returned.
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ft.getDescriptor("att_String").getName());

    }

    @Test
    public void testAttributeAccessConformance(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("{http://test.com}att_String", String.class);
        ftb.add("{http://test2.com}att_String", String.class);
        ftb.add("{http://test3.com}att_String", String.class);
        ftb.add("{http://test.com}att_Integer", Integer.class);
        ftb.add("{http://test.com}att_Double", Double.class);
        ftb.add("{http://test.com}att_Date", Date.class);
        ftb.add("{http://test.com}att_Geom", Geometry.class, CommonCRS.WGS84.normalizedGeographic());
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getAttributeDescriptors().get(0).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor(0).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor("att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor("http://test.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor("{http://test.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), sft.getDescriptor(DefaultName.create("http://test.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getAttributeDescriptors().get(1).getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getDescriptor(1).getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getDescriptor("http://test2.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getDescriptor("{http://test2.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), sft.getDescriptor(DefaultName.create("http://test2.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getAttributeDescriptors().get(2).getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getDescriptor(2).getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getDescriptor("http://test3.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getDescriptor("{http://test3.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), sft.getDescriptor(DefaultName.create("http://test3.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getAttributeDescriptors().get(3).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor(3).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor("att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor("http://test.com:att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor("{http://test.com}att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), sft.getDescriptor(DefaultName.create("http://test.com", "att_Integer")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getAttributeDescriptors().get(4).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor(4).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor("att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor("http://test.com:att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor("{http://test.com}att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), sft.getDescriptor(DefaultName.create("http://test.com", "att_Double")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getAttributeDescriptors().get(5).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor(5).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor("att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor("http://test.com:att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor("{http://test.com}att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), sft.getDescriptor(DefaultName.create("http://test.com", "att_Date")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getAttributeDescriptors().get(6).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getDescriptor(6).getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getDescriptor("att_Geom").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getDescriptor("http://test.com:att_Geom").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getDescriptor("{http://test.com}att_Geom").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getDescriptor(DefaultName.create("http://test.com", "att_Geom")).getName());

        //geometry attribut
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), sft.getGeometryDescriptor().getName());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), sft.getCoordinateReferenceSystem());


        ////////////////////////////////////////////////////////////////////////
        //same test on complex type ////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        ftb.reset();
        ftb.setName("test");
        ftb.add("{http://test.com}att_String", String.class,0,12,true,null);
        ftb.add("{http://test2.com}att_String", String.class,0,12,true,null);
        ftb.add("{http://test3.com}att_String", String.class,0,12,true,null);
        ftb.add("{http://test.com}att_Integer", Integer.class,0,12,true,null);
        ftb.add("{http://test.com}att_Double", Double.class,0,12,true,null);
        ftb.add("{http://test.com}att_Date", Date.class,0,12,true,null);
        ftb.add("{http://test.com}att_Geom", Geometry.class, CommonCRS.WGS84.normalizedGeographic(),0,12,true,null);
        FeatureType ft = ftb.buildFeatureType();
        assertFalse(ft instanceof SimpleFeatureType);


        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ft.getDescriptor("att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ft.getDescriptor("http://test.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ft.getDescriptor("{http://test.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_String"), ft.getDescriptor(DefaultName.create("http://test.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), ft.getDescriptor("http://test2.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), ft.getDescriptor("{http://test2.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test2.com}att_String"), ft.getDescriptor(DefaultName.create("http://test2.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), ft.getDescriptor("http://test3.com:att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), ft.getDescriptor("{http://test3.com}att_String").getName());
        assertEquals(DefaultName.valueOf("{http://test3.com}att_String"), ft.getDescriptor(DefaultName.create("http://test3.com", "att_String")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), ft.getDescriptor("att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), ft.getDescriptor("http://test.com:att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), ft.getDescriptor("{http://test.com}att_Integer").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Integer"), ft.getDescriptor(DefaultName.create("http://test.com", "att_Integer")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), ft.getDescriptor("att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), ft.getDescriptor("http://test.com:att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), ft.getDescriptor("{http://test.com}att_Double").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Double"), ft.getDescriptor(DefaultName.create("http://test.com", "att_Double")).getName());

        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), ft.getDescriptor("att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), ft.getDescriptor("http://test.com:att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), ft.getDescriptor("{http://test.com}att_Date").getName());
        assertEquals(DefaultName.valueOf("{http://test.com}att_Date"), ft.getDescriptor(DefaultName.create("http://test.com", "att_Date")).getName());

        //geometry attribut
        assertEquals(DefaultName.valueOf("{http://test.com}att_Geom"), ft.getGeometryDescriptor().getName());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), ft.getCoordinateReferenceSystem());

    }

    @Test
    public void testAttributeDetailConformance(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("att1", String.class);
        ftb.add("att2", String.class,0,1,true,null);
        ftb.add("att3", String.class,0,23,true,null);
        ftb.add("att4", String.class,0,23,false,null);
        ftb.add(DefaultName.valueOf("geom1"), Point.class,null,0,13,false,null);
        ftb.add(DefaultName.valueOf("geom2"), Point.class,CommonCRS.WGS84.normalizedGeographic(),1,21,true,null);
        final FeatureType ft = ftb.buildFeatureType();

        final List<PropertyDescriptor> properties = new ArrayList(ft.getDescriptors());
        PropertyDescriptor desc;
        AttributeDescriptor atDesc;
        GeometryDescriptor geoDesc;

        desc = properties.get(0);
        assertTrue(desc instanceof AttributeDescriptor);
        atDesc = (AttributeDescriptor) desc;
        assertEquals("att1", atDesc.getLocalName());
        assertEquals(1, atDesc.getMinOccurs());
        assertEquals(1, atDesc.getMaxOccurs());
        assertTrue(atDesc.isNillable());
        assertEquals(String.class, atDesc.getType().getBinding());
        assertEquals(0,atDesc.getUserData().size());

        desc = properties.get(1);
        assertTrue(desc instanceof AttributeDescriptor);
        atDesc = (AttributeDescriptor) desc;
        assertEquals("att2", atDesc.getLocalName());
        assertEquals(0, atDesc.getMinOccurs());
        assertEquals(1, atDesc.getMaxOccurs());
        assertTrue(atDesc.isNillable());
        assertEquals(String.class, atDesc.getType().getBinding());
        assertEquals(0,atDesc.getUserData().size());

        desc = properties.get(2);
        assertTrue(desc instanceof AttributeDescriptor);
        atDesc = (AttributeDescriptor) desc;
        assertEquals("att3", atDesc.getLocalName());
        assertEquals(0, atDesc.getMinOccurs());
        assertEquals(23, atDesc.getMaxOccurs());
        assertTrue(atDesc.isNillable());
        assertEquals(String.class, atDesc.getType().getBinding());
        assertEquals(0,atDesc.getUserData().size());

        desc = properties.get(3);
        assertTrue(desc instanceof AttributeDescriptor);
        atDesc = (AttributeDescriptor) desc;
        assertEquals("att4", atDesc.getLocalName());
        assertEquals(0, atDesc.getMinOccurs());
        assertEquals(23, atDesc.getMaxOccurs());
        assertFalse(atDesc.isNillable());
        assertEquals(String.class, atDesc.getType().getBinding());
        assertEquals(0,atDesc.getUserData().size());


        //geometries -----------------------------------------------------------

        desc = properties.get(4);
        assertTrue(desc instanceof GeometryDescriptor);
        geoDesc = (GeometryDescriptor) desc;
        assertEquals("geom1", geoDesc.getLocalName());
        assertEquals(0, geoDesc.getMinOccurs());
        assertEquals(13, geoDesc.getMaxOccurs());
        assertFalse(geoDesc.isNillable());
        assertEquals(Point.class, geoDesc.getType().getBinding());
        assertEquals(null, geoDesc.getCoordinateReferenceSystem());
        assertEquals(0,geoDesc.getUserData().size());

        desc = properties.get(5);
        assertTrue(desc instanceof GeometryDescriptor);
        geoDesc = (GeometryDescriptor) desc;
        assertEquals("geom2", geoDesc.getLocalName());
        assertEquals(1, geoDesc.getMinOccurs());
        assertEquals(21, geoDesc.getMaxOccurs());
        assertTrue(geoDesc.isNillable());
        assertEquals(Point.class, geoDesc.getType().getBinding());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), geoDesc.getCoordinateReferenceSystem());
        assertEquals(0,geoDesc.getUserData().size());

    }

    @Test
    public void testAbstractType() throws Exception {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("http://www.nowhereinparticular.net", "AbstractThing");
        ftb.setAbstract(true);

        SimpleFeatureType abstractType = ftb.buildSimpleFeatureType();
        ftb.setName("http://www.nowhereinparticular.net", "AbstractType2");
        ftb.setSuperType(abstractType);
        ftb.add(DefaultName.create("X"), String.class);
        SimpleFeatureType abstractType2 = ftb.buildSimpleFeatureType();

        assertTrue(abstractType.isAbstract());
        assertTrue(abstractType2.isAbstract());

        assertTrue("abstractType2 --|> abstractType", FeatureTypeUtilities.isDecendedFrom(abstractType2, abstractType));
        assertFalse("abstractType2 !--|> abstractType", FeatureTypeUtilities.isDecendedFrom(abstractType, abstractType2));

    }

    @Test
    public void testEquals() throws Exception {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(DefaultName.create("http://www.nowhereinparticular.net", "Thing"));
        ftb.add(DefaultName.create("X"), String.class);
        final SimpleFeatureType ft = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(DefaultName.create("http://www.nowhereinparticular.net", "Thing"));
        ftb.add(DefaultName.create("X"), String.class);
        SimpleFeatureType ft2 = ftb.buildSimpleFeatureType();

        assertEquals(ft, ft2);

        ftb.setName(DefaultName.create("Thingee"));
        assertTrue(!ft.equals(ftb.buildSimpleFeatureType()));

        ftb.copy(ft);
        ftb.setName(DefaultName.create("http://www.somewhereelse.net", ftb.getName().getLocalPart()));

        assertTrue(!ft.equals(ftb.buildSimpleFeatureType()));
        assertTrue(!ft.equals(null));
    }

    /**
     * Test FeatureTypes.getAncestors() by constructing three levels of derived types and testing
     * that the expected ancestors are returned at each level in reverse order.
     *
     * <p>
     *
     * UML type hierarchy of test types: Feature <|-- A <|-- B <|-- C
     *
     * @throws Exception
     */
    @Test
    public void testAncestors() throws Exception {
        String uri = "http://www.geotoolkit.org/example";

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(DefaultName.create(uri, "A"));
        final SimpleFeatureType typeA = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(DefaultName.create(uri, "B"));
        ftb.setSuperType(typeA);
        ftb.add(DefaultName.create("b"), String.class);
        final SimpleFeatureType typeB = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(DefaultName.create(uri, "C"));
        ftb.setSuperType(typeB);
        ftb.add(DefaultName.create("c"), Integer.class);
        final SimpleFeatureType typeC = ftb.buildSimpleFeatureType();

        // base type should have no ancestors
        assertEquals("Ancestors of Feature, nearest first",
                Collections.<FeatureType>emptyList(),
                FeatureTypeUtilities.getAncestors(BasicFeatureTypes.FEATURE));

        assertEquals("Ancestors of A, nearest first",
                UnmodifiableArrayList.wrap(new SimpleFeatureType[] {BasicFeatureTypes.FEATURE}),
                FeatureTypeUtilities.getAncestors(typeA));

        assertEquals("Ancestors of B, nearest first",
                UnmodifiableArrayList.wrap(new SimpleFeatureType[] {typeA,BasicFeatureTypes.FEATURE}),
                FeatureTypeUtilities.getAncestors(typeB));

        assertEquals("Ancestors of C, nearest first",
                UnmodifiableArrayList.wrap(new SimpleFeatureType[] {typeB,typeA,BasicFeatureTypes.FEATURE}),
                FeatureTypeUtilities.getAncestors(typeC));
    }

}
