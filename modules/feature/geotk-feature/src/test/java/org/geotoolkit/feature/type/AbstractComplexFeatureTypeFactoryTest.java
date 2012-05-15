package org.geotoolkit.feature.type;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.DirectPosition2D;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.type.*;

/**
 *
 * @author Alexis MANIN
 */
public abstract class AbstractComplexFeatureTypeFactoryTest extends AbstractFeatureTypeFactoryTest{

    @Test
    public void testCreateComplexType() {
        Name nm = new DefaultName("point");
        Name strNm = new DefaultName("String");
        Name ctNm = new DefaultName("complexTypeName");

        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strNm, 0, Integer.MAX_VALUE, false, "line");
        final List<PropertyDescriptor> descList = new ArrayList(2);
        descList.add(descriptor);
        descList.add(geoDesc);

        ComplexType res = getFeatureTypeFactory().createComplexType(ctNm, descList, true, false, null, null, null);

        assertNotNull("Complex type is null", res);
        assertNotNull("Name not set", res.getName());
        assertNotNull("Descriptors have not been writen into the complex type", res.getDescriptors());
        assertEquals("Name does not match", ctNm, res.getName());
        assertEquals("Descriptors are not properly set", geoDesc, res.getDescriptor(nm));
        assertEquals("Descriptors are not properly set", descriptor, res.getDescriptor(strNm));
    }
    
    @Test
    @Override
    public void createFeatureType() {
        Name nm = new DefaultName("point");
        Name strNm = new DefaultName("String");
        Name fNm = new DefaultName("featureTypeName");

        //types and descriptors
        GeometryType geoType = getFeatureTypeFactory().createGeometryType(nm, DirectPosition2D.class, null, true, false, null, null, null);
        GeometryDescriptor geoDesc = getFeatureTypeFactory().createGeometryDescriptor(geoType, nm, 1, 1, true, null);
        AttributeType attrType = getFeatureTypeFactory().createAttributeType(strNm, String.class, true, false, null, null, null);
        AttributeDescriptor descriptor = getFeatureTypeFactory().createAttributeDescriptor(attrType, strNm, 0, Integer.MAX_VALUE, false, "line");
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
