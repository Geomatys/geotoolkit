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
package org.geotoolkit.data.memory;

import java.util.Map;

import org.geotoolkit.data.AbstractFeatureCollection;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

/**
 *  FeatureCollection for vector process
 * @author Quentin Boileau
 * @module pending
 */
public abstract class WrapFeatureCollection extends AbstractFeatureCollection<Feature> {

    private final FeatureCollection<?> originalFC;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     */
    public WrapFeatureCollection(final FeatureCollection<?> originalFC) {
        super(originalFC.getID(), originalFC.getSource());
        this.originalFC = originalFC;
    }

    /**
     * Return the feature modify by the process
     * @param original Feature
     */
    protected abstract Feature modify(Feature original) throws DataStoreRuntimeException;

    /**
     * Return the original FeatureCollection
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
        return originalFC.getFeatureType();
    }

    /**
     * Return FeatureIterator connecting to the FeatureIterator from the
     * original FeatureCollection
     * @param hints
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws DataStoreRuntimeException {
        return new VectorFeatureIterator(originalFC.iterator());
    }

    /**
     * Useless because current FeatureCollection can't be modified
     * @param filter
     * @param values
     * @throws DataStoreException
     */
    @Override
    public void update(final Filter filter, final Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Unmodifiable collection");
    }

    /**
     * Useless because current FeatureCollection can't be modified
     * @param filter
     * @throws DataStoreException
     */
    @Override
    public void remove(final Filter filter) throws DataStoreException {
        throw new DataStoreException("Unmodifiable collection");
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public boolean isWritable() throws DataStoreRuntimeException {
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + originalFC.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Implementation of FeatureIterator for BufferFeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    private class VectorFeatureIterator extends  WrapFeatureIterator {

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public VectorFeatureIterator(final FeatureIterator<?> originalFI) {
            super(originalFI);
        }

        @Override
        protected Feature modify(Feature feature){
            return WrapFeatureCollection.this.modify(feature);
        }

    }
}
