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

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

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
    private final Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY,1);
    private final Border nullborder = BorderFactory.createEmptyBorder(1,1,1,1);
    
    
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if(value instanceof MapLayer){
            MapLayer layer = (MapLayer) value;
            lbl.setText(layer.getDescription().getTitle().toString());
            lbl.setIcon(getIcon(layer));
        }
                        
        return lbl;
    }
    
    private ImageIcon getIcon(final MapLayer layer){
//        DataStore ds = (DataStore) layer.getFeatureSource().getDataStore();
//
//        if (layer.getFeatureSource().getSchema().getName().getLocalPart().equals("GridCoverage")) {
//            return ICON_LAYER_FILE_RASTER_VISIBLE ;
//        } else if (AbstractFileDataStore.class.isAssignableFrom(ds.getClass())) {
//            return ICON_LAYER_FILE_VECTOR_VISIBLE ;
//        } else if (JDBC1DataStore.class.isAssignableFrom(ds.getClass())) {
//            return ICON_LAYER_DB_VISIBLE;
//        } else {
            return ICON_LAYER_VISIBLE;
//        }
    }

}
