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
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureCollection.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToFeatureCollectionConverter extends AbstractComplexInputConverter<FeatureCollection> {

    private static ComplexToFeatureCollectionConverter INSTANCE;

    private ComplexToFeatureCollectionConverter() {
    }

    public static synchronized ComplexToFeatureCollectionConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureCollectionConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureCollection> getTargetClass() {
        return FeatureCollection.class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureCollection
     */
    @Override
    public FeatureCollection convert(final ComplexDataType source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final List<Object> data = source.getContent();

        if (data.size() > 1) {
            throw new UnconvertibleObjectException("Invalid data input : Only one FeatureCollection expected.");
        }

        if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(source.getMimeType())) {

            final String content = WPSConvertersUtils.extractGeoJSONContentAsStringFromComplex(source);

            // We create a tmp file where we write the content of the complex.
            // It will be read later by a FeatureStoreFinder
            Path tmpFilePath = null;
            try {
                tmpFilePath = WPSConvertersUtils.writeTempJsonFile(content);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Unable to read complex.", ex);
            }

            try {
                return WPSConvertersUtils.readFeatureCollectionFromJson(tmpFilePath.toUri());
            } catch (MalformedURLException | DataStoreException ex) {
                throw new UnconvertibleObjectException(ex);
            } catch (URISyntaxException | IOException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else if (WPSMimeType.APP_GML.val().equalsIgnoreCase(source.getMimeType())||
                WPSMimeType.TEXT_XML.val().equalsIgnoreCase(source.getMimeType()) ||
                WPSMimeType.TEXT_GML.val().equalsIgnoreCase(source.getMimeType())) {
            //Read featureCollection
            XmlFeatureReader fcollReader = null;
            try {

                fcollReader = getFeatureReader(source);
                FeatureCollection extractData = (FeatureCollection) fcollReader.read(data.get(0));
                return (FeatureCollection) WPSConvertersUtils.fixFeature( extractData);

            } catch (MalformedURLException ex) {
                throw new UnconvertibleObjectException("Unable to reach the schema url.", ex);
            } catch (IllegalArgumentException ex) {
                throw new UnconvertibleObjectException("Unable to read the feature with the specified schema.", ex);
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Unable to read the feature schema.", ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("Unable to spread the CRS in feature.", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Unable to read feature from nodes.", ex);
            } catch (XMLStreamException ex) {
                throw new UnconvertibleObjectException("Unable to read feature from nodes.", ex);
            } finally {
                if (fcollReader != null) {
                    fcollReader.dispose();
                }
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
    }
}