/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

package org.geotoolkit.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.metadata.Metadata;

/**
 * Decorate a FeatureSet to a FeatureCollection.
 *
 * @author Johann Sorel
 */
public class DefaultFeatureCollection extends AbstractFeatureCollection {

    private final FeatureSet set;

    public DefaultFeatureCollection(FeatureSet set, String id, Source source) {
        super(id, source);
        this.set = set;
    }

    @Override
    public Stream<Feature> stream() {
        try {
            return features(false);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public FeatureType getType() {
        try {
            return set.getType();
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public FeatureIterator iterator(Hints hints) throws FeatureStoreRuntimeException {
        try {
            final Stream<Feature> features = features(false);
            final Iterator<Feature> ite = features.iterator();
            return new FeatureIterator() {
                @Override
                public Feature next() throws FeatureStoreRuntimeException {
                    return ite.next();
                }

                @Override
                public boolean hasNext() throws FeatureStoreRuntimeException {
                    return ite.hasNext();
                }

                @Override
                public void close() {
                    features.close();
                }
            };
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        return set.features(parallal);
    }

    @Override
    public void update(Filter filter, final Map<String, ?> values) throws DataStoreException {
        set.replaceIf(filter::evaluate, new UnaryOperator<Feature>() {
            @Override
            public Feature apply(Feature feature) {
                for (Entry<String,?> entry : values.entrySet()) {
                    feature.setPropertyValue(entry.getKey(), entry.getValue());
                }
                return feature;
            }
        });
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        set.removeIf(filter::evaluate);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return super.getMetadata();
    }

}
