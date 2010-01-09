/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Compares JAXB annotations with the UML ones.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public abstract class AnnotationsTest {
    /**
     * For formatting error message only.
     */
    private final StringBuilder buffer;

    /**
     * The type being tested.
     */
    private String testingClass;

    /**
     * The method being tested, or {@code null} if none.
     */
    private String testingMethod;

    /**
     * Creates a new test suite.
     */
    protected AnnotationsTest() {
        buffer = new StringBuilder();
    }

    /**
     * Returns the given message followed by the name of the class being tested.
     */
    private String message(final String text) {
        buffer.setLength(0);
        buffer.append(text).append(testingClass);
        final String method = testingMethod;
        if (method != null) {
            buffer.append('.').append(method).append("()");
        }
        return buffer.toString();
    }

    /**
     * Returns the list of GeoAPI types to be tested. Every elements in the returned
     * list must be interface, except {@link CodeList}.
     *
     * @return The interfaces or code lists to be tested.
     */
    protected abstract Class<?>[] getTestedTypes();

    /**
     * Returns the Geotk implementation for the given GeoAPI interface.
     *
     * @param  type The GeoAPI interface.
     * @return The Geotk implementation for the given interface.
     */
    protected abstract Class<?> getImplementation(Class<?> type);

    /**
     * If the given GeoAPI type, when marshalled to XML, is wrapped in an other XML element,
     * returns the wrapper for that XML element. Otherwise returns {@code null}. Such wrappers
     * are unusual, but the ISO 19139 standard is done that way.
     * <p>
     * This method can return the {@link Void#TYPE} special value if the given type is not
     * intented to have any wrapper (as opposed to {@code null}, which means that a wrapper
     * was expected but not found).
     *
     * @param  type The GeoAPI type.
     * @return The wrapper for the given type.
     */
    protected abstract Class<?> getWrapper(Class<?> type);

    /**
     * Returns the parent of the given interface, or {@code null} if none.
     * The parent interface is expected to be in the same package than the given interface.
     *
     * @param  type The type for which the parent is looked for.
     * @return The parent of the given type, or {@code null}.
     */
    private Class<?> getParent(final Class<?> type) {
        final Package p = type.getPackage();
        for (final Class<?> parent : type.getInterfaces()) {
            if (p.equals(parent.getPackage())) {
                return parent;
            }
        }
        return null;
    }

    /**
     * Tests the annotations on adapters. The test is applied for everything returned
     * by the {@link #getTestedTypes()} method, including both metadata interfaces and
     * code lists.
     */
    @Test
    public void testAdapterAnnotations() {
        for (final Class<?> type : getTestedTypes()) {
            testingClass = type.getName();
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull(message("Missing @UML annotation for "), classUML);
            /*
             * Check the annotation on the wrapper, if there is one. If no wrapper is declared
             * specifically for the current type, check if a wrapper is defined for the parent
             * interface. In such case, the getElement() method is required to be annotated by
             * @XmlElementRef, not @XmlElement, in order to let JAXB infer the name from the
             * actual subclass.
             */
            boolean wrapperIsInherited = false;
            Class<?> wrapper = getWrapper(type);
            if (wrapper == null) {
                Class<?> parent = type;
                while ((parent = getParent(parent)) != null) {
                    wrapper = getWrapper(parent);
                    if (wrapper != null) {
                        wrapperIsInherited = true;
                        break;
                    }
                }
            }
            if (wrapper == null) {
                /*
                 * Do not consider missing wrapper as fatal errors for now, since we known
                 * that some are missing. Instead just report the missing wrapper at build
                 * time.
                 */
                System.err.println(message("Missing adapter for "));
            } else if (!wrapper.equals(Void.TYPE)) {
                testingClass = wrapper.getName();
                final Method getter, setter;
                try {
                    getter = wrapper.getMethod("getElement", (Class<?>[]) null);
                    setter = wrapper.getMethod("setElement", getter.getReturnType());
                } catch (NoSuchMethodException e) {
                    fail(e.toString());
                    continue;
                }
                assertEquals("The setter method must be declared in the same class than the " +
                        "getter method - not in a parent class, to avoid issues with JAXB.",
                        getter.getDeclaringClass(), setter.getDeclaringClass());
                final XmlElement xmlElem = getter.getAnnotation(XmlElement.class);
                assertEquals(message("Expected @XmlElement XOR @XmlElementRef in "),
                        (xmlElem == null), getter.isAnnotationPresent(XmlElementRef.class));
                if (xmlElem != null) {
                    assertFalse(message("Expected @XmlElementRef for "), wrapperIsInherited);
                    assertEquals(message("Wrong @XmlElement for "), classUML.identifier(), xmlElem.name());
                }
            }
        }
    }

    /**
     * Tests the annotations on interfaces (not code list).
     */
    @Test
    public void testInterfaceAnnotations() {
        for (final Class<?> type : getTestedTypes()) {
            if (CodeList.class.isAssignableFrom(type)) {
                // Skip code lists, since they are not the purpose of this test.
                continue;
            }
            testingMethod = null;
            testingClass = type.getName();
            assertTrue(message("Not an interface: "), type.isInterface());
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull(message("Missing @UML annotation for "), classUML);
            /*
             * Get the implementation class, which is mandatory.
             */
            final Class<?> impl = getImplementation(type);
            testingClass = impl.getName();
            assertNotNull(message("No implementation found for "), impl);
            assertNotSame(message("No implementation found for "), type, impl);
            /*
             * Get the @XmlRootElement annotation and compare.
             */
            final XmlRootElement xmlRoot = impl.getAnnotation(XmlRootElement.class);
            assertNotNull(message("Missing @XmlRootElement annotation for "), xmlRoot);
            assertEquals(message("Wrong @XmlRootElement for "), classUML.identifier(), xmlRoot.name());
            /*
             * We do not expect a name attributes in @XmlType since the name
             * is already specified in the @XmlRootElement annotation.
             */
            final XmlType xmlType = impl.getAnnotation(XmlType.class);
            if (xmlType != null && false) { // TODO: this test is disabled for now.
                assertEquals(message("No @XmlType(name) value expected for "), "##default", xmlType.name());
            }
            /*
             * Compare the method annotations.
             */
            for (final Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Deprecated.class)) {
                    // Skip deprecated methods.
                    continue;
                }
                final String name = method.getName();
                if (name.equals("equals") || name.equals("hashCode") || name.equals("doubleValue")) {
                    /*
                     * Do not verify annotations for those methods that we know they are
                     * intentionaly not annotated.
                     */
                    continue;
                }
                testingMethod = name;
                final UML methodUML = method.getAnnotation(UML.class);
                assertNotNull(message("Missing @UML annotation for "), methodUML);
                /*
                 * Get the annotation from the method. If the method is not annotated,
                 * get the annotation from the field instead.
                 */
                final Method methodImpl;
                try {
                    methodImpl = impl.getMethod(name, (Class<?>[]) null);
                } catch (NoSuchMethodException ex) {
                    fail(message("Missing implementation for "));
                    continue;
                }
                XmlElement xmlElem = methodImpl.getAnnotation(XmlElement.class);
                if (xmlElem == null) try {
                    final Field field = impl.getDeclaredField(methodUML.identifier());
                    xmlElem = field.getAnnotation(XmlElement.class);
                } catch (NoSuchFieldException ex) {
                    // Ignore - we will consider that there is no annotation.
                }
                /*
                 * Just display the missing @XmlElement annotation for the method, since we know
                 * that some elements are not yet implemented (and consequently can not yet be
                 * annotated).
                 */
                if (xmlElem == null) {
                    System.err.println(message("Missing @XmlElement annotation for "));
                    continue;
                }
                assertEquals(message("Annotation mismatch for "), methodUML.identifier(), xmlElem.name());
            }
        }
    }
}
