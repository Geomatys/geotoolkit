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

import java.util.Map;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.feature.Feature;

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
        final FeatureCollection data = ReferenceToFeatureCollectionConverter.getInstance().convert(source, params);
        try (final FeatureIterator it = data.iterator()) {
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
}
