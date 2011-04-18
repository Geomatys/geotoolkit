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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import org.geotoolkit.gui.swing.go2.CanvasHandler;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.edition.JLayerComboBox;
import org.geotoolkit.gui.swing.go2.control.edition.EditionDelegate;
import org.geotoolkit.gui.swing.go2.control.edition.EditionHandler;
import org.geotoolkit.gui.swing.go2.control.edition.EditionTool;
import org.geotoolkit.gui.swing.go2.control.edition.JEditionToolComboBox;
import org.geotoolkit.gui.swing.go2.control.edition.SessionCommitAction;
import org.geotoolkit.gui.swing.go2.control.edition.SessionRollbackAction;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JEditionBar extends AbstractMapControlBar implements ActionListener,ItemListener{

    private final SessionCommitAction commitAction = new SessionCommitAction();
    private final SessionRollbackAction rollbackAction = new SessionRollbackAction();

    private final JButton guiEdit = new JButton(IconBundle.getIcon("16_edit_geom"));
    private final JEditionToolComboBox guiTools = new JEditionToolComboBox();
    private final JLayerComboBox guiLayers = new JLayerComboBox();

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JEditionBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JEditionBar(final JMap2D map) {
        setMap(map);
        guiEdit.setToolTipText(MessageBundle.getString("map_edit"));
        guiEdit.addActionListener(this);
        add(guiEdit);
        add(guiTools);
        add(guiLayers);
        add(commitAction);
        add(rollbackAction);

        guiTools.addItemListener(this);
        guiLayers.addItemListener(this);
    }
    
    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);

        guiLayers.setMap(map2d);
        guiEdit.setEnabled(map != null);
        guiTools.setEnabled(map != null);
        guiLayers.setEnabled(map != null);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        updateHandler(true);
    }

    /**
     *
     * @param set : if true will replace whatever handler is present
     * otherwise will replace it only if it's an edition handler.
     */
    private void updateHandler(boolean set){
        if(map == null) return;

        final Object candidate = guiLayers.getSelectedItem();
        if(candidate == null) return;

        final EditionTool tool = guiTools.getSelectedItem();
        if(tool == null) return;

        final EditionDelegate delegate = tool.createDelegate(map,candidate);
        if(delegate == null) return;

        final CanvasHandler before = map.getHandler();
        if(set || before instanceof EditionHandler){
            final EditionHandler handler = new EditionHandler(map,delegate);
            map.setHandler(handler);
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if(e.getSource() == guiLayers){
            final Object candidate = guiLayers.getSelectedItem();
            guiTools.setEdited(candidate);
            
            if(candidate instanceof FeatureMapLayer){
                commitAction.setLayer((FeatureMapLayer) candidate);
                rollbackAction.setLayer((FeatureMapLayer) candidate);
            }else{
                commitAction.setLayer(null);
                rollbackAction.setLayer(null);
            }

            //tool changed
            updateHandler(false);

        }else if(e.getSource() == guiTools){
            //tool changed
            updateHandler(false);
        }
    }

}
