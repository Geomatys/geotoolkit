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
package org.geotoolkit.gui.swing.go2.control.navigation;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class RefreshAction extends AbstractAction {

    private static final ImageIcon ICON_REFRESH_16 = IconBundle.getIcon("16_data_reload");
    private static final ImageIcon ICON_REFRESH_24 = IconBundle.getIcon("24_data_reload");

    private JMap2D map = null;

    public RefreshAction() {
        this(false);
    }

    public RefreshAction(boolean big) {
        super("",(big)?ICON_REFRESH_24 : ICON_REFRESH_16);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_refresh"));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {
            map.getCanvas().getController().repaint();
        }
    }

    public JMap2D getMap() {
        return map;
    }

    public void setMap(JMap2D map) {
        this.map = map;
        setEnabled(map != null);
    }
}
