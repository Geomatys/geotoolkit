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
package org.geotoolkit.xml;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ConcurrentModificationException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.PropertyException;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * Base class of {@link PooledMarshaller} and {@link PooledUnmarshaller}.
 * This class provides basic service for saving the initial values of [un]marshaller properties,
 * in order to reset them to their initial values after usage. This is required in order to allow
 * [un]marshaller reuse. In addition this base class translate properties key from JDK 6 names to
 * "endorsed JAR" names if needed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.00
 * @module
 */
abstract class Pooled implements Catching {
    /**
     * The prefix of property names which are internal to Sun implementation of JAXB.
     */
    private static final String INTERNAL = "com.sun.xml.bind.";

    /**
     * {@code true} if the JAXB implementation is the one bundled in JDK 6,
     * or {@code false} if this is an external implementation like a JAR put
     * in the endorsed directory.
     */
    private final boolean internal;

    /**
     * The initial state of the [un]marshaller. Will be filled only as needed,
     * often with null values (which must be supported by the map implementation).
     * <p>
     * <ul>
     *   <li>For each entry having a key of type {@link Class}, the value is the argument
     *       to be given to a {@code marshaller.setFoo(value)} method.</li>
     *   <li>For each entry having a key of type {@link String}, the value is the argument
     *       to be given to the {@code marshaller.setProperty(key, value)} method.</li>
     * </ul>
     */
    private final Map<Object, Object> initial;

    /**
     * The object converters to use during (un)marshalling.
     *
     * @since 3.07
     */
    private ObjectConverters converters;

    /**
     * Default constructor.
     *
     * @param internal {@code true} if the JAXB implementation is the one bundled in JDK 6,
     *        or {@code false} if this is an external implementation like a JAR put in the
     *        endorsed directory.
     */
    Pooled(final boolean internal) {
        this.internal = internal;
        initial = new LinkedHashMap<Object, Object>();
        converters = ObjectConverters.DEFAULT;
    }

    /**
     * Resets the marshaller in its initial state.
     *
     * @throws JAXBException If an error occurred while restoring a property.
     */
    public final void reset() throws JAXBException {
        for (final Map.Entry<Object,Object> entry : initial.entrySet()) {
            reset(entry.getKey(), entry.getValue());
        }
        initial.clear();
    }

    /**
     * Resets the given marshaller property to its initial state. This method is invoked
     * automatically by the {@link #reset()} method. The key is either a {@link String}
     * or a {@link Class}. If this is a string, then the value should be given to the
     * {@code setProperty(key, value)} method. Otherwise the value should be given to
     * {@code setFoo(value)} method where "Foo" is determined from the key.
     *
     * @param  key The property to reset.
     * @param  value The initial value to give to the property.
     * @throws JAXBException If an error occurred while restoring a property.
     */
    protected abstract void reset(final Object key, final Object value) throws JAXBException;

    /**
     * Returns {@code true} if the state is already saved for the given key.
     */
    final boolean containsKey(final Class<?> key) {
        return initial.containsKey(key);
    }

    /**
     * Saves the current state of a property.
     */
    final <E> void save(final Class<E> type, final E value) {
        if (initial.put(type, value) != null) {
            // Should never happen, unless on concurrent changes in a backgroung thread.
            throw new ConcurrentModificationException(type.toString());
        }
    }

    /**
     * Converts a property key from the JAXB name to the underlying implementation name.
     * This apply only to property key in the {@code "com.sun.xml.bind"} namespace.
     *
     * @param  key The JAXB property key.
     * @return The property key to use.
     */
    final String convertPropertyKey(String key) {
        if (internal && key.startsWith(INTERNAL)) {
            key = "com.sun.xml.internal.bind." + key.substring(INTERNAL.length());
        }
        return key;
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     * It saves the initial state if it was not already done, but subclasses will
     * need to complete the work.
     */
    public void setProperty(final String name, final Object value) throws PropertyException {
        if (!initial.containsKey(name)) {
            if (initial.put(name, getProperty(name)) != null) {
                // Should never happen, unless on concurrent changes in a backgroung thread.
                throw new ConcurrentModificationException(name);
            }
        }
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     */
    public abstract Object getProperty(final String name) throws PropertyException;

    /**
     * Delegates to {@code setAdapter(adapter.getClass(), adapter)} as specified
     * in {@code [Un]Marshaller} javadoc.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public final void setAdapter(final XmlAdapter adapter) {
        setAdapter((Class) adapter.getClass(), adapter);
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     * It saves the initial state if it was not already done, but subclasses will
     * need to complete the work.
     */
    @SuppressWarnings("rawtypes")
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        if (!initial.containsKey(type)) {
            save(type, getAdapter(type));
        }
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     */
    @SuppressWarnings("rawtypes")
    public abstract <A extends XmlAdapter> A getAdapter(final Class<A> type);

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     * It saves the initial state if it was not already done, but subclasses will
     * need to complete the work.
     */
    public void setSchema(final Schema schema) {
        if (!containsKey(Schema.class)) {
            save(Schema.class, getSchema());
        }
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     */
    public abstract Schema getSchema();

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     * It saves the initial state if it was not already done, but subclasses will
     * need to complete the work.
     */
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        if (!initial.containsKey(ValidationEventHandler.class)) {
            save(ValidationEventHandler.class, getEventHandler());
        }
    }

    /**
     * A method which is common to both {@code Marshaller} and {@code Unmarshaller}.
     */
    public abstract ValidationEventHandler getEventHandler() throws JAXBException;



    ////////////////                                               ////////////////
    ////////////////   Methods related to the Catching interface   ////////////////
    ////////////////                                               ////////////////

    /**
     * Returns the current object converters. This is {@link ObjectConverters#DEFAULT}
     * unless the user invoked {@link #setObjectConverters(ObjectConverters)}.
     *
     * @since 3.07
     */
    @Override
    public final ObjectConverters getObjectConverters() {
        return converters;
    }

    /**
     * Sets a new object converters to use for (un)marshalling processes.
     *
     * @since 3.07
     */
    @Override
    public final void setObjectConverters(final ObjectConverters converters) {
        this.converters = (converters != null) ? converters : ObjectConverters.DEFAULT;
    }

    /**
     * Must be invoked by subclasses before a {@code try} block performing a (un)marshalling
     * operation. Must be followed by a call to {@code finish()} in a {@code finally} block.
     *
     * {@preformat java
     *     MarshalProcess ctx = begin();
     *     try {
     *         ...
     *     } finally {
     *         ctx.finish();
     *     }
     * }
     *
     * @since 3.07
     */
    final MarshalContext begin() {
        return MarshalContext.begin(converters);
    }
}
