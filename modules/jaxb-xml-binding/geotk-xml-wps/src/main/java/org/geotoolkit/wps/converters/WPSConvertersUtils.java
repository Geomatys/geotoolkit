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
package org.geotoolkit.wps.converters;

import com.vividsolutions.jts.geom.Geometry;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.DefaultFeatureType;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.feature.type.DefaultPropertyDescriptor;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.wps.converters.inputs.AbstractInputConverter;
import org.geotoolkit.wps.converters.outputs.complex.AbstractComplexOutputConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class WPSConvertersUtils {

    /**
     * Fix the CRS problem for a Feature or a FeatureCollection
     *
     * @param dataValue a Feature or a FeatureCollection
     * @return the sale Feature/FeatureCollection fixed
     * @throws CstlServiceException
     */
    public static Object fixFeature(final Object dataValue) throws FactoryException {

        if (dataValue instanceof Feature) {

            final Feature featureIN = (Feature) dataValue;
            DefaultFeatureType ft = (DefaultFeatureType) featureIN.getType();
            fixFeatureType(featureIN, ft);

            return featureIN;
        }

        if (dataValue instanceof FeatureCollection) {
            final FeatureCollection featureColl = (FeatureCollection) dataValue;

            DefaultFeatureType ft = (DefaultFeatureType) featureColl.getFeatureType();
            final FeatureIterator featureIter = featureColl.iterator();
            if (featureIter.hasNext()) {
                final Feature feature = featureIter.next();
                fixFeatureType(feature, ft);
            }
            featureIter.close();
            return featureColl;
        }

        throw new IllegalArgumentException("Invalid Feature");
    }

    /**
     * Fix a FeatureType in spread the geometry CRS from a feature to the geometry descriptor CRS
     *
     * @param featureIN feature with geometry used to fix the geometry descriptor
     * @param type the featureType to fix
     * @throws CstlServiceException
     */
    private static void fixFeatureType(final Feature featureIN, DefaultFeatureType type) throws FactoryException {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.copy(type);

        //Fetch each geometry, get his CRS and 
        for (Property property : featureIN.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final String propertyName = property.getName().getLocalPart();
                final Geometry propertyGeom = (Geometry) property.getValue();
                final CoordinateReferenceSystem extractCRS = JTS.findCoordinateReferenceSystem(propertyGeom);

                final Iterator<PropertyDescriptor> ite = type.getDescriptors().iterator();

                while (ite.hasNext()) {
                    final DefaultPropertyDescriptor propertyDesc = (DefaultPropertyDescriptor) ite.next();

                    if (propertyDesc.getName().getLocalPart().equals(propertyName)) {
                        final DefaultGeometryType geomType = (DefaultGeometryType) propertyDesc.getType();
                        geomType.setCoordinateReferenceSystem(extractCRS);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Convert a string to a binding class. If the binding class isn't a primitive like Integer, Double, .. we search
     * into the converter list if found a match.
     *
     * @param data string to convert
     * @param binding wanted class
     * @return converted object
     * @throws CstlServiceException if there is no match found
     */
    public static <T> Object convertFromString(final String data, final Class binding) throws NonconvertibleObjectException {

        Object convertedData = null; //resulting Object

        ObjectConverter<String, T> converter;//converter
        try {
            //try to convert into a primitive type
            converter = ConverterRegistry.system().converter(String.class, binding);
        } catch (NonconvertibleObjectException ex) {
            //try to convert with some specified converter
            converter = WPSIO.getConverter(binding, WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL, null, null, null);

            if (converter == null) {
                throw new NonconvertibleObjectException("Converter can't be found.");
            }
        }
        convertedData = converter.convert(data);
        return convertedData;
    }

    /**
     * Convert an object into a String.
     *
     * @param data
     * @return toString object.
     */
    public static String convertToString(final Object data) {
        String out = null;

        if (data != null) {
            if (data instanceof CoordinateReferenceSystem) {
                out = IdentifiedObjects.getIdentifier((CoordinateReferenceSystem) data);
            } else {
                out = String.valueOf(data);
            }
        }
        return out;
    }

    /**
     * Get an convert data from a reference for an expected binding
     *
     * @param expectedClass
     * @param inputObject
     * @param schema
     * @param mime
     * @param encoding
     * @param inputID
     * @return
     * @throws CstlServiceException
     */
    public static Object convertFromComplex(final Class expectedClass, final ComplexDataType complex) throws NonconvertibleObjectException {

        final String mime = complex.getMimeType();
        final String encoding = complex.getEncoding();
        final String schema = complex.getSchema();

        final List<Object> content = complex.getContent();
        
        //remove white spaces
        if (content != null) {
            final Iterator<Object> ite = content.iterator();
            while (ite.hasNext()) {
                final Object obj = ite.next();
                if (obj == null || (obj instanceof String && ((String) obj).trim().isEmpty())) {
                    ite.remove();
                }
            }
        }
        
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractInputConverter.IN_DATA, content);
        parameters.put(AbstractInputConverter.IN_MIME, mime);
        parameters.put(AbstractInputConverter.IN_SCHEMA, schema);
        parameters.put(AbstractInputConverter.IN_ENCODING, encoding);

        final ObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema);

        if (converter == null) {
            throw new NonconvertibleObjectException("Input complex not supported, no converter found.");
        }

        return converter.convert(parameters);
    }

    /**
     * Get an convert data from a reference for an expected binding
     *
     * @param expectedClass
     * @param inputObject
     * @param schema
     * @param mime
     * @param encoding
     * @param inputID
     * @return
     * @throws CstlServiceException
     */
//    public static ComplexDataType convertToComplex(final Object object, final ComplexDataType complex, final Class clazz)
//            throws NonconvertibleObjectException {
//        
//        ComplexDataType complex = null;
//        
//        final Map<String, Object> parameters = new HashMap<String, Object>();
//        parameters.put(AbstractComplexOutputConverter.OUT_DATA, outputValue);
//        parameters.put(AbstractComplexOutputConverter.OUT_TMP_DIR_PATH, WPSUtils.getTempDirectoryPath());
//        parameters.put(AbstractComplexOutputConverter.OUT_TMP_DIR_URL, WPSUtils.getTempDirectoryURL(serviceURL));
//        parameters.put(AbstractComplexOutputConverter.OUT_ENCODING, requestedOutput.getEncoding());
//        parameters.put(AbstractComplexOutputConverter.OUT_MIME, requestedOutput.getMimeType());
//        parameters.put(AbstractComplexOutputConverter.OUT_SCHEMA, requestedOutput.getSchema());
//
//        final ObjectConverter converter = WPSIO.getConverter(outClass, WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX,
//                requestedOutput.getMimeType(), requestedOutput.getEncoding(), requestedOutput.getSchema());
//        if (converter == null) {
//            throw new CstlServiceException("Output complex not supported, no converter found.",
//                    OPERATION_NOT_SUPPORTED, outputIdentifier);
//        }
//
//         data.setComplexData((ComplexDataType) converter.convert(parameters));
//    }
    /**
     * Get an convert data from a reference for an expected binding
     *
     * @param href
     * @param method
     * @param mime
     * @param encoding
     * @param schema
     * @param expectedClass
     * @return an object
     * @throws NonconvertibleObjectException if something went wrong
     */
    public static Object convertFromReference(final InputReferenceType reference, final Class expectedClass) throws NonconvertibleObjectException {

        final String mime = reference.getMimeType();
        final String encoding = reference.getEncoding();
        final String schema = reference.getSchema();
        final String method = reference.getMethod() != null ? reference.getMethod() : "GET";

        InputStream stream = null;

        if (method.equalsIgnoreCase("GET")) {

            String href = reference.getHref();

            try {
                href = URLDecoder.decode(reference.getHref(), "UTF-8");
                final URL url = new URL(href);
                stream = url.openStream();

            } catch (UnsupportedEncodingException ex) {
                throw new NonconvertibleObjectException("Invalid reference href.", ex);
            } catch (IOException ex) {
                throw new NonconvertibleObjectException("Can't reach the reference data.", ex);
            }

        } else if (method.equalsIgnoreCase("POST")) {

            stream = postReferenceRequest(reference);
        }

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AbstractInputConverter.IN_STREAM, stream);
        parameters.put(AbstractInputConverter.IN_MIME, mime);
        parameters.put(AbstractInputConverter.IN_SCHEMA, schema);
        parameters.put(AbstractInputConverter.IN_ENCODING, encoding);

        final ObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE, mime, encoding, schema);

        if (converter == null) {
            throw new NonconvertibleObjectException("Input reference not supported, no converter found.");
        }

        return converter.convert(parameters);
    }

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
    
    public static String dataFormatToString(final String mime, final String encoding, final String schema) {
         final StringBuilder builder = new StringBuilder();
        final String begin = "[";
        final String end = "]";
        final String separator = ", ";

        builder.append(begin);
        
        builder.append("mimeType=");
        builder.append(mime);
        builder.append(separator);
        
        builder.append("encoding=");
        builder.append(encoding);
        builder.append(separator);
        
        builder.append("schema=");
        builder.append(schema);
       
        builder.append(end);
        return builder.toString();
    }
}
