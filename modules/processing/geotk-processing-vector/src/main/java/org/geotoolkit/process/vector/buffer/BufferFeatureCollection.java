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
package org.geotoolkit.process.vector.buffer;

import com.vividsolutions.jts.geom.Geometry;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.vector.VectorFeatureCollection;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Buffer process
 * @author Quentin Boileau
 * @module pending
 */
public class BufferFeatureCollection extends VectorFeatureCollection {

    private final FeatureType newFeatureType;
    private final double inputDistance;
    private final Unit<Length> inputUnit;
    private final Boolean inputLenient;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC
     * @param inputAccuracy
     * @param inputUnit
     * @param inputBehavior
     * @param inputLenient
     */
    public BufferFeatureCollection(final FeatureCollection<Feature> originalFC, final double inputDistance,
            final Unit<Length> inputUnit, final Boolean inputLenient) {
        super(originalFC);
        this.inputDistance = inputDistance;
        this.inputUnit = inputUnit;
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
    public Feature modify(final Feature original) throws DataStoreRuntimeException {
        try {
            return Buffer.makeBuffer(original, newFeatureType, inputDistance, inputUnit, inputLenient);
        } catch (FactoryException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }
}
