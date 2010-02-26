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

import java.io.IOException;
import java.util.List;

import org.geotoolkit.data.AbstractPropertyReader;
import org.geotoolkit.util.Converters;

import org.opengis.feature.type.PropertyDescriptor;

/**
 * Property reader that extract values from a array of objects.
 * The first elements can be skip by defining a start index.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class ArrayPropertyRW extends AbstractPropertyReader{

    private final int startIndex;
    private final List<Object[]> datas;
    private Object[] current = null;
    private int iteIndex = 0;

    ArrayPropertyRW(PropertyDescriptor[] desc, int startIndex, List<Object[]> datas) {
        super(desc);
        this.startIndex = startIndex;
        this.datas = datas;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasNext() throws IOException {
        return iteIndex < datas.size();
    }

    @Override
    public void next() throws IOException {
        current = datas.get(iteIndex);
        iteIndex++;
    }

    @Override
    public Object read(int index) throws IOException, ArrayIndexOutOfBoundsException {
        return current[startIndex+index];
    }

    @Override
    public void read(Object[] buffer) throws IOException {
        for(int i=0,n=getPropertyCount(); i<n; i++){
            buffer[i] = current[startIndex+i];
        }
    }

    public void setValue(int index, Object value){
        current[startIndex+index] = Converters.convert(
                value, metaData[index].getType().getBinding());
    }

    public void remove(){
        //must use -- since iteIndex is already on the next feature
        datas.remove(--iteIndex);
    }

}
