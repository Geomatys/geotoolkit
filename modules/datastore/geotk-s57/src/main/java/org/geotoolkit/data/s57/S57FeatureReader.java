/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FeatureReader implements FeatureReader{

    private final FeatureType type;

    public S57FeatureReader(FeatureType type) {
        this.type = type;
    }
    
    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("writing not supported");
    }
    
}
