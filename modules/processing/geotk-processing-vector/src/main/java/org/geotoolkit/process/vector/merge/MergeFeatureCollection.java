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
package org.geotoolkit.process.vector.merge;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 * FeatureCollection for Merge process
 * @author Quentin Boileau
 * @module pending
 */
public class MergeFeatureCollection extends WrapFeatureCollection {


    private final Collection<FeatureCollection> fcList;
    private final FeatureType newFeatureType;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param clippingList 
     */
    public MergeFeatureCollection(final Collection<FeatureCollection> fcList, final FeatureCollection firstFC) {
        super(firstFC);
        this.fcList = fcList;
        this.newFeatureType = firstFC.getFeatureType();
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
    protected Feature modify2(final Feature feature, final Map<Name,ObjectConverter> map) {
        try {
            return Merge.mergeFeature(feature, newFeatureType, map);
        } catch (NonconvertibleObjectException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

     @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws DataStoreRuntimeException {
        try {
            return new MergeFeatureIterator(fcList.iterator());
        } catch (NonconvertibleObjectException ex) {
           throw new DataStoreRuntimeException(ex);
        }
    }

    
    /**
     * Implementation of FeatureIterator which iterate from many FeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    private class MergeFeatureIterator implements FeatureIterator<Feature> {

        private final Iterator<FeatureCollection> fcIter;
        
        private FeatureCollection<Feature> nextFC;
        private FeatureIterator<Feature> ite;

        private Feature nextFeature;
        private Map<Name,ObjectConverter> conversionMap;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public MergeFeatureIterator(final Iterator<FeatureCollection> fcListIter) throws NonconvertibleObjectException {

            this.fcIter = fcListIter;
            
            if(fcIter.hasNext()){
                nextFC = fcIter.next();
                ite = nextFC.iterator();
                conversionMap = Merge.createConversionMap(newFeatureType, nextFC.getFeatureType());
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
            throw new DataStoreRuntimeException("Unmodifiable collection");
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
                        //ite.close();
                        //ite = null;
                    }
                } else {
                    if (fcIter.hasNext()) {
                        try {
                            nextFC = fcIter.next();
                            ite = nextFC.iterator();
                            conversionMap = Merge.createConversionMap(newFeatureType, nextFC.getFeatureType());
                        } catch (NonconvertibleObjectException ex) {
                           throw new DataStoreRuntimeException(ex);
                        }
                    } else {
                        break;
                    }
                }
            }

        }
    }
}
