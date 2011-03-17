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
package org.geotoolkit.process.vector.clip;

import java.util.NoSuchElementException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.process.vector.VectorFeatureCollection;
import org.geotoolkit.process.vector.clipgeometry.ClipGeometry;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * FeatureCollection for Clip process
 * @author Quentin Boileau
 * @module pending
 */
public class ClipFeatureCollection extends VectorFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection<Feature> clippingList;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param clippingList 
     */
    public ClipFeatureCollection(final FeatureCollection<Feature> originalFC, final FeatureCollection<Feature> clippingList) {
        super(originalFC);
        this.clippingList = clippingList;
        this.newFeatureType = ClipGeometry.changeFeatureType(super.getOriginalFeatureCollection().getFeatureType());

    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getFeatureType() {
        return newFeatureType;
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
        return new ClipFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature original) {
        return Clip.clipFeature(original, newFeatureType, clippingList);
    }

    /**
     * Implementation of FeatureIterator for VectorFeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    private class ClipFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private Feature nextFeature;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public ClipFeatureIterator(final FeatureIterator<?> originalFI) {
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
