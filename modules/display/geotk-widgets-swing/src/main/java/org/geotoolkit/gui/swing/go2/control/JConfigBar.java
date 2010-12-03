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

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * JMap2DControlBar is a JPanel to handle Navigation decoration and debugging panel
 *
 * @author johann sorel
 * @module pending
 */
public class JConfigBar extends JToolBar {

    private static final ImageIcon ICON_CONFIG = IconBundle.getInstance().getIcon("16_map2d_optimize");
    private static final ImageIcon ICON_DEBUG = IconBundle.getInstance().getIcon("16_deco_debug");

    private final ConfigAction ACTION_CONFIG = new ConfigAction();
    private final DebugAction ACTION_DEBUG = new DebugAction();

    private JMap2D map = null;
    private final JButton gui_config = buildButton(ICON_CONFIG, ACTION_CONFIG, MessageBundle.getString("map_config"));
    private final JButton gui_debug = buildButton(ICON_DEBUG, ACTION_DEBUG, MessageBundle.getString("map_debug"));
    private final int largeur = 2;

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JConfigBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JConfigBar(JMap2D pane) {
        setMap(pane);
        init();
    }

    private void init() {
        add(gui_config);
        add(gui_debug);
    }


    private JButton buildButton(ImageIcon img,Action action, String tooltip) {
        JButton but = new JButton(action);
        but.setIcon(img);
        but.setBorder(new EmptyBorder(largeur, largeur, largeur, largeur));
        but.setBorderPainted(false);
        but.setContentAreaFilled(false);
        but.setPreferredSize(new Dimension(25, 25));
        but.setOpaque(false);
        but.setToolTipText(tooltip);
        return but;
    }


    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(JMap2D map2d) {
        map = map2d;
        ACTION_CONFIG.setMap(map);
        ACTION_DEBUG.setMap(map);
    }
}
