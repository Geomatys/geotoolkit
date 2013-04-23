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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Stores additional databse SQL encoding informations.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface SQLDialect {
    
    String getTableEscape();
    
    Class getJavaType(int sqlType, String sqlTypeName);
    
    String getSQLType(Class javaType) throws SQLException;

    String getColumnSequence(Connection cx, String schemaName, String tableName, String columnName) throws SQLException;
    
}
