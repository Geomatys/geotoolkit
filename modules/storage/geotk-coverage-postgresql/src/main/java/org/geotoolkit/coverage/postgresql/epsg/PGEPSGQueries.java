/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.coverage.postgresql.epsg;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contain all queries, backed by a property file.
 *
 * @author Johann Sorel (Geomatys)
 */
public enum PGEPSGQueries {

    NEXT_CODE,
    
    CREATE_ALIAS,
    CREATE_AREA,
    CREATE_CHANGE,
    CREATE_COORDINATE_AXIS,
    CREATE_COORDINATE_AXIS_NAME,
    CREATE_COORDINATE_REFERENCE_SYSTEM,
    CREATE_COORDINATE_SYSTEM,
    CREATE_COORDINATE_OPERATION,
    CREATE_COORDINATE_OPERATION_METHOD,
    CREATE_COORDINATE_OPERATION_PARAMETER,
    CREATE_COORDINATE_OPERATION_PARAMETER_USAGE,
    CREATE_COORDINATE_OPERATION_PARAMETER_VALUE,
    CREATE_COORDINATE_OPERATION_PATH,
    CREATE_DATUM,
    CREATE_DEPRECATION,
    CREATE_ELLIPSOID,
    CREATE_NAMING_SYSTEM,
    CREATE_PRIME_MERIDIAN,
    CREATE_SUPERSESSION,
    CREATE_UNIT_OF_MEASURE,
    CREATE_VERSION_HISTORY,
    
    FIND_COORDINATE_REFERENCE_SYSTEM,
    FIND_COORDINATE_SYSTEM,
    FIND_DATUM,
    FIND_ELLIPSOID,
    FIND_PRIME_MERIDIAN,
    FIND_UNIT_OF_MEASURE,
    FIND_UNIT_OF_MEASURE_SELF;
    
    private final String query;
    private final Integer[] parameters;

    private PGEPSGQueries() {
        final String stmt = ResourceBundle.getBundle("org/geotoolkit/coverage/postgresql/epsg_queries").getString(name());
        
        final StringBuilder sb = new StringBuilder();
        
        int before = 0;
        final List<Integer> params = new ArrayList<Integer>();
        for(int i=stmt.indexOf('?',before); i>=0; i=stmt.indexOf('?',before)){
            final String part = stmt.substring(before, i);
            final int start = part.indexOf('[');
            final int end = part.indexOf(']');
            if(start < 0 || end < 0){
                throw new IllegalArgumentException("Invalid query "+name()+" : " + stmt);
            }
            final String type = part.substring(start+1, end);
            try {
                final Field f = Types.class.getField(type);
                params.add(f.getInt(null));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Type unknowed :  "+type+", Invalid query "+name()+" : " + stmt);
            }
            
            sb.append(part.substring(0, start));
            sb.append(part.substring(end+1));
            sb.append('?');
            
            before = i+1;
        }
        
        sb.append(stmt.substring(before));
        this.parameters = params.toArray(new Integer[params.size()]);
        this.query = sb.toString();
    }
    
    public String query(){
        return query;
    }
    
    public int getNbParameters(){
        return parameters.length;
    }

    /**
     * Create and fill prepared statement.
     * The original query statement may be modified if some parameters are null.
     * example : SELECT * FROM house WHERE user=[INTEGER]?
     * will be replaced by : SELECT * FROM house WHERE user IS NULL , if paramter is null.
     */
    public PreparedStatement createStatement(final Connection cnx, Object ... params) throws SQLException {
        
        final int nb = getNbParameters();        
        if(nb != params.length){
            throw new SQLException("Was expecting "+nb+" parameters for query but only received "+params.length);
        }
        
        boolean hasNullValue = false;
        for(Object obj : params){
            hasNullValue = (obj==null);
            if(hasNullValue) break;
        }
        
        String query = this.query;
        
        //adapt query for null values
        if(hasNullValue){
            final List<Object> noNullParams = new ArrayList<Object>();
            final StringBuilder sb = new StringBuilder();
            int before = 0;
            
            for(int i=query.indexOf('?',before),k=0; i>=0; i=query.indexOf('?',before),k++){
                final Object param = params[k];                
                final String part = query.substring(before, i);
                
                nullReplace:
                if(param == null){
                    //check if we have a '=' before
                    for(int t=part.length()-1;t>=0;t--){
                        final char c = part.charAt(t);
                        if(c == '='){
                            sb.append(part.substring(0,t));
                            sb.append(" IS NULL ");
                            break nullReplace;
                        }else if(c != ' '){
                            break;
                        }
                    }
                    
                    noNullParams.add(param);
                    sb.append(part);
                    sb.append('?');
                }else{
                    noNullParams.add(param);
                    sb.append(part);
                    sb.append('?');
                }
                
                before = i+1;
            }
            
            //add remaining
            params = noNullParams.toArray();
            sb.append(query.substring(before));
            query = sb.toString();
        }
        
        final PreparedStatement stmt = cnx.prepareStatement(query);
        fill(stmt, params);
        return stmt;
    }
    
    /**
     * Caution : if some arguments may be null consider using 'create' method to
     * automaticly refactor the query.
     */
    public void fillStatement(final PreparedStatement stmt, final Object ... params) throws SQLException{
        
        final int nb = getNbParameters();        
        if(nb != params.length){
            throw new SQLException("Was expecting "+nb+" parameters for query but only received "+params.length);
        }
        
        if(nb == 0){
            //nothing to fill
            return;
        }
        
        fill(stmt, params);
    }
    
    private void fill(final PreparedStatement stmt, final Object ... params) throws SQLException{
                
        for(int i=0;i<params.length;i++){
            final Object param = params[i];
            
            if(param != null){
                stmt.setObject(i+1, params[i], parameters[i]);
            }else{
                stmt.setNull(i+1, parameters[i]);
            }            
        }
    }
    
}
