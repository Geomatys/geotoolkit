/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcts.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.xml.MarshallerPool;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Marshal / unmarshal helpers for WCTS 05-013 0.3.0 XML documents.
 *
 * <p>
 * Unlike OGC OpenLS, WCTS operations have no shared outer envelope: each operation's request or
 * response type carries its own {@code @XmlRootElement}, so these helpers are generic over the
 * root type rather than dispatching through a single envelope class.
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class WCTSBindingUtilities {

    private WCTSBindingUtilities() {}

    /**
     * Marshal the given JAXB-annotated WCTS request or response object.
     *
     * @param object object to marshal, its class must carry {@code @XmlRootElement}
     * @return the marshalled XML document, UTF-8 encoded
     * @throws JAXBException if marshalling fails
     */
    public static byte[] marshall(final Object object) throws JAXBException {
        ArgumentChecks.ensureNonNull("object", object);
        final MarshallerPool pool = WCTSMarshallerPool.getInstance();
        final Marshaller marshaller = pool.acquireMarshaller();
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(object, out);
            return out.toByteArray();
        } finally {
            pool.recycle(marshaller);
        }
    }

    /**
     * Unmarshal a WCTS XML document as the given expected type.
     *
     * @param source one of {@link File}, {@link InputSource}, {@link InputStream}, {@link Node},
     * {@link Reader} or {@link Source}
     * @param type expected root type
     * @return the unmarshalled object
     * @throws JAXBException if unmarshalling fails, or if the document does not contain an
     * element of the expected type
     */
    public static <T> T unmarshall(final Object source, final Class<T> type) throws JAXBException {
        ArgumentChecks.ensureNonNull("source", source);
        ArgumentChecks.ensureNonNull("type", type);
        final MarshallerPool pool = WCTSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final Object unmarshalled;
        try {
            unmarshalled = unmarshallSource(source, unmarshaller);
        } finally {
            pool.recycle(unmarshaller);
        }

        final Object value = unmarshalled instanceof JAXBElement<?> element ? element.getValue() : unmarshalled;
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new JAXBException(String.format(
                "Read object is not compatible with queried type.%nExpected: %s%nBut was: %s",
                type, value == null ? null : value.getClass()));
    }

    private static Object unmarshallSource(final Object source, final Unmarshaller unmarshaller) throws JAXBException {
        if (source instanceof File file) {
            return unmarshaller.unmarshal(file);
        } else if (source instanceof InputSource inputSource) {
            return unmarshaller.unmarshal(inputSource);
        } else if (source instanceof InputStream inputStream) {
            return unmarshaller.unmarshal(inputStream);
        } else if (source instanceof Node node) {
            return unmarshaller.unmarshal(node);
        } else if (source instanceof Reader reader) {
            return unmarshaller.unmarshal(reader);
        } else if (source instanceof Source xmlSource) {
            return unmarshaller.unmarshal(xmlSource);
        } else {
            throw new IllegalArgumentException("Source object is not a valid class: " + source.getClass());
        }
    }
}
