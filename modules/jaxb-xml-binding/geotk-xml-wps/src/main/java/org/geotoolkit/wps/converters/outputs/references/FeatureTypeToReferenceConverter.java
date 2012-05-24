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
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.feature.type.FeatureType;

/**
 * Implementation of ObjectConverter to convert a {@link FeatureType feature type} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class FeatureTypeToReferenceConverter extends AbstractReferenceOutputConverter<FeatureType> {

    private static FeatureTypeToReferenceConverter INSTANCE;

    private FeatureTypeToReferenceConverter(){
    }

    public static synchronized FeatureTypeToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureTypeToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super FeatureType> getSourceClass() {
        return FeatureType.class; 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final FeatureType source, final Map<String,Object> params) throws NonconvertibleObjectException {
        
        if (params.get(TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        if (source == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        
        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        ReferenceType reference = null ;
        
        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));
        
        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        
        final String randomFileName = UUID.randomUUID().toString();
        
        //Write FeatureType
        try {
            
            final String schemaFileName = randomFileName + "_schema" + ".xsd";
            
            //create file
            final File schemaFile = new File((String) params.get(TMP_DIR_PATH), schemaFileName);
            final OutputStream schemaStream = new FileOutputStream(schemaFile);
            
            //write featureType xsd on file
            final XmlFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
            xmlFTWriter.write(source, schemaStream);
            
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +schemaFileName);
            
        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
        } catch (FileNotFoundException ex) {
            throw new NonconvertibleObjectException("Can't create xsd schema file.",ex);
        }
             
        return reference;
    }

}
