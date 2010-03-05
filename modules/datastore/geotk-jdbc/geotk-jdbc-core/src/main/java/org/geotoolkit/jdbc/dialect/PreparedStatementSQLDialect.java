/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.geotoolkit.data.jdbc.PreparedFilterToSQL;

import com.vividsolutions.jts.geom.Geometry;


/**
 * SQL dialect which uses prepared statements for database interaction.
 * 
 * @author Justin Deoliveira, OpenGEO
 *
 * @module pending
 */
public interface PreparedStatementSQLDialect extends SQLDialect {
    
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
    void prepareGeometryValue(final Geometry g, final int srid, final Class binding,
                                     final StringBuilder sql);
    
    /**
     * Prepares a function argument for a prepared statement.
     * 
     * @param clazz The mapped class of the argument.
     * @param sql The prepared sql statement buffer
     */
    void prepareFunctionArgument(final Class clazz, final StringBuilder sql);
    
    /**
     * Sets the geometry value into the prepared statement. 
     * @param g The geometry
     * @param srid the geometry native srid (should be forced into the encoded geometry)
     * @param binding the geometry type
     * @param ps the prepared statement
     * @param column the column index where the geometry is to be set
     * @throws SQLException
     */
    void setGeometryValue(final Geometry g, final int srid, final Class binding,
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
    void setValue(Object value, final Class binding, final PreparedStatement ps,
            final int column, final Connection cx) throws SQLException;
    
    PreparedFilterToSQL createPreparedFilterToSQL();

}
