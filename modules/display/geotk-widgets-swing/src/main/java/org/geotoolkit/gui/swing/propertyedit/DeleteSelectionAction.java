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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DeleteSelectionAction extends JFeaturePanelAction{

    public DeleteSelectionAction(){
        setText(MessageBundle.getString("delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final LayerFeaturePropertyPanel panel = getFeaturePanel();
                if(panel == null) return;
                final FeatureMapLayer layer = panel.getTarget();
                if(layer == null) return;

                if(layer.getFeatureSource() instanceof FeatureStore){
                    final FeatureStore<SimpleFeatureType,SimpleFeature> store =
                            (FeatureStore<SimpleFeatureType, SimpleFeature>) layer.getFeatureSource();
                    Filter fid = layer.getSelectionFilter();
                    if(fid != null){

                        int confirm = JOptionPane.showConfirmDialog(null, MessageBundle.getString("confirm_delete"),
                                MessageBundle.getString("confirm_delete"), JOptionPane.OK_CANCEL_OPTION);
                        if (JOptionPane.OK_OPTION == confirm) {

                            try {
                                store.removeFeatures(layer.getSelectionFilter());
                            } catch (IOException ex) {
                                Logger.getLogger(DeleteSelectionAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            panel.reset();
                        }
                    }
                }
            }
        });
    }

}
