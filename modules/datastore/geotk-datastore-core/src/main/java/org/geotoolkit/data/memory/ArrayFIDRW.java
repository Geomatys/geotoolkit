/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.memory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureIDReader;

/**
 * Generic Feature ID generator.
 * Will create an id with base + number.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class ArrayFIDRW implements FeatureIDReader{

    private final String base;
    private final AtomicLong inc;
    private final Iterator<String> iteKeys;

    ArrayFIDRW(Map<String,Object[]> features, String base, AtomicLong inc) {
        this.base = base;
        this.inc = inc;
        this.iteKeys = features.keySet().iterator();
    }

    @Override
    public String next() throws DataStoreException {
        if(hasNext()){
            return iteKeys.next();
        }else{
            //create a new key
            return base + inc.getAndIncrement();
        }
    }

    @Override
    public boolean hasNext() throws DataStoreException {
        return iteKeys.hasNext();
    }

    @Override
    public void close() throws DataStoreException {
    }

    public void remove(){
    }

}
