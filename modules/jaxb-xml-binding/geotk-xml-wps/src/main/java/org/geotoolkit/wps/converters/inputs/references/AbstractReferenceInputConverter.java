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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.feature.type.FeatureType;

/**
 *
 *
 * @author Quentin Boileau (Geomatys).
 */
public abstract class AbstractReferenceInputConverter<T> extends WPSDefaultConverter<ReferenceType, T> {

    @Override
    public Class<? super ReferenceType> getSourceClass() {
        return ReferenceType.class;
    }

    @Override
    public abstract Class<? extends T> getTargetClass();

    /**
     * Convert a ReferenceType {@link InputReferenceType input} or {@link OutputReferenceType output} into the requested {@code Object}.
     * @param source ReferenceType
     * @return Object
     * @throws NonconvertibleObjectException
     */
    @Override
    public abstract T convert(final ReferenceType source, Map<String, Object> params) throws NonconvertibleObjectException;

     /**
     * Get the JAXPStreamFeatureReader to read feature. If there is a schema defined, the JAXPStreamFeatureReader will
     * use it overwise it will use the embedded.
     *
     * @param source
     * @return
     * @throws MalformedURLException
     * @throws JAXBException
     * @throws IOException
     */
    protected XmlFeatureReader getFeatureReader(final ReferenceType source) throws MalformedURLException, JAXBException, IOException {

        JAXPStreamFeatureReader featureReader = new JAXPStreamFeatureReader();
        try {
            final XmlFeatureTypeReader xsdReader = new JAXBFeatureTypeReader();
            final String schema = source.getSchema();

            if (schema != null) {
                final URL schemaURL = new URL(schema);
                final List<FeatureType> featureTypes = xsdReader.read(schemaURL);
                if (featureTypes != null) {
                    featureReader = new JAXPStreamFeatureReader(featureTypes);
                }
            } else {
                featureReader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);
            }
        } catch(JAXBException ex) {
            featureReader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);
        }
        return featureReader;
    }

    protected InputStream getInputStreamFromReference (final ReferenceType source) throws NonconvertibleObjectException {

        ArgumentChecks.ensureNonNull("source", source);
        String method = null;

        if (source instanceof InputReferenceType) {
            method = ((InputReferenceType) source).getMethod();
        } else if (source instanceof OutputReferenceType) {
            method = "GET";
        }

        InputStream stream = null;

        if (method.equalsIgnoreCase("GET")) {

            try {
                String href = source.getHref().replaceAll("&amp;", "&");
                final URL url = new URL(href);
                stream = url.openStream();

            } catch (UnsupportedEncodingException ex) {
                throw new NonconvertibleObjectException("Invalid reference href.", ex);
            } catch (IOException ex) {
                throw new NonconvertibleObjectException("Can't reach the reference data.", ex);
            }

        } else if (method.equalsIgnoreCase("POST")) {

            stream = postReferenceRequest((InputReferenceType)source);
        }
        return stream;
    }


    /**
     * Return an Input {@link InputStream stream} of a reference using POST method.
     * @param reference
     * @return
     * @throws NonconvertibleObjectException
     */
    private static InputStream postReferenceRequest(final InputReferenceType reference) throws NonconvertibleObjectException {

        String href = null;
        try {
            href = URLDecoder.decode(reference.getHref(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new NonconvertibleObjectException("Invalid reference href.", ex);
        }


        final List<InputReferenceType.Header> headers = reference.getHeader();

        InputStream stream = null;
        OutputStream requestOS = null;
        Marshaller marshaller = null;

        try {
            final Object body = getReferenceBody(reference);
            if (body == null) {
                throw new NonconvertibleObjectException("No reference body found for the POST request.");
            }

            marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();

            // Make request
            final URLConnection conec = new URL(href).openConnection();
            conec.setConnectTimeout(60);
            conec.setDoOutput(true);
            conec.setRequestProperty("content-type", "text/xml");
            for (final InputReferenceType.Header header : headers) {
                conec.addRequestProperty(header.getKey(), header.getValue());
            }

            // Write request content
            requestOS = conec.getOutputStream();
            marshaller.marshal(body, requestOS);

            // Parse the response
            stream = conec.getInputStream();

        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("The requested body is not supported.", ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Can't reach the reference URL or the reference body URL.", ex);
        } finally {
            if (marshaller != null) {
                WPSMarshallerPool.getInstance().release(marshaller);
            }
            if (requestOS != null) {
                try {
                    requestOS.close();
                } catch (IOException ex) {
                    throw new NonconvertibleObjectException("Can't close the output stream.", ex);
                }
            }
        }
        return stream;
    }

    /**
     * Reach and unMarshall the body of a request.
     *
     * @param reference
     * @return
     * @throws UnsupportedEncodingException
     * @throws JAXBException
     * @throws MalformedURLException
     */
    private static Object getReferenceBody(final InputReferenceType reference)
            throws UnsupportedEncodingException, JAXBException, MalformedURLException {

        Object obj = null;

        if ( reference.getBody() != null ) {
            obj = reference.getBody();

        } else if (reference.getBodyReference() != null) {

            final String href = reference.getBodyReference().getHref();

            Unmarshaller unmarshaller = null;
            try {
                unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                final URL url = new URL(URLDecoder.decode(href, "UTF-8"));

                obj = unmarshaller.unmarshal(url);

            } finally {
                if (unmarshaller != null) {
                    WPSMarshallerPool.getInstance().release(unmarshaller);
                }
            }
        }
        return obj;
    }

}
