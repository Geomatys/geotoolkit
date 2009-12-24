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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.sql.DataSource;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.jdbc.datasource.DBCPDataSource;
import org.geotoolkit.data.query.Query;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;


/**
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public final class JDBCDataStore extends AbstractDataStore {

    private final DBCPDataSource source;
    private final SQLDialect dialect;
    private final String namespace;
    private final String databaseSchema;

    private final Map<Name,FeatureType> names = new HashMap<Name, FeatureType>();
    private Set<Name> nameCache = null;

    JDBCDataStore(DBCPDataSource source, SQLDialect dialect, String databaseSchema, String namespace){
        if(source == null){
            throw new NullPointerException("Data source can not be null;");
        }
        if(dialect == null){
            throw new NullPointerException("Dialect can not be null.");
        }
        if(namespace == null){
            throw new NullPointerException("Namespace can not be null.");
        }

        this.source = source;
        this.dialect = dialect;
        this.namespace = namespace;
        this.databaseSchema = databaseSchema;
    }

    public SQLDialect getDialect() {
        return dialect;
    }

    public DataSource getDataSource(){
        return source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        Set<Name> ref = nameCache;
        if(ref == null){
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
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
     * Helper method to encode table name which checks if a schema is set and
     * prefixes the table name with it.
     */
    protected void encodeTableName(final String tableName, final StringBuilder sql) {
        if (databaseSchema != null) {
            dialect.encodeSchemaName(databaseSchema, sql);
            sql.append(".");
        }

        dialect.encodeTableName(tableName, sql);
    }

    ////////////////////////////////////////////////////////////////////////////
    // other utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

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

}
