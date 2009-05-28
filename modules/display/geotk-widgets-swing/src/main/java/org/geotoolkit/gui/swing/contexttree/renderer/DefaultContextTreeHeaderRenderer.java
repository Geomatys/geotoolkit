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
package org.geotoolkit.gui.swing.contexttree.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;

/**
 * headerRenderer for treetable
 *
 * @author Johann Sorel
 */
public class DefaultContextTreeHeaderRenderer extends ColumnHeaderRenderer{

    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        
        if(value instanceof HeaderInfo){
            HeaderInfo evo = (HeaderInfo)value;
            
            if(evo.getIcon() == null){
                setIcon(IconBundle.EMPTY_ICON);
            }else{
                setIcon(evo.getIcon());
            }
            setName(evo.getHeaderText());
            setHorizontalAlignment(SwingConstants.CENTER); 
            Component parent = super.getTableCellRendererComponent(table,null,isSelected,hasFocus,row,column);
            
            return parent;
        }else{
            setIcon(IconBundle.EMPTY_ICON);
            setName(null);
            setToolTipText(null);
            return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        }
              
    }
}
