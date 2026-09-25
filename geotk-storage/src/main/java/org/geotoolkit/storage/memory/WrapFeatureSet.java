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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * Encapsulate FeatureCollection.
 *
 * @author Quentin Boileau
 * @module
 */
public abstract class WrapFeatureSet extends AbstractFeatureSet {

    protected final FeatureSet originalFC;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     */
    public WrapFeatureSet(final FeatureSet originalFC) {
        super(null);
        this.originalFC = originalFC;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return originalFC.getIdentifier();
    }

    /**
     * Return the feature modify on the fly
     * @param original Feature
     * @return new feature or null if should be skipped
     */
    protected abstract Feature modify(Feature original) throws FeatureStoreRuntimeException;

    /**
     * Return the original FeatureCollection
     * @return FeatureCollection : original
     */
    protected FeatureSet getOriginalFeatureCollection() {
        return originalFC;
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getType() throws DataStoreException {
        return originalFC.getType();
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return originalFC.features(parallel).map(this::modify).filter(Objects::nonNull);
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

}
