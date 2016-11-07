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
package org.geotoolkit.wps.converters.outputs.references;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;

/**
 * Implementation of ObjectConverter to convert a FeatureCollection into a {@link Reference}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class FeatureCollectionToReferenceConverter extends AbstractReferenceOutputConverter<FeatureCollection> {

    private static FeatureCollectionToReferenceConverter INSTANCE;

    private FeatureCollectionToReferenceConverter() {
    }

    public static synchronized FeatureCollectionToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureCollectionToReferenceConverter();
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
    public Reference convert(final FeatureCollection source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (params.get(TMP_DIR_URL) == null) {
            throw new UnconvertibleObjectException("The output directory URL should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        //TODO : useless test, null test above is all we need, fix this and other converters
        if (!(source instanceof FeatureCollection)) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of FeatureCollection.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);
        
        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));

        final FeatureType ft = source.getFeatureType();
        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map<String, String> schemaLocation = new HashMap<>();

        final String randomFileName = UUID.randomUUID().toString();

        if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
            //create file
            final String dataFileName = randomFileName + ".json";
            final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);

            try {
                FileOutputStream fos = new FileOutputStream(dataFile);
                GeoJSONStreamWriter writer = new GeoJSONStreamWriter(fos, ft, WPSConvertersUtils.FRACTION_DIGITS);
                FeatureStoreUtilities.write(writer, source);
            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (IOException e) {
                throw new UnconvertibleObjectException(e);
            }

            reference.setHref(params.get(TMP_DIR_URL) + "/" + dataFileName);
            reference.setSchema(null);
        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(reference.getMimeType())||
                   WPSMimeType.TEXT_XML.val().equalsIgnoreCase(reference.getMimeType()) ||
                   WPSMimeType.TEXT_GML.val().equalsIgnoreCase(reference.getMimeType())) {
            try {
                final String schemaFileName = randomFileName + "_schema" + ".xsd";
                //create file
                final File schemaFile = new File((String) params.get(TMP_DIR_PATH), schemaFileName);
                final OutputStream stream = new FileOutputStream(schemaFile);
                //write featureType xsd on file
                final XmlFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
                xmlFTWriter.write(ft, stream);

                reference.setSchema((String) params.get(TMP_DIR_URL) + "/" + schemaFileName);
                schemaLocation.put(namespace, reference.getSchema());

            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.", ex);
            }

            JAXPStreamFeatureWriter featureWriter = null;
            try {
                featureWriter = new JAXPStreamFeatureWriter(schemaLocation);

                final String dataFileName = randomFileName + ".xml";

                //create file
                final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);
                final OutputStream dataStream = new FileOutputStream(dataFile);

                //Write feature collection in file
                featureWriter.setOutput(dataStream);
                featureWriter.writeFeatureCollection(source, false, null);

                reference.setHref((String) params.get(TMP_DIR_URL) + "/" + dataFileName);

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
