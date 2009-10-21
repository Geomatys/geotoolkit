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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.opengis.feature.simple.SimpleFeature;

/**
 * point creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class PointDelegate extends AbstractEditionDelegate{

    private enum ACTION{
        ADD,
        MOVE,
        DELETE
    }

    private ACTION currentAction = ACTION.MOVE;


    public PointDelegate(DefaultEditionHandler handler) {
        super(handler);
    }

    @Override
    public void initialize() {
        //configure tool panel
        final JPanel pan = new JPanel(new GridLayout(3,3));
        pan.setOpaque(false);

        final ButtonGroup group = new ButtonGroup();
        JToggleButton button;

        button = new JToggleButton(new AbstractAction("",ICON_MOVE) {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.MOVE;
            }
        });
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_ADD) {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.ADD;
            }
        });
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_DELETE) {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.DELETE;
            }
        });
        group.add(button);
        pan.add(button);

        handler.getDecoration().setToolsPane(pan);
    }



    private SimpleFeature editedFeature = null;
    private Point editedGeometry = null;

    @Override
    public void mouseClicked(MouseEvent e) {
        switch(currentAction){
            case ADD:
                final Point geo = handler.getHelper().toJTS(e.getX(), e.getY());
                handler.getHelper().sourceAddGeometry(geo);
                break;
            case DELETE:
                final SimpleFeature sf = handler.getHelper().grabFeature(e.getX(), e.getY());
                if(sf != null){
                    handler.getHelper().sourceRemoveFeature(sf);
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(currentAction){
            case MOVE:
                editedFeature = handler.getHelper().grabFeature(e.getX(), e.getY());
                if(editedFeature != null){
                    editedGeometry = (Point) handler.getHelper().toObjectiveCRS(editedFeature);
                    handler.getDecoration().setGeometries(Collections.singleton(editedGeometry));
                }
                break;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(currentAction == ACTION.MOVE && editedFeature != null){
            final Coordinate mouseCoord = handler.getHelper().toCoord(e.getX(), e.getY());
            editedGeometry.getCoordinate().x = mouseCoord.x;
            editedGeometry.getCoordinate().y = mouseCoord.y;
            handler.getHelper().sourceModifyFeature(editedFeature, editedGeometry);
            handler.getDecoration().clearMemoryLayer();
            editedFeature = null;
            editedGeometry = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if(currentAction == ACTION.MOVE && editedFeature != null){
            final Coordinate mouseCoord = handler.getHelper().toCoord(e.getX(), e.getY());
            editedGeometry.getCoordinate().x = mouseCoord.x;
            editedGeometry.getCoordinate().y = mouseCoord.y;
            handler.getDecoration().repaint();
        }

    }

}
