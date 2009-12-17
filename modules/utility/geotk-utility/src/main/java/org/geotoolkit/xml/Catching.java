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
package org.geotoolkit.xml;


/**
 * Common interface for (un)marshallers capable to catch errors which may occur while processing
 * some kind of objects. This allows client code to control the behavior of the (un)marshalling
 * process when an element can not be processed. For example if an element in a XML document can
 * not be parsed as a {@linkplain java.net.URL}, the default behavior is to throw an exception
 * which cause the (un)marshalling of the entire document to fail.
 * <p>
 * The default behavior can be changed by invoking {@link #setObjectConverters(ObjectConverters)}
 * with a custom {@link ObjectConverters} instance. For example let suppose that we want to collect
 * the failures in a list without stopping the (un)marshalling process. This could be done as below:
 *
 * {@preformat java
 *     class Warnings extends ObjectConverters {
 *         // The warnings collected during (un)marshalling.
 *         List<String> messages = new ArrayList<String>();
 *
 *         // Collects the warnings and allows the process to continue.
 *         protected <T> boolean exceptionOccured(T value, Class<T> sourceType, Class<T> targetType, Exception e) {
 *             mesages.add(e.getLocalizedMessage());
 *             return true;
 *         }
 *     }
 *
 *     // Unmarshall a XML string, trapping some kind of errors.
 *     // Not all errors are trapped - see the ObjectConverters
 *     // javadoc for more details.
 *     Warnings myWarningList = new Warnings();
 *     Catching.Unmarshaller um = marshallerPool.acquireUnmarshaller();
 *     um.setObjectConverter(myWarningList);
 *     Object obj = um.unmarshal(xml);
 *     marshallerPool.release(um);
 *     if (!myWarningList.isEmpty()) {
 *         // Report here the warnings to the user.
 *     }
 * }
 *
 * {@code ObjectConverters} can also be used for replacing an erroneous URL by a fixed URL.
 * See the {@link ObjectConverters} javadoc for more details.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public interface Catching {
    /**
     * JAXB {@linkplain javax.xml.bind.Marshaller marshaller} combined with the {@link Catching}
     * interface. This is the specialized marshaller returned by {@link MarshallerPool}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @since 3.07
     * @module
     */
    interface Marshaller extends javax.xml.bind.Marshaller, Catching {
    }

    /**
     * JAXB {@linkplain javax.xml.bind.Unmarshaller unmarshaller} combined with the {@link Catching}
     * interface. This is the specialized unmarshaller returned by {@link MarshallerPool}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @since 3.07
     * @module
     */
    interface Unmarshaller extends javax.xml.bind.Unmarshaller, Catching {
    }

    /**
     * Returns the current converters used for some kind of objects found in XML documents.
     * If the {@link #setObjectConverters setObjectConverters} method has never been invoked
     * for the current (un)marshaller, then the default value is {@link ObjectConverters#DEFAULT}.
     *
     * @return The current converters for some kind of objects found in XML documents.
     */
    ObjectConverters getObjectConverters();

    /**
     * Sets a new converters to use for some kind of objects found in XML documents. If this
     * method is never invoked, then failure to parse an {@linkplain java.net.URL} (for example)
     * from a XML document will cause the unmarshalling to fail. Client code can set their
     * own converter which catch the errors instead.
     *
     * @param converters The new converters for some kind of objects found in XML documents.
     */
    void setObjectConverters(ObjectConverters converters);
}
