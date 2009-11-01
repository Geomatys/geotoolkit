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
import com.vividsolutions.jts.geom.Polygon;
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
public class PolygonDelegate extends AbstractEditionDelegate {

     private enum ACTION{
        GEOM_ADD,
        GEOM_MOVE,
        NODE_MOVE,
        NODE_ADD,
        NODE_DELETE
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
    private Polygon geometry = null;
    private int[] selection = new int[]{-1,-1,-1};
    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private boolean modified = false;
    private boolean added = false;
    private boolean draggingAll = false;
    private boolean justCreated = false;


    public PolygonDelegate(DefaultEditionHandler handler) {
        super(handler);
    }

    private void reset(){
        feature = null;
        geometry = null;
        selection = new int[]{-1,-1,-1};
        modified = false;
        added = false;
        draggingAll = false;
        coords.clear();
        justCreated = false;
        handler.getDecoration().setGeometries(null);

        switch(currentAction){
            case GEOM_ADD:
                handler.getDecoration().setGestureMessages(MSG_GEOM_ADD, null, MSG_DRAG, MSG_ZOOM);
                break;
            case GEOM_MOVE:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case NODE_ADD:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case NODE_DELETE:
                handler.getDecoration().setGestureMessages(MSG_GEOM_SELECT, null, MSG_DRAG, MSG_ZOOM);
                break;
            case NODE_MOVE:
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
        button.setToolTipText(MSG_GEOM_MOVE);
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
        button.setToolTipText(MSG_GEOM_ADD);
        group.add(button);
        pan.add(button);

        pan.add(new JLabel(" "));

        button = new JToggleButton(new AbstractAction("",ICON_NODE_MOVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.NODE_MOVE;
                reset();
            }
        });
        button.setToolTipText(MSG_NODE_MOVE);
        group.add(button);
        pan.add(button);

        button = new JToggleButton(new AbstractAction("",ICON_NODE_ADD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.NODE_ADD;
                reset();
            }
        });
        button.setToolTipText(MSG_NODE_ADD);
        group.add(button);
        pan.add(button);


        button = new JToggleButton(new AbstractAction("",ICON_NODE_DELETE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = ACTION.NODE_DELETE;
                reset();
            }
        });
        button.setToolTipText(MSG_NODE_DELETE);
        group.add(button);
        pan.add(button);


        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));

        pan.add(new JLabel(" "));
        pan.add(new JLabel(" "));

        button = new JButton(deleteAction);
        button.setToolTipText(MSG_GEOM_DELETE);
        pan.add(button);

        deleteAction.setEnabled(this.feature != null);
        handler.getDecoration().setToolsPane(pan);
        reset();
    }

    private void setCurrentFeature(SimpleFeature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (Polygon) handler.getHelper().toObjectiveCRS(feature);
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

                    if(justCreated){
                        justCreated = false;
                        //we must modify the second point since two point where added at the start
                        coords.remove(2);
                        coords.remove(1);
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));

                    }else if(coords.size() == 0){
                        justCreated = true;
                        //this is the first point of the geometry we create
                        //add 3 points that will be used when moving the mouse around
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    }else{
                        justCreated = false;
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    }

                    geometry = EditionHelper.createPolygon(coords);
                    added = true;
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    handler.getDecoration().setGestureMessages(MSG_NODE_ADD, MSG_SUBGEOM_VALIDATE, MSG_DRAG, MSG_ZOOM);
                    break;
                case NODE_MOVE :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_NODE_MOVE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }
                    break;
                case NODE_ADD :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_NODE_ADD, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }else{
                        Polygon result = (Polygon) handler.getHelper().insertNode(geometry, e.getX(), e.getY());
                        modified = modified || result != geometry;
                        geometry = result;
                        handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    }
                    break;
                case NODE_DELETE :
                    if(geometry == null){
                        setCurrentFeature(handler.getHelper().grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            handler.getDecoration().setGestureMessages(MSG_NODE_DELETE, MSG_VALIDATE, MSG_DRAG, MSG_ZOOM);
                        }
                    }else{
                        Polygon result = (Polygon) handler.getHelper().deleteNode(geometry, e.getX(), e.getY());
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
                    justCreated = false;
                    handler.getHelper().sourceAddGeometry(geometry);
                    reset();
                    handler.getDecoration().setGeometries(null);
                    coords.clear();
                    break;
                case NODE_MOVE :
                    if(modified){
                        handler.getHelper().sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case NODE_ADD :
                    if(modified){
                        handler.getHelper().sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case NODE_DELETE :
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
            case NODE_MOVE:
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
            case NODE_MOVE:
                if(selection[1] >= 0 && e.getButton()==BUTTON1){
                    //we were dragging a node
                    final Coordinate mouseCoord = handler.getHelper().toCoord(e.getX(), e.getY());
                    final Geometry sub = geometry.getGeometryN(selection[0]);
                    final Coordinate[] coords = sub.getCoordinates();
                    coords[selection[1]].setCoordinate(mouseCoord);
                    coords[selection[2]].setCoordinate(mouseCoord);
                    sub.geometryChanged();
                    geometry.geometryChanged();
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    modified = true;
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
            case NODE_MOVE:
                if(selection[1] >= 0 && pressed==BUTTON1){
                    final Coordinate mouseCoord = handler.getHelper().toCoord(e.getX(), e.getY());
                    final Geometry sub = geometry.getGeometryN(selection[0]);
                    final Coordinate[] coords = sub.getCoordinates();
                    coords[selection[1]].setCoordinate(mouseCoord);
                    coords[selection[2]].setCoordinate(mouseCoord);
                    sub.geometryChanged();
                    geometry.geometryChanged();
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    modified = true;
                    return;
                }
                break;
        }
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch(currentAction){
            case GEOM_ADD :
                if(coords.size() > 2){
                    if(justCreated){
                        coords.remove(coords.size()-1);
                        coords.remove(coords.size()-1);
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    }else{
                        coords.remove(coords.size()-1);
                        coords.add(handler.getHelper().toCoord(e.getX(), e.getY()));
                    }
                    geometry = EditionHelper.createPolygon(coords);
                    added = true;
                    handler.getDecoration().setGeometries(Collections.singleton(geometry));
                    return;
                }
                break;
        }
        super.mouseMoved(e);
    }

}
