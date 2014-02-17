/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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

import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JFeaturePanelAction extends JMenuItem{

    private LayerFeaturePropertyPanel panel = null;

    public JFeaturePanelAction(){}

    public JFeaturePanelAction(final Action action){
        super(action);
    }

    public LayerFeaturePropertyPanel getFeaturePanel(){
        return panel;
    }

    public void setFeaturePanel(final LayerFeaturePropertyPanel panel){
        this.panel = panel;
    }

}
