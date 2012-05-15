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
package org.geotoolkit.feature.type;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.feature.MocName;
import org.geotoolkit.geometry.DirectPosition2D;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.type.*;

/**
 * A class which extends from {@link FeatureTypeFactoryTest} to test a {@link FeatureTypeFactory} for complex features type creation.
 * Test creation of complex attributes/feature types 
 * @author Alexis MANIN
 */
public abstract class AbstractComplexFeatureTypeFactoryTest extends AbstractSimpleFeatureTypeFactoryTest{

    @Test
    public void testCreateComplexType() {
        Name nm = new MocName("point");
        Name strNm = new MocName("String");
        Name ctNm = new MocName("complexTypeName");

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
        Name nm = new MocName("point");
        Name strNm = new MocName("String");
        Name fNm = new MocName("featureTypeName");

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
