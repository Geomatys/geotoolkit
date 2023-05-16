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


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.xml.jaxp.ElementFeatureWriter;
import org.geotoolkit.storage.geojson.GeoJSONStreamWriter;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;


/**
 * Implementation of ObjectConverter to convert a Feature into a {@link Data}.
 *
 * @author Quentin Boileau (Geoamtys).
 * @author Theo Zozime
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
    public Class<Feature> getSourceClass() {
        return Feature.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data convert(Feature source, Map<String, Object> params) throws UnconvertibleObjectException {

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        } else if (params == null) {
            throw new UnconvertibleObjectException("Not enough information about data format");
        }

        final Object tmpMime = params.get(MIME);
        final String mime;
        if (tmpMime instanceof String) {
            mime = (String) tmpMime;
        } else {
            throw new UnconvertibleObjectException("No valid mime type given. We cannot determine output image format");
        }


        final Data complex = new Data();
        complex.setMimeType(mime);

        final Object tmpEncoding = params.get(ENCODING);
        if (tmpEncoding instanceof String) {
            complex.setEncoding((String) tmpEncoding);
        }

        final FeatureType ft = source.getType();
        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map<String, String> schemaLocation = new HashMap<>();

        if(WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(complex.getMimeType())) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                try (GeoJSONStreamWriter writer = new GeoJSONStreamWriter(baos, ft, WPSConvertersUtils.FRACTION_DIGITS)) {
                    Feature next = writer.next();
                    FeatureExt.copy(source, next, true);
                    writer.write();
                }
                WPSConvertersUtils.addCDATAToComplex(baos.toString("UTF-8"), complex);
                complex.setSchema(null);
            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (UnsupportedEncodingException e) {
                throw new UnconvertibleObjectException("Can't convert output stream into String.", e);
            }

        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(complex.getMimeType())||
                   WPSMimeType.TEXT_XML.val().equalsIgnoreCase(complex.getMimeType()) ||
                   WPSMimeType.TEXT_GML.val().equalsIgnoreCase(complex.getMimeType())) {

            try {
                complex.setSchema(WPSConvertersUtils.writeSchema(ft, params));
                schemaLocation.put(namespace, complex.getSchema());
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.", ex);
            }

            try {
                final ElementFeatureWriter efw = new ElementFeatureWriter(schemaLocation);
                complex.getContent().add(efw.writeFeature(source, null, true));
            } catch (ParserConfigurationException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureCollection into ResponseDocument.", ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + complex.getMimeType());

       return  complex;

    }
}

