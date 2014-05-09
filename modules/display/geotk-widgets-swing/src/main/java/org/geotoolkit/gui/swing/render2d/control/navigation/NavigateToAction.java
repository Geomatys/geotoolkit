/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011, Geomatys
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
import org.geotoolkit.gui.swing.render2d.control.navigation.JNavigateToPanel.NavigateToMapDecoration;
import org.geotoolkit.gui.swing.render2d.decoration.MapDecoration;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * Shows a dialog to fill in a coordinate and navigate to it.
 *
 * @author Johann Sorel
 * @module pending
 */
public class NavigateToAction extends AbstractMapAction {

    private static final ImageIcon ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_CROSSHAIRS, 16, FontAwesomeIcons.DEFAULT_COLOR);

    public NavigateToAction() {
        putValue(SMALL_ICON, ICON);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_nav_to"));
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null) {

            //search if a navigate to decoration is already present
            MapDecoration navtoDeco = null;
            for(MapDecoration deco : map.getDecorations()){
                if(deco instanceof JNavigateToPanel.NavigateToMapDecoration){
                    navtoDeco = deco;
                    break;
                }
            }

            if(navtoDeco != null){
                //remove the deco
                map.removeDecoration(navtoDeco);
            }else{
                //create and add it
                navtoDeco = new NavigateToMapDecoration();
                map.addDecoration(navtoDeco);
            }

        }
    }

}
