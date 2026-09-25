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
package org.geotoolkit.processing.vector.merge;

import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * FeatureCollection for Merge process
 * @author Quentin Boileau
 * @module
 */
public class MergeFeatureCollection extends WrapFeatureSet {


    private final FeatureSet[] fcList;
    private final FeatureType newFeatureType;

    /**
     * Connect to the original FeatureConnection
     * @param fcList array of FeatureCollection
     * @param firstFC
     */
    public MergeFeatureCollection(final FeatureSet[] fcList, final FeatureSet firstFC) throws DataStoreException {
        super(firstFC);
        this.fcList = fcList;
        this.newFeatureType = firstFC.getType();
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
    protected Feature modify(final Feature feature) {
        throw new UnsupportedOperationException("Function didn't used");
    }

    /**
     *
     * @param feature
     * @param map
     * @return result feature
     */
    protected Feature modify2(final Feature feature, final Map<GenericName,ObjectConverter> map) {
        try {
            return MergeProcess.mergeFeature(feature, newFeatureType, map);
        } catch (UnconvertibleObjectException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        Stream<Feature> stream = Stream.empty();
        for (FeatureSet fs : fcList) {
            final Map<GenericName,ObjectConverter> conversionMap = MergeProcess.createConversionMap(newFeatureType, fs.getType());
            stream = Stream.concat(stream, fs.features(parallel).map(f -> modify2(f, conversionMap)));
        }
        return stream;
    }

}
