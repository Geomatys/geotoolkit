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
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

    private final Iterator<Entry<String,Object[]>> ite;
    private Entry<String,Object[]> current = null;

    ArrayPropertyRW(final PropertyDescriptor[] desc, final Map<String,Object[]> features) {
        super(desc);
        ite = features.entrySet().iterator();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasNext() throws IOException {
        return ite.hasNext();
    }

    @Override
    public void next() throws IOException {
        if(hasNext()){
            current = ite.next();
        }else{
            //switch to append mode
            current = new SimpleEntry<String, Object[]>("", new Object[getPropertyCount()]);
        }
    }

    @Override
    public Object read(final int index) throws IOException, ArrayIndexOutOfBoundsException {
        return current.getValue()[index];
    }

    @Override
    public void read(final Object[] buffer) throws IOException {
        final Object[] vals = current.getValue();
        for(int i=0,n=getPropertyCount(); i<n; i++){
            buffer[i] = vals[i];
        }
    }

    public void setValue(final int index, final Object value){
        current.getValue()[index] = Converters.convert(
                value, metaData[index].getType().getBinding());
    }

    public void remove(){
        ite.remove();
    }

}
