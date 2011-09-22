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
package org.geotoolkit.gui.swing.propertyedit.filterproperty;

import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.swing.filter.JCQLFilterPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.map.FeatureMapLayer;

/**
 * CQL property panel
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCQLPropertyPanel extends JCQLFilterPanel implements PropertyPane{

    private FeatureMapLayer layer;
        
    private void parse(){
        setLayer(layer);
        setFilter(layer.getQuery().getFilter());
    }
    
    @Override
    public boolean canHandle(Object target) {
        return target instanceof FeatureMapLayer;
    }
    
    @Override
    public void setTarget(final Object target) {
        if (target instanceof FeatureMapLayer) {
            layer = (FeatureMapLayer) target;
            parse();
        }
    }

    @Override
    public void apply() {  
        if(layer !=null){
            layer.setQuery(QueryBuilder.filtered(layer.getCollection().getFeatureType().getName(), getFilter()));
        }
    }

    @Override
    public void reset() {
        parse();
    }

}
