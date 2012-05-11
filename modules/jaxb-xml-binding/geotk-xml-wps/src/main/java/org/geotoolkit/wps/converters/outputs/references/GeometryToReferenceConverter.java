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

import com.vividsolutions.jts.geom.Geometry;
import java.io.*;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a {@link Geometry geometry} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class GeometryToReferenceConverter extends AbstractReferenceOutputConverter {

    private static GeometryToReferenceConverter INSTANCE;

    private GeometryToReferenceConverter(){
    }

    public static synchronized GeometryToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new GeometryToReferenceConverter();
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
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if ( !(data instanceof Geometry)) {
            throw new NonconvertibleObjectException("The geometry is not an JTS geometry.");
        }
        
        final OutputReferenceType reference = new OutputReferenceType();
        
        reference.setMimeType((String) source.get(OUT_MIME));
        reference.setEncoding((String) source.get(OUT_ENCODING));
        reference.setSchema((String) source.get(OUT_SCHEMA));
        
        final String randomFileName = UUID.randomUUID().toString();
        Marshaller m = null;
        OutputStream geometryStream = null;
        try {
            //create file
            final File geometryFile = new File((String) source.get(OUT_TMP_DIR_PATH), randomFileName);
            geometryStream = new FileOutputStream(geometryFile);
            m = WPSMarshallerPool.getInstance().acquireMarshaller();
            m.marshal( JTStoGeometry.toGML((Geometry) data), geometryStream);
            reference.setSchema((String) source.get(OUT_TMP_DIR_URL) + "/" +randomFileName);
            
        } catch (FactoryException ex) {
            throw new NonconvertibleObjectException("Can't convert the JTS geometry to OpenGIS.", ex);
        } catch (FileNotFoundException ex) {
            throw new NonconvertibleObjectException("Can't create output reference file.", ex);
        } catch (JAXBException ex) {
             throw new NonconvertibleObjectException("JAXB exception while writing the geometry", ex);
        } finally {
            if(m!=null){
                WPSMarshallerPool.getInstance().release(m);
            }
            try {
                geometryStream.close();
            } catch (IOException ex) {
                throw new NonconvertibleObjectException("Can't close the output reference file stream.", ex);
            }
        }
        return reference;
    }
    
}