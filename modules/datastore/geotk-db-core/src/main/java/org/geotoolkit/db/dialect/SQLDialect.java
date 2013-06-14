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
package org.geotoolkit.db.dialect;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.sis.util.Version;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Stores additional databse SQL encoding informations.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface SQLDialect {
    
    Version getVersion(String schema) throws SQLException;
    
    FilterCapabilities getFilterCapabilities();
    
    FilterToSQL getFilterToSQL(ComplexType featureType);
    
    /**
     * Escape sequence for table names.
     * @return String
     */
    String getTableEscape();
    
    Class getJavaType(int sqlType, String sqlTypeName);
    
    String getSQLType(Class javaType) throws SQLException;

    String getColumnSequence(Connection cx, String schemaName, String tableName, String columnName) throws SQLException;
    
    /**
     * Test if a table is to be used as a FeatureType.
     * @param name
     * @return true if table should be ignored as a feature type.
     */
    boolean ignoreTable(String name);
    
    /**
     * Split filter in two, first part can be encoded in sql while second part
     * must be handle after the request.
     * @param filter not null
     * @return array of two filters.
     */
    Filter[] splitFilter(Filter filter, ComplexType type);
    
    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO CREATE SQL QUERIES ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Encode filter to SQL.
     * @param filter
     * @return SQL String
     */
    String encodeFilter(Filter filter, ComplexType type);

    void encodeColumnName(StringBuilder sql, String name);

    void encodeColumnType(StringBuilder sql, String sqlTypeName, Integer length);
    
    void encodeSchemaName(StringBuilder sql, String name);

    void encodeTableName(StringBuilder sql, String name);

    /**
     * Encode schema and table name portion of an sql query.
     * @param tableName
     * @param sql 
     */
    void encodeSchemaAndTableName(StringBuilder sql, String databaseSchema, String tableName);
    
    void encodeGeometryColumn(StringBuilder sql, GeometryDescriptor gatt, int srid, Hints hints);

    void encodeColumnAlias(StringBuilder sql, String name);
    
    void encodeLimitOffset(StringBuilder sql, Integer limit, int offset);
    
    void encodeValue(StringBuilder sql, Object value, Class type);

    void encodeGeometryValue(StringBuilder sql, Geometry value, int srid) throws DataStoreException;
    
    void encodePrimaryKey(StringBuilder sql, Class binding, String sqlType);

    void encodePostColumnCreateTable(StringBuilder sql, AttributeDescriptor att);

    void encodePostCreateTable(StringBuilder sql, String tableName);

    void postCreateTable(String schemaName, FeatureType featureType, Connection cx) throws SQLException;

    
    ////////////////////////////////////////////////////////////////////////////
    // PRIMARY KEY CALCULATION METHOS //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    Object nextValue(ColumnMetaModel column, Connection cx) throws SQLException, DataStoreException;
    
    
    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO READ FROM RESULTSET //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    void decodeColumnType(final AttributeTypeBuilder atb, final Connection cx,
            final String typeName, final int datatype, final String schemaName,
            final String tableName, final String columnName) throws SQLException;
    
    Integer getGeometrySRID(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException;

    CoordinateReferenceSystem createCRS(final int srid, final Connection cx) throws SQLException;
    
    Object decodeAttributeValue(AttributeDescriptor descriptor, ResultSet rs, 
            int i) throws SQLException;
    
    Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs,
        String column) throws IOException, SQLException;

    Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs,
        int column) throws IOException, SQLException;

    
}
