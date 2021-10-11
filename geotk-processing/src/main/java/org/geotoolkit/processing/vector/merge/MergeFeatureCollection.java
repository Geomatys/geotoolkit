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
package org.geotoolkit.processing.vector.merge;

import java.util.Map;
import java.util.NoSuchElementException;

import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.memory.WrapFeatureCollection;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * FeatureCollection for Merge process
 * @author Quentin Boileau
 * @module
 */
public class MergeFeatureCollection extends WrapFeatureCollection {


    private final FeatureCollection[] fcList;
    private final FeatureType newFeatureType;

    /**
     * Connect to the original FeatureConnection
     * @param fcList array of FeatureCollection
     * @param firstFC
     */
    public MergeFeatureCollection(final FeatureCollection[] fcList, final FeatureCollection firstFC) {
        super(firstFC);
        this.fcList = fcList;
        this.newFeatureType = firstFC.getType();
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getType() {
        return newFeatureType;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature feature) {
        throw new UnsupportedOperationException("Function didn't used");
    }

    /**
     *
     * @param feature
     * @param map
     * @return result feature
     */
    protected Feature modify2(final Feature feature, final Map<GenericName,ObjectConverter> map) {
        try {
            return MergeProcess.mergeFeature(feature, newFeatureType, map);
        } catch (UnconvertibleObjectException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

     @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        try {
            return new MergeFeatureIterator(fcList);
        } catch (UnconvertibleObjectException ex) {
           throw new FeatureStoreRuntimeException(ex);
        }
    }


    /**
     * Implementation of FeatureIterator which iterate from many FeatureCollection
     * @author Quentin Boileau
     * @module
     */
    private class MergeFeatureIterator implements FeatureIterator {

        private final FeatureCollection[] fcList;
        private int fcIter;
        private int nbFC; /* Number of FeatureCollection*/

        private FeatureCollection nextFC;
        private FeatureIterator ite;

        private Feature nextFeature;
        private Map<GenericName,ObjectConverter> conversionMap;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public MergeFeatureIterator(final FeatureCollection[] fcListIter) throws UnconvertibleObjectException {

            this.fcList = fcListIter;
            this.fcIter = 0;
            this.nbFC = fcList.length;

            if(nbFC > 0){
                nextFC = fcList[0];
                ite = nextFC.iterator();
                conversionMap = MergeProcess.createConversionMap(newFeatureType, nextFC.getType());
            }else{
                nextFC = null;
                ite = null;
            }
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

            final Feature feat = nextFeature;
            nextFeature = null;
            return feat;
        }

        /**
         * Close the original FeatureIterator
         */
        @Override
        public void close() {
            ite.close();
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
            throw new FeatureStoreRuntimeException("Unmodifiable collection");
        }

        /**
         * Find the next feature
         */
        private void findNext() {
            if (nextFeature != null) {
                return;
            }

            while (nextFeature == null) {

                if (nextFC != null) {
                    if (ite.hasNext()) {
                        nextFeature = modify2(ite.next(),conversionMap);
                        continue;
                    } else {
                        nextFC = null;
                    }
                } else {
                    if (fcIter < nbFC-1) {
                        try {
                            fcIter++;
                            nextFC = fcList[fcIter];
                            ite = nextFC.iterator();
                            conversionMap = MergeProcess.createConversionMap(newFeatureType, nextFC.getType());


                        } catch (UnconvertibleObjectException ex) {
                           throw new FeatureStoreRuntimeException(ex);
                        }
                    } else {
                        break;
                    }
                }
            }

        }
    }
}
