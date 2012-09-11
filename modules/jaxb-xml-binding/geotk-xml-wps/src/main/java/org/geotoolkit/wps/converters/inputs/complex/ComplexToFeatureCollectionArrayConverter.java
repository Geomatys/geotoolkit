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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureCollection array.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToFeatureCollectionArrayConverter extends AbstractComplexInputConverter {

    private static ComplexToFeatureCollectionArrayConverter INSTANCE;

    private ComplexToFeatureCollectionArrayConverter() {
    }

    public static synchronized ComplexToFeatureCollectionArrayConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureCollectionArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends Object> getTargetClass() {
        return FeatureCollection[].class;
    }
    
    /**
     * {@inheritDoc}
     * @return FeatureCollection array.
     */
    @Override
    public FeatureCollection[] convert(final ComplexDataType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        final List<Object> data = source.getContent();

        XmlFeatureReader fcollReader = null;
        try {
            fcollReader = getFeatureReader(source);
            if (!data.isEmpty()) {

                final List<FeatureCollection> features = new ArrayList<FeatureCollection>();
                for (int i = 0; i < data.size(); i++) {
                    final FeatureCollection f = (FeatureCollection) fcollReader.read(data.get(i));
                    features.add((FeatureCollection) WPSConvertersUtils.fixFeature(f));
                }
                return features.toArray(new FeatureCollection[features.size()]);
            } else {
                throw new NonconvertibleObjectException("Invalid data input : Empty Feature list.");
            }

        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException("Unable to reach the schema url.", ex);
        } catch (IllegalArgumentException ex) {
            throw new NonconvertibleObjectException("Unable to read the feature with the specified schema.", ex);
        } catch (JAXBException ex) {
            throw new NonconvertibleObjectException("Unable to read the feature schema.", ex);
        } catch (FactoryException ex) {
            throw new NonconvertibleObjectException("Unable to spread the CRS in feature.", ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Unable to read feature from nodes.", ex);
        } catch (XMLStreamException ex) {
            throw new NonconvertibleObjectException("Unable to read feature from nodes.", ex);
        } finally {
            if (fcollReader != null) {
                fcollReader.dispose();
            }
        }
    }
}
