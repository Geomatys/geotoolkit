/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.apache.sis.feature;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;

/**
 * Iterator over feature properties.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeaturePropertyIterator implements Iterator<Property>{

    private final Feature feature;
    private final Predicate<PropertyType> predicate;
    private final Iterator<? extends PropertyType> ite;
    private Property next = null;

    public FeaturePropertyIterator(Feature feature, Predicate<PropertyType> predicate) {
        this.feature = feature;
        this.predicate = predicate;
        this.ite = this.feature.getType().getProperties(true).iterator();
    }

    @Override
    public boolean hasNext() {
        findNext();
        return next!=null;
    }

    @Override
    public Property next() {
        findNext();
        if(next==null){
            throw new NoSuchElementException();
        }
        final Property temp = next;
        next=null;
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    private void findNext(){
        if(next!=null) return;

        while(next==null && ite.hasNext()){
            final PropertyType pt = ite.next();
            if(predicate.test(pt)){
                next = feature.getProperty(pt.getName().toString());
            }
        }
    }

}
