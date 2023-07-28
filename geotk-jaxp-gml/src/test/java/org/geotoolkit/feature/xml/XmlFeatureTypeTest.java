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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.test.feature.FeatureComparator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.IllegalNameException;
import static org.geotoolkit.feature.xml.GMLConvention.NILLABLE_CHARACTERISTIC;
import static org.geotoolkit.feature.xml.XmlTestData.*;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import static org.geotoolkit.storage.feature.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xml.DomCompare;
import org.junit.*;
import static org.junit.Assert.*;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.xml.sax.SAXException;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class XmlFeatureTypeTest {

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
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        FeatureComparator comparator = new FeatureComparator(simpleTypeFull, types.get(0));
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.compare();
    }

    @Test
    public void testReadSimpleGeomFeatureType() throws JAXBException {

        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleGeomType.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        FeatureComparator comparator = new FeatureComparator(simpleTypeGeom, types.get(0));
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.compare();
    }

    @Test
    public void testReadSimpleFeatureTypeWithAny() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAny.xsd")).getValues());
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
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithRestriction.xsd")).getValues());
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
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        Collection<? extends PropertyType> expProperties = multiGeomType.getProperties(false);
        Collection<? extends PropertyType> resProperties = types.get(0).getProperties(false);
        assertEquals(diffSizeProperties(expProperties, resProperties), expProperties.size(), resProperties.size());

        FeatureComparator comparator = new FeatureComparator(multiGeomType, types.get(0));
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.compare();
    }

    @Test
    public void testReadWfsFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/wfs1.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        final String ns = "http://mapserver.gis.umn.edu/mapserver";
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(ns,"quadrige");
        ftb.setSuperTypes(GMLConvention.ABSTRACTFEATURETYPE_31);
        AttributeTypeBuilder atb= ftb.addAttribute(Geometry.class).setName(ns, "msGeometry").setMinimumOccurs(0).setMaximumOccurs(1);
        atb.addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        atb.addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName(ns, "C_SIEPT38").setMinimumOccurs(1).setMaximumOccurs(1);
        ftb.addAttribute(String.class).setName(ns, "L_SIEPT").setMinimumOccurs(1).setMaximumOccurs(1);
        final FeatureType wfsType = ftb.build();

        FeatureComparator comparator = new FeatureComparator(wfsType, types.get(0));
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("http://www.w3.org/2001/XMLSchema-instance:@nil");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.compare();
    }

    @Test
    public void testReadComplexFeatureType() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());

        Collection<? extends PropertyType> expProperties = complexType.getProperties(false);
        Collection<? extends PropertyType> resProperties = types.get(0).getProperties(false);
        assertEquals(diffSizeProperties(expProperties, resProperties), expProperties.size(), resProperties.size());

        FeatureComparator comparator = new FeatureComparator(complexType, types.get(0));
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("http://www.w3.org/2001/XMLSchema-instance:@nil");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.ignoreDescription = true;
        comparator.compare();
    }

    @Test
    @Ignore("Random test failure due to connection timeout or when internet is not available. See #GEOTK-485")
    public void testReadVeryComplexFeatureType() throws Exception {
        final JAXBFeatureTypeReader reader = getReader(true);
        final URL url = new URL("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        final List<FeatureType> types = new ArrayList<>(reader.read(url).getValues());
        removeGMLBaseTypes(types);
        System.out.println("RESULT:" + types);
    }

    @Test
    public void testReadPropertyGroupFeatureType() throws JAXBException, IllegalNameException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final GenericNameIndex<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/propertyGroupType.xsd"));

        FeatureType type = types.get("SF_SamplingFeature");

        assertNotNull(type);
        assertEquals(7, type.getProperties(true).size());

    }

    @Test
    public void testReadInheritType() throws JAXBException, IllegalNameException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final GenericNameIndex<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/inheritType.xsd"));

        FeatureType type = types.get("SF_SpatialSamplingFeature");

        assertNotNull(type);
        assertEquals(8, type.getProperties(true).size());

    }

    @Test
    public void testAttributesType() throws JAXBException, IllegalNameException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final GenericNameIndex<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeWithAttribute.xsd"));

        FeatureType type = types.get("TestSimple");
        assertNotNull(type);
        assertEquals(typeWithAtts, type);
    }

    @Test
    public void testReadSimpleFeatureEmpty() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType readType = types.get(0);

        FeatureComparator comparator = new FeatureComparator(typeEmpty, readType);
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.ignoreDescription = true;
        comparator.compare();
    }

    @Test
    public void testReadSimpleFeatureEmpty2() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(false);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleTypeEmpty.xsd")).getValues());
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
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithNil.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType readType = types.get(0);

        FeatureComparator comparator = new FeatureComparator(typeWithNil, readType);
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.compare();
    }

    @Test
    public void testReadGroupChoice() throws JAXBException, IllegalNameException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final GenericNameIndex<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/GroupChoiceMinMaxType.xsd"));
        assertEquals(5, types.getNames().size());
        FeatureType dataset  = types.get("dataset");

        assertEquals("http://www.iho.int/S-121:dataset", dataset.getName().toString());

        //check we should have 4 properties, sis:id, gml:id, and the two group choices
        final PropertyType[] properties = dataset.getProperties(true).toArray(new PropertyType[0]);
        assertEquals("sis:identifier", properties[0].getName().toString());
        assertEquals("http://www.opengis.net/gml/3.2:@id", properties[1].getName().toString());
        assertEquals("http://www.iho.int/S-121:Right", properties[2].getName().toString());
        assertEquals("http://www.iho.int/S-121:Source", properties[3].getName().toString());
        assertTrue(properties[2] instanceof FeatureAssociationRole);
        assertTrue(properties[3] instanceof FeatureAssociationRole);
        FeatureAssociationRole far1 = (FeatureAssociationRole) properties[2];
        FeatureAssociationRole far2 = (FeatureAssociationRole) properties[3];
        assertEquals("http://www.iho.int/S-121:Right", far1.getValueType().getName().toString());
        assertEquals("http://www.iho.int/S-121:Source", far2.getValueType().getName().toString());
        //check min max occurs
        assertEquals(0, far1.getMinimumOccurs());
        assertEquals(Integer.MAX_VALUE, far1.getMaximumOccurs());
        assertEquals(0, far2.getMinimumOccurs());
        assertEquals(Integer.MAX_VALUE, far2.getMaximumOccurs());
    }

    @Test
    public void testReadTypeWithSubstitutions() throws JAXBException {
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/TypeWithSubstitution.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType type = types.get(0);
        assertEquals("TestSimple", type.getName().tip().toString());
        //we do not count the substitution groups
        assertEquals(5, type.getProperties(true).size());
    }

    @Test
    public void testReadReferenceType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final JAXBFeatureTypeReader reader = getReader(true);
        final List<FeatureType> types = new ArrayList<>(reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ReferenceType.xsd")).getValues());
        removeGMLBaseTypes(types);
        assertEquals(1, types.size());
        final FeatureType readType = types.get(0);

        FeatureComparator comparator = new FeatureComparator(typeReference, readType);
        comparator.ignoredCharacteristics.add("http://www.w3.org/1999/xlink:href");
        comparator.ignoredCharacteristics.add("mapping");
        comparator.ignoreDescription = true;
        comparator.compare();
    }

    @Test
    public void testWriteSimpleFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        StringWriter sw = new StringWriter();
        writer.write(simpleTypeFull, sw);

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), sw.toString());
    }

    @Test
    public void testWriteMultiGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final StringWriter sw = new StringWriter();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(multiGeomType, sw);

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"), sw.toString());
    }

    @Test
    public void testWriteSimpleGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final StringWriter sw = new StringWriter();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(simpleTypeGeom, sw);

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleGeomType.xsd"), sw.toString());
    }


    @Test
    public void testWriteComplexFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final StringWriter sw = new StringWriter();
        final JAXBFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(complexType, sw);

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"), sw.toString());
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

//    /**
//     * Ensure that all given properties can be found in input feature type.
//     * @param wantedProperties The properties we need to find.
//     * @param toCheck The feature type to search into.
//     */
//    private static void checkProperties(Collection<? extends PropertyType> wantedProperties, final FeatureType toCheck) {
//        for (PropertyType searched : wantedProperties) {
//            try {
//                final PropertyType found = toCheck.getProperty(searched.getName().toString());
//                Assert.assertEquals(searched, found);
//            } catch (PropertyNotFoundException e) {
//                fail(e.getMessage());
//            }
//        }
//    }

    private static String diffSizeProperties(Collection<? extends PropertyType> expProperties, Collection<? extends PropertyType> resProperties) {
        StringBuilder sb = new StringBuilder();
        if (expProperties.size() > resProperties.size()) {
            sb.append("Missing properties:\n");
            for (PropertyType expProp : expProperties) {
                boolean found = false;
                for (PropertyType resProp : resProperties) {
                    if (resProp.getName().equals(expProp.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    sb.append(" - ").append(expProp.getName()).append('\n');
                }
            }
        } else {
            sb.append("Additional properties:\n");
            for (PropertyType resProp : resProperties) {
                boolean found = false;
                for (PropertyType expProp : expProperties) {
                    if (resProp.getName().equals(expProp.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    sb.append(" - ").append(resProp.getName()).append('\n');
                }
            }
        }
        return sb.toString();
    }
    private static String diffProperties(Collection<? extends PropertyType> expProperties, Collection<? extends PropertyType> resProperties) {
        StringBuilder sb = new StringBuilder();
        sb.append("Different properties:\n");
        for (PropertyType expProp : expProperties) {
            for (PropertyType resProp : resProperties) {
                if (resProp.getName().equals(expProp.getName())) {
                    if (!resProp.equals(expProp)) {
                        String resatt = "";
                        String expatt = "";
                        if (resProp instanceof AttributeType) {
                            AttributeType att = (AttributeType)resProp;
                            resatt = " characteristics:" + att.characteristics().toString();
                        }
                        if (expProp instanceof AttributeType) {
                            AttributeType att = (AttributeType)expProp;
                            expatt = " characteristics:" + att.characteristics().toString();
                        }
                        sb.append("expected:").append(expProp).append(expatt).append(" but was:").append(resProp).append(resatt).append("\n");
                    }
                }
            }

        }
        return sb.toString();
    }
}
