/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
package org.geotoolkit.display2d.primitive;

import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

/**
 * Convenient representation of a feature for rendering.
 * We expect the sub classes to cache information for more efficient rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ProjectedFeature<T extends Feature> extends ProjectedObject<T> {

    /**
     * Get the id of the feature.
     *
     * @return FeatureId
     */
    FeatureId getFeatureId();

    /**
     * Get the original FeatureMapLayer from where the feature is from.
     *
     * @return FeatureMapLayer
     */
    @Override
    FeatureMapLayer getLayer();

    /**
     * Get the feature itself.
     *
     * @return Feature
     */
    @Override
    T getCandidate();

}
