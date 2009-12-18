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
package org.geotoolkit.data;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.geotoolkit.data.query.SortByComparator;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataUtilities {

    private DataUtilities() {
    }

    public static long calculateCount(FeatureIterator reader) throws DataStoreRuntimeException{
        long count = 0;

        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }

        return count;
    }

    /**
     * Iterate on the given iterator and calculate the envelope.
     * @throws DataStoreRuntimeException
     */
    public static Envelope calculateEnvelope(FeatureIterator iterator) throws DataStoreRuntimeException{
        if(iterator == null){
            throw new NullPointerException("Iterator can not be null");
        }
        
        BoundingBox env = null;

        try{
            while(iterator.hasNext()){
                final Feature f = iterator.next();
                final BoundingBox bbox = f.getBounds();
                if(!bbox.isEmpty()){
                    if(env != null){
                        env.include(bbox);
                    }else{
                        env = new DefaultBoundingBox(bbox, bbox.getCoordinateReferenceSystem());
                    }
                }
            }
        }finally{
            iterator.close();
        }

        return env;
    }

    public static FeatureCollection sequence(String id, FeatureCollection... collections) {
        return new FeatureCollectionSequence(id, collections);
    }

    public static <F extends Feature> FeatureIterator<F> sequence(FeatureIterator<F> ... iterators){
        return new FeatureIteratorSequence<F>(iterators);
    }
    
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> sequence(FeatureReader<T,F> ... readers){
        return new FeatureReaderSequence<T, F>(readers);
    }

    /**
     * Combine several FeatureIterator in one and merge them using the sort by orders.
     * All given iterator must already be sorted.
     *
     * @param <F> extends Feature
     * @param sorts : sorting orders
     * @param iterators : iterators to combine
     * @return FeatureIterator combining all others
     */
    public static <F extends Feature> FeatureIterator<F> combine(SortBy[] sorts, FeatureIterator<F> ... iterators){
        return combine(new SortByComparator(sorts), iterators);
    }

    /**
     * Combine several FeatureIterator in one and merge them using the comparator given.
     * All given iterators must already be sorted.
     *
     * @param <F> extends Feature
     * @param comparator : comparator
     * @param iterators : iterators to combine
     * @return FeatureIterator combining all others
     */
    public static <F extends Feature> FeatureIterator<F> combine(Comparator<? super F> comparator, FeatureIterator<F> ... iterators){
        if(iterators == null || iterators.length < 2 || (iterators.length == 1 && iterators[0] == null)){
            throw new IllegalArgumentException("There must be at least 2 non null iterators.");
        }
        if(comparator == null){
            throw new NullPointerException("Comprator can not be null.");
        }

        FeatureIterator<F> ite = iterators[0];

        for(int i=1; i<iterators.length; i++){
            ite = new FeatureIteratorCombine(comparator, ite, iterators[i]);
        }

        return ite;
    }

    /**
     * Provide a collection that link several collections in one.
     * All collection are appended in the order they are given like a sequence.
     * This implementation doesn't copy the features, it will call each wraped
     * collection one after the other.
     *
     * @author Johann Sorel (Geomatys)
     * @module pending
     */
    private static class FeatureCollectionSequence extends AbstractFeatureCollection {

        private final FeatureCollection[] wrapped;

        private FeatureCollectionSequence(String id, FeatureCollection[] wrapped) {
            super(id, (SimpleFeatureType) wrapped[0].getSchema());

            if(wrapped.length == 1){
                throw new IllegalArgumentException("Sequence of featureCollection must have at least 2 collections.");
            }

            this.wrapped = wrapped;
        }

        @Override
        public int size() {
            int size = 0;
            for (FeatureCollection c : wrapped) {
                size += c.size();
            }
            return size;
        }

        @Override
        public FeatureIterator iterator() throws DataStoreRuntimeException {
            return new SequenceIterator();
        }

        @Override
        public Envelope getEnvelope() throws DataStoreException {
            CoordinateReferenceSystem crs = null;
            if (wrapped.length > 0) {
                crs = wrapped[0].getEnvelope().getCoordinateReferenceSystem();
            }
            final JTSEnvelope2D env = new JTSEnvelope2D(crs);

            for (FeatureCollection c : wrapped) {
                env.expandToInclude(new JTSEnvelope2D(c.getEnvelope()));
            }

            return env;
        }

        public static FeatureCollection sequence(FeatureCollection... cols) {
            return new FeatureCollectionSequence("collection-1", cols);
        }

        public static FeatureCollection sequence(String id, FeatureCollection... cols) {
            return new FeatureCollectionSequence(id, cols);
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        private class SequenceIterator implements FeatureIterator {

            private int currentCollection = -1;
            private FeatureIterator ite = null;

            public SequenceIterator() {
                currentCollection = 0;
                ite = wrapped[currentCollection].iterator();
            }

            @Override
            public void close() {
                if (ite != null) {
                    ite.close();
                }
            }

            @Override
            public boolean hasNext() {

                if (ite == null) {
                    return false;
                }

                if (ite.hasNext()) {
                    return true;
                } else {
                    ite.close();
                }

                currentCollection++;
                while (currentCollection < wrapped.length) {
                    ite = wrapped[currentCollection].iterator();

                    if (ite.hasNext()) {
                        return true;
                    } else {
                        ite.close();
                    }

                    currentCollection++;
                }

                return false;
            }

            @Override
            public Feature next() {
                if (ite == null) {
                    throw new NoSuchElementException("No more elements");
                } else {
                    return ite.next();
                }
            }

            @Override
            public void remove() {
                if (ite == null) {
                    throw new NoSuchElementException("No more elements");
                } else {
                    ite.remove();
                }
            }
        }
    }

    /**
     * Provide a way to sequence several featureIterator in one.
     *
     * @author Johann Sorel (Geomatys)
     * @module pending
     */
    private static class FeatureIteratorSequence<F extends Feature> implements FeatureIterator<F> {

        private final FeatureIterator<F>[] wrapped;
        private int currentIndex = 0;
        private FeatureIterator<F> active = null;

        private FeatureIteratorSequence(FeatureIterator<F>[] wrapped) {
            if(wrapped == null || wrapped.length == 0 || wrapped[0] == null){
                throw new IllegalArgumentException("Iterators can not be empty or null");
            }
            this.wrapped = wrapped;
            active = wrapped[0];
        }

        @Override
        public F next() {
            if (active == null) {
                throw new NoSuchElementException("No more elements");
            } else {
                return active.next();
            }
        }

        @Override
        public void close() {
            for(FeatureIterator ite : wrapped){
                ite.close();
            }
        }

        @Override
        public boolean hasNext() {

            if (active == null) {
                return false;
            }

            if (active.hasNext()) {
                return true;
            } else {
                active.close();
            }

            currentIndex++;
            while (currentIndex < wrapped.length) {
                active = wrapped[currentIndex];

                if (active.hasNext()) {
                    return true;
                } else {
                    active.close();
                }

                currentIndex++;
            }

            return false;
        }

        @Override
        public void remove() {
            if(active != null){
                active.remove();
            }
        }

    }

    /**
     * Provide a way to sequence several featureReader in one.
     *
     * @author Johann Sorel (Geomatys)
     * @module pending
     */
    private static class FeatureReaderSequence<T extends FeatureType, F extends Feature> implements FeatureReader<T,F> {

        private final FeatureReader<T,F>[] wrapped;
        private int currentIndex = 0;
        private FeatureReader<T,F> active = null;

        private FeatureReaderSequence(FeatureReader<T,F>[] wrapped) {
            if(wrapped == null || wrapped.length == 0 || wrapped[0] == null){
                throw new IllegalArgumentException("Readers can not be empty or null");
            }
            this.wrapped = wrapped;
            active = wrapped[0];
        }

        @Override
        public F next() {
            if (active == null) {
                throw new NoSuchElementException("No more elements");
            } else {
                return active.next();
            }
        }

        @Override
        public void close() {
            for(FeatureIterator ite : wrapped){
                ite.close();
            }
        }

        @Override
        public boolean hasNext() {

            if (active == null) {
                return false;
            }

            if (active.hasNext()) {
                return true;
            } else {
                active.close();
            }

            currentIndex++;
            while (currentIndex < wrapped.length) {
                active = wrapped[currentIndex];

                if (active.hasNext()) {
                    return true;
                } else {
                    active.close();
                }

                currentIndex++;
            }

            return false;
        }

        @Override
        public void remove() {
            if(active != null){
                active.remove();
            }
        }

        @Override
        public T getFeatureType() {
            return wrapped[0].getFeatureType();
        }

    }

    /**
     * Combine several FeatureIterator and merge them using the comparator given.
     * All given iterator must already be ordered this same comparator, otherwise the results
     * are unpredictable.
     * 
     * @param <F> extends Feature
     */
    private static class FeatureIteratorCombine<F extends Feature> implements FeatureIterator<F>{

        private final FeatureIterator<F> ite1;
        private final FeatureIterator<F> ite2;
        private final Comparator<? super Feature> comparator;
        private FeatureIterator<F> active = null;
        private F ite1next = null;
        private F ite2next = null;
        private F next = null;

        private FeatureIteratorCombine(Comparator<? super Feature> comparator, FeatureIterator<F> ite1, FeatureIterator<F> ite2){
            if(ite1 == null || ite2 == null){
                throw new NullPointerException("Iterators can not be empty or null");
            }
            if(comparator == null ){
                throw new IllegalArgumentException("comparator can not be null or empty. use sequence if you have no comparator.");
            }

            this.comparator = comparator;
            this.ite1 = ite1;
            this.ite2 = ite2;
        }

        @Override
        public F next() {
            if(next == null){
                hasNext();
            }

            if(next == null){
                throw new NoSuchElementException("No more elements.");
            }else{
                F candidate = next;
                next = null;
                return candidate;
            }
        }

        @Override
        public void close() {
            ite1.close();
            ite2.close();
        }

        @Override
        public boolean hasNext() {
            if(next != null) return true;

            if(ite1next == null && ite1.hasNext()){
                ite1next = ite1.next();
            }

            if(ite2next == null && ite2.hasNext()){
                ite2next = ite2.next();
            }

            if (ite1next != null && ite2next != null) {
                
                if(comparator.compare(ite1next, ite2next) <= 0){
                    //ite1next is before
                    next = ite1next;
                    ite1next = null;
                    active = ite1;
                }else{
                    next = ite2next;
                    ite2next = null;
                    active = ite2;
                }
                
            } else if (ite1next == null) {
                next = ite2next;
                ite2next = null;
                active = ite2;
            } else if (ite2next == null) {
                next = ite1next;
                ite1next = null;
                active = ite1;
            } else {
                next = null;
                active = null;
            } 
            
            return next != null;
        }

        @Override
        public void remove() {
            if(active != null){
                active.remove();
            }
        }

    }

}
