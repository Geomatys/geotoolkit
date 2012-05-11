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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.inputs.AbstractInputConverter;
import org.geotoolkit.wps.io.WPSMimeType;
import org.opengis.feature.type.FeatureType;


/**
 * Implementation of ObjectConverter to convert a reference into a FeatureType.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToFeatureTypeConverter extends AbstractInputConverter {

    private static ReferenceToFeatureTypeConverter INSTANCE;

    private ReferenceToFeatureTypeConverter(){
    }

    public static synchronized ReferenceToFeatureTypeConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ReferenceToFeatureTypeConverter();
        }
        return INSTANCE;
    }
 
    @Override
    public Class<? extends Object> getTargetClass() {
        return FeatureType.class;
    }
    
    /**
     * {@inheritDoc}
     * @return FeatureType.
     */
    @Override
    public FeatureType convert(final Map<String, Object> source) throws NonconvertibleObjectException {

        final String mime = (String) source.get(IN_MIME) != null ? (String) source.get(IN_MIME) : WPSMimeType.TEXT_XML.val();
        final InputStream stream = (InputStream) source.get(IN_STREAM);
        
        //XML
        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val()) ||
                mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {
             try {
                final XmlFeatureTypeReader xsdReader = new JAXBFeatureTypeReader();
                final List<FeatureType> ft = xsdReader.read(stream);

                if(ft.size() != 1){
                    throw new NonconvertibleObjectException("Invalid reference input : More than one FeatureType in schema.");
                }
                return ft.get(0);
            } catch (JAXBException ex) {
                throw new NonconvertibleObjectException("Invalid reference input : can't read reference schema.",ex);
            }
        }else {
             throw new NonconvertibleObjectException("Reference data mime is not supported");
        }
    }
}