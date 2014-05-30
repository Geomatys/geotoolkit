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
package org.geotoolkit.wps.converters.inputs.complex;

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
import static org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.feature.type.FeatureType;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public abstract class AbstractComplexInputConverter<T> extends WPSDefaultConverter<ComplexDataType, T> {

    @Override
    public Class<? super ComplexDataType> getSourceClass() {
        return ComplexDataType.class;
    }

    @Override
    public abstract Class<? extends T> getTargetClass();

    /**
     * Convert a {@link ComplexDataType complex} into the requested {@code Object}. 
     * @param source ReferenceType 
     * @return Object
     * @throws NonconvertibleObjectException 
     */
    @Override
    public abstract T convert(final ComplexDataType source, Map<String, Object> params) throws NonconvertibleObjectException;
    
    /**
     * Get the JAXPStreamFeatureReader to read feature. If there is a schema defined, the JAXPStreamFeatureReader will
     * use it otherwise it will use the embedded.
     *
     * @param source
     * @return
     * @throws MalformedURLException
     * @throws JAXBException
     * @throws IOException
     */
    protected XmlFeatureReader getFeatureReader(final ComplexDataType source) throws MalformedURLException, JAXBException, IOException {
        
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
                featureReader.getProperties().put(READ_EMBEDDED_FEATURE_TYPE, true);
            }
        } catch(JAXBException ex) {
            featureReader.getProperties().put(READ_EMBEDDED_FEATURE_TYPE, true);
        }
        return featureReader;
    }
}
