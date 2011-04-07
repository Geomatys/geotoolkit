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
package org.geotoolkit.process.vector.union;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.memory.WrapFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Union process
 * @author Quentin Boileau
 * @module pending
 */
public class UnionFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection<Feature> unionFC;
    private final String inputGeomName; /* Geometry attribute name form inputFC used to compute intersections */

    private final String unionGeomName; /* Geometry attribute name form unionFC used to compute intersections */


    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param intersList FeatureCollection
     */
    public UnionFeatureCollection(final FeatureCollection<Feature> inputFC, final FeatureCollection<Feature> unionFC,
            final String inputGeomName, final String unionGeomName) {

        super(inputFC);
        this.unionFC = unionFC;

        /*
         * We ensure that inputGeomName and unionGeomName are not null and we get the CRS of
         * these geometry. This in order to genereate the new FeatureType based on two FeatureType
         * and with only one geometry (in this case inputGeomName)
         * If inputGeomName or unionGeomName is null, we use default geometry name and CRS.
         */
        CoordinateReferenceSystem geometryCRS;
        if (inputGeomName == null) {
            this.inputGeomName = inputFC.getFeatureType().getGeometryDescriptor().getName().getLocalPart();
            geometryCRS = inputFC.getFeatureType().getGeometryDescriptor().getCoordinateReferenceSystem();
        } else {
            this.inputGeomName = inputGeomName;
            final GeometryDescriptor buffDesc = (GeometryDescriptor) inputFC.getFeatureType().getDescriptor(inputGeomName);
            geometryCRS = buffDesc.getCoordinateReferenceSystem();
        }

        if (unionGeomName == null) {
            this.unionGeomName = unionFC.getFeatureType().getGeometryDescriptor().getName().getLocalPart();
        } else {
            this.unionGeomName = unionGeomName;
        }

        // Create the new FeatureType which concatenate two FeatureCollection FeatureType but with only one geometry
        // (inputGeomName)
        this.newFeatureType = Union.mergeType(inputFC.getFeatureType(), unionFC.getFeatureType(), this.inputGeomName, geometryCRS);

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

    private FeatureCollection modify2(final Feature original, final boolean firstPass, final Set<String> featureList) {
        try {
            if (firstPass) {
                return Union.unionFeatureToFC(original, newFeatureType, unionFC, inputGeomName, unionGeomName, firstPass, featureList);
            } else {
                return Union.unionFeatureToFC(original, newFeatureType, getOriginalFeatureCollection(), unionGeomName, inputGeomName, firstPass, featureList);
            }

        } catch (TransformException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (FactoryException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator<Feature> iterator(final Hints hints) throws DataStoreRuntimeException {
        return new IntersectionFeatureIterator(getOriginalFeatureCollection().iterator(), unionFC.iterator());
    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module pending
     */
    private class IntersectionFeatureIterator implements FeatureIterator<Feature> {

        private final FeatureIterator<?> originalFI;
        private final FeatureIterator<?> unionFI;
        private Feature nextFeature;
        private FeatureCollection<Feature> nextFC;
        private FeatureIterator<Feature> ite;

        /*
         * This boolean if used to do a second pass on the process in inverting FeatureCollection input and union.
         * During the second pass, we remove duplicates entries and add Features form union FeatureCollection without
         * intersection with inputFeatureCollection Features and remaining Features.
         */
        private boolean firstPass;

        /*
         * This Set contain all features already created, it is used in order to remove
         * duplicates features during the second pass
         */
        private final Set<String> featureList;

        /**
         * Connect to the original FeatureIterator
         * @param originalFI FeatureIterator
         */
        public IntersectionFeatureIterator(final FeatureIterator<?> originalFI, final FeatureIterator<?> unionFI) {
            this.originalFI = originalFI;
            this.unionFI = unionFI;
            nextFeature = null;
            nextFC = null;
            ite = null;
            firstPass = true;

            featureList = new HashSet<String>();
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
                    if (firstPass) { //first pass iterate on the original FeatureCollection
                        if (originalFI.hasNext()) {
                            nextFC = modify2(originalFI.next(), firstPass, featureList);
                            ite = nextFC.iterator();
                        } else {
                            firstPass = false;
                        }
                    } else {
                        if (unionFI.hasNext()) { //second pass iterate on the union FeatureCollection
                            nextFC = modify2(unionFI.next(), firstPass, featureList);
                            ite = nextFC.iterator();
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
}
