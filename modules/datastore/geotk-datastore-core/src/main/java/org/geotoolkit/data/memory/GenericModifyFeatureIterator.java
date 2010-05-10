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

package org.geotoolkit.data.memory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

/**
 * Supports on the fly modification of FeatureIterator contents.
 * This modify the features that match the given filter and changes the attributs values.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericModifyFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {


    protected final R iterator;
    protected final Filter filter;
    protected final Map<PropertyDescriptor,Object> values;
    protected F nextFeature = null;

    /**
     * Creates a new instance of GenericModifyFeatureIterator
     *
     * @param iterator FeatureReader to modify
     */
    private GenericModifyFeatureIterator(final R iterator, Filter filter, Map<PropertyDescriptor,Object> newValues) {
        this.iterator = iterator;
        this.filter = filter;
        this.values = newValues;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final F f = nextFeature;
            nextFeature = null;
            return f;
        } else {
            throw new NoSuchElementException("No such Feature exsists");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        if(nextFeature != null) return true;

        if(iterator.hasNext()){
            F candidate = iterator.next();
            if(filter.evaluate(candidate)){
                candidate = (F) SimpleFeatureBuilder.copy((SimpleFeature) candidate);
                //must modify this feature
                for(final Entry<PropertyDescriptor,Object> entry : values.entrySet()){
                    final Property prop = candidate.getProperty(entry.getKey().getName());
                    if(prop != null){
                        //the property might be null if the query didn't ask for this
                        //dont raise an error, that's normal
                        prop.setValue(entry.getValue());
                    }
                    
                }
            }

            nextFeature = candidate;
        }

        return nextFeature != null;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureIterator with a modifiycation set
     */
    public static <F extends Feature> FeatureIterator<F> wrap(FeatureIterator<F> reader, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values){
        return new GenericModifyFeatureIterator(reader, filter, values);
    }

}
