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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.internal.storage.MemoryFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.feature.Feature;
;

/**
 * Implementation of ObjectConverter to convert a reference into a FeatureSet.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ReferenceToFeatureSetConverter extends AbstractReferenceInputConverter<FeatureSet> {

    private static ReferenceToFeatureSetConverter INSTANCE;

    private ReferenceToFeatureSetConverter() {
    }

    public static synchronized ReferenceToFeatureSetConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToFeatureSetConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureSet> getTargetClass() {
        return FeatureSet.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return FeatureSet.
     */
    @Override
    public FeatureSet convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();

        //XML
        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val())
                || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            try {
                final XmlFeatureReader fcollReader = WPSIO.getFeatureReader(source.getSchema());

                // TODO: stream data instead of putting it in memory ?
                try (final Closeable readerClosing = () -> fcollReader.dispose();
                        final InputStream in = getInputStreamFromReference(source)) {

                    return castOrWrap(fcollReader.read(in));
                }

            } catch (UnconvertibleObjectException ex) {
                throw ex;
            } catch (IllegalArgumentException ex) {
                throw new UnconvertibleObjectException("Unable to read the feature with the specified schema.", ex);
            } catch (MalformedURLException ex) {
                throw new UnconvertibleObjectException("Invalid reference input: Malformed schema or resource.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Invalid reference input: access problem.", ex);
            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Invalid reference input: unknown error", ex);
            }
        } else if (mime.equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            try {
                return WPSConvertersUtils.readFeatureCollectionFromJson(URI.create(source.getHref()));

            } catch (DataStoreException | URISyntaxException | IOException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        } else {
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
        }
    }

    public static FeatureSet castOrWrap(final Object in) {
        if (in instanceof FeatureSet) {
            return (FeatureSet) in;
        } else if (in instanceof Feature f) {
            return new MemoryFeatureSet(null, f.getType(), Arrays.asList(f));
        } else
            throw new UnconvertibleObjectException("Read data is of unexpected type: " + (in == null ? "null" : in.getClass()));
    }
}
