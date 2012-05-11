/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.complex;


import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.feature.type.FeatureType;



/**
 * Implementation of ObjectConverter to convert a FeatureType into a {@link ComplexDataType}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public final class FeatureTypeToComplexConverter extends AbstractComplexOutputConverter {

    private static FeatureTypeToComplexConverter INSTANCE;

    private FeatureTypeToComplexConverter(){
    }

    public static synchronized FeatureTypeToComplexConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureTypeToComplexConverter();
        }
        return INSTANCE;
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexDataType convert(final Map<String, Object> source) throws NonconvertibleObjectException {
        
        
        final Object data = source.get(OUT_DATA);
        
        if (data == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(source.get(OUT_DATA) instanceof FeatureType)) {
            throw new NonconvertibleObjectException("The requested output data is not an instance of FeatureType.");
        }
        final ComplexDataType complex = new ComplexDataType();
        
        complex.setMimeType((String) source.get(OUT_MIME));
        complex.setEncoding((String) source.get(OUT_ENCODING));
        
        final FeatureType ft = (FeatureType) data;
        
        try {
            
            final XmlFeatureTypeWriter xmlWriter = new JAXBFeatureTypeWriter();
            complex.getContent().add(xmlWriter.writeToElement(ft));
            
        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("Can't write FeatureType into ResponseDocument.",ex);
        } catch (ParserConfigurationException ex) {
            throw new NonconvertibleObjectException("Can't write FeatureType into ResponseDocument.",ex);
        }

       return  complex;
       
      
    }
}

