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
package org.geotoolkit.gui.swing.go2.control.creation;

import java.awt.event.MouseEvent;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Default edition handler
 * 
 * @author Johann Sorel
 */
public class ModificationDelegate extends AbstractMouseDelegate {

    private static final Icon ICON_DELETE = IconBundle.getInstance().getIcon("16_delete");
    private static final Icon ICON_MOVE = IconBundle.getInstance().getIcon("16_move_node");
    private static final Icon ICON_ADD = IconBundle.getInstance().getIcon("16_add_node");
    private static final Icon ICON_REMOVE = IconBundle.getInstance().getIcon("16_remove_node");
    
    private final Action moveAction = new AbstractAction("",ICON_MOVE) {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(hasEditionGeometry){
            }
        }
    };

    private final Action addAction = new AbstractAction("",ICON_ADD) {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(hasEditionGeometry){
            }
        }
    };

    private final Action removeAction = new AbstractAction("",ICON_REMOVE) {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(hasEditionGeometry){
            }
        }
    };

    private final Action deleteAction = new AbstractAction("",ICON_DELETE) {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(hasEditionGeometry){
                removeGeometryEdit();
                geoms.clear();
                handler.clearMemoryLayer();
                handler.setMemoryLayerGeometry(geoms);
            }
        }
    };


    public ModificationDelegate(DefaultEditionDecoration handler) {
        super(handler);
    }

    public void prepare(JPanel panDetail){

        GridBagLayout gbl = new GridBagLayout();
        panDetail.setLayout(gbl);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;

        AbstractButton but;

        gc.gridy = 0;
        final JPanel general = new JPanel(new GridLayout(3, 3));
        general.setOpaque(false);
        final ButtonGroup group = new ButtonGroup();

        but = new JToggleButton(moveAction);
        but.setSelected(true);
        group.add(but);
        general.add(but);

        but = new JToggleButton(addAction);
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(removeAction);
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        but = new JToggleButton(" ");
        but.setEnabled(false);
        group.add(but);
        general.add(but);

        panDetail.add(general, gc);

        gc.gridy = 1;
        panDetail.add(new JSeparator(SwingConstants.HORIZONTAL), gc);


        gc.gridy = 2;
        JPanel intelli = new JPanel(new GridLayout(1, 3));
        intelli.setOpaque(false);

        but = new JButton(" ");
        but.setEnabled(false);
        intelli.add(but);

        but = new JButton(" ");
        but.setEnabled(false);
        intelli.add(but);

        but = new JButton(" ");
        but.setEnabled(false);
        intelli.add(but);

        panDetail.add(intelli, gc);

        gc.gridy = 3;
        panDetail.add(new JSeparator(SwingConstants.HORIZONTAL), gc);

        gc.gridy = 4;
        JPanel other = new JPanel(new GridLayout(1, 3));
        other.setOpaque(false);

        but = new JButton(" ");
        but.setEnabled(false);
        other.add(but);

        but = new JButton(" ");
        but.setEnabled(false);
        other.add(but);

        but = new JButton(deleteAction);
        other.add(but);
        
        panDetail.add(other, gc);

        panDetail.revalidate();
        panDetail.setVisible(true);
    }

    @Override
    public void fireStateChange() {
        coords.clear();
        geoms.clear();
        nbRightClick = 0;
        inCreation = false;
        hasEditionGeometry = false;
        hasGeometryChanged = false;
        editedFeatureID = null;
        editedNodes.clear();
        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            if (!hasEditionGeometry) {
                grabGeometry(e.getX(), e.getY());
            }
        } else if (button == MouseEvent.BUTTON3) {
            validateGeometryEdit();
            geoms.clear();
            coords.clear();
        }
        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            if (hasEditionGeometry) {
                grabGeometryNode(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (hasEditionGeometry && !editedNodes.isEmpty()) {
            hasGeometryChanged = true;
            dragGeometryNode(e.getX(), e.getY());
        }
        editedNodes.clear();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (hasEditionGeometry && !editedNodes.isEmpty()) {
            hasGeometryChanged = true;
            dragGeometryNode(e.getX(), e.getY());
        }
    }
}
