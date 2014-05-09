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
package org.geotoolkit.gui.swing.render2d.control.navigation;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * @author johann sorel (Puzzle-GIS)
 * @module pending
 */
public class PanAction extends AbstractMapAction {

    public static final ImageIcon ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_ARROWS, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final boolean infoOnClick;
    
    public PanAction() {
        this(false);
    }
    
    public PanAction(boolean infoOnClick) {
        this.infoOnClick = infoOnClick;
        putValue(SMALL_ICON, ICON);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_pan"));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null ) {
            map.setHandler(new PanHandler(map,infoOnClick));
        }
    }

}
