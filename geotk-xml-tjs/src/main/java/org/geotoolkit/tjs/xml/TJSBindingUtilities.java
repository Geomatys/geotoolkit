/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.tjs.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.xml.MarshallerPool;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Utility class to unmarshall TJS request/response objects.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class TJSBindingUtilities {

    private TJSBindingUtilities() {
    }

    /**
     * Unmarshal XML data from the specified source and return the resulting content tree.
     *
     * @param source object to unmarshall
     * @return the newly created root object of the java content tree.
     * @throws JAXBException if any unexpected error occurs while unmarshalling
     */
    public static Object unmarshall(final Object source) throws JAXBException {
        final MarshallerPool pool = TJSMarshallerPool.getInstance();
        final Unmarshaller unMarshaller = pool.acquireUnmarshaller();
        Object unmarshalled = unmarshall(source, unMarshaller);
        pool.recycle(unMarshaller);

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement<?>) unmarshalled).getValue();
        }
        return unmarshalled;
    }

    /**
     * Unmarshal XML data from the specified source and return the resulting content tree, cast
     * to the given data type.
     *
     * @param source object to unmarshall
     * @param dataType expected result type
     * @return the newly created root object of the java content tree
     * @throws JAXBException if any unexpected error occurs while unmarshalling, or if the
     *         unmarshalled content is not compatible with {@code dataType}
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshall(final Object source, final Class<T> dataType) throws JAXBException {
        ArgumentChecks.ensureNonNull("Return type", dataType);

        final Object unmarshalled = unmarshall(source);
        if (unmarshalled == null) {
            throw new JAXBException("No value available in given data source");
        } else if (dataType.isAssignableFrom(unmarshalled.getClass())) {
            return (T) unmarshalled;
        } else {
            throw new JAXBException(String.format(
                    "Read object is not compatible with queried type.%nExpected: %s%nBut was: %s",
                    dataType, unmarshalled.getClass()));
        }
    }

    private static Object unmarshall(final Object source, final Unmarshaller unMarshaller)
            throws JAXBException, IllegalArgumentException {
        if (source instanceof File) {
            return unMarshaller.unmarshal((File) source);
        } else if (source instanceof InputSource) {
            return unMarshaller.unmarshal((InputSource) source);
        } else if (source instanceof InputStream) {
            return unMarshaller.unmarshal((InputStream) source);
        } else if (source instanceof Node) {
            return unMarshaller.unmarshal((Node) source);
        } else if (source instanceof Reader) {
            return unMarshaller.unmarshal((Reader) source);
        } else if (source instanceof Source) {
            return unMarshaller.unmarshal((Source) source);
        } else if (source instanceof URL) {
            return unMarshaller.unmarshal((URL) source);
        } else if (source instanceof XMLEventReader) {
            return unMarshaller.unmarshal((XMLEventReader) source);
        } else if (source instanceof XMLStreamReader) {
            return unMarshaller.unmarshal((XMLStreamReader) source);
        } else if (source instanceof String) {
            return unMarshaller.unmarshal(new java.io.StringReader((String) source));
        } else {
            throw new IllegalArgumentException("Source object is not a valid class :" + source.getClass());
        }
    }
}
