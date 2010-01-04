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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.query.SortByComparator;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.sort.SortBy;

/**
 * Basic support for a  FeatureIterator that will sort features using the given sort by
 * orders. This may be very consuming in memory since this implementation must iterate
 * over all features and store them in memory in the right order.
 *
 * Don't use this class in you now that your iterator holds a great amount of features,
 * otherwise it may cause an Out Of Memory Exception.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericSortByFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;
    private final Comparator<Feature> comparator;
    protected List<F> ordered = null;
    protected int index = 0;

    /**
     * Creates a new instance of GenericSortByFeatureIterator
     *
     * @param iterator FeatureReader to sort
     * @param orders sorting orders
     */
    private GenericSortByFeatureIterator(final R iterator, SortBy[] orders) {
        this.iterator = iterator;
        this.comparator = new SortByComparator(orders);
    }

    private synchronized void sort() throws DataStoreRuntimeException{
        if(ordered != null) return;

        ordered = new ArrayList<F>();

        while(iterator.hasNext()){
            ordered.add(iterator.next());
        }

        Collections.sort(ordered,comparator);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        sort();
        try{
            F c = ordered.get(index);
            index++;
            return c;
        }catch(IndexOutOfBoundsException ex){
            throw new NoSuchElementException("No more elements.");
        }
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
    public boolean hasNext() throws DataStoreRuntimeException {
        sort();
        return index < ordered.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * Wrap a FeatureReader that will sort features using the given sort by.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericSortByFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericSortByFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericSortByFeatureReader(R reader,SortBy[] orders){
            super(reader,orders);
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

    }

    /**
     * Wrap a FeatureReader will a sort by order.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(FeatureReader<T,F> reader, SortBy[] orders){
        return new GenericSortByFeatureReader(reader, orders);
    }

    /**
     * Wrap a FeatureIterator will a sort by order.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(FeatureIterator<F> reader, SortBy[] orders){
        return new GenericSortByFeatureIterator(reader, orders);
    }

}
