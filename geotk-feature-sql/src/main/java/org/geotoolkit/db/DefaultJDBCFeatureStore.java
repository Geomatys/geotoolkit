/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.sql.DataSource;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Version;
import static org.geotoolkit.db.JDBCFeatureStore.JDBC_PROPERTY_RELATION;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.dialect.SQLQueryBuilder;
import org.geotoolkit.db.reverse.*;
import org.geotoolkit.db.session.JDBCSession;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.visitor.CRSAdaptorVisitor;
import org.geotoolkit.filter.visitor.FIDFixVisitor;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.storage.feature.query.DefaultQueryCapabilities;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.storage.feature.query.QueryCapabilities;
import org.geotoolkit.storage.feature.query.SQLQuery;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultJDBCFeatureStore extends JDBCFeatureStore{

    private static enum EditMode{
        UPDATE,
        INSERT,
        UPDATE_AND_INSERT
    }

    protected static final QueryCapabilities DEFAULT_CAPABILITIES = new DefaultQueryCapabilities(false, false,
            new String[]{org.geotoolkit.storage.feature.query.Query.GEOTK_QOM, CUSTOM_SQL});

    protected final GeometryFactory geometryFactory = new GeometryFactory();
    protected final FilterFactory filterFactory = FilterUtilities.FF;

    private final DataBaseModel dbmodel;
    private final String factoryId;
    private DataSource source;
    private SQLDialect dialect;
    private String baseSchema;

    //number of records to retrieve with each db call.
    private final int fetchSize;
    private SQLQueryBuilder queryBuilder;


    public DefaultJDBCFeatureStore(final ParameterValueGroup params,final String factoryId){
        super(params);
        this.factoryId = factoryId;

        fetchSize = parameters.getValue(AbstractJDBCProvider.FETCHSIZE);
        final boolean simpleTypes = parameters.getValue(AbstractJDBCProvider.SIMPLETYPE);
        dbmodel = new DataBaseModel(this, simpleTypes);

        try{
            baseSchema = parameters.getValue(AbstractJDBCProvider.SCHEMA);
            if (baseSchema != null && baseSchema.isEmpty()) {
                baseSchema = null;
            }
        }catch(ParameterNotFoundException ex){
            //parameter migth not exist on all database implementations
        }
    }

    public FilterFactory getFilterFactory() {
        return filterFactory;
    }

    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(typeName);
        return key != null && !(key.isNull());
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(factoryId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    public void setDialect(SQLDialect dialect) {
        ArgumentChecks.ensureNonNull("dialect", dialect);
        this.dialect = dialect;
    }

    protected SQLQueryBuilder getQueryBuilder(){
        if(queryBuilder == null){
            queryBuilder = new SQLQueryBuilder(this);
        }
        return queryBuilder;
    }

    /**
     * Get database version.
     */
    public Version getVersion() throws DataStoreException {
        try {
            return dialect.getVersion(getDatabaseSchema());
        } catch (SQLException e) {
            throw new DataStoreException("Error occured calculating bounds", e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataSource getDataSource() {
        return source;
    }

    public void setDataSource(DataSource ds){
        this.source = ds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDatabaseSchema() {
        return baseSchema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataBaseModel getDatabaseModel() {
        return dbmodel;
    }

    /**
     * Provide a session with transaction control.
     *
     * @return Session never null
     */
    @Override
    public Session createSession(boolean async, org.geotoolkit.version.Version version) {
        return new JDBCSession(this, async, version);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        ensureOpen();
        return dbmodel.getNames();
    }

    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName);
        return dbmodel.getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Query query) throws DataStoreException, MismatchedFeatureException {
        if (query instanceof SQLQuery) {
            final SQLQuery sqlQuery = (SQLQuery) query;
            final String sql = sqlQuery.getStatement();
            Connection cnx = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                cnx = getDataSource().getConnection();
                stmt = cnx.createStatement();
                rs = stmt.executeQuery(sql);
                return getDatabaseModel().analyzeResult(rs, sqlQuery.getName());
            } catch (SQLException ex) {
                throw new DataStoreException(ex);
            }finally{
                JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx,stmt,rs);
            }
        }
        return super.getFeatureType(query);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return DEFAULT_CAPABILITIES;
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        return getFeatureReader(query, null);
    }

    public FeatureReader getFeatureReader(final Query query, final Connection cnx) throws DataStoreException {

        if (query instanceof SQLQuery) {
            return getSQLFeatureReader((SQLQuery)query,cnx);

        } else if (query instanceof org.geotoolkit.storage.feature.query.Query) {
            final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query)query;
            final FeatureReader reader = getQOMFeatureReader(gquery,cnx);

            //take care of potential hints, like removing primary keys
            final QueryBuilder qb = new QueryBuilder();
            qb.setTypeName("remaining");
            qb.setHints(gquery.getHints());
            return FeatureStreams.subset(reader, qb.buildQuery());

        } else {
            throw new UnsupportedQueryException();
        }
    }

    /**
     * Get reader with geotk query model.
     */
    private FeatureReader getQOMFeatureReader(final org.geotoolkit.storage.feature.query.Query query, Connection cnx) throws DataStoreException {

        final String dbSchemaName = getDatabaseSchema();
        final FeatureType type = dbmodel.getFeatureType(query.getTypeName());
        final String tableName = type.getName().tip().toString();
        TableMetaModel tableMeta = null;
        if (dbSchemaName == null) {
            // Try to handle empty schema name given at configuration
            for (final SchemaMetaModel scheme : dbmodel.getSchemaMetaModels()) {
                final TableMetaModel tableMetaTemp = scheme.getTable(tableName);
                if (tableMetaTemp != null) {
                    tableMeta = tableMetaTemp;
                    break;
                }
            }
        } else {
            tableMeta = dbmodel.getSchemaMetaModel(getDatabaseSchema()).getTable(tableName);
        }

        if (tableMeta == null) {
            throw new DataStoreException("Unable to get table "+ tableName +" in the database.");
        }

        final FeatureType tableType = tableMeta.getType(TableMetaModel.View.ALLCOMPLEX).build();
        final PrimaryKey pkey = dbmodel.getPrimaryKey(query.getTypeName());


        //replace any PropertyEqualsTo in true ID filters
        Filter baseFilter = query.getSelection();
        baseFilter = (Filter) FIDFixVisitor.INSTANCE.visit(baseFilter);

        //split the filter between what can be send and must be handle by code
        final Filter[] divided = getDialect().splitFilter(baseFilter,tableType);
        Filter preFilter = divided[0];
        Filter postFilter = divided[1];

        //ensure spatial filters are in featuretype geometry crs
        preFilter = (Filter) new CRSAdaptorVisitor(tableType).visit(preFilter);

        // rebuild a new query with the same params, but just the pre-filter
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(preFilter);
        if(query.getResolution() != null){ //attach resampling in hints; used later by postgis dialect
            builder.getHints().add(new Hints(RESAMPLING, query.getResolution()));
        }
        final org.geotoolkit.storage.feature.query.Query preQuery = builder.buildQuery();

        final FeatureType baseType = getFeatureType(query.getTypeName());

        // Build the feature type returned by this query. Also build an eventual extra feature type
        // containing the attributes we might need in order to evaluate the post filter
        final FeatureType queryFeatureType;
        final FeatureType returnedFeatureType;
        if(query.retrieveAllProperties()) {
            returnedFeatureType = queryFeatureType = (FeatureType) baseType;
        } else {
            //TODO BUG here, query with filter on a not returned geometry field crash
            returnedFeatureType = (FeatureType) FeatureTypeExt.createSubType(tableType, query.getPropertyNames());
            final FilterAttributeExtractor extractor = new FilterAttributeExtractor(tableType);
            extractor.visit(postFilter, null);
            final GenericName[] extraAttributes = extractor.getAttributeNames();

            final List<GenericName> allAttributes = new ArrayList<>();
            for(String str : query.getPropertyNames()){
                allAttributes.add(type.getProperty(str).getName());
            }
            for (GenericName extraAttribute : extraAttributes) {
                if(!allAttributes.contains(extraAttribute)) {
                    allAttributes.add(extraAttribute);
                }
            }

            //ensure we have the primarykeys
            pkLoop :
            for(ColumnMetaModel pkc : pkey.getColumns()){
                final String pkcName = pkc.getName();
                for(GenericName n : allAttributes){
                    if(n.tip().toString().equals(pkcName)){
                        continue pkLoop;
                     }
                 }
                //add the pk attribut
                allAttributes.add(baseType.getProperty(pkcName).getName());
             }
            final GenericName[] allAttributeArray = allAttributes.toArray(new GenericName[allAttributes.size()]);
            queryFeatureType = (FeatureType) FeatureTypeExt.createSubType(tableType, allAttributeArray);
        }

        final String sql;

        //we gave him the connection, he must not release it
        final boolean release = (cnx == null);
        if(cnx==null){
            try {
                cnx = getDataSource().getConnection();
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        FeatureReader reader;
        // current implementation does not support LinkOperation attribute
        // so for now if we got one, we let the filter be evaluated in java and not with a sql query
        if (containsOperation(preQuery, baseType)) {
            try {
                sql = getQueryBuilder().selectSQL(baseType, QueryBuilder.all(baseType.getName()));
                reader = new JDBCFeatureReader(this, sql, baseType, cnx, release, null);
                FeatureStreams.subset(reader, query);
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        } else {
            try {
                sql = getQueryBuilder().selectSQL(queryFeatureType, preQuery);
                reader = new JDBCFeatureReader(this, sql, queryFeatureType, cnx, release, null);
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        // if post filter, wrap it
        if (postFilter != null && postFilter != Filter.include()) {
            reader = FeatureStreams.filter(reader, postFilter);
        }

        //if we need to constraint type
        if(!returnedFeatureType.equals(queryFeatureType)){
            reader = FeatureStreams.decorate(reader, new ViewMapper(type, query.getPropertyNames()), query.getHints());
        }
        return reader;
    }

    /**
     * return true if one of the requestes property is an operation.
     */
    private boolean containsOperation(org.geotoolkit.storage.feature.query.Query query, FeatureType type) {
        if (query.retrieveAllProperties()) {
            for (PropertyType prop : type.getProperties(true)) {
                if (prop instanceof Operation) {
                    return true;
                }
            }
        } else {
            for (String propertyName : query.getPropertyNames()) {
                PropertyType prop = type.getProperty(propertyName);
                if (prop instanceof Operation) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get reader with SQL query.
     */
    private FeatureReader getSQLFeatureReader(final SQLQuery query, Connection cnx) throws DataStoreException {

        final String sql = query.getStatement();

        //we gave him the connection, he must not release it
        final boolean release = (cnx == null);
        if(cnx==null){
            try {
                cnx = getDataSource().getConnection();
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        try {
            final FeatureType ft = getFeatureType(query);

            final JDBCFeatureReader reader = new JDBCFeatureReader(this, sql, ft, cnx, release, null);
            return reader;
        } catch (MismatchedFeatureException | SQLException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, FeatureStoreRuntimeException {
        if (query instanceof SQLQuery) {
            //can not optimize this query
            //iterator is closed by method
            return FeatureStoreUtilities.calculateEnvelope(getSQLFeatureReader((SQLQuery)query,null));
        }
        return super.getEnvelope(query);
    }

    @Override
    public long getCount(Query query) throws DataStoreException {
        if (query instanceof SQLQuery) {
            //iterator is closed by method
            return FeatureStoreUtilities.calculateCount(getSQLFeatureReader((SQLQuery)query,null));
        }
        return super.getCount(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        typeCheck(gquery.getTypeName());
        final Filter filter = gquery.getSelection();
        final Hints hints = gquery.getHints();
        try {
            if (Filter.exclude().equals(filter)) {
               //append mode
               return getFeatureWriterInternal(gquery.getTypeName(), Filter.exclude(), EditMode.INSERT, null, hints);
            } else {
                //update mode
                return getFeatureWriterInternal(gquery.getTypeName(), filter, EditMode.UPDATE_AND_INSERT, null, hints);
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    private FeatureWriter getFeatureWriterInternal(final String typeName, Filter baseFilter,
            final EditMode mode, Connection cnx, final Hints hints) throws DataStoreException, IOException {

        if(!isWritable(typeName)){
            throw new DataStoreException("Type "+ typeName + " is not writeable.");
        }

        final FeatureType baseType = getFeatureType(typeName);
        final PrimaryKey pkey = dbmodel.getPrimaryKey(typeName);

        //replace any PropertyEqualsTo in true ID filters
        baseFilter = (Filter) FIDFixVisitor.INSTANCE.visit(baseFilter);

        //split the filter between what can be send and must be handle by code
        final Filter[] divided = getDialect().splitFilter(baseFilter,baseType);
        Filter preFilter = divided[0];
        Filter postFilter = divided[1];

        //ensure spatial filters are in featuretype geometry crs
        preFilter = (Filter) new CRSAdaptorVisitor(baseType).visit(preFilter);

        //we gave him the connection, he must not release it
        final boolean release = (cnx == null);
        if(cnx==null){
            try {
                cnx = getDataSource().getConnection();
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        FeatureWriter writer;
        try {

            //check for insert only
            if ( EditMode.INSERT == mode ) {
                //build up a statement for the content, inserting only so we dont want
                //the query to return any data ==> Filter.exclude()
                final org.geotoolkit.storage.feature.query.Query queryNone = QueryBuilder.filtered(typeName, Filter.exclude());
                final String sql = getQueryBuilder().selectSQL(baseType, queryNone);
                getLogger().fine(sql);
                return new JDBCFeatureWriterInsert(this,sql,baseType,cnx,release,hints);
            }

            // build up a statement for the content
            final org.geotoolkit.storage.feature.query.Query preQuery = QueryBuilder.filtered(typeName, preFilter);
            final String sql = getQueryBuilder().selectSQL(baseType, preQuery);
            getLogger().fine(sql);

            if(EditMode.UPDATE == mode) {
                writer = new JDBCFeatureWriterUpdate(this,sql,baseType,cnx,release,hints);
            }else{
                //update insert case
                writer = new JDBCFeatureWriterUpdateInsert(this,sql,baseType,cnx,release,hints);
            }
        } catch (SQLException e) {
            // close the connection
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),(release)?cnx:null);
            // now we can safely rethrow the exception
            throw new DataStoreException(e);
        }

        //check for post filter and wrap accordingly
        if ( postFilter != null && postFilter != Filter.include() ) {
            writer = FeatureStreams.filter(writer, postFilter);
        }
        return writer;
    }

    /**
     * Updates an existing feature(s) in the database for a particular feature type / table.
     */
    protected void updateSingle(final FeatureType featureType, final Map<String,Object> changes,
            final Filter filter, final Connection cx) throws DataStoreException{
        if ((changes == null) || (changes.isEmpty())) {
            getLogger().warning("Update called with no attributes, doing nothing.");
            return;
        }

        Statement stmt = null;
        try {
            final String sql = getQueryBuilder().updateSQL(featureType, changes, filter);
            getLogger().log(Level.FINE, "Updating feature: {0}", sql);
            stmt = cx.createStatement();
            stmt.execute(sql);

            if (cx.getAutoCommit()) {
                fireFeaturesUpdated(featureType.getName(), null);
            }
        } catch (SQLException e) {
            throw new DataStoreException("Error occured updating features",e);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),null,stmt,null);
        }
    }

    @Override
    public void refreshMetaModel() throws IllegalNameException {
        dbmodel.clearCache();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Complexe feature types will be decomposed in flat types then relations
     * will be rebuilded.
     */
    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        ensureOpen();
        final GenericName typeName = featureType.getName();
        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }

        Connection cnx = null;
        Statement stmt = null;
        String sql = null;
        final List<FeatureType> flatTypes = new ArrayList<>();
        final List<TypeRelation> relations = new ArrayList<>();
        try {
            cnx = getDataSource().getConnection();
            cnx.setAutoCommit(false);
            stmt = cnx.createStatement();

            //we must first decompose the feature type in flat types, then recreate relations
            decompose(featureType, flatTypes, relations);

            //rebuild flat types
            for(FeatureType flatType : flatTypes){
                sql = getQueryBuilder().createTableSQL(flatType,cnx);
                stmt.execute(sql);
                dialect.postCreateTable(getDatabaseSchema(), flatType, cnx);
            }

            //rebuild relations
            if(!relations.isEmpty()){
                //refresh the model to find primary keys
                dbmodel.clearCache();

                for(TypeRelation relation : relations){
                    final String propertyName = relation.property.getName().tip().toString();
                    FeatureAssociationRole result = (FeatureAssociationRole) relation.property;
                    final int minOccurs = result.getMinimumOccurs();
                    final int maxOccurs = result.getMaximumOccurs();

                    if (maxOccurs<=1) {
                        //place relation on the children with a unique constraint
                        final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(Integer.class);
                        atb.setName(propertyName);
                        final SQLQueryBuilder b = getQueryBuilder();
                        final String addColumn = b.alterTableAddColumnSQL(relation.child, atb.build(), cnx);
                        stmt.execute(addColumn);
                        final String addForeignKey = b.alterTableAddForeignKey(relation.child, propertyName,
                                relation.parent.getName(), "fid", true);
                        stmt.execute(addForeignKey);
                        final String addUnique = b.alterTableAddIndex(relation.child,atb.getName().tip().toString());
                        stmt.execute(addUnique);
                    } else {
                        //place relation on the children
                        final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(Integer.class);
                        atb.setName(propertyName);
                        final SQLQueryBuilder b = getQueryBuilder();
                        final String addColumn = b.alterTableAddColumnSQL(relation.child, atb.build(), cnx);
                        stmt.execute(addColumn);
                        final String addForeignKey = b.alterTableAddForeignKey(relation.child, propertyName,
                                relation.parent.getName(), "fid", true);
                        stmt.execute(addForeignKey);
                    }
                }
            }
            cnx.commit();
        } catch (SQLException ex) {
            if(cnx!=null){
                try {
                    cnx.rollback();
                } catch (SQLException ex1) {
                    getLogger().log(Level.WARNING, ex1.getMessage(), ex1);
                }
            }
            throw new DataStoreException("Failed to create table "+typeName.tip() + "," + ex.getMessage() + "\n Query: "+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx,stmt,null);
        }

        // reset the type name cache, will be recreated when needed.
        dbmodel.clearCache();
    }

    @Override
    public void updateFeatureType(final FeatureType newft) throws DataStoreException {
        GenericName typeName = newft.getName();
        ensureOpen();
        final FeatureType oldft = getFeatureType(typeName.toString());

        //we only handle adding or removing columns
        final List<PropertyType> toRemove = new ArrayList<>();
        final List<PropertyType> toAdd = new ArrayList<>();

        toRemove.addAll(oldft.getProperties(true));
        toRemove.removeAll(newft.getProperties(true));
        toAdd.addAll(newft.getProperties(true));
        toAdd.removeAll(oldft.getProperties(true));

        //TODO : this is not perfect, we loose datas, we should update the column type when possible.
        Connection cnx = null;
        try{
            cnx = getDataSource().getConnection();

            for(PropertyType remove : toRemove){
                final String sql = getQueryBuilder().alterTableDropColumnSQL(oldft, remove, cnx);
                final Statement stmt = cnx.createStatement();
                try {
                    stmt.execute(sql);
                } finally {
                    JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
                }
            }

            for(PropertyType add : toAdd){
                final String sql = getQueryBuilder().alterTableAddColumnSQL(oldft, (AttributeType)add, cnx);
                final Statement stmt = cnx.createStatement();
                try {
                    stmt.execute(sql);
                } finally {
                    JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
                }
            }

        } catch (final SQLException ex) {
            throw new DataStoreException("Failed updating table " + typeName.tip() + ", " + ex.getMessage(), ex);
        } finally{
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx);
        }

        // reset the type name cache, will be recreated when needed.
        dbmodel.clearCache();
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException{
        ensureOpen();
        final FeatureType featureType = getFeatureType(typeName);
        final Set<FeatureType> visited = new HashSet<>();
        recursiveDelete(featureType,visited);
    }

    private void recursiveDelete(FeatureType featureType, Set<FeatureType> visited) throws DataStoreException{

        //search properties which are complex types
        for(PropertyType pt : featureType.getProperties(true)){
            if(pt instanceof DBRelationOperation){
                final RelationMetaModel relation = ((DBRelationOperation) pt).getRelation();
                if(!relation.isImported()){
                    //a table point toward this one.
                    final FeatureAssociationRole refType = (FeatureAssociationRole) (((DBRelationOperation)pt).getResult());
                    recursiveDelete(refType.getValueType(), visited);
                }

            }else if(pt instanceof FeatureAssociationRole){
                recursiveDelete( ((FeatureAssociationRole)pt).getValueType(),visited);
            }
        }

        if(visited.contains(featureType)) return;
        visited.add(featureType);

        final GenericName typeName = featureType.getName();
        Connection cnx = null;
        Statement stmt = null;
        String sql = null;
        try {
            cnx = getDataSource().getConnection();
            stmt = cnx.createStatement();
            sql = getQueryBuilder().dropSQL(featureType);

            stmt.execute(sql);
            // reset the type name cache, will be recreated when needed.
            dbmodel.clearCache();

        } catch (SQLException ex) {
            throw new DataStoreException("Failed to drop table " + typeName.tip() + "," + ex.getMessage() + "\n Query: " + sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx,stmt,null);
        }
    }

    /**
     * Decompose given type in flat types for table creation.
     */
    private void decompose(FeatureType type, List<FeatureType> types, List<TypeRelation> relations) throws DataStoreException{

        final GenericName dbName = NamesExt.create(type.getName().tip().toString());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(dbName);
        for(PropertyType pt : type.getProperties(true)){
            if (pt instanceof FeatureAssociationRole) {
                final FeatureAssociationRole asso = (FeatureAssociationRole) pt;
                final FeatureType attType = asso.getValueType();
                final TypeRelation relation = new TypeRelation();
                relation.property = asso;
                relation.parent = type;
                relation.child = attType;
                relations.add(relation);
                decompose(attType, types, relations);

            } else {
                ftb.addProperty(pt);
            }
        }

        final FeatureType flatType = ftb.build();
        if(!types.contains(flatType)){
            types.add(flatType);
        }
    }

    private static class TypeRelation {
        FeatureAssociationRole property;
        FeatureType parent;
        FeatureType child;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Fallback on reader/write iterator methods ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ResourceId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return addFeatures(groupName, newFeatures, null, hints);
    }

    public final List<ResourceId> addFeatures(String groupName, Collection<? extends Feature> newFeatures,
            Connection cnx, Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, cnx, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        updateFeatures(groupName, filter, values, null);
    }

    public void updateFeatures(final String groupName, final Filter filter,
            final Map<String, ? extends Object> values, Connection cnx) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values, cnx);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        removeFeatures(groupName, filter, null);
    }

    public void removeFeatures(final String groupName, final Filter filter, Connection cnx) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter, cnx);
    }

    protected void insert(final Collection<? extends Feature> features, final FeatureType featureType,
            final Connection cx) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(featureType.getName().toString());

        // we do this in a synchronized block because we need to do two queries,
        // first to figure out what the id will be, then the insert statement
        synchronized (this) {
            Statement st = null;
            try {
                st = cx.createStatement();
                final Object[] nextKeyValues = key.nextPrimaryKeyValues(this, cx);
                final String sql = getQueryBuilder().insertSQL(featureType, features, nextKeyValues, cx);
                st.executeUpdate(sql);

                if (cx.getAutoCommit()) {
                    fireFeaturesAdded(featureType.getName(), null);
                }
            } catch (SQLException e) {
                throw new DataStoreException("Error inserting features",e);
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(getLogger(),st);
            }
        }
    }

    protected void insert(final Feature feature, final FeatureType featureType,
            final Connection cx) throws DataStoreException {

        //decompose feature and make multiple inserts
        final List<InsertRelation> flats = decompose(feature);
        for(InsertRelation hierarchy : flats){
            final Feature parent = hierarchy.parent;
            final Feature flat = hierarchy.child;
            final RelationMetaModel relation = hierarchy.relation;
            //resolve parent id fields.
            if(parent!=null && relation!=null){
                final Object parentValue = parent.getProperty(relation.getForeignColumn()).getValue();
                flat.setPropertyValue(relation.getCurrentColumn(),parentValue);

//                    for(Property prop : flat.getProperties()){
//                        final RelationMetaModel relation = (RelationMetaModel) prop.getDescriptor().getUserData().get(JDBC_PROPERTY_RELATION);
//                        if(relation==null) continue;
//                        final Object parentValue = parent.getProperty(relation.getForeignColumn()).getValue();
//                        flat.getProperty(relation.getCurrentColumn()).setValue(parentValue);
//                    }
            }

            Object fid = insertFlat(flat, flat.getType(), cx);
            // we pass the fid to the root
            if (flat.getType().getName().equals(feature.getType().getName())) {
                feature.setPropertyValue(AttributeConvention.IDENTIFIER, fid);
            }
        }
    }

    private Object insertFlat(final Feature feature, final FeatureType featureType,
            final Connection cx) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(featureType.getName().toString());

        // we do this in a synchronized block because we need to do two queries,
        // first to figure out what the id will be, then the insert statement
        synchronized (this) {
            Statement stmt = null;
            String sql = null;
            try {
                stmt = cx.createStatement();

                //figure out what the next fid will be
                final Object[] nextKeyValues = key.nextPrimaryKeyValues(this, cx);

                //this technic must be generalize to all primary keys, must revisite tests for this
                sql = getQueryBuilder().insertSQL(featureType, feature, nextKeyValues, cx);
                getLogger().log(Level.FINE, "Inserting new feature: {0}", sql);

                if(nextKeyValues.length == 0 || nextKeyValues[0] == null){
                    stmt.execute(sql,Statement.RETURN_GENERATED_KEYS);
                    ResultSet rs = stmt.getGeneratedKeys();
                    rs.next();
                    final Object id = rs.getObject(1);
                    nextKeyValues[0] = id;
                    rs.close();
                    feature.setPropertyValue(key.getColumns().get(0).getName(),id);
                }else{
                    stmt.execute(sql);
                }

                if (cx.getAutoCommit()) {
                    fireFeaturesAdded(featureType.getName(), null);
                }

                return nextKeyValues[0];
            //st.executeBatch();
            } catch (SQLException ex) {
                throw new DataStoreException("Failed to intert features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
            }
        }
    }

    /**
     * Decompose feature in flat features, they are ordered in appropriate insertion order.
     */
    private List<InsertRelation> decompose(Feature candidate) throws DataStoreException{
        final List<InsertRelation> flats = new ArrayList<InsertRelation>();
        decompose(null,candidate,null, flats);
        return flats;
    }

    private void decompose(Feature parent, Feature candidate,
            RelationMetaModel relation, List<InsertRelation> flats) throws DataStoreException{
        //decompose main type
        final FeatureType featuretype = candidate.getType();
        final TableMetaModel table = dbmodel.getSchemaMetaModel(getDatabaseSchema()).getTable(featuretype.getName().tip().toString());
        final FeatureType flatType = table.getType(TableMetaModel.View.SIMPLE_FEATURE_TYPE).build();
        final Feature flat = flatType.newInstance();
        FeatureExt.copy(candidate, flat, false);

        //find the reverted relation
        if(relation!=null){
            final PropertyType property = flatType.getProperty(relation.getForeignColumn());
            relation = FeatureExt.getCharacteristicValue(property, JDBC_PROPERTY_RELATION.getName().toString(), null);
        }

        final InsertRelation rt = new InsertRelation();
        rt.parent = parent;
        rt.child = flat;
        rt.relation = relation;
        flats.add(rt);

        //decompose sub complex types
//        for(Property prop : candidate.getProperties()){
//            if(prop instanceof ComplexAttribute){
//                final RelationMetaModel cr = (RelationMetaModel) prop.getDescriptor().getUserData().get(JDBC_PROPERTY_RELATION);
//                decompose(flat,(ComplexAttribute)prop, cr, flats);
//            }
//        }
    }

    protected void update(final FeatureType featureType, final Map<String,? extends Object> changes,
            final Filter filter, final Connection cx) throws DataStoreException{
        if(changes==null || changes.isEmpty()){
            //do nothing
            return;
        }

        Statement stmt = null;
        String sql = null;
        try {
            sql = getQueryBuilder().updateSQL(featureType, changes, filter);
            stmt = cx.createStatement();
            stmt.execute(sql);

            if (cx.getAutoCommit()) {
                fireFeaturesUpdated(featureType.getName(), null);
            }
        } catch (SQLException ex) {
            throw new DataStoreException("Failed to update features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
        }
    }

    protected void delete(final FeatureType featureType, final Filter filter, final Connection cx)
            throws DataStoreException {

        Statement stmt = null;
        String sql = null;
        try {
            sql = getQueryBuilder().deleteSQL(featureType, filter);
            stmt = cx.createStatement();
            stmt.execute(sql);

            if (cx.getAutoCommit()) {
                fireFeaturesDeleted(featureType.getName(), null);
            }
        } catch (SQLException ex) {
            throw new DataStoreException("Failed to delete features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // other utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @return list of ids of the features added.
     */
    protected List<ResourceId> handleAddWithFeatureWriter(final String groupName, final Collection<? extends Feature> newFeatures,
            Connection cnx, final Hints hints) throws DataStoreException{
        try(final FeatureWriter writer = getFeatureWriterInternal(groupName, Filter.exclude(), EditMode.INSERT, cnx, hints)){
            return FeatureStoreUtilities.write(writer, newFeatures);
        }catch(FeatureStoreRuntimeException | IOException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     */
    protected void handleUpdateWithFeatureWriter(final String groupName, final Filter filter,
            final Map<String, ? extends Object> values, Connection cnx) throws DataStoreException {

        try(FeatureWriter writer = getFeatureWriterInternal(groupName, filter, EditMode.UPDATE, cnx, null)) {

            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Map.Entry<String,? extends Object> entry : values.entrySet()){
                    f.setPropertyValue(entry.getKey(),entry.getValue());
                }
                writer.write();
            }
        } catch(FeatureStoreRuntimeException | IOException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convenient method to handle adding features operation by using the
     * FeatureWriter.
     */
    protected void handleRemoveWithFeatureWriter(final String groupName, final Filter filter, Connection cnx) throws DataStoreException {

        try(FeatureWriter writer = getFeatureWriterInternal(groupName,filter,EditMode.UPDATE,cnx,null)) {

            while(writer.hasNext()){
                writer.next();
                writer.remove();
            }
        } catch(FeatureStoreRuntimeException | IOException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Check the feature store is open.
     */
    private void ensureOpen() throws DataStoreException{
        if(source == null){
            throw new DataStoreException("JDBC Feature store has been disposed already.");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (source != null) {
            getLogger().log(Level.WARNING,
                    "JDBC feature store has not been disposed properly. "+
                    "This may cause connexions to remain open, "+
                    "dispose feature stores when not needed anymore before dereferencing.");
            close();
        }
        super.finalize();
    }

    @Override
    public void close() {
        if (source instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) source;
                source = null;
                mds.close();
            } catch (SQLException e) {
                getLogger().log(Level.WARNING, "Failed to close datasource.", e);
            }
        }
    }
}
