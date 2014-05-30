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
package org.geotoolkit.process.vector.douglaspeucker;


import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.WrapFeatureCollection;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Douglas Peucker simplification process
 * @author Quentin Boileau
 * @module pending
 */
public class DouglasPeuckerFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final double inputAccuracy;
    private final Boolean inputBehavior;
    private final Boolean inputLenient;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC
     * @param inputAccuracy
     * @param inputUnit
     * @param inputBehavior
     * @param inputLenient
     */
    public DouglasPeuckerFeatureCollection(final FeatureCollection<Feature> originalFC, final double inputAccuracy,
            final Boolean inputBehavior, final Boolean inputLenient) {
        super(originalFC);
        this.inputAccuracy = inputAccuracy;
        this.inputBehavior = inputBehavior;
        this.inputLenient = inputLenient;
        this.newFeatureType = super.getOriginalFeatureCollection().getFeatureType();
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
            return DouglasPeuckerProcess.simplifyFeature(original, inputAccuracy, inputBehavior, inputLenient);
        } catch (FactoryException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }
}
