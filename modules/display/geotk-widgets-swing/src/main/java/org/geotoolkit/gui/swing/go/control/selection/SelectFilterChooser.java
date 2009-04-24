/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control.selection;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;

import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.map.map2d.Map2D;

/**
 * Filter chooser
 * 
 * @author Johann Sorel
 */
public class SelectFilterChooser extends JComboBox {

    private GoMap2D map = null;
    private ItemListener listListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {

//            if (map != null) {
//                SelectableMap2D editmap = (SelectableMap2D) map;
//                editmap.setSelectionFilter((SelectableMap2D.SELECTION_FILTER)getSelectedItem());
//            }
        }
    };
//    private SelectionListener selectionListener = new SelectionListener() {
//
//        public void selectionChanged(SelectionEvent event) {
//        }
//
//        public void selectionFilterChanged(SelectionEvent event) {
//
//            removeItemListener(listListener);
//            if(event.getFilter() != getSelectedItem()){
//                setSelectedItem(event.getFilter());
//                }
//            addItemListener(listListener);
//        }
//
//        public void selectionHandlerChanged(SelectionEvent event) {
//        }
//    };
    
    public SelectFilterChooser() {
        setRenderer(new listRenderer());

//        addItem(SelectableMap2D.SELECTION_FILTER.CONTAINS);
//        addItem(SelectableMap2D.SELECTION_FILTER.CROSSES);
//        addItem(SelectableMap2D.SELECTION_FILTER.DISJOINT);
//        addItem(SelectableMap2D.SELECTION_FILTER.INTERSECTS);
//        addItem(SelectableMap2D.SELECTION_FILTER.OVERLAPS);
//        addItem(SelectableMap2D.SELECTION_FILTER.TOUCHES);
//        addItem(SelectableMap2D.SELECTION_FILTER.WITHIN);

        addItemListener(listListener);

        initComboBox();
        
        setOpaque(false);
        setBorder(null);
    }

    private void initComboBox() {

        removeItemListener(listListener);

//        if (map != null && map instanceof SelectableMap2D) {
//            setEnabled(true);
//            SelectableMap2D select = (SelectableMap2D) map;
//            setSelectedItem(select.getSelectionFilter());
//        } else {
//            setEnabled(false);
//        }

        addItemListener(listListener);
    }

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map2d) {
                
//        if (map != null) {
//            if (map instanceof SelectableMap2D) {
//                ((SelectableMap2D) map).removeSelectableMap2DListener(selectionListener);
//            }
//        }
//
//        map = map2d;
//
//        if (map != null) {
//            if (map instanceof SelectableMap2D) {
//                ((SelectableMap2D) map).addSelectableMap2DListener(selectionListener);
//            }
//        }
//
//        initComboBox();
    }

    //----------------private classes-------------------------------------------
    private class listRenderer extends DefaultListCellRenderer {

        private JLabel lbl = new JLabel();
        private final Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
        private final Border nullborder = BorderFactory.createEmptyBorder(1,1,1,1);
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
//            if (value instanceof SelectableMap2D.SELECTION_FILTER) {
//                lbl.setText(((SelectableMap2D.SELECTION_FILTER) value).getTitle());
//                lbl.setIcon(((SelectableMap2D.SELECTION_FILTER) value).getIcon());
//            }else {
//                lbl.setIcon(null);
//                lbl.setText(value.toString());
//            }
//
//
//            if (isSelected) {
//                lbl.setBorder(border);
//            } else {
//                lbl.setBorder(nullborder);
//            }

            return lbl;
        }
    }
}
