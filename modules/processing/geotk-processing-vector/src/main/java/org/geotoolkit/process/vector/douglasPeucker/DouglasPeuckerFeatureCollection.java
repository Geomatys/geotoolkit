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

import java.util.NoSuchElementException;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.process.vector.VectorFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Douglas Peucker simplification process
 * @author Quentin Boileau
 * @module pending
 */
public class DouglasPeuckerFeatureCollection extends VectorFeatureCollection {

    private final FeatureType newFeatureType;
    private final double inputAccuracy;
    private final Unit<Length> inputUnit;
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
            final  Unit<Length> inputUnit, final Boolean inputBehavior, final Boolean inputLenient) {
        super(originalFC);
        this.inputAccuracy = inputAccuracy;
        this.inputUnit = inputUnit;
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
     * Return FeatureIterator connecting to the FeatureIterator from the
     * original FeatureCollection
     * @param hints
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    public FeatureIterator<Feature> iterator(Hints hints) throws DataStoreRuntimeException {
        return new ClipGeometryFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Feature modify(final Feature original) throws DataStoreRuntimeException {
        try {
            return DouglasPeucker.simplifyFeature(original,inputAccuracy,inputUnit,inputBehavior,inputLenient);
        }catch (FactoryException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
           throw new DataStoreRuntimeException(ex);
        } catch (TransformException ex) {
           throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * Implementation of FeatureIterator for VectorFeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    private class ClipGeometryFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private Feature nextFeature;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public ClipGeometryFeatureIterator(final FeatureIterator<?> originalFI) {
            this.originalFI = originalFI;
            nextFeature = null;
        }

        /**
         * Return the Feature modify by the process
         * @return Feature
         */
        @Override
        public Feature next() {
            findNext();

            if (nextFeature == null) {
                throw new NoSuchElementException("No more Feature.");
            }

            Feature feat = nextFeature;
            nextFeature = null;
            return feat;
        }

        /**
         * Close the original FeatureIterator
         */
        @Override
        public void close() {
            originalFI.close();
        }

        /**
         * Return hasNext() result from the original FeatureIterator
         */
        @Override
        public boolean hasNext() {
            findNext();
            return nextFeature != null;
        }

        /**
         * Useless because current FeatureCollection can't be modified
         */
        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Unmodifiable collection");
        }

        /**
         * Find the next feature using clipping process
         */
        private void findNext() {
            if (nextFeature != null) {
                return;
            }

            while (nextFeature == null && originalFI.hasNext()) {
                nextFeature = modify(originalFI.next());
            }
        }
    }
}
