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

import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Intersection process
 * @author Quentin Boileau
 * @module
 */
public class IntersectionFeatureCollection extends WrapFeatureSet {

    private final FeatureType newFeatureType;
    private final FeatureSet intersList;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param intersList FeatureCollection
     */
    public IntersectionFeatureCollection(final FeatureSet originalFC, final FeatureSet intersList,
            final String geometryName) throws DataStoreException {
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

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return originalFC.features(parallel).flatMap(this::modify2);
    }
    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature original) {
        throw new UnsupportedOperationException("Function didn't used");
    }

    private Stream<Feature> modify2(final Feature original) {

        try {
            return IntersectionProcess.intersetFeature(original, newFeatureType, intersList, geometryName).features(false);

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

}
