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

import org.geotoolkit.data.jdbc.FilterToSQLException;
import org.geotoolkit.data.jdbc.FilterToSQL;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.data.GmlObjectStore;
import org.geotoolkit.data.InProcessLockingManager;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.data.jdbc.FilterToSQLException;
import org.geotoolkit.data.jdbc.datasource.ManageableDataSource;
import org.geotoolkit.data.jdbc.fidmapper.FIDMapper;
import org.geotoolkit.data.store.ContentDataStore;
import org.geotoolkit.data.store.ContentEntry;
import org.geotoolkit.data.store.ContentFeatureSource;
import org.geotoolkit.data.store.ContentState;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Converters;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.GmlObjectId;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;


/**
 * Datastore implementation for jdbc based relational databases.
 * <p>
 * This class is not intended to be subclassed on a per database basis. Instead
 * the notion of a "dialect" is used.
 * </p>
 * <p>
 *   <h3>Dialects</h3>
 * A dialect ({@link SQLDialect}) encapsulates all the operations that are database
 * specific. Therefore to implement a jdbc based datastore one must extend SQLDialect.
 * The specific dialect to use is set using {@link #setSQLDialect(SQLDialect)}.
 * </p>
 * <p>
 *   <h3>Database Connections</h3>
 *   Connections to the underlying database are obtained through a {@link DataSource}.
 *   A datastore must be specified using {@link #setDataSource(DataSource)}.
 * </p>
 * <p>
 *   <h3>Schemas</h3>
 * This datastore supports the notion of database schemas, which is more or less
 * just a grouping of tables. When a schema is specified, only those tables which
 * are part of the schema are provided by the datastore. The schema is specified
 * using {@link #setDatabaseSchema(String)}.
 * </p>
 * <p>
 *   <h3>Spatial Functions</h3>
 * The set of spatial operations or functions that are supported by the
 * specific database are reported with a {@link FilterCapabilities} instance.
 * This is specified using {@link #setFilterCapabilities(FilterCapabilities)}.
 * </p>
 *
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public final class JDBCDataStore extends ContentDataStore implements GmlObjectStore {

    /**
     * The native SRID associated to a certain descriptor
     */
    public static final String JDBC_NATIVE_SRID = "nativeSRID";
    /**
     * name of table to use to store geometries when {@link #associations}
     * is set.
     */
    public static final String GEOMETRY_TABLE = "geometry";
    /**
     * name of table to use to store multi geometries made up of non-multi
     * geometries when {@link #associations} is set.
     */
    public static final String MULTI_GEOMETRY_TABLE = "multi_geometry";
    /**
     * name of table to use to store geometry associations when {@link #associations}
     * is set.
     */
    public static final String GEOMETRY_ASSOCIATION_TABLE = "geometry_associations";
    /**
     * name of table to use to store feature relationships (information about
     * associations) when {@link #associations} is set.
     */
    public static final String FEATURE_RELATIONSHIP_TABLE = "feature_relationships";
    /**
     * name of table to use to store feature associations when {@link #associations}
     * is set.
     */
    public static final String FEATURE_ASSOCIATION_TABLE = "feature_associations";
    /**
     * The envelope returned when bounds is called against a geometryless feature type
     */
    public static final JTSEnvelope2D EMPTY_ENVELOPE = new JTSEnvelope2D();
    /**
     * data source
     */
    private DataSource dataSource;
    /**
     * the dialect of sql
     */
    private SQLDialect dialect;
    /**
     * The database schema.
     */
    private String databaseSchema;
    /**
     * sql type to java class mappings
     */
    private HashMap<Integer, Class<?>> sqlTypeToClassMappings;
    /**
     * sql type name to java class mappings
     */
    private HashMap<String, Class<?>> sqlTypeNameToClassMappings;
    /**
     * java class to sql type mappings;
     */
    private HashMap<Class<?>, Integer> classToSqlTypeMappings;
    /**
     * sql type to sql type name overrides
     */
    private HashMap<Integer, String> sqlTypeToSqlTypeNameOverrides;
    /**
     * flag controlling if the datastore is supporting feature and geometry
     * relationships with associations
     */
    private boolean associations = false;
    /**
     * The fetch size for this datastore, defaulting to 1000. Set to a value less or equal
     * to 0 to disable fetch size limit and grab all the records in one shot.
     */
    private int fetchSize;
    /**
     * TODO: this must be removed and replaced by a more configurable
     * cache
     */
    List typeNameCache = null;

    /**
     * The current fetch size. The fetch size influences how many records are read from the
     * dbms at a time. If set to a value less or equal than zero, all the records will be
     * read in one shot, severily increasing the memory requirements to read a big number
     * of features.
     * @return
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * Changes the fetch size.
     * @param fetchSize
     */
    public void setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * The dialect the datastore uses to generate sql statements in order to
     * communicate with the underlying database.
     *
     * @return The dialect, never <code>null</code>.
     */
    public SQLDialect getSQLDialect() {
        return dialect;
    }

    /**
     * Sets the dialect the datastore uses to generate sql statements in order to
     * communicate with the underlying database.
     *
     * @param dialect The dialect, never <code>null</code>.
     */
    public void setSQLDialect(final SQLDialect dialect) {
        if (dialect == null) {
            throw new NullPointerException();
        }

        this.dialect = dialect;
    }

    /**
     * The data source the datastore uses to obtain connections to the underlying
     * database.
     *
     * @return The data source, never <code>null</code>.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the data source the datastore uses to obtain connections to the underlying
     * database.
     *
     * @param dataSource The data source, never <code>null</code>.
     */
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * The schema from which this datastore is serving tables from.
     *
     * @return the schema, or <code>null</code> if non specified.
     */
    public String getDatabaseSchema() {
        return databaseSchema;
    }

    /**
     * Set the database schema for the datastore.
     * <p>
     * When this value is set only those tables which are part of the schema are
     * served through the datastore. This value can be set to <code>null</code>
     * to specify no particular schema.
     * </p>
     * @param databaseSchema The schema, may be <code>null</code>.
     */
    public void setDatabaseSchema(final String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }

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
     * Flag controlling if the datastore is supporting feature and geometry
     * relationships with associations
     */
    public boolean isAssociations() {
        return associations;
    }

    /**
     * Sets the flag controlling if the datastore is supporting feature and geometry
     * relationships with associations
     */
    public void setAssociations(final boolean foreignKeyGeometries) {
        this.associations = foreignKeyGeometries;
    }

    /**
     * The sql type to java type mappings that the datastore uses when reading
     * and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerSqlTypeToClassMappings(java.util.Map)}
     * </p>
     * @return The mappings, never <code>null</code>.
     */
    public Map<Integer, Class<?>> getSqlTypeToClassMappings() {
        if (sqlTypeToClassMappings == null) {
            sqlTypeToClassMappings = new HashMap<Integer, Class<?>>();
            dialect.registerSqlTypeToClassMappings(sqlTypeToClassMappings);
        }

        return sqlTypeToClassMappings;
    }

    /**
     * The sql type name to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerSqlTypeNameToClassMappings(Map)}
     * </p>
     *
     * @return The mappings, never <code>null<code>.
     */
    public Map<String, Class<?>> getSqlTypeNameToClassMappings() {
        if (sqlTypeNameToClassMappings == null) {
            sqlTypeNameToClassMappings = new HashMap<String, Class<?>>();
            dialect.registerSqlTypeNameToClassMappings(sqlTypeNameToClassMappings);
        }

        return sqlTypeNameToClassMappings;
    }

    /**
     * The java type to sql type mappings that the datastore uses when reading
     * and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerClassToSqlMappings(Map)}
     * </p>
     * @return The mappings, never <code>null</code>.
     */
    public Map<Class<?>, Integer> getClassToSqlTypeMappings() {
        if (classToSqlTypeMappings == null) {
            classToSqlTypeMappings = new HashMap<Class<?>, Integer>();
            dialect.registerClassToSqlMappings(classToSqlTypeMappings);
        }

        return classToSqlTypeMappings;
    }

    /**
     * Returns any ovverides which map integer constants for database types (from {@link Types})
     * to database type names.
     * <p>
     * This method will return an empty map when there are no overrides.
     * </p>
     */
    public Map<Integer, String> getSqlTypeToSqlTypeNameOverrides() {
        if (sqlTypeToSqlTypeNameOverrides == null) {
            sqlTypeToSqlTypeNameOverrides = new HashMap<Integer, String>();
            dialect.registerSqlTypeToSqlTypeNameOverrides(sqlTypeToSqlTypeNameOverrides);
        }

        return sqlTypeToSqlTypeNameOverrides;
    }

    /**
     * Returns the java type mapped to the specified sql type.
     * <p>
     * If there is no such type mapped to <tt>sqlType</tt>, <code>null</code>
     * is returned.
     * </p>
     * @param sqlType The integer constant for the sql type from {@link Types}.
     *
     * @return The mapped java class, or <code>null</code>. if no such mapping exists.
     */
    public Class<?> getMapping(final int sqlType) {
        return getSqlTypeToClassMappings().get(sqlType);
    }

    /**
     * Returns the java type mapped to the specified sql type name.
     * <p>
     * If there is no such type mapped to <tt>sqlTypeName</tt>, <code>null</code>
     * is returned.
     * </p>
     * @param sqlTypeName The name of the sql type.
     *
     * @return The mapped java class, or <code>null</code>. if no such mapping exists.
     */
    public Class<?> getMapping(final String sqlTypeName) {
        return getSqlTypeNameToClassMappings().get(sqlTypeName);
    }

    /**
     * Returns the sql type mapped to the specified java type.
     * <p>
     * If there is no such type mapped to <tt>clazz</tt>, <code>Types.OTHER</code>
     * is returned.
     * </p>
     * @param clazz The java class.
     *
     * @return The mapped sql type from {@link Types}, Types.OTHER if no such
     * mapping exists.
     */
    public Integer getMapping(final Class<?> clazz) {
        Integer mapping = getClassToSqlTypeMappings().get(clazz);

        if (mapping == null) {
            mapping = Types.OTHER;
            Logger.warning("No mapping for " + clazz.getName());
        }

        return mapping;
    }

    /**
     * Creates a table in the underlying database from the specified table.
     * <p>
     * This method will map the classes of the attributes of <tt>featureType</tt>
     * to sql types and generate a 'CREATE TABLE' statement against the underlying
     * database.
     * </p>
     * @see DataStore#createSchema(SimpleFeatureType)
     *
     * @throws IllegalArgumentException If the table already exists.
     * @throws IOException If the table cannot be created due to an error.
     */
    @Override
    public void createSchema(final SimpleFeatureType featureType) throws IOException {
        if (entry(featureType.getName()) != null) {
            throw new IllegalArgumentException("Schema '" + featureType.getName() + "' already exists");
        }

        //execute the create table statement
        //TODO: create a primary key and a spatial index
        Connection cx = null;

        try {
            cx = createConnection();
            final String sql = createTableSQL(featureType, cx);
            Logger.log(Level.FINE, "Create schema: {0}", sql);

            final Statement st = cx.createStatement();

            try {
                st.execute(sql);
            } finally {
                closeSafe(st);
            }

            dialect.postCreateTable(databaseSchema, featureType, cx);
        } catch (Exception e) {
            throw (IOException) new IOException("Error occurred creating table").initCause(e);
        } finally {
            closeSafe(cx);
        }

        // reset the type name cache, there's a new type name in town
        typeNameCache = null;
    }

    /**
     *
     */
    @Override
    public Object getGmlObject(final GmlObjectId id, final Hints hints) throws IOException {
        //geometry?
        if (isAssociations()) {

            Connection cx = null;
            try {
                cx = createConnection();
                final Statement st;
                final ResultSet rs;

                if (getSQLDialect() instanceof PreparedStatementSQLDialect) {
                    st = selectGeometrySQLPS(id.getID(), cx);
                    rs = ((PreparedStatement) st).executeQuery();
                } else {
                    final String sql = selectGeometrySQL(id.getID());
                    Logger.log(Level.FINE, "Get GML object: {0}", sql);

                    st = cx.createStatement();
                    rs = st.executeQuery(sql);
                }

                try {
                    if (rs.next()) {
                        //read the geometry
                        final Geometry g = getSQLDialect().decodeGeometryValue(
                                null, rs, "geometry", getGeometryFactory(), cx);

                        //read the metadata
                        final String name = rs.getString("name");
                        final String desc = rs.getString("description");
                        setGmlProperties(g, id.getID(), name, desc);

                        return g;
                    }
                } finally {
                    closeSafe(rs);
                    closeSafe(st);
                }

            } catch (SQLException e) {
                throw (IOException) new IOException().initCause(e);
            } finally {
                closeSafe(cx);
            }
        }

        //regular feature, first feature out the feature type
        int i = id.getID().indexOf('.');
        if (i == -1) {
            Logger.info("Unable to determine feature type for GmlObjectId:" + id);
            return null;
        }

        //figure out the type name from the id
        final String featureTypeName = id.getID().substring(0, i);
        final SimpleFeatureType featureType = getSchema(featureTypeName);
        if (featureType == null) {
            throw new IllegalArgumentException("No such feature type: " + featureTypeName);
        }

        //load the feature
        final Id filter = getFilterFactory().id(Collections.singleton(id));
        final DefaultQuery query = new DefaultQuery(featureTypeName);
        query.setFilter(filter);
        query.setHints(hints);

        final FeatureCollection<SimpleFeatureType, SimpleFeature> features =
                getFeatureSource(featureTypeName).getFeatures(query);
        if (!features.isEmpty()) {
            final FeatureIterator<SimpleFeature> fi = features.features();
            try {
                if (fi.hasNext()) {
                    return fi.next();
                }
            } finally {
                features.close(fi);
            }
        }

        return null;
    }

    /**
     * Creates a new instance of {@link JDBCFeatureStore}.
     *
     * @see ContentDataStore#createFeatureSource(ContentEntry)
     */
    @Override
    protected ContentFeatureSource createFeatureSource(final ContentEntry entry) throws IOException {
        //check primary key to figure out if we must return a read only (feature source)
        final PrimaryKey pkey = getPrimaryKey(entry);
        if (pkey == null || pkey instanceof NullPrimaryKey) {
            return new JDBCFeatureSource(entry, null);
        }
        return new JDBCFeatureStore(entry, null);
    }

    /**
     * Creates an instanceof {@link JDBCState}.
     *
     * @see ContentDataStore#createContentState(ContentEntry)
     */
    @Override
    protected ContentState createContentState(final ContentEntry entry) {
        return new JDBCState(entry);
    }

    /**
     * Generates the list of type names provided by the database.
     * <p>
     * The list is generated from the underlying database metadata.
     * </p>
     */
    @Override
    protected List createTypeNames() throws IOException {
        if (typeNameCache != null) {
            return typeNameCache;
        }

        Connection cx = null;

        /*
         *        <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
         *        <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
         *        <LI><B>TABLE_NAME</B> String => table name
         *        <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
         *                        "VIEW",        "SYSTEM TABLE", "GLOBAL TEMPORARY",
         *                        "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
         *        <LI><B>REMARKS</B> String => explanatory comment on the table
         *  <LI><B>TYPE_CAT</B> String => the types catalog (may be <code>null</code>)
         *  <LI><B>TYPE_SCHEM</B> String => the types schema (may be <code>null</code>)
         *  <LI><B>TYPE_NAME</B> String => type name (may be <code>null</code>)
         *  <LI><B>SELF_REFERENCING_COL_NAME</B> String => name of the designated
         *                  "identifier" column of a typed table (may be <code>null</code>)
         *        <LI><B>REF_GENERATION</B> String => specifies how values in
         *                  SELF_REFERENCING_COL_NAME are created. Values are
         *                  "SYSTEM", "USER", "DERIVED". (may be <code>null</code>)
         */
        final List typeNames = new ArrayList();

        try {
            cx = createConnection();
            final DatabaseMetaData metaData = cx.getMetaData();
            final ResultSet tables = metaData.getTables(null, databaseSchema, "%",
                    new String[]{"TABLE", "VIEW"});

            try {
                while (tables.next()) {
                    final String schemaName = tables.getString("TABLE_SCHEM");
                    final String tableName = tables.getString("TABLE_NAME");

                    //use the dialect to filter
                    if (!dialect.includeTable(schemaName, tableName, cx)) {
                        continue;
                    }

                    typeNames.add(new DefaultName(namespaceURI, tableName));
                }
            } finally {
                closeSafe(tables);
            }
        } catch (SQLException e) {
            throw (IOException) new IOException("Error occurred getting table name list.").initCause(e);
        } finally {
            closeSafe(cx);
        }

        typeNameCache = typeNames;
        return typeNames;
    }

    /**
     * Returns the primary key object for a particular entry, deriving it from
     * the underlying database metadata.
     *
     */
    protected PrimaryKey getPrimaryKey(final ContentEntry entry) throws IOException {
        final JDBCState state = (JDBCState) entry.getState(Transaction.AUTO_COMMIT);

        if (state.getPrimaryKey() == null) {
            synchronized (this) {
                if (state.getPrimaryKey() == null) {
                    //get metadata from database
                    Connection cx = null;

                    try {
                        cx = createConnection();
                        final String tableName = entry.getName().getLocalPart();
                        final DatabaseMetaData metaData = cx.getMetaData();
                        final ResultSet primaryKey = metaData.getPrimaryKeys(null, databaseSchema, tableName);

                        try {
                            /*
                             *        <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
                             *        <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
                             *        <LI><B>TABLE_NAME</B> String => table name
                             *        <LI><B>COLUMN_NAME</B> String => column name
                             *        <LI><B>KEY_SEQ</B> short => sequence number within primary key
                             *        <LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
                             */
                            final ArrayList<PrimaryKeyColumn> cols = new ArrayList();

                            while (primaryKey.next()) {
                                final String columnName = primaryKey.getString("COLUMN_NAME");

                                //look up the type ( should only be one row )
                                final ResultSet columns = metaData.getColumns(null, databaseSchema, tableName, columnName);
                                columns.next();

                                final int binding = columns.getInt("DATA_TYPE");
                                Class columnType = getMapping(binding);

                                if (columnType == null) {
                                    Logger.warning("No class for sql type " + binding);
                                    columnType = Object.class;
                                }

                                //determine which type of primary key we have
                                PrimaryKeyColumn col = null;

                                //1. Auto Incrementing?
                                final Statement st = cx.createStatement();

                                try {
                                    //not actually going to get data
                                    st.setFetchSize(1);

                                    final StringBuffer sql = new StringBuffer();
                                    sql.append("SELECT ");
                                    dialect.encodeColumnName(columnName, sql);
                                    sql.append(" FROM ");
                                    encodeTableName(tableName, sql);

                                    sql.append(" WHERE 0=1");

                                    Logger.log(Level.FINE, "Grabbing table pk metadata: {0}", sql);

                                    ResultSet rs = null;
                                    try {
                                        rs = st.executeQuery(sql.toString());

                                        if (rs.getMetaData().isAutoIncrement(1)) {
                                            col = new AutoGeneratedPrimaryKeyColumn(columnName, columnType);
                                        }
                                    } finally {
                                        closeSafe(rs);
                                    }
                                } finally {
                                    closeSafe(st);
                                }

                                //2. Has a sequence?
                                if (col == null) {
                                    //TODO: look for a sequence
                                    final String sequenceName = dialect.getSequenceForColumn(databaseSchema,
                                            tableName, columnName, cx);
                                    if (sequenceName != null) {
                                        col = new SequencedPrimaryKeyColumn(columnName, columnType, sequenceName);
                                    }
                                }

                                if (col == null) {
                                    col = new NonIncrementingPrimaryKeyColumn(columnName, columnType);
                                }

                                cols.add(col);
                            }

                            final PrimaryKey pkey;
                            if (cols.isEmpty()) {
                                Logger.warning("No primary key found for " + tableName + ".");

                                pkey = new NullPrimaryKey(tableName);
                            } else {
                                pkey = new PrimaryKey(tableName, cols);
                            }

                            state.setPrimaryKey(pkey);
                        } finally {
                            closeSafe(primaryKey);
                        }
                    } catch (SQLException e) {
                        throw (IOException) new IOException("Error looking up primary key").initCause(e);
                    } finally {
                        closeSafe(cx);
                    }
                }
            }
        }

        return state.getPrimaryKey();
    }

    /**
     * Returns the primary key object for a particular feature type / table,
     * deriving it from the underlying database metadata.
     *
     */
    protected PrimaryKey getPrimaryKey(final SimpleFeatureType featureType) throws IOException {
        return getPrimaryKey(ensureEntry(featureType.getName()));
    }

    /**
     * Returns the bounds of the features for a particular feature type / table.
     *
     * @param featureType The feature type / table.
     * @param query Specifies rows to include in bounds calculation, as well as how many
     *              features and the offset if needed
     */
    protected JTSEnvelope2D getBounds(final SimpleFeatureType featureType, final Query query,
            final Connection cx) throws IOException {

        // handle geometryless case by returning an emtpy envelope
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
                Logger.log(Level.FINE, "Retriving bounding box: {0}", sql);

                st = cx.createStatement();
                rs = st.executeQuery(sql);
            }

            try {
                final JTSEnvelope2D bounds;
                Envelope e = null;
                if (rs.next()) {
                    e = dialect.decodeGeometryEnvelope(rs, 1, st.getConnection());
                }

                if (e == null) {
                    e = new Envelope();
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
                        bounds.expandToInclude(dialect.decodeGeometryEnvelope(rs, 1, st.getConnection()));
                    }
                }

                return bounds;
            } finally {
                closeSafe(rs);
                closeSafe(st);
            }
        } catch (SQLException e) {
            throw (IOException) new IOException("Error occured calculating bounds").initCause(e);
        }
    }

    /**
     * Returns the count of the features for a particular feature type / table.
     */
    protected int getCount(final SimpleFeatureType featureType, final Query query, final Connection cx)
            throws IOException {

        final Statement st;
        final ResultSet rs;
        try {
            if (dialect instanceof PreparedStatementSQLDialect) {
                st = selectCountSQLPS(featureType, query, cx);
                rs = ((PreparedStatement) st).executeQuery();
            } else {
                String sql = selectCountSQL(featureType, query);
                Logger.log(Level.FINE, "Counting features: {0}", sql);

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
            String msg = "Error occured calculating count";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }

    /**
     * Inserts a new feature into the database for a particular feature type / table.
     */
    protected void insert(final SimpleFeature feature, final SimpleFeatureType featureType,
            final Connection cx) throws IOException {
        insert(Collections.singletonList(feature), featureType, cx);
    }

    /**
     * Inserts a collection of new features into the database for a particular
     * feature type / table.
     */
    protected void insert(final Collection features, final SimpleFeatureType featureType,
            final Connection cx) throws IOException {
        final PrimaryKey key = getPrimaryKey(featureType);

        // we do this in a synchronized block because we need to do two queries,
        // first to figure out what the id will be, then the insert statement
        synchronized (this) {
            Statement st = null;
            try {
                if (!(dialect instanceof PreparedStatementSQLDialect)) {
                    st = cx.createStatement();
                }

                //figure out what the next fid will be
                final List<Object> nextKeyValues = getNextValues(key, cx);

                for (Iterator f = features.iterator(); f.hasNext();) {
                    final SimpleFeature feature = (SimpleFeature) f.next();

                    if (dialect instanceof PreparedStatementSQLDialect) {
                        final PreparedStatement ps = insertSQLPS(featureType, feature, nextKeyValues, cx);
                        try {
                            ps.execute();
                        } finally {
                            closeSafe(ps);
                        }
                    } else {
                        String sql = insertSQL(featureType, feature, nextKeyValues, cx);
                        Logger.log(Level.FINE, "Inserting new feature: {0}", sql);

                        //TODO: execute in batch to improve performance?
                        st.execute(sql);
                    }

                    //report the feature id as user data since we cant set the fid
                    String fid = featureType.getTypeName() + "." + encodeFID(nextKeyValues);
                    feature.getUserData().put("fid", fid);
                }

            //st.executeBatch();
            } catch (SQLException e) {
                throw (IOException) new IOException("Error inserting features").initCause(e);
            } finally {
                closeSafe(st);
            }
        }
    }

    /**
     * Updates an existing feature(s) in the database for a particular feature type / table.
     */
    protected void update(final SimpleFeatureType featureType, final List<AttributeDescriptor> attributes,
            final List<Object> values, final Filter filter, final Connection cx) throws IOException
    {
        update(featureType, attributes.toArray(new AttributeDescriptor[attributes.size()]),
                values.toArray(new Object[values.size()]), filter, cx);
    }

    /**
     * Updates an existing feature(s) in the database for a particular feature type / table.
     */
    protected void update(final SimpleFeatureType featureType, final AttributeDescriptor[] attributes,
            final Object[] values, final Filter filter, final Connection cx) throws IOException
    {
        if ((attributes == null) || (attributes.length == 0)) {
            Logger.warning("Update called with no attributes, doing nothing.");

            return;
        }

        if (dialect instanceof PreparedStatementSQLDialect) {
            try {
                final PreparedStatement ps = updateSQLPS(featureType, attributes, values, filter, cx);
                try {
                    ps.execute();
                }
                finally {
                    closeSafe( ps );
                }
            }
            catch (SQLException e) {
                throw (IOException) new IOException("Error occured updating features").initCause(e);
            }
        } else {
            try {
                final String sql = updateSQL(featureType, attributes, values, filter);
                Logger.log(Level.FINE, "Updating feature: {0}", sql);

                final Statement st = cx.createStatement();

                try {
                    st.execute(sql);
                } finally {
                    closeSafe(st);
                }
            } catch (SQLException e) {
                throw (IOException) new IOException("Error occured updating features").initCause(e);
            }
        }
    }

    /**
     * Deletes an existing feature in the database for a particular feature type / fid.
     */
    protected void delete(final SimpleFeatureType featureType, final String fid,
            final Connection cx) throws IOException
    {
        final Filter filter = filterFactory.id(Collections.singleton(filterFactory.featureId(fid)));
        delete(featureType, filter, cx);
    }

    /**
     * Deletes an existing feature(s) in the database for a particular feature type / table.
     */
    protected void delete(final SimpleFeatureType featureType, final Filter filter, final Connection cx)
            throws IOException {

        Statement st = null;
        try {
            if (dialect instanceof PreparedStatementSQLDialect) {
                st = deleteSQLPS(featureType, filter, cx);
                ((PreparedStatement) st).execute();
            } else {
                final String sql = deleteSQL(featureType, filter);
                Logger.log(Level.FINE, "Removing feature(s): {0}", sql);

                st = cx.createStatement();
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw (IOException) new IOException("Error occured calculating bounds").initCause(e);
        } finally {
            closeSafe(st);
        }
    }

    /**
     * Gets a database connection for the specified feature store.
     */
    protected final Connection getConnection(final JDBCState state) throws SQLException {
        // short circuit this state, all auto commit transactions are using the same
        if (state.getTransaction() == Transaction.AUTO_COMMIT) {
            final Connection cx = createConnection();
            try {
                cx.setAutoCommit(true);
            } catch (SQLException e) {
                // This exception should not occur.
                throw new AssertionError(e);
            }
            return cx;
        }

        // else we look to grab the connection from the state, and eventually create it
        // for the first time
        Connection cx = state.getConnection();
        if (cx == null) {
            synchronized (state) {
                //create a new connection
                cx = createConnection();

                //set auto commit to false, we know tx != auto commit
                try {
                    cx.setAutoCommit(false);
                } catch (SQLException e) {
                    // this exception should not occur.
                    throw new AssertionError(e);
                }

                //add connection state to the transaction
                state.setConnection(cx);
                state.getTransaction().putState(state, new JDBCTransactionState(cx, this));
            }
        }
        return cx;
    }

    /**
     * Creates a new connection.
     * <p>
     * Callers of this method should close the connection when done with it.
     * </p>.
     *
     */
    protected final Connection createConnection() throws SQLException {
        Logger.fine("CREATE CONNECTION");
        final Connection cx = getDataSource().getConnection();
        // isolation level is not set in the datastore, see
        // http://jira.codehaus.org/browse/GEOT-2021

        //call dialect callback to iniitalie the connection
        dialect.initializeConnection(cx);
        return cx;
    }

    /**
     * Releases an existing connection.
     */
    protected final void releaseConnection(final Connection cx, final JDBCState state) {
        //if the state is based off the AUTO_COMMIT transaction, close the
        // connection, otherwise wait until the transaction itself is closed to
        // close the connection
        if (state.getTransaction() == Transaction.AUTO_COMMIT) {
            closeSafe(cx);
        }
    }

    /**
     * Encodes a feature id from a primary key and result set values.
     */
    protected String encodeFID(final PrimaryKey pkey, final ResultSet rs)
            throws SQLException, IOException
    {
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

    /**
     * Gets the next value of a primary key.
     */
    protected List<Object> getNextValues(final PrimaryKey pkey, final Connection cx) throws SQLException, IOException {
        final ArrayList<Object> next = new ArrayList<Object>();
        for (PrimaryKeyColumn col : pkey.getColumns()) {
            next.add(getNextValue(col, pkey, cx));
        }
        return next;
    }

    /**
     * Gets the next value for the column of a primary key.
     */
    protected Object getNextValue(final PrimaryKeyColumn col, final PrimaryKey pkey, final Connection cx) throws SQLException, IOException {
        Object next = null;

        if (col instanceof AutoGeneratedPrimaryKeyColumn) {
            next = dialect.getNextAutoGeneratedValue(databaseSchema, pkey.getTableName(), col.getName(), cx);
        } else if (col instanceof SequencedPrimaryKeyColumn) {
            final String sequenceName = ((SequencedPrimaryKeyColumn) col).getSequenceName();
            next = dialect.getNextSequenceValue(databaseSchema, sequenceName, cx);
        } else {
            //try to calculate
            final Class t = col.getType();

            //is the column numeric?
            if (Number.class.isAssignableFrom(t)) {
                //is the column integral?
                if (t == Short.class || t == Integer.class || t == Long.class || BigInteger.class.isAssignableFrom(t) ||
                                                                                 BigDecimal.class.isAssignableFrom(t))
                {

                    final StringBuffer sql = new StringBuffer();
                    sql.append("SELECT MAX(");
                    dialect.encodeColumnName(col.getName(), sql);
                    sql.append(") + 1 FROM ");
                    encodeTableName(pkey.getTableName(), sql);

                    Logger.log(Level.FINE, "Getting next FID: {0}", sql);

                    final Statement st = cx.createStatement();
                    try {
                        final ResultSet rs = st.executeQuery(sql.toString());
                        try {
                            rs.next();
                            next = rs.getObject(1);

                            if (next == null) {
                                //this probably means there was no data in the table, set to 1
                                //TODO: probably better to do a count to check... but if this
                                // value already exists the db will throw an error when it tries
                                // to insert
                                next = 1;
                            }
                        } finally {
                            closeSafe(rs);
                        }
                    } finally {
                        closeSafe(st);
                    }
                }
            } else if (CharSequence.class.isAssignableFrom(t)) {
                //generate a random string
                next = SimpleFeatureBuilder.createDefaultFeatureId();
            }

            if (next == null) {
                throw new IOException("Cannot generate key value for column of type: " + t.getName());
            }
        }

        return next;
    }

    /**
     * Determines the next FID of a feature to be instered based on the primary key.
     */
    protected String getNextFID(final PrimaryKey pkey, final Connection cx) throws SQLException, IOException {
        final StringBuffer fid = new StringBuffer();
        for (PrimaryKeyColumn col : pkey.getColumns()) {
            final Object next = getNextValue(col, pkey, cx);
            fid.append(next);
        }
        return fid.toString();
    }

    //
    // SQL generation
    //
    /**
     * Generates a 'CREATE TABLE' sql statement.
     */
    protected String createTableSQL(final SimpleFeatureType featureType, final Connection cx) throws SQLException {
        //figure out the names and types of the columns
        final String[] columnNames = new String[featureType.getAttributeCount()];
        final Class[] classes = new Class[featureType.getAttributeCount()];
        String[] sqlTypeNames = null;

        //figure out which columns can not be null
        final boolean[] nillable = new boolean[featureType.getAttributeCount()];

        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeDescriptor attributeType = featureType.getDescriptor(i);

            //column name
            columnNames[i] = attributeType.getLocalName();

            //column type
            classes[i] = attributeType.getType().getBinding();

            //can be null?
            nillable[i] = attributeType.getMinOccurs() <= 0 || attributeType.isNillable();
        }

        sqlTypeNames = getSQLTypeNames(classes, cx);
        for (int i = 0; i < sqlTypeNames.length; i++) {
            if (sqlTypeNames[i] == null) {
                String msg = "Unable to map " + columnNames[i] + "( " + classes[i].getName() + ")";
                throw new SQLException(msg);
            }
        }

        return createTableSQL(featureType.getTypeName(), columnNames, sqlTypeNames, nillable, "fid", featureType);
    }

    /**
     * Ensures that that the specified transaction has access to features specified by a filter.
     * <p>
     * If any features matching the filter are locked, and the transaction does not have authorization
     * with respect to the lock, an exception is thrown.
     * </p>
     * @param featureType The feature type / table.
     * @param filter The filters.
     * @param tx The transaction.
     * @param cx The database connection.
     */
    protected void ensureAuthorization(final SimpleFeatureType featureType, final Filter filter,
            final Transaction tx, final Connection cx) throws IOException, SQLException
    {

        final Query query = new DefaultQuery(featureType.getTypeName(), filter, Query.NO_NAMES);

        Statement st = null;
        try {
            ResultSet rs = null;
            if (getSQLDialect() instanceof PreparedStatementSQLDialect) {
                st = selectSQLPS(featureType, query, cx);
                rs = ((PreparedStatement) st).executeQuery();
            } else {
                final String sql = selectSQL(featureType, query);
                Logger.fine(sql);
                st = cx.createStatement();
                rs = st.executeQuery(sql);
            }

            try {
                final PrimaryKey key = getPrimaryKey(featureType);
                final InProcessLockingManager lm = (InProcessLockingManager) getLockingManager();
                while (rs.next()) {
                    String fid = featureType.getTypeName() + "." + encodeFID(key, rs);
                    lm.assertAccess(featureType.getTypeName(), fid, tx);
                }
            } finally {
                closeSafe(rs);
            }
        } finally {
            closeSafe(st);
        }
    }

    /**
     * Helper method for creating geometry association table if it does not
     * exist.
     */
    protected void ensureAssociationTablesExist(final Connection cx) throws SQLException {
        // look for feature relationship table
        lookupMetadata(cx, createRelationshipTableSQL(cx), FEATURE_RELATIONSHIP_TABLE);

        // look for feature association table
        lookupMetadata(cx, createAssociationTableSQL(cx), FEATURE_ASSOCIATION_TABLE);

        // look up for geometry table
        lookupMetadata(cx, createGeometryTableSQL(cx), GEOMETRY_TABLE);

        // look up for multi geometry table
        lookupMetadata(cx, createMultiGeometryTableSQL(cx), MULTI_GEOMETRY_TABLE);

        // look up for metadata for geometry association table
        lookupMetadata(cx, createGeometryAssociationTableSQL(cx), GEOMETRY_ASSOCIATION_TABLE);
    }

    /**
     * Find in the metadata of the connection the property specified.
     */
    private void lookupMetadata(final Connection cx, final String sql, final String property) throws SQLException {
        final ResultSet tables = cx.getMetaData().getTables(null, databaseSchema, property, null);

        try {
            if (!tables.next()) {
                // does not exist, create it
                Logger.log(Level.FINE, "Creating geometry association table: {0}", sql);

                final Statement st = cx.createStatement();

                try {
                    st.execute(sql);
                } finally {
                    closeSafe(st);
                }
            }
        } finally {
            closeSafe(tables);
        }
    }

    /**
     * Creates the sql for the relationship table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     */
    protected String createRelationshipTableSQL(final Connection cx) throws SQLException {
        final String[] sqlTypeNames = getSQLTypeNames(new Class[]{String.class, String.class}, cx);
        final String[] columnNames = new String[]{"table", "col"};

        return createTableSQL(FEATURE_RELATIONSHIP_TABLE, columnNames, sqlTypeNames, null, null, null);
    }

    /**
     * Creates the sql for the association table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     */
    protected String createAssociationTableSQL(final Connection cx) throws SQLException {
        final String[] sqlTypeNames = getSQLTypeNames(new Class[]{
                    String.class, String.class, String.class, String.class
                }, cx);
        final String[] columnNames = new String[]{"fid", "rtable", "rcol", "rfid"};

        return createTableSQL(FEATURE_ASSOCIATION_TABLE, columnNames, sqlTypeNames, null, null, null);
    }

    /**
     * Creates the sql for the geometry table.
     *
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     */
    protected String createGeometryTableSQL(Connection cx) throws SQLException {
        final String[] sqlTypeNames = getSQLTypeNames(new Class[]{
                    String.class, String.class, String.class, String.class, Geometry.class
                }, cx);
        final String[] columnNames = new String[]{"id", "name", "description", "type", "geometry"};

        return createTableSQL(GEOMETRY_TABLE, columnNames, sqlTypeNames, null, null, null);
    }

    /**
     * Creates the sql for the multi_geometry table.
     *  <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     */
    protected String createMultiGeometryTableSQL(final Connection cx) throws SQLException {
        final String[] sqlTypeNames = getSQLTypeNames(new Class[]{String.class, String.class, Boolean.class}, cx);
        final String[] columnNames = new String[]{"id", "mgid", "ref"};

        return createTableSQL(MULTI_GEOMETRY_TABLE, columnNames, sqlTypeNames, null, null, null);
    }

    /**
     * Creates the sql for the relationship table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param table The table of the association
     * @param column The column of the association
     */
    protected String selectRelationshipSQL(final String table, final String column) {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("table", sql);
        sql.append(",");
        dialect.encodeColumnName("col", sql);

        sql.append(" FROM ");
        encodeTableName(FEATURE_RELATIONSHIP_TABLE, sql);

        if (table != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("table", sql);
            sql.append(" = ");
            dialect.encodeValue(table, String.class, sql);
        }

        if (column != null) {
            if (table == null) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("col", sql);
            sql.append(" = ");
            dialect.encodeValue(column, String.class, sql);
        }

        return sql.toString();
    }

    /**
     * Creates the prepared statement for a query against the relationship table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param table The table of the association
     * @param column The column of the association
     */
    protected PreparedStatement selectRelationshipSQLPS(final String table, final String column,
                                                        final Connection cx) throws SQLException
    {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("table", sql);
        sql.append(",");
        dialect.encodeColumnName("col", sql);

        sql.append(" FROM ");
        encodeTableName(FEATURE_RELATIONSHIP_TABLE, sql);

        if (table != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("table", sql);
            sql.append(" = ? ");
        }

        if (column != null) {
            if (table == null) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("col", sql);
            sql.append(" = ? ");
        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        if (table != null) {
            ps.setString(1, table);
        }
        if (column != null) {
            ps.setString(table != null ? 2 : 1, column);
        }
        return ps;
    }

    /**
     * Creates the sql for the association table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param fid The feature id of the association
     */
    protected String selectAssociationSQL(final String fid) {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("fid", sql);
        sql.append(",");
        dialect.encodeColumnName("rtable", sql);
        sql.append(",");
        dialect.encodeColumnName("rcol", sql);
        sql.append(", ");
        dialect.encodeColumnName("rfid", sql);

        sql.append(" FROM ");
        encodeTableName(FEATURE_ASSOCIATION_TABLE, sql);

        if (fid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("fid", sql);
            sql.append(" = ");
            dialect.encodeValue(fid, String.class, sql);
        }

        return sql.toString();
    }

    /**
     * Creates the prepared statement for the association table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param fid The feature id of the association
     */
    protected PreparedStatement selectAssociationSQLPS(final String fid, final Connection cx)
            throws SQLException {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("fid", sql);
        sql.append(",");
        dialect.encodeColumnName("rtable", sql);
        sql.append(",");
        dialect.encodeColumnName("rcol", sql);
        sql.append(", ");
        dialect.encodeColumnName("rfid", sql);

        sql.append(" FROM ");
        encodeTableName(FEATURE_ASSOCIATION_TABLE, sql);

        if (fid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("fid", sql);
            sql.append(" = ?");

        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        if (fid != null) {
            ps.setString(1, fid);
        }

        return ps;
    }

    /**
     * Creates the sql for a select from the geometry table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param gid The geometry id to select for, may be <code>null</code>
     *
     */
    protected String selectGeometrySQL(final String gid) {

        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("id", sql);
        sql.append(",");
        dialect.encodeColumnName("name", sql);
        sql.append(",");
        dialect.encodeColumnName("description", sql);
        sql.append(",");
        dialect.encodeColumnName("type", sql);
        sql.append(",");
        dialect.encodeColumnName("geometry", sql);
        sql.append(" FROM ");
        encodeTableName(GEOMETRY_TABLE, sql);

        if (gid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("id", sql);
            sql.append(" = ");
            dialect.encodeValue(gid, String.class, sql);
        }

        return sql.toString();
    }

    /**
     * Creates the prepared for a select from the geometry table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param gid The geometry id to select for, may be <code>null</code>
     *
     */
    protected PreparedStatement selectGeometrySQLPS(final String gid, final Connection cx) throws SQLException {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("id", sql);
        sql.append(",");
        dialect.encodeColumnName("name", sql);
        sql.append(",");
        dialect.encodeColumnName("description", sql);
        sql.append(",");
        dialect.encodeColumnName("type", sql);
        sql.append(",");
        dialect.encodeColumnName("geometry", sql);
        sql.append(" FROM ");
        encodeTableName(GEOMETRY_TABLE, sql);

        if (gid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("id", sql);
            sql.append(" = ?");
        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        if (gid != null) {
            ps.setString(1, gid);
        }

        return ps;
    }

    /**
     * Creates the sql for a select from the multi geometry table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param gid The geometry id to select for, may be <code>null</code>.
     */
    protected String selectMultiGeometrySQL(final String gid) {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("id", sql);
        sql.append(",");
        dialect.encodeColumnName("mgid", sql);
        sql.append(",");
        dialect.encodeColumnName("ref", sql);

        sql.append(" FROM ");
        encodeTableName(MULTI_GEOMETRY_TABLE, sql);

        if (gid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("id", sql);
            sql.append(" = ");
            dialect.encodeValue(gid, String.class, sql);
        }

        return sql.toString();
    }

    /**
     * Creates the prepared statement for a select from the multi geometry table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     * @param gid The geometry id to select for, may be <code>null</code>.
     */
    protected PreparedStatement selectMultiGeometrySQLPS(final String gid, final Connection cx)
            throws SQLException {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("id", sql);
        sql.append(",");
        dialect.encodeColumnName("mgid", sql);
        sql.append(",");
        dialect.encodeColumnName("ref", sql);

        sql.append(" FROM ");
        encodeTableName(MULTI_GEOMETRY_TABLE, sql);

        if (gid != null) {
            sql.append(" WHERE ");

            dialect.encodeColumnName("id", sql);
            sql.append(" = ?");
        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        if (gid != null) {
            ps.setString(1, gid);
        }

        return ps;
    }

    /**
     * Creates the sql for the geometry association table.
     * <p>
     * This method is only called when {@link JDBCDataStore#isAssociations()}
     * is true.
     * </p>
     */
    protected String createGeometryAssociationTableSQL(final Connection cx)
            throws SQLException {
        final String[] sqlTypeNames = getSQLTypeNames(new Class[]{
                    String.class, String.class, String.class, Boolean.class
                }, cx);
        final String[] columnNames = new String[]{"fid", "gname", "gid", "ref"};

        return createTableSQL(GEOMETRY_ASSOCIATION_TABLE, columnNames, sqlTypeNames, null, null, null);
    }

    /**
     * Creates the sql for a select from the geometry association table.
     * <p>
     * </p>
     * @param fid The fid to select for, may be <code>null</code>
     * @param gid The geometry id to select for, may be <code>null</code>
     * @param gname The geometry name to select for, may be <code>null</code>
     */
    protected String selectGeometryAssociationSQL(final String fid, final String gid, final String gname) {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("fid", sql);
        sql.append(",");
        dialect.encodeColumnName("gid", sql);
        sql.append(",");
        dialect.encodeColumnName("gname", sql);
        sql.append(",");
        dialect.encodeColumnName("ref", sql);

        sql.append(" FROM ");
        encodeTableName(GEOMETRY_ASSOCIATION_TABLE, sql);

        if (fid != null) {
            sql.append(" WHERE ");
            dialect.encodeColumnName("fid", sql);
            sql.append(" = ");
            dialect.encodeValue(fid, String.class, sql);
        }

        if (gid != null) {
            if (fid == null) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("gid", sql);
            sql.append(" = ");
            dialect.encodeValue(gid, String.class, sql);
        }

        if (gname != null) {
            if ((fid == null) && (gid == null)) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("gname", sql);
            sql.append(" = ");
            dialect.encodeValue(gname, String.class, sql);
        }

        return sql.toString();
    }

    /**
     * Creates the prepared statement for a select from the geometry association table.
     * <p>
     * </p>
     * @param fid The fid to select for, may be <code>null</code>
     * @param gid The geometry id to select for, may be <code>null</code>
     * @param gname The geometry name to select for, may be <code>null</code>
     */
    protected PreparedStatement selectGeometryAssociationSQLPS(final String fid, final String gid,
            final String gname, final Connection cx) throws SQLException
    {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        dialect.encodeColumnName("fid", sql);
        sql.append(",");
        dialect.encodeColumnName("gid", sql);
        sql.append(",");
        dialect.encodeColumnName("gname", sql);
        sql.append(",");
        dialect.encodeColumnName("ref", sql);

        sql.append(" FROM ");
        encodeTableName(GEOMETRY_ASSOCIATION_TABLE, sql);

        if (fid != null) {
            sql.append(" WHERE ");
            dialect.encodeColumnName("fid", sql);
            sql.append(" = ? ");
        }

        if (gid != null) {
            if (fid == null) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("gid", sql);
            sql.append(" = ? ");
        }

        if (gname != null) {
            if ((fid == null) && (gid == null)) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            dialect.encodeColumnName("gname", sql);
            sql.append(" = ?");
        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        if (fid != null) {
            ps.setString(1, fid);
        }

        if (gid != null) {
            ps.setString(fid != null ? 2 : 1, gid);
        }

        if (gname != null) {
            ps.setString(fid != null ? (gid != null ? 3 : 2) : (gid != null ? 2 : 1), gname);
        }

        return ps;
    }

    /**
     * Helper method for building a 'CREATE TABLE' sql statement.
     */
    private String createTableSQL(final String tableName, final String[] columnNames, final String[] sqlTypeNames,
            boolean[] nillable, String pkeyColumn, SimpleFeatureType featureType)
    {
        //build the create table sql
        final StringBuffer sql = new StringBuffer();
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
            sql.append(" ");

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

                dialect.encodeColumnType(sqlTypeNames[i] + "(" + length + ")", sql);
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
     * Searches the attribute descriptor restrictions in an attempt to determine
     * the length of the specified varchar column.
     */
    private Integer findVarcharColumnLength(final AttributeDescriptor att) {
        for (Filter r : att.getType().getRestrictions()) {
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
            Integer sqlType = getMapping(clazz);

            if (sqlType == null) {
                Logger.warning("No sql type mapping for: " + clazz);
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
            final String sqlTypeName = getSqlTypeToSqlTypeNameOverrides().get(sqlType);
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
        final Map<Integer, String> overrides = getSqlTypeToSqlTypeNameOverrides();
        for (int i = 0; i < sqlTypes.length; i++) {
            final String override = overrides.get(sqlTypes[i]);
            if (override != null) {
                sqlTypeNames[i] = override;
            }
        }

        return sqlTypeNames;
    }

    /**
     * Generates a 'SELECT p1, p2, ... FROM ... WHERE ...' statement.
     *
     * @param featureType
     *            the feature type that the query must return (may contain less
     *            attributes than the native one)
     * @param attributes
     *            the properties queried, or {@link Query#ALL_NAMES} to gather
     *            all of them
     * @param query
     *            the query to be run. The type name and property will be ignored, as they are
     *            supposed to have been already embedded into the provided feature type
     * @param sort
     *            sort conditions
     */
    protected String selectSQL(final SimpleFeatureType featureType, final Query query) throws IOException {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");

        //column names

        //primary key
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            dialect.encodeColumnName(col.getName(), sql);
            sql.append(",");
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

            sql.append(",");
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
                final SimpleFeatureType fullSchema = getSchema(featureType.getTypeName());
                final FilterToSQL toSQL = createFilterToSQL(fullSchema);
                sql.append(" ").append(toSQL.encodeToString(filter));
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
     * Encodes the sort-by portion of an sql query
     * @param featureType
     * @param sort
     * @param key
     * @param sql
     * @throws IOException
     */
    void sort(final SimpleFeatureType featureType, final SortBy[] sort, final PrimaryKey key,
            final StringBuffer sql) throws IOException
    {
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
                        sql.append(",");
                    }
                } else {
                    dialect.encodeColumnName(getPropertyName(featureType, sortBy.getPropertyName()),
                            sql);
                    sql.append(order);
                    sql.append(",");
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
     * @param attributes
     *            the properties queried, or {@link Query#ALL_NAMES} to gather
     *            all of them
     * @param query
     *            the query to be run. The type name and property will be ignored, as they are
     *            supposed to have been already embedded into the provided feature type
     * @param cx
     *            The database connection to be used to create the prepared
     *            statement
     */
    protected PreparedStatement selectSQLPS(final SimpleFeatureType featureType, final Query query,
                                            final Connection cx) throws SQLException, IOException
    {

        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");

        // primary key
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            dialect.encodeColumnName(col.getName(), sql);
            sql.append(",");
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

            sql.append(",");
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
                final SimpleFeatureType fullSchema = getSchema(featureType.getTypeName());
                toSQL = createPreparedFilterToSQL(fullSchema);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        //sorting
        sort(featureType, query.getSortBy(), key, sql);

        // finally encode limit/offset, if necessary
        applyLimitOffset(sql, query);

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY,
                                                                         ResultSet.CONCUR_READ_ONLY);
        ps.setFetchSize(fetchSize);

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }

    /**
     * Helper method for setting the values of the WHERE class of a prepared statement.
     *
     */
    protected void setPreparedFilterValues(final PreparedStatement ps, final PreparedFilterToSQL toSQL,
                                           final int offset, final Connection cx) throws SQLException
    {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

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
            if (Logger.isLoggable(Level.FINE)) {
                Logger.fine((i + 1) + " = " + value);
            }
        }
    }

    /**
     * Helper method for executing a property name against a feature type.
     * <p>
     * This method will fall back on {@link PropertyName#getPropertyName()} if
     * it does not evaulate against the feature type.
     * </p>
     */
    protected String getPropertyName(final SimpleFeatureType featureType, final PropertyName propertyName) {
        final AttributeDescriptor att = (AttributeDescriptor) propertyName.evaluate(featureType);

        if (att != null) {
            return att.getLocalName();
        }

        return propertyName.getPropertyName();
    }

    /**
     * Generates a 'SELECT' sql statement which selects bounds.
     *
     * @param featureType The feature type / table.
     * @param query Specifies which features are to be used for the bounds computation
     *              (and in particular uses filter, start index and max features)
     */
    protected String selectBoundsSQL(final SimpleFeatureType featureType, final Query query) throws SQLException {
        final StringBuffer sql = new StringBuffer();

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
            StringBuffer sb = new StringBuffer();
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
                                                  final Connection cx) throws SQLException
    {

        final StringBuffer sql = new StringBuffer();

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
            final StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            buildEnvelopeAggregates(featureType, sb);
            sb.append("FROM (");
            // wrap the existing query
            sql.insert(0, sb.toString());
            sql.append(")");
            dialect.encodeTableAlias("GT2_BOUNDS_", sql);
        }


        Logger.fine(sql.toString());
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
    void buildEnvelopeAggregates(final SimpleFeatureType featureType, final StringBuffer sql) {
        //walk through all geometry attributes and build the query
        for (Iterator a = featureType.getAttributeDescriptors().iterator(); a.hasNext();) {
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
     * Generates a 'SELECT count(*) FROM' sql statement. In case limit/offset is
     * used, we'll need to apply them on a <code>select *<code>
     * as limit/offset usually alters the number of returned rows
     * (and a count returns just one), and then count on the result of that first select
     */
    protected String selectCountSQL(final SimpleFeatureType featureType, final Query query) throws SQLException {
        final StringBuffer sql = new StringBuffer();

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
                sql.append(" ").append(toSQL.encodeToString(filter));
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
                                                 final Connection cx) throws SQLException
    {
        final StringBuffer sql = new StringBuffer();

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

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }

    /**
     * Generates a 'DELETE FROM' sql statement.
     */
    protected String deleteSQL(final SimpleFeatureType featureType, final Filter filter) throws SQLException {
        final StringBuffer sql = new StringBuffer();

        sql.append("DELETE FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                final FilterToSQL toSQL = createFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        return sql.toString();
    }

    /**
     * Generates a 'DELETE FROM' prepared statement.
     */
    protected PreparedStatement deleteSQLPS(final SimpleFeatureType featureType, final Filter filter,
                                            final Connection cx) throws SQLException
    {
        final StringBuffer sql = new StringBuffer();

        sql.append("DELETE FROM ");
        encodeTableName(featureType.getTypeName(), sql);

        PreparedFilterToSQL toSQL = null;
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                toSQL = createPreparedFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        Logger.fine(sql.toString());
        final PreparedStatement ps = cx.prepareStatement(sql.toString());

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, 0, cx);
        }

        return ps;
    }

    /**
     * Generates a 'INSERT INFO' sql statement.
     * @throws IOException
     */
    protected String insertSQL(final SimpleFeatureType featureType, final SimpleFeature feature,
                               final List keyValues, final Connection cx) throws IOException
    {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ");
        encodeTableName(featureType.getTypeName(), sql);

        //column names
        sql.append(" ( ");

        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            dialect.encodeColumnName(featureType.getDescriptor(i).getLocalName(), sql);
            sql.append(",");
        }

        //primary key values
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            //only include if its non auto generating
            if (!(col instanceof AutoGeneratedPrimaryKeyColumn)) {
                dialect.encodeColumnName(col.getName(), sql);
                sql.append(",");
            }
        }
        sql.setLength(sql.length() - 1);

        //values
        sql.append(" ) VALUES ( ");

        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            final AttributeDescriptor att = featureType.getDescriptor(i);
            final Class binding = att.getType().getBinding();

            final Object value = feature.getAttribute(att.getLocalName());

            if (value == null) {
                if (!att.isNillable()) {
                    //TODO: throw an exception
                }

                sql.append("null");
            } else {
                if (Geometry.class.isAssignableFrom(binding)) {
                    final Geometry g = (Geometry) value;
                    final int srid = getGeometrySRID(g, att);
                    dialect.encodeGeometryValue(g, srid, sql);
                } else {
                    dialect.encodeValue(value, binding, sql);
                }
            }

            sql.append(",");
        }
        for (int i = 0; i < key.getColumns().size(); i++) {
            final PrimaryKeyColumn col = key.getColumns().get(i);

            //only include if its non auto generating
            if (!(col instanceof AutoGeneratedPrimaryKeyColumn)) {
                    final Object value = keyValues.get(i);
                    dialect.encodeValue(value, col.getType(), sql);
                    sql.append(",");
            }
        }
        sql.setLength(sql.length() - 1);

        sql.append(")");

        return sql.toString();
    }

    /**
     * Generates a 'INSERT INFO' prepared statement.
     */
    protected PreparedStatement insertSQLPS(final SimpleFeatureType featureType, final SimpleFeature feature,
            final List keyValues, final Connection cx) throws IOException, SQLException
    {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ");
        encodeTableName(featureType.getTypeName(), sql);

        // column names
        sql.append(" ( ");

        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            dialect.encodeColumnName(featureType.getDescriptor(i).getLocalName(), sql);
            sql.append(",");
        }

        // primary key values
        final PrimaryKey key = getPrimaryKey(featureType);

        for (PrimaryKeyColumn col : key.getColumns()) {
            //only include if its non auto generating
            if (!(col instanceof AutoGeneratedPrimaryKeyColumn)) {
                dialect.encodeColumnName(col.getName(), sql);
                sql.append(",");
            }
        }
        sql.setLength(sql.length() - 1);

        // values
        sql.append(" ) VALUES ( ");
        for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
            // geometries might need special treatment, delegate to the dialect
            if (att instanceof GeometryDescriptor) {
                final Geometry geometry = (Geometry) feature.getAttribute(att.getName());
                dialect.prepareGeometryValue(geometry, getDescriptorSRID(att), att.getType().getBinding(), sql);
            } else {
                sql.append("?");
            }
            sql.append(",");
        }
        for (PrimaryKeyColumn col : key.getColumns()) {
            //only include if its non auto generating
            if (!(col instanceof AutoGeneratedPrimaryKeyColumn)) {
                sql.append("?").append(",");
            }
        }

        sql.setLength(sql.length() - 1);
        sql.append(")");
        Logger.log(Level.FINE, "Inserting new feature with ps: {0}", sql);

        //create the prepared statement
        final PreparedStatement ps = cx.prepareStatement(sql.toString());

        //set the attribute values
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            final AttributeDescriptor att = featureType.getDescriptor(i);
            final Class binding = att.getType().getBinding();

            final Object value = feature.getAttribute(att.getLocalName());
            if (value == null) {
                if (!att.isNillable()) {
                    //TODO: throw an exception
                }
            }

            if (Geometry.class.isAssignableFrom(binding)) {
                final Geometry g = (Geometry) value;
                final int srid = getGeometrySRID(g, att);
                dialect.setGeometryValue(g, srid, binding, ps, i + 1);
            } else {
                dialect.setValue(value, binding, ps, i + 1, cx);
            }
            if (Logger.isLoggable(Level.FINE)) {
                Logger.fine((i + 1) + " = " + value);
            }
        }

        //set the key values
        int i = featureType.getAttributeCount();
        for (int j = 0; j < key.getColumns().size(); j++) {
            final PrimaryKeyColumn col = key.getColumns().get(j);
            //only include if its non auto generating
            if (!(col instanceof AutoGeneratedPrimaryKeyColumn)) {
                //get the next value for the column
                //Object value = getNextValue( col, key, cx );
                final Object value = keyValues.get(j);
                dialect.setValue(value, col.getType(), ps, i + 1, cx);
                i++;
                if (Logger.isLoggable(Level.FINE)) {
                    Logger.fine((i) + " = " + value);
                }
            }
        }

        return ps;
    }

    /**
     * Looks up the geometry srs by trying a number of heuristics. Returns -1 if all attempts
     * at guessing the srid failed.
     */
    protected int getGeometrySRID(final Geometry g, final AttributeDescriptor descriptor) throws IOException {
        int srid = getDescriptorSRID(descriptor);

        if (g == null) {
            return srid;
        }

        // check for srid in the jts geometry then
        if (srid <= 0 && g.getSRID() > 0) {
            srid = g.getSRID();
        }

        // check if the geometry has anything
        if (srid <= 0) {
            // check for crs object
            final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) g.getUserData();

            if (crs != null) {
                try {
                    final Integer candidate = CRS.lookupEpsgCode(crs, false);
                    if (candidate != null) {
                        srid = candidate;
                    }
                } catch (Exception e) {
                    // ok, we tried...
                }
            }
        }

        return srid;
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
     * Generates an 'UPDATE' sql statement.
     */
    protected String updateSQL(final SimpleFeatureType featureType, final AttributeDescriptor[] attributes,
            final Object[] values, final Filter filter) throws IOException, SQLException
    {
        final BasicSQLDialect dialect = (BasicSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ");
        encodeTableName(featureType.getTypeName(), sql);

        sql.append(" SET ");

        for (int i = 0; i < attributes.length; i++) {
            dialect.encodeColumnName(attributes[i].getLocalName(), sql);
            sql.append(" = ");

            if (Geometry.class.isAssignableFrom(attributes[i].getType().getBinding())) {
                    final Geometry g = (Geometry) values[i];
                    final int srid = getGeometrySRID(g, attributes[i]);
                    dialect.encodeGeometryValue(g, srid, sql);
            } else {
                dialect.encodeValue(values[i], attributes[i].getType().getBinding(), sql);
            }

            sql.append(",");
        }

        sql.setLength(sql.length() - 1);
        sql.append(" ");

        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                final FilterToSQL toSQL = createFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        return sql.toString();
    }

    /**
     * Generates an 'UPDATE' prepared statement.
     */
    protected PreparedStatement updateSQLPS(final SimpleFeatureType featureType,
            final AttributeDescriptor[] attributes, final Object[] values, final Filter filter,
            final Connection cx) throws IOException, SQLException
    {
        final PreparedStatementSQLDialect dialect = (PreparedStatementSQLDialect) getSQLDialect();

        final StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ");
        encodeTableName(featureType.getTypeName(), sql);

        sql.append(" SET ");

        for (int i = 0; i < attributes.length; i++) {
            final AttributeDescriptor att = attributes[i];
            dialect.encodeColumnName(att.getLocalName(), sql);
            sql.append(" = ");

            // geometries might need special treatment, delegate to the dialect
            if (attributes[i] instanceof GeometryDescriptor) {
                final Geometry geometry = (Geometry) values[i];
                final Class<?> binding = att.getType().getBinding();
                dialect.prepareGeometryValue(geometry, getDescriptorSRID(att), binding, sql);
            } else {
                sql.append("?");
            }
            sql.append(",");
        }
        sql.setLength(sql.length() - 1);
        sql.append(" ");

        PreparedFilterToSQL toSQL = null;
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            try {
                toSQL = createPreparedFilterToSQL(featureType);
                sql.append(" ").append(toSQL.encodeToString(filter));
            } catch (FilterToSQLException e) {
                throw new SQLException(e);
            }
        }

        final PreparedStatement ps = cx.prepareStatement(sql.toString());
        Logger.log(Level.FINE, "Updating features with prepared statement: {0}", sql);

        int i = 0;
        for (; i < attributes.length; i++) {
            final AttributeDescriptor att = attributes[i];
            final Class binding = att.getType().getBinding();
            if (Geometry.class.isAssignableFrom(binding)) {
                final Geometry g = (Geometry) values[i];
                dialect.setGeometryValue(g, getDescriptorSRID(att), binding, ps, i + 1);
            } else {
                dialect.setValue(values[i], binding, ps, i + 1, cx);
            }
            if (Logger.isLoggable(Level.FINE)) {
                Logger.fine((i + 1) + " = " + values[i]);
            }
        }

        if (toSQL != null) {
            setPreparedFilterValues(ps, toSQL, i, cx);
        //for ( int j = 0; j < toSQL.getLiteralValues().size(); j++, i++)  {
        //    Object value = toSQL.getLiteralValues().get( j );
        //    Class binding = toSQL.getLiteralTypes().get( j );
        //
        //    dialect.setValue( value, binding, ps, i+1, cx );
        //    if ( LOGGER.isLoggable( Level.FINE ) ) {
        //        LOGGER.fine( (i+1) + " = " + value );
        //}
        }

        return ps;
    }

    /**
     * Creates a new instance of a filter to sql encoder.
     * <p>
     * The <tt>featureType</tt> may be null but it is not recommended. Such a
     * case where this may neccessary is when a literal needs to be encoded in
     * isolation.
     * </p>
     */
    protected FilterToSQL createFilterToSQL(final SimpleFeatureType featureType) {
        return initializeFilterToSQL(((BasicSQLDialect) dialect).createFilterToSQL(), featureType);
    }

    /**
     * Creates a new instance of a filter to sql encoder to be
     * used in a prepared statement.
     *
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
            } catch (IOException e) {
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
                public Object[] getPKAttributes(String FID)
                        throws IOException {
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
     * Helper method to encode table name which checks if a schema is set and
     * prefixes the table name with it.
     */
    protected void encodeTableName(final String tableName, final StringBuffer sql) {
        if (databaseSchema != null) {
            dialect.encodeSchemaName(databaseSchema, sql);
            sql.append(".");
        }

        dialect.encodeTableName(tableName, sql);
    }

    /**
     * Helper method for setting the gml:id of a geometry as user data.
     */
    protected void setGmlProperties(final Geometry g, final String gid, final String name, final String description) {
        // set up the user data
        Map userData = null;

        if (g.getUserData() != null) {
            if (g.getUserData() instanceof Map) {
                userData = (Map) g.getUserData();
            } else {
                userData = new HashMap();
                userData.put(g.getUserData().getClass(), g.getUserData());
            }
        } else {
            userData = new HashMap();
        }

        if (gid != null) {
            userData.put("gml:id", gid);
        }

        if (name != null) {
            userData.put("gml:name", name);
        }

        if (description != null) {
            userData.put("gml:description", description);
        }

        g.setUserData(userData);
    }

    /**
     * Applies the limit/offset elements to the query if they are specified
     * and if the dialect supports them
     * @param sql The sql to be modified
     * @param the query that holds the limit and offset parameters
     */
    void applyLimitOffset(final StringBuffer sql, final Query query) {
        if (checkLimitOffset(query)) {
            final Integer offset = query.getStartIndex();
            final int limit = query.getMaxFeatures();
            dialect.applyLimitOffset(sql, limit, offset != null ? offset : 0);
        }
    }

    /**
     * Checks if the query needs limit/offset treatment
     * @param query
     * @return true if the query needs limit/offset treatment and if the sql dialect can do that natively
     */
    boolean checkLimitOffset(final Query query) {
        // if we cannot, don't bother checking the query
        if (!dialect.isLimitOffsetSupported()) {
            return false;
        }

        // the check the query has at least a non default value for limit/offset
        final Integer offset = query.getStartIndex();
        final int limit = query.getMaxFeatures();
        return limit != Integer.MAX_VALUE || (offset != null && offset > 0);
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
            Logger.warning(msg);

            if (Logger.isLoggable(Level.FINER)) {
                Logger.log(Level.FINER, msg, e);
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
            Logger.warning(msg);

            if (Logger.isLoggable(Level.FINER)) {
                Logger.log(Level.FINER, msg, e);
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
//            System.out.println("Closing connection " + System.identityHashCode(cx));
            cx.close();
            Logger.fine("CLOSE CONNECTION");
        } catch (SQLException e) {
            String msg = "Error occurred closing connection";
            Logger.warning(msg);

            if (Logger.isLoggable(Level.FINER)) {
                Logger.log(Level.FINER, msg, e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (dataSource != null) {
            Logger.severe("There's code using JDBC based datastore and " +
                    "not disposing them. This may lead to temporary loss of database connections. " +
                    "Please make sure all data access code calls DataStore.dispose() " +
                    "before freeing all references to it");
            dispose();
        }
        super.finalize();
    }

    @Override
    public void dispose() {
        if (dataSource instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) dataSource;
                dataSource = null;
                mds.close();
            } catch (SQLException e) {
                // it's ok, we did our best..
                Logger.log(Level.FINE, "Could not close dataSource", e);
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
     * @param param  {@link Hints#GEOMETRY_GENERALIZATION} or {@link Hints#GEOMETRY_SIMPLIFICATION}
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
    protected void encodeGeometryColumn(final GeometryDescriptor gatt, final StringBuffer sql,
                                        final Hints hints)
    {

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
}
