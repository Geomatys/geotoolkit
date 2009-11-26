/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.FilteringFeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.store.ContentEntry;
import org.geotoolkit.data.store.ContentFeatureStore;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;


/**
 * FeatureStore implementation for jdbc based relational database tables.
 * <p>
 * All read only methods are delegated to {@link JDBCFeatureSource}.
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public final class JDBCFeatureStore extends ContentFeatureStore {
    /**
     * jdbc feature source to delegate to, we do this b/c we can't inherit from
     * both ContentFeatureStore and JDBCFeatureSource at the same time
     */
    JDBCFeatureSource delegate;

    /**
     * Creates the new feature store.
     * @param entry The datastore entry.
     * @param query The defining query.
     */
    public JDBCFeatureStore(final ContentEntry entry, final Query query) throws IOException {
        super(entry,query);

        delegate = new JDBCFeatureSource(entry, query) {
            @Override
            public void setTransaction(Transaction transaction) {
                super.setTransaction(transaction);

                //keep this feature store in sync
                JDBCFeatureStore.this.setTransaction(transaction);
            }
        };

    	final Set<Hints.Key> jdbcHints = new HashSet<Hints.Key>();
    	jdbcHints.addAll(hints);
    	getDataStore().getSQLDialect().addSupportedHints(jdbcHints);
    	hints=Collections.unmodifiableSet(jdbcHints);
    }

    @Override
    public JDBCDataStore getDataStore() {
        return delegate.getDataStore();
    }

    @Override
    public ContentEntry getEntry() {
        return delegate.getEntry();
    }

    @Override
    public Name getName() {
        return delegate.getName();
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return delegate.getQueryCapabilities();
    }

    @Override
    public JDBCState getState() {
        return delegate.getState();
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void setTransaction(final Transaction transaction) {
        //JD: note, we need to set both super and delegate transactions.
        super.setTransaction(transaction);

        //JD: this guard ensures that a recursive loop will not form
        if ( delegate.getTransaction() != transaction ) {
            delegate.setTransaction(transaction);
        }
    }

    public PrimaryKey getPrimaryKey() {
        return delegate.getPrimaryKey();
    }

    @Override
    protected SimpleFeatureType buildFeatureType() throws IOException {
        return delegate.buildFeatureType();
    }

    @Override
    protected int getCountInternal(final Query query) throws IOException {
        return delegate.getCount(query);
    }

    @Override
    protected JTSEnvelope2D getBoundsInternal(final Query query) throws IOException {
        return delegate.getBoundsInternal(query);
    }

    @Override
    protected boolean canFilter() {
        return delegate.canFilter();
    }

    @Override
    protected boolean canSort() {
        return delegate.canSort();
    }

    @Override
    protected boolean canRetype() {
        return delegate.canRetype();
    }

    @Override
    protected boolean canLimit() {
        return delegate.canLimit();
    }

    @Override
    protected boolean canOffset() {
        return delegate.canOffset();
    }

    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(
            final Query query) throws IOException
    {
        return delegate.getReaderInternal(query);
    }

//  /**
//  * This method operates by delegating to the
//  * {@link JDBCFeatureCollection#update(AttributeDescriptor[], Object[])}
//  * method provided by the feature collection resulting from
//  * {@link #filtered(ContentState, Filter)}.
//  *
//  * @see FeatureStore#modifyFeatures(AttributeDescriptor[], Object[], Filter)
//  */
// public void modifyFeatures(AttributeDescriptor[] type, Object[] value, Filter filter)
//     throws IOException {
//     if (filter == null) {
//         String msg = "Must specify a filter, must not be null.";
//         throw new IllegalArgumentException(msg);
//     }
//
//     JDBCFeatureCollection features = (JDBCFeatureCollection) filtered(getState(), filter);
//     features.update(type, value);
// }

    @Override
    protected FeatureWriter<SimpleFeatureType, SimpleFeature> getWriterInternal(final Query query,
            final int flags) throws IOException
    {

        if (flags == 0) {
            throw new IllegalArgumentException( "no write flags set" );
        }

        //get connection from current state
        Connection cx = null;

        Filter postFilter;
        //check for update only case
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer;
        try {
            cx = getDataStore().getConnection(getState());
            //check for insert only
            if ( (flags | WRITER_ADD) == WRITER_ADD ) {
                final QueryBuilder builder = new QueryBuilder(query);
                builder.setFilter(Filter.EXCLUDE);
                Query queryNone = builder.buildQuery();
                if ( getDataStore().getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                    PreparedStatement ps = getDataStore().selectSQLPS(getSchema(), queryNone, cx);
                    return new JDBCInsertFeatureWriter( ps, cx, delegate, query.getHints() );
                }
                else {
                    //build up a statement for the content, inserting only so we dont want
                    // the query to return any data ==> Filter.EXCLUDE
                    String sql = getDataStore().selectSQL(getSchema(), queryNone);
                    getDataStore().getLogger().fine(sql);

                    return new JDBCInsertFeatureWriter( sql, cx, delegate, query.getHints() );
                }
            }

            //split the filter
            Filter[] split = delegate.splitFilter(query.getFilter());
            Filter preFilter = split[0];
            postFilter = split[1];

            // build up a statement for the content
            final QueryBuilder builder = new QueryBuilder(query);
            builder.setFilter(preFilter);
            final Query preQuery = builder.buildQuery();
            
            if(getDataStore().getSQLDialect() instanceof PreparedStatementSQLDialect) {
                PreparedStatement ps = getDataStore().selectSQLPS(getSchema(), preQuery, cx);
                if ( (flags | WRITER_UPDATE) == WRITER_UPDATE ) {
                    writer = new JDBCUpdateFeatureWriter(ps, cx, delegate, query.getHints() );
                } else {
                    //update insert case
                    writer = new JDBCUpdateInsertFeatureWriter(ps, cx, delegate, query.getPropertyNames(), query.getHints() );
                }
            } else {
                String sql = getDataStore().selectSQL(getSchema(), preQuery);
                getDataStore().getLogger().fine(sql);

                if ( (flags | WRITER_UPDATE) == WRITER_UPDATE ) {
                    writer = new JDBCUpdateFeatureWriter( sql, cx, delegate, query.getHints() );
                } else {
                    //update insert case
                    writer = new JDBCUpdateInsertFeatureWriter( sql, cx, delegate, query.getHints() );
                }
            }

        }
        catch (SQLException e) {
            // close the connection
            getDataStore().closeSafe(cx);
            // now we can safely rethrow the exception
            throw (IOException) new IOException( ).initCause(e);
        }

        //check for post filter and wrap accordingly
        if ( postFilter != null && postFilter != Filter.INCLUDE ) {
            writer = new FilteringFeatureWriter( writer, postFilter );
        }
        return writer;
    }

    @Override
    public void updateFeatures(final AttributeDescriptor[] types, final Object[] values, final Filter filter)
            throws IOException {

        // we cannot trust attribute definitions coming from outside, they might not
        // have the same metadata the inner ones have. Let's remap them
        final AttributeDescriptor[] innerTypes = new AttributeDescriptor[types.length];
        for (int i = 0; i < types.length; i++) {
            innerTypes[i] = getSchema().getDescriptor(types[i].getLocalName());
            if(innerTypes[i] == null)
                throw new IllegalArgumentException("Unknon attribute " + types[i].getLocalName());
        }

        // split the filter
        final Filter[] splitted = delegate.splitFilter(filter);
        final Filter preFilter = splitted[0];
        final Filter postFilter = splitted[1];

        if (postFilter != null && !Filter.INCLUDE.equals(postFilter)) {
            // we don't have a fast way to perform this update, let's do it the
            // feature by feature way then
            super.updateFeatures(innerTypes, values, filter);
        } else {
            final Connection cx;
            // we want to support a "batch" update, but we need to be weary of locks
            final SimpleFeatureType featureType = getSchema();
            try {
                // let's grab the connection
                cx = getDataStore().getConnection(getState());
                getDataStore().ensureAuthorization(featureType, preFilter, getTransaction(), cx);
            } catch (SQLException e) {
                throw (IOException) new IOException().initCause( e );
            }
            getDataStore().update(getSchema(), innerTypes, values, preFilter, cx);
        }
    }

    @Override
    public void removeFeatures(final Filter filter) throws IOException {
        final Filter[] splitted = delegate.splitFilter(filter);
        final Filter preFilter = splitted[0];
        final Filter postFilter = splitted[1];

        if (postFilter != null && !Filter.INCLUDE.equals(postFilter)) {
            // we don't have a fast way to perform this delete, let's do it the
            // feature by feature way then
            super.removeFeatures(filter);
        } else {
            final Connection cx;
            // we want to support a "batch" delete, but we need to be weary of locks
            final SimpleFeatureType featureType = getSchema();
            try {
                // let's grab the connection
                cx = getDataStore().getConnection(getState());
                getDataStore().ensureAuthorization(featureType, preFilter, getTransaction(), cx);
            } catch (SQLException e) {
                throw (IOException) new IOException().initCause( e );
            }

            getDataStore().delete(featureType, preFilter, cx);
        }
    }
}
