/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.geotools.util.Converters;

import com.vividsolutions.jts.geom.Geometry;


/**
 * SQL dialect which uses prepared statements for database interaction.
 * 
 * @author Justin Deoliveira, OpenGEO
 *
 */
public abstract class PreparedStatementSQLDialect extends SQLDialect {

    protected PreparedStatementSQLDialect(JDBCDataStore dataStore) {
        super(dataStore);
        
    }
    
    /**
     * Prepares the geometry value for a prepared statement.
     * <p>
     * This method should be overridden if the implementation needs to 
     * wrap the geometry placeholder in the function. The default implementation
     * just appends the default placeholder: '?'.
     * </p>
     * @param g The geometry value.
     * @param srid The spatial reference system of the geometry.
     * @param binding The class of the geometry.
     * @param sql The prepared sql statement buffer. 
     */
    public void prepareGeometryValue(final Geometry g, final int srid, final Class binding,
                                     final StringBuffer sql)
    {
        sql.append( "?" );
    }
    
    /**
     * Prepares a function argument for a prepared statement.
     * 
     * @param clazz The mapped class of the argument.
     * @param sql The prepared sql statement buffer
     */
    public void prepareFunctionArgument(final Class clazz, final StringBuffer sql) {
        sql.append("?");
    }
    
    /**
     * Sets the geometry value into the prepared statement. 
     * @param g The geometry
     * @param srid the geometry native srid (should be forced into the encoded geometry)
     * @param binding the geometry type
     * @param ps the prepared statement
     * @param column the column index where the geometry is to be set
     * @throws SQLException
     */
    public abstract void setGeometryValue(final Geometry g, final int srid, final Class binding,
            final PreparedStatement ps, final int column) throws SQLException;

    /**
     * Sets a value in a prepared statement, for "basic types" (non-geometry).
     * <p>
     * Subclasses should override this method if they need to do something custom or they 
     * wish to support non-standard types. 
     * </p>
     * @param value the value.
     * @param binding The class of the value.
     * @param ps The prepared statement.
     * @param column The column the value maps to.
     * @param cx The database connection.
     * @throws SQLException
     */
    public void setValue(Object value, final Class binding, final PreparedStatement ps,
            final int column, final Connection cx) throws SQLException
    {
        
        //get the sql type
        final Integer sqlType = dataStore.getMapping( binding );
        
        //handl null case
        if ( value == null ) {
            ps.setNull( column, sqlType );
            return;
        }
        
        //convert the value if necessary
        if ( ! binding.isInstance( value ) ) {
            final Object converted = Converters.convert(value, binding);
            if ( converted != null ) {
                value = converted;
            } else {
                dataStore.getLogger().warning( "Unable to convert " + value + " to " + binding.getName() );
            }
        }
        
        switch( sqlType ) {
            case Types.VARCHAR:
                ps.setString( column, (String) value );
                break;
            case Types.BOOLEAN:
                ps.setBoolean( column, (Boolean) value );
                break;
            case Types.SMALLINT:
                ps.setShort( column, (Short) value );
                break;
            case Types.INTEGER:
                ps.setInt( column, (Integer) value );
                break;
            case Types.BIGINT:
                ps.setLong( column, (Long) value );
                break;
            case Types.REAL:
                ps.setFloat( column, (Float) value );
                break;
            case Types.DOUBLE:
                ps.setDouble( column, (Double) value );
                break;
            case Types.NUMERIC:
                ps.setBigDecimal( column, (BigDecimal) value );
                break;
            case Types.DATE:
                ps.setDate( column, (Date) value );
                break;
            case Types.TIME:
                ps.setTime( column, (Time) value );
                break;
            case Types.TIMESTAMP:
                ps.setTimestamp( column, (Timestamp) value );
                break;
            default:
                ps.setObject( column, value );
        }
        
    }
    
    public PreparedFilterToSQL createPreparedFilterToSQL() {
        final PreparedFilterToSQL f2s = new PreparedFilterToSQL();
        f2s.setCapabilities(BASE_DBMS_CAPABILITIES);
        return f2s;
    }
}
