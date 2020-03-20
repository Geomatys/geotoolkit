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

import com.fasterxml.jackson.core.JsonEncoding;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Reference;
import org.locationtech.jts.geom.Geometry;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a {@link Geometry geometry} into a {@link OutputReferenceType reference}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
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
     * Helper method that returns a json extension if the provided mime-type is APP_GEOJSON.
     *
     * @param mimeType string containing a mime-type
     * @return either an empty String or a String equals to ".json"
     */
    private static String getFileExtension(String mimeType) {
        if (WPSMimeType.APP_GEOJSON.val().equals(mimeType)) {
            return ".json";
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reference convert(final Geometry source, final Map<String,Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if ( !(source instanceof Geometry)) {
            throw new UnconvertibleObjectException("The geometry is not an JTS geometry.");
        }

        Reference reference = new Reference();

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        String gmlVersion = (String) params.get(GMLVERSION);
        if (gmlVersion == null) {
            gmlVersion = "3.1.1";
        }

        final String randomPathName = UUID.randomUUID().toString() + getFileExtension(reference.getMimeType());
        final Path geometryFile = buildPath(params, randomPathName);
        final String tmpDirUrl = (String) params.get(TMP_DIR_URL);

        OutputStream geometryStream = null;
        try {
            geometryStream = Files.newOutputStream(geometryFile);
            if (WPSMimeType.APP_GML.val().equalsIgnoreCase(reference.getMimeType())||
                WPSMimeType.TEXT_XML.val().equalsIgnoreCase(reference.getMimeType()) ||
                WPSMimeType.TEXT_GML.val().equalsIgnoreCase(reference.getMimeType()) ||
                reference.getMimeType() == null) { // default to XML

                final Marshaller m = WPSMarshallerPool.getInstance().acquireMarshaller();
                m.marshal(JTStoGeometry.toGML(gmlVersion, source), geometryStream);

                final String relLoc = getRelativeLocation(geometryFile, params);
                reference.setHref(tmpDirUrl + '/' + relLoc.replace('\\', '/'));
                WPSMarshallerPool.getInstance().recycle(m);

            } else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
                GeoJSONStreamWriter.writeSingleGeometry(geometryStream, source, JsonEncoding.UTF8, WPSConvertersUtils.FRACTION_DIGITS, true);
                final String relLoc = getRelativeLocation(geometryFile, params);
                reference.setHref(tmpDirUrl + '/' + relLoc.replace('\\', '/'));
            } else {
                throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
            }

        } catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Can't convert the JTS geometry to OpenGIS.", ex);
        } catch (FileNotFoundException ex) {
            throw new UnconvertibleObjectException("Can't create output reference file.", ex);
        } catch (JAXBException ex) {
             throw new UnconvertibleObjectException("JAXB exception while writing the geometry", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex);
        } finally {
            try {
                if (geometryStream != null)
                    geometryStream.close();
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't close the output reference file stream.", ex);
            }
        }
        return reference;
    }

}
