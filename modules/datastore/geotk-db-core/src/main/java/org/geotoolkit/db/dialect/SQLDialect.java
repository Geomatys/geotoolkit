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
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;

/**
 * Stores additional databse SQL encoding informations.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface SQLDialect {
    
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
    Filter[] splitFilter(Filter filter);
    
    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO CREATE SQL QUERIES ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Encode filter to SQL.
     * @param filter
     * @return SQL String
     */
    String encodeFilter(Filter filter);

    void encodeColumnName(StringBuilder sql, String name);

    void encodeSchemaName(StringBuilder sql, String name);

    void encodeTableName(StringBuilder sql, String name);

    void encodeGeometryColumn(StringBuilder sql, GeometryDescriptor gatt, int srid, Hints hints);

    void encodeColumnAlias(StringBuilder sql, String name);
    
    void encodeLimitOffset(StringBuilder sql, Integer limit, int offset);
    
    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO READ FROM RESULTSET //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final String column, final GeometryFactory factory) throws IOException, SQLException;

    Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final int column, final GeometryFactory factory) throws IOException, SQLException;
    
}
