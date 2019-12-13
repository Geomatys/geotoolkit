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
package org.geotoolkit.processing.vector.intersection;

import java.util.NoSuchElementException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Intersection process
 * @author Quentin Boileau
 * @module
 */
public class IntersectionFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection intersList;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param intersList FeatureCollection
     */
    public IntersectionFeatureCollection(final FeatureCollection originalFC, final FeatureCollection intersList,
            final String geometryName) {
        super(originalFC);
        this.intersList = intersList;
        this.geometryName = geometryName;
        this.newFeatureType = VectorProcessUtils.oneGeometryFeatureType(originalFC.getType(), geometryName);
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
        throw new UnsupportedOperationException("Function didn't used");
    }

    private FeatureCollection modify2(final Feature original) {

        try {
            return IntersectionProcess.intersetFeature(original, newFeatureType, intersList, geometryName);

        } catch (FactoryException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (MismatchedDimensionException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (TransformException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (ProcessException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return new IntersectionFeatureIterator(getOriginalFeatureCollection().iterator());
    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module
     */
    private class IntersectionFeatureIterator implements FeatureIterator {

        private final FeatureIterator originalFI;
        private Feature nextFeature;
        private FeatureCollection nextFC;
        private FeatureIterator ite;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public IntersectionFeatureIterator(final FeatureIterator originalFI) {
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
            throw new FeatureStoreRuntimeException("Unmodifiable collection");
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
