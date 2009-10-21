/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.util.NoSuchElementException;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A FeatureWriter that captures modifications against a FeatureReader.
 * 
 * <p>
 * You will eventually need to write out the differences, later.
 * </p>
 * 
 * <p>
 * The api has been implemented in terms of  FeatureReader<SimpleFeatureType, SimpleFeature> to make explicit that
 * no Features are writen out by this Class.
 * </p>
 *
 * @author Jody Garnett, Refractions Research
 *
 * @see TransactionStateDiff
 * @source $URL$
 * @module pending
 */
public abstract class DiffFeatureWriter implements FeatureWriter<SimpleFeatureType, SimpleFeature> {

    private final FeatureReader<SimpleFeatureType, SimpleFeature> reader;
    //keep it package visible for test cases
    final Diff diff;
    private SimpleFeature next; // next value aquired by hasNext()
    private SimpleFeature live; // live value supplied by FeatureReader
    private SimpleFeature current; // duplicate provided to user

    /**
     * DiffFeatureWriter construction.
     *
     * @param reader
     * @param diff
     */
    public DiffFeatureWriter(final FeatureReader<SimpleFeatureType, SimpleFeature> reader, final Diff diff) {
        this(reader, diff, Filter.INCLUDE);
    }

    /**
     * DiffFeatureWriter construction.
     *
     * @param reader
     * @param diff
     * @param filter
     */
    public DiffFeatureWriter(final FeatureReader<SimpleFeatureType, SimpleFeature> reader,
            final Diff diff, final Filter filter) {
        this.reader = new DiffFeatureReader<SimpleFeatureType, SimpleFeature>(reader, diff, filter);
        this.diff = diff;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getFeatureType() {
        return reader.getFeatureType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature next() throws IOException {
        final SimpleFeatureType type = getFeatureType();
        if (hasNext()) {
            // hasNext() will take care recording
            // any modifications to current
            try {
                live = next; // update live value
                next = null; // hasNext will need to search again            
                current = SimpleFeatureBuilder.copy(live);

                return current;
            } catch (IllegalAttributeException e) {
                throw (IOException) new IOException("Could not modify content").initCause(e);
            }
        } else {
            // Create new content
            // created with an empty ID
            // (The real writer will supply a FID later) 
            try {
                live = null;
                next = null;
                current = SimpleFeatureBuilder.build(type, new Object[type.getAttributeCount()],
                        "new" + diff.nextFID);
                diff.nextFID++;
                return current;
            } catch (IllegalAttributeException e) {
                throw new IOException("Could not create new content");
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() throws IOException {
        if (live != null) {
            // mark live as removed
            diff.remove(live.getID());
            fireNotification(FeatureEvent.Type.REMOVED, JTSEnvelope2D.reference(live.getBounds()));
            live = null;
            current = null;
        } else if (current != null) {
            // cancel additional content
            current = null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write() throws IOException {
        //DJB: I modified this so it doesnt throw an error if you
        //     do an update and you didnt actually change anything.
        //     (We do the work)
        if ((live != null)) {
            // We have a modification to record!
            diff.update(live.getID(), current);

            final JTSEnvelope2D bounds = new JTSEnvelope2D((CoordinateReferenceSystem) null);
            bounds.include(live.getBounds());
            bounds.include(current.getBounds());
            fireNotification(FeatureEvent.Type.CHANGED, bounds);
            live = null;
            current = null;
        } else if ((live == null) && (current != null)) {
            // We have new content to record
            //
            diff.add(current.getID(), current);
            fireNotification(FeatureEvent.Type.ADDED, JTSEnvelope2D.reference(current.getBounds()));
            current = null;
        } else {
            throw new IOException("No feature available to write");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws IOException {
        if (next != null) {
            // we found next already
            return true;
        }

        live = null;
        current = null;

        if (reader.hasNext()) {
            try {
                next = reader.next();
            } catch (NoSuchElementException e) {
                throw new DataSourceException("No more content", e);
            } catch (IllegalAttributeException e) {
                throw new DataSourceException("No more content", e);
            }

            return true;
        }

        return false;
    }

    /**
     * Clean up resources associated with this writer.
     * 
     * <p>
     * Diff is not clear()ed as it is assumed that it belongs to a
     * Transaction.State object and may yet be written out.
     * </p>
     *
     * @see org.geotoolkit.data.FeatureWriter#close()
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }

        current = null;
        live = null;
        next = null;
    }

    /**
     * Subclass must provide the notification.
     * 
     * <p>
     * Notification requirements for modifications against a Transaction should
     * only be issued to FeatureSource<SimpleFeatureType, SimpleFeature> instances that opperate against the
     * same typeName and Transaction.
     * </p>
     * 
     * <p>
     * Other FeatureSource<SimpleFeatureType, SimpleFeature> instances with the same typeName will be notified
     * when the Transaction is committed.
     * </p>
     *
     * @param eventType One of FeatureType.FEATURES_ADDED, FeatureType.CHANGED,
     *        FeatureType.FEATURES_REMOVED
     * @param bounds
     */
    protected abstract void fireNotification(FeatureEvent.Type eventType, JTSEnvelope2D bounds);
}
