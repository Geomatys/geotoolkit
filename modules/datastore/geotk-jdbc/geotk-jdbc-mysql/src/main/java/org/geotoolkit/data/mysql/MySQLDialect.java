/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.mysql;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.dialect.AbstractSQLDialect;

import org.opengis.feature.type.GeometryDescriptor;


public class MySQLDialect extends AbstractSQLDialect {

    public MySQLDialect(final JDBCDataStore dataStore) {
        super(dataStore,"`");
        //register the base mapping
        initBaseClassToSqlMappings(classToSqlTypeMappings);
        initBaseSqlTypeNameToClassMappings(sqlTypeNameToClassMappings);
        initBaseSqlTypeToClassMappings(sqlTypeToClassMappings);
        initBaseSqlTypeToSqlTypeNameOverrides(sqlTypeToSqlTypeNameOverrides);

        sqlTypeToSqlTypeNameOverrides.put(Types.VARCHAR, "VARCHAR");
        sqlTypeToSqlTypeNameOverrides.put(Types.BOOLEAN, "BOOL");
    }
    
    @Override
    public Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
            final String column, final GeometryFactory factory, final Connection cx)
            throws IOException, SQLException{
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void encodeGeometryEnvelope(final String tableName, final String geometryColumn,
            final StringBuilder sql){
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public Envelope decodeGeometryEnvelope(final ResultSet rs, final int column,
            final Connection cx) throws SQLException, IOException{
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void encodePrimaryKey(final Class binding, final String sqlType, final StringBuilder sql) {
        if(Integer.class.isAssignableFrom(binding) || Short.class.isAssignableFrom(binding)){
            sql.append(" SERIAL ");
        }else if(Long.class.isAssignableFrom(binding)){
            sql.append(" BIGSERIAL ");
        }else{
             sql.append(' ').append(sqlType).append(' ');
        }
        sql.append("PRIMARY KEY");
    }

    @Override
    public void encodeGeometryValue(Geometry value, final int srid, final StringBuilder sql) throws IOException{
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public FilterToSQL createFilterToSQL(){
        final MySQLFilterToSQL sql = new MySQLFilterToSQL(this);
        return sql;
    }

    @Override
    public boolean isLimitOffsetSupported(){
        return true;
    }

    @Override
    public void applyLimitOffset(final StringBuilder sql, final int limit, final int offset){
        if (limit > 0 && limit < Integer.MAX_VALUE) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        } else if (offset > 0) {
            sql.append(" OFFSET ").append(offset);
        }
    }

}
