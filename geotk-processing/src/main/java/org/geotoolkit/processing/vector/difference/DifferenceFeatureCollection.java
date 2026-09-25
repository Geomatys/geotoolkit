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
package org.geotoolkit.processing.vector.difference;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.locationtech.jts.geom.Geometry;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Difference process
 * @author Quentin Boileau
 * @module
 */
public class DifferenceFeatureCollection extends WrapFeatureSet {

    private final FeatureType newFeatureType;
    private final FeatureSet clippingList;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param clippingList
     */
    public DifferenceFeatureCollection(final FeatureSet originalFC, final FeatureSet clippingList) throws DataStoreException {
        super(originalFC);
        this.clippingList = clippingList;
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
        try {
            return DifferenceProcess.clipFeature(original, newFeatureType, clippingList);
        } catch (MismatchedDimensionException | TransformException | FactoryException | DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }
}
