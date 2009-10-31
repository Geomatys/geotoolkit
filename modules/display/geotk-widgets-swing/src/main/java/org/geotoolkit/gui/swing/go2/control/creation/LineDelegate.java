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

import com.vividsolutions.jts.geom.LineString;
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
 * line creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class LineDelegate extends AbstractEditionDelegate {


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


    private ACTION currentAction = ACTION.MOVE;
    private final EditionHelper.EditionContext context = new EditionHelper.EditionContext();

    public LineDelegate(DefaultEditionHandler handler) {
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
                    context.coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    context.geometry = EditionHelper.createGeometry(context.coords);
                    handler.getDecoration().setGeometries(Collections.singleton(context.geometry));
                    context.added = true;
                    break;
                default:
                    setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){

            // create new data
            if(context.added && context.geometry instanceof LineString){
                handler.getHelper().sourceAddGeometry(context.geometry);
                handler.getDecoration().setGeometries(null);
            }
            //save changes if we had some
            else if(context.modified){
                handler.getHelper().sourceModifyFeature(context.feature, context.geometry);
                handler.getDecoration().setGeometries(null);
            }
            context.reset();
            deleteAction.setEnabled(context.feature != null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(currentAction){
            case MOVE:
                if(context.geometry != null){
                    //start dragging mode
//                    handler.getHelper().grabGeometryNode(context, e.getX(), e.getY());
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

}
