/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.RandomAccess;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Classes} static methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 2.5
 */
public final class ClassesTest {
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
     * Tests {@link Classes#mostSpecificClass} and {@link Classes#commonClass}.
     */
    @Test
    public void testCommonParent() {
        final Set<Object> types = new HashSet<Object>();

        assertTrue(types.add(new NotSerializableException()));
        assertEquals(NotSerializableException.class, Classes.commonClass     (types));
        assertEquals(NotSerializableException.class, Classes.specializedClass(types));

        assertTrue(types.add(new InvalidObjectException(null)));
        assertEquals(ObjectStreamException.class, Classes.commonClass     (types));
        assertEquals(ObjectStreamException.class, Classes.specializedClass(types));

        assertTrue(types.add(new FileNotFoundException()));
        assertEquals(IOException.class, Classes.commonClass     (types));
        assertEquals(IOException.class, Classes.specializedClass(types));

        assertTrue(types.add(new IOException()));
        assertEquals(IOException.class, Classes.commonClass     (types));
        assertEquals(IOException.class, Classes.specializedClass(types));

        assertTrue(types.add(new Exception()));
        assertEquals(  Exception.class, Classes.commonClass     (types));
        assertEquals(IOException.class, Classes.specializedClass(types));
    }

    /**
     * Tests {@link Classes#commonInterfaces}.
     */
    @Test
    public void testCommonInterfaces() {
        final Set<Class<?>> interfaces = Classes.commonInterfaces(ArrayList.class, HashSet.class);
        assertFalse(interfaces.contains(Set         .class));
        assertFalse(interfaces.contains(List        .class));
        assertTrue (interfaces.contains(Collection  .class));
        assertFalse(interfaces.contains(Iterable    .class));
        assertFalse(interfaces.contains(RandomAccess.class));
        assertTrue (interfaces.contains(Serializable.class));
        assertTrue (interfaces.contains(Cloneable   .class));
    }

    /**
     * Tests {@link Classes#sameInterfaces}.
     */
    @Test
    @SuppressWarnings("unchecked") // We break consistency on purpose for one test.
    public void testSameInterfaces() {
        assertTrue (Classes.sameInterfaces(StringBuilder.class, String.class, CharSequence.class));
        assertTrue (Classes.sameInterfaces(StringBuilder.class, String.class, Serializable.class));
        assertFalse(Classes.sameInterfaces((Class)  File.class, String.class, CharSequence.class));
        assertTrue (Classes.sameInterfaces(         File.class, String.class, Serializable.class));
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
        final Class<?>[] s = new Class[] {Set.class};
        final Class<ClassesTest> c = ClassesTest.class;
        assertNull(Classes.boundOfParameterizedAttribute(c.getMethod("getter0", g)));
        assertNull(Classes.boundOfParameterizedAttribute(c.getMethod("setter0", s)));
        assertEquals(Long   .class, Classes.boundOfParameterizedAttribute(c.getField ("attrib2"   )));
        assertEquals(Integer.class, Classes.boundOfParameterizedAttribute(c.getMethod("getter1", g)));
        assertEquals(Byte   .class, Classes.boundOfParameterizedAttribute(c.getMethod("getter2", g)));
        assertEquals(Object .class, Classes.boundOfParameterizedAttribute(c.getMethod("getter3", g)));
        assertEquals(String .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter1", s)));
        assertEquals(Short  .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter2", s)));
        assertEquals(Object .class, Classes.boundOfParameterizedAttribute(c.getMethod("setter3", s)));
    }

    public Set<? extends Long> attrib2 = null;
    public Set                 getter0() {return null;} // Intentionnaly unparameterized.
    public Set<       Integer> getter1() {return null;}
    public Set<? extends Byte> getter2() {return null;}
    public Set<? super  Float> getter3() {return null;}
    public void setter0(Set                  dummy) {}  // Intentionnaly unparameterized.
    public void setter1(Set<         String> dummy) {}
    public void setter2(Set<? extends Short> dummy) {}
    public void setter3(Set<? super  Double> dummy) {}
}
