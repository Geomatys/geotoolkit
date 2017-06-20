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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.storage.AbstractResource;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
class DefaultFeatureResource extends AbstractResource implements FeatureResource {

    private final FeatureStore store;
    private final GenericName name;

    public DefaultFeatureResource(FeatureStore store, GenericName name) {
        super(name);
        this.store = store;
        this.name = name;
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return store.getFeatureType(name.toString());
    }

    @Override
    public Stream<Feature> read(Query query) throws DataStoreException {
        if (query==null) {
            query = QueryBuilder.all(name);
        }
        final FeatureReader reader = store.getFeatureReader(query);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize((Iterator)reader, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

}
