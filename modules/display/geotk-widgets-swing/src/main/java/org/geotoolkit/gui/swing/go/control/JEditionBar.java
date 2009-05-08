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
package org.geotoolkit.gui.swing.go.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.go.control.selection.DefaultSelectionHandler;
import org.geotoolkit.gui.swing.misc.Render.LayerListRenderer;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JEditionBar extends JToolBar implements MapControlBar{

    private static final ImageIcon ICON_EDIT = IconBundle.getInstance().getIcon("16_edit_geom");

    private final JButton guiEdit = new JButton(ICON_EDIT);
    private final JComboBox guiLayers = new JComboBox();

//    private final LasoSelectionDecoration deco = new LasoSelectionDecoration();
//    private final LasoSelectionHandler handler = new LasoSelectionHandler();

    private boolean installed = false;

    private final ActionListener listener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(map == null) return;

//            map.setHandler(new LasoSelectionHandler(map));
//
//            if(e.getSource() == guiSelect){
//                if(installed){
//                    map.setHandler(new LasoSelectionHandler(map));
////                    map.removeDecoration( deco);
//                    installed = false;
//                }else{
////                    map.addDecoration(10, deco);
//                    installed = true;
//                }
//            }else{
//
//            }
        }
    };

        
    private GoMap2D map = null;

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JEditionBar() {
        this(null);

    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JEditionBar(GoMap2D map) {
        setMap(map);

        guiLayers.setRenderer(new LayerListRenderer());

        guiEdit.addActionListener(listener);
        guiLayers.addActionListener(listener);

        add(guiEdit);
        add(guiLayers);

    }

    
    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    @Override
    public void setMap(GoMap2D map2d) {
        map = map2d;

        guiEdit.setEnabled(false);
        guiLayers.setEnabled(false);

        if(map != null){
            AbstractContainer2D container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                guiEdit.setEnabled(true);
                guiLayers.setEnabled(true);
                ContextContainer2D cc = (ContextContainer2D) container;
                guiLayers.setModel(new ListComboBoxModel(cc.getContext().layers()));
            }else{
                guiLayers.setModel(new DefaultComboBoxModel());
            }
        }else{
            guiLayers.setModel(new DefaultComboBoxModel());
        }

    }

    @Override
    public GoMap2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
