/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import java.net.URL;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.internal.jaxb.MarshalContext;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * Wraps a {@link Unmarshaller} in order to have some control on the modifications
 * applied on it. This is done in order to make the unmarshaller safer for reuse.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
@Decorator(Unmarshaller.class)
final class PooledUnmarshaller extends Pooled implements Unmarshaller {
    /**
     * The wrapper marshaller which does the real work.
     */
    private final Unmarshaller unmarshaller;

    /**
     * Creates a pooled unmarshaller wrapping the given one.
     *
     * @param unmarshaller The unmarshaller to use for the actual work.
     * @param internal {@code true} if the JAXB implementation is the one bundled in JDK 6,
     *        or {@code false} if this is an external implementation like a JAR put in the
     *        endorsed directory.
     */
    PooledUnmarshaller(final Unmarshaller unmarshaller, final boolean internal) {
        super(internal);
        this.unmarshaller = unmarshaller;
    }

    /**
     * Resets the given unmarshaller property to its initial state.
     *
     * @param  key The property to reset.
     * @param  value The initial value to give to the property.
     * @throws JAXBException If an error occurred while restoring a property.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    protected void reset(final Object key, final Object value) throws JAXBException {
        if (key instanceof String) {
            unmarshaller.setProperty((String) key, value);
        } else if (key == AttachmentUnmarshaller.class) {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller) value);
        } else if (key == Schema.class) {
            unmarshaller.setSchema((Schema) value);
        } else if (key == Listener.class) {
            unmarshaller.setListener((Listener) value);
        } else if (key == ValidationEventHandler.class) {
            unmarshaller.setEventHandler((ValidationEventHandler) value);
        } else {
            unmarshaller.setAdapter((Class) key, (XmlAdapter) value);
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final InputStream input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final URL input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final File input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Reader input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final InputSource input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Node input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final Node input, final Class<T> declaredType) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input, declaredType);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Source input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final Source input, final Class<T> declaredType) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input, declaredType);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final XMLStreamReader input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLStreamReader input, final Class<T> declaredType) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input, declaredType);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final XMLEventReader input) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLEventReader input, final Class<T> declaredType) throws JAXBException {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.unmarshal(input, declaredType);
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        final MarshalContext ctx = begin();
        try {
            return unmarshaller.getUnmarshallerHandler();
        } finally {
            ctx.finish();
        }
    }

    /**
     * Delegates to the wrapped unmarshaller. This method is invoked by the parent
     * class if the given name was not one of the {@link XML} constants.
     */
    @Override
    void setStandardProperty(final String name, final Object value) throws PropertyException {
        unmarshaller.setProperty(name, value);
    }

    /**
     * Delegates to the wrapped unmarshaller. This method is invoked by the parent
     * class if the given name was not one of the {@link XML} constants.
     */
    @Override
    Object getStandardProperty(final String name) throws PropertyException {
        return unmarshaller.getProperty(name);
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        super.setAdapter(type, adapter);
        unmarshaller.setAdapter(type, adapter);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        return unmarshaller.getAdapter(type);
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     *
     * @deprecated Replaced by {@link #setSchema(javax.xml.validation.Schema)} in JAXB 2.0.
     */
    @Override
    @Deprecated
    public void setValidating(final boolean validating) throws JAXBException {
        if (!containsKey(Boolean.class)) {
            save(Boolean.class, unmarshaller.isValidating());
        }
        unmarshaller.setValidating(validating);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     *
     * @deprecated Replaced by {@link #getSchema()} in JAXB 2.0.
     */
    @Override
    @Deprecated
    public boolean isValidating() throws JAXBException {
        return unmarshaller.isValidating();
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public void setSchema(final Schema schema) {
        super.setSchema(schema);
        unmarshaller.setSchema(schema);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public Schema getSchema() {
        return unmarshaller.getSchema();
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        super.setEventHandler(handler);
        unmarshaller.setEventHandler(handler);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return unmarshaller.getEventHandler();
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller au) {
        if (!containsKey(AttachmentUnmarshaller.class)) {
            save(AttachmentUnmarshaller.class, unmarshaller.getAttachmentUnmarshaller());
        }
        unmarshaller.setAttachmentUnmarshaller(au);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return unmarshaller.getAttachmentUnmarshaller();
    }

    /**
     * Delegates to the wrapped marshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public void setListener(final Listener listener) {
        if (!containsKey(Listener.class)) {
            save(Listener.class, unmarshaller.getListener());
        }
        unmarshaller.setListener(listener);
    }

    /**
     * Delegates to the wrapped marshaller.
     */
    @Override
    public Listener getListener() {
        return unmarshaller.getListener();
    }
}
