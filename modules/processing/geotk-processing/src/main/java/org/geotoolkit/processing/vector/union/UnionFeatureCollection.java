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
package org.geotoolkit.processing.vector.union;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.opengis.feature.AttributeType;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.memory.WrapFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;


/**
 * FeatureCollection for Union process
 *
 * @author Quentin Boileau
 */
public class UnionFeatureCollection extends WrapFeatureCollection {

    private final FeatureType newFeatureType;
    private final FeatureCollection unionFC;

    /** Geometry attribute name form inputFC used to compute intersections */
    private final String inputGeomName;

    /** Geometry attribute name form unionFC used to compute intersections */
    private final String unionGeomName;

    /**
     * Connect to the original FeatureConnection
     */
    public UnionFeatureCollection(final FeatureCollection inputFC, final FeatureCollection unionFC,
            final String inputGeomName, final String unionGeomName)
    {
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
            final PropertyType property = inputFC.getFeatureType().getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
            this.inputGeomName = property.getName().toString();
            geometryCRS = FeatureExt.getCRS(property);
        } else {
            this.inputGeomName = inputGeomName;
            final AttributeType<?> buffDesc = (AttributeType<?>) inputFC.getFeatureType().getProperty(inputGeomName);
            geometryCRS = FeatureExt.getCRS(buffDesc);
        }

        if (unionGeomName == null) {
            this.unionGeomName = AttributeConvention.GEOMETRY_PROPERTY.toString();
        } else {
            this.unionGeomName = unionGeomName;
        }

        // Create the new FeatureType which concatenate two FeatureCollection FeatureType but with only one geometry
        // (inputGeomName)
        this.newFeatureType = UnionProcess.mergeType(inputFC.getFeatureType(), unionFC.getFeatureType(), this.inputGeomName, geometryCRS);
    }

    /**
     * Return the new FeatureType
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
                return UnionProcess.unionFeatureToFC(original, newFeatureType, unionFC, inputGeomName, unionGeomName, firstPass, featureList);
            } else {
                return UnionProcess.unionFeatureToFC(original, newFeatureType, getOriginalFeatureCollection(), unionGeomName, inputGeomName, firstPass, featureList);
            }
        } catch (TransformException | FactoryException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return new IntersectionFeatureIterator(getOriginalFeatureCollection().iterator(), unionFC.iterator());
    }

    /**
     * Implementation of FeatureIterator
     * @author Quentin Boileau
     * @module
     */
    private class IntersectionFeatureIterator implements FeatureIterator {

        private final FeatureIterator originalFI;
        private final FeatureIterator unionFI;
        private Feature nextFeature;
        private FeatureCollection nextFC;
        private FeatureIterator ite;

        /*
         * This boolean if used to do a second pass on the process in inverting FeatureCollection input and union.
         * During the second pass, duplicates entries are removed and Features form union FeatureCollection without
         * intersection with input FeatureCollection Features and remaining Features are added.
         */
        private boolean firstPass;

        /*
         * This Set contain all features already created, it is used in order to remove
         * duplicates features during the second pass
         */
        private final Set<String> featureList;

        /**
         * Connect to the original FeatureIterator
         */
        public IntersectionFeatureIterator(final FeatureIterator originalFI, final FeatureIterator unionFI) {
            this.originalFI = originalFI;
            this.unionFI = unionFI;
            nextFeature = null;
            nextFC = null;
            ite = null;
            firstPass = true;
            featureList = new HashSet<>();
        }

        /**
         * Return the Feature modify by the process
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
