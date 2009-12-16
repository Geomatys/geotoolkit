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

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureWriterAppend<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F>{

    protected final T type;

    public AbstractFeatureWriterAppend(T type){
        if(type == null){
            throw new NullPointerException("Type can not be null.");
        }
        this.type = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getFeatureType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() throws DataStoreRuntimeException {
        throw new DataStoreRuntimeException("Can not remove from a feature writer append.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return false;
    }

}
