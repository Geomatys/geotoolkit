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
import org.opengis.feature.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.*;

/**
 * A class to test methods from a FeatureFactory, only supports simple features.
 * A boolean must be given to the constructor to say if we test a validating featureFactory (boolean to true) or not (boolean to false).
 * The class tests simple/geometry attribute creation, as association and simple feature creation.
 * @author Alexis MANIN
 */
public abstract class AbstractSimpleFeatureFactoryTest {

    /**
     * A boolean to tell if we test a validating or a lenient factory.
     */
    protected final boolean validating;

    public AbstractSimpleFeatureFactoryTest(boolean validating) {
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
     * Test of Association creation method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateAssociation() {
        //initialisation
        System.out.println("Association creation test");
        Name nm = new MocName("point");
        Name finalNm = new MocName("pointAsso");
        String id = "id-0";
        Object value = new DirectPosition2D(50, 60);

        //prepare association creation
        AttributeType type = getFeatureTypeFactory().createAttributeType(nm, DirectPosition2D.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(type, nm, 1, 1, false, new DirectPosition2D(0, 0));
        Attribute attr = getFeatureFactory().createAttribute(value, descriptor, id);
        AssociationType asType = getFeatureTypeFactory().createAssociationType(nm, type, false, null, null, null);
        AssociationDescriptor asDescriptor = getFeatureTypeFactory().createAssociationDescriptor(asType, finalNm, 1, 1, false);
        Association asso = getFeatureFactory().createAssociation(attr, asDescriptor);

        //Tests
        assertEquals("Association name does not match", asso.getName(), finalNm);
        assertEquals("Association value does not match", asso.getValue(), attr);
        assertEquals("Association type does not match", asso.getType(), asType);
        assertEquals("Association descriptor does not match", asso.getDescriptor(), asDescriptor);
    }

    /**
     * Test of Attribute creation method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateSimpleAttribute() {
        System.out.println("Simple Attribute creation test");
        Object value = new DirectPosition2D(50, 60);
        Name nm = new MocName("point");
        Name finalNm = new MocName("pointAsso");
        String id = "id-0";

        AttributeType type = getFeatureTypeFactory().createAttributeType(nm, DirectPosition2D.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(type, finalNm, 1, 1, false, new DirectPosition2D(0, 0));
        Attribute attr = getFeatureFactory().createAttribute(value, descriptor, id);

        assertFalse("Generated attribute is complex type. Simple expected", attr instanceof ComplexAttribute);
        assertEquals("Attribute name doesn't match", attr.getName(), finalNm);
        assertEquals("Attribute value doesn't match", attr.getValue(), value);
        assertEquals("Attribute type does not match", attr.getType(), type);
    }

    /**
     * Test of GeometryAttribute creation method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateGeometryAttribute() {
        System.out.println("GeometryAttribute creation test");
        Object value = new DirectPosition2D(50, 60);
        Name nm = new MocName("point");
        String id = "id-0";

        GeometryType type = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor desc = getFeatureTypeFactory().createGeometryDescriptor(type, nm, 1, 1, true, null);

        Attribute attr = getFeatureFactory().createAttribute(value, desc, id);

        assertTrue("Generated attribute is not of type GeometryAttribute", attr instanceof GeometryAttribute);
        assertEquals("Attribute name doesn't match", attr.getName(), nm);
        assertEquals("Attribute value doesn't match", attr.getValue(), value);
        assertEquals("Attribute type does not match", attr.getType(), type);
    }

    /**
     * Test of simple feature creation via createFeature method, of class
     * AbstractFeatureFactory.
     */
    @Test
    public void testCreateSimpleFeature() {
        System.out.println("SimpleFeature creation test");
        Name nm = new MocName("point");
        Name strNm = new MocName("String");
        Object geomValue = new DirectPosition2D(50, 60);

        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType type = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(type, strNm, 1, 1, false, "line");
        final List<AttributeDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //properties
        final Collection<Property> properties = new ArrayList<Property>();
        GeometryAttribute geomAttr = getFeatureFactory().createGeometryAttribute(geomValue, geoDesc, "id-geom", null);
        properties.add(getFeatureFactory().createAttribute("line1", descriptor, null));
        properties.add(geomAttr);

        //feature creation
        FeatureType fType = getFeatureTypeFactory().createSimpleFeatureType(nm, descList, geoDesc, false, null, null, null);
        final Feature feature = getFeatureFactory().createFeature(properties, fType, "id_0");
        feature.setDefaultGeometryProperty(geomAttr);

        //Tests
        assertTrue(feature instanceof SimpleFeature);

        assertNotNull(feature.getProperty("String"));
        assertNotNull(feature.getDefaultGeometryProperty());

        assertEquals("line1", feature.getProperty("String").getValue());
        assertEquals(geomValue, feature.getDefaultGeometryProperty().getValue());

        try {
            ((SimpleFeature) feature).setAttribute(strNm, null);
            if (validating) {
                fail("Validating Factory has not checked the value insertion");
            }

        } catch (Exception e) {
            if (!validating) {
                fail("Lenient Factory should not check for property validity");
            }
        }
    }
}
