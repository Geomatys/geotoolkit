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

import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * A class to test methods from a FeatureFactory, only supports simple features.
 * A boolean must be given to the constructor to say if we test a validating
 * featureFactory (boolean to true) or not (boolean to false). The class tests
 * simple/geometry attribute creation, as association and simple feature
 * creation.
 *
 * @author Alexis MANIN (geomatys)
 */
public abstract class AbstractSimpleFeatureTest {

    /**
     * A boolean to tell if we test a validating or a lenient factory.
     */
    protected final boolean validating;

    public AbstractSimpleFeatureTest(boolean validating) {
        this.validating = validating;
    }

    /**
     * A function which return the current used feature factory
     *
     * @return the feature factory to use for tests
     */
    public abstract FeatureFactory getFeatureFactory();

    /**
     * A function which return the current used feature type factory
     *
     * @return the feature type factory to use for tests
     */
    public abstract FeatureTypeFactory getFeatureTypeFactory();

    /**
     * Test of Attribute creation via FeatureFactory and ensure it is valid
     * (test access / writing).
     */
    @Test
    public void testCreateSimpleAttribute() {
        final FeatureFactory FF = getFeatureFactory();
        final Object value = new MockDirectPosition2D(50, 60);
        final Name nm = new DefaultName("point");
        final Name finalNm = new DefaultName("pointAsso");
        final String id = "id-0";
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        final AttributeType type = FTF.createAttributeType(nm, MockDirectPosition2D.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = FTF.createAttributeDescriptor(type, finalNm, 1, 1, false, new MockDirectPosition2D(0, 0));

        final Attribute attr = FF.createAttribute(value, descriptor, id);
        //tests
        assertNotNull("Atrtribute has not been built", attr);
        assertFalse("Generated attribute is complex type. Simple expected", attr instanceof ComplexAttribute);
        //Name
        assertNotNull("Name has not been set", attr.getName());
        assertEquals("Attribute name doesn't match", finalNm, attr.getName());
        //Value
        assertNotNull("Attribute has not been set", attr.getValue());
        assertEquals("Attribute value doesn't match", value, attr.getValue());
        //Type
        assertNotNull("Attribute type has not been set", attr.getType());
        assertEquals("Attribute type does not match", type, attr.getType());
        //identifier
        assertNotNull("Identifier has not been set", attr.getIdentifier());
        assertEquals("Identifier is not correct", id, attr.getIdentifier().toString());
        //Nillable
        assertFalse("The data should not be nillable", attr.isNillable());
        //writing test
        attr.setValue(new MockDirectPosition2D(0, 0));
        assertNotNull("setValue() method has not worked", attr.getValue());
        assertEquals("setValue() method has not worked", new MockDirectPosition2D(0, 0), attr.getValue());

    }

    /**
     * Test of GeometryAttribute creation via FeatureFactory and ensure it is
     * valid (test access / writing).
     */
    @Test
    public void testCreateGeometryAttribute() {
        final FeatureFactory FF = getFeatureFactory();
        final Object value = new MockDirectPosition2D(50, 60);
        final Name nm = new DefaultName("point");
        final String id = "id-0";
        final Map crsInfo = new HashMap<String, String>();
        crsInfo.put("name", "myCRS");
        crsInfo.put("first", "one");
        crsInfo.put("second", "two");
        final CoordinateReferenceSystem curCRS = new MockCRS(crsInfo);
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        final GeometryType type = FTF.createGeometryType(nm, MockDirectPosition2D.class, curCRS, true, false, null, null, null);
        final GeometryDescriptor desc = FTF.createGeometryDescriptor(type, nm, 1, 1, false, null);

        final GeometryAttribute attr = FF.createGeometryAttribute(value, desc, id, curCRS);
        assertNotNull("Atrtribute has not been built", attr);
        assertTrue("Generated attribute is not of type GeometryAttribute", attr instanceof GeometryAttribute);
        //Name
        assertNotNull("Name has not been set", attr.getName());
        assertEquals("Attribute name doesn't match", nm, attr.getName());
        //Value
        assertNotNull("Geometry has not been set", attr.getValue());
        assertEquals("Attribute value doesn't match", value, attr.getValue());
        //Type
        assertNotNull("Geometry type has not been set", attr.getType());
        assertEquals("Geometry type does not match", type, attr.getType());
        //identifier
        assertNotNull("Identifier has not been set", attr.getIdentifier());
        assertEquals("Identifier is not correct", id, attr.getIdentifier().toString());
        //Nillable
        assertFalse("The data should not be nillable", attr.isNillable());
        //bounding
        //assertNotNull("Geometry envelope should never be null", attr.getBounds());
        //writing test
        attr.setValue(new MockDirectPosition2D(0, 0));
        assertNotNull("setValue() method has not worked", attr.getValue());
        assertEquals("setValue() method has not worked", new MockDirectPosition2D(0, 0), attr.getValue());
    }

    /**
     * Test of Association creation via FeatureFactory and ensure it is valid
     * (test access / writing).
     */
    @Test
    public void testCreateAssociation() {
        //initialisation
        final FeatureFactory FF = getFeatureFactory();
        final Name nm = new DefaultName("point");
        final Name finalNm = new DefaultName("pointAsso");
        final String id = "id-0";
        final Object value = new MockDirectPosition2D(50, 60);
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        //prepare association creation
        final AttributeType type = FTF.createAttributeType(nm, MockDirectPosition2D.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = FTF.createAttributeDescriptor(type, nm, 1, 1, false, new MockDirectPosition2D(0, 0));
        final Attribute attr = FF.createAttribute(value, descriptor, id);
        final AssociationType asType = FTF.createAssociationType(nm, type, false, null, null, null);
        final AssociationDescriptor asDescriptor = FTF.createAssociationDescriptor(asType, finalNm, 1, 1, false);

        //create object to test
        final Association asso = FF.createAssociation(attr, asDescriptor);

        //Tests
        assertNotNull("Association has not been set", asso);
        //name
        assertNotNull("Association name has not been set", asso.getName());
        assertEquals("Association name does not match", asso.getName(), finalNm);
        //value
        assertNotNull("Association value has not been set", asso.getValue());
        assertEquals("Association value does not match", attr, asso.getValue());
        //type
        assertNotNull("Association type has not been set", asso.getType());
        assertEquals("Association type does not match", asType, asso.getType());
        //descriptor
        assertNotNull("Association descriptor has not been set", asso.getDescriptor());
        assertEquals("Association descriptor does not match", asDescriptor, asso.getDescriptor());
        //attribute type
        assertNotNull("Attribute type has not been set", asso.getRelatedType());
        assertEquals("Attribute is not correct", type, asso.getRelatedType());
        //writing test
        final Object newValue = new MockDirectPosition2D(51, 51);
        final Attribute attr2 = FF.createAttribute(newValue, descriptor, "id-1");
        asso.setValue(attr2);
        assertNotNull("Association setValue() method has not worked", asso.getValue());
        assertEquals("Association setValue() method has not worked", attr2, asso.getValue());
    }

    /**
     * Test of simple feature creation via FeatureFactory and ensure the feature
     * is valid (test access / writing).
     *
     */
    @Test
    public void testCreateSimpleFeature() {
        final FeatureFactory FF = getFeatureFactory();
        final Name nm = new DefaultName("point");
        final Name strNm = new DefaultName("String");
        Object geomValue = new MockDirectPosition2D(50, 60);
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        //types and descriptors
        final GeometryType geoType = FTF.createGeometryType(nm, MockDirectPosition2D.class, null, true, false, null, null, null);
        final GeometryDescriptor geoDesc = FTF.createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        final AttributeType type = FTF.createAttributeType(strNm, String.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = FTF.createAttributeDescriptor(type, strNm, 1, 1, false, "line");
        final List<AttributeDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //properties
        final Collection<Property> properties = new ArrayList<Property>();
        GeometryAttribute geomAttr = FF.createGeometryAttribute(geomValue, geoDesc, "id-geom", null);
        properties.add(FF.createAttribute("line1", descriptor, "id_test"));
        properties.add(geomAttr);
        Attribute same_id_attr = FF.createAttribute("line1", descriptor, "id_test");

        //feature type creation
        final FeatureType fType = FTF.createSimpleFeatureType(nm, descList, geoDesc, false, null, null, null);

        //feature creation, check for insertion of same id values.
        final Feature feature = FF.createFeature(properties, fType, "id_0");
        feature.setDefaultGeometryProperty(geomAttr);

        //Tests
        assertNotNull("feature is NULL", feature);
        assertTrue(feature instanceof SimpleFeature);
        SimpleFeature simpleFeature = (SimpleFeature) feature;
        //Name
        assertNotNull("Name is NULL", feature.getIdentifier());
        assertEquals("Name is not right", "id_0", feature.getIdentifier().toString());
        //type
        assertNotNull("The feature type should never be null", feature.getType());
        assertEquals("The feature type is not right", fType, feature.getType());
        //Value
        assertNotNull(feature.getProperty("String"));
        assertNotNull(feature.getDefaultGeometryProperty());
        assertEquals("line1", feature.getProperty("String").getValue());
        assertEquals(geomValue, feature.getDefaultGeometryProperty().getValue());
        //Envelope
//        assertNotNull("The feature bound should never be null", feature.getBounds());
        //identifier
        assertNotNull("The feature identifier should never be null", feature.getIdentifier());
        assertEquals("Identifier is wrong", "id_0", feature.getIdentifier().toString());

        //validation
        try {
            simpleFeature.setAttribute(strNm, null);
            if (validating) {
                fail("Validating Factory has not checked the value insertion");
            }

        } catch (Exception e) {
            if (!validating) {
                fail("Lenient Factory should not check for property validity");
            }
        }

        if (!validating) {
            try {
                feature.validate();
                fail("The validation test should have returned an exception cause we have a null value in a non nillable attribute");
            } catch (Exception e) {
            }
        }

        //insert a value whose id is already referenced
        try {
            feature.getProperties().add(same_id_attr);
            feature.validate();
            fail("Validate() method did not detect that we have two attributes with the same identifiant");
        } catch (Exception e) {
        }
        feature.getProperties().remove(same_id_attr);

        //writing tests
        feature.getProperty(nm).setValue(new MockDirectPosition2D(13, 37));
        assertNotNull("Property setValue() method has not worked", feature.getProperty(nm).getValue());
        assertEquals("Property setValue() method has not worked", new MockDirectPosition2D(13, 37), feature.getProperty(nm).getValue());

        geomAttr = FF.createGeometryAttribute(new MockDirectPosition2D(0, 0), geoDesc, "Geo", null);
        feature.setDefaultGeometryProperty(geomAttr);
        assertNotNull("setDefaultGeometryProperty() has not worked", feature.getDefaultGeometryProperty());
        assertEquals("setDefaultGeometryProperty() has not worked", new MockDirectPosition2D(0, 0), feature.getDefaultGeometryProperty().getValue());

        geomValue = new MockDirectPosition2D(19, 19);
        simpleFeature.setDefaultGeometry(geomValue);
        assertNotNull("setDefaultGeometry() method has not worked", feature.getDefaultGeometryProperty());
        assertEquals("setDefaultGeometry() method has not worked", geomValue, feature.getDefaultGeometryProperty().getValue());

        simpleFeature.setAttribute("String", "lineTest");
        assertNotNull("setAttribute() method has not worked", simpleFeature.getAttribute("String"));
        assertEquals("setDefaultGeometry() method has not worked", "lineTest", simpleFeature.getAttribute("String"));
        assertNotNull("setAttribute() method has not worked", simpleFeature.getAttribute(strNm));
        assertEquals("setDefaultGeometry() method has not worked", "lineTest", simpleFeature.getAttribute(strNm));

        //insertion via a list
        final List<Object> insert = new ArrayList<Object>(2);
        insert.add("insertion");
        insert.add(new MockDirectPosition2D(1337, 1337));
        simpleFeature.setAttributes(insert);
        assertNotNull("setAttributes() method has not worked", simpleFeature.getAttributes());
        assertArrayEquals("setAttributes() method has not worked", insert.toArray(), simpleFeature.getAttributes().toArray());
        //insertion via an array
        insert.clear();
        insert.add("insertion2");
        insert.add(new MockDirectPosition2D(0, 0));
        simpleFeature.setAttributes(insert.toArray());
        assertNotNull("setAttributes() method has not worked", simpleFeature.getAttributes());
        assertArrayEquals("setAttributes() method has not worked", insert.toArray(), simpleFeature.getAttributes().toArray());

        //check that if we try to get or set an attribute which does not exist, it returns null.
        assertNull(simpleFeature.getAttribute("non_existant"));

        try {
            simpleFeature.setAttribute("non_existant", "value");
            fail("No exception catched for bad name parameter on method setAttribute()");
        } catch (Exception e) {
        }
        assertNull("Trying to access non existant value, method getProperty should have returned null", feature.getProperty("non_existant"));
        Name tmpName = new DefaultName("non-existant");
        assertNull("Trying to access non existant value, method getProperty should have returned null", feature.getProperty(tmpName));
    }
}
