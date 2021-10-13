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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureSetWrapper;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureSet.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToFeatureSetConverter extends AbstractComplexInputConverter<FeatureSet> {

    private static ComplexToFeatureSetConverter INSTANCE;

    private ComplexToFeatureSetConverter() {
    }

    public static synchronized ComplexToFeatureSetConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFeatureSetConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureSet> getTargetClass() {
        return FeatureSet.class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureSet
     */
    @Override
    public FeatureSet convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final List<Object> data = source.getContent();

        if (data.size() > 1) {
            throw new UnconvertibleObjectException("Invalid data input : Only one FeatureSet expected.");
        }

        try (final Stream<FeatureSet> stream = readFeatureArrays(source)) {
                return stream.findFirst().orElse(null);
        }
    }
}
