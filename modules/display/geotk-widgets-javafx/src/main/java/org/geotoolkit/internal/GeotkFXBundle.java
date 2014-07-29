/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 20014, Geomatys
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
package org.geotoolkit.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.opengis.util.InternationalString;

/**
 * Internalization of all javafx widgets.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class GeotkFXBundle {

    private static final String PATH = "org/geotoolkit/gui/javafx/internal/Bundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PATH);
    
    /**
     * Get the local string for the given class and key.
     * The object class name will be pre-concatenate with the key.
     */
    public static String getString(Object base, final String key){
        return getString(base.getClass(), key);
    }
    
    /**
     * Get the local string for the given class and key.
     * The class name will be pre-concatenate with the key.
     */
    public static String getString(Class clazz, final String key){
        try{
            return BUNDLE.getString(clazz.getName()+"."+key);
        }catch(MissingResourceException ex){
            return "Missing key : "+key;
        }
    }
    
    
    /**
     * Get the local string for the given key.
     */
    public static String getString(final String key){
        try{
            return BUNDLE.getString(key);
        }catch(MissingResourceException ex){
            return "Missing key : "+key;
        }
    }

    public static String getString(final String key, Object obj1){
        return getString(key, new Object[]{obj1});
    }

    public static String getString(final String key, final Object[] objects){
        String text = getString(key);
        String pattern;
        for (int i = 0; i < objects.length; i++) {
            pattern = "{"+i+"}";
            if (text.contains(pattern)) {
                text = text.replace(pattern, objects[i].toString());
            }
        }
        return text;
    }

    public static InternationalString getI18NString(final String key){
        try{
            String text =  BUNDLE.getString(key);
            if(text.startsWith("$")){
                return getI18NString(text.substring(1));
            }
        }catch(MissingResourceException ex){
            throw new RuntimeException("Missing resource key : "+key,ex);
        }
        return new ResourceInternationalString(PATH, key);
    }

    private GeotkFXBundle(){}
    
}
