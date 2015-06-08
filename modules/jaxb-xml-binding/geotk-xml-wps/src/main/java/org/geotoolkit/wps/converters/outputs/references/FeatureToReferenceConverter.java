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
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.wps.converters.WPSConvertersUtils;

/**
 * Implementation of ObjectConverter to convert a {@link Feature feature} into a {@link OutputReferenceType reference}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
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
    public Class<Feature> getSourceClass() {
        return Feature.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final Feature source, final Map<String,Object> params) throws UnconvertibleObjectException {


        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        FeatureType ft = null;
        if (source instanceof Feature) {
            ft = source.getType();
        } else {
            throw new UnconvertibleObjectException("The requested output reference data is not an instance of Feature.");
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

        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map <String, String> schemaLocation = new HashMap<String, String>();

        final String randomFileName = UUID.randomUUID().toString();

        if(WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
            //create file
            final String dataFileName = randomFileName+".json";
            final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);
            try {
                FileOutputStream fos = new FileOutputStream(dataFile);
                try (GeoJSONStreamWriter writer = new GeoJSONStreamWriter(fos, ft, WPSConvertersUtils.FRACTION_DIGITS)) {
                    Feature next = writer.next();
                    FeatureUtilities.copy(source, next, true);
                    writer.write();
                }

            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (FileNotFoundException e) {
                throw new UnconvertibleObjectException(e);
            }

            reference.setHref(params.get(TMP_DIR_URL) + "/" +dataFileName);
            reference.setSchema(null);

        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(reference.getMimeType())) {
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
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
            } catch (FileNotFoundException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.",ex);
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
                throw new UnconvertibleObjectException(ex);
            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Stax exception while writing the feature collection", ex);
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException("FeatureStore exception while writing the feature collection", ex);
            } catch (FeatureStoreRuntimeException ex) {
                throw new UnconvertibleObjectException("FeatureStoreRuntimeException exception while writing the feature collection", ex);
            } finally {
                try {
                    if (featureWriter != null) {
                        featureWriter.dispose();
                    }
                } catch (IOException ex) {
                     throw new UnconvertibleObjectException(ex);
                } catch (XMLStreamException ex) {
                     throw new UnconvertibleObjectException(ex);
                }
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
        return reference;
    }
}
