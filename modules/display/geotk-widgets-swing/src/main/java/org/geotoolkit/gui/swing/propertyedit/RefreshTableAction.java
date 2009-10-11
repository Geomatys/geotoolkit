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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class RefreshTableAction extends JFeaturePanelAction{

    public RefreshTableAction(){
        setText(MessageBundle.getString("refresh_table"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final LayerFeaturePropertyPanel panel = getFeaturePanel();
                if(panel == null) return;
                panel.reset();
            }
        });
    }

}
