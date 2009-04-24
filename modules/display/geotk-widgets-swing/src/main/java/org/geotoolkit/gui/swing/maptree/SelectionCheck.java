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
package org.geotoolkit.gui.swing.maptree;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import org.geotoolkit.gui.swing.resource.IconBundle;


/**
 * Component used to present layer selectability
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
final class SelectionCheck extends JCheckBox {

    private static final ImageIcon ICO_SELECT = IconBundle.getInstance().getIcon("16_select");
    private static final ImageIcon ICO_NOSELECT = IconBundle.getInstance().getIcon("16_noselect");

    @Override
    public void paintComponent(Graphics g) {
        int x = (getWidth() - 16) / 2;
        int y = (getHeight() - 16) / 2;
        g.drawImage((isSelected()) ? ICO_SELECT.getImage() : ICO_NOSELECT.getImage(), x, y, this);
    }
}
