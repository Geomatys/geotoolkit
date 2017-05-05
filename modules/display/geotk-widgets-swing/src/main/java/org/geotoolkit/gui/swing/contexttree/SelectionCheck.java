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
package org.geotoolkit.gui.swing.contexttree;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;


/**
 * Component used to present layer selectability
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
class SelectionCheck extends JCheckBox {

    private static final ImageIcon ICO_SELECT = IconBuilder.createIcon(FontAwesomeIcons.ICON_INFO, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_NOSELECT = IconBuilder.createIcon(FontAwesomeIcons.ICON_INFO, 16, Color.LIGHT_GRAY);

    public SelectionCheck() {
        setOpaque(false);
        setBorderPainted(false);
        setPressedIcon(         ICO_NOSELECT);
        setRolloverIcon(        ICO_NOSELECT);
        setRolloverSelectedIcon(ICO_SELECT);
        setIcon(                ICO_NOSELECT);
        setSelectedIcon(        ICO_SELECT);
        setDisabledIcon(        ICO_NOSELECT);
        setDisabledSelectedIcon(ICO_SELECT);
    }

}
