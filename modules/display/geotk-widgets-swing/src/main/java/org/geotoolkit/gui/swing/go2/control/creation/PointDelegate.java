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

    public PointDelegate(DefaultEditionHandler handler) {
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
        final JPanel pan = new JPanel(new GridLayout(3,2));
        pan.setOpaque(false);

        final ButtonGroup group = new ButtonGroup();
        AbstractButton button;

        button = new JToggleButton(new AbstractAction("",ICON_MOVE) {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.MOVE;
            }
        });
        button.setSelected(true);
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

        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));

        button = new JButton(deleteAction);
        group.add(button);
        pan.add(button);

        deleteAction.setEnabled(feature != null);
        handler.getDecoration().setToolsPane(pan);
    }

    private void setCurrentFeature(SimpleFeature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (Point)handler.getHelper().toObjectiveCRS(feature);
        }else{
            this.geometry = null;
        }
        deleteAction.setEnabled(this.feature != null);
        handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            switch(currentAction){
                case ADD:
                    if(this.geometry == null){
                        final Point geo = handler.getHelper().toJTS(e.getX(), e.getY());
                        handler.getHelper().sourceAddGeometry(geo);
                    }
                    break;
                default:
                    setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){
            //save changes if we had some
            if(this.modified){
                handler.getHelper().sourceModifyFeature(this.feature, this.geometry);
                handler.getDecoration().setGeometries(null);
            }
            reset();
            deleteAction.setEnabled(this.feature != null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(currentAction){
            case MOVE:
                if(this.geometry != null){
                    //start dragging mode
                    coordSelected = handler.getHelper().grabGeometrynode(geometry, e.getX(), e.getY());
                }
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(currentAction == ACTION.MOVE && coordSelected){
            //we were dragging a node
            this.modified = true;
            this.geometry = handler.getHelper().toJTS(e.getX(), e.getY());
            handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(currentAction == ACTION.MOVE && coordSelected){
            this.modified = true;
            this.geometry = handler.getHelper().toJTS(e.getX(), e.getY());
            handler.getDecoration().setGeometries(Collections.singleton(this.geometry));
        }
    }

}
