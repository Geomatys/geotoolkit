/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.geojson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.GeoJSONUtils;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeoJSONStreamReader implements Iterator<Feature>, AutoCloseable {

    private final FeatureType type;
    private final GeoJSONReader reader;
    private Feature first;

    public GeoJSONStreamReader(InputStream stream, FeatureType parentType, String typeName, CoordinateReferenceSystem crs) throws IOException, DataStoreException {
        final GeoJSONObject result = GeoJSONParser.parse(stream);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        if (parentType != null) {
            ftb.setSuperTypes(parentType);
        }
        ftb.setName(typeName);

        if (crs == null) {
            crs = GeoJSONUtils.getCRS(result);
        }

        GeoJSONFeature jsonFeature = null;
        if (result instanceof GeoJSONFeatureCollection) {
            GeoJSONFeatureCollection jsonFeatureCollection = (GeoJSONFeatureCollection) result;
            if (!jsonFeatureCollection.hasNext()) {
                //empty FeatureCollection error ?
                throw new DataStoreException("Empty GeoJSON FeatureCollection");
            } else {

                // TODO should we analyse all Features from FeatureCollection to be sure
                // that each Feature properties JSON object define exactly the same properties
                // with the same bindings ?
                jsonFeature = jsonFeatureCollection.next();
                GeoJSONStore.fillTypeFromFeature(ftb, crs, jsonFeature, false);
            }

        } else if (result instanceof GeoJSONFeature) {
            jsonFeature = (GeoJSONFeature) result;
            GeoJSONStore.fillTypeFromFeature(ftb, crs, jsonFeature, true);
        }
        type = ftb.build();

        this.reader = new GeoJSONReader(result, type, new ReentrantReadWriteLock());
        first = reader.toFeature(jsonFeature);
    }

    public FeatureType getType() {
        return type;
    }

    @Override
    public boolean hasNext() {
        if (first != null) return true;
        return reader.hasNext();
    }

    @Override
    public Feature next() {
        if (first != null) {
            Feature f = first;
            first = null;
            return f;
        }
        return reader.next();
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }

}
