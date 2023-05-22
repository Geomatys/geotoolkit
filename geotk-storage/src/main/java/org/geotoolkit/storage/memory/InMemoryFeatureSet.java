/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.memory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.feature.Features;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.Geometries;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.Utilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.storage.feature.query.QueryUtilities;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * FeatureSet implementation stored in memory.
 * A spatial index is automaticaly created if the FeatureType has a default geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryFeatureSet extends AbstractFeatureSet implements WritableFeatureSet {

    private static final CoordinateReferenceSystem INDEX_CRS = CommonCRS.WGS84.normalizedGeographic();
    private static final JTSEnvelope2D WORLD_ENV = new JTSEnvelope2D(CRS.getDomainOfValidity(INDEX_CRS));

    private final FeatureType type;
    private final List<Feature> features = new CopyOnWriteArrayList<>();
    private GenericName id;
    //keep an index if there is a geometry property
    private String geometryAttribute;
    private CoordinateReferenceSystem defaultGeometryCrs;
    /**
     * Tree is in CRS:84, always.
     */
    private Quadtree tree;

    public InMemoryFeatureSet(FeatureType type) {
        this(null, type, null, true);
    }

    public InMemoryFeatureSet(String id, FeatureType type) {
        this((id != null) ? NamesExt.create(id) : null, type, null, true);
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this collection will be copied.
     */
    public InMemoryFeatureSet(FeatureType type, Collection<Feature> features) {
        this(null, type, features, true);
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this list will be copied.
     */
    public InMemoryFeatureSet(FeatureType type, List<Feature> features) {
        this(null, type, features, true);
    }

    /**
     *
     * @param id featureSet resource identifier
     * @param type stored features type.
     * @param features collection of stored features, this list will be copied.
     */
    public InMemoryFeatureSet(GenericName id, FeatureType type, Collection<Feature> features, boolean createIndex) {
        super(null, false);
        this.id = id;
        this.type = type;
        if (features != null) this.features.addAll(features);

        if (createIndex) {
            createIndex();
        }
    }

    private void createIndex() {
        //build an index if we have a geometry with a crs
        try {
            PropertyType defaultGeometry = FeatureExt.getDefaultGeometry(type);
            Optional<String> linkTarget = Features.getLinkTarget(defaultGeometry);
            if (linkTarget.isPresent()) {
                defaultGeometry = type.getProperty(linkTarget.get());
            }

            Optional<AttributeType<?>> opt = Features.toAttribute(defaultGeometry);
            if (opt.isPresent()) {
                geometryAttribute = defaultGeometry.getName().toString();
                defaultGeometryCrs = FeatureExt.getCRS(opt.get());
                if (defaultGeometryCrs != null) {
                    tree = new Quadtree();
                }
            }
        } catch (PropertyNotFoundException | IllegalStateException ex) {
            //no index
        }

        if (tree != null) {
            for (Feature f : features) {
                tree.insert(getEnvelope(f),f);
            }
        }
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        if (id != null) return Optional.of(id);
        return Optional.of(type.getName());
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public Stream<Feature> features(boolean parallal) {
        Stream<Feature> str = parallal ? features.parallelStream() : features.stream();
        str = str.map(FeatureExt::deepCopy);
        return str;
    }

    private Stream<Feature> features(boolean parallal, Envelope env) throws DataStoreException {
        if (env == null || tree == null) {
            Stream<Feature> str = parallal ? features.parallelStream() : features.stream();
            str = str.map(FeatureExt::deepCopy);
            return str;
        } else {
            try {
                env = Envelopes.transform(env, INDEX_CRS);
            } catch (TransformException ex) {
                throw new DataStoreException("Could not transform query envelope", ex);
            }
            final JTSEnvelope2D jtsenv = new JTSEnvelope2D(env);
            final List<Feature> lst = tree.query(jtsenv);
            return parallal ? lst.parallelStream() : lst.stream();
        }
    }

    @Override
    public FeatureSet subset(Query query) throws UnsupportedQueryException, DataStoreException {
        if (query instanceof FeatureQuery fq && tree != null) {
            final Filter<? super Feature> selection = fq.getSelection();
            final Envelope env = QueryUtilities.extractEnvelope(selection);
            if (env != null && env != QueryUtilities.NO_EVAL) {
                return new SubSet(env).subset(query);
            }
        }
        return super.subset(query);
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void add(Iterator<? extends Feature> features) {
        while (features.hasNext()) {
            final Feature feature = features.next();
            if (tree != null) {
                synchronized (tree) {
                    this.features.add(feature);
                    tree.insert(getEnvelope(feature),feature);
                }
            } else {
                this.features.add(feature);
            }
        }
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super Feature> filter) {
        if (tree != null) {
            boolean removed = false;
            synchronized (tree) {
                for (int i = features.size()-1; i >= 0; i--) {
                    Feature feature = features.get(i);
                    if (removed |= filter.test(feature)) {
                        features.remove(i);
                        tree.remove(getEnvelope(feature), feature);
                    }
                }
            }
            return removed;
        } else {
            return features.removeIf(filter);
        }
    }

    @Override
    public synchronized void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) {
        final ListIterator<Feature> iterator = features.listIterator();
        if (tree != null) {
            synchronized (tree) {
                while (iterator.hasNext()) {
                    final Feature feature = iterator.next();
                    if (filter.test(feature)) {
                        Feature changed = updater.apply(feature);
                        if (changed == null) {
                            iterator.remove();
                            tree.remove(getEnvelope(feature), feature);
                        } else {
                            iterator.set(changed);
                            tree.remove(getEnvelope(feature), feature);
                            tree.insert(getEnvelope(changed), changed);
                        }
                    }
                }
            }
        } else {
            while (iterator.hasNext()) {
                final Feature feature = iterator.next();
                if (filter.test(feature)) {
                    Feature changed = updater.apply(feature);
                    if (changed == null) iterator.remove();
                    else iterator.set(changed);
                }
            }
        }
    }

    /**
     * Return the envelope in CRS:84 for use in the spatial index.
     */
    private JTSEnvelope2D getEnvelope(Feature feature) {
        Object candidate = feature.getPropertyValue(geometryAttribute);
        if (candidate instanceof Geometry geom) {
            final org.locationtech.jts.geom.Envelope jtsEnv = geom.getEnvelopeInternal();
            CoordinateReferenceSystem geomCrs = Geometries.wrap(geom).get().getCoordinateReferenceSystem();
            if (geomCrs == null) geomCrs = defaultGeometryCrs;

            JTSEnvelope2D env = new JTSEnvelope2D(jtsEnv, geomCrs);
            if (!Utilities.equalsIgnoreMetadata(INDEX_CRS, geomCrs)) {
                try {
                    final Envelope cdt = Envelopes.transform(env, INDEX_CRS);
                    if (!Double.isFinite(cdt.getMinimum(0))
                      ||!Double.isFinite(cdt.getMinimum(1))
                      ||!Double.isFinite(cdt.getMaximum(0))
                      ||!Double.isFinite(cdt.getMaximum(1))) {
                        //failed to convert the envelope do index crs, use world instead
                        env = WORLD_ENV;
                    } else {
                        env = new JTSEnvelope2D(cdt);
                    }
                } catch (TransformException ex) {
                    //failed to convert the envelope do index crs, use world instead
                    env = WORLD_ENV;
                }
            }
            return env;
        } else {
            return null;
        }
    }

    private final class SubSet implements FeatureSet {

        private final Envelope env;

        public SubSet(Envelope env) {
            this.env = env;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            return InMemoryFeatureSet.this.getType();
        }

        @Override
        public Stream<Feature> features(boolean parallel) throws DataStoreException {
            return InMemoryFeatureSet.this.features(parallel, env);
        }

        @Override
        public Optional<Envelope> getEnvelope() throws DataStoreException {
            return InMemoryFeatureSet.this.getEnvelope();
        }

        @Override
        public Optional<GenericName> getIdentifier() throws DataStoreException {
            return InMemoryFeatureSet.this.getIdentifier();
        }

        @Override
        public Metadata getMetadata() throws DataStoreException {
            return InMemoryFeatureSet.this.getMetadata();
        }

        @Override
        public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
            InMemoryFeatureSet.this.addListener(eventType, listener);
        }

        @Override
        public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
            InMemoryFeatureSet.this.removeListener(eventType, listener);
        }
    }

}
