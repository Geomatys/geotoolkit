/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    Created on July 21, 2003, 4:00 PM
 */
package org.geotoolkit.feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author  en
 * @author jgarnett
 * @module pending
 */
public class FeatureTypeTest extends DataTestCase {

    static Set immutable;

    static {
        immutable = new HashSet();
        immutable.add(String.class);
        immutable.add(Integer.class);
        immutable.add(Double.class);
        immutable.add(Float.class);
    }

    public FeatureTypeTest(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FeatureTypeTest.class);
        return suite;
    }

    public void testAbstractType() throws Exception {

        FeatureTypeBuilder tb = new FeatureTypeBuilder();
        tb.setName("http://www.nowhereinparticular.net", "AbstractThing");
        tb.setAbstract(true);

        SimpleFeatureType abstractType = tb.buildSimpleFeatureType();
        tb.setName("http://www.nowhereinparticular.net", "AbstractType2");
        tb.setSuperType(abstractType);
        tb.add(new DefaultName("X"), String.class);
        SimpleFeatureType abstractType2 = tb.buildSimpleFeatureType();

        assertTrue(abstractType.isAbstract());
        assertTrue(abstractType2.isAbstract());

        //assertTrue("extends gml feature", FeatureTypes.isDecendedFrom(abstractType, new URI("http://www.opengis.net/gml"),"Feature"));
        //assertTrue("extends gml feature", FeatureTypes.isDecendedFrom(abstractType2, new URI("http://www.opengis.net/gml"),"Feature"));
        assertTrue("abstractType2 --|> abstractType", FeatureTypeUtilities.isDecendedFrom(abstractType2, abstractType));
        assertFalse("abstractType2 !--|> abstractType", FeatureTypeUtilities.isDecendedFrom(abstractType, abstractType2));

        try {
            SimpleFeatureBuilder.build(abstractType, new Object[0], null);
            fail("abstract type allowed create");
        } catch (IllegalArgumentException iae) {
        } catch (UnsupportedOperationException uoe) {
        }

        try {
            SimpleFeatureBuilder.build(abstractType2, new Object[0], null);
            fail("abstract type allowed create");
        } catch (IllegalArgumentException iae) {
        } catch (UnsupportedOperationException uoe) {
        }

    }

    public void testEquals() throws Exception {
        FeatureTypeBuilder tb = new FeatureTypeBuilder();
        tb.setName(new DefaultName("http://www.nowhereinparticular.net", "Thing"));
        tb.add(new DefaultName("X"), String.class);
        final SimpleFeatureType ft = tb.buildSimpleFeatureType();

        tb = new FeatureTypeBuilder();
        tb.setName(new DefaultName("http://www.nowhereinparticular.net", "Thing"));
        tb.add(new DefaultName("X"), String.class);

        SimpleFeatureType ft2 = tb.buildSimpleFeatureType();
        assertEquals(ft, ft2);

        tb.setName(new DefaultName("Thingee"));
        assertTrue(!ft.equals(tb.buildSimpleFeatureType()));

        tb.copy(ft);
        tb.setName(new DefaultName("http://www.somewhereelse.net", tb.getName().getLocalPart()));

        assertTrue(!ft.equals(tb.buildSimpleFeatureType()));
        assertTrue(!ft.equals(null));
    }

    public void testCopyFeature() throws Exception {
        SimpleFeature feature = lakeFeatures[0];
        assertDuplicate("feature", feature, SimpleFeatureBuilder.copy(feature));
    }

    /**
     * Test FeatureTypes.getAncestors() by constructing three levels of derived types and testing
     * that the expected ancestors are returned at each level in reverse order.
     * 
     * <p>
     * 
     * UML type hierarchy of test types: Feature <|-- A <|-- B <|-- C
     * 
     * @throws Exception
     */
    @SuppressWarnings("serial")
    public void testAncestors() throws Exception {
        String uri = "http://www.geotoolkit.org/example";
        FeatureTypeBuilder tb;

        tb = new FeatureTypeBuilder();
        tb.setName(new DefaultName(uri, "A"));
        final SimpleFeatureType typeA = tb.buildSimpleFeatureType();

        tb = new FeatureTypeBuilder();
        tb.setName(new DefaultName(uri, "B"));
        tb.setSuperType(typeA);
        tb.add(new DefaultName("b"), String.class);
        final SimpleFeatureType typeB = tb.buildSimpleFeatureType();

        tb = new FeatureTypeBuilder();
        tb.setName(new DefaultName(uri, "C"));
        tb.setSuperType(typeB);
        tb.add(new DefaultName("c"), Integer.class);
        final SimpleFeatureType typeC = tb.buildSimpleFeatureType();

        // base type should have no ancestors
        assertEquals("Ancestors of Feature, nearest first", Collections.<FeatureType>emptyList(),
                FeatureTypeUtilities.getAncestors(BasicFeatureTypes.FEATURE));

        assertEquals("Ancestors of A, nearest first", new ArrayList<FeatureType>() {

            {
                add(BasicFeatureTypes.FEATURE);
            }
        }, FeatureTypeUtilities.getAncestors(typeA));

        assertEquals("Ancestors of B, nearest first", new ArrayList<FeatureType>() {

            {
                add(typeA);
                add(BasicFeatureTypes.FEATURE);
            }
        }, FeatureTypeUtilities.getAncestors(typeB));

        assertEquals("Ancestors of C, nearest first", new ArrayList<FeatureType>() {

            {
                add(typeB);
                add(typeA);
                add(BasicFeatureTypes.FEATURE);
            }
        }, FeatureTypeUtilities.getAncestors(typeC));
    }

    public void testDeepCopy() throws Exception {
        // primative        
        String str = "FooBar";
        Integer i = new Integer(3);
        Float f = new Float(3.14);
        Double d = new Double(3.14159);

        AttributeTypeBuilder ab = new AttributeTypeBuilder();
        AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        ab.setBinding(Object.class);
        adb.setName(new DefaultName("test"));
        adb.setType(ab.buildType());
        AttributeDescriptor testType = adb.buildDescriptor();

        assertSame("String", str, FeatureUtilities.duplicate(str));
        assertSame("Integer", i, FeatureUtilities.duplicate(i));
        assertSame("Float", f, FeatureUtilities.duplicate(f));
        assertSame("Double", d, FeatureUtilities.duplicate(d));

        // collections
        Object objs[] = new Object[]{str, i, f, d,};
        int ints[] = new int[]{1, 2, 3, 4,};
        List list = new ArrayList();
        list.add(str);
        list.add(i);
        list.add(f);
        list.add(d);
        Map map = new HashMap();
        map.put("a", str);
        map.put("b", i);
        map.put("c", f);
        map.put("d", d);
        assertDuplicate("objs", objs, FeatureUtilities.duplicate(objs));
        assertDuplicate("ints", ints, FeatureUtilities.duplicate(ints));
        assertDuplicate("list", list, FeatureUtilities.duplicate(list));
        assertDuplicate("map", map, FeatureUtilities.duplicate(map));

        // complex type
        SimpleFeature feature = lakeFeatures[0];

        Coordinate coords = new Coordinate(1, 3);
        Coordinate coords2 = new Coordinate(1, 3);
        GeometryFactory gf = new GeometryFactory();
        Geometry point = gf.createPoint(coords);
        Geometry point2 = gf.createPoint(coords2);

        // JTS does not implement Object equals contract
        assertTrue("jts identity", point != point2);
        assertTrue("jts equals1", point.equals(point2));
        assertTrue("jts equals", !point.equals((Object) point2));

        assertDuplicate("jts duplicate", point, point2);
        assertDuplicate("feature", feature, FeatureUtilities.duplicate(feature));
        assertDuplicate("point", point, FeatureUtilities.duplicate(point));
    }

    public void testReprojectType() throws Exception{
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("attGeom1", Point.class, 0,1,false,null);
        ftb.add(DefaultName.valueOf("attGeom2"), Point.class, DefaultGeographicCRS.WGS84,0,1,false,null);
        final FeatureType ft = ftb.buildFeatureType();

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");
        final FeatureType res = FeatureTypeUtilities.transform(ft, crs);


        assertEquals(crs, ((GeometryDescriptor)res.getDescriptor("attGeom1")).getCoordinateReferenceSystem() );
        assertEquals(crs, ((GeometryDescriptor)res.getDescriptor("attGeom2")).getCoordinateReferenceSystem() );
    }

    protected void assertDuplicate(String message, Object expected, Object value) {
        // Ensure value is equal to expected 
        if (expected.getClass().isArray()) {
            int length1 = Array.getLength(expected);
            int length2 = Array.getLength(value);
            assertEquals(message, length1, length2);
            for (int i = 0; i < length1; i++) {
                assertDuplicate(
                        message + "[" + i + "]",
                        Array.get(expected, i),
                        Array.get(value, i));
            }
            //assertNotSame( message, expected, value );
        } else if (expected instanceof Geometry) {
            // JTS Geometry does not meet the Obejct equals contract!
            // So we need to do our assertEquals statement
            //
            assertTrue(message, value instanceof Geometry);
            assertTrue(message, expected instanceof Geometry);
            Geometry expectedGeom = (Geometry) expected;
            Geometry actualGeom = (Geometry) value;
            assertTrue(message, expectedGeom.equals(actualGeom));
        } else if (expected instanceof SimpleFeature) {
            assertDuplicate(message, ((SimpleFeature) expected).getAttributes(),
                    ((SimpleFeature) value).getAttributes());
        } else {
            assertEquals(message, expected, value);
        }
        // Ensure Non Immutables are actually copied
        if (!immutable.contains(expected.getClass())) {
            //assertNotSame( message, expected, value );
        }
    }
}
