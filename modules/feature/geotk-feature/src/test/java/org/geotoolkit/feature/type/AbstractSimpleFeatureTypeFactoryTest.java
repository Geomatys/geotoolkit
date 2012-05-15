/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature.type;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.feature.MocName;
import org.geotoolkit.geometry.DirectPosition2D;
import static org.junit.Assert.*;
import org.junit.*;
import org.opengis.feature.type.*;

/**
 * A class to test {@link FeatureTypeFactory} methods (only for simple features).
 *
 * Test creation of attributes and association types as descriptors. Also test geometry type & simple feature type creation 
 * 
 * @author Alexis MANIN
 *
 */
public abstract class AbstractSimpleFeatureTypeFactoryTest {

    public AbstractSimpleFeatureTypeFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * A function which return the current used feature type factory
     *
     * @return the feature type factory to use for tests
     */
    public abstract FeatureTypeFactory getFeatureTypeFactory();

    @Test
    public void testCreateSchema() {
        System.out.println("Schema creation test");
        Schema test = getFeatureTypeFactory().createSchema("bouh");
        assertNotNull("Schema creation failed", test);
        assertNotNull("Schema URI is null", test.getURI());
        assertEquals("Schema URI is not the one expected", "bouh", test.getURI());
    }

    @Test
    public void testCreateAttributeType() {
        System.out.println("AttributeType creation test");
        Name nm = new MocName("bouh");
        AttributeType res = getFeatureTypeFactory().createAttributeType(nm, String.class, true, false, null, null, null);
        assertNotNull("AttributeType creation failed", res);
        assertNotNull("AttributeType name not set", res.getName());
        assertEquals("AttributeType name does not match", res.getName().toString(), "bouh");
        assertTrue("AttributeType is not identified (but should be)", res.isIdentified());
        assertFalse("AttributeType set abstract while it's not true", res.isAbstract());
        assertEquals("AttributeType does not bind the right class", String.class, res.getBinding());
    }

    @Test
    public void testCreateGeometryType() {
        System.out.println("GeometryType creation test");
        Name nm = new MocName("bouh");
        AttributeType res = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        assertNotNull("GeometryType creation failed", res);
        assertNotNull("GeometryType name not set", res.getName());
        assertEquals("GeometryType name does not match", res.getName().toString(), "bouh");
        assertTrue("GeometryType is not identified (but should be)", res.isIdentified());
        assertFalse("GeometryType set abstract while it's not true", res.isAbstract());
        assertEquals("GeometryType does not bind the right class", DirectPosition2D.class, res.getBinding());

    }

    @Test
    public void testCreateAssociationType() {
        System.out.println("AssociationType creation test");
        Name nm = new MocName("lines");
        Name asNm = new MocName("Association");
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(nm, String.class, true, false, null, null, null);
        AssociationType asType = getFeatureTypeFactory().createAssociationType(asNm, attrType, false, null, null, null);
        assertNotNull("AssociationType creation failed", asType);
        assertNotNull("AssociationType name not set", asType.getName());
        assertNotNull("AssociationType related type not set", asType.getRelatedType());
        assertEquals("AssociationType name does not match", asNm, asType.getName());
        assertEquals("AssociationType related type does not match", attrType, asType.getRelatedType());
    }

    @Test
    public void testCreateAttributeDescriptor() {
        System.out.println("Attribute descriptor creation test");
        Name nm = new MocName("lines");
        Name adNm = new MocName("lines descriptor");
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(nm, String.class, true, false, null, null, null);
        AttributeDescriptor res = getFeatureTypeFactory().createAttributeDescriptor(attrType, adNm, 1, 1, false, "defaultvalueishere");
        assertNotNull("Attribute descriptor not set", res);
        assertNotNull("Attribute descriptor name not set", res.getName());
        assertNotNull("Attribute default value not set", res.getDefaultValue());
        assertTrue("Attribute occurences are wrong", res.getMinOccurs() == 1 && res.getMaxOccurs() == 1);
        assertEquals("Attribute descriptor name does not match", res.getName(), adNm);
        assertEquals("Attribute default value does not match", res.getDefaultValue(), "defaultvalueishere");
    }

    @Test
    public void testCreateGeometryDescriptor() {
        System.out.println("Geometry descriptor creation test");
        Name nm = new MocName("geometry");
        Name gdNm = new MocName("geomatry descriptor");
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(nm, DirectPosition2D.class, true, false, null, null, null);
        AttributeDescriptor res = getFeatureTypeFactory().createAttributeDescriptor(attrType, gdNm, 0, Integer.MAX_VALUE, true, null);
        assertNotNull("Geometry descriptor not set", res);
        assertNotNull("Geometry descriptor name not set", res.getName());
        assertTrue("Geometry occurences are wrong", res.getMinOccurs() == 0 && res.getMaxOccurs() == Integer.MAX_VALUE);
        assertEquals("Geometry descriptor name does not match", res.getName(), gdNm);
        assertNull("Geometry default value does not match", res.getDefaultValue());
    }

    @Test
    public void testCreateAssociationDescriptor() {
        System.out.println("Association descriptor creation test");
        Name nm = new MocName("lines");
        Name asNm = new MocName("Association");
        Name adNm = new MocName("descriptor");
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(nm, String.class, true, false, null, null, null);
        AssociationType asType = getFeatureTypeFactory().createAssociationType(asNm, attrType, false, null, null, null);
        AssociationDescriptor res = getFeatureTypeFactory().createAssociationDescriptor(asType, adNm, 1, 1, false);

        assertNotNull("Association descriptor not set", res);
        assertNotNull("Association descriptor name not set", res.getName());
        assertNotNull("Association type not set", res.getType());
        assertTrue("Association occurences are wrong", res.getMinOccurs() == 1 && res.getMaxOccurs() == 1);
        assertEquals("Association descriptor name does not match", res.getName(), adNm);
        assertEquals("Association descriptor type does not match", res.getType(), asType);
    }

    @Test
    public void createSimpleFeatureType() {
        System.out.println("Simple feature type creation test");
        Name nm = new MocName("point");
        Name strNm = new MocName("String");
        Name fNm = new MocName("featureTypeName");
        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType type = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(type, strNm, 1, 1, false, "line");
        final List<AttributeDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //feature creation
        FeatureType fType = getFeatureTypeFactory().createSimpleFeatureType(fNm, descList, geoDesc, false, null, null, null);

        //Tests
        assertNotNull("Feature type have not been created");
        assertNotNull("Name is not set", fType.getName());
        assertNotNull("Descriptors are not set", fType.getDescriptors());
        assertNotNull("Geometry descriptor is not set", fType.getGeometryDescriptor());

        assertEquals("Feature type name is not properly set", fType.getName(), fNm);
        assertEquals("Feature descriptors are not properly set", fType.getDescriptor(nm), geoDesc);
        assertEquals("Feature descriptors are not properly set", fType.getDescriptor(strNm), descriptor);

        assertFalse("Feature type set as abstracted while it's not", fType.isAbstract());
    }

    @Test
    public void createFeatureType() {
        Name nm = new MocName("point");
        Name strNm = new MocName("String");
        Name fNm = new MocName("featureTypeName");

        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strNm, 1, 1, false, "line");
        final List<AttributeDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        //feature type creation
        FeatureType res = getFeatureTypeFactory().createSimpleFeatureType(fNm, descList, geoDesc, false, null, null, null);

        assertNotNull("FeatureType has not been set", res);
        assertNotNull("FeatureType name has not been set", res.getName());
        assertNotNull("Descriptors are not set", res.getDescriptors());
        assertNotNull("Geometry descriptor is not set", res.getGeometryDescriptor());

        assertEquals("Feature type name is not properly set", res.getName(), fNm);
        assertEquals("Feature descriptors are not properly set", res.getDescriptor(nm), geoDesc);
        assertEquals("Feature descriptors are not properly set", res.getDescriptor(strNm), descriptor);

        assertFalse("Feature type set as abstracted while it's not", res.isAbstract());

    }
}
