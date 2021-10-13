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
package org.geotoolkit.processing.vector.clipgeometry;

import org.locationtech.jts.geom.Geometry;

import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.memory.WrapFeatureCollection;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;


/**
 * FeatureCollection for Clip process
 * @author Quentin Boileau
 * @module
 */
public class ClipGeometryFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final Geometry clipGeometry;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     */
    public ClipGeometryFeatureCollection(final FeatureCollection originalFC, final Geometry inputClipGeometry) {
        super(originalFC);
        this.clipGeometry = inputClipGeometry;
        this.newFeatureType = VectorProcessUtils.changeGeometryFeatureType(super.getType(), Geometry.class);
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
        return ClipGeometryProcess.clipFeature(original, newFeatureType, clipGeometry);
    }
}
