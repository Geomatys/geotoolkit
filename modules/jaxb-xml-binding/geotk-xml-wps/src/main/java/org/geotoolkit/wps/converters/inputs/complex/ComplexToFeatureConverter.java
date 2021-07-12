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

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Data;
import org.opengis.feature.Feature;

/**
 * Implementation of ObjectConverter to convert a complex input into a Feature.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToFeatureConverter extends AbstractComplexInputConverter<Feature> {

    private static ComplexToFeatureConverter INSTANCE;

    private ComplexToFeatureConverter() {
    }

    public static synchronized ComplexToFeatureConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Feature> getTargetClass() {
        return Feature.class;
    }

    /**
     * {@inheritDoc}
     * @return Feature
     */
    @Override
    public Feature convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {
        try (final Stream<Feature> stream = AbstractComplexInputConverter.readFeatureArrays(source)
                        .flatMap(ComplexToFeatureConverter::openUnchecked)) {

            final Iterator<Feature> it = stream.iterator();

            final Feature result;
            if (it.hasNext()) {
                result = it.next();
            } else {
                result = null;
            }

            if (it.hasNext()) {
                throw new UnconvertibleObjectException("A single feature was expected from datasource, but we've got at least two.");
            }

            return result;
        }
    }

    private static Stream<Feature> openUnchecked(final FeatureSet dataset) {
        try {
            return dataset.features(false);
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException("Cannot read features from source complex data.", ex);
        }
    }
}
