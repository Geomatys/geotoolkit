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
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureCollection array.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToFeatureCollectionArrayConverter extends AbstractComplexInputConverter<FeatureCollection[]> {

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
    public Class<FeatureCollection[]> getTargetClass() {
        return FeatureCollection[].class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureCollection array.
     */
    @Override
    public FeatureCollection[] convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {
        try (final Stream<FeatureSet> stream = AbstractComplexInputConverter.readFeatureArrays(source)) {
            return stream
                    .toArray(size -> new FeatureCollection[size]);
        }
    }
}
