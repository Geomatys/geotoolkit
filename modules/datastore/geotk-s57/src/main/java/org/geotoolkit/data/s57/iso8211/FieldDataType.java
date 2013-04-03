/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57.iso8211;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public enum FieldDataType {
    TEXT("A"),
    INTEGER("I"),
    REAL_FIXED("R"),
    REAL_FLOAT("S"),
    LOGICAL("C"),
    INTEGER_UNSIGNED("B1"),
    INTEGER_SIGNED("B2"),
    REAL("B4"),
    BINARY("B");//placed at the end to est B1,B2,B4 before
    
    private final String code;
    
    private FieldDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    public static List<SubFieldDescription> get(String value) throws IOException {
        if(value.charAt(0) != '(' || value.charAt(value.length()-1) != ')'){
            throw new IOException("Type should be wrapped between () but was :"+value);
        }
        final List<SubFieldDescription> types = new ArrayList<SubFieldDescription>();
        
        value = value.substring(1, value.length()-1);
        final String[] parts = value.split(",");
        for(String p : parts){
            int repetition = 1;
            //check for a repetition
            if(Character.isDigit(p.charAt(0))){
                int lastDigit = 0;
                for(int i=1;Character.isDigit(p.charAt(i));i++) lastDigit = i;
                repetition = Integer.valueOf(p.substring(0, lastDigit+1));
                p = p.substring(lastDigit+1);
            }
            p = p.toUpperCase();
            //search the type
            FieldDataType type = null;
            for(FieldDataType t : values()){
                if(p.startsWith(t.code)){
                    type = t;
                    p = p.substring(t.code.length());
                    break;
                }
            }
            if(type == null){
                throw new IOException("Unreconized field type :"+value);
            }
            //parse length if set
            Integer length = null;
            if(!p.isEmpty()){
                if(p.charAt(0) == '('){
                    p = p.substring(1, p.length()-1);
                }
                length = Integer.valueOf(p);
            }
            
            //create types
            for(int i=0;i<repetition;i++){
                final SubFieldDescription sft = new SubFieldDescription();
                sft.setType(type);
                sft.setLength(length);
                types.add(sft);
            }
        }
        
        return types;
    }
    
    
    
}
