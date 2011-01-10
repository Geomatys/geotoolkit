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

import java.util.ResourceBundle;
import org.geotoolkit.util.ResourceInternationalString;
import org.opengis.util.InternationalString;

/**
 * Internalization of all styling widgets.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class MessageBundle {

    private static final String PATH = "org/geotoolkit/gui/swing/resource/Bundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PATH);
    
    /**
     * Get the local string for the given key.
     */
    public static String getString(final String key){
        return BUNDLE.getString(key);
    }

    public static InternationalString getI18NString(final String key){
        return new ResourceInternationalString(PATH, key);
    }

}
