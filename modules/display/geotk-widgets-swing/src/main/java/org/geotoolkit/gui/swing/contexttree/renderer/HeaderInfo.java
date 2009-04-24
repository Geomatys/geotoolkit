/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2007-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.contexttree.renderer;

import javax.swing.Icon;

/**
 * General header
 *
 * @author Johann Sorel
 */
public class HeaderInfo {

    private final String identify;
    private final String inheader;
    private final Icon comp;
    
    
    /**
     * 
     * @param identify
     * @param inheader
     * @param tooltip
     * @param icon
     */
    public HeaderInfo(String identify, String inheader, Icon icon){
        this.identify = identify;
        this.inheader = inheader;
        this.comp = icon;
    }
        
    public String getHeaderText(){
        return inheader;
    }
    
    public Icon getIcon(){
        return comp;
    }
            
    @Override
    public String toString() {
        return identify;
    }

    
    
    
}
