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

import com.vividsolutions.jts.geom.Point;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.opengis.feature.simple.SimpleFeature;

import static org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionDecoration.*;
import static java.awt.event.MouseEvent.*;

/**
 * point creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class PointDelegate extends AbstractEditionDelegate{

    private enum ACTION{
        ADD,
        MOVE
    }

    private final Action deleteAction = new AbstractAction("", ICON_DELETE) {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(feature != null){
                handler.getHelper().sourceRemoveFeature(feature);
                reset();
            }
        }
    };


    private ACTION currentAction = ACTION.MOVE;
    private SimpleFeature feature = null;
    private Point geometry = null;
    private boolean modified = false;
    private boolean coordSelected = false;

    public PointDelegate(final DefaultEditionHandler handler) {
        super(handler);
    }

    private void reset(){
        feature = null;
        geometry = null;
        modified = false;
        coordSelected = false;
        handler.getDecoration().setGeometries(null);
    }

    @Override
    public void initialize() {
        //configure tool panel
        final JPanel pan = new JPanel(new GridLayout(5,1));
        pan.setOpaque(false);

        final ButtonGroup group = new ButtonGroup();
        AbstractButton button;

        button = new JToggleButton(new AbstractAction("",ICON_MOVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.MOVE;
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
            }
        });
        button.setToolTipText(MSG_GEOM_MOVE);
        button.setSelected(true);
        group.add(button);
        pan.add(button);

        pan.add(new JLabel(" "));

        button = new JToggleButton(new AbstractAction("",ICON_ADD) {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.ADD;
                handler.getDecoration().setGestureMessages(MSG_GEOM_ADD, null, MSG_DRAG, MSG_ZOOM);
            }
        });
        button.setToolTipText(MSG_GEOM_ADD);
        group.add(button);
        pan.add(button);

        pan.add(new JLabel(" "));

        button = new JButton(deleteAction);
        button.setToolTipText(MSG_GEOM_DELETE);
        pan.add(button);

        deleteAction.setEnabled(feature != null);
        handler.getDecoration().setToolsPane(pan);

        handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
    }

    private void setCurrentFeature(final SimpleFeature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (Point)handler.getHelper().toObjectiveCRS(feature);
            handler.getDecoration().setGestureMessages(MSG_GEOM_MOVE, null, MSG_DRAG, MSG_ZOOM);
        }else{
            this.geometry = null;
        }
        deleteAction.setEnabled(this.feature != null);
        handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            switch(currentAction){
                case ADD:
                    if(this.geometry == null){
                        final Point geo = handler.getHelper().toJTS(e.getX(), e.getY());
                        handler.getHelper().sourceAddGeometry(geo);
                    }
                    break;
                case MOVE:
                    setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){
            //save changes if we had some
            if(this.modified){
                handler.getHelper().sourceModifyFeature(this.feature, this.geometry);
                handler.getDecoration().setGeometries(null);
            }
            handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
            reset();
            deleteAction.setEnabled(this.feature != null);
        }
    }

    int pressed = -1;

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();
        switch(currentAction){
            case MOVE:
                if(this.geometry != null && e.getButton() == BUTTON1){
                    //start dragging mode
                    coordSelected = handler.getHelper().grabGeometrynode(geometry, e.getX(), e.getY());
                    return;
                }
                break;
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        switch(currentAction){
            case MOVE:
                if(coordSelected && e.getButton() == BUTTON1){
                    //we were dragging a node
                    this.modified = true;
                    this.geometry = handler.getHelper().toJTS(e.getX(), e.getY());
                    handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
                    return;
                }
                break;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        switch(currentAction){
            case MOVE:
                if(coordSelected && pressed == BUTTON1){
                    this.modified = true;
                    this.geometry = handler.getHelper().toJTS(e.getX(), e.getY());
                    handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
                    handler.getDecoration().setGestureMessages(MSG_GEOM_MOVE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                    return;
                }

        }
        super.mouseDragged(e);
    }

}
