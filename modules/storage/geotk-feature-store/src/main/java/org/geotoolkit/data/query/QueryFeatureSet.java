/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.data.query;

import java.util.stream.Stream;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.feature.ViewMapper;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 * Java in memory implementation of a queried subset of a FeatureSet.
 *
 * This implementation is not efficient, all filtering and transformation
 * operations are computed in memory.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class QueryFeatureSet implements FeatureSet {

    private final FeatureSet base;
    private final Query query;

    private QueryFeatureSet(FeatureSet base, Query query) {
        this.base = base;
        this.query = query;
    }

    @Override
    public GenericName getIdentifier() {
        return base.getIdentifier();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        //create a new metadata preserving only the identification informations
        final Metadata metadata = base.getMetadata();
        final DefaultMetadata meta = new DefaultMetadata();
        meta.setIdentificationInfo(metadata.getIdentificationInfo());
        return meta;
    }

    /**
     * Current implementation do not calculate envelope but in future versions
     * and if the query permits it the envelope could be return without significant
     * cost.
     *
     * @return always null
     * @throws DataStoreException
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return null;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        FeatureType ft = base.getType();
        final String[] properties = query.getPropertyNames();
        if (properties!=null && FeatureTypeExt.isAllProperties(ft, properties)) {
            ft = new ViewMapper(ft, properties).getMappedType();
        }
        if (query.getCoordinateSystemReproject() != null) {
            ft = new ReprojectMapper(ft, query.getCoordinateSystemReproject()).getMappedType();
        }

        return ft;
    }

    @Override
    public FeatureSet subset(org.apache.sis.storage.Query query) throws UnsupportedQueryException, DataStoreException {
        if (query instanceof Query) {
            final Query newQuery = QueryUtilities.subQuery(this.query, (Query)query);
            return QueryFeatureSet.apply(base, newQuery);
        }
        return FeatureSet.super.subset(query);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final Stream<Feature> stream = base.features(parallel);
        return FeatureStreams.subset(stream, base.getType(), query);
    }

    /**
     * Filter and transform a FeatureSet using a Query.
     *
     * If the query has no effect, the original FeatureSet will be returned.
     *
     * @param set original FeatureSet, must not be null
     * @param query query to apply, must not be null
     * @return transformed feature set
     */
    public static FeatureSet apply(FeatureSet set, Query query) {
        if (QueryUtilities.queryAll(query)) {
            //query does nothing
            return set;
        }
        return new QueryFeatureSet(set, query);
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

}
