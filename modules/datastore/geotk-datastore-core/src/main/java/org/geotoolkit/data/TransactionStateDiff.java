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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.concurrent.Transaction.State;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A Transaction.State that keeps a difference table for use with
 * AbstractDataStore.
 *
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 */
public class TransactionStateDiff implements State {

    /**
     * DataStore used to commit() results of this transaction.
     *
     * @see TransactionStateDiff.commit();
     */
    private AbstractDataStore store;
    /**
     * Tranasction this State is opperating against.
     */
    private Transaction transaction;
    /**
     * Map of differences by typeName.
     * 
     * <p>
     * Differences are stored as a Map of Feature by fid, and are reset during
     * a commit() or rollback().
     * </p>
     */
    private final Map typeNameDiff = new HashMap();

    public TransactionStateDiff(final AbstractDataStore dataStore) {
        store = dataStore;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void setTransaction(final Transaction transaction) {
        if (transaction != null) {
            // configure
            this.transaction = transaction;
        } else {
            this.transaction = null;

            if (typeNameDiff != null) {
                for (Iterator i = typeNameDiff.values().iterator();
                        i.hasNext();) {
                    Diff diff = (Diff) i.next();
                    diff.clear();
                }

                typeNameDiff.clear();
            }

            store = null;
        }
    }

    public synchronized Diff diff(final String typeName) throws IOException {
        if (!exists(typeName)) {
            throw new IOException(typeName + " not defined");
        }

        if (typeNameDiff.containsKey(typeName)) {
            return (Diff) typeNameDiff.get(typeName);
        } else {
            final Diff diff = new Diff();
            typeNameDiff.put(typeName, diff);

            return diff;
        }
    }

    boolean exists(final String typeName) {
        final String[] types;
        try {
            types = store.getTypeNames();
        } catch (IOException e) {
            return false;
        }
        Arrays.sort(types);

        return Arrays.binarySearch(types, typeName) != -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void addAuthorization(final String AuthID)
            throws IOException {
        // not required for TransactionStateDiff
    }

    /**
     * Will apply differences to store.
     *
     * @see org.geotoolkit.data.Transaction.State#commit()
     */
    @Override
    public synchronized void commit() throws IOException {
        Map.Entry entry;

        for (Iterator i = typeNameDiff.entrySet().iterator(); i.hasNext();) {
            entry = (Entry) i.next();

            final String typeName = (String) entry.getKey();
            final Diff diff = (Diff) entry.getValue();
            applyDiff(typeName, diff);
        }
    }

    /**
     * Called by commit() to apply one set of diff
     * 
     * <p>
     * The provided <code> will be modified as the differences are applied,
     * If the operations are all successful diff will be empty at
     * the end of this process.
     * </p>
     * 
     * <p>
     * diff can be used to represent the following operations:
     * </p>
     * 
     * <ul>
     * <li>
     * fid|null: represents a fid being removed
     * </li>
     * 
     * <li>
     * fid|feature: where fid exists, represents feature modification
     * </li>
     * <li>
     * fid|feature: where fid does not exist, represents feature being modified
     * </li>
     * </ul>
     * 
     *
     * @param typeName typeName being updated
     * @param diff differences to apply to FeatureWriter
     *
     * @throws IOException If the entire diff cannot be writen out
     * @throws DataSourceException If the entire diff cannot be writen out
     */
    void applyDiff(final String typeName, final Diff diff) throws IOException {
        if (diff.isEmpty()) {
            return;
        }
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer;
        try {
            writer = store.createFeatureWriter(typeName, transaction);
        } catch (UnsupportedOperationException e) {
            // backwards compatibility
            throw e;
        }
        SimpleFeature feature;
        SimpleFeature update;
        String fid;

        try {
            while (writer.hasNext()) {
                feature = (SimpleFeature) writer.next();
                fid = feature.getID();

                if (diff.modified2.containsKey(fid)) {
                    update = (SimpleFeature) diff.modified2.get(fid);

                    if (update == NULL) {
                        writer.remove();

                        // notify
                        store.listenerManager.fireFeaturesRemoved(typeName,
                                transaction, JTSEnvelope2D.reference(feature.getBounds()), true);
                    } else {
                        try {
                            feature.setAttributes(update.getAttributes());
                            writer.write();

                            // notify                        
                            final JTSEnvelope2D bounds = new JTSEnvelope2D((CoordinateReferenceSystem) null);
                            bounds.include(feature.getBounds());
                            bounds.include(update.getBounds());
                            store.listenerManager.fireFeaturesChanged(typeName,
                                    transaction, bounds, true);
                        } catch (IllegalAttributeException e) {
                            throw new DataSourceException("Could update " + fid,
                                    e);
                        }
                    }
                }
            }

            SimpleFeature addedFeature;
            SimpleFeature nextFeature;

            synchronized (diff) {
                for (Iterator i = diff.added.values().iterator(); i.hasNext();) {
                    addedFeature = (SimpleFeature) i.next();

                    fid = addedFeature.getID();

                    nextFeature = (SimpleFeature) writer.next();

                    if (nextFeature == null) {
                        throw new DataSourceException("Could not add " + fid);
                    } else {
                        try {
                            nextFeature.setAttributes(addedFeature.getAttributes());
                            writer.write();

                            // notify
                            store.listenerManager.fireFeaturesAdded(typeName,
                                    transaction, JTSEnvelope2D.reference(nextFeature.getBounds()), true);
                        } catch (IllegalAttributeException e) {
                            throw new DataSourceException("Could update " + fid,
                                    e);
                        }
                    }
                }
            }
        } finally {
            writer.close();
            store.listenerManager.fireChanged(typeName, transaction, true);
            diff.clear();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void rollback() throws IOException {
        Map.Entry entry;

        for (Iterator i = typeNameDiff.entrySet().iterator(); i.hasNext();) {
            entry = (Entry) i.next();

            final String typeName = (String) entry.getKey();
            final Diff diff = (Diff) entry.getValue();

            diff.clear(); // rollback differences
            store.listenerManager.fireChanged(typeName, transaction, false);
        }
    }

    /**
     * Convience Method for a Transaction based FeatureReader.
     * 
     * <p>
     * Constructs a DiffFeatureReader that works against this Transaction.
     * </p>
     *
     * @param typeName TypeName to aquire a Reader on
     *
     * @return  FeatureReader<SimpleFeatureType, SimpleFeature> the mask orgional contents with against the
     *         current Differences recorded by the Tansasction State
     *
     * @throws IOException If typeName is not Manged by this Tansaction State
     */
    public synchronized FeatureReader<SimpleFeatureType, SimpleFeature> reader(final String typeName)
            throws IOException {
        final Diff diff = diff(typeName);
        final FeatureReader<SimpleFeatureType, SimpleFeature> reader = store.getFeatureReader(typeName);

        return new DiffFeatureReader<SimpleFeatureType, SimpleFeature>(reader, diff);
    }

    /**
     * Convience Method for a Transaction based FeatureWriter
     * 
     * <p>
     * Constructs a DiffFeatureWriter that works against this Transaction.
     * </p>
     *
     * @param typeName Type Name to record differences against
     * @param filter 
     *
     * @return A FeatureWriter that records Differences against a FeatureReader
     *
     * @throws IOException If a FeatureRader could not be constucted to record
     *         differences against
     */
    public synchronized FeatureWriter<SimpleFeatureType, SimpleFeature> writer(final String typeName, final Filter filter)
            throws IOException {
        final Diff diff = diff(typeName);
        final FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                new FilteringFeatureReader<SimpleFeatureType, SimpleFeature>(store.getFeatureReader(typeName, new DefaultQuery(typeName, filter)), filter);

        return new DiffFeatureWriter(reader, diff, filter) {

            @Override
            public void fireNotification(final FeatureEvent.Type eventType, final JTSEnvelope2D bounds) {
                switch (eventType) {
                    case ADDED:
                        store.listenerManager.fireFeaturesAdded(typeName, transaction, bounds, false);
                        break;
                    case CHANGED:
                        store.listenerManager.fireFeaturesChanged(typeName, transaction, bounds, false);
                        break;
                    case REMOVED:
                        store.listenerManager.fireFeaturesRemoved(typeName, transaction, bounds, false);
                        break;
                }
            }

            @Override
            public String toString() {
                return "<DiffFeatureWriter>(" + reader.toString() + ")";
            }
        };
    }
    /**
     * A NullObject used to represent the absence of a SimpleFeature.
     * <p>
     * This class is used by TransactionStateDiff as a placeholder
     * to represent features that have been removed. The concept
     * is generally useful and may wish to be taken out as a separate
     * class (used for example to represent deleted rows in a shapefile).
     */
    public static final SimpleFeature NULL = new SimpleFeature() {

        @Override
        public Object getAttribute(final String path) {
            return null;
        }

        @Override
        public Object getAttribute(final int index) {
            return null;
        }

        public Object[] getAttributes(final Object[] attributes) {
            return null;
        }

        @Override
        public JTSEnvelope2D getBounds() {
            return null;
        }

        @Override
        public Geometry getDefaultGeometry() {
            return null;
        }

        @Override
        public SimpleFeatureType getFeatureType() {
            return null;
        }

        @Override
        public String getID() {
            return null;
        }

        @Override
        public FeatureId getIdentifier() {
            return null;
        }

        public int getNumberOfAttributes() {
            return 0;
        }

        @Override
        public void setAttribute(final int position, final Object val) {
        }

        @Override
        public void setAttribute(final String path, final Object attribute)
                throws IllegalAttributeException {
        }

        public void setDefaultGeometry(final Geometry geometry)
                throws IllegalAttributeException {
        }

        @Override
        public Object getAttribute(final Name name) {
            return null;
        }

        @Override
        public int getAttributeCount() {
            return 0;
        }

        @Override
        public List<Object> getAttributes() {
            return null;
        }

        @Override
        public SimpleFeatureType getType() {
            return null;
        }

        @Override
        public void setAttribute(Name name, Object value) {
        }

        @Override
        public void setAttributes(List<Object> values) {
        }

        @Override
        public void setAttributes(Object[] values) {
        }

        @Override
        public void setDefaultGeometry(Object geometry) {
        }

        @Override
        public GeometryAttribute getDefaultGeometryProperty() {
            return null;
        }

        @Override
        public void setDefaultGeometryProperty(
                GeometryAttribute geometryAttribute) {
        }

        @Override
        public Collection<Property> getProperties(Name name) {
            return null;
        }

        @Override
        public Collection<Property> getProperties() {
            return null;
        }

        @Override
        public Collection<Property> getProperties(String name) {
            return null;
        }

        @Override
        public Property getProperty(Name name) {
            return null;
        }

        @Override
        public Property getProperty(String name) {
            return null;
        }

        @Override
        public Collection<? extends Property> getValue() {
            return null;
        }

        @Override
        public void setValue(Collection<Property> values) {
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return null;
        }

        @Override
        public Name getName() {
            return null;
        }

        @Override
        public Map<Object, Object> getUserData() {
            return null;
        }

        @Override
        public boolean isNillable() {
            return false;
        }

        @Override
        public void setValue(Object newValue) {
        }

        @Override
        public String toString() {
            return "<NullFeature>";
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object arg0) {
            if (!(arg0 instanceof TransactionStateDiff)) {
                return false;
            }
            return this == arg0;
        }

        @Override
        public void validate() {
        }
    };
}
