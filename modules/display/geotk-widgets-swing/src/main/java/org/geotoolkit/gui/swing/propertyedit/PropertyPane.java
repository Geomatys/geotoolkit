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
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * property panel interface
 *
 * @author Johann Sorel
 * @module
 */
public interface PropertyPane {

    /**
     * PropertyChange event name used to ask a reset call.
     */
    public static final String RELOAD = "reload";

    public boolean canHandle(Object target);

    public void setTarget(Object target);

    public void apply();

    public void reset();

    public String getTitle();

    public ImageIcon getIcon();

    public Image getPreview();

    public String getToolTip();

    public Component getComponent();

}
