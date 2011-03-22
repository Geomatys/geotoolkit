/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector;

import java.util.Map;
import java.util.NoSuchElementException;

import org.geotoolkit.data.AbstractFeatureCollection;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

/**
 *  FeatureCollection for vector process
 * @author Quentin Boileau
 * @module pending
 */
public abstract class VectorFeatureCollection extends AbstractFeatureCollection<Feature> {

    private final FeatureCollection<?> originalFC;
    private final FeatureType featureType;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     */
    public VectorFeatureCollection(final FeatureCollection<?> originalFC) {
        super(originalFC.getID(), originalFC.getSource());
        this.originalFC = originalFC;
        this.featureType = originalFC.getFeatureType();
    }

    /**
     * Return the feature modify by the process
     * @param original Feature
     */
    protected abstract Feature modify(Feature original) throws DataStoreRuntimeException;

    /**
     * Return the orignal FeatureCollection
     * @return FeatureCollection : original
     */
    protected FeatureCollection<?> getOriginalFeatureCollection() {
        return originalFC;
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureCollection<Feature> subCollection(Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Return FeatureIterator connecting to the FeatureIterator from the
     * original FeatureCollection
     * @param hints
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    public FeatureIterator<Feature> iterator(Hints hints) throws DataStoreRuntimeException {
        return new VectorFeatureIterator(originalFC.iterator());
    }

    /**
     * Useless because current FeatureCollection can't be modified
     * @param filter
     * @param values
     * @throws DataStoreException
     */
    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Unmodifiable collection");
    }

    /**
     * Useless because current FeatureCollection can't be modified
     * @param filter
     * @throws DataStoreException
     */
    @Override
    public void remove(Filter filter) throws DataStoreException {
        throw new DataStoreException("Unmodifiable collection");
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public boolean isWritable() throws DataStoreRuntimeException {
        return false;
    }

   /**
     * Implementation of FeatureIterator for BufferFeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    private class VectorFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private Feature nextFeature;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public VectorFeatureIterator(final FeatureIterator<?> originalFI) {
            this.originalFI = originalFI;
            nextFeature = null;
        }

        /**
         * Return the Feature modify by the process
         * @return Feature
         */
        @Override
        public Feature next() {
            findNext();

            if (nextFeature == null) {
                throw new NoSuchElementException("No more Feature.");
            }

            Feature feat = nextFeature;
            nextFeature = null;
            return feat;
        }

        /**
         * Close the original FeatureIterator
         */
        @Override
        public void close() {
            originalFI.close();
        }

        /**
         * Return hasNext() result from the original FeatureIterator
         */
        @Override
        public boolean hasNext() {
            findNext();
            return nextFeature != null;
        }

        /**
         * Useless because current FeatureCollection can't be modified
         */
        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Unmodifiable collection");
        }

        /**
         * Find the next feature using clipping process
         */
        private void findNext() {
            if (nextFeature != null) {
                return;
            }

            while (nextFeature == null && originalFI.hasNext()) {
                nextFeature = modify(originalFI.next());
            }
        }
    }
}
