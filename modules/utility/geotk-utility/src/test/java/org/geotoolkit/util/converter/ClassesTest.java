/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.util.converter;

import java.io.*;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.RandomAccess;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.CoordinateOperation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Classes} static methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 */
public final strictfp class ClassesTest {
    /**
     * Tests {@link Classes#changeArrayDimension}.
     */
    @Test
    public void testChangeArrayDimension() {
        assertEquals(float    .class, Classes.changeArrayDimension(float    .class,  0));
        assertEquals(Float    .class, Classes.changeArrayDimension(Float    .class,  0));
        assertEquals(float[]  .class, Classes.changeArrayDimension(float    .class,  1));
        assertEquals(Float[]  .class, Classes.changeArrayDimension(Float    .class,  1));
        assertEquals(float[][].class, Classes.changeArrayDimension(float    .class,  2));
        assertEquals(Float[][].class, Classes.changeArrayDimension(Float    .class,  2));
        assertEquals(float[][].class, Classes.changeArrayDimension(float[]  .class,  1));
        assertEquals(Float[][].class, Classes.changeArrayDimension(Float[]  .class,  1));
        assertEquals(float[]  .class, Classes.changeArrayDimension(float[][].class, -1));
        assertEquals(Float[]  .class, Classes.changeArrayDimension(Float[][].class, -1));
        assertEquals(float    .class, Classes.changeArrayDimension(float[][].class, -2));
        assertEquals(Float    .class, Classes.changeArrayDimension(Float[][].class, -2));
        assertNull  (                 Classes.changeArrayDimension(float[][].class, -3));
        assertNull  (                 Classes.changeArrayDimension(Float[][].class, -3));
        assertNull  (                 Classes.changeArrayDimension(Void.TYPE,       -1));
        assertEquals(Void.TYPE,       Classes.changeArrayDimension(Void.TYPE,        1));
    }

    /**
     * Tests {@link Classes#getAllInterfaces}.
     */
    @Test
    public void testGetAllInterfaces() {
        final Set<Class<?>> interfaces = Classes.getAllInterfaces(ArrayList.class);
        assertTrue(interfaces.contains(List        .class));
        assertTrue(interfaces.contains(Collection  .class));
        assertTrue(interfaces.contains(Iterable    .class));
        assertTrue(interfaces.contains(RandomAccess.class));
        assertTrue(interfaces.contains(Serializable.class));
        assertTrue(interfaces.contains(Cloneable   .class));
    }

    /**
     * Tests {@link Classes#getLeafInterfaces}.
     *
     * @since 3.18
     */
    @Test
    public void testGetLeafInterfaces() {
        assertArrayEquals("TreeSet class", new Class<?>[] {NavigableSet.class},
                Classes.getLeafInterfaces(TreeSet.class, Collection.class));

        assertArrayEquals("Convolved class", new Class<?>[] {Transformation.class, GeographicCRS.class},
                Classes.getLeafInterfaces(T3.class, IdentifiedObject.class));
    }

    /**
     * Dummy class for {@link #testGetAllInterfaces()}.
     */
    private static abstract class T1 implements GeographicCRS {}
    private static abstract class T2 extends T1 implements SingleCRS, CoordinateOperation {}
    private static abstract class T3 extends T2 implements Transformation {}

    /**
     * Tests {@link Classes#findSpecializedClass} and {@link Classes#findCommonClass}.
     */
    @Test
    public void testFindCommonParent() {
        final Set<Object> types = new HashSet<Object>();

        assertTrue(types.add(new NotSerializableException()));
        assertEquals(NotSerializableException.class, Classes.findCommonClass     (types));
        assertEquals(NotSerializableException.class, Classes.findSpecializedClass(types));

        assertTrue(types.add(new InvalidObjectException(null)));
        assertEquals(ObjectStreamException.class, Classes.findCommonClass     (types));
        assertEquals(ObjectStreamException.class, Classes.findSpecializedClass(types));

        assertTrue(types.add(new FileNotFoundException()));
        assertEquals(IOException.class, Classes.findCommonClass     (types));
        assertEquals(IOException.class, Classes.findSpecializedClass(types));

        assertTrue(types.add(new IOException()));
        assertEquals(IOException.class, Classes.findCommonClass     (types));
        assertEquals(IOException.class, Classes.findSpecializedClass(types));

        assertTrue(types.add(new Exception()));
        assertEquals(  Exception.class, Classes.findCommonClass     (types));
        assertEquals(IOException.class, Classes.findSpecializedClass(types));
    }

    /**
     * Tests {@link Classes#findCommonInterfaces}.
     */
    @Test
    public void testFindCommonInterfaces() {
        final Set<Class<?>> interfaces = Classes.findCommonInterfaces(ArrayList.class, HashSet.class);
        assertFalse(interfaces.contains(Set         .class));
        assertFalse(interfaces.contains(List        .class));
        assertTrue (interfaces.contains(Collection  .class));
        assertFalse(interfaces.contains(Iterable    .class));
        assertFalse(interfaces.contains(RandomAccess.class));
        assertTrue (interfaces.contains(Serializable.class));
        assertTrue (interfaces.contains(Cloneable   .class));
    }

    /**
     * Tests {@link Classes#implementSameInterfaces}.
     */
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"}) // We break consistency on purpose for one test.
    public void testImplementSameInterfaces() {
        assertTrue (Classes.implementSameInterfaces(StringBuilder.class, String.class, CharSequence.class));
        assertTrue (Classes.implementSameInterfaces(StringBuilder.class, String.class, Serializable.class));
        assertFalse(Classes.implementSameInterfaces((Class)  File.class, String.class, CharSequence.class));
        assertTrue (Classes.implementSameInterfaces(         File.class, String.class, Serializable.class));
    }

    /**
     * Tests the {@link #boundOfParameterizedAttribute} method.
     *
     * @throws NoSuchFieldException  Should never occur.
     * @throws NoSuchMethodException Should never occur.
     */
    @Test
    public void testBoundOfParameterizedAttribute()
            throws NoSuchFieldException, NoSuchMethodException
    {
        final Class<?>[] g = null;
        final Class<?>[] s = new Class<?>[] {Set.class};
        final Class<ClassesTest> c = ClassesTest.class;
        assertNull(Classes.boundOfParameterizedAttribute(c.getMethod("getter0", g)));
        assertNull(Classes.boundOfParameterizedAttribute(c.getMethod("setter0", s)));
        assertEquals(Long   .class, Classes.boundOfParameterizedAttribute(c.getField ("attrib2"   )));
        assertEquals(Integer.class, Classes.boundOfParameterizedAttribute(c.getMethod("getter1", g)));
        assertEquals(Byte   .class, Classes.boundOfParameterizedAttribute(c.getMethod("getter2", g)));
        assertEquals(Object .class, Classes.boundOfParameterizedAttribute(c.getMethod("getter3", g)));
        assertEquals(short[].class, Classes.boundOfParameterizedAttribute(c.getMethod("getter4", g)));
        assertEquals(String .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter1", s)));
        assertEquals(Short  .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter2", s)));
        assertEquals(Object .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter3", s)));
    }

    public Set<? extends Long> attrib2 = null;
    @SuppressWarnings("rawtypes")
    public Set                 getter0() {return null;} // Intentionnaly unparameterized.
    public Set<       Integer> getter1() {return null;}
    public Set<? extends Byte> getter2() {return null;}
    public Set<? super  Float> getter3() {return null;}
    public Set<       short[]> getter4() {return null;}
    @SuppressWarnings("rawtypes")
    public void setter0(Set                  dummy) {}  // Intentionnaly unparameterized.
    public void setter1(Set<         String> dummy) {}
    public void setter2(Set<? extends Short> dummy) {}
    public void setter3(Set<? super  Double> dummy) {}
}
