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
package org.geotoolkit.process.vector.intersection;

import java.util.NoSuchElementException;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Intersection process
 * @author Quentin Boileau
 * @module pending
 */
public class IntersectionFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection<Feature> intersList;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param intersList FeatureCollection
     */
    public IntersectionFeatureCollection(final FeatureCollection<Feature> originalFC, final FeatureCollection<Feature> intersList,
            final String geometryName) {
        super(originalFC);
        this.intersList = intersList;
        this.geometryName = geometryName;
        this.newFeatureType = VectorProcessUtils.oneGeometryFeatureType(originalFC.getFeatureType(), geometryName);
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
        throw new UnsupportedOperationException("Function didn't used");
    }

    private FeatureCollection modify2(final Feature original) {

        try {
            return Intersection.intersetFeature(original, newFeatureType, intersList, geometryName);

        } catch (FactoryException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (ProcessException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws DataStoreRuntimeException {
        return new IntersectionFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module pending
     */
    private class IntersectionFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private Feature nextFeature;
        private FeatureCollection<Feature> nextFC;
        private FeatureIterator<Feature> ite;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public IntersectionFeatureIterator(final FeatureIterator<?> originalFI) {
            this.originalFI = originalFI;
            nextFeature = null;
            nextFC = null;
            ite = null;
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

            final Feature feat = nextFeature;
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
         * Find the next feature 
         */
        private void findNext() {
            if (nextFeature != null) {
                return;
            }

            while (nextFeature == null) {

                if (nextFC != null) {
                    if (ite.hasNext()) {
                        nextFeature = ite.next();
                        continue;
                    } else {
                        nextFC = null;
                        ite = null;
                    }
                } else {
                    if (originalFI.hasNext()) {
                        nextFC = modify2(originalFI.next());
                        ite = nextFC.iterator();
                    } else {
                        break;
                    }
                }
            }

        }
    }
}
