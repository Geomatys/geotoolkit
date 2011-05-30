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
package org.geotoolkit.process.vector.spacialjoin;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.WrapFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * FeatureCollection for SpacialJoin process
 * @author Quentin Boileau
 * @module pending
 */
public class SpacialJoinFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection<Feature> sourceFC;
    private final boolean method;

    /**
     * Create the new
     * @param sourceFC the source FeatureCollection
     * @param targetFC the target FeatureCollection
     * @param method boolean to set the used method
     */
    public SpacialJoinFeatureCollection(final FeatureCollection<Feature> sourceFC,
            final FeatureCollection<Feature> targetFC, final boolean method) {

        super(targetFC);
        this.sourceFC = sourceFC;
        this.method = method;
        this.newFeatureType = SpacialJoin.concatType(targetFC.getFeatureType(), sourceFC.getFeatureType());
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
        return SpacialJoin.join(original, newFeatureType, sourceFC, method);
    }
}
