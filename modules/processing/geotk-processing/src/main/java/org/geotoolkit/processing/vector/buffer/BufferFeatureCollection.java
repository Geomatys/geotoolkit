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
package org.geotoolkit.processing.vector.buffer;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Buffer process
 * @author Quentin Boileau
 * @module pending
 */
public class BufferFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final double inputDistance;
    private final Boolean inputLenient;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC
     * @param inputAccuracy
     * @param inputBehavior
     * @param inputLenient
     */
    public BufferFeatureCollection(final FeatureCollection originalFC, final double inputDistance, final Boolean inputLenient) {
        super(originalFC);
        this.inputDistance = inputDistance;
        this.inputLenient = inputLenient;
        this.newFeatureType = VectorProcessUtils.changeGeometryFeatureType(super.getFeatureType(), Geometry.class);
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
    public Feature modify(final Feature original) throws FeatureStoreRuntimeException {
        try {
            return BufferProcess.makeBuffer(original, newFeatureType, inputDistance, inputLenient);
        } catch (FactoryException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }
}
