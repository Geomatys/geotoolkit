/*
 *    Types - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2005-2012 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.geotoolkit.feature;

import org.opengis.util.GenericName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.geotoolkit.feature.type.*;

/**
 * A class which extends {@link AbstracSimpleFeatureFactoryTest} to test complex
 * feature creation. The class owns methods to check for complex attributes and
 * complex features creation.
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class AbstractComplexFeatureTest extends AbstractSimpleFeatureTest {

    public AbstractComplexFeatureTest(boolean validate) {
        super(validate);
    }

    /**
     * Test of createComplexAttribute method, via {@link  FeatureFactory} and
     * ensure it is valid (test access / writing).
     */
    @Test
    public void testCreateComplexAttribute() {
        final FeatureFactory FF = getFeatureFactory();
        //complex type creation
        final GenericName strName = NamesExt.create("string");
        final GenericName fName = NamesExt.create("featureTypeName");
        final AttributeType attrType = getFeatureTypeFactory().createAttributeType(strName, String.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strName, 0, Integer.MAX_VALUE, false, "line");
        final List<PropertyDescriptor> attrList = new ArrayList<PropertyDescriptor>();
        attrList.add(descriptor);
        AttributeType cmplxType = getFeatureTypeFactory().createComplexType(strName, attrList, false, false, null, null, null);

        //descriptor creation
        AttributeDescriptor descriptor2 = getFeatureTypeFactory().createAttributeDescriptor(cmplxType, strName, 0, Integer.MAX_VALUE, true, null);
        final List<AttributeDescriptor> descList = new ArrayList(1);
        descList.add(descriptor2);
        final FeatureType type = getFeatureTypeFactory().createSimpleFeatureType(fName, descList, null, false, null, null, null);

        //properties making
        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute("line1", descriptor, "id-0"));
        properties.add(FF.createAttribute("line2", descriptor, "id-1"));
        properties.add(FF.createAttribute("line3", descriptor, "id-2"));

        //object to test
        final Attribute cmplxAtr = FF.createAttribute(properties, (AttributeDescriptor) type.getDescriptor("string"), "string");
        //tests
        assertNotNull("ComplexAttribute is NULL", cmplxAtr);
        assertTrue(cmplxAtr instanceof ComplexAttribute);
        //id
        assertNotNull("The attribute id should not be null", cmplxAtr.getIdentifier());
        assertEquals("Attribute id doesn't match", "string", cmplxAtr.getIdentifier().toString());
        //type
        assertNotNull("The type is NULL", cmplxAtr.getType());
        assertEquals("The type doesn't match", cmplxType, cmplxAtr.getType());
        //value
        assertNotNull("Attribute value is NULL", cmplxAtr.getValue());
        assertEquals("Attribute value doesn't match", properties, cmplxAtr.getValue());
        //writing tests
        final Collection<Property> properties2 = new ArrayList<Property>();
        properties2.add(FF.createAttribute("line1b", descriptor, "id-0"));
        properties2.add(FF.createAttribute("line2b", descriptor, "id-1"));
        properties2.add(FF.createAttribute("line3b", descriptor, "id-2"));

        cmplxAtr.setValue(properties2);
        assertNotNull("Attribute setValue() method has not worked", cmplxAtr.getValue());
        assertEquals(properties2.size(), ((Collection) cmplxAtr.getValue()).size());
        for (int i = 0; i < properties2.size(); i++) {
            Object tmp1 = ((Property) ((ArrayList) properties2).get(i)).getValue();
            Object tmp2 = ((Property) ((ArrayList) cmplxAtr.getValue()).get(i)).getValue();
            if (!tmp1.equals(tmp2)) {
                fail("Attribute setValue() method has not worked");
            }
        }
        ComplexAttribute complex = (ComplexAttribute) cmplxAtr;

        //test insertion of a value which owns an id already referenced into the feature
        try {
            complex.getProperties().add(FF.createAttribute("lineSup", descriptor, "id-1"));
            complex.validate();
            fail("Validate() method did not detect that we have two attributes with the same identifiant");
        } catch (Exception e) {
        }

        //access to non existant name
        assertNull("Trying to access non existant value, method getProperty should have returned null", complex.getProperty("non_existant"));
        assertTrue("Trying to access non existant value, method getProperties should have returned an empty list", complex.getProperties("non_existant").isEmpty());
        GenericName tmpName = NamesExt.create("non-existant");
        assertNull("Trying to access non existant value, method getProperty should have returned null", complex.getProperty(tmpName));
        assertTrue("Trying to access non existant value, method getProperties should have returned an empty list", complex.getProperties(tmpName).isEmpty());
    }

    /**
     * Test of complex feature creation via {@link FeatureFactory} and ensure it
     * is valid (test access / writing).
     */
    @Test
    public void testCreateComplexFeature() {
        final FeatureFactory FF = getFeatureFactory();
        final GenericName name = NamesExt.create("point");
        final GenericName strName = NamesExt.create("string");
        final GenericName fName = NamesExt.create("featureTypeName");

        //types and descriptors
        final GeometryType geoType = getFeatureTypeFactory().createGeometryType(name, MockDirectPosition2D.class, null, true, false, null, null, null);
        final GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, name, 1, 1, true, null);

        final AttributeType attrType = getFeatureTypeFactory().createAttributeType(strName, String.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strName, 0, Integer.MAX_VALUE, false, "line");
        final List<PropertyDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //feature type creation
        final FeatureType type = getFeatureTypeFactory().createFeatureType(fName, descList, geoDesc, false, null, null, null);

        //attributes creation
        final Object geomValue = new MockDirectPosition2D(50, 60);

        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createGeometryAttribute(geomValue, type.getGeometryDescriptor(), "id-0", null));
        properties.add(FF.createAttribute("line1", (AttributeDescriptor) type.getDescriptor("string"), "id-1"));
        properties.add(FF.createAttribute("line2", (AttributeDescriptor) type.getDescriptor("string"), "id-2"));
        properties.add(FF.createAttribute("line3", (AttributeDescriptor) type.getDescriptor("string"), "id-3"));

        //create feature
        final Feature feature = FF.createFeature(properties, type, "id_0");

        //test it
        assertNotNull("Feature has not been set", feature);
        assertFalse(feature.getType().isSimple());
        //type
        assertNotNull("type has not been set", feature.getType());
        assertEquals("type is wrong", type, feature.getType());
        //properties
        assertNotNull(feature.getDefaultGeometryProperty());
        assertNotNull(feature.getDefaultGeometryProperty().getValue());
        assertEquals(feature.getDefaultGeometryProperty().getValue(), geomValue);
        //Envelope
        //assertNotNull("The feature bound should never be null", feature.getBounds());
        //identifier
        assertNotNull("The feature identifier should never be null", feature.getIdentifier());

        //check if access to complex property via getProperty() send back the first element
        assertNotNull(feature.getProperties("string"));
        assertNotNull(feature.getProperty("string").getValue());
        assertEquals("line1", feature.getProperty("string").getValue());

        //validation test
        try {
            feature.getProperty("string").setValue(null);
            if (validating) {
                fail("Validating Factory has not checked the value insertion");
            }
        } catch (Exception e) {
            if (!validating) {
                fail("Lenient Factory should not check for property validity");
            }
        }

        try {
            feature.validate();
            fail("The validation test should have returned an exception, cause we have a null value in a non nillable attribute");
        } catch (Exception e) {
        }

        //test insertion of a value which owns an id already referenced into the feature
        try {
            feature.getProperties().add(FF.createAttribute("line3", (AttributeDescriptor) type.getDescriptor("string"), "id-3"));
            feature.validate();
            fail("Validate() method did not detect that we have two attributes with the same identifiant");
        } catch (Exception e) {
        }

        //writing test
        final Collection<Property> properties2 = new ArrayList<Property>();
        properties2.add(FF.createGeometryAttribute(geomValue, type.getGeometryDescriptor(), "id-0", null));
        properties2.add(FF.createAttribute("line1B", (AttributeDescriptor) type.getDescriptor("string"), "id-1"));
        properties2.add(FF.createAttribute("line2B", (AttributeDescriptor) type.getDescriptor("string"), "id-2"));
        properties2.add(FF.createAttribute("line3B", (AttributeDescriptor) type.getDescriptor("string"), "id-3"));

        feature.setValue(properties2);

        assertTrue("feature setValue() method has not worked", properties2.size() == feature.getProperties().size());

        Property[] tmp = new Property[properties2.size()];
        feature.getProperties().toArray(tmp);
        for (int i = 0; i < properties2.size(); i++) {
            assertEquals("feature setValue() method has not worked", ((Property) ((ArrayList) properties2).get(i)).getValue(), tmp[i].getValue());
        }

        //access test (to non existant attribute)
        assertNull("Trying to access non existant value, method getProperty should have returned null", feature.getProperty("non_existant"));
        assertTrue("Trying to access non existant value, method getProperties should have returned an empty list", feature.getProperties("non_existant").isEmpty());
        GenericName tmpName = NamesExt.create("non-existant");
        assertNull("Trying to access non existant value, method getProperty should have returned null", feature.getProperty(tmpName));
        assertTrue("Trying to access non existant value, method getProperties should have returned an empty list", feature.getProperties(tmpName).isEmpty());
    }
}
