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
package org.geotoolkit.process.vector.regroup;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Regroup process
 * @author Quentin Boileau
 * @module pending
 */
public class RegroupFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final String regroupAttribute;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param intersList FeatureCollection
     * @param geometryName String
     */
    public RegroupFeatureCollection(final FeatureCollection<Feature> originalFC, final String regroupAttribute,
            final String geometryName) {
        super(originalFC);
        this.regroupAttribute = regroupAttribute;
        this.geometryName = geometryName;
        this.newFeatureType = Regroup.regroupFeatureType(originalFC.getFeatureType(), geometryName, regroupAttribute);
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

            final FeatureCollection<Feature> fiteredFC = (FeatureCollection<Feature>) super.getOriginalFeatureCollection().subCollection(filter(attributeValue));
            
            return Regroup.regroupFeature(regroupAttribute, attributeValue, newFeatureType, geometryName, fiteredFC);

        } catch (FactoryException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * Call the getAttributesValues()
     * @return a Collection of Objects
     */
    private Collection<Object> getAttributeValues() {
        return Regroup.getAttributeValues(regroupAttribute, (FeatureCollection<Feature>) super.getOriginalFeatureCollection());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws DataStoreRuntimeException {
        return new RegroupFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    private Query filter(final Object attributeValue)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(
                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

        final Filter filter = ff.equals(ff.property(regroupAttribute), ff.literal(attributeValue));
        return QueryBuilder.filtered(new DefaultName("filter"), filter);

    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module pending
     */
    private class RegroupFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private Feature nextFeature;
        private final Collection<Object> attributeValues;
        private Object nextValue;
        private final Iterator<Object> attributeIterator;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public RegroupFeatureIterator(final FeatureIterator<?> originalFI) {
            this.originalFI = originalFI;

            nextFeature = null;
            attributeValues = getAttributeValues();
            attributeIterator = attributeValues.iterator();
            nextValue = null;
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
            throw new DataStoreRuntimeException("Unmodifiable collection");
        }

        /**
         * Find the next feature 
         */
        private void findNext() {

            if (nextFeature != null) {
                return;
            }

            while (nextFeature == null && attributeIterator.hasNext()) {
                nextValue = attributeIterator.next();
                nextFeature = modify2(nextValue);
            }
        }
    }
}
