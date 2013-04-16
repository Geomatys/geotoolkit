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
package org.geotoolkit.data.s57.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.Classes;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57ModelObject {
 
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UCS2 = Charset.forName("UTF-16");
    
    /** lexical levels should be filled before reading values */
    public S57Constants.LexicalLevel attfLexicalLevel;
    public S57Constants.LexicalLevel natfLexicalLevel;
    
    public void read(Field isofield) throws IOException {
        throw new IOException("Not implemented yet.");
    }
    
    public Field write() throws IOException {
        throw new IOException("Not implemented yet.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        final String name = Classes.getShortClassName(this);
        final List lst = new ArrayList();
        final java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
        for(java.lang.reflect.Field f : fields){
            if(java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            try {
                Object value = f.get(this);
                lst.add(f.getName() +" = "+value);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(S57ModelObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(S57ModelObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Trees.toString(name, lst);
    }
    
    protected static String toString(Object candidate){
        if(candidate==null) return null;
        
        if(candidate instanceof String){
            return (String)candidate;
        }else if(candidate instanceof byte[]){
            return new String(((byte[])candidate));
        }
        return candidate.toString();
    }
    
    protected static Integer toInteger(Object candidate){
        if(candidate instanceof byte[]){
            candidate = new String(((byte[])candidate));
        }
        
        if(candidate instanceof String){
            return Integer.valueOf((String)candidate);
        }else if(candidate instanceof Number){
            return ((Number)candidate).intValue();
        }
        return null;
    }
    
    protected static Long toLong(Object candidate){
        if(candidate instanceof byte[]){
            candidate = new String(((byte[])candidate));
        }
        
        if(candidate instanceof String){
            return Long.valueOf((String)candidate);
        }else if(candidate instanceof Number){
            return ((Number)candidate).longValue();
        }
        return null;
    }
    
    protected static Double toDouble(Object candidate){
        if(candidate instanceof byte[]){
            candidate = new String(((byte[])candidate));
        }
        
        if(candidate instanceof String){
            return Double.valueOf((String)candidate);
        }else if(candidate instanceof Number){
            return ((Number)candidate).doubleValue();
        }
        return null;
    }
    
}
