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
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * multipolygon creation handler
 *
 * @author Johann Sorel
 * @module pending
 */
public class MultiPointDelegate extends AbstractEditionDelegate {

     private enum ACTION{
        GEOM_ADD,
        GEOM_MOVE,
        SUB_MOVE,
        SUB_ADD,
        SUB_DELETE
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

    private ACTION currentAction = ACTION.GEOM_MOVE;
    private SimpleFeature feature = null;
    private MultiPoint geometry = null;
    private final List<Point> subGeometries =  new ArrayList<Point>();
    private int[] selection = new int[]{-1,-1,-1};
    private boolean modified = false;
    private boolean added = false;
    private boolean draggingAll = false;


    public MultiPointDelegate(DefaultEditionHandler handler) {
        super(handler);
    }

    private void reset(){
        feature = null;
        geometry = null;
        subGeometries.clear();
        selection = new int[]{-1,-1,-1};
        modified = false;
        added = false;
        draggingAll = false;
        handler.getDecoration().setGeometries(null);

        switch(currentAction){
            case GEOM_ADD:
                handler.getDecoration().setGestureMessages(MSG_SUBGEOM_ADD, null, MSG_DRAG, MSG_ZOOM);
                break;
            case GEOM_MOVE:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case SUB_ADD:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case SUB_DELETE:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case SUB_MOVE:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
        }

    }

    @Override
    public void initialize() {
        //configure tool panel
        final JPanel pan = new JPanel(new GridLayout(4,3));
        pan.setOpaque(false);

        final ButtonGroup group = new ButtonGroup();
        AbstractButton button;

        button = new JToggleButton(new AbstractAction("",ICON_MOVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.GEOM_MOVE;
                reset();
            }
        });
        button.setSelected(true);
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_ADD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.GEOM_ADD;
                reset();
            }
        });
        group.add(button);
        pan.add(button);

        pan.add(new JLabel(" "));

        button = new JToggleButton(new AbstractAction("",ICON_SUBPOLYGON_MOVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.SUB_MOVE;
                reset();
            }
        });
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_SUBPOLYGON_ADD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.SUB_ADD;
                reset();
            }
        });
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_SUBPOLYGON_DELETE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.SUB_DELETE;
                reset();
            }
        });
        group.add(button);
        pan.add(button);

        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));

        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));

        button = new JButton(deleteAction);
        pan.add(button);

        deleteAction.setEnabled(this.feature != null);
        handler.getDecoration().setToolsPane(pan);
        reset();
    }

    private void setCurrentFeature(SimpleFeature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (MultiPoint) handler.getHelper().toObjectiveCRS(feature);
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
                case GEOM_MOVE :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_GEOM_MOVE, null, MSG_DRAG, MSG_ZOOM);
                        }
                    }
                    break;
                case GEOM_ADD:
                    Point candidate = handler.getHelper().toJTS(e.getX(), e.getY());
                    subGeometries.add(candidate);
                    geometry = EditionHelper.createMultiPoint(subGeometries);
                    added = true;
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    handler.getDecoration().setGestureMessages(MSG_NODE_ADD, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                    break;
                case SUB_MOVE :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_SUBGEOM_MOVE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }
                    break;
                case SUB_ADD :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            for(int i=0,n=geometry.getNumGeometries(); i<n; i++){
                                subGeometries.add((Point) geometry.getGeometryN(i));
                            }
                            handler.getDecoration().setGestureMessages(MSG_SUBGEOM_ADD, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }else{
                        Point cdt2 = handler.getHelper().toJTS(e.getX(), e.getY());
                        subGeometries.add(cdt2);
                        geometry = EditionHelper.createMultiPoint(subGeometries);
                        added = true;
                        handler.getDecoration().setGeometries(Collections.singleton(geometry));
                        handler.getDecoration().setGestureMessages(MSG_NODE_ADD, MSG_SUBGEOM_VALIDATE, MSG_DRAG, MSG_ZOOM);
                    }
                    break;
                case SUB_DELETE :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_SUBGEOM_DELETE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }else{
                        MultiPoint result = (MultiPoint) handler.getHelper().deleteSubGeometry(geometry, e.getX(), e.getY());
                        if(result != null){
                            modified = modified || result != geometry;
                            geometry = result;
                            handler.getDecoration().setGeometries(Collections.singleton(geometry));
                        }
                    }
                    break;
            }
        }else if(button == MouseEvent.BUTTON3){
            switch(currentAction){
                case GEOM_MOVE:
                    if(draggingAll){
                        handler.getHelper().sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case GEOM_ADD:
                    if (subGeometries.size() > 0) {
                        MultiPoint geo = EditionHelper.createMultiPoint(subGeometries);
                        handler.getHelper().sourceAddGeometry(geo);
                        reset();
                    }
                    handler.getDecoration().setGeometries(null);
                    break;
                case SUB_MOVE :
                    if(modified){
                        handler.getHelper().sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case SUB_ADD :
                    if (subGeometries.size() > 0) {
                        MultiPoint geo = EditionHelper.createMultiPoint(subGeometries);
                        handler.getHelper().sourceModifyFeature(feature, geo);
                    }
                    handler.getDecoration().setGeometries(null);
                    reset();
                    break;
                case SUB_DELETE :
                    if(modified){
                        handler.getHelper().sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;

            }

            deleteAction.setEnabled(feature != null);
        }
    }

    int pressed = -1;
    int lastX = 0;
    int lastY = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = e.getButton();
        lastX = e.getX();
        lastY = e.getY();

        switch(currentAction){
            case GEOM_MOVE:
                if(this.geometry != null && e.getButton()==BUTTON1){
                    handler.getDecoration().setGestureMessages(MSG_GEOM_MOVE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                    try {
                        //start dragging mode
                        final Geometry mouseGeo = handler.getHelper().mousePositionToGeometry(e.getX(), e.getY());
                        if(mouseGeo.intersects(geometry)){
                            draggingAll = true;
                        }
                    } catch (NoninvertibleTransformException ex) {
                        Logger.getLogger(MultiPolygonDelegate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                break;
            case SUB_MOVE:
                if(this.geometry != null && e.getButton()==BUTTON1){
                    //start dragging mode
                    selection = handler.getHelper().grabGeometryNode(geometry, e.getX(), e.getY());
                    return;
                }
                break;
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        switch(currentAction){
            case GEOM_MOVE:
                if(draggingAll && pressed==BUTTON1){
                    int currentX = e.getX();
                    int currentY = e.getY();

                    handler.getHelper().moveGeometry(geometry, currentX-lastX, currentY-lastY);
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
            case SUB_MOVE:
                if(selection[0] >= 0 && pressed==BUTTON1){
                    modified = true;
                    int currentX = e.getX();
                    int currentY = e.getY();

                    handler.getHelper().moveSubGeometry(geometry,selection[0], currentX-lastX, currentY-lastY);
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        switch(currentAction){
            case GEOM_MOVE:
                if(draggingAll && pressed==BUTTON1){
                    int currentX = e.getX();
                    int currentY = e.getY();

                    handler.getHelper().moveGeometry(geometry, currentX-lastX, currentY-lastY);
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
            case SUB_MOVE:
                if(selection[0] >= 0 && pressed==BUTTON1){
                    modified = true;
                    int currentX = e.getX();
                    int currentY = e.getY();

                    handler.getHelper().moveSubGeometry(geometry,selection[0], currentX-lastX, currentY-lastY);
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
        }
        super.mouseDragged(e);
    }

}
