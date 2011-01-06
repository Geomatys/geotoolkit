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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionHandler;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JEditionBar extends JToolBar implements MapControlBar{

    private static final ImageIcon ICON_EDIT = IconBundle.getIcon("16_edit_geom");

    private final JButton guiEdit = new JButton(ICON_EDIT);

    private JMap2D map = null;

    private final DefaultEditionHandler handler = new DefaultEditionHandler(map);

    private final ActionListener listener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(map == null) return;

            handler.setMap(map);
            map.setHandler(handler);
            
        }
    };


    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JEditionBar() {
        this(null);

        guiEdit.setToolTipText(MessageBundle.getString("map_edit"));

    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JEditionBar(JMap2D map) {
        setMap(map);

        guiEdit.addActionListener(listener);
        add(guiEdit);
    }

    
    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    @Override
    public void setMap(JMap2D map2d) {
        map = map2d;

        guiEdit.setEnabled(false);

        if(map != null){
            AbstractContainer2D container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                guiEdit.setEnabled(true);
            }else{
            }
        }

        handler.setMap(map);

    }

    @Override
    public JMap2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
