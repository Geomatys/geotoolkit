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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Implementation of ObjectConverter to convert a {@link Feature feature} into a {@link Reference reference}.
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
    public Reference convert(final Feature source, final Map<String,Object> params) throws UnconvertibleObjectException {


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

        Reference reference = new Reference();

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));

        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map <String, String> schemaLocation = new HashMap<>();

        final String randomFileName = UUID.randomUUID().toString();
        final String tmpDirUrl = (String) params.get(TMP_DIR_URL);

        if(WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
            //create file
            final Path dataFile = buildPath(params, randomFileName + ".json");
            try {
                 try (OutputStream fos = Files.newOutputStream(dataFile);
                      GeoJSONStreamWriter writer = new GeoJSONStreamWriter(fos, ft, WPSConvertersUtils.FRACTION_DIGITS)) {
                    Feature next = writer.next();
                    FeatureExt.copy(source, next, true);
                    writer.write();
                }

            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (IOException e) {
                throw new UnconvertibleObjectException(e);
            }

            final String relLoc = getRelativeLocation(dataFile, params);
            reference.setHref(tmpDirUrl + "/" + relLoc);
            reference.setSchema(null);

        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(reference.getMimeType())||
                   WPSMimeType.TEXT_XML.val().equalsIgnoreCase(reference.getMimeType()) ||
                   WPSMimeType.TEXT_GML.val().equalsIgnoreCase(reference.getMimeType())) {
            //Write FeatureType
            try {
                reference.setSchema(WPSConvertersUtils.writeSchema(ft, params));
                schemaLocation.put(namespace, reference.getSchema());

            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.",ex);
            }

            //Write Feature
            final XmlFeatureWriter featureWriter = new JAXPStreamFeatureWriter(schemaLocation);

            //create file
            final Path dataFile = buildPath(params, randomFileName + ".xml");
            try (final OutputStream dataStream = Files.newOutputStream(dataFile);
                 final AutoCloseable xmlCloser = () -> featureWriter.dispose()) {

                //Write feature in file
                featureWriter.write(source, dataStream);
                final String relLoc = getRelativeLocation(dataFile, params);
                reference.setHref(tmpDirUrl + "/" + relLoc);

            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Stax exception while writing the feature collection", ex);
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException("FeatureStore exception while writing the feature collection", ex);
            } catch (FeatureStoreRuntimeException ex) {
                throw new UnconvertibleObjectException("FeatureStoreRuntimeException exception while writing the feature collection", ex);
            } catch (Exception ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
        return reference;
    }
}
