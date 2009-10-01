/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.annotation.XmlElement;
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
     *
     * @param  type The GeoAPI type.
     * @return The wrapper for the given type.
     */
    protected abstract Class<?> getWrapper(Class<?> type);

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
            testingClass = type.getName();
            assertTrue(message("Not an interface: "), type.isInterface());
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull(message("Missing @UML annotation for "), classUML);
            /*
             * Checks the annotation on the wrapper, if there is one.
             */
            final Class<?> wrapper = getWrapper(type);
            if (wrapper != null) {
                testingClass = wrapper.getName();
                // TODO: Test for the annotated method here.
            }
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
            assertEquals(message("Annotation mismatch for "), classUML.identifier(), xmlRoot.name());
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
