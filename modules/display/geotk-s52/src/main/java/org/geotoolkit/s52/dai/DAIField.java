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
package org.geotoolkit.s52.dai;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DAIField {
    
    private final String code;

    public DAIField(String code) {
        this.code = code;
    }

    /**
     * All fields have a unique code.
     * @return String, never null
     */
    public String getCode() {
        return code;
    }
        
    /**
     * Read subfield from given string.
     * @param str 
     */
    public final void read(String str){
        if(str.startsWith(code)){
            str = str.substring(code.length());
            str.trim();
        }
        readSubFields(str);
    }
    
    protected abstract void readSubFields(String str);
    
    public DAIField newInstance(){
        try {
            return (DAIField)this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create a new instance of : "+this.getClass().getName());
        }
    }
    
}
