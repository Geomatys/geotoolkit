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
package org.geotoolkit.processing.vector.spatialjoin;

import java.util.stream.Stream;
import org.apache.sis.internal.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * FeatureCollection for SpatialJoin process
 * @author Quentin Boileau
 * @module
 */
public class SpatialJoinFeatureCollection extends AbstractFeatureSet {

    private final FeatureType newFeatureType;
    private final FeatureSet targetFC;
    private final FeatureSet sourceFC;
    private final boolean method;

    /**
     * Create the new
     * @param sourceFC the source FeatureCollection
     * @param targetFC the target FeatureCollection
     * @param method boolean to set the used method
     */
    public SpatialJoinFeatureCollection(final FeatureSet sourceFC,
            final FeatureSet targetFC, final boolean method) throws DataStoreException {
        super(null);
        this.targetFC = targetFC;
        this.sourceFC = sourceFC;
        this.method = method;
        this.newFeatureType = SpatialJoinProcess.concatType(targetFC.getType(), sourceFC.getType());
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
    private Feature modify(final Feature original) {
        try {
            return SpatialJoinProcess.join(original, newFeatureType, sourceFC, method);
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return targetFC.features(parallel).map(this::modify);
    }
}
