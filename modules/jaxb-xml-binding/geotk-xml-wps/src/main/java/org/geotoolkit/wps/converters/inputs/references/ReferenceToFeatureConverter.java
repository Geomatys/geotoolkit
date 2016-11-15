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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.Reference;
import org.opengis.feature.Feature;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a reference into a Feature.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ReferenceToFeatureConverter extends AbstractReferenceInputConverter<Feature> {

    private static ReferenceToFeatureConverter INSTANCE;

    private ReferenceToFeatureConverter() {
    }

    public static synchronized ReferenceToFeatureConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToFeatureConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Feature> getTargetClass() {
        return Feature.class;
    }

    @Override
    public Feature convert(Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        //XML
        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            XmlFeatureReader fcollReader = null;
            try {
                fcollReader = getFeatureReader(source);
                final FeatureCollection fcoll = (FeatureCollection) fcollReader.read(stream);
                if (fcoll.size() == 1) {
                    Feature feat = null;
                    final FeatureIterator ite = fcoll.iterator();
                    try {
                        if (ite.hasNext()) {
                            feat = (Feature) WPSConvertersUtils.fixFeature(ite.next());
                        }
                    } finally {
                        ite.close();
                    }
                    return feat;
                } else {
                    throw new UnconvertibleObjectException("Stream contain more than one Feature.");
                }

            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("Invalid reference input : can't spread CRS.", ex);
            } catch (IllegalArgumentException ex) {
                throw new UnconvertibleObjectException("Unable to read the feature with the specified schema.", ex);
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Invalid reference input : can't read reference schema.", ex);
            } catch (MalformedURLException ex) {
                throw new UnconvertibleObjectException("Invalid reference input : Malformed schema or resource.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Invalid reference input : IO.", ex);
            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Invalid reference input.", ex);
            } finally {
                if (fcollReader != null) {
                    fcollReader.dispose();
                }
            }
        } else if (mime.equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            try {
                return WPSConvertersUtils.readFeatureFromJson(URI.create(source.getHref()));
            } catch (DataStoreException | URISyntaxException | IOException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
    }
}