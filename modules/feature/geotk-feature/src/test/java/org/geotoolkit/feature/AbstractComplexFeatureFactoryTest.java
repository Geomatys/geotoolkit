/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.geometry.DirectPosition2D;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.*;

/**
 * A class which extends {@link AbstracSimpleFeatureFactoryTest} to test complex feature creation.
 * The class owns methods to check for complex attributes and complex features creation.
 * @author Alexis Manin
 */
public abstract class AbstractComplexFeatureFactoryTest extends AbstractSimpleFeatureFactoryTest /*
 * implements FeatureFactoryTest
 */ {

    public AbstractComplexFeatureFactoryTest(boolean validate) {
        super(validate);
    }

    /**
     * Test of createComplexAttribute method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateComplexAttribute() {
        System.out.println("ComplexAttribute creation test");

        //complex type creation
        Name strNm = new MocName("string");
        Name fNm = new MocName("featureTypeName");
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(strNm, String.class, false, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strNm, 0, Integer.MAX_VALUE, false, "line");        
        List<PropertyDescriptor> attrList = new ArrayList<PropertyDescriptor>();
        attrList.add(descriptor);
        AttributeType cmplxType = getFeatureTypeFactory().createComplexType(strNm, attrList, validating, validating, null, null, null);
        
        //descriptor creation
        AttributeDescriptor descriptor2 = getFeatureTypeFactory().createAttributeDescriptor(cmplxType, strNm, 0, Integer.MAX_VALUE, true, null);
        final List<AttributeDescriptor> descList = new ArrayList(1);
        descList.add(descriptor2);
        FeatureType type = getFeatureTypeFactory().createSimpleFeatureType(fNm, descList, null, false, null, null, null);
        
        //properties making
        final Collection<Property> properties = new ArrayList<Property>();
        
        List line1 = new ArrayList<String>();
        line1.add("this ");
        line1.add("is ");
        line1.add("a ");
        line1.add("line.");
        
        List line2 = new ArrayList<String>();
        line2.add("here ");
        line2.add("is ");
        line2.add("another ");
        line2.add("one.");
        
        List line3 = new ArrayList<String>();
        line3.add("the ");
        line3.add("last ");
        line3.add("but ");
        line3.add("not the least.");
        properties.add(getFeatureFactory().createAttribute(line1, (AttributeDescriptor) type.getDescriptor("string"), null));
        properties.add(getFeatureFactory().createAttribute(line2, (AttributeDescriptor) type.getDescriptor("string"), null));
        properties.add(getFeatureFactory().createAttribute(line3, (AttributeDescriptor) type.getDescriptor("string"), null));

        Attribute cmplxAtr = getFeatureFactory().createAttribute(properties, (AttributeDescriptor) type.getDescriptor("string"), "string");
        assertTrue(cmplxAtr instanceof ComplexAttribute);
        assertEquals("Attribute id doesn't match", "string", cmplxAtr.getIdentifier().toString());
        assertEquals("Attribute value doesn't match", properties, cmplxAtr.getValue());
    }

    /**
     * Test of complex feature creation via createFeature method, of class
     * AbstractFeatureFactory.
     */
    @Test
    public void testCreateComplexFeature() {
        Name nm = new MocName("point");
        Name strNm = new MocName("string");
        Name fNm = new MocName("featureTypeName");
        
        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D .class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strNm, 0, Integer.MAX_VALUE, false, "line");
        final List<PropertyDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //feature type creation
        FeatureType type = getFeatureTypeFactory().createFeatureType(fNm, descList, geoDesc, false, null, null, null);
        
        //attributes creation
        Object geomValue = new DirectPosition2D(50, 60);

        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(getFeatureFactory().createGeometryAttribute(geomValue, type.getGeometryDescriptor(), "id-0", null));
        properties.add(getFeatureFactory().createAttribute("line1", (AttributeDescriptor) type.getDescriptor("string"), "id-1"));
        properties.add(getFeatureFactory().createAttribute("line2", (AttributeDescriptor) type.getDescriptor("string"), "id-2"));
        properties.add(getFeatureFactory().createAttribute("line3", (AttributeDescriptor) type.getDescriptor("string"), "id-3"));
        //create feature
        final Feature feature = getFeatureFactory().createFeature(properties, type, "id_0");

        //test it
        assertFalse(feature instanceof SimpleFeature);
        assertNotNull(feature.getDefaultGeometryProperty());
        assertNotNull(feature.getDefaultGeometryProperty().getValue());
        assertEquals(feature.getDefaultGeometryProperty().getValue(), geomValue);

        //check if access to complex property via getProperty() send back the first element
        assertNotNull(feature.getProperties("string"));
        assertNotNull(feature.getProperty("string").getValue());
        assertEquals("line1", feature.getProperty("string").getValue());

        //validation test is disabled
        
//        try {
//            feature.getProperty("String").setValue(null);
//            if (validating) {
//                fail("Validating Factory has not checked the value insertion");
//            }
//        } catch (Exception e) {
//            if (!validating) {
//                fail("Lenient Factory should not check for property validity");
//            }
//        }
    }
}
