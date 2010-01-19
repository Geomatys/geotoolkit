/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

import javax.swing.JComponent;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class JNavigatorBand<T extends Comparable> extends JComponent {

    private NavigatorModel model = null;

    public NavigatorModel getModel() {
        return model;
    }

    public void setModel(NavigatorModel model) {
        this.model = model;
    }


}
