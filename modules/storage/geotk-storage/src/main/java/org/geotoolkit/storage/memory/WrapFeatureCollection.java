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
package org.geotoolkit.storage.memory;

import java.util.Map;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.feature.AbstractFeatureCollection;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;

/**
 * Encapsulate FeatureCollection.
 *
 * @author Quentin Boileau
 * @module
 */
public abstract class WrapFeatureCollection extends AbstractFeatureCollection {

    private final FeatureCollection originalFC;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     */
    public WrapFeatureCollection(final FeatureCollection originalFC) {
        super(NamedIdentifier.castOrCopy(originalFC.getIdentifier().get()), originalFC.getSession());
        this.originalFC = originalFC;
    }

    /**
     * Return the feature modify on the fly
     * @param original Feature
     */
    protected abstract Feature modify(Feature original) throws FeatureStoreRuntimeException;

    /**
     * Return the original FeatureCollection
     * @return FeatureCollection : original
     */
    protected FeatureCollection getOriginalFeatureCollection() {
        return originalFC;
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getType() {
        return originalFC.getType();
    }

    /**
     * Return FeatureIterator connecting to the FeatureIterator from the
     * original FeatureCollection
     * @param hints
     * @return FeatureIterator
     * @throws FeatureStoreRuntimeException
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return new VectorFeatureIterator(originalFC.iterator());
    }

    @Override
    public boolean isEmpty() {
        final boolean empty;
        try (FeatureIterator ite = iterator()) {
            empty = !ite.hasNext();
        }
        return empty;
    }

    /**
     * Useless because current FeatureCollection can't be modified
     * @param filter
     * @param values
     * @throws DataStoreException
     */
    @Override
    public void update(final Filter filter, final Map<String, ?> values) throws DataStoreException {
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
    public boolean isWritable() throws FeatureStoreRuntimeException {
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
     * Implementation of WarpFeatureIterator
     * @author Quentin Boileau
     * @module
     */
    private class VectorFeatureIterator extends  WrapFeatureIterator {

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public VectorFeatureIterator(final FeatureIterator originalFI) {
            super(originalFI);
        }

        @Override
        protected Feature modify(Feature feature){
            return WrapFeatureCollection.this.modify(feature);
        }

    }
}
