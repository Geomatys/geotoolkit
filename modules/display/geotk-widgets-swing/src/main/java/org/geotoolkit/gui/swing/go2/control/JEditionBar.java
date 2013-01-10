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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.geotoolkit.gui.swing.go2.CanvasHandler;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.edition.EditionDelegate;
import org.geotoolkit.gui.swing.go2.control.edition.EditionHandler;
import org.geotoolkit.gui.swing.go2.control.edition.EditionTool;
import org.geotoolkit.gui.swing.go2.control.edition.JEditionToolComboBox;
import org.geotoolkit.gui.swing.go2.control.edition.JLayerComboBox;
import org.geotoolkit.gui.swing.go2.control.edition.SessionCommitAction;
import org.geotoolkit.gui.swing.go2.control.edition.SessionRollbackAction;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.openide.awt.DropDownButtonFactory;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JEditionBar extends AbstractMapControlBar implements ActionListener,PropertyChangeListener,ListSelectionListener{

    private final SessionCommitAction commitAction = new SessionCommitAction();
    private final SessionRollbackAction rollbackAction = new SessionRollbackAction();

    private final JButton guiEdit;
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

        final JPopupMenu menu = new JPopupMenu();

        guiTools.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guiLayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JPanel pane = new JPanel(new GridBagLayout());
        final JLabel lbl1 = new JLabel(MessageBundle.getString("layers"));
        final JLabel lbl2 = new JLabel(MessageBundle.getString("editTool"));
        final JMenuItem active = new JMenuItem(MessageBundle.getString("ok"));
        final JScrollPane pane1 = new JScrollPane(guiLayers);
        final JScrollPane pane2 = new JScrollPane(guiTools);
        pane1.setPreferredSize(new Dimension(280, 140));
        pane2.setPreferredSize(new Dimension(280, 140));
        pane1.setMaximumSize(new Dimension(280, 140));
        pane2.setMaximumSize(new Dimension(280, 140));

        int y=1;
        final GridBagConstraints cst = new GridBagConstraints();
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.gridx = 1;
        cst.gridy = y++;
        cst.weighty = 0;
        pane.add(lbl1,cst);
        cst.gridy = y++;
        cst.weighty = 1;
        pane.add(pane1,cst);
        cst.gridy = y++;
        cst.weighty = 0;
        pane.add(lbl2,cst);
        cst.gridy = y++;
        cst.weighty = 1;
        pane.add(pane2,cst);
        cst.gridy = y++;
        cst.gridx = 1;
        cst.weighty = 1;
        cst.weightx = 0;

        menu.add(pane);
        menu.add(active);

        guiEdit = DropDownButtonFactory.createDropDownButton(IconBundle.getIcon("16_edit_geom"), menu);
        guiEdit.setToolTipText(MessageBundle.getString("map_edit"));
        guiEdit.addActionListener(this);
        active.addActionListener(this);
        add(guiEdit);
        add(commitAction);
        add(rollbackAction);

        guiTools.addListSelectionListener(this);
        guiLayers.addListSelectionListener(this);
        guiLayers.addPropertyChangeListener("model", this);
        setMap(map);
    }

    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);

        guiLayers.setMap(map2d);
        guiEdit.setEnabled(map != null);
        guiTools.setEnabled(map != null);
        guiLayers.setEnabled(map != null);

        final Object candidate = guiLayers.getSelectedValue();
        guiTools.setEdited(candidate);
        if(candidate instanceof FeatureMapLayer){
            commitAction.setLayer((FeatureMapLayer) candidate);
            rollbackAction.setLayer((FeatureMapLayer) candidate);
        }else{
            commitAction.setLayer(null);
            rollbackAction.setLayer(null);
        }
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

        final Object candidate = guiLayers.getSelectedValue();
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
    public void propertyChange(PropertyChangeEvent e) {

        if(e.getSource() == guiLayers){
            final Object candidate = guiLayers.getSelectedValue();
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
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getSource() == guiLayers){
            final Object candidate = guiLayers.getSelectedValue();
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
