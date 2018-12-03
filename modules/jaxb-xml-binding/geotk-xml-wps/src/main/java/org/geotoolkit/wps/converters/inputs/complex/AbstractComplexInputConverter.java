/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.complex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.converters.inputs.references.ReferenceToFeatureCollectionConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public abstract class AbstractComplexInputConverter<T> extends WPSDefaultConverter<Data, T> {

    @Override
    public Class<Data> getSourceClass() {
        return Data.class;
    }

    @Override
    public abstract Class<T> getTargetClass();

    /**
     * Convert a {@link Data complex} into the requested {@code Object}.
     * @param source ReferenceType
     * @return Object
     * @throws UnconvertibleObjectException
     */
    @Override
    public abstract T convert(final Data source, Map<String, Object> params) throws UnconvertibleObjectException;

    /**
     *
     * @param source The complex value to read as a sequence of distinct feature
     * collections.
     * @return A stream of parsed data. Never null, but can be empty. WARNING:
     * You have to properly close the stream after usage.
     */
    static Stream<FeatureCollection> readFeatureArrays(final Data source) {
        ArgumentChecks.ensureNonNull("Source complex data", source);

        if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(source.getMimeType())) {
            return fromGeoJSON(source);
        } else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(source.getMimeType())
                || WPSMimeType.TEXT_XML.val().equalsIgnoreCase(source.getMimeType())
                || WPSMimeType.TEXT_GML.val().equalsIgnoreCase(source.getMimeType())) {
            return fromXml(source);
        }

        throw new UnconvertibleObjectException("Unsupported mime-type: " + source.getMimeType());
    }

    /**
     * TODO: change content for a more stable and performant algorithm.
     * Especially, we create temporary files which are never deleted (because
     * the resulting feature collection read in them), and it is very bad. We
     * have to use a geojson streaming reader.
     *
     * @param source
     * @return
     */
    private static Stream<FeatureCollection> fromGeoJSON(final Data source) {
        return source.getContent().stream()
                .map(WPSConvertersUtils::geojsonContentAsString)
                .map(text -> {
                    try {
                        final Path tmpFile = WPSConvertersUtils.writeTempJsonFile(text);
                        return tmpFile;
                    } catch (IOException ex) {
                        throw new UnconvertibleObjectException("Unable to read complex.", ex);
                    }
                })
                .map(file -> {
                    try {
                        return WPSConvertersUtils.readFeatureCollectionFromJson(file.toUri());
                    } catch (MalformedURLException | DataStoreException ex) {
                        throw new UnconvertibleObjectException(ex);
                    } catch (URISyntaxException | IOException ex) {
                        throw new UnconvertibleObjectException(ex);
                    }
                });
    }

    private static Stream<FeatureCollection> fromXml(final Data source) {
        final XmlFeatureReader fcollReader;
        try {
            fcollReader = WPSIO.getFeatureReader(source.getSchema());
        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("Unable to reach the schema url.", ex);
        }

        final Stream<FeatureCollection> result = source.getContent().stream()
                .map(in -> {
                    try {
                        return fcollReader.read(in);
                    } catch (XMLStreamException | IOException ex) {
                        throw new UnconvertibleObjectException("Unable to read feature from nodes.", ex);
                    }
                })
                .map(ReferenceToFeatureCollectionConverter::castOrWrap);
        result.onClose(() -> fcollReader.dispose());

        return result;
    }
}
