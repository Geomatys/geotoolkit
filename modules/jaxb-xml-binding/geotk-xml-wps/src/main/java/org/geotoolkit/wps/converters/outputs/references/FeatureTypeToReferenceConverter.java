/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.outputs.references;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.opengis.feature.type.FeatureType;

/**
 * Implementation of ObjectConverter to convert a {@link FeatureType feature type} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class FeatureTypeToReferenceConverter extends AbstractReferenceOutputConverter {

    private static FeatureTypeToReferenceConverter INSTANCE;

    private FeatureTypeToReferenceConverter(){
    }

    public static synchronized FeatureTypeToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureTypeToReferenceConverter();
        }
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputReferenceType convert(final Map<String,Object> source) throws NonconvertibleObjectException {
        
        if (source.get(OUT_TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        final Object data = source.get(OUT_DATA);
        
        if (data == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        FeatureType ft = null;
        if (data instanceof FeatureType) {
            ft = (FeatureType) data;
        } else {
            throw new NonconvertibleObjectException("The requested output reference data is not an instance of Feature or FeatureCollection.");
        }
        
        final OutputReferenceType reference = new OutputReferenceType();
        
        reference.setMimeType((String) source.get(OUT_MIME));
        reference.setEncoding((String) source.get(OUT_ENCODING));
        
        final String randomFileName = UUID.randomUUID().toString();
        
        //Write FeatureType
        try {
            
            final String schemaFileName = randomFileName + "_schema" + ".xsd";
            
            //create file
            final File schemaFile = new File((String) source.get(OUT_TMP_DIR_PATH), schemaFileName);
            final OutputStream schemaStream = new FileOutputStream(schemaFile);
            
            //write featureType xsd on file
            final XmlFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
            xmlFTWriter.write(ft, schemaStream);
            
            reference.setHref((String) source.get(OUT_TMP_DIR_URL) + "/" +schemaFileName);
            
        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
        } catch (FileNotFoundException ex) {
            throw new NonconvertibleObjectException("Can't create xsd schema file.",ex);
        }
             
        return reference;
    }
    
}
