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
import javax.swing.JOptionPane;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.filter.Filter;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @author Alexis Manin (Geomatys)
 * @module pending
 */
public class DeleteSelectionAction extends AbstractAction {

    public DeleteSelectionAction(){
        super(MessageBundle.format("delete"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final FeatureMapLayer layer;
        final LayerFeaturePropertyPanel panel;
        Object source = e.getSource();
        if (source instanceof LayerFeaturePropertyPanel) {
            panel = (LayerFeaturePropertyPanel) source;
        } else if (source instanceof JFeaturePanelAction) {
            panel = ((JFeaturePanelAction) source).getFeaturePanel();
        } else {
            source = getValue(LayerFeaturePropertyPanel.ACTION_REF);
            if (source != null && source instanceof LayerFeaturePropertyPanel) {
                panel = (LayerFeaturePropertyPanel) source;
            } else {
                return;
            }
        }

        layer = panel.getTarget();
        final FeatureCollection collection = layer.getCollection();
        if (collection.isWritable()) {
            final Filter fid = layer.getSelectionFilter();
            if (fid != null) {
                final int confirm = JOptionPane.showConfirmDialog(null, MessageBundle.format("confirm_delete"),
                        MessageBundle.format("confirm_delete"), JOptionPane.OK_CANCEL_OPTION);
                if (JOptionPane.OK_OPTION == confirm) {

                    try {
                        collection.remove(fid);
                        if (collection.getSession() != null) {
                            collection.getSession().commit();
                        }
                    } catch (DataStoreException ex) {
                        ex.printStackTrace();
                    }
                    panel.reset();
                }
            }
        }
    }

}
