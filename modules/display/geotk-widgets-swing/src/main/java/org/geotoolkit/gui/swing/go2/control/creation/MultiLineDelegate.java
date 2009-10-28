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

import com.vividsolutions.jts.geom.Geometry;
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
 * multiline creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiLineDelegate extends AbstractEditionDelegate {

     private enum ACTION{
        ADD,
        MOVE
    }

    private final Action deleteAction = new AbstractAction("", ICON_DELETE) {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(context.feature != null){
                handler.getHelper().sourceRemoveFeature(context.feature);
                handler.getDecoration().setGeometries(null);
            }
        }
    };


    private final EditionHelper.EditionContext context = new EditionHelper.EditionContext();
    private ACTION currentAction = ACTION.MOVE;
    private int nbRighClick = 0;

    public MultiLineDelegate(DefaultEditionHandler handler) {
        super(handler);
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

        deleteAction.setEnabled(context.feature != null);
        handler.getDecoration().setToolsPane(pan);
    }

    private void setCurrentFeature(SimpleFeature feature){
        context.feature = feature;
        if(feature != null){
            context.geometry = handler.getHelper().toObjectiveCRS(feature);
        }else{
            context.geometry = null;
        }
        deleteAction.setEnabled(context.feature != null);
        handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            switch(currentAction){
                case ADD:
                    nbRighClick = 0;
                    context.coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    Geometry candidate = EditionHelper.createGeometry(context.coords);
                    if (context.subGeometries.size() > 0) {
                        context.subGeometries.remove(context.subGeometries.size() - 1);
                    }
                    context.subGeometries.add(candidate);
                    context.geometry = EditionHelper.createMultiLine(context.subGeometries);
                    handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
                    context.added = true;
                    handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
                    break;
                default:
                    setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){
            switch(currentAction){
                case ADD:
                    nbRighClick++;
                    if (nbRighClick == 1) {
                        if (context.coords.size() > 1) {
                            if (context.subGeometries.size() > 0) {
                                context.subGeometries.remove(context.subGeometries.size() - 1);
                            }
                            Geometry geo = EditionHelper.createLine(context.coords);
                            context.subGeometries.add(geo);
                        } else if (context.coords.size() > 0) {
                            if (context.subGeometries.size() > 0) {
                                context.subGeometries.remove(context.subGeometries.size() - 1);
                            }
                        }
                    } else {
                        if (context.subGeometries.size() > 0) {
                            Geometry geo = EditionHelper.createMultiLine(context.subGeometries);
                            handler.getHelper().sourceAddGeometry(new Geometry[]{geo});
                            nbRighClick = 0;
                            context.reset();
                        }
                        handler.getDecoration().setGeometries(null);
                    }
                    context.coords.clear();
                    break;
                case MOVE :
                    if(context.modified){
                        handler.getHelper().sourceModifyFeature(context.feature, context.geometry);
                        handler.getDecoration().setGeometries(null);
                    }
                    break;

            }

            deleteAction.setEnabled(context.feature != null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(currentAction){
            case MOVE:
                if(context.geometry != null){
                    //start dragging mode
                    handler.getHelper().grabGeometryNode(context, e.getX(), e.getY());
                }
                break;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(currentAction == ACTION.MOVE && context.nodes != null){
            //we were dragging a node
            handler.getHelper().dragGeometryNode(context, e.getX(), e.getY());
            handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if(currentAction == ACTION.MOVE && context.nodes != null){
            handler.getHelper().dragGeometryNode(context, e.getX(), e.getY());
            handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
        }

    }




//    @Override
//    public void mouseClicked(MouseEvent e) {//
//        if (button == MouseEvent.BUTTON1) {
//            nbRightClick = 0;
//            coords.add(editionDecoration.toCoord(e.getX(), e.getY()));
//            updateCreationGeoms();
//
//        } else if (button == MouseEvent.BUTTON3) {
//            nbRightClick++;
//            if (nbRightClick == 1) {
//                inCreation = false;
//                if (coords.size() > 1) {
//                    if (geoms.size() > 0) {
//                        geoms.remove(geoms.size() - 1);
//                    }
//                    Geometry geo = EditionHelper.createLine(coords);
//                    geoms.add(geo);
//                } else if (coords.size() > 0) {
//                    if (geoms.size() > 0) {
//                        geoms.remove(geoms.size() - 1);
//                    }
//                }
//            } else {
//                if (geoms.size() > 0) {
//                    Geometry geo = EditionHelper.createMultiLine(geoms);
//                    editionDecoration.editAddGeometry(new Geometry[]{geo});
//                    nbRightClick = 0;
//                    geoms.clear();
//                }
//            }
//            coords.clear();
//        }
//    }

}
