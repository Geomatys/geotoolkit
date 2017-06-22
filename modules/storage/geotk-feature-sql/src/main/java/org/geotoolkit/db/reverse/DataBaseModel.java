/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2016, Geomatys
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


import org.opengis.util.GenericName;
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
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalNameException;
import static org.geotoolkit.data.AbstractFeatureStore.*;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import org.geotoolkit.db.DBRelationOperation;
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
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.parameter.Parameters;
import org.opengis.coverage.Coverage;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
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
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    /**
     * Feature type used to mark types which are sub types of others.
     */
    private static final FeatureType SUBTYPE;
    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SubType");
        ftb.setAbstract(true);
        SUBTYPE = ftb.build();
    }

    private final JDBCFeatureStore store;
    private final GenericNameIndex<PrimaryKey> pkIndex = new GenericNameIndex<>();
    private final GenericNameIndex<FeatureType> typeIndex = new GenericNameIndex<>();
    private Map<String,SchemaMetaModel> schemas = null;
    private Set<GenericName> nameCache = null;
    private final boolean simpleTypes;

    //various cache while analyzing model
    private DatabaseMetaData metadata;
    private CachedResultSet cacheSchemas;
    private CachedResultSet cacheTables;
    private CachedResultSet cacheColumns;
    private CachedResultSet cachePrimaryKeys;
    private CachedResultSet cacheImportedKeys;
    private CachedResultSet cacheExportedKeys;
    private CachedResultSet cacheIndexInfos;
    //this set contains schema names which are needed to rebuild relations
    private Set<String> visitedSchemas;
    private Set<String> requieredSchemas;


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
    public synchronized void clearCache() throws IllegalNameException{
        pkIndex.clear();
        typeIndex.clear();
        nameCache = null;
        schemas = null;
    }

    public PrimaryKey getPrimaryKey(final String featureTypeName) throws DataStoreException{
        if(schemas == null){
            analyze();
        }
        return pkIndex.get(store, featureTypeName);
    }

    public synchronized Set<GenericName> getNames() throws DataStoreException {
        Set<GenericName> ref = nameCache;
        if(ref == null){
            analyze();
            final Set<GenericName> names = new HashSet<>();
            for(GenericName name : typeIndex.getNames()) {
                final FeatureType type = typeIndex.get(store, name.toString());
                if(SUBTYPE.isAssignableFrom(type)) continue;
                if(store.getDialect().ignoreTable(name.tip().toString())) continue;
                names.add(name);
            }
            ref = Collections.unmodifiableSet(names);
            nameCache = ref;
        }
        return ref;
    }

    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        if(schemas == null){
            analyze();
        }
        return typeIndex.get(store, typeName);
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
        final String databaseSchema = store.getDatabaseSchema();

        visitedSchemas = new HashSet<>();
        requieredSchemas = new HashSet<>();

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
                        ImportedKey.PK_NAME,
                        ImportedKey.FK_NAME,
                        ImportedKey.FKTABLE_SCHEM,
                        ImportedKey.FKTABLE_NAME,
                        ImportedKey.FKCOLUMN_NAME,
                        ImportedKey.PKTABLE_SCHEM,
                        ImportedKey.PKTABLE_NAME,
                        ImportedKey.PKCOLUMN_NAME,
                        ImportedKey.DELETE_RULE);
                cacheExportedKeys = new CachedResultSet(metadata.getExportedKeys(null, null, null),
                        ExportedKey.PK_NAME,
                        ExportedKey.FK_NAME,
                        ExportedKey.PKTABLE_SCHEM,
                        ExportedKey.PKTABLE_NAME,
                        ExportedKey.PKCOLUMN_NAME,
                        ExportedKey.FKTABLE_SCHEM,
                        ExportedKey.FKTABLE_NAME,
                        ExportedKey.FKCOLUMN_NAME,
                        ExportedKey.DELETE_RULE);
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

            if(databaseSchema!=null){
                requieredSchemas.add(databaseSchema);
            }else{
                final Iterator<Map> ite = cacheSchemas.filter(Filter.INCLUDE);
                while(ite.hasNext()) {
                    requieredSchemas.add((String)ite.next().get(Schema.TABLE_SCHEM));
                }
            }

            //we need to analyze requiered schema references
            while(!requieredSchemas.isEmpty()){
                final String sn = requieredSchemas.iterator().next();
                visitedSchemas.add(sn);
                requieredSchemas.remove(sn);
                final SchemaMetaModel schema = analyzeSchema(sn,cx);
                schemas.put(schema.name, schema);
            }

            reverseSimpleFeatureTypes(cx);
            reverseComplexFeatureTypes();

        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing database model.\n"+e.getMessage(), e);
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
            visitedSchemas = null;
            requieredSchemas = null;
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

                    final FeatureTypeBuilder ft;
                    if(simpleTypes){
                        ft = table.getType(TableMetaModel.View.SIMPLE_FEATURE_TYPE);
                    }else{
                        ft = table.getType(TableMetaModel.View.COMPLEX_FEATURE_TYPE);
                    }
                    final GenericName name = ft.getName();
                    pkIndex.add(store, name, table.key);
                    if(table.isSubType()){
                        //we don't show subtype, they are part of other feature types, add a flag to identify then
                        ft.setSuperTypes(SUBTYPE);
                    }
                    typeIndex.add(store, name, ft.build());
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

        final String schemaName = (String) tableSet.get(Table.TABLE_SCHEM);
        final String tableName = (String) tableSet.get(Table.TABLE_NAME);
        final String tableType = (String) tableSet.get(Table.TABLE_TYPE);

        final TableMetaModel table = new TableMetaModel(tableName,tableType);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        try {

            //explore all columns ----------------------------------------------
            final Filter tableFilter = filter(Table.TABLE_SCHEM, schemaName, Table.TABLE_NAME, tableName);

            final Iterator<Map> ite1 = cacheColumns.filter(tableFilter);
            while(ite1.hasNext()){
                ftb.addAttribute(analyzeColumn(ite1.next(),cx));
            }

            //find primary key -------------------------------------------------
            final List<ColumnMetaModel> cols = new ArrayList<>();
            final Iterator<Map> pkIte = cachePrimaryKeys.filter(tableFilter);
            while(pkIte.hasNext()){
                final Map result = pkIte.next();
                final String columnName = (String) result.get(Column.COLUMN_NAME);

                //look up the type ( should only be one row )
                final Iterator<Map> cite = cacheColumns.filter(
                        FF.and(tableFilter, FF.equals(FF.property(Column.COLUMN_NAME), FF.literal(columnName))));
                final Map column = cite.next();

                final int sqlType = ((Number)column.get(Column.DATA_TYPE)).intValue();
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
            final List<String> names = new ArrayList<>();
            final Map<String,List<String>> uniqueIndexes = new HashMap<>();
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
                    lst = new ArrayList<>();
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
                    for(PropertyTypeBuilder desc : ftb.properties()){
                        if(desc.getName().tip().toString().equals(columnName)){
                            final AttributeTypeBuilder atb = (AttributeTypeBuilder) desc;
                            atb.addCharacteristic(JDBCFeatureStore.JDBC_PROPERTY_UNIQUE).setDefaultValue(Boolean.TRUE);
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

                    final int sqlType = ((Number) result.get(Column.DATA_TYPE)).intValue();
                    final String sqlTypeName = (String) result.get(Column.TYPE_NAME);
                    final Class columnType = dialect.getJavaType(sqlType, sqlTypeName);
                    final ColumnMetaModel col = new ColumnMetaModel(schemaName, tableName, columnName,
                            sqlType, sqlTypeName, columnType, Type.NON_INCREMENTING);
                    cols.add(col);

                    //set the information
                    for(PropertyTypeBuilder desc : ftb.properties()){
                        if(desc.getName().tip().toString().equals(columnName)){
                            final AttributeTypeBuilder atb = (AttributeTypeBuilder) desc;
                            atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
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
            for(PropertyTypeBuilder desc : ftb.properties()){
                for(ColumnMetaModel col : cols){
                    if(desc.getName().tip().toString().equals(col.getName())){
                        final AttributeTypeBuilder atb = (AttributeTypeBuilder) desc;
                        atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
                        break;
                    }
                }
            }


            //find imported keys -----------------------------------------------
            Iterator<Map> ite = cacheImportedKeys.filter(filter(ImportedKey.FKTABLE_SCHEM, schemaName, ImportedKey.FKTABLE_NAME, tableName));
            while(ite.hasNext()){
                final Map result = ite.next();
                String relationName = (String)result.get(ImportedKey.PK_NAME);
                if(relationName==null) relationName = (String)result.get(ImportedKey.FK_NAME);
                final String localColumn = (String)result.get(ImportedKey.FKCOLUMN_NAME);
                final String refSchemaName = (String)result.get(ImportedKey.PKTABLE_SCHEM);
                final String refTableName = (String)result.get(ImportedKey.PKTABLE_NAME);
                final String refColumnName = (String)result.get(ImportedKey.PKCOLUMN_NAME);
                final int deleteRule = ((Number)result.get(ImportedKey.DELETE_RULE)).intValue();
                final boolean deleteCascade = DatabaseMetaData.importedKeyCascade == deleteRule;
                final RelationMetaModel relation = new RelationMetaModel(relationName,localColumn,
                        refSchemaName, refTableName, refColumnName, true, deleteCascade);
                table.importedKeys.add(relation);

                if(refSchemaName!=null && !visitedSchemas.contains(refSchemaName)) requieredSchemas.add(refSchemaName);

                //set the information
                for(PropertyTypeBuilder desc : ftb.properties()){
                    if(desc.getName().tip().toString().equals(localColumn)){
                        final AttributeTypeBuilder atb = (AttributeTypeBuilder) desc;
                        atb.addCharacteristic(JDBCFeatureStore.JDBC_PROPERTY_RELATION).setDefaultValue(relation);
                        break;
                    }
                }
            }

            //find exported keys -----------------------------------------------
            ite = cacheExportedKeys.filter(filter(ImportedKey.PKTABLE_SCHEM, schemaName, ImportedKey.PKTABLE_NAME, tableName));
            while(ite.hasNext()){
                final Map result = ite.next();
                String relationName = (String)result.get(ExportedKey.FKCOLUMN_NAME);
                if(relationName==null) relationName = (String)result.get(ExportedKey.FK_NAME);
                final String localColumn = (String)result.get(ExportedKey.PKCOLUMN_NAME);
                final String refSchemaName = (String)result.get(ExportedKey.FKTABLE_SCHEM);
                final String refTableName = (String)result.get(ExportedKey.FKTABLE_NAME);
                final String refColumnName = (String)result.get(ExportedKey.FKCOLUMN_NAME);
                final int deleteRule = ((Number)result.get(ImportedKey.DELETE_RULE)).intValue();
                final boolean deleteCascade = DatabaseMetaData.importedKeyCascade == deleteRule;
                table.exportedKeys.add(new RelationMetaModel(relationName, localColumn,
                        refSchemaName, refTableName, refColumnName, false, deleteCascade));

                if(refSchemaName!=null && !visitedSchemas.contains(refSchemaName)) requieredSchemas.add(refSchemaName);
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
        table.tableType = ftb;
        return table;
    }

    private AttributeType analyzeColumn(final Map columnSet, final Connection cx) throws SQLException, DataStoreException{
        final SQLDialect dialect = store.getDialect();
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

        final String schemaName     = (String) columnSet.get(Column.TABLE_SCHEM);
        final String tableName      = (String) columnSet.get(Column.TABLE_NAME);
        final String columnName     = (String) columnSet.get(Column.COLUMN_NAME);
        final int columnSize        = ((Number)columnSet.get(Column.COLUMN_SIZE)).intValue();
        final int columnDataType    = ((Number)columnSet.get(Column.DATA_TYPE)).intValue();
        final String columnTypeName = (String) columnSet.get(Column.TYPE_NAME);
        final String columnNullable = (String) columnSet.get(Column.IS_NULLABLE);

        atb.setName(columnName);
        atb.setLength(columnSize);

        try {
            dialect.decodeColumnType(atb, cx, columnTypeName, columnDataType, schemaName, tableName, columnName);
        } catch (SQLException e) {
            throw new DataStoreException("Error occurred analyzing column : " + columnName, e);
        }

        atb.setMinimumOccurs(Column.VALUE_NO.equalsIgnoreCase(columnNullable) ? 1 : 0);
        atb.setMaximumOccurs(1);

        return atb.build();
    }

    /**
     * Analyze the metadata of the ResultSet to rebuild a feature type.
     *
     * @param result
     * @param name
     * @return FeatureType
     * @throws SQLException
     * @throws org.apache.sis.storage.DataStoreException
     */
    public FeatureType analyzeResult(final ResultSet result, final String name) throws SQLException, DataStoreException{
        final SQLDialect dialect = store.getDialect();
        final String namespace = store.getDefaultNamespace();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        if (namespace != null) {
            ftb.setName(namespace, name);
        } else {
            ftb.setName(name);
        }

        final ResultSetMetaData metadata = result.getMetaData();
        final int nbcol = metadata.getColumnCount();

        for(int i=1; i<=nbcol; i++){
            final String columnName = metadata.getColumnName(i);
            final String columnLabel = metadata.getColumnLabel(i);
            final String typeName = metadata.getColumnTypeName(i);
            final String schemaName = metadata.getSchemaName(i);
            final String tableName = metadata.getTableName(i);
            final int sqlType = metadata.getColumnType(i);
            final String sqlTypeName = metadata.getColumnTypeName(i);

            //search if we already have this minute
            PropertyType desc = null;
            final SchemaMetaModel schema = getSchemaMetaModel(schemaName);
            if(schema != null){
                TableMetaModel table = schema.getTable(tableName);
                if(table != null){
                    try{
                        desc = table.getType(TableMetaModel.View.SIMPLE_FEATURE_TYPE).build().getProperty(columnName);
                    }catch(PropertyNotFoundException ex){}
                }
            }

            if(desc == null){
                //could not find the original type
                //this column must be calculated
                final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

                final int nullable = metadata.isNullable(i);
                atb.setName(ensureGMLNS(namespace, columnLabel));
                atb.setMinimumOccurs(nullable == metadata.columnNullable ? 0 : 1);
                atb.setMaximumOccurs(1);

                atb.setName(ensureGMLNS(namespace, columnLabel));
                Connection cx = null;
                try {
                    cx = store.getDataSource().getConnection();
                    final Class type = dialect.getJavaType(sqlType, sqlTypeName);
                    if (type.equals(Geometry.class)) {
                        // try to determine the real geometric type
                        dialect.decodeGeometryColumnType(atb, cx, result, i, true);
                    } else {
                        atb.setName(columnLabel); // why so this a sencond time ?
                        atb.setValueClass(type);
                    }
                } catch (SQLException e) {
                    throw new DataStoreException("Error occurred analyzing column : " + columnName, e);
                } finally {
                    closeSafe(store.getLogger(),cx);
                }

                desc = atb.build();
            }

            ftb.addProperty(desc);
        }

        return ftb.build();
    }

    /**
     * Rebuild simple feature types for each table.
     */
    private void reverseSimpleFeatureTypes(final Connection cx){
        final SQLDialect dialect = store.getDialect();


        for(final SchemaMetaModel schema : schemas.values()){
            for(final TableMetaModel table : schema.tables.values()){
                final String tableName = table.name;

                //fill the namespace--------------------------------------------
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder(table.tableType.build());
                final String namespace = store.getDefaultNamespace();
                final String featureName = ftb.getName().tip().toString();
                if (namespace != null) {
                    ftb.setName(namespace, featureName);
                } else {
                    ftb.setName(featureName);
                }

                final List<PropertyTypeBuilder> descs = ftb.properties();

                for(int i=0,n=descs.size(); i<n; i++){
                    final AttributeTypeBuilder atb = (AttributeTypeBuilder) descs.get(i);
                    final String name = atb.getName().tip().toString();

                    atb.setName(ensureGMLNS(namespace,name));

                    //Set the CRS if it's a geometry
                    final Class binding = atb.getValueClass();
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
                            atb.addCharacteristic(JDBCFeatureStore.JDBC_PROPERTY_SRID).setDefaultValue(srid);
                        }
                    }
                }

                table.simpleFeatureType = ftb;
            }
        }

    }

    /**
     * Rebuild complex feature types using foreign key relations.
     */
    private void reverseComplexFeatureTypes(){

        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

        //result map
        final Map<String,TableMetaModel> builded = new HashMap<>();

        //first pass to create the real types but without relations types-------
        //since we must have all of them before creating relations
        final List<Object[]> secondPass = new ArrayList<>();

        for(final SchemaMetaModel schema : schemas.values()){
            for(final TableMetaModel table : schema.tables.values()){
                final String code = schema.name +"."+table.name;
                builded.put(code, table);

                //create the complex model by replacing descriptors
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder(table.simpleFeatureType.build());

                // add 0:1 relations operations --------------------------------
                for(final RelationMetaModel relation : table.importedKeys){

                    final GenericName relationName = NamesExt.create(store.getDefaultNamespace(), relation.getRelationName());
                    final DBRelationOperation op = new DBRelationOperation(relationName, store, relation, relation.getForeignTable());
                    ftb.addProperty(op);

                    final Object[] futur = new Object[]{table, relation};
                    secondPass.add(futur);
                }

                // add N:1 relations operations---------------------------------
                for(final RelationMetaModel relation : table.exportedKeys){

                    String relationNameTip = relation.getRelationName();
                    if(relationNameTip==null){
                        //find an appropriate name
                        relationNameTip = relation.getForeignColumn();
                        for(PropertyTypeBuilder dpd : ftb.properties()){
                            if(relationNameTip.equals(dpd.getName().tip().toString())){
                                //name already used, make it unique by including reference table name
                                relationNameTip = relation.getForeignTable()+ASSOCIATION_SEPARATOR+relation.getForeignColumn();
                                break;
                            }
                        }
                    }


                    final GenericName relationName = NamesExt.create(store.getDefaultNamespace(), relationNameTip);
                    final DBRelationOperation op = new DBRelationOperation(relationName, store, relation, relation.getForeignTable());
                    ftb.addProperty(op);

                    final Object[] futur = new Object[]{table, relation};
                    secondPass.add(futur);
                }

                table.complexFeatureType = ftb;
                table.allType = ftb;
            }
        }

//        //second pass to fill relations-----------------------------------------
//        for(Object[] futur : secondPass){
//            final TableMetaModel primaryTable = (TableMetaModel) futur[0];
//            final RelationMetaModel relation = (RelationMetaModel) futur[1];
//            final String relCode = relation.getForeignSchema() +"."+relation.getForeignTable();
//            final TableMetaModel foreignTable = (TableMetaModel)builded.get(relCode);
//
//            //update complex feature type
//            final FeatureTypeBuilder cft = primaryTable.getType(TableMetaModel.View.COMPLEX_FEATURE_TYPE);
//            modifyField(foreignTable, relation, cft);
//
//            //update full type
//            final FeatureTypeBuilder allt = primaryTable.getType(TableMetaModel.View.ALLCOMPLEX);
//            modifyField(foreignTable, relation, allt);
//        }

    }

//    private int modifyField(final TableMetaModel foreignTable,
//            final RelationMetaModel relation, final FeatureTypeBuilder candidate){
//        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
//
//        final Collection<PropertyTypeBuilder> descs = candidate.properties();
//        final FeatureTypeBuilder foreignType = foreignTable.getType(TableMetaModel.View.COMPLEX_FEATURE_TYPE);
//
//        //create the new association descriptor derivated
//        atb.reset();
//        atb.copy(foreignType);
//        atb.setParentType(null);
//
//        //find the descriptor to replace
//        int index = -1;
//        String searchedName = relation.isImported() ? relation.getCurrentColumn() : relation.getForeignTable()+ASSOCIATION_SEPARATOR+relation.getForeignColumn();
//        for (int i = 0, n = descs.size(); i < n; i++) {
//            final PropertyDescriptor pd = descs.get(i);
//            if(pd.getName().tip().toString().equals(searchedName)){
//                index = i;
//                break;
//            }
//        }
//
//        // Foreign property cannot be found using its complete name, so we'll try to get it with simple name.
//        if (index < 0 && !relation.isImported()) {
//            searchedName = relation.getForeignColumn();
//            for (int i = 0, n = descs.size(); i < n; i++) {
//                final PropertyDescriptor pd = descs.get(i);
//                if (pd.getName().tip().toString().equals(searchedName)) {
//                    index = i;
//                    break;
//                }
//            }
//        }
//
//        final PropertyDescriptor baseDescriptor = descs.get(index);
//        adb.reset();
//        adb.copy(baseDescriptor);
//        adb.setDefaultValue(null);
//
//        final PropertyDescriptor newDescriptor;
//        if(relation.isDeleteCascade()){
//            adb.setType(foreignType);
//            newDescriptor = adb.buildDescriptor();
//        }else{
//            adb.setType(atb.buildAssociationType(foreignType));
//            newDescriptor = adb.buildAssociationDescriptor();
//        }
//
//        newDescriptor.getUserData().put(JDBCFeatureStore.JDBC_PROPERTY_RELATION,relation);
//        candidate.changeProperty(index, newDescriptor);
//
//        return index;
//    }

}
