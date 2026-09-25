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
package org.geotoolkit.storage.feature;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureSetMapper;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.SortByComparator;
import org.geotoolkit.storage.memory.WrapFeatureIterator;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.SortProperty;

/**
 * Feature stream utility functions.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureStreams {

    private FeatureStreams(){}

    public static FeatureSet decorate(FeatureSet fs, FeatureSetMapper mask) {
        return new WrapFeatureSet(fs) {
            @Override
            public FeatureType getType() throws DataStoreException {
                return mask.getMappedType();
            }

            @Override
            protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
                return mask.apply(original);
            }
        };
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
     * Decorate a feature reader.
     *
     * @param reader source reader
     * @param mask FeatureType mask
     * @param hints additional hints
     * @return decorated reader
     */
    public static FeatureReader decorate(final FeatureReader reader, final FeatureSetMapper mask, Hints hints){
        return GenericMappedFeatureIterator.wrap(reader, mask, hints);
    }

    /**
     * Wrap a FeatureReader with a filter.
     * @param reader source reader
     * @param filter filter used to select matching features
     * @return filtered reader
     */
    public static FeatureReader filter(final FeatureReader reader, final Filter filter){
        if (filter==null || Filter.include().equals(filter)) return reader;
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
        if (filter==null || Filter.include().equals(filter)) return writer;
        return GenericFilterFeatureIterator.wrap(writer, filter);
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
     * View a subset of given data.
     *
     * @param reader source reader
     * @param query query used to filter and transform the reader
     * @return feature reader subset
     */
    public static FeatureReader subset(FeatureReader reader, final Query query) throws DataStoreException{
        return GenericQueryFeatureIterator.wrap(reader, query);
    }

    /**
     * Wrap a FeatureIterator will a sort by order.
     *
     * @param iterator source iterator
     * @param orders sorting order
     * @return sorted iterator
     */
    public static FeatureIterator sort(final FeatureIterator iterator, final SortProperty ... orders){
        return GenericSortByFeatureIterator.wrap(iterator, orders);
    }

    /**
     * Wrap a FeatureReader will a sort by order.
     *
     * @param reader source reader
     * @param orders sorting order
     * @return sorted reader
     */
    public static FeatureReader sort(final FeatureReader reader, final SortProperty ... orders){
        return GenericSortByFeatureIterator.wrap(reader, orders);
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
     * Wrap an Iterator as a FeatureIterator.
     *
     * @param iterator source iterator
     * @return feature iterator
     */
    public static FeatureIterator asIterator(final Iterator<? extends Feature> iterator){
        return GenericWrapFeatureIterator.wrapToIterator(iterator);
    }

    /**
     * Map given stream of features as a {@link FeatureIterator feature iterator}.
     *
     * @param features Must not be null.
     * @return An iterator that serves input stream elements. its {@link FeatureIterator#close() closing} operation will
     * implicitely close the input stream.
     */
    public static FeatureIterator asIterator(final Stream<Feature> features) {
        final WrapFeatureIterator it = new WrapFeatureIterator(features.iterator()) {};
        it.onClose(features::close);
        return it;
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
     * Decorate a given data stream.
     *
     * @param stream The stream to expose as a feature reader. Its {@link Stream#close()} operation will be called by
     *               the reader {@link FeatureReader#close()} function.
     * @param datatype type of the features from the stream (they must at least inherit from it). Must not be null.
     */
    public static FeatureReader asReader(final Stream<Feature> stream, final FeatureType datatype) {
        try {
            return asReader(asIterator(stream), datatype);
        } catch (RuntimeException e) {
            try {
                stream.close();
            } catch (Exception bis) {
                e.addSuppressed(bis);
            }

            throw e;
        }
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
            stream = stream.onClose(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((AutoCloseable)reader).close();
                    } catch (Exception ex) {
                        Logger.getLogger("org.geotoolkit.data").log(Level.WARNING, null, ex);
                    }
                }
            });
        }

        return stream;
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
     * Combine several FeatureIterator in one and merge them using the sort by orders.
     * All given iterator must already be sorted.
     *
     * @param sorts : sorting orders
     * @param iterators : iterators to combine
     * @return FeatureIterator combining all others
     */
    public static FeatureIterator combine(final SortProperty[] sorts, final FeatureIterator ... iterators){
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
            if (next == null) {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements.");
                }
            }
            Feature candidate = next;
            next = null;
            return candidate;
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
