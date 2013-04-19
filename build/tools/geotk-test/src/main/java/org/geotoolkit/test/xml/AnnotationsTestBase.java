/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.test.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;
import org.opengis.annotation.Obligation;
import org.opengis.annotation.Specification;

import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Compares JAXB annotations with the UML ones. Subclasses shall implement the abstract methods
 * defined in this class, in order to provide the following information:
 * <p>
 * <ul>
 *   <li>The list of GeoAPI interfaces for which to test the implementation.</li>
 *   <li>The implementation class for a given GeoAPI interface.</li>
 *   <li>The JAXB adapter class for a given GeoAPI interface.</li>
 * </ul>
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.16 (derived from 3.05)
 */
public abstract strictfp class AnnotationsTestBase extends TestBase {
    /**
     * The string used in JAXB annotations for default names or namespaces.
     */
    protected static final String DEFAULT = "##default";

    /**
     * The type being tested, or {@code null} if none. In case of test failure, this information
     * will be used for formatting a message giving the name of class and methods where the failure
     * occurred.
     */
    private String testingClass;

    /**
     * The method being tested, or {@code null} if none. In case of test failure, this information
     * will be used for formatting a message giving the name of class and methods where the failure
     * occurred.
     */
    private String testingMethod;

    /**
     * Creates a new test suite.
     */
    protected AnnotationsTestBase() {
    }

    /**
     * Prints the given message followed by the name of the class being tested.
     */
    private void warning(String message) {
        if (testingClass != null) {
            final StringBuilder buffer = new StringBuilder(message);
            buffer.append(testingClass);
            if (testingMethod != null) {
                buffer.append('.').append(testingMethod).append("()");
            }
            message = buffer.toString();
        }
        System.out.flush();
        System.err.println(message);
        System.err.flush();
    }

    /**
     * If a test failed, reports the class and method names were the failure occurred.
     */
    @After
    public final void printFailureLocation() {
        if (testingClass != null) {
            warning("TEST FAILURE: ");
        }
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
     * intended to have any wrapper (as opposed to {@code null}, which means that a wrapper
     * was expected but not found).
     *
     * @param  type The GeoAPI type.
     * @return The wrapper for the given type.
     */
    protected abstract Class<?> getWrapper(Class<?> type);

    /**
     * Returns the expected namespace for an element defined by the given specification.
     *
     * @param specification The specification.
     * @return The expected namespace.
     */
    protected abstract String getNamespace(Specification specification);

    /**
     * Returns the prefix to use for the given namespace.
     *
     * @param namespace The namespace URI.
     * @return The prefix to use.
     */
    protected abstract String getPrefixForNamespace(String namespace);

    /**
     * Returns the type for an element of the given name. For example in ISO 19139, the type
     * of {@code CI_Citation} is {@code CI_Citation_Type}.
     *
     * @param  rootName The XML root name of the element (usually the same than the UML name).
     * @param  implName The name of the implementation class.
     * @return The name of the XML type for the given element, or {@link #DEFAULT} if none.
     */
    protected abstract String getTypeForElement(String rootName, String implName);

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
     * Returns the given name space if different than {@value #DEFAULT}, or the package
     * name space of the given class otherwise.
     *
     * @param type      The implementation or wrapper class for which to get the package namespace.
     * @param namespace The namespace given by the {@code @XmlRoot} or {@code @XmlElement} annotation.
     * @param uml       The {@code @UML} annotation, or {@code null} if none.
     */
    private String getNamespace(final Class<?> type, String namespace, final UML uml) {
        assertFalse("Missing namespace.", namespace.trim().isEmpty());
        final XmlSchema schema = type.getPackage().getAnnotation(XmlSchema.class);
        assertNotNull("Missing @XmlSchema package annotation.", schema);
        final String schemaNamespace = schema.namespace();
        assertFalse("Missing namespace in @XmlSchema package annotation.", schemaNamespace.trim().isEmpty());
        assertFalse("Redundant namespace declaration.", namespace.equals(schemaNamespace));
        if (DEFAULT.equals(namespace)) {
            namespace = schemaNamespace;
        }
        if (uml != null) {
            final Specification specification = uml.specification();
            if (specification != null) {
                // TODO: following check is a temporary patch.
                if (namespace.equals("http://www.isotc211.org/2005/srv")) {
                    return namespace;
                }
                assertEquals("Wrong namespace for the ISO specification.", getNamespace(specification), namespace);
            }
        }
        return namespace;
    }

    /**
     * Tests the annotations on adapters. The test is applied for everything returned
     * by the {@link #getTestedTypes()} method, including both metadata interfaces and
     * code lists. More specifically this method tests that:
     * <p>
     * <ul>
     *   <li>The wrapper have a getter and a setter method declared in the same class.</li>
     *   <li>The getter method is annotated with {@code @XmlElement} <strong>or</strong>
     *       {@code @XmlElementRef}, but not both</li>
     *   <li>{@code @XmlElementRef} is used only in parent classes, not in leaf classes.</li>
     *   <li>The name declared in {@code @XmlElement} matches the {@code @UML} identifier.</li>
     * </ul>
     */
    @Test
    public void testAdapterAnnotations() {
        for (final Class<?> type : getTestedTypes()) {
            testingClass = type.getName();
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull("Missing @UML annotation.", classUML);
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
            assertNotNull("Missing JAXB adapter.", wrapper);
            if (wrapper.equals(Void.TYPE)) {
                // If the wrapper is intentionally undefined, skip it.
                continue;
            }
            /*
             * Now fetch the getter/setter methods and verify the getter annotation.
             */
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
            assertEquals("Expected @XmlElement XOR @XmlElementRef.",
                    (xmlElem == null), getter.isAnnotationPresent(XmlElementRef.class));
            if (xmlElem != null) {
                assertFalse("Expected @XmlElementRef.", wrapperIsInherited);
                assertEquals("Wrong @XmlElement.", classUML.identifier(), xmlElem.name());
            }
            /*
             * Compare with the implementation annotation.
             */
            final Class<?> impl = getImplementation(type);
            assertTrue("Wrong implementation class.", type.isAssignableFrom(impl));
            if (!CodeList.class.isAssignableFrom(type)) {
                testingClass = impl.getName();
                final XmlRootElement root = impl.getAnnotation(XmlRootElement.class);
                assertNotNull("Missing @XmlRootElement.", root);
                final String rootNamespace = getNamespace(impl, root.namespace(), classUML);
                if (xmlElem != null) {
                    assertEquals("Inconsistent @XmlRootElement name.", xmlElem.name(), root.name());
                    assertEquals("Inconsistent @XmlRootElement namespace.",
                            getNamespace(wrapper, xmlElem.namespace(), null), rootNamespace);
                }
            }
        }
        // On success, disable the report of failed class or method.
        testingClass  = null;
        testingMethod = null;
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
            assertTrue("Not an interface.", type.isInterface());
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull("Missing @UML annotation.", classUML);
            /*
             * Get the implementation class, which is mandatory.
             */
            final Class<?> impl = getImplementation(type);
            testingClass = impl.getName();
            assertNotNull("No implementation found.", impl);
            assertNotSame("No implementation found.", type, impl);
            /*
             * Get the @XmlRootElement annotation and compare.
             */
            final XmlRootElement xmlRoot = impl.getAnnotation(XmlRootElement.class);
            assertNotNull("Missing @XmlRootElement annotation.", xmlRoot);
            assertEquals("Wrong @XmlRootElement.", classUML.identifier(), xmlRoot.name());
            /*
             * Check the @XmlType attribute.
             */
            final XmlType xmlType = impl.getAnnotation(XmlType.class);
            assertNotNull("Missing @XmlType annotation.", xmlType);
            assertEquals("Wrong @XmlType name.", getTypeForElement(xmlRoot.name(), impl.getSimpleName()), xmlType.name());
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
                     * intentionally not annotated.
                     */
                    continue;
                }
                testingMethod = name;
                final UML methodUML = method.getAnnotation(UML.class);
                assertNotNull("Missing @UML annotation.", methodUML);
                /*
                 * Get the annotation from the method. If the method is not annotated,
                 * get the annotation from the field instead.
                 */
                final Method methodImpl;
                try {
                    methodImpl = impl.getMethod(name, (Class<?>[]) null);
                } catch (NoSuchMethodException ex) {
                    fail("Missing implementation: " + ex);
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
                    // Note: lines with the "[WARNING]" string are highlighted by Jenkins.
                    warning("[WARNING] Missing @XmlElement annotation for ");
                    continue;
                }
                assertEquals("Annotation mismatch.", methodUML.identifier(), xmlElem.name());
                /*
                 * Check that the namespace is coherent with the UML specification and not
                 * redundant with the @XmlSchema. Then check that the package @XmlNs declare
                 * all the namespace found in the implementation.
                 */
                final String namespace = xmlElem.namespace();
                final String resolvedNS = getNamespace(impl, namespace, methodUML);
                if (!DEFAULT.equals(namespace)) {
                    assertFalse("Redundant namespace declaration.", namespace.equals(
                            impl.getPackage().getAnnotation(XmlSchema.class).namespace()));
                }
                boolean found = false;
                for (final XmlNs ns : impl.getPackage().getAnnotation(XmlSchema.class).xmlns()) {
                    final String nsURI = ns.namespaceURI();
                    assertEquals("Unexpected namespace prefix.", getPrefixForNamespace(nsURI), ns.prefix());
                    if (resolvedNS.equals(nsURI)) {
                        found = true;
                    }
                }
                assertTrue("Undeclared namespace in package @XmlSchema", found);
                /*
                 * Check the 'required' declaration.
                 */
                assertEquals("Wrong @XmlElement.required",
                        methodUML.obligation() == Obligation.MANDATORY, xmlElem.required());
            }
        }
        // On success, disable the report of failed class or method.
        testingClass  = null;
        testingMethod = null;
    }
}
