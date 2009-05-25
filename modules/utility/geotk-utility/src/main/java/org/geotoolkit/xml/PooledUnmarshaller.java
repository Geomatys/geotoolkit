/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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

import java.net.URL;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.geotoolkit.lang.Decorator;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * Wraps a {@link Unmarshaller} in order to have some control on the modifications applied on it.
 * This is done in order to make the unmarshaller safer for reuse.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
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
     * @throws PropertyException If an error occured while restoring a property.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void reset(final Object key, final Object value) throws PropertyException {
        if (key instanceof String) {
            unmarshaller.setProperty((String) key, value);
        } else if (AttachmentUnmarshaller.class.equals(key)) {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller) value);
        } else if (Schema.class.equals(key)) {
            unmarshaller.setSchema((Schema) value);
        } else if (Listener.class.equals(key)) {
            unmarshaller.setListener((Listener) value);
        } else {
            unmarshaller.setAdapter((Class) key, (XmlAdapter) value);
        }
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final InputStream input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final URL input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final File input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Reader input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final InputSource input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Node input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final Node input, final Class<T> declaredType) throws JAXBException {
        return unmarshaller.unmarshal(input, declaredType);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final Source input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final Source input, final Class<T> declaredType) throws JAXBException {
        return unmarshaller.unmarshal(input, declaredType);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final XMLStreamReader input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLStreamReader input, final Class<T> declaredType) throws JAXBException {
        return unmarshaller.unmarshal(input, declaredType);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public Object unmarshal(final XMLEventReader input) throws JAXBException {
        return unmarshaller.unmarshal(input);
    }

    /**
     * Delegates the unmarshalling to the wrapped unmarshaller.
     */
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLEventReader input, final Class<T> declaredType) throws JAXBException {
        return unmarshaller.unmarshal(input, declaredType);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return unmarshaller.getUnmarshallerHandler();
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public void setProperty(String name, final Object value) throws PropertyException {
        name = convertPropertyKey(name);
        super.setProperty(name, value);
        unmarshaller.setProperty(name, value);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public Object getProperty(final String name) throws PropertyException {
        return unmarshaller.getProperty(convertPropertyKey(name));
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
     */
    @Override
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        super.setAdapter(type, adapter);
        unmarshaller.setAdapter(type, adapter);
    }

    /**
     * Delegates to the wrapped unmarshaller.
     */
    @Override
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        return unmarshaller.getAdapter(type);
    }

    /**
     * Delegates to the wrapped unmarshaller. The initial state will be saved
     * if it was not already done, for future restoration by {@link #reset()}.
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
