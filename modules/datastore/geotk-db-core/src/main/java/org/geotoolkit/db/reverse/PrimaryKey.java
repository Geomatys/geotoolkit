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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

/**
 * Describe a table primary key.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PrimaryKey {
        
    private final String tableName;
    private final List<ColumnMetaModel> columns;

    public PrimaryKey(String tableName) {
        this(tableName,null);
    }
    
    public PrimaryKey(String tableName, List<ColumnMetaModel> columns) {
        this.tableName = tableName;
        if(columns == null) columns = Collections.emptyList();
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnMetaModel> getColumns() {
        return columns;
    }

    public boolean isNull(){
        return columns.isEmpty();
    }
    
    /**
     * Encodes a feature id from a primary key and result set values.
     */
    public String encodeFID(final ResultSet rs) throws SQLException {

        final int size = columns.size();
        
        if (size == 0) {
            // generate a random id
            return SimpleFeatureBuilder.createDefaultFeatureId();
        }else if (size == 1) {
            // single value compose the id, use it directly
            return escapeDot(rs.getString(columns.get(0).getName()));
        }else{
            // several values compose the id
            final Object[] keyValues = new Object[size];
            for(int i=0; i<size; i++) {
                final ColumnMetaModel column = columns.get(i);
                keyValues[i] = rs.getString(column.getName());
            }
            return encodeFID(keyValues);
        }
    }

    private static String encodeFID(final Object[] keyValues) {
        final StringBuilder fid = new StringBuilder();
        for (Object val : keyValues) {
            fid.append(escapeDot(val)).append('.');
        }
        //remove last dot
        fid.setLength(fid.length() - 1);
        return fid.toString();
    }

    private static String escapeDot(Object obj){
        String str = String.valueOf(obj);
        return str.replaceAll("\\.", "◼");
    }
    
    private static String unescapeDot(String str){
        return str.replaceAll("◼",".");
    }
    
}
