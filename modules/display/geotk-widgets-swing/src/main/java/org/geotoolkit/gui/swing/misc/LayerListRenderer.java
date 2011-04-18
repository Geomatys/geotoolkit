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
package org.geotoolkit.gui.swing.misc;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.MapLayer;

/**
 * layer list renderer
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class LayerListRenderer extends DefaultListCellRenderer {

    private static final ImageIcon ICON_LAYER_VISIBLE = IconBundle.getIcon("16_maplayer_visible");    
    
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if(value instanceof MapLayer){
            MapLayer layer = (MapLayer) value;
            lbl.setText(layer.getDescription().getTitle().toString());
            lbl.setIcon(ICON_LAYER_VISIBLE);
        }
                        
        return lbl;
    }

}
