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
import javax.swing.AbstractAction;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @author Alexis Manin (Geomatys)
 * @module pending
 */
public class ClearSelectionAction extends AbstractAction {

    public ClearSelectionAction() {
        super(MessageBundle.format("clear_selection"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object target = e.getSource();
        if (target instanceof LayerFeaturePropertyPanel) {
            target = ((LayerFeaturePropertyPanel)target).getTarget();
        } else if (target instanceof JFeaturePanelAction) {
            target = ((JFeaturePanelAction)target).getFeaturePanel().getTarget();
        } else {
            target = getValue(LayerFeaturePropertyPanel.ACTION_REF);
            if (target != null && target instanceof LayerFeaturePropertyPanel) {
                target = ((LayerFeaturePropertyPanel)target).getTarget();
            }
        }       
        
        if (target instanceof FeatureMapLayer) {
            ((FeatureMapLayer)target).setSelectionFilter(null);
        }
    }

}
