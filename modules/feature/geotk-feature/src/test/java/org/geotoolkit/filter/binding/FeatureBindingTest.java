/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.filter.binding;

import java.util.Collection;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import java.util.Iterator;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.feature.FeatureUtilities;
import org.junit.Test;
import org.geotoolkit.feature.Attribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureBindingTest extends org.geotoolkit.test.TestBase {

    public FeatureBindingTest() {
    }

    @Test
    public void testFactories(){
        final Binding[] factories = Bindings.getBindings();

        int xpathFactory = -1;
        int defaultFactory = -1;

        for(int i=0;i<factories.length;i++){
            if(factories[i] instanceof XPathBinding){
                xpathFactory = i;
            }else if(factories[i] instanceof ComplexAttributeBinding){
                defaultFactory = i;
            }
        }

        assertTrue(xpathFactory != -1);
        assertTrue(defaultFactory != -1);
        assertTrue(defaultFactory < xpathFactory);


    }

    @Test
    public void testSimpleFeatureFlatAccessor() {

        //test a simple attribut------------------------------------------------
        Binding accessor = Bindings.getBinding(Feature.class, "testGeometry");
        assertNotNull(accessor);
        Object att = accessor.get(FEATURE_1, "testGeometry", Geometry.class);
        assertEquals(FEATURE_1.getDefaultGeometryProperty().getValue(), att);


        //test a simple attribut------------------------------------------------
        accessor = Bindings.getBinding(Feature.class, "//testGeometry");
        assertNotNull(accessor);
        att = (Geometry) accessor.get(FEATURE_1, "//testGeometry", Geometry.class);
        assertEquals(FEATURE_1.getDefaultGeometryProperty().getValue(), att);

        //test id---------------------------------------------------------------
        accessor = Bindings.getBinding(Feature.class, "@id");
        assertNotNull(accessor);
        Object id = accessor.get(FEATURE_1, "@id", null);
        assertEquals(FEATURE_1.getIdentifier().getID(), id);

        //test xpath index------------------------------------------------------
        accessor = Bindings.getBinding(Feature.class, "*[10]");
        assertNotNull(accessor);
        att = accessor.get(FEATURE_1, "*[10]", null);
        assertEquals("test string data", att);
        assertEquals(FEATURE_1.getProperty("testString").getValue(), att);

        //test a geometry name with accents-------------------------------------
        accessor = Bindings.getBinding(Feature.class, "attribut.Géométrie");
        assertNotNull(accessor);
        att = accessor.get(FEATURE_1, "attribut.Géométrie", null);
        assertEquals("POINT(45,32)", att);
        assertEquals(FEATURE_1.getProperty("attribut.Géométrie").getValue(), att);

    }

    @Test
    public void testSimpleFeatureTypeFlatAccessor() {

        //test a simple attribut------------------------------------------------
        Binding accessor = Bindings.getBinding(SimpleFeatureType.class, "testGeometry");
        assertNotNull(accessor);
        Object att = (GeometryDescriptor) accessor.get(FEATURE_TYPE_1, "testGeometry", null);
        assertEquals(FEATURE_TYPE_1.getGeometryDescriptor(), att);

        //test a simple attribut------------------------------------------------
        accessor = Bindings.getBinding(SimpleFeatureType.class, "//testGeometry");
        assertNotNull(accessor);
        att = (GeometryDescriptor) accessor.get(FEATURE_TYPE_1, "//testGeometry", null);
        assertEquals(FEATURE_TYPE_1.getGeometryDescriptor(), att);

        //test xpath index------------------------------------------------------
        accessor = Bindings.getBinding(SimpleFeatureType.class, "*[10]");
        assertNotNull(accessor);
        att = accessor.get(FEATURE_TYPE_1, "*[10]", null);
        assertEquals(FEATURE_TYPE_1.getDescriptor("testString"), att);

        //test a geometry name with accents-------------------------------------
        accessor = Bindings.getBinding(SimpleFeatureType.class, "attribut.Géométrie");
        assertNotNull(accessor);
        att = accessor.get(FEATURE_TYPE_1, "attribut.Géométrie", null);
        assertEquals(FEATURE_TYPE_1.getDescriptor("attribut.Géométrie"), att);

    }

    @Test
    public void testComplexFeatureAccessor() {
        Binding accessor;
        String xpath;

        final Feature candidate = FeatureUtilities.copy(CX_FEATURE);

        // flat attribut test //////////////////////////////////////////////////
        xpath = "attString";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        Object val = accessor.get(candidate, xpath, null);
        assertEquals("toto1", val);
        //test setting
        accessor.set(candidate, xpath, "bigJohn");
        val = accessor.get(candidate, xpath, null);
        assertEquals("bigJohn", val);
        accessor.set(candidate, xpath, "toto1");

        // flat attribut test //////////////////////////////////////////////////
        xpath = "{http://test.com}attString";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        val = accessor.get(candidate, xpath, null);
        assertEquals("toto1", val);
        //test setting
        accessor.set(candidate, xpath, "Alex");
        val = accessor.get(candidate, xpath, null);
        assertEquals("Alex", val);
        accessor.set(candidate, xpath, "toto1");

        // flat attribut test //////////////////////////////////////////////////
        xpath = "/{http://test2.com}attString";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        val = accessor.get(candidate, xpath, null);
        assertEquals("toto3", val);
        //test setting
        accessor.set(candidate, xpath, "Alex");
        val = accessor.get(candidate, xpath, null);
        assertEquals("Alex", val);
        accessor.set(candidate, xpath, "toto3");


        // sub path attribut ///////////////////////////////////////////////////
        xpath = "/{http://test.com}attCpx/{http://test.com}attString";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        val = accessor.get(candidate, xpath, null);
        assertEquals("toto19", val);
        //test setting
        accessor.set(candidate, xpath, "Franck");
        val = accessor.get(candidate, xpath, null);
        assertEquals("Franck", val);
        accessor.set(candidate, xpath, "toto19");

        // sub path attribut ///////////////////////////////////////////////////
        xpath = "/{http://test.com}attCpx[{http://test2.com}attString='marcel2']";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        val = accessor.get(candidate, xpath, Property.class);

        final Iterator<Property> ite = candidate.getProperties("attCpx").iterator();
        ite.next();
        assertEquals(ite.next(), val);

        //accessing a collection of properties /////////////////////////////////
        xpath = "attCpx";
        accessor = Bindings.getBinding(Feature.class, xpath);
        assertNotNull(accessor);
        val = accessor.get(candidate, xpath, Collection.class);

        assertNotNull(val);
        assertTrue(val instanceof Collection);
        Collection col = (Collection) val;
        assertEquals(2, col.size());

    }

    @Test
    public void testComplexFeatureTypeAccessor() {
        Binding accessor;

        // flat attribut test //////////////////////////////////////////////////
        accessor = Bindings.getBinding(FeatureType.class, "attString");
        assertNotNull(accessor);
        Object val = accessor.get(CX_FEATURE_TYPE, "attString", null);
        assertEquals(CX_FEATURE_TYPE.getDescriptor("{http://test.com}attString"), val);

        // flat attribut test //////////////////////////////////////////////////
        accessor = Bindings.getBinding(FeatureType.class, "{http://test.com}attString");
        assertNotNull(accessor);
        val = accessor.get(CX_FEATURE_TYPE, "{http://test.com}attString", null);
        assertEquals(CX_FEATURE_TYPE.getDescriptor("{http://test.com}attString"), val);

        // flat attribut test //////////////////////////////////////////////////
        accessor = Bindings.getBinding(FeatureType.class, "/{http://test.com}attString");
        assertNotNull(accessor);
        val = accessor.get(CX_FEATURE_TYPE, "/{http://test.com}attString", null);
        assertEquals(CX_FEATURE_TYPE.getDescriptor("{http://test.com}attString"), val);


        // sub path attribut ///////////////////////////////////////////////////
        accessor = Bindings.getBinding(FeatureType.class, "/{http://test.com}attCpx/{http://test.com}attString");
        assertNotNull(accessor);
        val = accessor.get(CX_FEATURE_TYPE, "/{http://test.com}attCpx/{http://test.com}attString", null);
        ComplexType type = (ComplexType) CX_FEATURE_TYPE.getDescriptor("{http://test.com}attCpx").getType();
        assertEquals(type.getDescriptor("{http://test.com}attString"), val);

        // sub path attribut ///////////////////////////////////////////////////
        accessor = Bindings.getBinding(FeatureType.class, "//{http://test.com}attCpx/{http://test.com}attString");
        assertNotNull(accessor);
        val = accessor.get(CX_FEATURE_TYPE, "//{http://test.com}attCpx/{http://test.com}attString", null);
        type = (ComplexType) CX_FEATURE_TYPE.getDescriptor("{http://test.com}attCpx").getType();
        assertEquals(type.getDescriptor("{http://test.com}attString"), val);

    }

    @Test
    public void testAttributeAccessor(){

        Binding accessor = Bindings.getBinding(Attribute.class, ".");
        assertNotNull(accessor);
        Property prop = FEATURE_1.getProperty("testGeometry");
        Object att = accessor.get(prop, ".", Geometry.class);
        assertEquals(FEATURE_1.getDefaultGeometryProperty().getValue(), att);

        att = accessor.get(prop, ".", Property.class);
        assertEquals(FEATURE_1.getDefaultGeometryProperty(), att);

    }

}
