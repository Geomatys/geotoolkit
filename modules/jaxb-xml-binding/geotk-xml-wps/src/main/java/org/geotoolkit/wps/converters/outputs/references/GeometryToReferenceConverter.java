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
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a {@link Geometry geometry} into a {@link OutputReferenceType reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class GeometryToReferenceConverter extends AbstractReferenceOutputConverter<Geometry> {

    private static GeometryToReferenceConverter INSTANCE;

    private GeometryToReferenceConverter(){
    }

    public static synchronized GeometryToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new GeometryToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry> getSourceClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final Geometry source, final Map<String,Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if ( !(source instanceof Geometry)) {
            throw new UnconvertibleObjectException("The geometry is not an JTS geometry.");
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

        String gmlVersion = (String) params.get(GMLVERSION);
        if (gmlVersion == null) {
            gmlVersion = "3.1.1";
        }

        final String randomFileName = UUID.randomUUID().toString();
        OutputStream geometryStream = null;
        try {
            //create file
            final File geometryFile = new File((String) params.get(TMP_DIR_PATH), randomFileName);
            geometryStream = new FileOutputStream(geometryFile);
            final Marshaller m = WPSMarshallerPool.getInstance().acquireMarshaller();
            m.marshal( JTStoGeometry.toGML(gmlVersion, source), geometryStream);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +randomFileName);
            WPSMarshallerPool.getInstance().recycle(m);

        } catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Can't convert the JTS geometry to OpenGIS.", ex);
        } catch (FileNotFoundException ex) {
            throw new UnconvertibleObjectException("Can't create output reference file.", ex);
        } catch (JAXBException ex) {
             throw new UnconvertibleObjectException("JAXB exception while writing the geometry", ex);
        } finally {
            try {
                geometryStream.close();
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't close the output reference file stream.", ex);
            }
        }
        return reference;
    }

}