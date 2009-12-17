/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data;

import java.util.Collection;

import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @param <F> extends Feature
 * @module pending
 */
public interface FeatureCollection<F extends Feature> extends Collection<F> {

    String getID();

    FeatureType getSchema();

    Envelope getEnvelope() throws DataStoreException;

    FeatureCollection<F> subCollection(Query query) throws DataStoreException;

    /**
     * Override Iterator to return a limited type FeatureIterator.
     * 
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    FeatureIterator<F> iterator() throws DataStoreRuntimeException;

    void addListener();

    void removeListener();

}
