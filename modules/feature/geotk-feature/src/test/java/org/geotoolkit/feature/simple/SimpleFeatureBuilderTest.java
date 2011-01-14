/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.simple;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DataTestCase;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.FeatureValidationUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;

public class SimpleFeatureBuilderTest extends DataTestCase {

    static final Set immutable;

    static {
        immutable = new HashSet();
        immutable.add(String.class);
        immutable.add(Integer.class);
        immutable.add(Double.class);
        immutable.add(Float.class);
    }

    SimpleFeatureBuilder builder;

    public SimpleFeatureBuilderTest(final String testName) throws Exception {
        super(testName);
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SimpleFeatureBuilderTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
        FeatureTypeBuilder typeBuilder = new FeatureTypeBuilder();
        typeBuilder.setName("test");
        typeBuilder.add("point", Point.class, DefaultGeographicCRS.WGS84);
        typeBuilder.add("integer", Integer.class);
        typeBuilder.add("float", Float.class);

        SimpleFeatureType featureType = typeBuilder.buildSimpleFeatureType();

        builder = new SimpleFeatureBuilder(featureType);
        builder.setValidating(true);
    }

    public void testSanity() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        builder.add(gf.createPoint(new Coordinate(0, 0)));
        builder.add(new Integer(1));
        builder.add(new Float(2.0));

        SimpleFeature feature = builder.buildFeature("fid");
        assertNotNull(feature);

        assertEquals(3, feature.getAttributeCount());

        assertTrue(gf.createPoint(new Coordinate(0, 0)).equals((Geometry) feature.getAttribute("point")));
        assertEquals(new Integer(1), feature.getAttribute("integer"));
        assertEquals(new Float(2.0), feature.getAttribute("float"));
    }

    public void testTooFewAttributes() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        builder.add(gf.createPoint(new Coordinate(0, 0)));
        builder.add(new Integer(1));

        SimpleFeature feature = builder.buildFeature("fid");
        assertNotNull(feature);

        assertEquals(3, feature.getAttributeCount());

        assertTrue(gf.createPoint(new Coordinate(0, 0)).equals((Geometry) feature.getAttribute("point")));
        assertEquals(new Integer(1), feature.getAttribute("integer"));
        assertNull(feature.getAttribute("float"));
    }

    public void testSetSequential() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        builder.set("point", gf.createPoint(new Coordinate(0, 0)));
        builder.set("integer", new Integer(1));
        builder.set("float", new Float(2.0));

        SimpleFeature feature = builder.buildFeature("fid");
        assertNotNull(feature);

        assertEquals(3, feature.getAttributeCount());

        assertTrue(gf.createPoint(new Coordinate(0, 0)).equals((Geometry) feature.getAttribute(0)));
        assertEquals(new Integer(1), feature.getAttribute(1));
        assertEquals(new Float(2.0), feature.getAttribute(2));
    }

    public void testSetNonSequential() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        builder.set("float", new Float(2.0));
        builder.set("point", gf.createPoint(new Coordinate(0, 0)));
        builder.set("integer", new Integer(1));

        SimpleFeature feature = builder.buildFeature("fid");
        assertNotNull(feature);

        assertEquals(3, feature.getAttributeCount());

        assertTrue(gf.createPoint(new Coordinate(0, 0)).equals((Geometry) feature.getAttribute(0)));
        assertEquals(new Integer(1), feature.getAttribute(1));
        assertEquals(new Float(2.0), feature.getAttribute(2));
    }

    public void testSetTooFew() throws Exception {
        builder.set("integer", new Integer(1));
        SimpleFeature feature = builder.buildFeature("fid");
        assertNotNull(feature);

        assertEquals(3, feature.getAttributeCount());

        assertNull(feature.getAttribute(0));
        assertEquals(new Integer(1), feature.getAttribute(1));
        assertNull(feature.getAttribute(2));
    }

    public void testConverting() throws Exception {
        builder.set("integer", "1");
        SimpleFeature feature = builder.buildFeature("fid");

        try {
            builder.set("integer", "foo");
            fail("should have failed");
        } catch (Exception e) {
        }

    }

    public void testCreateFeatureWithLength() throws Exception {

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName("name");
        atb.setBinding(String.class);
        atb.setLength(5);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName("name");
        adb.setType(atb.buildType());

        builder.setName("test");
        builder.add(adb.buildDescriptor());
        
        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Val"}, "ID");

        assertNotNull(feature);

        try {
            feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Longer Than 5"}, "ID");
            feature.validate();
            fail("this should fail because the value is longer than 5 characters");
        } catch (Exception e) {
            // good
        }
    }

    public void testCreateFeatureWithRestriction() throws Exception {
        FilterFactory fac = FactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        PropertyIsEqualTo filter = fac.equals(fac.property("string"), fac.literal("Value"));

        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setBinding(String.class);
        atb.addRestriction(filter);
        AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(attributeName);
        adb.setType(atb.buildType());

        builder.setName("test");
        builder.add(adb.buildDescriptor());
        

        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Value"}, "ID");

        assertNotNull(feature);

        try {
            SimpleFeature sf = SimpleFeatureBuilder.build(featureType, new Object[]{"NotValue"}, "ID");
            sf.validate();
            fail("PropertyIsEqualTo filter should have failed");
        } catch (Exception e) {
            //good
        }

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

    public void testCopyFeature() throws Exception {
        SimpleFeature feature = lakeFeatures[0];
        assertDuplicate("feature", feature, FeatureUtilities.copy(feature));
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

    protected void assertDuplicate(final String message, final Object expected, final Object value) {
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


    public void testWithoutRestriction(){
        // used to prevent warning
        FilterFactory fac = FactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName("test");
        builder.add(attributeName, String.class);
        SimpleFeatureType featureType = builder.buildSimpleFeatureType();

        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Value"},
                null);

        assertNotNull( feature );
    }
    /**
     * This utility class is used by Types to prevent attribute modification.
     */
    public void testRestrictionCheck() {
        FilterFactory fac = FactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        PropertyIsEqualTo filter = fac.equals(fac.property("string"), fac
                .literal("Value"));

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setBinding(String.class);
        atb.addRestriction(filter);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(attributeName);
        adb.setType(atb.buildType());

        builder.setName("test");
        builder.add(adb.buildDescriptor());

        SimpleFeatureType featureType = builder.buildSimpleFeatureType();

        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[]{"Value"},
                null);

        assertNotNull( feature );

    }

    public void testAssertNamedAssignable(){
        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        builder.reset();
        builder.setName("Test");
        builder.add("name", String.class );
        builder.add("age", Double.class );
        SimpleFeatureType test = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("age", Double.class );
        builder.add("name",String.class);
        SimpleFeatureType test2 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("name",String.class);
        SimpleFeatureType test3 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName("Test");
        builder.add("name",String.class);
        builder.add("distance", Double.class );
        SimpleFeatureType test4 = builder.buildSimpleFeatureType();

        FeatureValidationUtilities.assertNameAssignable( test, test );
        FeatureValidationUtilities.assertNameAssignable( test, test2 );
        FeatureValidationUtilities.assertNameAssignable( test2, test );
        try {
            FeatureValidationUtilities.assertNameAssignable( test, test3 );
            fail("Expected assertNameAssignable to fail as age is not covered");
        }
        catch ( IllegalArgumentException expected ){
        }

        FeatureValidationUtilities.assertOrderAssignable( test, test4 );
    }

}
