/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 Geomatys
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.diff.Diff;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.data.query.QueryBuilder;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * This is a starting point for providing your own FeatureSource<SimpleFeatureType, SimpleFeature> implementation.
 *
 * <p>
 * Subclasses must implement:
 * </p>
 *
 * <ul>
 * <li>
 * getDataStore()
 * </li>
 * <li>
 * getSchema()
 * </li>
 * <li>
 * addFeatureListener()
 * </li>
 * <li>
 * removeFeatureListener()
 * </li>
 * </ul>
 *
 * <p>
 * You may find a FeatureSource<SimpleFeatureType, SimpleFeature> implementations that is more specific to your needs - such as
 * JDBCFeatureSource.
 * </p>
 *
 * <p>
 * For an example of this class customized for use please see MemoryDataStore.
 * </p>
 *
 * @author Jody Garnett, Refractions Research Inc
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureSource implements FeatureSource<SimpleFeatureType, SimpleFeature> {

    protected final Set hints;
    protected QueryCapabilities queryCapabilities = new QueryCapabilities();

    public AbstractFeatureSource() {
        this(null);
    }

    /**
     * This constructors allows to set the supported hints
     * @param hints
     */
    public AbstractFeatureSource(final Set hints) {
        if(hints == null){
            this.hints = Collections.EMPTY_SET;
        }else{
            this.hints = Collections.unmodifiableSet(new HashSet(hints));
        }

    }

    /**
     * Overrides to explicitly type narrow the return type to {@link DataStore}
     */
    @Override
    public abstract DataStore getDataStore();

    /**
     * Returns the same name than the feature type (ie,
     * {@code getSchema().getName()} to honor the simple feature land common
     * practice of calling the same both the Features produces and their types
     * 
     * @since 2.5
     * @see FeatureSource#getName()
     */
    @Override
    public Name getName() {
        return getSchema().getName();
    }

    /**
     * By default, no Hints are supported
     */
    @Override
    public Set getSupportedHints() {
        return hints;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return queryCapabilities;
    }

    /**
     * Retrieve the Transaction this FeatureSource<SimpleFeatureType, SimpleFeature> is operating against.
     *
     * <p>
     * For a plain FeatureSource<SimpleFeatureType, SimpleFeature> that cannot modify this will always be Transaction.AUTO_COMMIT.
     * </p>
     *
     * @return Transacstion FeatureSource<SimpleFeatureType, SimpleFeature> is operating against
     */
    public Transaction getTransaction() {
        return Transaction.AUTO_COMMIT;
    }

    /**
     * Ensure query modified with typeName.
     * <p>
     * This method will make copy of the provided query, using
     * DefaultQuery, if query.getTypeName is not equal to
     * getSchema().getTypeName().
     * </p>
     * @param query Original query
     * @return Query with getTypeName() equal to getSchema().getTypeName()
     */
    protected Query namedQuery(final Query query) {
        final String typeName = getSchema().getTypeName();
        final String candidate = query.getTypeName().getLocalPart();
        if (!typeName.equals(candidate)) {
            return new QueryBuilder()
                    .setTypeName(getSchema().getName())
                    .setFilter(query.getFilter())
                    .setMaxFeatures(query.getMaxFeatures())
                    .setProperties(query.getPropertyNames())
                    .setHandle(query.getHandle())
                    .buildQuery();
        }
        return query;
    }

    /**
     * Retrieve all Features.
     *
     * @return FeatureResults of all Features in FeatureSource
     *
     * @throws IOException If features could not be obtained
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() throws IOException {
        return getFeatures(Filter.INCLUDE);
    }

    /**
     * Retrieve all Feature matching the Filter.
     *
     * @param filter Indicates features to retrieve
     *
     * @return FeatureResults indicating features matching filter
     *
     * @throws IOException If results could not be obtained
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(final Filter filter) throws IOException {
        return getFeatures(QueryBuilder.filtered(getSchema().getName(), filter));
    }

    /**
     * Provides an interface to for the Results of a Query.
     *
     * <p>
     * Various queries can be made against the results, the most basic being to retrieve Features.
     * </p>
     *
     * @param query
     *
     * @see FeatureSource#getFeatures(org.geotoolkit.data.query.Query)
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(Query query) throws IOException {
        final SimpleFeatureType schema = getSchema();
        final Name typeName = schema.getName();

        if (query.getTypeName() == null) {
            //typeName unspecified we will "any" use a default
            query = new QueryBuilder().copy(query).setTypeName(typeName).buildQuery();
        } else if (!typeName.equals(query.getTypeName())) {
            throw new IOException("Query type name : "+ query.getTypeName() +"doesn't match schema name : "+typeName);
        }

        final QueryCapabilities queryCapabilities = getQueryCapabilities();
        if (!queryCapabilities.supportsSorting(query.getSortBy())) {
            throw new DataSourceException("DataStore cannot provide the requested sort order");
        }

        return new DefaultFeatureResults(this, query);
    }

    /**
     * Retrieve Bounds of all Features.
     *
     * <p>
     * Currently returns null, consider getFeatures().getBounds() instead.
     * </p>
     *
     * <p>
     * Subclasses may override this method to perform the appropriate optimization for this result.
     * </p>
     *
     * @return null representing the lack of an optimization
     *
     * @throws IOException DOCUMENT ME!
     */
    @Override
    public JTSEnvelope2D getBounds() throws IOException {
        return getBounds(QueryBuilder.all(getSchema().getName()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSEnvelope2D getBounds(final Filter filter) throws IOException {
        return getBounds(QueryBuilder.filtered(getSchema().getName(), filter));
    }

    /**
     * Retrieve Bounds of Query results.
     *
     * <p>
     * Currently returns null, consider getFeatures( query ).getBounds() instead.
     * </p>
     *
     * <p>
     * Subclasses may override this method to perform the appropriate optimization for this result.
     * </p>
     *
     * @param query Query we are requesting the bounds of
     *
     * @return null representing the lack of an optimization
     *
     * @throws IOException DOCUMENT ME!
     */
    @Override
    public JTSEnvelope2D getBounds(final Query query) throws IOException {
        if (query.getFilter() == Filter.EXCLUDE) {
            return new JTSEnvelope2D(new Envelope(), getSchema().getCoordinateReferenceSystem());
        }

        final DataStore dataStore = getDataStore();

        if ((dataStore == null) || !(dataStore instanceof AbstractDataStore)) {
            // too expensive
            return null;
        } else {
            // ask the abstract data store
            return ((AbstractDataStore) dataStore).getBounds(namedQuery(query));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getCount() throws IOException {
        return getCount(Filter.INCLUDE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getCount(final Filter filter) throws IOException {
        return getCount(QueryBuilder.filtered(getSchema().getName(), filter));
    }

    /**
     * Retrieve total number of Query results.
     *
     * <p>
     * Currently returns -1, consider getFeatures( query ).getCount() instead.
     * </p>
     *
     * <p>
     * Subclasses may override this method to perform the appropriate optimization for this result.
     * </p>
     *
     * @param query Query we are requesting the count of
     *
     * @return -1 representing the lack of an optimization
     */
    @Override
    public int getCount(final Query query) throws IOException {
        if (query.getFilter() == Filter.EXCLUDE) {
            return 0;
        }

        final DataStore dataStore = getDataStore();
        if ((dataStore == null) || !(dataStore instanceof AbstractDataStore)) {
            // too expensive
            return -1;
        }
        // ask the abstract data store
        final Transaction t = getTransaction();

        final int nativeCount = ((AbstractDataStore) dataStore).getCount(namedQuery(query));
        if (nativeCount == -1) {
            return -1;
        }

        //State state = t.getState(dataStore);
        int delta = 0;
        if (t != Transaction.AUTO_COMMIT) {
            if (t.getState(dataStore) == null) {
                return nativeCount;
            }

            if (!(t.getState(dataStore) instanceof TransactionStateDiff)) {
                //we cannot proceed; abort!
                return -1;
            }
            final Diff diff = ((AbstractDataStore) dataStore).state(t).diff(namedQuery(query).getTypeName().getLocalPart());
            synchronized (diff) {
                Iterator it = diff.added.values().iterator();
                while (it.hasNext()) {
                    final Object feature = it.next();
                    if (query.getFilter().evaluate(feature)) {
                        delta++;
                    }
                }

                it = diff.modified2.values().iterator();
                while (it.hasNext()) {
                    final Object feature = it.next();

                    if (feature == TransactionStateDiff.NULL && query.getFilter().evaluate(feature)) {
                        delta--;
                    }
                }
            }
        }

        return nativeCount + delta;
    }

}
