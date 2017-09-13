/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
import org.geotoolkit.internal.data.GenericModifyFeatureIterator;
import org.geotoolkit.internal.data.GenericCachedFeatureIterator;
import org.geotoolkit.internal.data.GenericDecoratedFeatureIterator;
import org.geotoolkit.internal.data.GenericFilterFeatureIterator;
import org.geotoolkit.internal.data.GenericFilterIterator;
import org.geotoolkit.internal.data.GenericMaxFeatureIterator;
import org.geotoolkit.internal.data.GenericQueryFeatureIterator;
import org.geotoolkit.internal.data.GenericReprojectFeatureIterator;
import org.geotoolkit.internal.data.GenericWrapFeatureIterator;
import org.geotoolkit.internal.data.GenericSortByFeatureIterator;
import org.geotoolkit.internal.data.GenericStartIndexFeatureIterator;
import org.geotoolkit.internal.data.GenericEmptyFeatureIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.SortByComparator;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DecoratedFeatureType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Feature stream utility functions.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureStreams {

    private FeatureStreams(){}

    /**
     * Wrap a FeatureIterator with a cache size.
     * A separate thread is created to load the cache buffer.
     *
     * @param reader source reader
     * @param cacheSize number of features to keep in cache
     * @return cached iterator
     */
    public static FeatureIterator cached(final FeatureIterator reader, final int cacheSize){
        return GenericCachedFeatureIterator.wrap(reader, cacheSize);
    }

    /**
     * Wrap a FeatureReader with a cache size.
     * A separate thread is created to load the cache buffer.
     *
     * @param reader source reader
     * @param cacheSize number of features to keep in cache
     * @return cached reader
     */
    public static FeatureReader cached(final FeatureReader reader, final int cacheSize){
        return GenericCachedFeatureIterator.wrap(reader, cacheSize);
    }

    /**
     * Create an caching FeatureCollection wrapping the given collection.
     * A separate thread is created to load the cache buffer.
     *
     * @param col source collection
     * @param cacheSize number of features to keep in cache
     * @return cached collection
     */
    public static FeatureCollection cached(final FeatureCollection col, final int cacheSize){
        return GenericCachedFeatureIterator.wrap(col, cacheSize);
    }

    /**
     * Create an empty FeatureIterator of the given type.
     *
     * @return empty iterator
     */
    public static FeatureIterator emptyIterator(){
        return GenericEmptyFeatureIterator.createIterator();
    }

    /**
     * Create an empty FeatureReader of the given type.
     *
     * @param type FeatureType can be null
     * @return empty reader
     */
    public static FeatureReader emptyReader(final FeatureType type){
        return GenericEmptyFeatureIterator.createReader(type);
    }

    /**
     * Create an empty FeatureWriter of the given type.
     *
     * @param type FeatureType can be null
     * @return empty writer
     */
    public static FeatureWriter emptyWriter(final FeatureType type){
        return GenericEmptyFeatureIterator.createWriter(type);
    }

    /**
     * Create an empty FeaturCollection wrapping the collection.
     *
     * @param col source collection to mimic
     * @return empty collection
     */
    public static FeatureCollection emptyCollection(final FeatureCollection col){
        return GenericEmptyFeatureIterator.wrap(col);
    }

    /**
     * Decorate a feature reader.
     *
     * @param reader source reader
     * @param mask FeatureType mask
     * @param hints additional hints
     * @return decorated reader
     */
    public static FeatureReader decorate(final FeatureReader reader, final DecoratedFeatureType mask, Hints hints){
        return GenericDecoratedFeatureIterator.wrap(reader, mask, hints);
    }

    /**
     * Decorate a FeatureCollection.
     *
     * @param col source collection
     * @param mask FeatureType mask
     * @return decorated collection
     */
    public static FeatureCollection decorate(final FeatureCollection col, final DecoratedFeatureType mask){
        return GenericDecoratedFeatureIterator.wrap(col, mask);
    }

    /**
     * Wrap an Iterator with a filter.
     * @param <F>
     * @param iterator source iterator
     * @param filter filter used to select matching features
     * @return filtered iterator
     */
    public static <F> Iterator<F> filter(final Iterator<F> iterator, final Filter filter){
        if (filter==null || Filter.INCLUDE.equals(filter)) return iterator;
        return GenericFilterIterator.wrap(iterator, filter);
    }

    /**
     * Wrap a FeatureIterator with a filter.
     * @param iterator source iterator
     * @param filter filter used to select matching features
     * @return filtered iterator
     */
    public static FeatureIterator filter(final FeatureIterator iterator, final Filter filter){
        if (filter==null || Filter.INCLUDE.equals(filter)) return iterator;
        return GenericFilterFeatureIterator.wrap(iterator, filter);
    }

    /**
     * Wrap a FeatureReader with a filter.
     * @param reader source reader
     * @param filter filter used to select matching features
     * @return filtered reader
     */
    public static FeatureReader filter(final FeatureReader reader, final Filter filter){
        if (filter==null || Filter.INCLUDE.equals(filter)) return reader;
        return GenericFilterFeatureIterator.wrap(reader, filter);
    }

    /**
     * Wrap a FeatureWriter with a filter.
     *
     * @param writer source writer
     * @param filter filter used to select matching features
     * @return filtered writer
     */
    public static FeatureWriter filter(final FeatureWriter writer, final Filter filter){
        if (filter==null || Filter.INCLUDE.equals(filter)) return writer;
        return GenericFilterFeatureIterator.wrap(writer, filter);
    }

    /**
     * Create an filtered FeatureCollection wrapping the given collection.
     *
     * @param col source collection
     * @param filter filter used to select matching features
     * @return filtered collection
     */
    public static FeatureCollection filter(final FeatureCollection col, final Filter filter){
        if (filter==null || Filter.INCLUDE.equals(filter)) return col;
        return GenericFilterFeatureIterator.wrap(col, filter);
    }

    /**
     * Wrap a FeatureReader with a max limit.
     *
     * @param iterator source iterator
     * @param limit maximum number of features to return
     * @return reduced iterator
     */
    public static FeatureIterator limit(final FeatureIterator iterator, final int limit){
        return GenericMaxFeatureIterator.wrap(iterator, limit);
    }

    /**
     * Wrap a FeatureReader with a max limit.
     *
     * @param reader source reader
     * @param limit maximum number of features to return
     * @return reduced reader
     */
    public static FeatureReader limit(final FeatureReader reader, final int limit){
        return GenericMaxFeatureIterator.wrap(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a max limit.
     *
     * @param writer source writer
     * @param limit maximum number of features to return
     * @return reduced writer
     */
    public static FeatureWriter limit(final FeatureWriter writer, final int limit){
        return GenericMaxFeatureIterator.wrap(writer, limit);
    }

    /**
     * Create an limited FeatureCollection wrapping the given collection.
     *
     * @param col source collection
     * @param limit maximum number of features to return
     * @return reduced collection
     */
    public static FeatureCollection limit(final FeatureCollection col, final int limit){
        return GenericMaxFeatureIterator.wrap(col, limit);
    }

    /**
     * View a subset of given data.
     *
     * @param reader source reader
     * @param query query used to filter and transform the reader
     * @return feature reader subset
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static FeatureReader subset(FeatureReader reader, final Query query) throws DataStoreException{
        return GenericQueryFeatureIterator.wrap(reader, query);
    }

    /**
     * View a subset of given data.
     *
     * @param col source collection
     * @param query query used to filter and transform the collection
     * @return collection subset
     */
    public static FeatureCollection subset(final FeatureCollection col, final Query query){
        return GenericQueryFeatureIterator.wrap(col, query);
    }

    /**
     * View a subset of given data stream.
     *
     * @param stream source feature stream
     * @param type Type of features in the stream
     * @param query query used to filter and transform the stream
     * @return collection subset
     */
    public static Stream<Feature> subset(final Stream<Feature> stream, final FeatureType type, final Query query) throws DataStoreException{
        return GenericQueryFeatureIterator.wrap(stream, type, query);
    }

    /**
     * Wrap a FeatureReader with a reprojection.
     *
     * @param reader source reader
     * @param crs target CoordinateReferenceSystem
     * @param hints additional hints
     * @return reprojected feature reader
     * @throws org.opengis.util.FactoryException if a transformation operation fails
     */
    public static FeatureReader reproject(final FeatureReader reader, final CoordinateReferenceSystem crs, final Hints hints) throws FactoryException {
        return GenericReprojectFeatureIterator.wrap(reader, crs, hints);
    }

    /**
     * Create a reproject FeatureCollection wrapping the given collection.
     *
     * @param col source collection
     * @param crs target CoordinateReferenceSystem
     * @return reprojected collection
     */
    public static FeatureCollection reproject(final FeatureCollection col, final CoordinateReferenceSystem crs){
        return GenericReprojectFeatureIterator.wrap(col, crs);
    }

    /**
     * Wrap a FeatureIterator will a sort by order.
     *
     * @param iterator source iterator
     * @param orders sorting order
     * @return sorted iterator
     */
    public static FeatureIterator sort(final FeatureIterator iterator, final SortBy ... orders){
        return GenericSortByFeatureIterator.wrap(iterator, orders);
    }

    /**
     * Wrap a FeatureReader will a sort by order.
     *
     * @param reader source reader
     * @param orders sorting order
     * @return sorted reader
     */
    public static FeatureReader sort(final FeatureReader reader, final SortBy ... orders){
        return GenericSortByFeatureIterator.wrap(reader, orders);
    }

    /**
     * Wrap a FeatureCollection will a sort by order.
     *
     * @param col source collection
     * @param orders sorting order
     * @return sorted collection
     */
    public static FeatureCollection sort(final FeatureCollection col, final SortBy ... orders){
        return GenericSortByFeatureIterator.wrap(col, orders);
    }

    /**
     * Wrap a FeatureIterator with a start index.
     *
     * @param iterator source iterator
     * @param offset number of features to skip
     * @return feature iterator
     */
    public static FeatureIterator skip(final FeatureIterator iterator, final int offset){
        return GenericStartIndexFeatureIterator.wrap(iterator, offset);
    }

    /**
     * Wrap a FeatureReader with a start index.
     *
     * @param reader source reader
     * @param offset number of features to skip
     * @return feature reader
     */
    public static FeatureReader skip(final FeatureReader reader, final int offset){
        return GenericStartIndexFeatureIterator.wrap(reader, offset);
    }

    /**
     * Wrap a FeatureWriter with a start index.
     *
     * @param writer source writer
     * @param offset number of features to skip
     * @return feature writer
     */
    public static FeatureWriter skip(final FeatureWriter writer, final int offset){
        return GenericStartIndexFeatureIterator.wrap(writer, offset);
    }

    /**
     * Create an differed start index FeatureCollection wrapping the given collection.
     *
     * @param col source collection
     * @param offset number of features to skip
     * @return collection
     */
    public static FeatureCollection skip(final FeatureCollection col, final int offset){
        return GenericStartIndexFeatureIterator.wrap(col, offset);
    }

    /**
     * Wrap an Iterator as a FeatureIterator.
     *
     * @param iterator source iterator
     * @return feature iterator
     */
    public static FeatureIterator asIterator(final Iterator<? extends Feature> iterator){
        return GenericWrapFeatureIterator.wrapToIterator(iterator);
    }

    /**
     * Wrap an Iterator as a FeatureReader.
     *
     * @param reader source reader
     * @param type reader declared feature type
     * @return feature reader
     */
    public static FeatureReader asReader(final Iterator<? extends Feature> reader, final FeatureType type){
        return GenericWrapFeatureIterator.wrapToReader(reader,type);
    }

    /**
     * Wrap an Iterator as a FeatureWriter.
     *
     * @param writer source writer
     * @param type writer declared feature type
     * @return feature writer
     */
    public static FeatureWriter asWriter(final Iterator<? extends Feature> writer, final FeatureType type){
        return GenericWrapFeatureIterator.wrapToWriter(writer, type);
    }

    /**
     * Wrap an iterator in a stream
     * @param reader
     * @return
     */
    public static <T> Stream<T> asStream(final Iterator<T> reader) {
        final Iterable<T> iterable = () -> reader;
        Stream<T> stream = StreamSupport.stream(iterable.spliterator(), false);
        if (reader instanceof AutoCloseable) {
            stream.onClose(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((AutoCloseable)reader).close();
                    } catch (Exception ex) {
                        Logging.getLogger("org.geotoolkit.data").log(Level.WARNING, null, ex);
                    }
                }
            });
        }

        return stream;
    }

    /**
     * Wrap a FeatureIterator with a modification set.
     *
     * @param reader source reader
     * @param filter filter used to indicate which features will be updated
     * @param values new properties values
     * @return updated iterator
     */
    public static FeatureIterator update(final FeatureIterator reader, final Filter filter, final Map<String, ?> values){
        return GenericModifyFeatureIterator.wrap(reader, filter, values);
    }

    /**
     * Create a view collection which iterates over all children collection.
     *
     * @param id new collection identifier
     * @param collections children collections
     * @return view of all collection
     */
    public static FeatureCollection sequence(final String id, final FeatureCollection... collections) {
        return new FeatureCollectionSequence(id, collections);
    }

    /**
     * Create a view iterator which iterates over all children iterators.
     *
     * @param iterators children iterators
     * @return view of all iterators
     */
    public static FeatureIterator sequence(final FeatureIterator ... iterators){
        return new FeatureIteratorSequence(iterators);
    }

    /**
     * Create a view iterator which iterates over all children readers.
     *
     * @param readers children readers
     * @return view of all readers.
     */
    public static FeatureReader sequence(final FeatureReader ... readers){
        return new FeatureReaderSequence(readers);
    }

    /**
     * Combine several FeatureIterator in one and merge them using the sort by orders.
     * All given iterator must already be sorted.
     *
     * @param sorts : sorting orders
     * @param iterators : iterators to combine
     * @return FeatureIterator combining all others
     */
    public static FeatureIterator combine(final SortBy[] sorts, final FeatureIterator ... iterators){
        return combine(new SortByComparator(sorts), iterators);
    }

    /**
     * Combine several FeatureIterator in one and merge them using the comparator given.
     * All given iterators must already be sorted.
     *
     * @param comparator : comparator
     * @param iterators : iterators to combine
     * @return FeatureIterator combining all others
     */
    public static FeatureIterator combine(final Comparator<Feature> comparator, final FeatureIterator ... iterators){
        if (iterators == null || iterators.length==0) {
            throw new IllegalArgumentException("There must be at least 2 iterators.");
        } else if(iterators.length == 1){
            //do nothing, return the only iterator
            return iterators[0];
        }

        ensureNonNull("comparator", comparator);

        FeatureIterator ite = iterators[0];

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
     * @module
     */
    private static class FeatureCollectionSequence extends AbstractFeatureCollection {

        private final FeatureCollection[] wrapped;

        private FeatureCollectionSequence(final String id, final FeatureCollection[] wrapped) {
            super(id, wrapped[0].getSource());

            if(wrapped.length == 1){
                throw new IllegalArgumentException("Sequence of featureCollection must have at least 2 collections.");
            }

            //check all collection types are the same
            final FeatureType type = wrapped[0].getType();
            for (int i=1;i<wrapped.length;i++) {
                if (!wrapped[i].getType().equals(type)) {
                    throw new IllegalArgumentException("Collections must have the same type.");
                }
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
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return new SequenceIterator(hints);
        }

        @Override
        public Envelope getEnvelope() throws DataStoreException {
            CoordinateReferenceSystem crs = null;
            if (wrapped.length > 0) {
                crs = wrapped[0].getEnvelope().getCoordinateReferenceSystem();
            }
            GeneralEnvelope bbox = null;

            for (FeatureCollection c : wrapped) {
                Envelope e = c.getEnvelope();

                if (e != null) {
                    if (bbox != null) {
                        bbox.add(e);
                    } else {
                      bbox = new GeneralEnvelope(e);
                    }
                }
            }
            return bbox;
        }

        public static FeatureCollection sequence(final FeatureCollection... cols) {
            return new FeatureCollectionSequence("collection-1", cols);
        }

        public static FeatureCollection sequence(final String id, final FeatureCollection... cols) {
            return new FeatureCollectionSequence(id, cols);
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void update(final Filter filter, final Map values) throws DataStoreException {
            for(FeatureCollection c : wrapped){
                c.update(filter, values);
            }
        }

        @Override
        public void remove(final Filter filter) throws DataStoreException {
            for(FeatureCollection c : wrapped){
                c.remove(filter);
            }
        }

        @Override
        public Session getSession() {
            return null;
        }

        @Override
        public FeatureType getType() {
            return wrapped[0].getType();
        }

        @Override
        public FeatureCollection subset(final Query query) throws DataStoreException {
            FeatureCollection[] subs = new FeatureCollection[wrapped.length];
            for(int i=0;i<subs.length;i++){
                subs[i] = wrapped[i].subset(query);
            }
            return new FeatureCollectionSequence("subid", subs);
        }

        private class SequenceIterator implements FeatureIterator {

            private final Hints hints;
            private int currentCollection = -1;
            private FeatureIterator ite = null;

            public SequenceIterator(final Hints hints) {
                this.hints = hints;
                currentCollection = 0;
                ite = wrapped[currentCollection].iterator(hints);
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
                    ite = wrapped[currentCollection].iterator(hints);

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
     * @module
     */
    private static class FeatureIteratorSequence implements FeatureIterator {

        private final FeatureIterator[] wrapped;
        private int currentIndex = 0;
        private FeatureIterator active = null;

        private FeatureIteratorSequence(final FeatureIterator[] wrapped) {
            if(wrapped == null || wrapped.length == 0 || wrapped[0] == null){
                throw new IllegalArgumentException("Iterators can not be empty or null");
            }
            this.wrapped = wrapped;
            active = wrapped[0];
        }

        @Override
        public Feature next() {
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
                //Do not close it, featurestore often use locks, so the thread who created
                //the iterator must close it, but the iteration might be done by another.
                //active.close();
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
     * @module
     */
    private static class FeatureReaderSequence implements FeatureReader {

        private final FeatureReader[] wrapped;
        private int currentIndex = 0;
        private FeatureReader active = null;

        private FeatureReaderSequence(final FeatureReader[] wrapped) {
            if(wrapped == null || wrapped.length == 0 || wrapped[0] == null){
                throw new IllegalArgumentException("Readers can not be empty or null");
            }
            this.wrapped = wrapped;
            active = wrapped[0];
        }

        @Override
        public Feature next() {
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
        public FeatureType getFeatureType() {
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
    private static class FeatureIteratorCombine implements FeatureIterator{

        private final FeatureIterator ite1;
        private final FeatureIterator ite2;
        private final Comparator<? super Feature> comparator;
        private FeatureIterator active = null;
        private Feature ite1next = null;
        private Feature ite2next = null;
        private Feature next = null;

        private FeatureIteratorCombine(final Comparator<? super Feature> comparator, final FeatureIterator ite1, final FeatureIterator ite2){
            ensureNonNull("iterator1", ite1);
            ensureNonNull("iterator2", ite2);
            if(comparator == null ){
                throw new IllegalArgumentException("comparator can not be null. use sequence if you have no comparator.");
            }

            this.comparator = comparator;
            this.ite1 = ite1;
            this.ite2 = ite2;
        }

        @Override
        public Feature next() {
            if(next == null){
                hasNext();
            }

            if(next == null){
                throw new NoSuchElementException("No more elements.");
            }else{
                Feature candidate = next;
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
