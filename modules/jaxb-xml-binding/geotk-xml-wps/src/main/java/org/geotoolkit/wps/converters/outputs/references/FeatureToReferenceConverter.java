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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Implementation of ObjectConverter to convert a {@link Feature feature} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class FeatureToReferenceConverter extends AbstractReferenceOutputConverter<Feature> {

    private static FeatureToReferenceConverter INSTANCE;

    private FeatureToReferenceConverter(){
    }

    public static synchronized FeatureToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super Feature> getSourceClass() {
        return Feature.class;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final Feature source, final Map<String,Object> params) throws NonconvertibleObjectException {
        
        
        if (params.get(TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        if (source == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        FeatureType ft = null;
        if (source instanceof Feature) {
            ft = source.getType();
        } else {
            throw new NonconvertibleObjectException("The requested output reference data is not an instance of Feature.");
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
               
        final String namespace = ft.getName().getURI();
        final Map <String, String> schemaLocation = new HashMap<String, String>();
        
        final String randomFileName = UUID.randomUUID().toString();

        if(reference.getMimeType().equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            //create file
            final String dataFileName = randomFileName+".json";
            final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);
            try {
                FileOutputStream fos = new FileOutputStream(dataFile);
                GeoJSONStreamWriter writer = new GeoJSONStreamWriter(fos, ft, "UTF-8", 7);
                Feature next = writer.next();
                FeatureUtilities.copy(source, next, true);
                writer.write();
                writer.close();

            } catch (DataStoreException e) {
                throw new NonconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (FileNotFoundException e) {
                throw new NonconvertibleObjectException(e);
            }

            reference.setHref(params.get(TMP_DIR_URL) + "/" +dataFileName);
            reference.setSchema(null);

        } else {
        //Write FeatureType
        try {
            final String schemaFileName = randomFileName + "_schema" + ".xsd";
            
            //create file
            final File schemaFile = new File((String) params.get(TMP_DIR_PATH), schemaFileName);
            final OutputStream schemaStream = new FileOutputStream(schemaFile);
            
            //write featureType xsd on file
            final XmlFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
            xmlFTWriter.write(ft, schemaStream);
            
            reference.setSchema((String) params.get(TMP_DIR_URL) + "/" +schemaFileName);
            schemaLocation.put(namespace, reference.getSchema());
            
        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
        } catch (FileNotFoundException ex) {
            throw new NonconvertibleObjectException("Can't create xsd schema file.",ex);
        }
             
        //Write Feature
        XmlFeatureWriter featureWriter = null;
        try {

            final String dataFileName = randomFileName+".xml";

            //create file
            final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);
            final OutputStream dataStream = new FileOutputStream(dataFile);
            
            //Write feature in file
            featureWriter = new JAXPStreamFeatureWriter(schemaLocation);
            featureWriter.write(source, dataStream);
            reference.setHref(params.get(TMP_DIR_URL) + "/" +dataFileName);
            
        } catch (IOException ex) {
            throw new NonconvertibleObjectException(ex);
        } catch (XMLStreamException ex) {
            throw new NonconvertibleObjectException("Stax exception while writing the feature collection", ex);
        } catch (DataStoreException ex) {
            throw new NonconvertibleObjectException("FeatureStore exception while writing the feature collection", ex);
        } catch (FeatureStoreRuntimeException ex) {
            throw new NonconvertibleObjectException("FeatureStoreRuntimeException exception while writing the feature collection", ex);
        } finally {
            try {
                if (featureWriter != null) {
                    featureWriter.dispose();
                }
            } catch (IOException ex) {
                 throw new NonconvertibleObjectException(ex);
            } catch (XMLStreamException ex) {
                 throw new NonconvertibleObjectException(ex);
            }
        }
        }
        return reference;
    }
}
