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

import java.util.List;
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

    private final int idIndex;
    private final String base;
    private final List<Object[]> datas;
    private final AtomicLong inc;
    private Object[] current = null;
    private int iteIndex = 0;

    ArrayFIDRW(int idIndex, List<Object[]> datas, String base, AtomicLong inc) {
        this.idIndex = idIndex;
        this.datas = datas;
        this.base = base;
        this.inc = inc;
    }

    @Override
    public String next() throws DataStoreException {
        if(hasNext()){
            current = datas.get(iteIndex);
            iteIndex++;
            return current[idIndex].toString();
        }else{
            return base + inc.getAndIncrement();
        }
    }

    @Override
    public boolean hasNext() throws DataStoreException {
        return iteIndex < datas.size();
    }

    @Override
    public void close() throws DataStoreException {
    }

    public void remove(){
        iteIndex--;
    }

}
