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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FilteringFeatureReader;
import org.geotoolkit.data.Query;
import org.geotoolkit.data.QueryCapabilities;
import org.geotoolkit.data.ReTypeFeatureReader;
import org.geotoolkit.data.Transaction;
import org.geotoolkit.data.store.ContentEntry;
import org.geotoolkit.data.store.ContentFeatureSource;
import static org.geotoolkit.factory.Hints.Key;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.Association;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.visitor.CapabilitiesFilterSplitter;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;


public class JDBCFeatureSource extends ContentFeatureSource {

    /**
     * primary key of the table
     */
    PrimaryKey primaryKey;

    /**
     * Creates the new feature store.
     * @param entry The datastore entry.
     * @param query The defining query.
     */
    public JDBCFeatureSource(final ContentEntry entry, final Query query) throws IOException {
        super(entry, query);
        
        //TODO: cache this
        primaryKey = ((JDBCDataStore) entry.getDataStore()).getPrimaryKey(entry);
    }
    
    @Override
    protected QueryCapabilities buildQueryCapabilities() {
        return new JDBCQueryCapabilities(this);
    }
    
    @Override
    protected void addHints(final Set<Key> hints) {
        // mark the features as detached, that is, the user can directly alter them
        // without altering the state of the datastore
        hints.add(HintsPending.FEATURE_DETACHED);
        getDataStore().getSQLDialect().addSupportedHints(hints);
    }

    /**
     * Type narrow to {@link JDBCDataStore}.
     */
    @Override
    public JDBCDataStore getDataStore() {
        return (JDBCDataStore) super.getDataStore();
    }

    /**
     * Type narrow to {@link JDBCState}.
     */
    @Override
    public JDBCState getState() {
        return (JDBCState) super.getState();
    }

    /**
     * Returns the primary key of the table backed by feature store.
     */
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }
    
    /**
     * Builds the feature type from database metadata.
     */
    @Override
    protected SimpleFeatureType buildFeatureType() throws IOException {
        final SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        final AttributeTypeBuilder ab = new AttributeTypeBuilder();

        //set up the name
        final String tableName = entry.getName().getLocalPart();
        tb.setName(tableName);

        //set the namespace, if not null
        if (entry.getName().getNamespaceURI() != null) {
            tb.setNamespaceURI(entry.getName().getNamespaceURI());
        } else {
            //use the data store
            tb.setNamespaceURI(getDataStore().getNamespaceURI());
        }

        //grab the schema
        final String databaseSchema = getDataStore().getDatabaseSchema();

        //ensure we have a connection
        Connection cx = null;

        //get metadata about columns from database
        try {
            cx = getDataStore().getConnection(getState());
            final DatabaseMetaData metaData = cx.getMetaData();

            /*
             *        <LI><B>COLUMN_NAME</B> String => column name
             *        <LI><B>DATA_TYPE</B> int => SQL type from java.sql.Types
             *        <LI><B>TYPE_NAME</B> String => Data source dependent type name,
             *  for a UDT the type name is fully qualified
             *        <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
             *            types this is the maximum number of characters, for numeric or
             *            decimal types this is precision.
             *        <LI><B>BUFFER_LENGTH</B> is not used.
             *        <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
             *        <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
             *        <LI><B>NULLABLE</B> int => is NULL allowed.
             *      <UL>
             *      <LI> columnNoNulls - might not allow <code>NULL</code> values
             *      <LI> columnNullable - definitely allows <code>NULL</code> values
             *      <LI> columnNullableUnknown - nullability unknown
             *      </UL>
             *         <LI><B>COLUMN_DEF</B> String => default value (may be <code>null</code>)
             *        <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
             *      does not allow NULL values; "YES" means the column might
             *      allow NULL values.  An empty string means nobody knows.
             */
            final ResultSet columns = metaData.getColumns(null, databaseSchema, tableName, "%");

            try {
                final SQLDialect dialect = getDataStore().getSQLDialect();

                while (columns.next()) {
                    String name = columns.getString("COLUMN_NAME");

                    //do not include primary key in the type
                    /*
                     *        <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
                     *        <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
                     *        <LI><B>TABLE_NAME</B> String => table name
                     *        <LI><B>COLUMN_NAME</B> String => column name
                     *        <LI><B>KEY_SEQ</B> short => sequence number within primary key
                     *        <LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
                     */
                    final ResultSet primaryKeys = metaData.getPrimaryKeys(null, databaseSchema, tableName);

                    try {
                        while (primaryKeys.next()) {
                            final String keyName = primaryKeys.getString("COLUMN_NAME");

                            if (name.equals(keyName)) {
                                name = null;

                                break;
                            }
                        }
                    } finally {
                        getDataStore().closeSafe(primaryKeys);
                    }

                    if (name == null) {
                        continue;
                    }

                    //check for association
                    if (getDataStore().isAssociations()) {
                        getDataStore().ensureAssociationTablesExist(cx);

                        //check for an association
                        final Statement st;
                        final ResultSet relationships;
                        if ( getDataStore().getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                            st = getDataStore().selectRelationshipSQLPS(tableName, name, cx);
                            relationships = ((PreparedStatement)st).executeQuery();
                        }
                        else {
                            final String sql = getDataStore().selectRelationshipSQL(tableName, name);
                            getDataStore().getLogger().fine(sql);
                            
                            st = cx.createStatement();
                            relationships = st.executeQuery(sql);
                        }

                       try {
                            if (relationships.next()) {
                                //found, create a special mapping 
                                tb.add(name, Association.class);

                                continue;
                            }
                        } finally {
                            getDataStore().closeSafe(relationships);
                            getDataStore().closeSafe(st);
                        }
                        
                    }

                    //figure out the type mapping

                    //first ask the dialect
                    Class binding = dialect.getMapping(columns, cx);

                    if (binding == null) {
                        //determine from type mappings
                        int dataType = columns.getInt("DATA_TYPE");
                        binding = getDataStore().getMapping(dataType);
                    }

                    if (binding == null) {
                        //determine from type name mappings
                        String typeName = columns.getString("TYPE_NAME");
                        binding = getDataStore().getMapping(typeName);
                    }

                    //if still not found, resort to Object
                    if (binding == null) {
                        getDataStore().getLogger().warning("Could not find mapping for:" + name);
                        binding = Object.class;
                    }

                    //nullability
                    if ( "NO".equalsIgnoreCase( columns.getString( "IS_NULLABLE" ) ) ) {
                        ab.nillable(false);
                        ab.minOccurs(1);
                    }
                    
                    //determine if this attribute is a geometry or not
                    if (Geometry.class.isAssignableFrom(binding)) {
                        //add the attribute as a geometry, try to figure out 
                        // its srid first
                        Integer srid = null;
                        CoordinateReferenceSystem crs = null;
                        try {
                            srid = dialect.getGeometrySRID(databaseSchema, tableName, name, cx);
                            if(srid != null)
                                crs = dialect.createCRS(srid, cx);
                        } catch (SQLException e) {
                            String msg = "Error occured determing srid for " + tableName + "."
                                + name;
                            getDataStore().getLogger().log(Level.WARNING, msg, e);
                        }

                        ab.setBinding(binding);
                        ab.setName(name);
                        ab.setCRS(crs);
                        if(srid != null)
                            ab.addUserData(JDBCDataStore.JDBC_NATIVE_SRID, srid);
                        tb.add(ab.buildDescriptor(name, ab.buildGeometryType()));
                    } else {
                        //add the attribute
                        ab.setName(name);
                        ab.setBinding(binding);
                        tb.add(ab.buildDescriptor(name, ab.buildType()));
                    }
                }

                return tb.buildFeatureType();
            } finally {
                getDataStore().closeSafe(columns);
            }
        } catch (SQLException e) {
            String msg = "Error occurred building feature type";
            throw (IOException) new IOException(msg).initCause(e);
        }
        finally {
            getDataStore().releaseConnection( cx, getState() );
        }
    }

    /**
     * Helper method for splitting a filter.
     */
    Filter[] splitFilter(final Filter original) {
        final Filter[] split = new Filter[2];
        if ( original != null ) {
            //create a filter splitter
            final CapabilitiesFilterSplitter splitter = new CapabilitiesFilterSplitter(getDataStore().getFilterCapabilities(),
                    getSchema(), null);
            original.accept(splitter, null);

            split[0] = splitter.getFilterPre();
            split[1] = splitter.getFilterPost();
        }
        
        final SimplifyingFilterVisitor visitor = new SimplifyingFilterVisitor();
        visitor.setFIDValidator( new PrimaryKeyFIDValidator( this ) );
        split[0] = (Filter) split[0].accept(visitor, null);
        split[1] = (Filter) split[1].accept(visitor, null);
        
        return split;
    }

    @Override
    protected int getCountInternal(final Query query) throws IOException {
        final JDBCDataStore dataStore = getDataStore();

        //split the filter
        final Filter[] split = splitFilter( query.getFilter() );
        final Filter preFilter = split[0];
        final Filter postFilter = split[1];
        
        
            if ((postFilter != null) && (postFilter != Filter.INCLUDE)) {
                try {
                    //calculate manually, dont use datastore optimization
                    getDataStore().getLogger().fine("Calculating size manually");
    
                    int count = 0;
    
                    //grab a reader
                     FeatureReader<SimpleFeatureType, SimpleFeature> reader = getReader( query );
                    try {
                        while (reader.hasNext()) {
                            reader.next();
                            count++;
                        }
                    } finally {
                        reader.close();
                    }
    
                    return count;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                //no post filter, we have a preFilter, or preFilter is null.. 
                // either way we can use the datastore optimization
                final Connection cx;
                try {
                    cx = dataStore.getConnection(getState());
                } catch (SQLException ex) {
                    throw new IOException(ex);
                }
                try {
                    final DefaultQuery q = new DefaultQuery(query);
                    q.setFilter(preFilter);
                    int count = dataStore.getCount(getSchema(), q, cx);
                    // if native support for limit and offset is not implemented, we have to ajust the result
                    if (!dataStore.getSQLDialect().isLimitOffsetSupported()) {
                        if (query.getStartIndex() != null && query.getStartIndex() > 0) {
                            if (query.getStartIndex() > count) {
                                count = 0;
                            } else {
                                count -= query.getStartIndex();
                            }
                        }
                        if (query.getMaxFeatures() > 0 && count > query.getMaxFeatures()) {
                            count = query.getMaxFeatures();
                        }
                    }
                    return count;
                }
                finally {
                    dataStore.releaseConnection(cx, getState());
                }
            } 
        
    }
    
    @Override
    protected JTSEnvelope2D getBoundsInternal(final Query query) throws IOException {
        final JDBCDataStore dataStore = getDataStore();

        //split the filter
        final Filter[] split = splitFilter(query.getFilter());
        final Filter preFilter = split[0];
        final Filter postFilter = split[1];

        if ((postFilter != null) && (postFilter != Filter.INCLUDE) || (query.getMaxFeatures() < Integer.MAX_VALUE && !canLimit())
                                     || (query.getStartIndex() != null && query.getStartIndex() > 0 && !canOffset()))
        {
            //calculate manually, don't use datastore optimization
            getDataStore().getLogger().fine("Calculating bounds manually");

            // grab the 2d part of the crs
            final CoordinateReferenceSystem flatCRS = CRS.getHorizontalCRS(getSchema().getCoordinateReferenceSystem());
            final JTSEnvelope2D bounds = new JTSEnvelope2D(flatCRS);

            // grab a reader
            final DefaultQuery q = new DefaultQuery(query);
            q.setFilter(postFilter);
            final FeatureReader<SimpleFeatureType, SimpleFeature> i = getReader(q);
            try {
                if (i.hasNext()) {
                    SimpleFeature f = (SimpleFeature) i.next();
                    bounds.init(f.getBounds());

                    while (i.hasNext()) {
                        f = i.next();
                        bounds.include(f.getBounds());
                    }
                }
            } finally {
                i.close();
            }

            return bounds;
        } else {
            //post filter was null... pre can be set or null... either way
            // use datastore optimization
            final Connection cx;
            try {
                cx = dataStore.getConnection(getState());
            } catch (SQLException ex) {
                throw new IOException(ex);
            }
            try {
                final DefaultQuery q = new DefaultQuery(query);
                q.setFilter(preFilter);
                return dataStore.getBounds(getSchema(), q, cx);
            } finally {
                getDataStore().releaseConnection(cx, getState());
            }
        }
    }
    
    @Override
    protected boolean canFilter() {
        return true;
    }
    
    @Override
    protected boolean canSort() {
        return true;
    }
    
    @Override
    protected boolean canRetype() {
        return true;
    }
    
    @Override
    protected boolean canLimit() {
        return getDataStore().getSQLDialect().isLimitOffsetSupported();
    }
    
    @Override
    protected boolean canOffset() {
        return getDataStore().getSQLDialect().isLimitOffsetSupported();
    }
    
    @Override
    protected  FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(Query query) throws IOException {
        // split the filter
        Filter[] split = splitFilter(query.getFilter());
        Filter preFilter = split[0];
        Filter postFilter = split[1];
        
        // rebuild a new query with the same params, but just the pre-filter
        DefaultQuery preQuery = new DefaultQuery(query);
        preQuery.setFilter(preFilter);
        
        // Build the feature type returned by this query. Also build an eventual extra feature type
        // containing the attributes we might need in order to evaluate the post filter
        SimpleFeatureType querySchema;
        SimpleFeatureType returnedSchema;
        if(query.getPropertyNames() == Query.ALL_NAMES) {
            returnedSchema = querySchema = getSchema();
        } else {
            returnedSchema = SimpleFeatureTypeBuilder.retype(getSchema(), query.getPropertyNames());
            FilterAttributeExtractor extractor = new FilterAttributeExtractor(getSchema());
            postFilter.accept(extractor, null);
            String[] extraAttributes = extractor.getAttributeNames();
            if(extraAttributes == null || extraAttributes.length == 0) {
                querySchema = returnedSchema;
            } else {
                List<String> allAttributes = new ArrayList<String>(Arrays.asList(query.getPropertyNames())); 
                for (String extraAttribute : extraAttributes) {
                    if(!allAttributes.contains(extraAttribute))
                        allAttributes.add(extraAttribute);
                }
                String[] allAttributeArray =  (String[]) allAttributes.toArray(new String[allAttributes.size()]);
                querySchema = SimpleFeatureTypeBuilder.retype(getSchema(), allAttributeArray);
            }
        }
        
        //grab connection
        final Connection cx;
        try {
            cx = getDataStore().getConnection(getState());
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        
        //create the reader
        FeatureReader<SimpleFeatureType, SimpleFeature> reader;
        
        try {
            // this allows PostGIS to page the results and respect the fetch size
            if (getState().getTransaction() == Transaction.AUTO_COMMIT) {
                cx.setAutoCommit(false);
            }
            
            final SQLDialect dialect = getDataStore().getSQLDialect();
            if (dialect instanceof PreparedStatementSQLDialect) {
                final PreparedStatement ps = getDataStore().selectSQLPS(querySchema, preQuery, cx);
                reader = new JDBCFeatureReader(ps, cx, this, querySchema, query.getHints());
            } else {
                //build up a statement for the content
                final String sql = getDataStore().selectSQL(querySchema, preQuery);
                getDataStore().getLogger().fine(sql);
    
                reader = new JDBCFeatureReader( sql, cx, this, querySchema, query.getHints() );
            }
        } catch (SQLException e) {
            // close the connection 
            getDataStore().closeSafe(cx);
            // safely rethrow
            throw (IOException) new IOException().initCause(e);
        }
        

        // if post filter, wrap it
        if (postFilter != null && postFilter != Filter.INCLUDE) {
            reader = new FilteringFeatureReader<SimpleFeatureType, SimpleFeature>(reader,postFilter);
            if(!returnedSchema.equals(querySchema))
                reader = new ReTypeFeatureReader(reader, returnedSchema);
        }

        return reader;
    }

}
