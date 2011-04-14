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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

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
public class JEditionBar extends AbstractMapControlBar implements ActionListener{

    private final JButton guiEdit = new JButton(IconBundle.getIcon("16_edit_geom"));
    private final DefaultEditionHandler handler = new DefaultEditionHandler(map);

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
    public JEditionBar(final JMap2D map) {
        setMap(map);
        guiEdit.setToolTipText(MessageBundle.getString("map_edit"));
        guiEdit.addActionListener(this);
        add(guiEdit);
    }
    
    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);

        guiEdit.setEnabled(false);

        if(map != null){
            final AbstractContainer2D container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                guiEdit.setEnabled(true);
            }
        }

        handler.setMap(map);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(map == null) return;
        handler.setMap(map);
        map.setHandler(handler);
    }

}
