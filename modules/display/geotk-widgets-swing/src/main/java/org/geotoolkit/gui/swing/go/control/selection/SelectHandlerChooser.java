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

import org.geotoolkit.gui.swing.map.map2d.Map2D;
/**
 * Handler chooser
 * 
 * @author Johann Sorel
 */
public class SelectHandlerChooser extends JComboBox {

    private Map2D map = null;
    private ItemListener listListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {

//            if (map != null && map instanceof SelectableMap2D) {
//                SelectableMap2D editmap = (SelectableMap2D) map;
//                editmap.setSelectionHandler((SelectionHandler) getSelectedItem());
//            }
        }
    };
//    private SelectionListener selectionListener = new SelectionListener() {
//
//        public void selectionChanged(SelectionEvent event) {
//        }
//
//        public void selectionFilterChanged(SelectionEvent event) {
//        }
//
//        public void selectionHandlerChanged(SelectionEvent event) {
//            removeItemListener(listListener);
//            if (event.getHandler() != getSelectedItem()) {
//                if (event.getHandler() instanceof DefaultSelectionHandler) {
//                    setSelectedItem(defaultHandler);
//                } else if (event.getHandler() instanceof LasoSelectionHandler) {
//                    setSelectedItem(lasoHandler);
//                } else {
//
//                }
//            }
//            addItemListener(listListener);
//        }
//    };
//    private final DefaultSelectionHandler defaultHandler = new DefaultSelectionHandler();
//    private final LasoSelectionHandler lasoHandler = new LasoSelectionHandler();

    public SelectHandlerChooser() {
        setRenderer(new listRenderer());

//        addItem(defaultHandler);
//        addItem(lasoHandler);

        addItemListener(listListener);

        initComboBox();
        
        setOpaque(false);
        setBorder(null);
    }

    private void initComboBox() {

//        removeItemListener(listListener);
//
//        if (map != null && map instanceof SelectableMap2D) {
//            setEnabled(true);
//            SelectableMap2D select = (SelectableMap2D) map;
//
//            if (select.getSelectionHandler() instanceof DefaultSelectionHandler) {
//                setSelectedItem(defaultHandler);
//            } else if (select.getSelectionHandler() instanceof LasoSelectionHandler) {
//                setSelectedItem(lasoHandler);
//            } else {
//
//            }
//
//        } else {
//            setEnabled(false);
//        }
//
//        addItemListener(listListener);
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
        private final Border nullborder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//            if (value instanceof SelectionHandler) {
//                lbl.setIcon(((SelectionHandler) value).getIcon());
//                lbl.setText(((SelectionHandler) value).getTitle());
//            } else {
//                lbl.setIcon(null);
//                lbl.setText(value.toString());
//            }
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
