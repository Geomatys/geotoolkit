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
package org.geotoolkit.process.converters;


import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;


/**
 * Implementation of ObjectConverter to convert a String into a Map.
 * Structure is : {login:max, password:secret}
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StringToMapConverter extends SimpleConverter<String, Map> {

    private static StringToMapConverter INSTANCE;

    private StringToMapConverter(){
    }

    public static StringToMapConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToMapConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends Map> getTargetClass() {
        return Map.class ;
    }
    @Override
    public Map convert(String s) throws NonconvertibleObjectException {
        if(s == null) throw new NonconvertibleObjectException("Null string");
        
        s = s.trim();
        if(s.charAt(0) != '{' || s.charAt(s.length()-1) != '}'){
            throw new NonconvertibleObjectException("Invalid string format, string must start with '{' and end with '}' .");
        }
        s = s.substring(1, s.length()-1);
        
        final Map<String,Object> parameters = new HashMap<String, Object>();
        
        for(String part : s.split(",")){
            part = part.trim();
            final int i = part.indexOf(':');
            if(i<= 0){
                throw new NonconvertibleObjectException("Invalid string format, parameter structure must be 'key:value' .");
            }            
            parameters.put(part.substring(0, i), part.substring(i+1));
        }
        
        return parameters;
    }
}


