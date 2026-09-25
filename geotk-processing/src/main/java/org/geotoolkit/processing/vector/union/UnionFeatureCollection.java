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
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * FeatureCollection for Union process
 *
 * @author Quentin Boileau
 */
public class UnionFeatureCollection extends WrapFeatureSet {

    private final FeatureType newFeatureType;
    private final FeatureSet unionFC;

    /** Geometry attribute name form inputFC used to compute intersections */
    private final String inputGeomName;

    /** Geometry attribute name form unionFC used to compute intersections */
    private final String unionGeomName;

    /**
     * Connect to the original FeatureConnection
     */
    public UnionFeatureCollection(final FeatureSet inputFC, final FeatureSet unionFC,
            final String inputGeomName, final String unionGeomName) throws DataStoreException
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
            final PropertyType property = inputFC.getType().getProperty(AttributeConvention.GEOMETRY);
            this.inputGeomName = property.getName().toString();
            geometryCRS = FeatureExt.getCRS(property);
        } else {
            this.inputGeomName = inputGeomName;
            final AttributeType<?> buffDesc = (AttributeType<?>) inputFC.getType().getProperty(inputGeomName);
            geometryCRS = FeatureExt.getCRS(buffDesc);
        }

        if (unionGeomName == null) {
            this.unionGeomName = AttributeConvention.GEOMETRY;
        } else {
            this.unionGeomName = unionGeomName;
        }

        // Create the new FeatureType which concatenate two FeatureCollection FeatureType but with only one geometry
        // (inputGeomName)
        this.newFeatureType = UnionProcess.mergeType(inputFC.getType(), unionFC.getType(), this.inputGeomName, geometryCRS);
    }

    /**
     * Return the new FeatureType
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

    private List<Feature> modify2(final Feature original, final boolean firstPass, final Set<String> featureList) {
        try {
            if (firstPass) {
                return UnionProcess.unionFeatureToFC(original, newFeatureType, unionFC, inputGeomName, unionGeomName, firstPass, featureList);
            } else {
                return UnionProcess.unionFeatureToFC(original, newFeatureType, getOriginalFeatureCollection(), unionGeomName, inputGeomName, firstPass, featureList);
            }
        } catch (TransformException | FactoryException | DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {

        /*
         * This Set contain all features already created, it is used in order to remove
         * duplicates features during the second pass
         */
        final Set<String> featureList = new HashSet<>();

        //first stream on the first collection
        Stream<Feature> stream1 = originalFC.features(false);
        stream1 = stream1.flatMap(f -> modify2(f, true, featureList).stream());
         //second pass iterate on the union FeatureCollection
        Stream<Feature> stream2 = unionFC.features(false);
        stream2 = stream2.flatMap(f -> modify2(f, false, featureList).stream());

        return Stream.concat(stream1, stream2);
    }

}
