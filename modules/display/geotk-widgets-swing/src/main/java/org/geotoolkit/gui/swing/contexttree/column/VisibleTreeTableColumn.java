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
package org.geotoolkit.gui.swing.contexttree.column;


import org.geotoolkit.gui.swing.contexttree.renderer.DefaultCellEditor;
import org.geotoolkit.gui.swing.contexttree.renderer.DefaultCellRenderer;
import org.geotoolkit.gui.swing.contexttree.renderer.HeaderInfo;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * Default visibility column
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class VisibleTreeTableColumn extends TreeTableColumn {
    
    
    /**
     * column with checkbox for jcontexttree
     */
    public VisibleTreeTableColumn() {
       
        setCellEditor( new DefaultCellEditor(new VisibleComponent()));
        setCellRenderer( new DefaultCellRenderer(new VisibleComponent()));
                
        String name = MessageBundle.getString("contexttreetable_visible");
        setHeaderValue( new HeaderInfo(name,null,IconBundle.getInstance().getIcon("16_visible") ));
        
        setEditable(true);
        setResizable(false);
        setMaxWidth(25);
        setMinWidth(25);
        setPreferredWidth(25);
        setWidth(25);
        
        setEditableOnMouseOver(true);
    }
         
    
   
    @Override
    public void setValue(Object target, Object value) {
    }
    
    
    @Override
    public Object getValue(Object target) {
        
        if(target instanceof MapLayer)
            return (MapLayer)target;
        else
            return "n/a";
    }
    
    
        
    @Override
    public boolean isCellEditable(Object target){
        
         if(target instanceof MapLayer)
            return isEditable();
        else
            return false;
    }
    
    
    @Override
    public Class getColumnClass() {
        return MapLayer.class;
    }

    
}
