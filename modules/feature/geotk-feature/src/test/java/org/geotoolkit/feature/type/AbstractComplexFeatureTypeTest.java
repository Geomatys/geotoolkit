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
package org.geotoolkit.feature.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.feature.MockCRS;
import org.geotoolkit.feature.MockDirectPosition2D;
import org.geotoolkit.feature.DefaultName;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A class which extends from {@link FeatureTypeFactoryTest} to test a {@link FeatureTypeFactory}
 * for complex features type creation. Test creation of complex
 * attributes/feature types
 *
 * @author Alexis MANIN (geomatys)
 */
public abstract class AbstractComplexFeatureTypeTest extends AbstractSimpleFeatureTypeTest {

    /**
     * Test for {@link ComplexType} creation. Check for name, descriptors,
     * identifier, abstract level and parent.
     */
    @Test
    public void testCreateComplexType() {
        final Name name = new DefaultName("point");
        final Name strName = new DefaultName("String");
        final Name ctName = new DefaultName("complexTypeName");
        final Map crsInfo = new HashMap<String, String>();
        crsInfo.put("name", "myCRS");
        crsInfo.put("first", "one");
        crsInfo.put("second", "two");
        final CoordinateReferenceSystem curCRS = new MockCRS(crsInfo);
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        //types and descriptors
        final GeometryType geoType = FTF.createGeometryType(name, MockDirectPosition2D.class, curCRS, true, true, null, null, null);
        final GeometryDescriptor geoDesc = FTF.createGeometryDescriptor(geoType, name, 1, 1, true, null);
        final AttributeType attrType = FTF.createAttributeType(strName, String.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = FTF.createAttributeDescriptor(attrType, strName, 0, Integer.MAX_VALUE, false, "line");
        final AttributeDescriptor desc_doublon = FTF.createAttributeDescriptor(attrType, strName, 0, Integer.MAX_VALUE, false, "line");
        final List<PropertyDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);
        descList.add(desc_doublon);

        //create object to test, should raise an exception cause we have two descriptors with the same name
        ComplexType res;
        try {
            res = FTF.createComplexType(ctName, descList, true, false, null, attrType, descriptionSample);
            fail("We can't create a complexType with multiple descriptors with the same name");
        } catch (Exception e) {

        }
        descList.remove(desc_doublon);
        res = FTF.createComplexType(ctName, descList, true, false, null, attrType, descriptionSample);

        //tests
        assertNotNull("Complex type is null", res);
        //Name
        assertNotNull("Name not set", res.getName());
        assertEquals("Name does not match", ctName, res.getName());
        //descriptor
        assertNotNull("Descriptors have not been writen into the complex type", res.getDescriptors());
        assertEquals("Descriptors are not properly set", geoDesc, res.getDescriptor(name));
        assertEquals("Descriptors are not properly set", descriptor, res.getDescriptor(strName));
        //identifier
        assertTrue("The complexType should be identified", res.isIdentified());
        //abstraction
        assertFalse("ComplexType marked as abstract while it's not", res.isAbstract());
        //parent
        assertNotNull("ComplexType parent has not been set", res.getSuper());
        assertEquals("ComplexType parent has been wrong set", attrType, res.getSuper());
        //description
        assertNotNull("Attribute description is NULL", res.getDescription());
        assertEquals("Attribute description is wrong", descriptionSample.toString(), res.getDescription().toString());
        //Binding
        assertNotNull("Binding has not been set for this complexType", res.getBinding());
        //assertTrue(res.getBinding().class == (Collection<Property>.class));

        //check for access to non-existant descriptor
        try {
            res.getDescriptor("non-existant");
        } catch (Exception e) {
        }
        Name tmpName = new DefaultName("non-existant");
        try {
            res.getDescriptor(tmpName);
        } catch (Exception e) {
        }
    }

    /**
     * Test for {@link FeatureType} creation. Check for name, descriptors,
     * identifier, abstract level and parent.
     */
    @Test
    public void createFeatureType() {
        final Name name = new DefaultName("point");
        final Name strName = new DefaultName("String");
        final Name fName = new DefaultName("featureTypeName");
        final FeatureTypeFactory FTF = getFeatureTypeFactory();

        //types and descriptors
        final GeometryType geoType = FTF.createGeometryType(name, MockDirectPosition2D.class, null, true, false, null, null, null);
        final GeometryDescriptor geoDesc = FTF.createGeometryDescriptor(geoType, name, 1, 1, true, null);
        final AttributeType attrType = FTF.createAttributeType(strName, String.class, true, false, null, null, null);
        final AttributeDescriptor descriptor = FTF.createAttributeDescriptor(attrType, strName, 0, Integer.MAX_VALUE, false, "line");
        final List<AttributeDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //feature type creation
        final FeatureType res = FTF.createSimpleFeatureType(fName, descList, geoDesc, false, null, attrType, descriptionSample);

        assertNotNull("FeatureType has not been set", res);
        //Name
        assertNotNull("FeatureType name has not been set", res.getName());
        assertEquals("Feature type name is not properly set", res.getName(), fName);
        //descriptor
        assertNotNull("Descriptors are not set", res.getDescriptors());
        assertNotNull("Geometry descriptor is not set", res.getGeometryDescriptor());
        assertEquals("Feature descriptors are not properly set", res.getDescriptor(name), geoDesc);
        assertEquals("Feature descriptors are not properly set", res.getDescriptor(strName), descriptor);
        //abstraction
        assertFalse("Feature type set as abstracted while it's not", res.isAbstract());
        //parent
        assertNotNull("ComplexType parent has not been set", res.getSuper());
        assertEquals("ComplexType parent has been wrong set", attrType, res.getSuper());
        //description
        assertNotNull("Attribute description is NULL", res.getDescription());
        assertEquals("Attribute description is wrong", descriptionSample.toString(), res.getDescription().toString());
        //check for access to non-existant descriptor
        try {
            res.getDescriptor("non-existant");
        } catch (Exception e) {
        }
        Name tmpName = new DefaultName("non-existant");
        try {
            res.getDescriptor(tmpName);
        } catch (Exception e) {
        }


    }
}
