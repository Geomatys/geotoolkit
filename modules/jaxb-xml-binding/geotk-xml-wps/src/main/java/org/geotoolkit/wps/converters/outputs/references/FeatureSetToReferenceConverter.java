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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.feature.FeatureType;

/**
 * Implementation of ObjectConverter to convert a FeatureSet into a {@link Reference}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class FeatureSetToReferenceConverter extends AbstractReferenceOutputConverter<FeatureSet> {

    private static FeatureSetToReferenceConverter INSTANCE;

    private FeatureSetToReferenceConverter() {
    }

    public static synchronized FeatureSetToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureSetToReferenceConverter();
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
    public Reference convert(final FeatureSet source, final Map<String, Object> params) throws UnconvertibleObjectException {

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
        if (!(source instanceof FeatureSet)) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of FeatureSet.");
        }

        Reference reference = new Reference();

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));

        final FeatureType ft;
        try {
            ft = source.getType();
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException("Can't write acess FeatureSet type.", ex);
        }
        final String namespace = NamesExt.getNamespace(ft.getName());
        final Map<String, String> schemaLocation = new HashMap<>();

        final String randomFileName = UUID.randomUUID().toString();

        if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
            //create file
            final Path dataFile = buildPath(params, randomFileName + ".json");

            try (OutputStream fos = Files.newOutputStream(dataFile, CREATE,  TRUNCATE_EXISTING, WRITE);
                 GeoJSONStreamWriter writer = new GeoJSONStreamWriter(fos, ft, WPSConvertersUtils.FRACTION_DIGITS)){
                 FeatureStoreUtilities.write(writer, (Collection) source);
            } catch (DataStoreException e) {
                throw new UnconvertibleObjectException("Can't write Feature into GeoJSON output stream.", e);
            } catch (IOException e) {
                throw new UnconvertibleObjectException(e);
            }

            final String relLoc = getRelativeLocation(dataFile, params);
            reference.setHref(params.get(TMP_DIR_URL) + "/" + relLoc);
            reference.setSchema(null);

        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(reference.getMimeType())||
                   WPSMimeType.TEXT_XML.val().equalsIgnoreCase(reference.getMimeType()) ||
                   WPSMimeType.TEXT_GML.val().equalsIgnoreCase(reference.getMimeType())) {
            try {
                reference.setSchema(WPSConvertersUtils.writeSchema(ft, params));
                schemaLocation.put(namespace, reference.getSchema());

            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Can't create xsd schema file.", ex);
            }

            //Write Feature
            final JAXPStreamFeatureWriter featureWriter = new JAXPStreamFeatureWriter(schemaLocation);

            //create file
            final Path dataFile = buildPath(params, randomFileName+".xml");
            try (final OutputStream dataStream = Files.newOutputStream(dataFile);
                 final AutoCloseable xmlCloser = () -> featureWriter.dispose()) {

                //Write feature in file
                featureWriter.write(source, dataStream);
                final String relLoc = getRelativeLocation(dataFile, params);
                reference.setHref(params.get(TMP_DIR_URL) + "/" + relLoc);

            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Stax exception while writing the feature collection", ex);
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException("FeatureStore exception while writing the feature collection", ex);
            } catch (FeatureStoreRuntimeException ex) {
                throw new UnconvertibleObjectException("FeatureStoreRuntimeException exception while writing the feature collection", ex);
            } catch (Exception ex) {
                throw new UnconvertibleObjectException(ex);
            }
        } else {
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
        }
        return reference;

    }

}
