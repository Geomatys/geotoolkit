/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.records.RecordsApi;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSON;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.request.records.GetRecords;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * A catalog's records, exposed as a SIS {@link FeatureSet}.
 *
 * <p>OGC API - Records Part 1: Core, backed by {@link RecordsApi#getRecords(GetRecords)}.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class RecordResource extends CollectionItemResource implements FeatureSet {

    private static final int PAGE_SIZE = 1000;

    private final RecordsApi api;
    private FeatureType type;

    public RecordResource(CollectionResource parent, CollectionDescription description) {
        super(parent, description);
        this.api = new RecordsApi(parent.api.getConfiguration());
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (type == null) {
            final GeoJSONFeatureCollection page = fetchPage(0, 1);
            final List<GeoJSONFeature> features = page.getFeatures();
            // ponytail: type inferred from the first item's own properties, no JSON Schema (Part 5)
            // support yet. Catalogs with heterogeneous record properties may fail to convert.
            final GeoJSONFeature sample;
            if (features == null || features.isEmpty()) {
                sample = new GeoJSONFeature();
                sample.setProperties(java.util.Map.of());
            } else {
                sample = features.get(0);
            }
            type = GeoJSON.fromGeoJSON(sample, null).getType();
        }
        return type;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final FeatureType ft = getType();
        final Iterator<Feature> iterator = new PagingIterator(ft);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, parallel);
    }

    private GeoJSONFeatureCollection fetchPage(int offset, int limit) throws DataStoreException {
        try {
            final HttpResponse<Supplier<GeoJSONFeatureCollection>> response = api.getRecords(
                    new GetRecords().collectionId(description.getId()).offset(offset).limit(limit)).send();
            return response.body().get();
        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }
    }

    private final class PagingIterator implements Iterator<Feature> {

        private final FeatureType type;
        private int offset = 0;
        private List<GeoJSONFeature> page = List.of();
        private int index = 0;
        private boolean done = false;

        PagingIterator(FeatureType type) {
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            while (index >= page.size() && !done) {
                try {
                    final GeoJSONFeatureCollection collection = fetchPage(offset, PAGE_SIZE);
                    page = collection.getFeatures() == null ? List.of() : collection.getFeatures();
                } catch (DataStoreException ex) {
                    throw new BackingStoreException(ex);
                }
                index = 0;
                offset += page.size();
                if (page.size() < PAGE_SIZE) {
                    done = true;
                }
            }
            return index < page.size();
        }

        @Override
        public Feature next() {
            if (!hasNext()) throw new NoSuchElementException();
            try {
                return GeoJSON.fromGeoJSON(page.get(index++), type);
            } catch (DataStoreException ex) {
                throw new BackingStoreException(ex);
            }
        }
    }

}
