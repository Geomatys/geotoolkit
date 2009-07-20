/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.collection.DelegateFeatureReader;
import org.geotoolkit.feature.collection.CollectionEvent;
import org.geotoolkit.feature.collection.CollectionListener;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.collection.DelegateFeatureIterator;
import org.geotoolkit.feature.collection.SubFeatureCollection;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.util.NullProgressListener;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;

/**
 * A starting point for implementing FeatureCollection's backed onto a FeatureReader.
 * <p>
 * This implementation requires you to implement the following:
 * <ul>
 * <li>getSchema() - this should match reader.getSchema()
 * <li>reader()</br>
 *     features() - override one of these two method to access content
 * <li>getBounds()
 * <li>getCount()
 * <li>collection()
 * </p>
 * <p>
 * This class will implement the 'extra' methods required by FeatureCollection
 * for you (in simple terms based on the FeatureResults API). Anything that is
 * <i>often</i> customised is available to you as a constructor parameters.
 * <p>
 * Enjoy.
 * </p>
 * @author jgarnett
 * @since 2.1.RC0
 * @source $URL$
 */
public abstract class DataFeatureCollection implements FeatureCollection<SimpleFeatureType, SimpleFeature> {

    /** logger */
    static Logger LOGGER = org.geotoolkit.util.logging.Logging.getLogger("org.geotoolkit.data.store");
    static private int unique = 0;

    /**
     * Collection based on a generic collection
     */
    protected DataFeatureCollection() {
        this("features" + (unique++));
    }

    /**
     * Collection based on a generic collection
     */
    protected DataFeatureCollection(final String id) {
        this(id, null);
    }

    /** Subclass must think about what consitructors it needs. */
    protected DataFeatureCollection(final String id, final SimpleFeatureType memberType) {
        this.id = (id == null) ? "featureCollection" : id;
        this.schema = memberType;
    }

    /**
     * To let listeners know that something has changed.
     */
    protected void fireChange(final SimpleFeature[] features, final int type) {
        final CollectionEvent cEvent = new CollectionEvent(this, features, type);

        for (int i = 0, ii = listeners.size(); i < ii; i++) {
            ((CollectionListener) listeners.get(i)).collectionChanged(cEvent);
        }
    }

    protected void fireChange(final SimpleFeature feature, final int type) {
        fireChange(new SimpleFeature[]{feature}, type);
    }

    protected void fireChange(final Collection coll, final int type) {
        SimpleFeature[] features = new SimpleFeature[coll.size()];
        features = (SimpleFeature[]) coll.toArray(features);
        fireChange(features, type);
    }

    public FeatureReader<SimpleFeatureType, SimpleFeature> reader() throws IOException {
        return new DelegateFeatureReader<SimpleFeatureType, SimpleFeature>(getSchema(), features());
    }

    //
    // Feature Results methods
    //
    // To be implemented by subclass
    //
    @Override
    public abstract JTSEnvelope2D getBounds();

    public abstract int getCount() throws IOException;

    //public abstract FeatureCollection<SimpleFeatureType, SimpleFeature> collection() throws IOException;
    //
    // Additional Subclass "hooks"
    //
    /**
     * Subclass may provide an implementation of this method to indicate
     * that read/write support is provided.
     * <p>
     * All operations that attempt to modify the "data" will
     * use this method, allowing them to throw an "UnsupportedOperationException"
     * in the same manner as Collections.unmodifiableCollection(Collection c)
     * </p>
     * @throws UnsupportedOperationException To indicate that write support is not avaiable
     */
    protected FeatureWriter<SimpleFeatureType, SimpleFeature> writer() throws IOException {
        throw new UnsupportedOperationException("Modification of this collection is not supported");
    }
    //
    // FeatureCollection<SimpleFeatureType, SimpleFeature> methods
    //
    // implemented in terms of feature results
    //
    //
    // Content Access
    //
    /** Set of open resource iterators & featureIterators */
    private final Set open = new HashSet();
    /**
     * listeners
     */
    protected final List listeners = new ArrayList();
    /**
     * id used when serialized to gml
     */
    protected String id;
    protected SimpleFeatureType schema;

    /**
     * FeatureIterator<SimpleFeature> is entirely based on iterator().
     * <p>
     * So when we implement FeatureCollection.iterator() this will work
     * out of the box.
     */
    @Override
    public FeatureIterator<SimpleFeature> features() {
        final FeatureIterator<SimpleFeature> iterator = new DelegateFeatureIterator<SimpleFeature>(this, iterator());
        open.add(iterator);
        return iterator;
    }

    /**
     * Iterator may (or may) not support modification.
     */
    @Override
    final public Iterator<SimpleFeature> iterator() {
        Iterator<SimpleFeature> iterator;
        try {
            iterator = openIterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        open.add(iterator);
        return iterator;
    }

    /**
     * Returns a FeatureWriterIterator, or FeatureReaderIterator over content.
     * <p>
     * If you have a way to tell that you are readonly please subclass with
     * a less hardcore check - this implementations catches a
     * UnsupportedOpperationsException from wrtier()!
     *
     * @return Iterator, should be closed closeIterator
     */
    protected Iterator<SimpleFeature> openIterator() throws IOException {
        try {
            return new FeatureWriterIterator(writer());
        } catch (IOException badWriter) {
            return new NoContentIterator(badWriter);
        } catch (UnsupportedOperationException readOnly) {
        }
        try {
            return new FeatureReaderIterator<SimpleFeature>(reader());
        } catch (IOException e) {
            return new NoContentIterator(e);
        }
    }

    @Override
    final public void close(final Iterator<SimpleFeature> close) {
        try {
            closeIterator(close);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing iterator", e);
        }
        open.remove(close);
    }

    protected void closeIterator(final Iterator<SimpleFeature> close) throws IOException {
        if (close == null) {
            // iterator probably failed during consturction !
        } else if (close instanceof FeatureReaderIterator) {
            FeatureReaderIterator<SimpleFeature> iterator = (FeatureReaderIterator<SimpleFeature>) close;
            iterator.close(); // only needs package visability
        } else if (close instanceof FeatureWriterIterator) {
            FeatureWriterIterator iterator = (FeatureWriterIterator) close;
            iterator.close(); // only needs package visability
        }
    }

    @Override
    public void close(final FeatureIterator<SimpleFeature> iterator) {
        iterator.close();
        open.remove(iterator);
    }

    /** Default implementation based on getCount() - this may be expensive */
    @Override
    public int size() {
        try {
            return getCount();
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "IOException while calculating size() of FeatureCollection", e);
            }
            return 0;
        }
    }

    //
    // Off into implementation land!
    //
    /**
     * Default implementation based on creating an reader, testing hasNext, and closing.
     * <p>
     * For once the Collections API does not give us an escape route, we *have* to check the data.
     * </p>
     */
    @Override
    public boolean isEmpty() {
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = null;
        try {
            reader = reader();
            try {
                return !reader.hasNext();
            } catch (IOException e) {
                return true; // error seems like no features are available
            }
        } catch (IOException e) {
            return true;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // return value already set
                }
            }
        }
    }

    @Override
    public boolean contains(final Object o) {
        if (!(o instanceof SimpleFeature)) {
            return false;
        }
        final SimpleFeature value = (SimpleFeature) o;
        final String ID = value.getID();

        FeatureReader<SimpleFeatureType, SimpleFeature> reader = null;
        try {
            reader = reader();
            try {
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    if (!ID.equals(feature.getID())) {
                        continue; // skip with out full equal check
                    }
                    if (value.equals(feature)) {
                        return true;
                    }
                }
                return false; // not found
            } catch (IOException e) {
                return false; // error seems like no features are available
            } catch (NoSuchElementException e) {
                return false; // error seems like no features are available
            } catch (IllegalAttributeException e) {
                return false; // error seems like no features are available
            }
        } catch (IOException e) {
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // return value already set
                }
            }
        }
    }

    @Override
    public Object[] toArray() {
        return toArray(new SimpleFeature[size()]);
    }

    @Override
    public Object[] toArray(final Object[] array) {
        List list = new ArrayList();
        Iterator i = iterator();
        try {
            while (i.hasNext()) {
                list.add(i.next());
            }
        } finally {
            close(i);
        }
        return list.toArray(array);
    }

    @Override
    public boolean add(final SimpleFeature arg0) {
        return false;
    }

    @Override
    public boolean remove(final Object arg0) {
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        for (Object o : collection) {
            if (contains(o) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Optimized implementation of addAll that recognizes the
     * use of collections obtained with subCollection( filter ).
     * <p>
     * This method is constructed by either:
     * <ul>
     * <li>Filter OR
     * <li>Removing an extact match of Filter AND
     * </ul>
     *
     */
    @Override
    public boolean addAll(final Collection collection) {
        if (collection instanceof FeatureCollection) {
            return addAll((FeatureCollection<?, ?>) collection);
        }
        try {
            final FeatureWriter writer = writer();
            try {
                // skip to end
                while (writer.hasNext()) {
                    final Feature feature = writer.next();
                }
                for (Object obj : collection) {
                    if (obj instanceof SimpleFeature) {
                        final SimpleFeature copy = (SimpleFeature) obj;
                        final SimpleFeature feature = (SimpleFeature) writer.next();

                        feature.setAttributes(copy.getAttributes());
                        writer.write();
                    }
                }
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return true;
        } catch (IOException ignore) {
            return false;
        }
    }

    @Override
    public boolean addAll(final FeatureCollection resource) {
        return false;
    }

    @Override
    public boolean removeAll(final Collection arg0) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection arg0) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public void accepts(final org.opengis.feature.FeatureVisitor visitor, org.opengis.util.ProgressListener progress) {
        Iterator iterator = null;
        if (progress == null) {
            progress = new NullProgressListener();
        }
        try {
            final float size = size();
            float position = 0;
            progress.started();
            for (iterator = iterator(); !progress.isCanceled() && iterator.hasNext(); progress.progress(position++ / size)) {
                try {
                    final SimpleFeature feature = (SimpleFeature) iterator.next();
                    visitor.visit(feature);
                } catch (Exception erp) {
                    progress.exceptionOccurred(erp);
                }
            }
        } finally {
            progress.complete();
            close(iterator);
        }
    }

    /**
     * Will return an optimized subCollection based on access
     * to the origional FeatureSource.
     * <p>
     * The subCollection is constructed by using an AND Filter.
     * For the converse of this opperation please see
     * collection.addAll( Collection ), it has been optimized
     * to be aware of these filter based SubCollections.
     * </p>
     * <p>
     * This method is intended in a manner similar to subList,
     * example use:
     * <code>
     * collection.subCollection( myFilter ).clear()
     * </code>
     * </p>
     * @param filter Filter used to determine sub collection.
     * @since GeoTools 2.2, Filter 1.1
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> subCollection(final Filter filter) {
        if (filter == Filter.INCLUDE) {
            return this;
        }
        return new SubFeatureCollection(this, filter);
    }

    /**
     * Construct a sorted view of this content.
     * <p>
     * Sorts may be combined togther in a stable fashion, in congruence
     * with the Filter 1.1 specification.
     * </p>
     * This method should also be able to handle GeoTools specific
     * sorting through detecting order as a SortBy2 instance.
     *
     * @param order
     *
     * @since GeoTools 2.2, Filter 1.1
     * @return FeatureList sorted according to provided order

     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> sort(final SortBy order) {
        if (order instanceof SortBy) {
            SortBy advanced = (SortBy) order;
            return sort(advanced);
        }
        return null; // new OrderedFeatureList( this, order );
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public final void addListener(final CollectionListener listener) throws NullPointerException {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(final CollectionListener listener)
            throws NullPointerException {
        listeners.remove(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return schema;
    }
}
