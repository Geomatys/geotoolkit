/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.opengis.util.InternationalString;

/**
 * Internalization of all styling widgets.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public final class MessageBundle {

    private static final String PATH = "org/geotoolkit/gui/swing/resource/Bundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PATH);
    
    /**
     * Get the local string for the given key.
     */
    public static String getString(final String key){
        try{
            String text =  BUNDLE.getString(key);
            if(text.startsWith("$")){
                text = getString(text.substring(1));
            }
            return text;
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

    private MessageBundle(){}
    
}
