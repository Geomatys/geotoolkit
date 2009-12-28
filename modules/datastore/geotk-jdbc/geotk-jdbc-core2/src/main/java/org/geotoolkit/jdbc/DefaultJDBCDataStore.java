/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) Geomatys
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

import org.geotoolkit.jdbc.dialect.PreparedStatementSQLDialect;
import org.geotoolkit.jdbc.dialect.SQLDialect;
import org.geotoolkit.jdbc.dialect.BasicSQLDialect;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.data.jdbc.FilterToSQLException;
import org.geotoolkit.data.jdbc.datasource.ManageableDataSource;
import org.geotoolkit.data.jdbc.fidmapper.FIDMapper;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.visitor.CapabilitiesFilterSplitter;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Converters;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public final class DefaultJDBCDataStore extends AbstractJDBCDataStore {

    private final Map<Name,FeatureType> names = new HashMap<Name, FeatureType>();
    private final Map<FeatureType,PrimaryKey> primaryKeys = new HashMap<FeatureType, PrimaryKey>();
    private Set<Name> nameCache = null;

    DefaultJDBCDataStore(String namespace){
        super(namespace);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PrimaryKey getPrimaryKey(FeatureType type) throws DataStoreException{
        if(nameCache == null){
            visitTables();
        }
        return primaryKeys.get(type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        Set<Name> ref = nameCache;
        if(ref == null){
            visitTables();
            ref = Collections.unmodifiableSet(new HashSet<Name>(names.keySet()));
            nameCache = ref;
        }
        return ref;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema(Name typeName) throws DataStoreException {
        typeCheck(typeName);
        return names.get(typeName);
    }

    /**
     * Explore the available tables and generate schemas and primary keys.
     * @throws DataStoreException
     */
    private void visitTables() throws DataStoreException{
        nameCache = null;
        names.clear();
        primaryKeys.clear();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        final SimpleFeatureType type = (SimpleFeatureType) getSchema(query.getTypeName());

        // split the filter
        Filter[] split = splitFilter(query.getFilter(),type);
        Filter preFilter = split[0];
        Filter postFilter = split[1];

        // rebuild a new query with the same params, but just the pre-filter
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(preFilter);
        final Query preQuery = builder.buildQuery();

        // Build the feature type returned by this query. Also build an eventual extra feature type
        // containing the attributes we might need in order to evaluate the post filter
        SimpleFeatureType querySchema;
        SimpleFeatureType returnedSchema;
        if(query.retrieveAllProperties()) {
            returnedSchema = querySchema = type;
        } else {
            returnedSchema = SimpleFeatureTypeBuilder.retype(type, query.getPropertyNames());
            FilterAttributeExtractor extractor = new FilterAttributeExtractor(type);
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
                querySchema = SimpleFeatureTypeBuilder.retype(type, allAttributeArray);
            }
        }

        //grab connection
        final Connection cx;
        try {
            cx = createConnection();
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        }

        //create the reader
        FeatureReader<SimpleFeatureType, SimpleFeature> reader;

        try {
            // this allows PostGIS to page the results and respect the fetch size
            // if (getState().getTransaction() == Transaction.AUTO_COMMIT)
            cx.setAutoCommit(false);

            final SQLDialect dialect = getDialect();
            if (dialect instanceof PreparedStatementSQLDialect) {
                final PreparedStatement ps = selectSQLPS(querySchema, preQuery, cx);
                reader = new JDBCFeatureReader(ps, cx, this, query.getTypeName(), querySchema, query.getHints());
            } else {
                //build up a statement for the content
                final String sql = selectSQL(querySchema, preQuery);
                getLogger().fine(sql);

                reader = new JDBCFeatureReader( sql, cx, this, query.getTypeName(), querySchema, query.getHints() );
            }
        } catch (SQLException e) {
            // close the connection
            closeSafe(cx);
            // safely rethrow
            throw (DataStoreException) new DataStoreException().initCause(e);
        } catch (IOException e) {
            // close the connection
            closeSafe(cx);
            // safely rethrow
            throw (DataStoreException) new DataStoreException().initCause(e);
        }


        // if post filter, wrap it
        if (postFilter != null && postFilter != Filter.INCLUDE) {
            reader = GenericFilterFeatureIterator.wrap(reader, postFilter);
            if(!returnedSchema.equals(querySchema))
                reader = GenericRetypeFeatureIterator.wrap(reader, returnedSchema);
        }

        return reader;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        final SimpleFeatureType type = (SimpleFeatureType) getSchema(query.getTypeName());

        //split the filter
        final Filter[] split = splitFilter( query.getFilter(),type );
        final Filter preFilter = split[0];
        final Filter postFilter = split[1];


        if ((postFilter != null) && (postFilter != Filter.INCLUDE)) {
            try {
                //calculate manually, dont use datastore optimization
                getLogger().fine("Calculating size manually");

                int count = 0;

                //grab a reader
                FeatureReader<SimpleFeatureType, SimpleFeature> reader = getFeatureReader(query);
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
                cx = createConnection();
            } catch (SQLException ex) {
                throw new DataStoreException(ex);
            }
            try {
                final QueryBuilder builder = new QueryBuilder(query);
                builder.setFilter(preFilter);
                final Query q = builder.buildQuery();
                int count = getCount(type, q, cx);
                // if native support for limit and offset is not implemented, we have to ajust the result
                if (!getDialect().isLimitOffsetSupported()) {
                    if (query.getStartIndex() > 0) {
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
            } finally {
                closeSafe(cx);
            }
        }
    }

    /**
     * Returns the count of the features for a particular feature type / table.
     * Rely on the database count capabilities.
     */
    private int getCount(final SimpleFeatureType featureType, final Query query, final Connection cx)
            throws DataStoreException {

        final Statement st;
        final ResultSet rs;
        try {
            if (dialect instanceof PreparedStatementSQLDialect) {
                st = selectCountSQLPS(featureType, query, cx);
                rs = ((PreparedStatement) st).executeQuery();
            } else {
                final String sql = selectCountSQL(featureType, query);
                getLogger().log(Level.FINE, "Counting features: {0}", sql);

                st = cx.createStatement();
                rs = st.executeQuery(sql);
            }

            try {
                rs.next();
                return rs.getInt(1);
            } finally {
                closeSafe(rs);
                closeSafe(st);
            }
        } catch (SQLException e) {
            throw (DataStoreException) new DataStoreException("Error occured calculating count").initCause(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, DataStoreRuntimeException {
        typeCheck(query.getTypeName());
        final SimpleFeatureType type = (SimpleFeatureType) getSchema(query.getTypeName());

        //split the filter
        final Filter[] split = splitFilter(query.getFilter(),type);
        final Filter preFilter = split[0];
        final Filter postFilter = split[1];

        final boolean canLimitOffset = dialect.isLimitOffsetSupported();

        if ((postFilter != null) && (postFilter != Filter.INCLUDE) || (query.getMaxFeatures() != null && !canLimitOffset)
                                     || (query.getStartIndex() > 0 && !canLimitOffset)) {
            //calculate manually, don't use datastore optimization
            getLogger().fine("Calculating bounds manually");

            // grab the 2d part of the crs
            final CoordinateReferenceSystem flatCRS = CRS.getHorizontalCRS(type.getCoordinateReferenceSystem());
            final JTSEnvelope2D bounds = new JTSEnvelope2D(flatCRS);

            // grab a reader
            final QueryBuilder builder = new QueryBuilder(query);
            builder.setFilter(postFilter);
            final Query q = builder.buildQuery();
            final FeatureReader<SimpleFeatureType, SimpleFeature> i = getFeatureReader(q);
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
                cx = createConnection();
            } catch (SQLException ex) {
                throw new DataStoreException(ex);
            }
            try {
                final QueryBuilder builder = new QueryBuilder(query);
                builder.setFilter(preFilter);
                final Query q = builder.buildQuery();
                return getEnvelope(type, q, cx);
            } finally {
                closeSafe(cx);
            }
        }
    }

    /**
     * Returns the bounds of the features for a particular feature type / table.
     * Rely on tha database to obtain envelope.
     *
     * @param featureType The feature type / table.
     * @param query Specifies rows to include in bounds calculation, as well as how many
     *              features and the offset if needed
     */
    private Envelope getEnvelope(final SimpleFeatureType featureType, final Query query,
            final Connection cx) throws DataStoreException {

        // handle geometryless case by returning an empty envelope
        if (featureType.getGeometryDescriptor() == null) {
            return EMPTY_ENVELOPE;
        }

        final Statement st;
        final ResultSet rs;
        try {
            if (dialect instanceof PreparedStatementSQLDialect) {
                st = selectBoundsSQLPS(featureType, query, cx);
                rs = ((PreparedStatement) st).executeQuery();
            } else {
                final String sql = selectBoundsSQL(featureType, query);
                getLogger().log(Level.FINE, "Retriving bounding box: {0}", sql);

                st = cx.createStatement();
                rs = st.executeQuery(sql);
            }

            try {
                final JTSEnvelope2D bounds;
                com.vividsolutions.jts.geom.Envelope e = null;
                if (rs.next()) {
                    try {
                        e = dialect.decodeGeometryEnvelope(rs, 1, st.getConnection());
                    } catch (IOException ex) {
                        throw new DataStoreException(ex);
                    }
                }

                if (e == null) {
                    e = new com.vividsolutions.jts.geom.Envelope();
                    e.init(0, 0, 0, 0);
                    //e.setToNull();
                }

                if (e instanceof JTSEnvelope2D) {
                    bounds = (JTSEnvelope2D) e;
                } else {
                    //set the crs to be the crs of the feature type
                    // grab the 2d part of the crs
                    final CoordinateReferenceSystem flatCRS = CRS.getHorizontalCRS(
                            featureType.getCoordinateReferenceSystem());

                    if (e != null) {
                        bounds = new JTSEnvelope2D(e, flatCRS);
                    } else {
                        bounds = new JTSEnvelope2D(flatCRS);
                        bounds.setToNull();
                    }
                }

                //keep going to handle case where envelope is not calculated
                // as aggregate function
                if (e.isNull() == false) { // featuretype not empty
                    while (rs.next()) {
                        try {
                            bounds.expandToInclude(dialect.decodeGeometryEnvelope(rs, 1, st.getConnection()));
                        } catch (IOException ex) {
                            throw new DataStoreException(ex);
                        }
                    }
                }

                return bounds;
            } finally {
                closeSafe(rs);
                closeSafe(st);
            }
        } catch (SQLException e) {
            throw (DataStoreException) new DataStoreException("Error occured calculating bounds").initCause(e);
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(!(featureType instanceof SimpleFeatureType)){
            throw new DataStoreException("JDBC datastore can handle only simple feature types.");
        }

        if(!featureType.getName().equals(typeName)){
            throw new DataStoreException("JDBC datastore can only hold typename same as feature type name.");
        }

        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }


        //execute the create table statement
        //TODO: create a primary key and a spatial index
        Connection cx = null;

        try {
            cx = createConnection();
            final String sql = createTableSQL((SimpleFeatureType) featureType,cx);
            getLogger().log(Level.FINE, "Create schema: {0}", sql);

            final Statement st = cx.createStatement();

            try {
                st.execute(sql);
            } finally {
                closeSafe(st);
            }

            dialect.postCreateTable(databaseSchema, (SimpleFeatureType)featureType, cx);
        } catch (Exception e) {
            throw (DataStoreException) new DataStoreException("Error occurred creating table").initCause(e);
        } finally {
            closeSafe(cx);
        }

        // reset the type name cache, will be recreated when needed.
        nameCache = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Connection utils ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new connection.
     * Callers of this method should close the connection when done with it.
     */
    protected final Connection createConnection() throws SQLException {
        getLogger().fine("CREATE CONNECTION");
        final Connection cx = getDataSource().getConnection();
        // isolation level is not set in the datastore, see
        // http://jira.codehaus.org/browse/GEOT-2021

        //call dialect callback to iniitalie the connection
        dialect.initializeConnection(cx);
        return cx;
    }

    /**
     * Utility method for closing a result set.
     * <p>
     * This method closed the result set "safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param rs The result set to close.
     */
    public void closeSafe(final ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            String msg = "Error occurred closing result set";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * Utility method for closing a statement.
     * <p>
     * This method closed the statement"safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param st The statement to close.
     */
    public void closeSafe(final Statement st) {
        if (st == null) {
            return;
        }

        try {
            st.close();
        } catch (SQLException e) {
            String msg = "Error occurred closing statement";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * Utility method for closing a connection.
     * <p>
     * This method closed the connection "safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param cx The connection to close.
     */
    public void closeSafe(final Connection cx) {
        if (cx == null) {
            return;
        }

        try {
            cx.close();
            getLogger().fine("CLOSE CONNECTION");
        } catch (SQLException e) {
            String msg = "Error occurred closing connection";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // SQL utils ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * {@inheritDoc }
     */
    @Override
    public FilterToSQL createFilterToSQL(final FeatureType featureType) {
        return initializeFilterToSQL(((BasicSQLDialect) dialect).createFilterToSQL(), (SimpleFeatureType)featureType);
    }

    /**
     * Creates a new instance of a filter to sql encoder to be
     * used in a prepared statement.
     */
    protected PreparedFilterToSQL createPreparedFilterToSQL(final SimpleFeatureType featureType) {
        return initializeFilterToSQL(((PreparedStatementSQLDialect) dialect).createPreparedFilterToSQL(), featureType);
    }

    /**
     * Helper method to initialize a filter encoder instance.
     */
    protected <F extends FilterToSQL> F initializeFilterToSQL(final F toSQL, final SimpleFeatureType featureType) {
        toSQL.setSqlNameEscape(dialect.getNameEscape());

        if (featureType != null) {
            //set up a fid mapper
            //TODO: remove this
            final PrimaryKey key;

            try {
                key = getPrimaryKey(featureType);
            } catch (DataStoreException e) {
                throw new RuntimeException(e);
            }

            FIDMapper mapper = new FIDMapper() {

                @Override
                public String createID(Connection conn, SimpleFeature feature, Statement statement)
                        throws IOException {
                    return null;
                }

                @Override
                public int getColumnCount() {
                    return key.getColumns().size();
                }

                @Override
                public int getColumnDecimalDigits(int colIndex) {
                    return 0;
                }

                @Override
                public String getColumnName(int colIndex) {
                    return key.getColumns().get(colIndex).getName();
                }

                @Override
                public int getColumnSize(int colIndex) {
                    return 0;
                }

                @Override
                public int getColumnType(int colIndex) {
                    return 0;
                }

                @Override
                public String getID(Object[] attributes) {
                    return null;
                }

                @Override
                public Object[] getPKAttributes(String FID) throws IOException {
                    return decodeFID(key, FID, false).toArray();
                }

                @Override
                public boolean hasAutoIncrementColumns() {
                    return false;
                }

                @Override
                public void initSupportStructures() {
                }

                @Override
                public boolean isAutoIncrement(int colIndex) {
                    return false;
                }

                @Override
                public boolean isVolatile() {
                    return false;
                }

                @Override
                public boolean returnFIDColumnsAsAttributes() {
                    return false;
                }

                @Override
                public boolean isValid(String fid) {
                    return true;
                }
            };
            toSQL.setFeatureType(featureType);
            toSQL.setFIDMapper(mapper);
        }

        return toSQL;
    }

    /**
     * Generates a 'CREATE TABLE' sql statement.
     */
    protected String createTableSQL(final SimpleFeatureType featureType, final Connection cx) throws SQLException {
        //figure out the names and types of the columns
        final int size = featureType.getAttributeCount();
        final String[] columnNames = new String[size];
        final Class[] classes = new Class[size];
        final boolean[] nillable = new boolean[size];

        for (int i=0; i<size; i++) {
            final AttributeDescriptor attributeType = featureType.getDescriptor(i);
            columnNames[i] = attributeType.getLocalName();
            classes[i] = attributeType.getType().getBinding();
            nillable[i] = attributeType.getMinOccurs() <= 0 || attributeType.isNillable();
        }

        final String[] sqlTypeNames = getSQLTypeNames(classes, cx);

        for (int i=0; i<sqlTypeNames.length; i++) {
            if (sqlTypeNames[i] == null) {
                throw new SQLException("Unable to map " + columnNames[i] + "( " + classes[i].getName() + ")");
            }
        }

        return createTableSQL(featureType.getTypeName(), columnNames, sqlTypeNames, nillable, "fid", featureType);
    }

    /**
     * Helper method for building a 'CREATE TABLE' sql statement.
     */
    private String createTableSQL(final String tableName, final String[] columnNames, final String[] sqlTypeNames,
            boolean[] nillable, String pkeyColumn, SimpleFeatureType featureType) {
        //build the create table sql
        final StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");

        encodeTableName(tableName, sql);
        sql.append(" ( ");

        //primary key column
        if (pkeyColumn != null) {
            dialect.encodePrimaryKey(pkeyColumn, sql);
            sql.append(", ");
        }

        //normal attributes
        for (int i = 0; i < columnNames.length; i++) {
            //the column name
            dialect.encodeColumnName(columnNames[i], sql);
            sql.append(' ');

            //sql type name
            //JD: some sql dialects require strings / varchars to have an
            // associated size with them
            if (sqlTypeNames[i].toUpperCase().startsWith("VARCHAR")) {
                Integer length = null;
                if (featureType != null) {
                    AttributeDescriptor att = featureType.getDescriptor(columnNames[i]);
                    length = findVarcharColumnLength(att);
                }
                if (length == null || length < 0) {
                    length = 255;
                }

                dialect.encodeColumnType(sqlTypeNames[i] + '(' + length + ')', sql);
            } else {
                dialect.encodeColumnType(sqlTypeNames[i], sql);
            }

            //nullable
            if (nillable != null && !nillable[i]) {
                sql.append(" NOT NULL ");
            }

            //delegate to dialect to encode column postamble
            if (featureType != null) {
                AttributeDescriptor att = featureType.getDescriptor(columnNames[i]);
                dialect.encodePostColumnCreateTable(att, sql);
            }

            //sql.append(sqlTypeNames[i]);
            if (i < (sqlTypeNames.length - 1)) {
                sql.append(", ");
            }
        }

        sql.append(" ) ");

        //encode anything post create table
        dialect.encodePostCreateTable(tableName, sql);

        return sql.toString();
    }

    /**
     * Helper method to encode table name which checks if a schema is set and
     * prefixes the table name with it.
     */
    protected void encodeTableName(final String tableName, final StringBuilder sql) {
        if (databaseSchema != null) {
            dialect.encodeSchemaName(databaseSchema, sql);
            sql.append('.');
        }

        dialect.encodeTableName(tableName, sql);
    }


    /**
     * Generates a 'SELECT p1, p2, ... FROM ... WHERE ...' statement.
     *
     * @param featureType
     *            the feature type that the query must return (may contain less
     *            attributes than the native one)
     * @param query
     *            the query to be run. The type name and property will be ignored, as they are
     *            supposed to have been already embedded into the provided feature type
     * @return String
     */
    protected String selectSQL(final SimpleFeatureType featureType, final Query query) throws IOException,DataStoreException {
        final StringBuilder sql = new StringBuilder("SELECT ");

        //column names

        //primary key
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            dialect.encodeColumnName(col.getName(), sql);
            sql.append(',');
        }

        //other columns
        for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
            if (att instanceof GeometryDescriptor) {
                //encode as geometry
                encodeGeometryColumn((GeometryDescriptor) att, sql, query.getHints());

                //alias it to be the name of the original geometry
                dialect.encodeColumnAlias(att.getLocalName(), sql);
            } else {
                dialect.encodeColumnName(att.getLocalName(), sql);
            }

            sql.append(',');
        }

        sql.setLength(sql.length() - 1);

        sql.append(" FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        //filtering
        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                // grab the full feature type, as we might be encoding a filter
                // that uses attributes that aren't returned in the results
                final SimpleFeatureType fullSchema = (SimpleFeatureType) getSchema(featureType.getTypeName());
                final FilterToSQL toSQL = createFilterToSQL(fullSchema);
                sql.append(' ').append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new IOException(e);
            }
        }

        //sorting
        sort(featureType, query.getSortBy(), key, sql);

        // finally encode limit/offset, if necessary
        applyLimitOffset(sql, query);

        return sql.toString();
    }


    /**
     * Generates a 'SELECT count(*) FROM' sql statement. In case limit/offset is
     * used, we'll need to apply them on a <code>select *<code>
     * as limit/offset usually alters the number of returned rows
     * (and a count returns just one), and then count on the result of that first select
     */
    protected String selectCountSQL(final SimpleFeatureType featureType, final Query query) throws SQLException {
        final StringBuilder sql = new StringBuilder();

        final boolean limitOffset = checkLimitOffset(query);
        if (limitOffset) {
            sql.append("SELECT * FROM ");
        } else {
            sql.append("SELECT count(*) FROM ");
        }
        encodeTableName(featureType.getTypeName(), sql);

        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                final FilterToSQL toSQL = createFilterToSQL(featureType);
                sql.append(' ').append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        if (limitOffset) {
            applyLimitOffset(sql, query);
            sql.insert(0, "SELECT COUNT(*) FROM (");
            sql.append(") AS GT_COUNT_ ");

        }

        return sql.toString();
    }

    /**
     * Generates a 'SELECT count(*) FROM' prepared statement.
     */
    protected PreparedStatement selectCountSQLPS(final SimpleFeatureType featureType, final Query query,
                                                 final Connection cx) throws SQLException{
        final StringBuilder sql = new StringBuilder();

        final boolean limitOffset = checkLimitOffset(query);
        if (limitOffset) {
            sql.append("SELECT * FROM ");
        } else {
            sql.append("SELECT count(*) FROM ");
        }
        encodeTableName(featureType.getTypeName(), sql);

        PreparedFilterToSQL toSQL = null;
        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                toSQL = createPreparedFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        if (limitOffset) {
            applyLimitOffset(sql, query);
            sql.insert(0, "SELECT COUNT(*) FROM (");
            sql.append(")");
            dialect.encodeTableAlias("GT_COUNT_", sql);
        }

        getLogger().fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }


    /**
     * Encodes the sort-by portion of an sql query
     * @param featureType
     * @param sort
     * @param key
     * @param sql
     * @throws IOException
     */
    void sort(final SimpleFeatureType featureType, final SortBy[] sort, final PrimaryKey key,
            final StringBuilder sql) throws IOException {
        if ((sort != null) && (sort.length > 0)) {
            sql.append(" ORDER BY ");

            for (final SortBy sortBy : sort) {
                final String order;
                if (sortBy.getSortOrder() == SortOrder.DESCENDING) {
                    order = " DESC";
                } else {
                    order = " ASC";
                }

                if (SortBy.NATURAL_ORDER.equals(sortBy) || SortBy.REVERSE_ORDER.equals(sortBy)) {
                    if (key instanceof NullPrimaryKey) {
                        throw new IOException("Cannot do natural order without a primary key");
                    }

                    for (PrimaryKeyColumn col : key.getColumns()) {
                        dialect.encodeColumnName(col.getName(), sql);
                        sql.append(order);
                        sql.append(',');
                    }
                } else {
                    dialect.encodeColumnName(getPropertyName(featureType, sortBy.getPropertyName()), sql);
                    sql.append(order);
                    sql.append(',');
                }
            }

            sql.setLength(sql.length() - 1);
        }
    }

    /**
     * Generates a 'SELECT p1, p2, ... FROM ... WHERE ...' prepared statement.
     *
     * @param featureType
     *            the feature type that the query must return (may contain less
     *            attributes than the native one)
     * @param query
     *            the query to be run. The type name and property will be ignored, as they are
     *            supposed to have been already embedded into the provided feature type
     * @param cx
     *            The database connection to be used to create the prepared
     *            statement
     */
    protected PreparedStatement selectSQLPS(final SimpleFeatureType featureType, final Query query,
                                            final Connection cx) throws SQLException, IOException, DataStoreException
    {

        final StringBuilder sql = new StringBuilder("SELECT ");

        // primary key
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            dialect.encodeColumnName(col.getName(), sql);
            sql.append(',');
        }

        //other columns
        for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
            if (att instanceof GeometryDescriptor) {
                //encode as geometry
                encodeGeometryColumn((GeometryDescriptor) att, sql, query.getHints());

                //alias it to be the name of the original geometry
                dialect.encodeColumnAlias(att.getLocalName(), sql);
            } else {
                dialect.encodeColumnName(att.getLocalName(), sql);
            }

            sql.append(',');
        }

        sql.setLength(sql.length() - 1);

        sql.append(" FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        //filtering
        PreparedFilterToSQL toSQL = null;
        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                // grab the full feature type, as we might be encoding a filter
                // that uses attributes that aren't returned in the results
                final SimpleFeatureType fullSchema = (SimpleFeatureType) getSchema(featureType.getTypeName());
                toSQL = createPreparedFilterToSQL(fullSchema);
                sql.append(' ').append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        //sorting
        sort(featureType, query.getSortBy(), key, sql);

        // finally encode limit/offset, if necessary
        applyLimitOffset(sql, query);

        getLogger().fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY,
                                                                         ResultSet.CONCUR_READ_ONLY);
        ps.setFetchSize(fetchSize);

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }


    /**
     * Generates a 'SELECT' sql statement which selects bounds.
     *
     * @param featureType The feature type / table.
     * @param query Specifies which features are to be used for the bounds computation
     *              (and in particular uses filter, start index and max features)
     */
    protected String selectBoundsSQL(final SimpleFeatureType featureType, final Query query) throws SQLException {
        final StringBuilder sql = new StringBuilder();

        final boolean offsetLimit = checkLimitOffset(query);
        if (offsetLimit) {
            // envelopes are aggregates, just like count, so we must first isolate
            // the rows against which the aggregate will work in a subquery
            sql.append(" SELECT *");
        } else {
            sql.append("SELECT ");
            buildEnvelopeAggregates(featureType, sql);
        }

        sql.append(" FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                final FilterToSQL toSQL = createFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        // finally encode limit/offset, if necessary
        if (offsetLimit) {
            applyLimitOffset(sql, query);
            // build the prologue
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            buildEnvelopeAggregates(featureType, sb);
            sb.append("FROM (");
            // wrap the existing query
            sql.insert(0, sb.toString());
            sql.append(")");
            dialect.encodeTableAlias("GT2_BOUNDS_", sql);
        }

        return sql.toString();
    }

    /**
     * Generates a 'SELECT' prepared statement which selects bounds.
     *
     * @param featureType The feature type / table.
     * @param query Specifies which features are to be used for the bounds computation
     *              (and in particular uses filter, start index and max features)
     * @param cx A database connection.
     */
    protected PreparedStatement selectBoundsSQLPS(final SimpleFeatureType featureType, final Query query,
                                                  final Connection cx) throws SQLException{

        final StringBuilder sql = new StringBuilder();

        final boolean offsetLimit = checkLimitOffset(query);
        if (offsetLimit) {
            // envelopes are aggregates, just like count, so we must first isolate
            // the rows against which the aggregate will work in a subquery
            sql.append(" SELECT *");
        } else {
            sql.append("SELECT ");
            buildEnvelopeAggregates(featureType, sql);
        }

        sql.append(" FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        // encode the filter
        PreparedFilterToSQL toSQL = null;
        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                toSQL = createPreparedFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        // finally encode limit/offset, if necessary
        if (offsetLimit) {
            applyLimitOffset(sql, query);
            // build the prologue
            final StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            buildEnvelopeAggregates(featureType, sb);
            sb.append("FROM (");
            // wrap the existing query
            sql.insert(0, sb.toString());
            sql.append(")");
            dialect.encodeTableAlias("GT2_BOUNDS_", sql);
        }


        getLogger().fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }

    /**
     * Builds a list of the aggregate function calls necesary to compute each geometry
     * column bounds
     * @param featureType
     * @param sql
     */
    private void buildEnvelopeAggregates(final SimpleFeatureType featureType, final StringBuilder sql) {
        //walk through all geometry attributes and build the query
        for (final Iterator a = featureType.getAttributeDescriptors().iterator(); a.hasNext();) {
            final AttributeDescriptor attribute = (AttributeDescriptor) a.next();
            if (attribute instanceof GeometryDescriptor) {
                final String geometryColumn = featureType.getGeometryDescriptor().getLocalName();
                dialect.encodeGeometryEnvelope(featureType.getTypeName(), geometryColumn, sql);
                sql.append(",");
            }
        }
        sql.setLength(sql.length() - 1);
    }

    /**
     * Applies the limit/offset elements to the query if they are specified
     * and if the dialect supports them
     * @param sql The sql to be modified
     * @param the query that holds the limit and offset parameters
     */
    private void applyLimitOffset(final StringBuilder sql, final Query query) {
        if (checkLimitOffset(query)) {
            final int offset = query.getStartIndex();
            final Integer limit = query.getMaxFeatures();
            dialect.applyLimitOffset(sql, limit, offset);
        }
    }

    /**
     * Checks if the query needs limit/offset treatment
     * @param query
     * @return true if the query needs limit/offset treatment and if the sql dialect can do that natively
     */
    private boolean checkLimitOffset(final Query query) {
        // if we cannot, don't bother checking the query
        if (!dialect.isLimitOffsetSupported()) {
            return false;
        }

        // the check the query has at least a non default value for limit/offset
        final int offset = query.getStartIndex();
        final Integer limit = query.getMaxFeatures();
        return limit != null || offset > 0;
    }

    /**
     * Helper method for setting the values of the WHERE class of a prepared statement.
     *
     */
    protected void setPreparedFilterValues(final PreparedStatement ps, final PreparedFilterToSQL toSQL,
                                           final int offset, final Connection cx) throws SQLException{
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getDialect();

        for (int i = 0; i < toSQL.getLiteralValues().size(); i++) {
            final Object value = toSQL.getLiteralValues().get(i);
            final Class binding = toSQL.getLiteralTypes().get(i);
            Integer srid = toSQL.getSRIDs().get(i);
            if (srid == null) {
                srid = -1;
            }

            if (binding != null && Geometry.class.isAssignableFrom(binding)) {
                dialect.setGeometryValue((Geometry) value, srid, binding, ps, offset + i + 1);
            } else {
                dialect.setValue(value, binding, ps, offset + i + 1, cx);
            }
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().fine((i + 1) + " = " + value);
            }
        }
    }


    /**
     * Helper method for determining what the sql type names are for a set of
     * classes.
     * <p>
     * This method uses a combination of dialect mappings and database metadata
     * to determine which sql types map to the specified classes.
     * </p>
     */
    private String[] getSQLTypeNames(final Class[] classes, final Connection cx)
            throws SQLException {
        //figure out what the sql types are corresponding to the feature type
        // attributes
        final int[] sqlTypes = new int[classes.length];
        final String[] sqlTypeNames = new String[sqlTypes.length];

        for (int i = 0; i < classes.length; i++) {
            final Class clazz = classes[i];
            Integer sqlType = dialect.getMapping(clazz);

            if (sqlType == null) {
                getLogger().warning("No sql type mapping for: " + clazz);
                sqlType = Types.OTHER;
            }

            sqlTypes[i] = sqlType;

            //if this a geometric type, get the name from teh dialect
            //if ( attributeType instanceof GeometryDescriptor ) {
            if (Geometry.class.isAssignableFrom(clazz)) {
                String sqlTypeName = dialect.getGeometryTypeName(sqlType);

                if (sqlTypeName != null) {
                    sqlTypeNames[i] = sqlTypeName;
                }
            }

            //check the overrides
            final String sqlTypeName = dialect.getSqlTypeToSqlTypeNameOverrides().get(sqlType);
            if (sqlTypeName != null) {
                sqlTypeNames[i] = sqlTypeName;
            }

        }

        //figure out the type names that correspond to the sql types from
        // the database metadata
        final DatabaseMetaData metaData = cx.getMetaData();

        /*
         *      <LI><B>TYPE_NAME</B> String => Type name
         *        <LI><B>DATA_TYPE</B> int => SQL data type from java.sql.Types
         *        <LI><B>PRECISION</B> int => maximum precision
         *        <LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal
         *      (may be <code>null</code>)
         *        <LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal
        (may be <code>null</code>)
         *        <LI><B>CREATE_PARAMS</B> String => parameters used in creating
         *      the type (may be <code>null</code>)
         *        <LI><B>NULLABLE</B> short => can you use NULL for this type.
         *      <UL>
         *      <LI> typeNoNulls - does not allow NULL values
         *      <LI> typeNullable - allows NULL values
         *      <LI> typeNullableUnknown - nullability unknown
         *      </UL>
         *        <LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive.
         *        <LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
         *      <UL>
         *      <LI> typePredNone - No support
         *      <LI> typePredChar - Only supported with WHERE .. LIKE
         *      <LI> typePredBasic - Supported except for WHERE .. LIKE
         *      <LI> typeSearchable - Supported for all WHERE ..
         *      </UL>
         *        <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned.
         *        <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value.
         *        <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
         *      auto-increment value.
         *        <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name
         *      (may be <code>null</code>)
         *        <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
         *        <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
         *        <LI><B>SQL_DATA_TYPE</B> int => unused
         *        <LI><B>SQL_DATETIME_SUB</B> int => unused
         *        <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
         */
        final ResultSet types = metaData.getTypeInfo();

        try {
            while (types.next()) {
                final int sqlType = types.getInt("DATA_TYPE");
                final String sqlTypeName = types.getString("TYPE_NAME");

                for (int i = 0; i < sqlTypes.length; i++) {
                    //check if we already have the type name from the dialect
                    if (sqlTypeNames[i] != null) {
                        continue;
                    }

                    if (sqlType == sqlTypes[i]) {
                        sqlTypeNames[i] = sqlTypeName;
                    }
                }
            }
        } finally {
            closeSafe(types);
        }

        // apply the overrides specified by the dialect
        final Map<Integer, String> overrides = dialect.getSqlTypeToSqlTypeNameOverrides();
        for (int i = 0; i < sqlTypes.length; i++) {
            final String override = overrides.get(sqlTypes[i]);
            if (override != null) {
                sqlTypeNames[i] = override;
            }
        }

        return sqlTypeNames;
    }

    ////////////////////////////////////////////////////////////////////////////
    // other utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * The filter capabilities which reports which spatial operations the
     * underlying database can handle natively.
     *
     * @return The filter capabilities, never <code>null</code>.
     */
    public DefaultFilterCapabilities getFilterCapabilities() {
        if (dialect instanceof PreparedStatementSQLDialect) {
            return ((PreparedStatementSQLDialect) dialect).createPreparedFilterToSQL().getCapabilities();
        } else {
            return ((BasicSQLDialect) dialect).createFilterToSQL().getCapabilities();
        }
    }

    /**
     * Helper method for splitting a filter.
     */
    Filter[] splitFilter(final Filter original, FeatureType schema) throws DataStoreException {
        final Filter[] split = new Filter[2];
        if ( original != null ) {
            //create a filter splitter
            final CapabilitiesFilterSplitter splitter = new CapabilitiesFilterSplitter(getFilterCapabilities(),
                    schema, null);
            original.accept(splitter, null);

            split[0] = splitter.getFilterPre();
            split[1] = splitter.getFilterPost();
        }

        final SimplifyingFilterVisitor visitor = new SimplifyingFilterVisitor();
        final PrimaryKey key = getPrimaryKey(schema);
        visitor.setFIDValidator( new PrimaryKeyFIDValidator( this,key ) );
        split[0] = (Filter) split[0].accept(visitor, null);
        split[1] = (Filter) split[1].accept(visitor, null);

        return split;
    }

    /**
     * Searches the attribute descriptor restrictions in an attempt to determine
     * the length of the specified varchar column.
     */
    private static Integer findVarcharColumnLength(final AttributeDescriptor att) {
        for (final Filter r : att.getType().getRestrictions()) {
            if (r instanceof PropertyIsLessThanOrEqualTo) {
                final PropertyIsLessThanOrEqualTo c = (PropertyIsLessThanOrEqualTo) r;
                if (c.getExpression1() instanceof Function &&
                        ((Function) c.getExpression1()).getName().toLowerCase().endsWith("length")) {
                    if (c.getExpression2() instanceof Literal) {
                        final Integer length = c.getExpression2().evaluate(null, Integer.class);
                        if (length != null) {
                            return length;
                        }
                    }
                }
            }
        }

        return null;
    }


    @Override
    protected void finalize() throws Throwable {
        if (source != null) {
            getLogger().severe("There's code using JDBC based datastore and " +
                    "not disposing them. This may lead to temporary loss of database connections. " +
                    "Please make sure all data access code calls DataStore.dispose() " +
                    "before freeing all references to it");
            dispose();
        }
        super.finalize();
    }

    @Override
    public void dispose() {
        if (source instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) source;
                source = null;
                mds.close();
            } catch (SQLException e) {
                // it's ok, we did our best..
                getLogger().log(Level.FINE, "Could not close dataSource", e);
            }
        }
    }

    /**
     * Checks if geometry generalization required and makes sense
     *
     * @param hints 	hints hints passed in
     * @param gatt 		Geometry attribute descriptor
     * @return			true to indicate generalization
     */
    protected boolean isGeneralizationRequired(final Hints hints, final GeometryDescriptor gatt) {
        return isGeometryReduceRequired(hints, gatt, HintsPending.GEOMETRY_GENERALIZATION);
    }

    /**
     * Checks if geometry simplification required and makes sense
     *
     * @param hints 	hints hints passed in
     * @param gatt 		Geometry attribute descriptor
     * @return			true to indicate simplification
     */
    protected boolean isSimplificationRequired(final Hints hints, final GeometryDescriptor gatt) {
        return isGeometryReduceRequired(hints, gatt, HintsPending.GEOMETRY_SIMPLIFICATION);
    }

    /**
     * Checks if reduction required and makes sense
     *
     * @param hints	  hints passed in
     * @param gatt   Geometry attribute descriptor
     * @param param  {@link HintsPending#GEOMETRY_GENERALIZATION} or {@link HintsPending#GEOMETRY_SIMPLIFICATION}
     * @return true to indicate reducing the geometry, false otherwise
     */
    protected boolean isGeometryReduceRequired(final Hints hints, final GeometryDescriptor gatt,
                                               final HintsPending.Key param)
    {
        if (hints == null) {
            return false;
        }
        if (hints.containsKey(param) == false) {
            return false;
        }
        if (gatt.getType().getBinding() == Point.class) {
            return false;
        }
        return true;
    }

    /**
     * Encoding a geometry column with respect to hints
     * Supported Hints are provided by {@link SQLDialect#addSupportedHints(Set)}
     *
     * @param gatt
     * @param sql
     * @param hints , may be null
     */
    protected void encodeGeometryColumn(final GeometryDescriptor gatt, final StringBuilder sql,
                                        final Hints hints){

        final int srid = getDescriptorSRID(gatt);
        if (isGeneralizationRequired(hints, gatt)) {
            final Double distance = (Double) hints.get(HintsPending.GEOMETRY_GENERALIZATION);
            dialect.encodeGeometryColumnGeneralized(gatt, srid, sql, distance);
            return;
        }

        if (isSimplificationRequired(hints, gatt)) {
            final Double distance = (Double) hints.get(HintsPending.GEOMETRY_SIMPLIFICATION);
            dialect.encodeGeometryColumnSimplified(gatt, srid, sql, distance);
            return;
        }

        dialect.encodeGeometryColumn(gatt, srid, sql);
    }

    /**
     * Extracts the eventual native SRID user property from the descriptor,
     * returns -1 if not found
     * @param descriptor
     */
    protected int getDescriptorSRID(final AttributeDescriptor descriptor) {
        int srid = -1;

        // check if we have stored the native srid in the descriptor (we should)
        if (descriptor.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID) != null) {
            srid = (Integer) descriptor.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID);
        }

        return srid;
    }

    /**
     * Helper method for executing a property name against a feature type.
     * <p>
     * This method will fall back on {@link PropertyName#getPropertyName()} if
     * it does not evaulate against the feature type.
     * </p>
     */
    protected static String getPropertyName(final SimpleFeatureType featureType, final PropertyName propertyName) {
        final AttributeDescriptor att = (AttributeDescriptor) propertyName.evaluate(featureType);

        if (att != null) {
            return att.getLocalName();
        }

        return propertyName.getPropertyName();
    }


    /**
     * Encodes a feature id from a primary key and result set values.
     */
    protected String encodeFID(final PrimaryKey pkey, final ResultSet rs)
            throws SQLException{
        // no pk columns
        if (pkey.getColumns().isEmpty()) {
            return SimpleFeatureBuilder.createDefaultFeatureId();
        }

        // just one, no need to build support structures
        if (pkey.getColumns().size() == 1) {
            return rs.getString(1);
        }

        // more than one
        final List<Object> keyValues = new ArrayList<Object>();
        for (int i = 0; i < pkey.getColumns().size(); i++) {
            String o = rs.getString(i + 1);
            keyValues.add(o);
        }
        return encodeFID(keyValues);
    }

    protected String encodeFID(final List<Object> keyValues) {
        final StringBuffer fid = new StringBuffer();
        for (Object o : keyValues) {
            fid.append(o).append(".");
        }
        fid.setLength(fid.length() - 1);
        return fid.toString();
    }

    /**
     * Decodes a fid into its components based on a primary key.
     *
     * @param strict If set to true the value of the fid will be validated against
     *   the type of the key columns. If a conversion can not be made, an exception will be thrown.
     */
    protected List<Object> decodeFID(final PrimaryKey key, String FID, final boolean strict) {
        //strip off the feature type name
        if (FID.startsWith(key.getTableName() + ".")) {
            FID = FID.substring(key.getTableName().length() + 1);
        }

        try {
            FID = URLDecoder.decode(FID, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never occur, because we asked for UTF-8 which is
            // known to be supported.
            throw new AssertionError(e);
        }

        //check for case of multi column primary key and try to backwards map using
        // "." as a seperator of values
        final List values;
        if (key.getColumns().size() > 1) {
            final String[] split = FID.split("\\.");

            //copy over to avoid array store exception
            //values = new ArrayList(split.length);
            values = Arrays.asList(split);
        } else {
            //single value case
            values = new ArrayList();
            values.add(FID);
        }
        if (values.size() != key.getColumns().size()) {
            throw new IllegalArgumentException("Illegal fid: " + FID + ". Expected " +
                    key.getColumns().size() + " values but got " + values.size());
        }

        //convert to the type of the key
        //JD: usually this would be done by the dialect directly when the value
        // actually gets set but the FIDMapper interface does not report types
        for (int i = 0; i < values.size(); i++) {
            final Object value = values.get(i);
            if (value != null) {
                final Class type = key.getColumns().get(i).getType();
                final Object converted = Converters.convert(value, type);
                if (converted != null) {
                    values.set(i, converted);
                }
                if (strict && !type.isInstance(converted)) {
                    throw new IllegalArgumentException("Value " + values.get(i) + " illegal for type " + type.getName());
                }
            }
        }

        return values;
    }

}
