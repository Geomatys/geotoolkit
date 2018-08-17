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

import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.wps.xml.v200.Data;
import org.opengis.feature.Feature;

/**
 * Implementation of ObjectConverter to convert a complex input into a Feature array.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToFeatureArrayConverter extends AbstractComplexInputConverter<Feature[]> {

    private static ComplexToFeatureArrayConverter INSTANCE;

    private ComplexToFeatureArrayConverter() {
    }

    public static synchronized ComplexToFeatureArrayConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Feature[]> getTargetClass() {
        return Feature[].class;
    }

    /**
     * {@inheritDoc}
     * @return Feature array.
     */
    @Override
    public Feature[] convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {
        try (final Stream<FeatureCollection> stream = AbstractComplexInputConverter.readFeatureArrays(source)) {
            return stream
                    .flatMap(fc -> {
                        try {
                            return fc.features(false);
                        } catch (DataStoreException ex) {
                            throw new UnconvertibleObjectException("Cannot read features from source complex data.", ex);
                        }
                    })
                    .toArray(size -> new Feature[size]);
        }
    }
}
