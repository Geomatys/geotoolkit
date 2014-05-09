/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2009 - 2014, Geomatys
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
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;


/**
 * Component used to present layer visibility
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
class VisibleCheck extends JCheckBox {

    private static final ImageIcon ICO_VISIBLE = IconBuilder.createIcon(FontAwesomeIcons.ICON_EYE, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_NOVISIBLE = IconBuilder.createIcon(FontAwesomeIcons.ICON_EYE_SLASH, 16, Color.LIGHT_GRAY);

    public VisibleCheck() {
        setOpaque(false);
        setBorderPainted(false);
        setPressedIcon(         ICO_NOVISIBLE);
        setRolloverIcon(        ICO_NOVISIBLE);
        setRolloverSelectedIcon(ICO_VISIBLE);
        setIcon(                ICO_NOVISIBLE);
        setSelectedIcon(        ICO_VISIBLE);
        setDisabledIcon(        ICO_NOVISIBLE);
        setDisabledSelectedIcon(ICO_VISIBLE);
    }

}
