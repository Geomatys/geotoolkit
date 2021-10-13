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
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureSet array.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToFeatureSetArrayConverter extends AbstractComplexInputConverter<FeatureSet[]> {

    private static ComplexToFeatureSetArrayConverter INSTANCE;

    private ComplexToFeatureSetArrayConverter() {
    }

    public static synchronized ComplexToFeatureSetArrayConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureSetArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureSet[]> getTargetClass() {
        return FeatureSet[].class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureSet array.
     */
    @Override
    public FeatureSet[] convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {
        try (final Stream<FeatureSet> stream = AbstractComplexInputConverter.readFeatureArrays(source)) {
            return stream
                    .toArray(size -> new FeatureSet[size]);
        }
    }
}
