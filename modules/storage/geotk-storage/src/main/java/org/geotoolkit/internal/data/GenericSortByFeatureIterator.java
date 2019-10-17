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

package org.geotoolkit.internal.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.query.SortByComparator;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
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
 * @module
 */
public class GenericSortByFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

    protected final R iterator;
    private final Comparator<Feature> comparator;
    protected List<Feature> ordered = null;
    protected int index = 0;

    /**
     * Creates a new instance of GenericSortByFeatureIterator
     *
     * @param iterator FeatureReader to sort
     * @param orders sorting orders
     */
    private GenericSortByFeatureIterator(final R iterator, final SortBy[] orders) {
        this.iterator = iterator;
        this.comparator = new SortByComparator(orders);
    }

    private synchronized void sort() throws FeatureStoreRuntimeException{
        if(ordered != null) return;

        ordered = new ArrayList<>();

        while(iterator.hasNext()){
            ordered.add(FeatureExt.copy(iterator.next()));
        }

        Collections.sort(ordered,comparator);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        sort();
        try{
            Feature c = ordered.get(index);
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
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('[').append(comparator).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureReader that will sort features using the given sort by.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericSortByFeatureReader extends GenericSortByFeatureIterator<FeatureReader> implements FeatureReader{

        private GenericSortByFeatureReader(final FeatureReader reader,final SortBy[] orders){
            super(reader,orders);
        }

        @Override
        public FeatureType getFeatureType() {
            return iterator.getFeatureType();
        }

    }

    private static final class GenericSortByFeatureCollection extends WrapFeatureCollection{

        private final SortBy[] order;

        private GenericSortByFeatureCollection(final FeatureCollection original, final SortBy[] order){
            super(original);
            this.order = order;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), order);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader will a sort by order.
     */
    public static FeatureReader wrap(final FeatureReader reader, final SortBy[] orders){
        return new GenericSortByFeatureReader(reader, orders);
    }

    /**
     * Wrap a FeatureIterator will a sort by order.
     */
    public static FeatureIterator wrap(final FeatureIterator reader, final SortBy[] orders){
        if(reader instanceof FeatureReader){
            return wrap((FeatureReader)reader,orders);
        }else{
            return new GenericSortByFeatureIterator(reader, orders);
        }
    }

    /**
     * Wrap a FeatureCollection will a sort by order.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final SortBy[] orders){
        return new GenericSortByFeatureCollection(original,orders);
    }

}
