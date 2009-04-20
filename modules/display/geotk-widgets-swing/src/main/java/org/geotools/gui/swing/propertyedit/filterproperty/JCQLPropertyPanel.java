/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.propertyedit.filterproperty;

import org.geotools.data.DefaultQuery;
import org.geotools.gui.swing.filter.JCQLFilterPanel;
import org.geotools.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.map.MapLayer;

/**
 * CQL property panel
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JCQLPropertyPanel extends JCQLFilterPanel implements PropertyPane{

    private MapLayer layer;
        
    private void parse(){
        setLayer(layer);
        setFilter(layer.getQuery().getFilter());
    }
    
    @Override
    public void setTarget(Object target) {
        if (target instanceof MapLayer) {
            layer = (MapLayer) target;
            parse();
        }
    }

    @Override
    public void apply() {  
        if(layer !=null){
            layer.setQuery(new DefaultQuery("cqlfilter", getFilter()));
        }
    }

    @Override
    public void reset() {
        parse();
    }

}
