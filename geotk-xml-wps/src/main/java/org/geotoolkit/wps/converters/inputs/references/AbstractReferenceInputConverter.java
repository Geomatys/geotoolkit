/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Reference;

/**
 * TODO v200 in/out difeeence
 *
 * @author Quentin Boileau (Geomatys).
 */


public abstract class AbstractReferenceInputConverter<T> extends WPSDefaultConverter<Reference, T> {

    @Override
    public Class<Reference> getSourceClass() {
        return Reference.class;
    }

    @Override
    public abstract Class<T> getTargetClass();

    /**
     * Convert a Reference {@link InputReference input} or
     * {@link OutputReference output} into the requested {@code Object}.
     *
     * @param source Reference
     * @return Object
     * @throws UnconvertibleObjectException
     */
    @Override
    public abstract T convert(final Reference source, Map<String, Object> params) throws UnconvertibleObjectException;

    protected static URLConnection connect(final Reference ref) throws UnconvertibleObjectException {
        final String brutHref;
        if (ref == null || (brutHref = ref.getHref()) == null) {
            throw new UnconvertibleObjectException("Null reference given.");
        }

        final URL href;
        try {
            final String decoded = URLDecoder.decode(brutHref, "UTF-8");
            href = new URL(decoded);
        } catch (UnsupportedEncodingException | MalformedURLException ex) {
            throw new UnconvertibleObjectException("Invalid reference href: "+brutHref, ex);
        }

        final URLConnection connection;
        try {
            connection = href.openConnection();
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Cannot connect to reference url:"+href, ex);
        }

        for (final Reference.Header header : ref.getHeader()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

        addBody(connection, ref.getBody(), ref.getBodyReference());

        return connection;
    }

    private static void addBody(final URLConnection connection, Object body, final Reference.BodyReference bodyReference) {
        InputStream bodyStream = null;
        if (body != null) {
            if (!(body instanceof String)) {
                // TODO: Check if it can be something else than xml.
                connection.setRequestProperty("content-type", "text/xml");
                final Marshaller marshaller;
                try {
                    marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
                } catch (JAXBException ex) {
                    throw new UnconvertibleObjectException("Internal server error.", ex);
                }

                // Write request content
                final StringWriter writer = new StringWriter();
                try {
                    marshaller.marshal(body, writer);
                } catch (JAXBException ex) {
                    throw new UnconvertibleObjectException("The requested body is not supported.", ex);
                }

                body = writer.getBuffer().toString();

                try {
                    WPSMarshallerPool.getInstance().recycle(marshaller);
                } catch (Exception e) {
                    // We don't want to stop communication if the xml writer cannot
                    // be disposed. The only important fact is that it's already
                    // done its part.
                    LOGGER.log(Level.WARNING, "A marshaller cannot be recycled", e);
                }
            }

            bodyStream = new ByteArrayInputStream(((String)body).getBytes(StandardCharsets.UTF_8));

        } else if (bodyReference != null) {
            try {
                bodyStream = new URL(URLDecoder.decode(bodyReference.getHref(), "UTF-8")).openStream();
            } catch (Exception e) {
                throw new UnconvertibleObjectException("invalid body reference: "+bodyReference.getHref(), e);
            }
        }

        if (bodyStream != null) {
            connection.setDoOutput(true);
            try (final InputStream inClose = bodyStream;
                    final OutputStream outStream = connection.getOutputStream()) {
                IOUtilities.copy(bodyStream, outStream);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Cannot post reference body", ex);
            }
        }
    }

    protected InputStream getInputStreamFromReference(final Reference source) throws UnconvertibleObjectException {
        ArgumentChecks.ensureNonNull("source", source);
        try {
            return connect(source).getInputStream();
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Can't reach the reference data.", ex);
        }
    }
}
