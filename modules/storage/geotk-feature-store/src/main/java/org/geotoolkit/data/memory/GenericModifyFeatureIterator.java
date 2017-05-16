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
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;

/**
 * Supports on the fly modification of FeatureIterator contents.
 * This modify the features that match the given filter and changes the attributs values.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GenericModifyFeatureIterator<R extends FeatureIterator> implements FeatureIterator {


    protected final R iterator;
    protected final Filter filter;
    protected final Map<String,?> values;
    protected Feature nextFeature = null;

    /**
     * Creates a new instance of GenericModifyFeatureIterator
     *
     * @param iterator FeatureReader to modify
     */
    private GenericModifyFeatureIterator(final R iterator, final Filter filter, final Map<String,?> newValues) {
        this.iterator = iterator;
        this.filter = filter;
        this.values = newValues;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final Feature f = nextFeature;
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
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if(nextFeature != null) return true;

        if(iterator.hasNext()){
            Feature candidate = iterator.next();
            if(filter.evaluate(candidate)){
                candidate = FeatureExt.copy(candidate);
                //must modify this feature
                for(final Entry<String,?> entry : values.entrySet()){
                    try{
                        candidate.setPropertyValue(entry.getKey(), entry.getValue());
                    }catch(IllegalArgumentException ex){
                        //the property might be null if the query didn't ask for this
                        //dont raise an error, that's normal
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
    public static FeatureIterator wrap(final FeatureIterator reader, final Filter filter, final Map<String, ?> values){
        return new GenericModifyFeatureIterator(reader, filter, values);
    }

    public static Feature apply(Feature candidate, final Map<String, ?> values){

        candidate = FeatureExt.copy(candidate);
        //must modify this feature
        for(final Entry<String,?> entry : values.entrySet()){
            try{
                candidate.setPropertyValue(entry.getKey(), entry.getValue());
            }catch(IllegalArgumentException ex){
                //the property might be null if the query didn't ask for this
                //dont raise an error, that's normal
            }
        }

        return candidate;
    }

}
