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

package org.geotoolkit.db.reverse;


import com.vividsolutions.jts.geom.Geometry;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.data.AbstractFeatureStore.*;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import org.geotoolkit.db.JDBCFeatureStore;
import static org.geotoolkit.db.JDBCFeatureStoreUtilities.*;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.reverse.ColumnMetaModel.Type;
import org.geotoolkit.db.reverse.MetaDataConstants.Column;
import org.geotoolkit.db.reverse.MetaDataConstants.ExportedKey;
import org.geotoolkit.db.reverse.MetaDataConstants.ImportedKey;
import org.geotoolkit.db.reverse.MetaDataConstants.Index;
import org.geotoolkit.db.reverse.MetaDataConstants.Schema;
import org.geotoolkit.db.reverse.MetaDataConstants.Table;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.ModifiableFeatureTypeFactory;
import org.geotoolkit.feature.type.ModifiableType;
import org.geotoolkit.parameter.Parameters;
import org.opengis.coverage.Coverage;
import org.opengis.feature.type.*;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Represent the structure of the database. The work done here is similar to
 * reverse engineering.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DataBaseModel {

    public static final String ASSOCIATION_SEPARATOR = "→";

    /**
     * The native SRID associated to a certain descriptor
     */
    public static final String JDBC_NATIVE_SRID = "nativeSRID";

    /**
     * Custom factory where types can be modified after they are created.
     */
    private static final FeatureTypeFactory FTF = new ModifiableFeatureTypeFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    /**
     * Dummy type which will be replaced dynamically in the reverse engineering process.
     */
    private static final ComplexType FLAG_TYPE = FTF.createComplexType(
            new DefaultName("flag"), Collections.EMPTY_LIST, false, false, Collections.EMPTY_LIST, null, null);

    private final JDBCFeatureStore store;
    private final Map<Name,PrimaryKey> pkIndex = new HashMap<Name, PrimaryKey>();
    private final Map<Name,FeatureType> typeIndex = new HashMap<Name, FeatureType>();
    private Map<String,SchemaMetaModel> schemas = null;
    private Set<Name> nameCache = null;
    private final boolean simpleTypes;

    //metadata getSuperTable query is not implemented on all databases
    private Boolean handleSuperTableMetadata = null;

    //various cache while analyzing model
    private DatabaseMetaData metadata;
    private CachedResultSet cacheSchemas;
    private CachedResultSet cacheTables;
    private CachedResultSet cacheColumns;
    private CachedResultSet cachePrimaryKeys;
    private CachedResultSet cacheImportedKeys;
    private CachedResultSet cacheExportedKeys;
    private CachedResultSet cacheIndexInfos;

    public DataBaseModel(final JDBCFeatureStore store, final boolean simpleTypes){
        this.store = store;
        this.simpleTypes = simpleTypes;
    }

    public boolean isSimpleTypes() {
        return simpleTypes;
    }

    public Collection<SchemaMetaModel> getSchemaMetaModels() throws DataStoreException {
        if(schemas == null){
            analyze();
        }
        return schemas.values();
    }

    public SchemaMetaModel getSchemaMetaModel(String name) throws DataStoreException{
        if(schemas == null){
            analyze();
        }
        return schemas.get(name);
    }

    /**
     * Clear the model cache. A new database analyze will be made the next time
     * it is needed.
     */
    public synchronized void clearCache(){
        pkIndex.clear();
        typeIndex.clear();
        nameCache = null;
        schemas = null;
    }

    public PrimaryKey getPrimaryKey(final Name featureTypeName) throws DataStoreException{
        if(schemas == null){
            analyze();
        }
        return pkIndex.get(featureTypeName);
    }

    public synchronized Set<Name> getNames() throws DataStoreException {
        Set<Name> ref = nameCache;
        if(ref == null){
            analyze();
            final Set<Name> names = new HashSet<Name>();
            for(Entry<Name,FeatureType> entry : typeIndex.entrySet()){
                if(Boolean.TRUE.equals(entry.getValue().getUserData().get("subtype"))) continue;
                if(store.getDialect().ignoreTable(entry.getKey().getLocalPart())) continue;
                names.add(entry.getKey());
            }
            ref = Collections.unmodifiableSet(names);
            nameCache = ref;
        }
        return ref;
    }

    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        if(schemas == null){
            analyze();
        }
        return typeIndex.get(typeName);
    }

    /**
     * Explore all tables and views then recreate a complex feature model from
     * relations.
     */
    private synchronized void analyze() throws DataStoreException{
        if(schemas != null){
            //already analyzed
            return;
        }

        clearCache();
        schemas = new HashMap<>();
        final SQLDialect dialect = store.getDialect();

        Connection cx = null;
        try {
            cx = store.getDataSource().getConnection();

            metadata = cx.getMetaData();

            // Cache all metadata informations, we will loop on them plenty of times ////////
            cacheSchemas = new CachedResultSet(metadata.getSchemas(),
                    Schema.TABLE_SCHEM);
            cacheTables = new CachedResultSet(
                    metadata.getTables(null,null,null,new String[]{Table.VALUE_TYPE_TABLE, Table.VALUE_TYPE_VIEW}),
                    Table.TABLE_SCHEM,
                    Table.TABLE_NAME,
                    Table.TABLE_TYPE);
            cacheColumns = new CachedResultSet(metadata.getColumns(null, null, null, "%"),
                    Column.TABLE_SCHEM,
                    Column.TABLE_NAME,
                    Column.COLUMN_NAME,
                    Column.COLUMN_SIZE,
                    Column.DATA_TYPE,
                    Column.TYPE_NAME,
                    Column.IS_NULLABLE,
                    Column.IS_AUTOINCREMENT,
                    Column.REMARKS);
            if(dialect.supportGlobalMetadata()){
                cachePrimaryKeys = new CachedResultSet(metadata.getPrimaryKeys(null, null, null),
                        Column.TABLE_SCHEM,
                        Column.TABLE_NAME,
                        Column.COLUMN_NAME);
                cacheImportedKeys = new CachedResultSet(metadata.getImportedKeys(null, null, null),
                        ImportedKey.FKTABLE_SCHEM,
                        ImportedKey.FKTABLE_NAME,
                        ImportedKey.FKCOLUMN_NAME,
                        ImportedKey.PKTABLE_SCHEM,
                        ImportedKey.PKTABLE_NAME,
                        ImportedKey.PKCOLUMN_NAME,
                        ImportedKey.DELETE_RULE);
                cacheExportedKeys = new CachedResultSet(metadata.getExportedKeys(null, null, null),
                        ImportedKey.PKTABLE_SCHEM,
                        ImportedKey.PKTABLE_NAME,
                        ExportedKey.PKCOLUMN_NAME,
                        ExportedKey.FKTABLE_SCHEM,
                        ExportedKey.FKTABLE_NAME,
                        ExportedKey.FKCOLUMN_NAME,
                        ImportedKey.DELETE_RULE);
            }else{
                //we have to loop ourself on all schema and tables to collect informations
                cachePrimaryKeys = new CachedResultSet();
                cacheImportedKeys = new CachedResultSet();
                cacheExportedKeys = new CachedResultSet();
                
                final Iterator<Map> ite = cacheSchemas.filter(Filter.INCLUDE);
                while(ite.hasNext()) {
                    final String schemaName = (String)ite.next().get(Schema.TABLE_SCHEM);
                    final Filter filter = FF.equals(FF.property(Table.TABLE_SCHEM), FF.literal(schemaName));
                    final Iterator<Map> tableite = cacheTables.filter(filter);
                    while (tableite.hasNext()) {
                        final Map info = tableite.next();
                        cachePrimaryKeys.append(metadata.getPrimaryKeys(null, schemaName, (String)info.get(Table.TABLE_NAME)),
                            Column.TABLE_SCHEM,
                            Column.TABLE_NAME,
                            Column.COLUMN_NAME);
                        cacheImportedKeys.append(metadata.getImportedKeys(null, schemaName, (String)info.get(Table.TABLE_NAME)),
                            ImportedKey.FKTABLE_SCHEM,
                            ImportedKey.FKTABLE_NAME,
                            ImportedKey.FKCOLUMN_NAME,
                            ImportedKey.PKTABLE_SCHEM,
                            ImportedKey.PKTABLE_NAME,
                            ImportedKey.PKCOLUMN_NAME,
                            ImportedKey.DELETE_RULE);
                        cacheExportedKeys.append(metadata.getExportedKeys(null, schemaName, (String)info.get(Table.TABLE_NAME)),
                            ImportedKey.PKTABLE_SCHEM,
                            ImportedKey.PKTABLE_NAME,
                            ExportedKey.PKCOLUMN_NAME,
                            ExportedKey.FKTABLE_SCHEM,
                            ExportedKey.FKTABLE_NAME,
                            ExportedKey.FKCOLUMN_NAME,
                            ImportedKey.DELETE_RULE);
                    }
                }
            }


            ////////////////////////////////////////////////////////////////////////////////


            final Iterator<Map> ite = cacheSchemas.filter(Filter.INCLUDE);
            while(ite.hasNext()) {
                final String SchemaName = (String)ite.next().get(Schema.TABLE_SCHEM);
                final SchemaMetaModel schema = analyzeSchema(SchemaName,cx);
                schemas.put(schema.name, schema);
            }

            reverseSimpleFeatureTypes(cx);
            reverseComplexFeatureTypes();

        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing database model.", e);
        } finally {
            closeSafe(store.getLogger(),cx);
            cacheSchemas = null;
            cacheTables = null;
            cacheColumns = null;
            cachePrimaryKeys = null;
            cacheImportedKeys = null;
            cacheExportedKeys = null;
            cacheIndexInfos = null;
            metadata = null;
        }


        //build indexes---------------------------------------------------------
        final String baseSchemaName = store.getDatabaseSchema();

        final Collection<SchemaMetaModel> candidates;
        if(baseSchemaName == null){
            //take all schemas
            candidates = getSchemaMetaModels();
        }else{
            candidates = Collections.singleton(getSchemaMetaModel(baseSchemaName));
        }

        for(SchemaMetaModel schema : candidates){
           if (schema != null) {
                for(TableMetaModel table : schema.tables.values()){
                    
                    final ComplexType ft;
                    if(simpleTypes){
                        ft = table.getType(TableMetaModel.View.SIMPLE_FEATURE_TYPE);
                    }else{
                        ft = table.getType(TableMetaModel.View.COMPLEX_FEATURE_TYPE);
                    }
                    final Name name = ft.getName();
                    pkIndex.put(name, table.key);
                    if(table.isSubType()){
                        //we don't show subtype, they are part of other feature types, add a flag to identify then
                        ft.getUserData().put("subtype", Boolean.TRUE);
                    }
                    typeIndex.put(name, (FeatureType)ft);
                 }
            } else {
                throw new DataStoreException("Specifed schema " + baseSchemaName + " does not exist.");
             }
         }
        
    }

    private SchemaMetaModel analyzeSchema(final String schemaName, final Connection cx) throws DataStoreException{

        final SchemaMetaModel schema = new SchemaMetaModel(schemaName);

        try {
            Filter filter = FF.equals(FF.property(Table.TABLE_SCHEM), FF.literal(schemaName));

            if(Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.TABLE,store.getConfiguration()).getValue()!=null &&
               !Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.TABLE,store.getConfiguration()).getValue().toString().isEmpty()){
                filter = FF.and(filter, FF.equals(FF.property(Table.TABLE_NAME),
                        FF.literal(Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.TABLE,store.getConfiguration()).getValue().toString())));
            }

            final Iterator<Map> ite = cacheTables.filter(filter);
            while (ite.hasNext()) {
                final TableMetaModel table = analyzeTable(ite.next(),cx);
                schema.tables.put(table.name, table);
            }

        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing database model.", e);
        }

        return schema;
    }

    private Filter filter(String schemafield, String schemaName, String tablefield, String tableName){
        return FF.and(      FF.equals(FF.property(schemafield), FF.literal(schemaName)),
                            FF.equals(FF.property(tablefield), FF.literal(tableName)));
    }

    private TableMetaModel analyzeTable(final Map tableSet, final Connection cx) throws DataStoreException, SQLException{
        final SQLDialect dialect = store.getDialect();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(FTF);

        final String schemaName = (String) tableSet.get(Table.TABLE_SCHEM);
        final String tableName = (String) tableSet.get(Table.TABLE_NAME);
        final String tableType = (String) tableSet.get(Table.TABLE_TYPE);

        final TableMetaModel table = new TableMetaModel(tableName,tableType);

        try {

            //explore all columns ----------------------------------------------
            Filter tableFilter = filter(Table.TABLE_SCHEM, schemaName, Table.TABLE_NAME, tableName);

            final Iterator<Map> ite1 = cacheColumns.filter(tableFilter);
            while(ite1.hasNext()){
                ftb.add(analyzeColumn(ite1.next(),cx));
            }

            //find primary key -------------------------------------------------
            final List<ColumnMetaModel> cols = new ArrayList();
            final Iterator<Map> pkIte = cachePrimaryKeys.filter(tableFilter);
            while(pkIte.hasNext()){
                final Map result = pkIte.next();
                final String columnName = (String) result.get(Column.COLUMN_NAME);

                //look up the type ( should only be one row )
                final Iterator<Map> cite = cacheColumns.filter(
                        FF.and(tableFilter, FF.equals(FF.property(Column.COLUMN_NAME), FF.literal(columnName))));
                final Map column = cite.next();

                final int sqlType = (Integer)column.get(Column.DATA_TYPE);
                final String sqlTypeName = (String) column.get(Column.TYPE_NAME);
                Class columnType = dialect.getJavaType(sqlType, sqlTypeName);

                if (columnType == null) {
                    store.getLogger().log(Level.WARNING, "No class for sql type {0}", sqlType);
                    columnType = Object.class;
                }

                ColumnMetaModel col = null;

                final String str = (String) column.get(Column.IS_AUTOINCREMENT);
                if(Column.VALUE_YES.equalsIgnoreCase(str)){
                    col = new ColumnMetaModel(schemaName, tableName, columnName, sqlType, sqlTypeName, columnType, Type.AUTO);
                }else {
                    final String sequenceName = dialect.getColumnSequence(cx,schemaName, tableName, columnName);
                    if (sequenceName != null) {
                        col = new ColumnMetaModel(schemaName, tableName, columnName, sqlType,
                                sqlTypeName, columnType, Type.SEQUENCED,sequenceName);
                    }else{
                        col = new ColumnMetaModel(schemaName, tableName, columnName, sqlType,
                                sqlTypeName, columnType, Type.NON_INCREMENTING);
                    }
                }

                cols.add(col);
            }

            //Search indexes, they provide informations such as :
            // - Unique indexes may indicate 1:1 relations in complexe features
            // - Unique indexes can be used as primary key if no primary key are defined
            final boolean pkEmpty = cols.isEmpty();
            final List<String> names = new ArrayList<String>();
            final Map<String,List<String>> uniqueIndexes = new HashMap<String, List<String>>();
            String indexname = null;
            //we can't cache this one, seems to be a bug in the driver, it won't find anything for table name like '%'
            cacheIndexInfos = new CachedResultSet(metadata.getIndexInfo(null, schemaName, tableName,true,false),
                    Index.TABLE_SCHEM,
                    Index.TABLE_NAME,
                    Index.COLUMN_NAME,
                    Index.INDEX_NAME);
            final Iterator<Map> indexIte = cacheIndexInfos.filter(tableFilter);
            while(indexIte.hasNext()){
                final Map result = indexIte.next();
                final String columnName = (String)result.get(Index.COLUMN_NAME);
                final String idxName = (String)result.get(Index.INDEX_NAME);

                List<String> lst = uniqueIndexes.get(idxName);
                if(lst == null){
                    lst = new ArrayList<String>();
                    uniqueIndexes.put(idxName, lst);
                }
                lst.add(columnName);

                if(pkEmpty){
                    //we use a single index columns set as primary key
                    //we must not mix with other potential indexes.
                    if(indexname == null){
                        indexname = idxName;
                    }else if(!indexname.equals(idxName)){
                        continue;
                    }
                    names.add(columnName);
                }
            }

            //for each unique index composed of one column add a flag on the property descriptor
            for(Entry<String,List<String>> entry : uniqueIndexes.entrySet()){
                final List<String> columns = entry.getValue();
                if(columns.size() == 1){
                    String columnName = columns.get(0);
                    for(PropertyDescriptor desc : ftb.getProperties()){
                        if(desc.getName().getLocalPart().equals(columnName)){
                            desc.getUserData().put(JDBCFeatureStore.JDBC_PROPERTY_UNIQUE, Boolean.TRUE);
                        }
                    }
                }
            }

            if(pkEmpty && !names.isEmpty()){
                //build a primary key from unique index
                final Iterator<Map> ite = cacheColumns.filter(tableFilter);
                while(ite.hasNext()){
                    final Map result = ite.next();
                    final String columnName = (String) result.get(Column.COLUMN_NAME);
                    if(!names.contains(columnName)){
                        continue;
                    }

                    final int sqlType = (Integer) result.get(Column.DATA_TYPE);
                    final String sqlTypeName = (String) result.get(Column.TYPE_NAME);
                    final Class columnType = dialect.getJavaType(sqlType, sqlTypeName);
                    final ColumnMetaModel col = new ColumnMetaModel(schemaName, tableName, columnName,
                            sqlType, sqlTypeName, columnType, Type.NON_INCREMENTING);
                    cols.add(col);

                    //set the information
                    for(PropertyDescriptor desc : ftb.getProperties()){
                        if(desc.getName().getLocalPart().equals(columnName)){
                            desc.getUserData().put(HintsPending.PROPERTY_IS_IDENTIFIER,Boolean.TRUE);
                            break;
                        }
                    }
                }
            }


            if(cols.isEmpty()){
                if (Table.VALUE_TYPE_TABLE.equals(tableType)) {
                    store.getLogger().log(Level.INFO, "No primary key found for {0}.", tableName);
                }
            }
            table.key = new PrimaryKey(tableName, cols);

            //mark primary key columns
            for(PropertyDescriptor desc : ftb.getProperties()){
                for(ColumnMetaModel col : cols){
                    if(desc.getName().getLocalPart().equals(col.getName())){
                        desc.getUserData().put(HintsPending.PROPERTY_IS_IDENTIFIER, Boolean.TRUE);
                        break;
                    }
                }
            }


            //find imported keys -----------------------------------------------
            Iterator<Map> ite = cacheImportedKeys.filter(filter(ImportedKey.FKTABLE_SCHEM, schemaName, ImportedKey.FKTABLE_NAME, tableName));
            while(ite.hasNext()){
                final Map result = ite.next();
                final String localColumn = (String)result.get(ImportedKey.FKCOLUMN_NAME);
                final String refSchemaName = (String)result.get(ImportedKey.PKTABLE_SCHEM);
                final String refTableName = (String)result.get(ImportedKey.PKTABLE_NAME);
                final String refColumnName = (String)result.get(ImportedKey.PKCOLUMN_NAME);
                final int deleteRule = (Integer)result.get(ImportedKey.DELETE_RULE);
                final boolean deleteCascade = DatabaseMetaData.importedKeyCascade == deleteRule;
                final RelationMetaModel relation = new RelationMetaModel(localColumn,
                        refSchemaName, refTableName, refColumnName, true, deleteCascade);
                table.importedKeys.add(relation);

                //set the information
                for(PropertyDescriptor desc : ftb.getProperties()){
                    if(desc.getName().getLocalPart().equals(localColumn)){
                        desc.getUserData().put(JDBCFeatureStore.JDBC_PROPERTY_RELATION,relation);
                        break;
                    }
                }
            }

            //find exported keys -----------------------------------------------
            ite = cacheExportedKeys.filter(filter(ImportedKey.PKTABLE_SCHEM, schemaName, ImportedKey.PKTABLE_NAME, tableName));
            while(ite.hasNext()){
                final Map result = ite.next();
                final String localColumn = (String)result.get(ExportedKey.PKCOLUMN_NAME);
                final String refSchemaName = (String)result.get(ExportedKey.FKTABLE_SCHEM);
                final String refTableName = (String)result.get(ExportedKey.FKTABLE_NAME);
                final String refColumnName = (String)result.get(ExportedKey.FKCOLUMN_NAME);
                final int deleteRule = (Integer)result.get(ImportedKey.DELETE_RULE);
                final boolean deleteCascade = DatabaseMetaData.importedKeyCascade == deleteRule;
                table.exportedKeys.add(new RelationMetaModel(localColumn,
                        refSchemaName, refTableName, refColumnName, false, deleteCascade));
            }

            //find parent table if any -----------------------------------------
//            if(handleSuperTableMetadata == null || handleSuperTableMetadata){
//                try{
//                    result = metadata.getSuperTables(null, schemaName, tableName);
//                    while (result.next()) {
//                        final String parentTable = result.getString(SuperTable.SUPERTABLE_NAME);
//                        table.parents.add(parentTable);
//                    }
//                }catch(final SQLException ex){
//                    //not implemented by database
//                    handleSuperTableMetadata = Boolean.FALSE;
//                    store.getLogger().log(Level.INFO, "Database does not handle getSuperTable, feature type hierarchy will be ignored.");
//                }finally{
//                    closeSafe(store.getLogger(),result);
//                }
//            }

        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing table : " + tableName, e);
        }

        ftb.setName(tableName);
        table.tableType = ftb.buildType();
        return table;
    }

    private AttributeDescriptor analyzeColumn(final Map columnSet, final Connection cx) throws SQLException, DataStoreException{
        final SQLDialect dialect = store.getDialect();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
        final AttributeTypeBuilder atb = new AttributeTypeBuilder(FTF);

        final String schemaName     = (String) columnSet.get(Column.TABLE_SCHEM);
        final String tableName      = (String) columnSet.get(Column.TABLE_NAME);
        final String columnName     = (String) columnSet.get(Column.COLUMN_NAME);
        final int columnSize        = (Integer)columnSet.get(Column.COLUMN_SIZE);
        final int columnDataType    = (Integer)columnSet.get(Column.DATA_TYPE);
        final String columnTypeName = (String) columnSet.get(Column.TYPE_NAME);
        final String columnNullable = (String) columnSet.get(Column.IS_NULLABLE);

        atb.setName(columnName);
        atb.setLength(columnSize);
        adb.setName(columnName);

        try {
            dialect.decodeColumnType(atb, cx, columnTypeName, columnDataType, schemaName, tableName, columnName);
        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing column : " + columnName, e);
        }

        //table values are always min 1, max 1
        adb.setMinOccurs(1);
        adb.setMaxOccurs(1);

        //nullability
        adb.setNillable(!Column.VALUE_NO.equalsIgnoreCase(columnNullable));

        if(Geometry.class.isAssignableFrom(atb.getBinding()) || Coverage.class.isAssignableFrom(atb.getBinding())){
            adb.setType(atb.buildGeometryType());
        }else{
            adb.setType(atb.buildType());
        }
        adb.findBestDefaultValue();
        return adb.buildDescriptor();
    }

    /**
     * Analyze the metadata of the ResultSet to rebuild a feature type.
     *
     * @param result
     * @param name
     * @return FeatureType
     * @throws SQLException
     */
    public FeatureType analyzeResult(final ResultSet result, final String name) throws SQLException, DataStoreException{
        final SQLDialect dialect = store.getDialect();
        final String namespace = store.getDefaultNamespace();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(FTF);
        ftb.setName(namespace, name);

        final ResultSetMetaData metadata = result.getMetaData();
        final int nbcol = metadata.getColumnCount();

        for(int i=1; i<=nbcol; i++){
            final String columnName = metadata.getColumnName(i);
            final String typeName = metadata.getColumnTypeName(i);
            final String schemaName = metadata.getSchemaName(i);
            final String tableName = metadata.getTableName(i);
            final int sqlType = metadata.getColumnType(i);
            final String sqlTypeName = metadata.getColumnTypeName(i);

            //search if we already have this minute
            PropertyDescriptor desc = null;
            final SchemaMetaModel schema = getSchemaMetaModel(schemaName);
            if(schema != null){
                TableMetaModel table = schema.getTable(tableName);
                if(table != null){
                    desc = table.getType(TableMetaModel.View.SIMPLE_FEATURE_TYPE).getDescriptor(columnName);
                }
            }

            if(desc == null){
                //could not find the original type
                //this column must be calculated
                final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
                final AttributeTypeBuilder atb = new AttributeTypeBuilder(FTF);

                adb.setName(ensureGMLNS(namespace, columnName));
                adb.setMinOccurs(1);
                adb.setMaxOccurs(1);

                final int nullable = metadata.isNullable(i);
                adb.setNillable(nullable == metadata.columnNullable);


                atb.setName(ensureGMLNS(namespace, columnName));
                Connection cx = null;
                try {
                    cx = store.getDataSource().getConnection();
                    final Class type = dialect.getJavaType(sqlType, sqlTypeName);
                    if (type.equals(Geometry.class)) {
                        // try to determine the real geometric type
                        dialect.decodeGeometryColumnType(atb, cx, result, i);
                    } else {
                        atb.setName(columnName); // why so this a sencond time ?
                        atb.setBinding(type);
                    }
                } catch (SQLException e) {
                    throw new DataStoreException("Error occurred analyzing column : " + columnName, e);
                } finally {
                    closeSafe(store.getLogger(),cx);
                }

                if(Geometry.class.isAssignableFrom(atb.getBinding())){
                    adb.setType(atb.buildGeometryType());
                }else{
                    adb.setType(atb.buildType());
                }

                adb.findBestDefaultValue();
                desc = adb.buildDescriptor();
            }

            ftb.add(desc);
        }

        return ftb.buildFeatureType();
    }

    /**
     * Rebuild simple feature types for each table.
     */
    private void reverseSimpleFeatureTypes(final Connection cx){
        final SQLDialect dialect = store.getDialect();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(FTF);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
        final AttributeTypeBuilder atb = new AttributeTypeBuilder(FTF);

        for(final SchemaMetaModel schema : schemas.values()){
            for(final TableMetaModel table : schema.tables.values()){
                final String tableName = table.name;

                //fill the namespace--------------------------------------------
                ftb.reset();
                ftb.copy(table.tableType);
                final String namespace = store.getDefaultNamespace();
                ftb.setName(namespace, ftb.getName().getLocalPart());

                final List<PropertyDescriptor> descs = ftb.getProperties();

                for(int i=0,n=descs.size(); i<n; i++){
                    final PropertyDescriptor desc = descs.get(i);
                    final PropertyType type = desc.getType();
                    final String name = desc.getName().getLocalPart();

                    adb.reset();
                    adb.copy((AttributeDescriptor) desc);
                    adb.setName(ensureGMLNS(namespace,name));
                    atb.reset();
                    atb.copy((AttributeType) type);
                    atb.setName(ensureGMLNS(namespace,name));
                    adb.setType(atb.buildType());

                    //Set the CRS if it's a geometry
                    final Class binding = type.getBinding();
                    if (Geometry.class.isAssignableFrom(binding) || Coverage.class.isAssignableFrom(binding)) {
                        
                        //look up the type ( should only be one row )
                        final Filter tableFilter = filter(Table.TABLE_SCHEM, schema.name, Table.TABLE_NAME, tableName);
                        final Filter colFilter = FF.equals(FF.property(Column.COLUMN_NAME), FF.literal(name));
                        final Iterator<Map> meta = cacheColumns.filter(
                                FF.and(tableFilter, colFilter));
                        final Map metas = meta.next();
                        
                        //add the attribute as a geometry, try to figure out
                        // its srid first
                        Integer srid = null;
                        CoordinateReferenceSystem crs = null;
                        try {
                            srid = dialect.getGeometrySRID(store.getDatabaseSchema(), tableName, name, metas, cx);
                            if(srid != null)
                                crs = dialect.createCRS(srid, cx);
                        } catch (SQLException e) {
                            String msg = "Error occured determing srid for " + tableName + "."+ name;
                            store.getLogger().log(Level.WARNING, msg, e);
                        }

                        atb.setCRS(crs);
                        if(srid != null){
                            adb.addUserData(JDBCFeatureStore.JDBC_PROPERTY_SRID, srid);
                        }
                        adb.setType(atb.buildGeometryType());
                        adb.findBestDefaultValue();
                    }

                    descs.set(i, adb.buildDescriptor());
                }

                table.simpleFeatureType = ftb.buildSimpleFeatureType();
            }
        }

    }

    /**
     * Rebuild complex feature types using foreign key relations.
     */
    private void reverseComplexFeatureTypes(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(FTF);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
        final AttributeTypeBuilder atb = new AttributeTypeBuilder(FTF);

        //result map
        final Map<String,TableMetaModel> builded = new HashMap<String, TableMetaModel>();

        //first pass to create the real types but without relations types-------
        //since we must have all of them before creating relations
        final List<Object[]> secondPass = new ArrayList<Object[]>();

        for(final SchemaMetaModel schema : schemas.values()){
            for(final TableMetaModel table : schema.tables.values()){
                final String code = schema.name +"."+table.name;
                builded.put(code, table);

                //create the complex model by replacing descriptors
                final ComplexType baseType = table.simpleFeatureType;
                ftb.reset();
                ftb.copy(baseType);

                // replace 0:1 relations----------------------------------------
                for(final RelationMetaModel relation : table.importedKeys){

                    //find the descriptor to replace
                    final List<PropertyDescriptor> descs = ftb.getProperties();
                    int index = -1;
                    for(int i=0,n=descs.size();i<n;i++){
                        final PropertyDescriptor pd = descs.get(i);
                        if(pd.getName().getLocalPart().equals(relation.getCurrentColumn())){
                            index = i;
                        }
                    }

                    //create the new descriptor derivated
                    final PropertyDescriptor baseDescriptor = descs.get(index);
                    adb.reset();
                    adb.copy((AttributeDescriptor) baseDescriptor);
                    adb.setType(FLAG_TYPE);
                    adb.setDefaultValue(null);
                    adb.addUserData(JDBCFeatureStore.JDBC_PROPERTY_RELATION, relation);
                    final PropertyDescriptor newDescriptor = adb.buildDescriptor();
                    descs.set(index, newDescriptor);

                    final Object[] futur = new Object[]{table, relation};
                    secondPass.add(futur);
                }

                // create N:1 relations-----------------------------------------
                for(final RelationMetaModel relation : table.exportedKeys){

                    //find an appropriate name
                    Name n = new DefaultName(store.getDefaultNamespace(),relation.getForeignColumn());
                    for(PropertyDescriptor dpd : ftb.getProperties()){
                        if(n.getLocalPart().equals(dpd.getName().getLocalPart())){
                            //name already used, make it unique by including reference table name
                            n = new DefaultName(store.getDefaultNamespace(),
                                relation.getForeignTable()+ASSOCIATION_SEPARATOR+relation.getForeignColumn());
                            break;
                        }
                    }

                    final ComplexType foreignType = schemas.get(relation.getForeignSchema())
                            .getTable(relation.getForeignTable()).getType(TableMetaModel.View.TABLE);
                    final PropertyDescriptor propDesc = foreignType.getDescriptor(relation.getForeignColumn());
                    final boolean unique = Boolean.TRUE.equals(propDesc.getUserData().get(JDBCFeatureStore.JDBC_PROPERTY_UNIQUE));
                    adb.reset();
                    adb.setName(n);
                    adb.setType(FLAG_TYPE);
                    adb.setMinOccurs(0);
                    adb.setMaxOccurs(unique? 1 : Integer.MAX_VALUE);
                    adb.setNillable(false);
                    adb.setDefaultValue(null);
                    adb.addUserData(JDBCFeatureStore.JDBC_PROPERTY_RELATION, relation);
                    final PropertyDescriptor newDescriptor = adb.buildDescriptor();
                    ftb.add(newDescriptor);

                    final Object[] futur = new Object[]{table, relation};
                    secondPass.add(futur);
                }

                if(table.isSubType()){
                    //we wont a complex type for sub types, we don't want them to have ids
                    table.complexAttType = ftb.buildType();
                    table.complexFeatureType = ftb.buildFeatureType();
                    table.allType = ftb.buildFeatureType();
                }else{
                    table.complexAttType = ftb.buildFeatureType();
                    table.complexFeatureType = ftb.buildFeatureType();
                    table.allType = ftb.buildFeatureType();
                }
            }
        }

        //second pass to fill relations-----------------------------------------
        for(Object[] futur : secondPass){
            final TableMetaModel primaryTable = (TableMetaModel) futur[0];
            final RelationMetaModel relation = (RelationMetaModel) futur[1];
            final String relCode = relation.getForeignSchema() +"."+relation.getForeignTable();
            final TableMetaModel foreignTable = (TableMetaModel)builded.get(relCode);

            //update complex attribute type
            final ModifiableType cat = (ModifiableType) primaryTable.getType(TableMetaModel.View.COMPLEX_ATTRIBUTE_TYPE);
            final int index = modifyField(foreignTable, relation, cat);

            //update complex feature type
            final ModifiableType cft = (ModifiableType) primaryTable.getType(TableMetaModel.View.COMPLEX_FEATURE_TYPE);
            modifyField(foreignTable, relation, cft);

            //update full type
            final ModifiableType allt = (ModifiableType) primaryTable.getType(TableMetaModel.View.ALLCOMPLEX);
            modifyField(foreignTable, relation, allt);


            if(primaryTable.isSubType() && relation.isDeleteCascade() && relation.isImported()){
                //this type is a subtype and relation points toward it's parent
                //so we actualy don't want to view this property since it will
                //be visible the other way araound (parent -> child)
                cat.changeProperty(index, null);
                cft.changeProperty(index, null);
            }


        }
    }

    private int modifyField(final TableMetaModel foreignTable,
            final RelationMetaModel relation, final ModifiableType candidate){
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder(FTF);
        final AttributeTypeBuilder atb = new AttributeTypeBuilder(FTF);

        final List<PropertyDescriptor> descs = candidate.getDescriptors();
        final ComplexType foreignType = foreignTable.getType(TableMetaModel.View.COMPLEX_ATTRIBUTE_TYPE);

        //create the new association descriptor derivated
        atb.reset();
        atb.copy(foreignType);
        atb.setParentType(null);

        //find the descriptor to replace
        int index = -1;
        final String searchedName = relation.isImported() ? relation.getCurrentColumn() : relation.getForeignColumn();
        for(int i=0,n=descs.size();i<n;i++){
            final PropertyDescriptor pd = descs.get(i);
            if(pd.getName().getLocalPart().equals(searchedName)){
                index = i;
            }
        }

        final PropertyDescriptor baseDescriptor = descs.get(index);
        adb.reset();
        adb.copy(baseDescriptor);
        adb.setDefaultValue(null);

        final PropertyDescriptor newDescriptor;
        if(relation.isDeleteCascade()){
            adb.setType(foreignType);
            newDescriptor = adb.buildDescriptor();
        }else{
            adb.setType(atb.buildAssociationType(foreignType));
            newDescriptor = adb.buildAssociationDescriptor();
        }

        newDescriptor.getUserData().put(JDBCFeatureStore.JDBC_PROPERTY_RELATION,relation);
        candidate.changeProperty(index, newDescriptor);

        return index;
    }

}
