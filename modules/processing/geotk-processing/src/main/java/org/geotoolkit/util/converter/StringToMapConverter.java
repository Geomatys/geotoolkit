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
package org.geotoolkit.util.converter;


import java.util.HashMap;
import java.util.Map;
import org.apache.sis.util.UnconvertibleObjectException;


/**
 * Implementation of ObjectConverter to convert a String into a Map.
 * Structure is : {login=max, password=secret}
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
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Map> getTargetClass() {
        return Map.class ;
    }
    @Override
    public Map apply(final String str) throws UnconvertibleObjectException {
        if(str == null) throw new UnconvertibleObjectException("Null string");

        String trimmed = str.trim();

        if(trimmed.charAt(0) != '{' || trimmed.charAt(trimmed.length()-1) != '}'){
            throw new UnconvertibleObjectException("Invalid string format, string must start with '{' and end with '}' . String is : "+trimmed );
        }
        trimmed = trimmed.substring(1, trimmed.length()-1);

        final Map<String,Object> parameters = new HashMap<String, Object>();

        if(!trimmed.isEmpty()){
            for(String part : trimmed.split(",")){
                part = part.trim();
                if(part.isEmpty()){
                    throw new UnconvertibleObjectException("Invalid string format, empty parameter . String is : "+str );
                }

                final int i = part.indexOf('=');
                if(i<= 0){
                    throw new UnconvertibleObjectException("Invalid string format, parameter structure must be 'key=value' . String is : "+part );
                }
                parameters.put(part.substring(0, i), part.substring(i+1));
            }
        }

        return parameters;
    }
}


