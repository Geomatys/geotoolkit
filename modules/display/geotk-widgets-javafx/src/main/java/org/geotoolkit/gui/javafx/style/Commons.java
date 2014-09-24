/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.style;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class Commons {
    
    public static final Image ICON_STYLE     = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_BOOK,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_FTS       = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TAG,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_RULE      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILTER,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_NEW       = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PLUS,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_DUPLICATE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILES_O,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_DELETE    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TRASH_O,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    
}
