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

package org.geotoolkit.gui.swing.propertyedit;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractPropertyPane extends JPanel implements PropertyPane {

    private final String title;
    private final ImageIcon icon;
    private final Image preview;
    private final String tooltip;

    public AbstractPropertyPane(String title, ImageIcon icon, Image preview, String tooltip) {
        this.title = title;
        this.icon = icon;
        this.preview = preview;
        this.tooltip = tooltip;
    }

    @Override
    public final String getTitle() {
        return title;
    }

    @Override
    public final ImageIcon getIcon() {
        return icon;
    }

    @Override
    public final Image getPreview() {
        return preview;
    }

    @Override
    public final String getToolTip() {
        return tooltip;
    }

    @Override
    public final Component getComponent() {
        return this;
    }

}
