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
package org.geotoolkit.gui.swing.go.control.creation;

import java.awt.event.MouseEvent;



import com.vividsolutions.jts.geom.Geometry;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Default edition handler
 * 
 * @author Johann Sorel
 */
public class ModificationDelegate extends AbstractMouseDelegate {

    private static final Icon ICON_DELETE = IconBundle.getInstance().getIcon("16_delete");


    private final JButton guiEnd = new JButton(new AbstractAction("",ICON_DELETE) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(hasEditionGeometry){
                removeGeometryEdit();
                geoms.clear();
                handler.clearMemoryLayer();
                handler.setMemoryLayerGeometry(geoms);
            }
        }
    });

    public ModificationDelegate(DefaultEditionDecoration handler) {
        super(handler);
    }

    public void prepare(JPanel panDetail){
        panDetail.setLayout(new GridLayout(3, 3));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(new JButton(" "));
        panDetail.add(guiEnd);
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
