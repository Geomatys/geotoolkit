/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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

package org.geotoolkit.feature.xml;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import static org.geotoolkit.data.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.*;

import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;

import static org.junit.Assert.*;
import static org.geotoolkit.feature.xml.XmlTestData.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTypeTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static void removeGMLBaseTypes(List<FeatureType> types){
        for(int i=types.size()-1;i>=0;i--){
            final FeatureType candidate = types.get(i);
            if(Utils.GML_FEATURE_TYPES.contains(candidate.getName())){
                types.remove(i);
            }
        }
    }

    @Test
    public void testReadSimpleFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureTypeWithAny() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAny.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimple");
        ftb.add(DefaultName.create(GML_311_NAMESPACE, "_any"),Object.class,0,1,true,null);
        final FeatureType simpleTypeAny = ftb.buildFeatureType();

        assertEquals(simpleTypeAny, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureType2() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType2.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(simpleTypeFull.getDescriptors().size(), types.get(0).getDescriptors().size());
        final int size = simpleTypeFull.getDescriptors().size();
        Iterator<PropertyDescriptor> expIt = simpleTypeFull.getDescriptors().iterator();
        Iterator<PropertyDescriptor> resIt = types.get(0).getDescriptors().iterator();
        for (int i = 0; i < size; i++) {
            PropertyDescriptor expDesc = expIt.next();
            PropertyDescriptor resDesc = resIt.next();
            assertEquals(expDesc, resDesc);
        }
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadMultiGeomFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadWfsFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/wfs1.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        //assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadComplexFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(complexType, types.get(0));
    }
    
    @Test
    public void testReadVeryComplexFeatureType() throws Exception {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final URL url = new URL("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        final List<FeatureType> types = reader.read(url);
        removeGMLBaseTypes(types);
        System.out.println("RESULT:" + types);
    }

    @Test
    public void testReadPropertyGroupFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/propertyGroupType.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("SF_SamplingFeature")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(5, type.getDescriptors().size());

    }

    @Test
    public void testReadInheritType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/inheritType.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("SF_SpatialSamplingFeature")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(6, type.getDescriptors().size());

    }

    @Test
    public void testAttributesType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAttribute.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("TestSimple")){
                type = ft;
            }
        }

        assertNotNull(type);
        
        assertEquals(typeWithAtts.getDescriptors().size(), types.get(0).getDescriptors().size());
        final int size = typeWithAtts.getDescriptors().size();
        Iterator<PropertyDescriptor> expIt = typeWithAtts.getDescriptors().iterator();
        Iterator<PropertyDescriptor> resIt = types.get(0).getDescriptors().iterator();
        for (int i = 0; i < size; i++) {
            PropertyDescriptor expDesc = expIt.next();
            PropertyDescriptor resDesc = resIt.next();
            assertEquals(expDesc, resDesc);
        }
        
        assertEquals(typeWithAtts, types.get(0));

    }

    @Test
    public void testReadSimpleFeatureEmpty() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(typeEmpty, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureEmpty2() throws JAXBException {
        final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
        reader.setSkipStandardObjectProperties(false);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        //TODO we should check all properties
        final FeatureType type = types.get(0);
        final PropertyDescriptor idesc = type.getDescriptor("identifier");
        assertNotNull(idesc);
        final PropertyType itype = idesc.getType();
        assertTrue(itype instanceof ComplexType);
        assertEquals("CodeWithAuthorityType", itype.getName().tip().toString());
        final ComplexType ct = (ComplexType) itype;
        assertEquals(2, ct.getDescriptors().size());
        
    }

    @Test
    public void testReadTypeWithNil() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithNil.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(typeWithNil, types.get(0));
    }

    @Test
    public void testReadTypeWithSubstitutions() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType type = types.get(0);
        assertEquals("TestSimple", type.getName().tip().toString());
        assertEquals(29, type.getDescriptors().size());
        //28 substitutiongroups
    }

    @Test
    public void testWriteSimpleFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(simpleTypeFull, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
    }

    @Test
    public void testWriteMultiGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(multiGeomType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"), temp);
    }

    @Test
    public void testWriteComplexFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(complexType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"), temp);
    }

    @Ignore
    @Test
    public void testWriteTypeWithSubstitutions() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType type = types.get(0);

        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter("3.2.1");
        writer.write(type, new FileOutputStream(temp));

        //NOTE : there are some variations since element references are lost in the way.
        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution2.xsd"), temp);

    }
    
    protected static String getStringResponse(URLConnection conec) throws UnsupportedEncodingException, IOException {
        final StringWriter sw     = new StringWriter();
        final BufferedReader in   = new BufferedReader(new InputStreamReader(conec.getInputStream(), "UTF-8"));
        char [] buffer = new char[1024];
        int size;
        while ((size = in.read(buffer, 0, 1024)) > 0) {
            sw.append(new String(buffer, 0, size));
        }
        return sw.toString();
    }
}
