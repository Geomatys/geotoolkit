/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Alexis MANIN
 */
public abstract class AbstractFeatureFactoryTest {

    protected final boolean validating;

    public AbstractFeatureFactoryTest(boolean validating) {
        this.validating = validating;
    }

    public abstract FeatureFactory getFeatureFactory();

    public abstract FeatureTypeFactory getFeatureTypeFactory();

    /**
     * Test of createAssociation method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateAssociation() {
        System.out.println("Association creation test");
        Name nm = new DefaultName("point");
        Name finalNm = new DefaultName("pointAsso");
        String id = "id-0";
        Object value = new DirectPosition2D(50, 60);

        AttributeType type = getFeatureTypeFactory().createAttributeType(nm, DirectPosition2D.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(type, nm, 1, 1, false, new DirectPosition2D(0, 0));
        Attribute attr = getFeatureFactory().createAttribute(value, descriptor, id);
        AssociationType asType = getFeatureTypeFactory().createAssociationType(nm, type, false, null, null, null);
        AssociationDescriptor asDescriptor = getFeatureTypeFactory().createAssociationDescriptor(asType, finalNm, 1, 1, false);
        Association asso = getFeatureFactory().createAssociation(attr, asDescriptor);

        assertEquals("Association name does not match", asso.getName(), finalNm);
        assertEquals("Association value does not match", asso.getValue(), attr);
        assertEquals("Association type does not match", asso.getType(), asType);
        assertEquals("Association descriptor does not match", asso.getDescriptor(), asDescriptor);
    }

    /**
     * Test of createAttribute method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateSimpleAttribute() {
        System.out.println("Simple Attribute creation test");
        Object value = new DirectPosition2D(50, 60);
        Name nm = new DefaultName("point");
        Name finalNm = new DefaultName("pointAsso");
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
     * Test of createGeometryAttribute method, of class AbstractFeatureFactory.
     */
    @Test
    public void testCreateGeometryAttribute() {
        System.out.println("GeometryAttribute creation test");
        Object value = new DirectPosition2D(50, 60);
        Name nm = new DefaultName("point");
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
        Name nm = new DefaultName("point");
        Name strNm = new DefaultName("String");
        Object geomValue = new DirectPosition2D(50, 60);

        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D .class, null, true, false, null, null, null);
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
            ((SimpleFeature)feature).setAttribute(strNm, null);
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
