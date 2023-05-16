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
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
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
 * Implementation of ObjectConverter to convert a FeatureSet into a {@link Data}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class FeatureSetToComplexConverter extends AbstractComplexOutputConverter<FeatureSet> {

    private static FeatureSetToComplexConverter INSTANCE;

    private FeatureSetToComplexConverter() {
    }

    public static synchronized FeatureSetToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureSetToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureSet> getSourceClass() {
        return FeatureSet.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data convert(final FeatureSet source, final Map<String, Object> params) throws UnconvertibleObjectException {
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

        final FeatureType ft;
        try {
            ft = source.getType();
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException("Can't write acess FeatureSet type.", ex);
        }
        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map<String, String> schemaLocation = new HashMap<>();

        if(WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(mime)) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                try (GeoJSONStreamWriter writer = new GeoJSONStreamWriter(baos, ft, WPSConvertersUtils.FRACTION_DIGITS);
                     Stream<Feature> st = source.features(false)) {

                    Iterator<Feature> iterator = st.iterator();
                    while (iterator.hasNext()) {
                        Feature next = iterator.next();
                        Feature neww = writer.next();
                        FeatureExt.copy(next, neww, false);
                        writer.write();
                    }
                }
                WPSConvertersUtils.addCDATAToComplex(baos.toString("UTF-8"), complex);
                complex.setSchema(null);
            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (UnsupportedEncodingException e) {
                throw new UnconvertibleObjectException("Can't convert output stream into String.", e);
            }
        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(mime)||
                   WPSMimeType.TEXT_XML.val().equalsIgnoreCase(mime) ||
                   WPSMimeType.TEXT_GML.val().equalsIgnoreCase(mime)) {
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
                complex.getContent().add(efw.writeFeatureCollection(source, null, true, false, null, true));

            } catch (DataStoreException | ParserConfigurationException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureSet into ResponseDocument.", ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + complex.getMimeType());

        return complex;

    }
}
