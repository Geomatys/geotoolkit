/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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

import com.vividsolutions.jts.geom.Geometry;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import static org.geotoolkit.data.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.*;

import static org.junit.Assert.*;
import static org.geotoolkit.feature.xml.XmlTestData.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.xml.sax.SAXException;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class XmlFeatureTypeTest extends org.geotoolkit.test.TestBase {

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

        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        // GML defines a lot more properties than what we strictly need. Nonetheless,
        // we must ensure that required prpoerties are read from xsd.
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureTypeWithAny() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAny.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(GMLConvention.ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Object.class).setName(NamesExt.create(GML_311_NAMESPACE, "any")).setMinimumOccurs(0).setMaximumOccurs(1);
        final FeatureType simpleTypeAny = ftb.build();

        assertEquals(simpleTypeAny, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureTypeWithRestriction() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithRestriction.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(GMLConvention.ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(String.class).setName(NamesExt.create(GML_311_NAMESPACE, "attString")).setMaximalLength(3)
                .addCharacteristic(GMLConvention.NILLABLE_CHARACTERISTIC).setDefaultValue(Boolean.TRUE);
        final FeatureType simpleTypeRestriction = ftb.build();

        assertEquals(simpleTypeRestriction, types.get(0));
    }

    @Test
    public void testReadMultiGeomFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadWfsFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/wfs1.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        final String ns = "http://mapserver.gis.umn.edu/mapserver";
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(ns,"quadrige");
        ftb.setSuperTypes(GMLConvention.ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Geometry.class).setName(ns, "msGeometry").setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAttribute(String.class).setName(ns, "C_SIEPT38").setMinimumOccurs(1).setMaximumOccurs(1);
        ftb.addAttribute(String.class).setName(ns, "L_SIEPT").setMinimumOccurs(1).setMaximumOccurs(1);
        final FeatureType wfsType = ftb.build();

        assertEquals(wfsType, types.get(0));
    }

    @Test
    public void testReadComplexFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        assertEquals(complexType, types.get(0));
    }

    @Test
    @Ignore("Random test failure due to connection timeout or when internet is not available. See #GEOTK-485")
    public void testReadVeryComplexFeatureType() throws Exception {
        final JAXBFeatureTypeReader reader = getReader(true);
        final URL url = new URL("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        final List<FeatureType> types = reader.read(url);
        removeGMLBaseTypes(types);
        System.out.println("RESULT:" + types);
    }

    @Test
    public void testReadPropertyGroupFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/propertyGroupType.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("SF_SamplingFeature")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(7, type.getProperties(true).size());

    }

    @Test
    public void testReadInheritType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/inheritType.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("SF_SpatialSamplingFeature")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(8, type.getProperties(true).size());

    }

    @Test
    public void testAttributesType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAttribute.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().tip().toString().equals("TestSimple")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(typeWithAtts, type);
    }

    @Test
    public void testReadSimpleFeatureEmpty() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType readType = types.get(0);

        assertEquals(typeEmpty.getName(), readType.getName());
        checkProperties(typeEmpty.getProperties(true), readType);
    }

    @Test
    public void testReadSimpleFeatureEmpty2() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(false);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        //TODO we should check all properties
        final FeatureType type = types.get(0);
        final PropertyType itype = type.getProperty("http://www.opengis.net/gml/3.2:identifier");
        assertNotNull(itype);
        assertTrue(itype instanceof AttributeType);
        final AttributeType ct = (AttributeType) itype;
        assertEquals(String.class, ct.getValueClass());
        assertNotNull(ct.characteristics().get("@codeSpace"));
        //assertEquals("CodeWithAuthorityType", ct.getName().tip().toString());

    }

    @Test
    public void testReadTypeWithNil() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithNil.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType readType = types.get(0);

        assertEquals(typeEmpty.getName(), readType.getName());
        checkProperties(typeEmpty.getProperties(true), readType);
    }

    @Test
    public void testReadTypeWithSubstitutions() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution.xsd"));
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType type = types.get(0);
        assertEquals("TestSimple", type.getName().tip().toString());
        //we do not count the substitution groups
        assertEquals(3, type.getProperties(true).size());
    }

    @Test
    public void testWriteSimpleFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(simpleTypeFull, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
    }

    @Test
    public void testWriteMultiGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(multiGeomType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"), temp);
    }

    @Test
    public void testWriteComplexFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(complexType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"), temp);
    }

//    @Ignore
//    @Test
//    public void testWriteTypeWithSubstitutions() throws JAXBException, IOException, ParserConfigurationException, SAXException {
//        final JAXBFeatureTypeReader reader = getReader(true);
//        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
//                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution.xsd"));
//        removeGMLBaseTypes(types);
//        assertEquals(1, types.size());
//        final FeatureType type = types.get(0);
//
//        final File temp = File.createTempFile("gml", ".xml");
//        temp.deleteOnExit();
//        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter("3.2.1");
//        writer.write(type, new FileOutputStream(temp));
//
//        //NOTE : there are some variations since element references are lost in the way.
//        DomCompare.compare(XmlFeatureTypeTest.class
//                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution2.xsd"), temp);
//
//    }

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

    private static JAXBFeatureTypeReader getReader(boolean skipStandardObjectProperties) {
        final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
        reader.setSkipStandardObjectProperties(skipStandardObjectProperties);
        return reader;
    }

    @Test
    public void testReadBiefType212() throws Exception {
        testReadBiefType(XmlFeatureTypeTest.class.getResource("bief212.xsd"));
    }

    @Test
    public void testReadBiefType311() throws Exception {
        testReadBiefType(XmlFeatureTypeTest.class.getResource("bief311.xsd"));
    }

    @Test
    public void testReadBiefType321() throws Exception {
        testReadBiefType(XmlFeatureTypeTest.class.getResource("bief321.xsd"));
    }
    
    /**
     * Read and check Bief datatype. The bief type is a schema extracted from
     * real-world WFS service. We want to ensure that its geometry and simple
     * attributes are well-extracted.
     * @param resource The resource in which is stored bief schema. The aim is
     * to be able to give same schema for different GML version.
     * @throws Exception Well, if we cannot read the XSD file, or its content is
     * invalid (cannot be converted to feature type).
     */
    public void testReadBiefType(final URL resource) throws Exception {
        final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final FeatureType type = reader.read(resource, "bief");
        Assert.assertNotNull("Read feature type is null", type);
        // First, check geometry
        final PropertyType geometry = type.getProperty("geom");
        checkIsAttribute(geometry, Geometry.class);

        // Then, check other attributes are present
        final long attrCount = type.getProperties(false).stream()
                .filter(AttributeType.class::isInstance)
                .count();
        Assert.assertEquals("Bad number of properties", 11, attrCount);

        checkIsAttribute(type.getProperty("brigade"), String.class);
    }


    private void checkIsAttribute(final PropertyType toCheck, final Class expectedValueClass) {
        Assert.assertNotNull("Property not defined", toCheck);
        Assert.assertTrue("Expected an attribute, but was "+ toCheck.getClass(), toCheck instanceof AttributeType);
        final AttributeType attr = (AttributeType) toCheck;
        Assert.assertTrue("Geometric attribute is not a JTS geometry", expectedValueClass.isAssignableFrom(attr.getValueClass()));
    }

    /**
     * Ensure that all given properties can be found in input feature type.
     * @param wantedProperties The properties we need to find.
     * @param toCheck The feature type to search into.
     */
    private static void checkProperties(Collection<? extends PropertyType> wantedProperties, final FeatureType toCheck) {
        for (PropertyType searched : wantedProperties) {
            try {
                final PropertyType found = toCheck.getProperty(searched.getName().toString());
                Assert.assertEquals(searched, found);
            } catch (PropertyNotFoundException e) {
                fail(e.getMessage());
            }
        }
    }
}
