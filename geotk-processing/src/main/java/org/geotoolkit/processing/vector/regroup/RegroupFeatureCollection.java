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
package org.geotoolkit.processing.vector.regroup;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.memory.WrapFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Regroup process
 * @author Quentin Boileau
 * @module
 */
public class RegroupFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final String regroupAttribute;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param regroupAttribute
     * @param geometryName String
     */
    public RegroupFeatureCollection(final FeatureCollection originalFC, final String regroupAttribute,
            final String geometryName) {
        super(originalFC);
        this.regroupAttribute = regroupAttribute;
        this.geometryName = geometryName;
        this.newFeatureType = RegroupProcess.regroupFeatureType(originalFC.getType(), geometryName, regroupAttribute);
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
    protected Feature modify(final Feature original) {
        throw new UnsupportedOperationException("Function didn't used");
    }

    /**
     * Run the process algorithm to generate a Feature from an attribute Value
     * @param attributeValue Object
     * @return a Feature
     */
    private Feature modify2(final Object attributeValue) {
        try {
            if(attributeValue != null){
                final FeatureSet fiteredFC = super.getOriginalFeatureCollection().subset(filter(attributeValue));
                return RegroupProcess.regroupFeature(regroupAttribute, attributeValue, newFeatureType, geometryName, fiteredFC);
            }else{
                //In this case the request is Regroup.regroupFeature(null, null, newFeatureType, geometryName, originalFC);
                return RegroupProcess.regroupFeature(regroupAttribute, attributeValue, newFeatureType, geometryName,
                        (FeatureCollection)super.getOriginalFeatureCollection());
            }

        } catch (FactoryException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * Call the getAttributesValues()
     * @return a Collection of Objects
     */
    private Collection<Object> getAttributeValues() {
        return RegroupProcess.getAttributeValues(regroupAttribute, super.getOriginalFeatureCollection());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return new RegroupFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    private FeatureQuery filter(final Object attributeValue)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final FilterFactory ff = FilterUtilities.FF;

        final Filter filter = ff.equal(ff.property(regroupAttribute), ff.literal(attributeValue));
        final FeatureQuery query = new FeatureQuery();
        query.setSelection(filter);
        return query;
    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module
     */
    private class RegroupFeatureIterator implements FeatureIterator {

        private final FeatureIterator originalFI;
        private Feature nextFeature;
        private final Collection<Object> attributeValues;
        private Object nextValue;
        private final Iterator<Object> attributeIterator;
        private boolean alreadyPass;
        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public RegroupFeatureIterator(final FeatureIterator originalFI) {
            this.originalFI = originalFI;

            nextFeature = null;
            attributeValues = getAttributeValues();

            if(attributeValues.isEmpty()){
                attributeIterator = null;
            }else{
                attributeIterator = attributeValues.iterator();
            }
            nextValue = null;
            alreadyPass = false;
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
            throw new FeatureStoreRuntimeException("Unmodifiable collection");
        }

        /**
         * Find the next feature
         */
        private void findNext() {
            if (nextFeature != null) {
                return;
            }
            if(attributeIterator == null && !alreadyPass ){
                while(nextFeature == null){
                    nextFeature = modify2(null);
                }
                alreadyPass = true;
            }

            if(attributeIterator != null){
                while (nextFeature == null && attributeIterator.hasNext()) {
                    nextValue = attributeIterator.next();
                    nextFeature = modify2(nextValue);
                }
            }
        }
    }
}
