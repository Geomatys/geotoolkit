/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.SortByComparator;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.collection.CloseableIterator;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataUtilities {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data");

    private DataUtilities() {
    }

    public static FeatureCollection collection(final Feature ... features){
        final FeatureCollection col = collection("", features[0].getType());
        col.addAll(Arrays.asList(features));
        return col;
    }

    /**
     * Convinient method to create a featurecollection from a collection of features.
     * @param type
     * @param features
     * @return FeatureCollection
     */
    public static FeatureCollection collection(final FeatureType type, final Collection<? extends Feature> features){
        final FeatureCollection col = collection("", type);
        col.addAll(features);
        return col;
    }

    public static FeatureCollection collection(final String id, FeatureType type){
        if(type == null){
            //a collection with no defined type, make a generic abstract type
            //that is possible since feature collection may not always have a type.
            FeatureTypeBuilder sftb = new FeatureTypeBuilder();
            sftb.setName("null");
            sftb.setAbstract(true);
            type = sftb.buildSimpleFeatureType();
        }

        final MemoryDataStore ds = new MemoryDataStore(type, true);
        final Session session = ds.createSession(false);

        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(type.getName()));
        ((AbstractFeatureCollection)col).setId(id);

        return col;
    }

    /**
     * Copy the features from the first collection to the second.
     * This method takes care of correctly closing interators if source collection
     * is a FeatureCollection.
     * @param source : source collection.
     * @param target : collection to copy features into.
     */
    public static Collection fill(final Collection source, final Collection target){
        if(target instanceof FeatureCollection){
            //we can safely use the addAll method.
            target.addAll(source);
        }else{
            //we are not sure that the given collection will take care of closing
            //the underlying iterator, we better do the iteration ourself.
            final Iterator ite = source.iterator();
            try{
                while(ite.hasNext()){
                    final Object f = ite.next();
                    target.add(f);
                }
            }finally{
                //todo must close safely both iterator
                if(ite instanceof Closeable){
                    try {
                        ((Closeable) ite).close();
                    } catch (IOException ex) {
                        throw new DataStoreRuntimeException(ex);
                    }
                }
            }
        }
        return target;
    }

    /**
     * Write the features from the given collection and return the list of generated FeatureID
     * send by the writer.
     * 
     * @param writer
     * @param collection
     * @return List of generated FeatureId
     * @throws DataStoreRuntimeException
     */
    public static List<FeatureId> write(final FeatureWriter writer, final Collection<? extends Feature> collection)
            throws DataStoreRuntimeException{
        final List<FeatureId> ids = new ArrayList<FeatureId>();

        final Iterator<? extends Feature> ite = collection.iterator();
        try{
            while(ite.hasNext()){
                final Feature f = ite.next();
                final Feature candidate = writer.next();

                for(Property property : f.getProperties()){
                    candidate.getProperty(property.getName()).setValue(property.getValue());
                }
                writer.write();
                ids.add(candidate.getIdentifier());
            }
        }finally{
            
            writer.close();
            
            DataStoreRuntimeException e = null;
            //todo must close safely both iterator
            if(ite instanceof Closeable){
                try {
                    ((Closeable) ite).close();
                } catch (Exception ex) {
                    e = new DataStoreRuntimeException(ex);
                }
            }

            
            if(e != null){
                throw e;
            }
        }

        return ids;
    }

    /**
     * Iterate on the given iterator and calculate count.
     * @throws DataStoreRuntimeException
     */
    public static long calculateCount(final CloseableIterator reader) throws DataStoreRuntimeException{
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
    public static Envelope calculateEnvelope(final FeatureIterator iterator) throws DataStoreRuntimeException{
        ensureNonNull("iterator", iterator);
        
        BoundingBox env = null;

        try{
            while(iterator.hasNext()){
                final Feature f = iterator.next();
                final BoundingBox bbox = f.getBounds();
                if(bbox != null){
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

    public static FeatureCollection sequence(final String id, final FeatureCollection... collections) {
        return new FeatureCollectionSequence(id, collections);
    }

    public static <F extends Feature> FeatureIterator<F> sequence(final FeatureIterator<F> ... iterators){
        return new FeatureIteratorSequence<F>(iterators);
    }
    
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> sequence(final FeatureReader<T,F> ... readers){
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
    public static <F extends Feature> FeatureIterator<F> combine(final SortBy[] sorts, final FeatureIterator<F> ... iterators){
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
    public static <F extends Feature> FeatureIterator<F> combine(final Comparator<? super F> comparator, final FeatureIterator<F> ... iterators){
        if(iterators == null || iterators.length < 2 || (iterators.length == 1 && iterators[0] == null)){
            throw new IllegalArgumentException("There must be at least 2 non null iterators.");
        }

        ensureNonNull("comparator", comparator);

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

        private FeatureCollectionSequence(final String id, final FeatureCollection[] wrapped) {
            super(id, wrapped[0].getSource());

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
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
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
        public FeatureType getFeatureType() {
            return wrapped[0].getFeatureType();
        }

        @Override
        public FeatureCollection subCollection(final Query query) throws DataStoreException {
            FeatureCollection[] subs = new FeatureCollection[wrapped.length];
            for(int i=0;i<subs.length;i++){
                subs[i] = wrapped[i].subCollection(query);
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
     * @module pending
     */
    private static class FeatureIteratorSequence<F extends Feature> implements FeatureIterator<F> {

        private final FeatureIterator<F>[] wrapped;
        private int currentIndex = 0;
        private FeatureIterator<F> active = null;

        private FeatureIteratorSequence(final FeatureIterator<F>[] wrapped) {
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

        private FeatureReaderSequence(final FeatureReader<T,F>[] wrapped) {
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

        private FeatureIteratorCombine(final Comparator<? super Feature> comparator, final FeatureIterator<F> ite1, final FeatureIterator<F> ite2){
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
