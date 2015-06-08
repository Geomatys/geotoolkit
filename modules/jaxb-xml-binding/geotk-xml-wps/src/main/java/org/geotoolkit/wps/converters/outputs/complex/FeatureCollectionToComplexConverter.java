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
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.ElementFeatureWriter;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.wps.converters.WPSConvertersUtils;

/**
 * Implementation of ObjectConverter to convert a FeatureCollection into a {@link ComplexDataType}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class FeatureCollectionToComplexConverter extends AbstractComplexOutputConverter<FeatureCollection> {

    private static FeatureCollectionToComplexConverter INSTANCE;

    private FeatureCollectionToComplexConverter() {
    }

    public static synchronized FeatureCollectionToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureCollectionToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureCollection> getSourceClass() {
        return FeatureCollection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexDataType convert(final FeatureCollection source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof FeatureCollection)) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of FeatureCollection.");
        }

        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding((String) params.get(ENCODING));
        complex.setMimeType((String) params.get(MIME));

        final FeatureType ft = source.getFeatureType();
        final String namespace = DefaultName.getNamespace(ft.getName());
        final Map<String, String> schemaLocation = new HashMap<String, String>();

        if(WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(complex.getMimeType())) {

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GeoJSONStreamWriter writer = new GeoJSONStreamWriter(baos, ft, WPSConvertersUtils.FRACTION_DIGITS);
                FeatureStoreUtilities.write(writer, source);
                WPSConvertersUtils.addCDATAToComplex(baos.toString("UTF-8"), complex);
                complex.setSchema(null);
            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (UnsupportedEncodingException e) {
                throw new UnconvertibleObjectException("Can't convert output stream into String.", e);
            }
        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(complex.getMimeType())) {

            if (params.get(TMP_DIR_PATH) == null) {
                throw new UnconvertibleObjectException("The output directory should be defined.");
            }

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
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.", ex);
            } catch (FileNotFoundException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.", ex);
            }

            try {

                final ElementFeatureWriter efw = new ElementFeatureWriter(schemaLocation);
                complex.getContent().add(efw.writeFeatureCollection(source, true, false, null));

            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureCollection into ResponseDocument.", ex);
            } catch (ParserConfigurationException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureCollection into ResponseDocument.", ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + complex.getMimeType());

        return complex;

    }

}
