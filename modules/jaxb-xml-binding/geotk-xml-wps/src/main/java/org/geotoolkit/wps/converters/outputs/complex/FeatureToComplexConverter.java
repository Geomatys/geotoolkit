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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.data.geojson.GeoJSONWriter;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.ElementFeatureWriter;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;


/**
 * Implementation of ObjectConverter to convert a Feature into a {@link ComplexDataType}.
 * 
 * @author Quentin Boileau (Geoamtys).
 */
public final class FeatureToComplexConverter extends AbstractComplexOutputConverter<Feature> {

    private static FeatureToComplexConverter INSTANCE;

    private FeatureToComplexConverter(){
    }

    public static synchronized FeatureToComplexConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureToComplexConverter();
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
    public ComplexDataType convert(Feature source, Map<String, Object> params) throws NonconvertibleObjectException {
        
        if (params.get(TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
                
        if (source == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof Feature)) {
            throw new NonconvertibleObjectException("The requested output data is not an instance of Feature.");
        }
        
        final ComplexDataType complex = new ComplexDataType();
        
        complex.setMimeType((String) params.get(MIME));
        complex.setEncoding((String) params.get(ENCODING));
        
        final FeatureType ft = source.getType();
        final String namespace = ft.getName().getURI();
        final Map<String, String> schemaLocation = new HashMap<String, String>();

        if(complex.getMimeType().equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            complex.getContent().add(GeoJSONWriter.toGeoJSON(source));
            complex.setSchema(null);

        } else {

            try {
                final String schemaFileName = "schema_" + UUID.randomUUID().toString() + ".xsd";
                //create file
                final File schemaFile = new File((String) params.get(TMP_DIR_PATH), schemaFileName);
                final OutputStream stream = new FileOutputStream(schemaFile);
                //write featureType xsd on file
                final XmlFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
                xmlFTWriter.write(ft, stream);

                complex.setSchema((String) params.get(TMP_DIR_URL) + "/" + schemaFileName);
                schemaLocation.put(namespace, complex.getSchema());
            } catch (JAXBException ex) {
                throw new NonconvertibleObjectException("Can't write FeatureType into xsd schema.", ex);
            } catch (FileNotFoundException ex) {
                throw new NonconvertibleObjectException("Can't create xsd schema file.", ex);
            }

            try {
                final ElementFeatureWriter efw = new ElementFeatureWriter(schemaLocation);
                complex.getContent().add(efw.writeFeature(source, null, true));
            } catch (ParserConfigurationException ex) {
                throw new NonconvertibleObjectException("Can't write FeatureCollection into ResponseDocument.", ex);
            }
        }

       return  complex;
       
    }
}

