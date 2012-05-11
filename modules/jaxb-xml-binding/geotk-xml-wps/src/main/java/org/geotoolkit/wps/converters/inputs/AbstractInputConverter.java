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
package org.geotoolkit.wps.converters.inputs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;
import org.opengis.feature.type.FeatureType;

/**
 * An abstract class to define the source and target class used by all input converter.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public abstract class AbstractInputConverter extends SimpleConverter<Map<String, Object>, Object> {

    public static final String IN_DATA      = "inData";
    public static final String IN_MIME      = "inMime";
    public static final String IN_SCHEMA    = "inSchema";
    public static final String IN_ENCODING  = "inEncoding";
    public static final String IN_STREAM    = "inStream";

    @Override
    public Class<? super Map<String, Object>> getSourceClass() {
        return Map.class;
    }

    @Override
    public Class<? extends Object> getTargetClass() {
        return Object.class;
    }

    /**
     * Convert the data from source Map into the requested {@code Object}. 
     * The {@code source} Map contain : 
     * <ul>
     *      <li>inData : data object from complex input.</li>
     *      <li>inHref : the url to the data in reference case.</li>
     *      <li>inMime : mime type of the data like text/xml, ...</li>
     *      <li>inEncoding : is the data requires a schema</li>
     *      <li>encoding : the data encoding like UTF8, ...</li>
     * </ul>
     * @param source
     * @return the converted inData into the specialized converter output object.
     * @throws NonconvertibleObjectException if an error occurs durring the convertion processing.
     */
    @Override
    public abstract Object convert(Map<String, Object> source) throws NonconvertibleObjectException;
    
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
    protected XmlFeatureReader getFeatureReader(final Map<String, Object> source) throws MalformedURLException, JAXBException, IOException {
        
        JAXPStreamFeatureReader featureReader = new JAXPStreamFeatureReader();
        try {
            final XmlFeatureTypeReader xsdReader = new JAXBFeatureTypeReader();
            final String schema = (String) source.get(IN_SCHEMA);

            if (schema != null) {
                final URL schemaURL = new URL(schema);
                final List<FeatureType> featureTypes = xsdReader.read(schemaURL.openStream());
                if (featureTypes != null) {
                    featureReader = new JAXPStreamFeatureReader(featureTypes);
                }
            } else {
                featureReader.setReadEmbeddedFeatureType(true);
            }
        } catch(JAXBException ex) {
            featureReader.setReadEmbeddedFeatureType(true);
        }
        return featureReader;
    }
}
