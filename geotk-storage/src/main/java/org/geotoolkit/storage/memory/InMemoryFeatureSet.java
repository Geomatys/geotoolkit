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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.feature.Features;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.geometry.wrapper.GeometryWrapper;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.storage.feature.query.QueryUtilities;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.index.quadtree.Quadtree;
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
 *
 * <h4>Spatial index</h4>
 * A spatial index is automatically created if the FeatureType has a default geometry.
 * For now, it is limited to a 2D quad-tree using a geographic CRS.
 * It might not be very accurate, but its main purpose is to avoid full-scan on bbox query evaluation.
 * If you do not want the index to be created and filled, you must create your dataset as follows:
 * {@code var dataset = new InMemoryFeatureSet(id, type, features, false); }
 *
 * <h4>Mutability</h4>
 * This implementation is mutable. You can add, remove or update features using {@link WritableFeatureSet} interface.
 * <br/>
 * <em>Warning</em>: For now this implementation does <em>not</em> respect {@link WritableFeatureSet} contract.
 * Therefore, it will <em>not</em> check for duplicates upon insertion.
 * <br/>
 * Other behaviours to pay attention to:
 * <ul>
 *     <li>
 *         In case of error while updating this dataset state, there is no rollback.
 *         The dataset is left in an undefined state, and should not be used anymore (or with extra-care)
 *     </li>
 *     <li>
 *         Features returned by {@link #features(boolean)} are a <em>copy</em> of stored ones.
 *         It means that any modification will <em>not</em> be preserved by this dataset.
 *         Note that this behaviour <em>might change in the future.</em>
 *     </li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class InMemoryFeatureSet extends AbstractFeatureSet implements WritableFeatureSet {

    private static final CoordinateReferenceSystem INDEX_CRS = CommonCRS.WGS84.normalizedGeographic();
    private static final JTSEnvelope2D WORLD_ENV = new JTSEnvelope2D(CRS.getDomainOfValidity(INDEX_CRS));

    private final FeatureType type;
    private final List<Feature> features = new ArrayList<>();
    private final GenericName id;
    //keep an index if there is a geometry property
    private final String geometryAttribute;
    private final CoordinateReferenceSystem defaultGeometryCrs;
    /**
     * Tree is in CRS:84, always.
     * We force the geographic CRS because we cannot determine a single CRS adapted to all features.
     * TODO: In the future, if FeatureSet or FeatureType API defines a CRS <em>constraint</em>, we should be able to improve it.
     */
    private final Quadtree tree;

    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

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
            var defaultGeometry = getDefaultGeometry(type);
            geometryAttribute = defaultGeometry == null ? null : defaultGeometry.getName().toString();
            defaultGeometryCrs = defaultGeometry == null ? null : FeatureExt.getCRS(defaultGeometry);
            if (defaultGeometryCrs == null) {
                tree = null;
            } else {
                tree = new Quadtree();
                for (Feature f : this.features) {
                    final JTSEnvelope2D fEnv = getEnvelope(f);
                    if (fEnv != null) tree.insert(fEnv, f);
                }
            }
        } else {
            geometryAttribute = null;
            defaultGeometryCrs = null;
            tree = null;
        }
    }

    private void write(Runnable writeAction) {
        var wl = stateLock.writeLock();
        wl.lock();
        try {
            writeAction.run();
        } finally {
            wl.unlock();
        }
    }

    private <T> T write(Supplier<T> writeAction) {
        var wl = stateLock.writeLock();
        wl.lock();
        try {
            return writeAction.get();
        } finally {
            wl.unlock();
        }
    }

    private <T> T read(Supplier<T> readAction) {
        var rl = stateLock.readLock();
        rl.lock();
        try {
            return readAction.get();
        } finally {
            rl.unlock();
        }
    }

    /**
     * @return {@code null} if no default geometry can be found.
     */
    private static PropertyType getDefaultGeometry(FeatureType type) {
        PropertyType defaultGeometry = null;
        try {
            defaultGeometry = FeatureExt.getDefaultGeometry(type);
            Optional<String> linkTarget = Features.getLinkTarget(defaultGeometry);
            if (linkTarget.isPresent()) {
                defaultGeometry = type.getProperty(linkTarget.get());
            }
        } catch (PropertyNotFoundException | IllegalStateException ex) {
            // We cannot found any reliable default geometry
        }
        return defaultGeometry;
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
    public Stream<Feature> features(boolean parallel) {
        final Feature[] snapshot = read(() -> features.toArray(Feature[]::new));
        var dataStream = Arrays.stream(snapshot);
        if (parallel) dataStream = dataStream.parallel();
        // TODO: verify if deep-copy is needed.
        return dataStream.map(FeatureExt::deepCopy);
    }

    private Stream<Feature> features(boolean parallel, Envelope env) throws DataStoreException {
        if (env == null) return features(parallel);

        assert tree != null;
        try {
            env = Envelopes.transform(env, INDEX_CRS);
        } catch (TransformException ex) {
            throw new DataStoreException("Could not transform query envelope", ex);
        }

        final JTSEnvelope2D jtsenv = new JTSEnvelope2D(env);
        final List<Feature> lst = read(() -> tree.query(jtsenv));
        var datastream = parallel ? lst.parallelStream() : lst.stream();
        return datastream.map(FeatureExt::deepCopy);
    }

    @Override
    public FeatureSet subset(Query query) throws DataStoreException {
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
        // First, we compute/consume all required information, to avoid locking for too long.
        // It seems preferable because:
        // 1. We do not know how long it takes to extract elements from the iterator (It might do IO, processing, etc.)
        // 2. In case we maintain an index, we'll have to process envelopes, and It might take time.
        List<Feature> buffer = new ArrayList<>();
        List<org.locationtech.jts.geom.Envelope> envs = tree == null ? null : new ArrayList<>();
        while (features.hasNext()) {
            var next = features.next();
            buffer.add(next);
            if (envs != null) envs.add(getEnvelope(next));
        }

        write(() -> {
            this.features.addAll(buffer);
            if (tree != null) {
                for (int i = 0; i < buffer.size(); i++) {
                    final org.locationtech.jts.geom.Envelope env = envs.get(i);
                    if (env != null) tree.insert(env, buffer.get(i));
                }
            }
        });

        // Only add new features once index has been successfully updated.
        // TODO: if tree insertion above fails somewhere in the middle, the tree and feature list will end desynchronized
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) {
        if (tree == null) return write(() -> features.removeIf(filter));

        Map<Feature, org.locationtech.jts.geom.Envelope> toRemove = new HashMap<>();
        for (Feature f : features) {
            if (filter.test(f)) {
                toRemove.put(f, getEnvelope(f));
            }
        }

        if (toRemove.isEmpty()) return false;

        write(() -> {
            toRemove.forEach((f, env) -> tree.remove(env, f));
            features.removeAll(toRemove.keySet());
        });
        return true;
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) {
        if (tree == null) {
            write(() -> features.replaceAll(f -> filter.test(f) ? updater.apply(f) : f));
        } else {
            write(() -> replaceIfWithIndexUpdate(filter, updater));
        }
    }

    private void replaceIfWithIndexUpdate(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) {
        assert tree != null;
        assert stateLock.isWriteLockedByCurrentThread();
        final ListIterator<Feature> iterator = features.listIterator();
        while (iterator.hasNext()) {
            final Feature feature = iterator.next();
            if (filter.test(feature)) {
                Feature changed = updater.apply(feature);
                final JTSEnvelope2D featureEnvelope = getEnvelope(feature);
                if (featureEnvelope != null) tree.remove(featureEnvelope, feature);
                if (changed == null) {
                    iterator.remove();
                } else {
                    iterator.set(changed);
                    final JTSEnvelope2D changedEnvelope = getEnvelope(changed);
                    if (changedEnvelope != null) tree.insert(changedEnvelope, changed);
                }
            }
        }
    }

    /**
     * @return Either:
     * <ul>
     *     <li>{@code null} if input is not a JTS geometry.</li>
     *     <li>{@link #WORLD_ENV} if the conversion to {@link #INDEX_CRS} fails or produce an invalid envelope.</li>
     *     <li>Else the envelope in {@link #INDEX_CRS} to use it in spatial index.</li>
     * </ul>
     */
    private JTSEnvelope2D getEnvelope(Feature feature) {
        var fEnv = Geometries.wrap(feature.getPropertyValue(geometryAttribute))
                .map(GeometryWrapper::getEnvelope)
                .orElse(null);

        if (fEnv == null) return null;
        if (fEnv.getCoordinateReferenceSystem() == null) fEnv.setCoordinateReferenceSystem(defaultGeometryCrs);
        try {
            fEnv = GeneralEnvelope.castOrCopy(Envelopes.transform(fEnv, INDEX_CRS));
        } catch (TransformException ex) {
            return WORLD_ENV;
        }

        var env = new JTSEnvelope2D(fEnv);
        if (!Double.isFinite(env.getMinimum(0))
            ||!Double.isFinite(env.getMinimum(1))
            ||!Double.isFinite(env.getMaximum(0))
            ||!Double.isFinite(env.getMaximum(1))) {
            return WORLD_ENV;
        }

        return env;
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
