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

import org.geotoolkit.db.session.JDBCSession;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Version;
import org.geotoolkit.data.*;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.query.TextStatement;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.dialect.SQLQueryBuilder;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.DataBaseModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.db.reverse.RelationMetaModel;
import org.geotoolkit.db.reverse.TableMetaModel;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.filter.visitor.CRSAdaptorVisitor;
import org.geotoolkit.filter.visitor.FIDFixVisitor;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultJDBCFeatureStore extends AbstractFeatureStore implements JDBCFeatureStore{
    
    private static enum EditMode{
        UPDATE,
        INSERT,
        UPDATE_AND_INSERT
    }

    protected static final QueryCapabilities DEFAULT_CAPABILITIES = new DefaultQueryCapabilities(false, false, new String[]{Query.GEOTK_QOM, CUSTOM_SQL});
    
    protected final GeometryFactory geometryFactory = new GeometryFactory();
    protected final FilterFactory filterFactory = FactoryFinder.getFilterFactory(null);
    
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
        
        fetchSize = (Integer)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.FETCHSIZE, params).getValue();
        final boolean simpleTypes = (Boolean)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.SIMPLETYPE, params).getValue();        
        dbmodel = new DataBaseModel(this, simpleTypes); 
        
        try{
            baseSchema = (String)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.SCHEMA, params).getValue();
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
    public boolean isWritable(final Name typeName) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(typeName);
        return key != null && !(key.isNull());
    }
    
    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(factoryId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDefaultNamespace() {
        return super.getDefaultNamespace();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Logger getLogger() {
        return super.getLogger();
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
     * @return
     * @throws DataStoreException 
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
     * @param async
     * @param version
     * @return Session never null
     */
    @Override
    public Session createSession(boolean async, org.geotoolkit.version.Version version) {
        return new JDBCSession(this, async, version);
    }
    
    @Override
    public Set<Name> getNames() throws DataStoreException {
        ensureOpen();
        return dbmodel.getNames();
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        ensureOpen();
        return dbmodel.getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Query query) throws DataStoreException, SchemaException {
        if(CUSTOM_SQL.equalsIgnoreCase(query.getLanguage())){
            final TextStatement txt = (TextStatement) query.getSource();
            final String sql = txt.getStatement();
            Connection cnx = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                cnx = getDataSource().getConnection();
                stmt = cnx.createStatement();
                rs = stmt.executeQuery(sql);
                return getDatabaseModel().analyzeResult(rs, query.getTypeName().getLocalPart());
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
        final Source source = query.getSource();
        
        final FeatureReader reader;
        if(source instanceof Selector){
            reader = getQOMFeatureReader(query,cnx);
        }else if(source instanceof TextStatement){
            reader = getSQLFeatureReader(query,cnx);
        }else{
            throw new DataStoreException("Unsupported source type : " + source);
        }
        
        //take care of potential hints, like removing primary keys
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(new DefaultName("remaining"));
        qb.setHints(query.getHints());
        return handleRemaining(reader, qb.buildQuery());
    }

    /**
     * Get reader with geotk query model.
     * @param query
     * @return FeatureReader
     * @throws DataStoreException 
     */
    private FeatureReader getQOMFeatureReader(final Query query, Connection cnx) throws DataStoreException {
        
        if(!query.isSimple()){
            throw new DataStoreException("Query is not simple.");
        }
        
        final FeatureType baseType = getFeatureType(query.getTypeName());
        final PrimaryKey pkey = dbmodel.getPrimaryKey(query.getTypeName());
                
        
        //replace any PropertyEqualsTo in true ID filters
        Filter baseFilter = query.getFilter();
        baseFilter = (Filter) baseFilter.accept(new FIDFixVisitor(), null);
        
        //split the filter between what can be send and must be handle by code
        final Filter[] divided = getDialect().splitFilter(baseFilter,baseType);
        Filter preFilter = divided[0];
        Filter postFilter = divided[1];
        
        //ensure spatial filters are in featuretype geometry crs
        preFilter = (Filter)preFilter.accept(new CRSAdaptorVisitor(baseType),null);
        
        // rebuild a new query with the same params, but just the pre-filter
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(preFilter);
        if(query.getResolution() != null){ //attach resampling in hints; used later by postgis dialect
            builder.getHints().add(new Hints(RESAMPLING, query.getResolution()));
        }
        final Query preQuery = builder.buildQuery();
        
        // Build the feature type returned by this query. Also build an eventual extra feature type
        // containing the attributes we might need in order to evaluate the post filter
        final FeatureType queryFeatureType;
        final FeatureType returnedFeatureType;
        if(query.retrieveAllProperties()) {
            returnedFeatureType = queryFeatureType = baseType;
        } else {
            returnedFeatureType = FeatureTypeBuilder.retype(baseType, query.getPropertyNames());
            final FilterAttributeExtractor extractor = new FilterAttributeExtractor(baseType);
            postFilter.accept(extractor, null);
            final Name[] extraAttributes = extractor.getAttributeNames();
            final List<Name> allAttributes = new ArrayList<Name>(Arrays.asList(query.getPropertyNames()));
            for (Name extraAttribute : extraAttributes) {
                if(!allAttributes.contains(extraAttribute)) {
                    allAttributes.add(extraAttribute);
                }
            }

            //ensure we have the primarykeys
            pkLoop :
            for(ColumnMetaModel pkc : pkey.getColumns()){
                final String pkcName = pkc.getName();
                for(Name n : allAttributes){
                    if(n.getLocalPart().equals(pkcName)){
                        continue pkLoop;
                     }
                 }
                //add the pk attribut
                allAttributes.add(baseType.getDescriptor(pkcName).getName());
             }

            final Name[] allAttributeArray = allAttributes.toArray(new Name[allAttributes.size()]);
            queryFeatureType = FeatureTypeBuilder.retype(baseType, allAttributeArray);
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
        try {
            sql = getQueryBuilder().selectSQL(queryFeatureType, preQuery);
            reader = new JDBCFeatureReader(this, sql, queryFeatureType, cnx, release, null);
        } catch (SQLException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        
        
        // if post filter, wrap it
        if (postFilter != null && postFilter != Filter.INCLUDE) {
            reader = GenericFilterFeatureIterator.wrap(reader, postFilter);
        }

        //if we need to reproject data
        final CoordinateReferenceSystem reproject = query.getCoordinateSystemReproject();
        if(reproject != null && !CRS.equalsIgnoreMetadata(reproject,baseType.getCoordinateReferenceSystem())){
            try {
                reader = GenericReprojectFeatureIterator.wrap(reader, reproject,query.getHints());
            } catch (FactoryException ex) {
                throw new DataStoreException(ex);
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }

        //if we need to constraint type
        if(!returnedFeatureType.equals(queryFeatureType)){
            reader = GenericRetypeFeatureIterator.wrap(reader, returnedFeatureType, query.getHints());
        }

        return reader;
    }
    
    /**
     * Get reader with SQL query.
     * @param query
     * @return FeatureReader
     * @throws DataStoreException 
     */
    private FeatureReader getSQLFeatureReader(final Query query, Connection cnx) throws DataStoreException {

        final TextStatement stmt = (TextStatement) query.getSource();
        final String sql = stmt.getStatement();

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
        } catch (SchemaException ex) {
            throw new DataStoreException(ex);
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, FeatureStoreRuntimeException {
        if(CUSTOM_SQL.equalsIgnoreCase(query.getLanguage())){
            //can not optimize this query
            //iterator is closed by method
            return FeatureStoreUtilities.calculateEnvelope(getSQLFeatureReader(query,null));
        }
        return super.getEnvelope(query);
    }
    
    @Override
    public long getCount(Query query) throws DataStoreException {
        if(CUSTOM_SQL.equalsIgnoreCase(query.getLanguage())){
            //iterator is closed by method
            return FeatureStoreUtilities.calculateCount(getSQLFeatureReader(query,null));
        }
        return super.getCount(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        return getFeatureWriter(typeName, filter, null, hints);
    }
    
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, 
            final Connection cnx, final Hints hints) throws DataStoreException {
        try {
            return getFeatureWriterInternal(typeName, filter, EditMode.UPDATE_AND_INSERT, cnx, hints);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(final Name typeName, final Hints hints) throws DataStoreException {
        return getFeatureWriterAppend(typeName, null, hints);
    }
    
    public FeatureWriter getFeatureWriterAppend(final Name typeName, final Connection cnx, final Hints hints) throws DataStoreException {
        try {
            return getFeatureWriterInternal(typeName, Filter.EXCLUDE, EditMode.INSERT, cnx, hints);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    private FeatureWriter getFeatureWriterInternal(final Name typeName, Filter baseFilter,
            final EditMode mode, Connection cnx, final Hints hints) throws DataStoreException, IOException {

        if(!isWritable(typeName)){
            throw new DataStoreException("Type "+ typeName + " is not writeable.");
        }

        final FeatureType baseType = getFeatureType(typeName);
        final PrimaryKey pkey = dbmodel.getPrimaryKey(typeName);
        
        //replace any PropertyEqualsTo in true ID filters
        baseFilter = (Filter) baseFilter.accept(new FIDFixVisitor(), null);
        
        //split the filter between what can be send and must be handle by code
        final Filter[] divided = getDialect().splitFilter(baseFilter,baseType);
        Filter preFilter = divided[0];
        Filter postFilter = divided[1];
        
        //ensure spatial filters are in featuretype geometry crs
        preFilter = (Filter)preFilter.accept(new CRSAdaptorVisitor(baseType),null);

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
                //the query to return any data ==> Filter.EXCLUDE
                final Query queryNone = QueryBuilder.filtered(typeName, Filter.EXCLUDE);
                final String sql = getQueryBuilder().selectSQL(baseType, queryNone);
                getLogger().fine(sql);
                return new JDBCFeatureWriterInsert(this,sql,baseType,cnx,release,hints);
            }


            // build up a statement for the content
            final Query preQuery = QueryBuilder.filtered(typeName, preFilter);
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
        if ( postFilter != null && postFilter != Filter.INCLUDE ) {
            writer = GenericFilterFeatureIterator.wrap(writer, postFilter);
        }
        return writer;
    }
    
    /**
     * Updates an existing feature(s) in the database for a particular feature type / table.
     */
    protected void updateSingle(final FeatureType featureType, final Map<AttributeDescriptor,Object> changes,
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
        } catch (SQLException e) {
            throw new DataStoreException("Error occured updating features",e);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),null,stmt,null);
        }
        fireFeaturesUpdated(featureType.getName(), null);
    }
    
    @Override
    public void refreshMetaModel() {
        dbmodel.clearCache();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Complexe feature types will be decomposed in flat types then relations
     * will be rebuilded.
     * 
     * @param typeName
     * @param featureType
     * @throws DataStoreException 
     */
    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        ensureOpen();
        
        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }
        if(!featureType.getName().equals(typeName)){
            throw new DataStoreException("JDBC datastore can only hold typename same as feature type name.");
        }
        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }

        Connection cnx = null;
        Statement stmt = null;
        String sql = null;
        try {
            cnx = getDataSource().getConnection();
            cnx.setAutoCommit(false);
            stmt = cnx.createStatement();
            
            //we must first decompose the feature type in flat types, then recreate relations
            final List<FeatureType> flatTypes = new ArrayList<FeatureType>();
            final List<TypeRelation> relations = new ArrayList<TypeRelation>();
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
                    final String baseTypeName = relation.type.getName().getLocalPart();
                    final String propertyName = relation.property.getName().getLocalPart();
                    final int minOccurs = relation.property.getMinOccurs();
                    final int maxOccurs = relation.property.getMaxOccurs();

                    if(relation.property.getType() instanceof AssociationType){
                        final AssociationType assType = (AssociationType) relation.property.getType();
                        final String targetTypeName = assType.getRelatedType().getName().getLocalPart();
                        throw new DataStoreException("Association property not supported");
                        
                    }else if(relation.property.getType() instanceof ComplexType){
                        final String targetTypeName = relation.property.getType().getName().getLocalPart();
                        final PrimaryKey targetKey = dbmodel.getPrimaryKey(new DefaultName(getDefaultNamespace(), targetTypeName));
                        final PrimaryKey sourceKey = dbmodel.getPrimaryKey(relation.type.getName());
                        if(targetKey.getColumns().size() != 1 || sourceKey.getColumns().size() != 1){
                            throw new DataStoreException("Multiple key column relations not supported.");
                        }
                        final ColumnMetaModel sourceColumnMeta = sourceKey.getColumns().get(0);
                        final ColumnMetaModel targetColumnMeta = targetKey.getColumns().get(0);
                        final FeatureType targetType = dbmodel.getFeatureType(
                                new DefaultName(getDefaultNamespace(), targetColumnMeta.getTable()));
                        
                        
                        //we create an relation in the opposite direction
                        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                        atb.setName(propertyName);
                        atb.setBinding(sourceColumnMeta.getJavaType());
                        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                        adb.setName(propertyName);
                        adb.setNillable(true);
                        adb.setMinOccurs(0);
                        adb.setMaxOccurs(1);
                        adb.setType(atb.buildType());
                        final PropertyDescriptor pdesc = adb.buildDescriptor();

                        //create the property
                        sql = getQueryBuilder().alterTableAddColumnSQL(targetType, pdesc, cnx);
                        stmt.execute(sql);
                        //add the foreign key relation
                        sql = getQueryBuilder().alterTableAddForeignKey(
                                targetType, propertyName, relation.type, sourceColumnMeta.getName(), true);
                        stmt.execute(sql);
                        
                        if(maxOccurs==1){
                            //we add a unique index, equivalent to a 0:1 relation
                            sql = getQueryBuilder().alterTableAddIndex(targetType, propertyName);
                            stmt.execute(sql);
                        }
                        
                    }else{
                        throw new DataStoreException("Unsupported relation type "+relation.property.getType().getClass());
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
            throw new DataStoreException("Failed to create table "+typeName.getLocalPart()+","+ex.getMessage()+"\n Query : "+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx,stmt,null);
        }

        // reset the type name cache, will be recreated when needed.
        dbmodel.clearCache();
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType newft) throws DataStoreException {
        ensureOpen();
        final FeatureType oldft = getFeatureType(typeName);

        //we only handle adding or removing columns
        final List<PropertyDescriptor> toRemove = new ArrayList<PropertyDescriptor>();
        final List<PropertyDescriptor> toAdd = new ArrayList<PropertyDescriptor>();

        toRemove.addAll(oldft.getDescriptors());
        toRemove.removeAll(newft.getDescriptors());
        toAdd.addAll(newft.getDescriptors());
        toAdd.removeAll(oldft.getDescriptors());

        //TODO : this is not perfect, we loose datas, we should update the column type when possible.
        Connection cnx = null;
        try{
            cnx = getDataSource().getConnection();

            for(PropertyDescriptor remove : toRemove){
                final String sql = getQueryBuilder().alterTableDropColumnSQL(oldft, remove, cnx);
                final Statement stmt = cnx.createStatement();
                try {
                    stmt.execute(sql);
                } finally {
                    JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
                }
            }

            for(PropertyDescriptor add : toAdd){
                final String sql = getQueryBuilder().alterTableAddColumnSQL(oldft, add, cnx);
                final Statement stmt = cnx.createStatement();
                try {
                    stmt.execute(sql);
                } finally {
                    JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
                }
            }

        } catch (final SQLException ex) {
            throw new DataStoreException("Failed updating table "+typeName.getLocalPart()+", "+ex.getMessage(), ex);
        } finally{
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx);
        }

        // reset the type name cache, will be recreated when needed.
        dbmodel.clearCache();
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException{
        ensureOpen();
        final FeatureType featureType = getFeatureType(typeName);
        recursiveDelete(featureType);
    }
    
    private void recursiveDelete(ComplexType featureType) throws DataStoreException{
        
        //search properties which are complex types
        for(PropertyDescriptor desc : featureType.getDescriptors()){
            final PropertyType pt = desc.getType();
            if(pt instanceof ComplexType){
                recursiveDelete((ComplexType)pt);
            }
        }
        
        final Name typeName = featureType.getName();
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
            throw new DataStoreException("Failed to drop table "+typeName.getLocalPart()+","+ex.getMessage()+"\n Query : "+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),cnx,stmt,null);
        }
        
    }
        
    /**
     * Decompose given type in flat types for table creation.
     * 
     * @param type
     * @param types
     * @param relations
     * @throws DataStoreException 
     */
    private void decompose(ComplexType type, List<FeatureType> types, List<TypeRelation> relations) throws DataStoreException{
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(type.getName());
        for(PropertyDescriptor desc : type.getDescriptors()){
            final PropertyType pt = desc.getType();
            if(pt instanceof AssociationType){
                final AssociationType assType = (AssociationType) pt;
                final AttributeType attType = assType.getRelatedType();
                if(!(attType instanceof ComplexType)){
                    throw new DataStoreException("Only associations with complex type target are supported");
                }
                final TypeRelation relation = new TypeRelation();
                relation.type = type;
                relation.property = desc;
                relations.add(relation);
                decompose(((ComplexType)attType), types, relations);
                
            }else if(pt instanceof ComplexType){
                final ComplexType comType = (ComplexType) pt;
                final TypeRelation relation = new TypeRelation();
                relation.type = type;
                relation.property = desc;
                relations.add(relation);
                decompose(comType, types, relations);
            }else{
                ftb.add(desc);
            }
        }
        
        final FeatureType flatType = ftb.buildFeatureType();
        types.add(flatType);
    }
    
    private static class TypeRelation {
        ComplexType type;
        PropertyDescriptor property;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Fallback on reader/write iterator methods ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
        
    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return addFeatures(groupName, newFeatures, null, hints);
    }
    
    public final List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, 
            Connection cnx, Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, cnx, hints);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        updateFeatures(groupName, filter, values, null);
    }
    
    public void updateFeatures(final Name groupName, final Filter filter, 
            final Map<? extends PropertyDescriptor, ? extends Object> values, Connection cnx) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values, cnx);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        removeFeatures(groupName, filter, null);
    }
    
    public void removeFeatures(final Name groupName, final Filter filter, Connection cnx) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter, cnx);
    }

    protected void insert(final Collection<? extends ComplexAttribute> features, final ComplexType featureType,
            final Connection cx) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(featureType.getName());

        // we do this in a synchronized block because we need to do two queries,
        // first to figure out what the id will be, then the insert statement
        synchronized (this) {
            Statement st = null;
            try {
                st = cx.createStatement();
                final Object[] nextKeyValues = key.nextPrimaryKeyValues(this, cx);
                final String sql = getQueryBuilder().insertSQL(featureType, features, nextKeyValues, cx);
                st.executeUpdate(sql);

            } catch (SQLException e) {
                throw new DataStoreException("Error inserting features",e);
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(getLogger(),st);
            }
        }

        fireFeaturesAdded(featureType.getName(), null);
    }

    protected void insert(final ComplexAttribute feature, final ComplexType featureType,
            final Connection cx) throws DataStoreException {
        
        if(featureType instanceof SimpleFeatureType){
            insertFlat(feature, featureType, cx);
        }else{
            //decompose feature and make multiple inserts
            final List<Entry<ComplexAttribute,ComplexAttribute>> flats = decompose(feature);
            for(Entry<ComplexAttribute,ComplexAttribute> hierarchy : flats){
                final ComplexAttribute parent = hierarchy.getKey();
                final ComplexAttribute flat = hierarchy.getValue();
                //resolve parent id fields.
                if(parent!=null){
                    for(Property prop : flat.getProperties()){
                        final RelationMetaModel relation = (RelationMetaModel) prop.getDescriptor().getUserData().get(JDBC_PROPERTY_RELATION);
                        if(relation==null) continue;
                        final Object parentValue = parent.getProperty(relation.getForeignColumn()).getValue();
                        flat.getProperty(relation.getCurrentColumn()).setValue(parentValue);
                    }
                }
                
                insertFlat(flat, flat.getType(), cx);
            }
        }
    }
    
    private void insertFlat(final ComplexAttribute feature, final ComplexType featureType,
            final Connection cx) throws DataStoreException {
        final PrimaryKey key = dbmodel.getPrimaryKey(featureType.getName());

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
                    feature.getProperty(key.getColumns().get(0).getName()).setValue(id);
                }else{
                    stmt.execute(sql);
                }

                //report the feature id as user data since we cant set the fid
                final String fid = featureType.getName().getLocalPart() + "." + PrimaryKey.encodeFID(nextKeyValues);
                feature.getUserData().put("fid", fid);

            //st.executeBatch();
            } catch (SQLException ex) {
            throw new DataStoreException("Failed to intert features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
            }
        }
        fireFeaturesAdded(featureType.getName(), null);
    }

    /**
     * Decompose feature in flat features, they are ordered in appropriate insertion order.
     * 
     * @param candidate 
     */
    private List<Entry<ComplexAttribute,ComplexAttribute>> decompose(ComplexAttribute candidate) throws DataStoreException{
        final List<Entry<ComplexAttribute,ComplexAttribute>> flats = new ArrayList<Entry<ComplexAttribute,ComplexAttribute>>();        
        decompose(null,candidate,flats);
        return flats;
    }
    
    private void decompose(ComplexAttribute parent, ComplexAttribute candidate, List<Entry<ComplexAttribute,ComplexAttribute>> flats) throws DataStoreException{
        //decompose main type
        final ComplexType featuretype = candidate.getType();
        final TableMetaModel table = dbmodel.getSchemaMetaModel(getDatabaseSchema()).getTable(featuretype.getName().getLocalPart());
        final ComplexType flatType = table.getSimpleType();
        final ComplexAttribute flat = FeatureUtilities.defaultProperty(flatType);
        FeatureUtilities.copy(candidate, flat, false);        
        flats.add(new AbstractMap.SimpleImmutableEntry<ComplexAttribute, ComplexAttribute>(parent, flat));
        
        //decompose sub complex types
        for(Property prop : candidate.getProperties()){
            if(prop instanceof ComplexAttribute){
                decompose(flat,(ComplexAttribute)prop, flats);
            }
        }
    }
    
    protected void update(final FeatureType featureType, final Map<AttributeDescriptor,Object> changes,
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
        } catch (SQLException ex) {
            throw new DataStoreException("Failed to update features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
        }
        fireFeaturesUpdated(featureType.getName(), null);
    }

    protected void delete(final FeatureType featureType, final Filter filter, final Connection cx)
            throws DataStoreException {

        Statement stmt = null;
        String sql = null;
        try {
            sql = getQueryBuilder().deleteSQL(featureType, filter);
            stmt = cx.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            throw new DataStoreException("Failed to delete features : "+ex.getMessage()+"\nSQL Query :"+sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(),stmt);
        }
        fireFeaturesDeleted(featureType.getName(), null);
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // other utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @param groupName
     * @param newFeatures
     * @return list of ids of the features added.
     * @throws DataStoreException
     */
    protected List<FeatureId> handleAddWithFeatureWriter(final Name groupName, final Collection<? extends Feature> newFeatures,
            Connection cnx, final Hints hints) throws DataStoreException{
        try{
            return FeatureStoreUtilities.write(getFeatureWriterAppend(groupName,cnx,hints), newFeatures);
        }catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }
    
    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @param groupName
     * @param filter
     * @param values
     * @param Connection
     * @throws DataStoreException
     */
    protected void handleUpdateWithFeatureWriter(final Name groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values, Connection cnx) throws DataStoreException {

        final FeatureWriter writer = getFeatureWriter(groupName,filter,cnx,null);

        try{
            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Map.Entry<? extends PropertyDescriptor,? extends Object> entry : values.entrySet()){
                    f.getProperty(entry.getKey().getName()).setValue(entry.getValue());
                }
                writer.write();
            }
        } catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        } finally{
            writer.close();
        }
    }
    
    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     * 
     * @param groupName
     * @param filter
     * @param Connection
     * @throws DataStoreException
     */
    protected void handleRemoveWithFeatureWriter(final Name groupName, final Filter filter, Connection cnx) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(groupName,filter,cnx,null);

        try{
            while(writer.hasNext()){
                writer.next();
                writer.remove();
            }
        } catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        } finally{
            writer.close();
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
                getLogger().log(Level.WARNING, "Failed to close datasource.", e);
            }
        }
    }

}
