/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.iterator;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Wrap a feature Iterator and check that it is properly close.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CheckCloseFeatureIterator implements FeatureIterator<Feature>, FeatureReader<FeatureType, Feature>{

    private final FeatureIterator ite;
    private boolean isClosed = false;

    public CheckCloseFeatureIterator(final FeatureIterator ite){
        if(ite == null){
            throw new NullPointerException("Iterator can not be null.");
        }
        this.ite = ite;
    }

    public boolean isClosed(){
        return isClosed;
    }

    @Override
    public Feature next() {
        return ite.next();
    }

    @Override
    public void close() {
        isClosed = true;
        ite.close();
    }

    @Override
    public boolean hasNext() {
        return ite.hasNext();
    }

    @Override
    public void remove() {
        ite.remove();
    }

    @Override
    public FeatureType getFeatureType() {
        if(ite instanceof FeatureReader){
            return ((FeatureReader)ite).getFeatureType();
        }else{
            throw new IllegalArgumentException("Iterator is not a Feature reader.");
        }
    }

}
