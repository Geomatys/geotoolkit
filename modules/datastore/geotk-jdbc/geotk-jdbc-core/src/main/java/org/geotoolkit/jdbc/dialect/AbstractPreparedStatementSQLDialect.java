/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2020, Geomatys
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
package org.geotoolkit.jdbc.dialect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.geotoolkit.util.Converters;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.data.jdbc.PreparedFilterToSQL;

import com.vividsolutions.jts.geom.Geometry;


/**
 * SQL dialect which uses prepared statements for database interaction.
 * 
 * @author Justin Deoliveira, OpenGEO
 * @module pending
 */
public abstract class AbstractPreparedStatementSQLDialect extends AbstractSQLDialect implements PreparedStatementSQLDialect{

    protected static final char CHAR_INTEROGATION = '?';

    protected AbstractPreparedStatementSQLDialect(final JDBCDataStore dataStore) {
        super(dataStore);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void prepareGeometryValue(final Geometry g, final int srid, final Class binding,
                                     final StringBuilder sql){
        sql.append(CHAR_INTEROGATION);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void prepareFunctionArgument(final Class clazz, final StringBuilder sql) {
        sql.append(CHAR_INTEROGATION);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(Object value, final Class binding, final PreparedStatement ps,
            final int column, final Connection cx) throws SQLException{
        
        //get the sql type
        final Integer sqlType = getMapping( binding );
        
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

    /**
     * {@inheritDoc }
     */
    @Override
    public PreparedFilterToSQL createPreparedFilterToSQL() {
        final PreparedFilterToSQL f2s = new PreparedFilterToSQL(this);
        f2s.setCapabilities(BASE_DBMS_CAPABILITIES);
        return f2s;
    }

}
