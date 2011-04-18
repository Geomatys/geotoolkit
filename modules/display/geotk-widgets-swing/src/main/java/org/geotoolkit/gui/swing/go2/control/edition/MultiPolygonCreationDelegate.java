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
package org.geotoolkit.gui.swing.go2.control.edition;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import static org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionDecoration.*;
import static java.awt.event.MouseEvent.*;

/**
 * multipolygon creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiPolygonCreationDelegate extends AbstractFeatureEditionDelegate {

     private enum ACTION{
        GEOM_ADD,
        GEOM_MOVE,
        NODE_MOVE,
        NODE_ADD,
        NODE_DELETE,
        SUB_MOVE,
        SUB_ADD,
        SUB_DELETE
    }

    private ACTION currentAction = ACTION.GEOM_MOVE;
    private int nbRighClick = 0;
    private Feature feature = null;
    private MultiPolygon geometry = null;
    private final List<Geometry> subGeometries =  new ArrayList<Geometry>();
    private int[] selection = new int[]{-1,-1,-1};
    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private boolean modified = false;
    private boolean added = false;
    private boolean draggingAll = false;
    private boolean justCreated = false;


    public MultiPolygonCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        geometry = null;
        subGeometries.clear();
        selection = new int[]{-1,-1,-1};
        modified = false;
        added = false;
        draggingAll = false;
        coords.clear();
        justCreated = false;
        nbRighClick = 0;
        decoration.setGeometries(null);
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (MultiPolygon) helper.toObjectiveCRS(feature);
        }else{
            this.geometry = null;
        }
        decoration.setGeometries(Collections.singleton(this.geometry));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            switch(currentAction){
                case GEOM_MOVE :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }
                    break;
                case GEOM_ADD:
                    nbRighClick = 0;
                    
                    if(justCreated){
                        justCreated = false;
                        //we must modify the second point since two point where added at the start
                        coords.remove(2);
                        coords.remove(1);
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        
                    }else if(coords.size() == 0){
                        justCreated = true;
                        //this is the first point of the geometry we create
                        //add 3 points that will be used when moving the mouse around
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        Geometry candidate = EditionHelper.createPolygon(coords);
                        subGeometries.add(candidate);
                    }else{
                        justCreated = false;
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                    }

                    Geometry candidate = EditionHelper.createPolygon(coords);
                    if (subGeometries.size() > 0) {
                        subGeometries.remove(subGeometries.size() - 1);
                    }
                    subGeometries.add(candidate);
                    geometry = EditionHelper.createMultiPolygon(subGeometries);
                    added = true;
                    decoration.setGeometries(Collections.singleton(geometry));
                    break;
                case NODE_MOVE :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }
                    break;
                case NODE_ADD :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }else{
                        MultiPolygon result = (MultiPolygon) helper.insertNode(geometry, e.getX(), e.getY());
                        modified = modified || result != geometry;
                        geometry = result;
                        decoration.setGeometries(Collections.singleton(geometry));
                    }
                    break;
                case NODE_DELETE :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }else{
                        MultiPolygon result = (MultiPolygon) helper.deleteNode(geometry, e.getX(), e.getY());
                        if(result != null){
                            modified = modified || result != geometry;
                            geometry = result;
                            decoration.setGeometries(Collections.singleton(geometry));
                        }
                    }
                    break;
                case SUB_MOVE :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }
                    break;
                case SUB_ADD :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                        if(geometry != null){
                            for(int i=0,n=geometry.getNumGeometries(); i<n; i++){
                                subGeometries.add(geometry.getGeometryN(i));
                            }
                        }
                    }else{
                        nbRighClick = 0;

                        if(justCreated){
                            justCreated = false;
                            //we must modify the second point since two point where added at the start
                            coords.remove(2);
                            coords.remove(1);
                            coords.add(helper.toCoord(e.getX(), e.getY()));
                            coords.add(helper.toCoord(e.getX(), e.getY()));

                        }else if(coords.size() == 0){
                            justCreated = true;
                            //this is the first point of the geometry we create
                            //add 3 points that will be used when moving the mouse around
                            coords.add(helper.toCoord(e.getX(), e.getY()));
                            coords.add(helper.toCoord(e.getX(), e.getY()));
                            coords.add(helper.toCoord(e.getX(), e.getY()));
                            Geometry cdt2 = EditionHelper.createPolygon(coords);
                            subGeometries.add(cdt2);
                        }else{
                            justCreated = false;
                            coords.add(helper.toCoord(e.getX(), e.getY()));
                        }

                        Geometry cdt2 = EditionHelper.createPolygon(coords);
                        if (subGeometries.size() > 0) {
                            subGeometries.remove(subGeometries.size() - 1);
                        }
                        subGeometries.add(cdt2);
                        geometry = EditionHelper.createMultiPolygon(subGeometries);
                        added = true;
                        decoration.setGeometries(Collections.singleton(geometry));
                    }
                    break;
                case SUB_DELETE :
                    if(geometry == null){
                        setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
                    }else{
                        MultiPolygon result = (MultiPolygon) helper.deleteSubGeometry(geometry, e.getX(), e.getY());
                        if(result != null){
                            modified = modified || result != geometry;
                            geometry = result;
                            decoration.setGeometries(Collections.singleton(geometry));
                        }
                    }
                    break;
            }
        }else if(button == MouseEvent.BUTTON3){
            switch(currentAction){
                case GEOM_MOVE:
                    if(draggingAll){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case GEOM_ADD:
                    justCreated = false;
                    nbRighClick++;
                    if (nbRighClick == 1) {
                        if (coords.size() > 2) {
                            if (subGeometries.size() > 0) {
                                subGeometries.remove(subGeometries.size() - 1);
                            }
                            Geometry geo = EditionHelper.createPolygon(coords);
                            subGeometries.add(geo);
                        } else if (coords.size() > 0) {
                            if (subGeometries.size() > 0) {
                                subGeometries.remove(subGeometries.size() - 1);
                            }
                        }
                    } else {
                        if (subGeometries.size() > 0) {
                            Geometry geo = EditionHelper.createMultiPolygon(subGeometries);
                            helper.sourceAddGeometry(geo);
                            nbRighClick = 0;
                            reset();
                        }
                        decoration.setGeometries(null);
                    }
                    coords.clear();
                    break;
                case NODE_MOVE :
                    if(modified){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case NODE_ADD :
                    if(modified){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case NODE_DELETE :
                    if(modified){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case SUB_MOVE :
                    if(modified){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;
                case SUB_ADD :
                    if (subGeometries.size() > 0) {
                        Geometry geo = EditionHelper.createMultiPolygon(subGeometries);
                        helper.sourceModifyFeature(feature, geo);
                    }
                    decoration.setGeometries(null);
                    coords.clear();
                    reset();
                    break;
                case SUB_DELETE :
                    if(modified){
                        helper.sourceModifyFeature(feature, geometry);
                    }
                    reset();
                    break;

            }

        }
    }

    int pressed = -1;
    int lastX = 0;
    int lastY = 0;

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();
        lastX = e.getX();
        lastY = e.getY();
        
        switch(currentAction){
            case GEOM_MOVE:
                if(this.geometry != null && e.getButton()==BUTTON1){
                    try {
                        //start dragging mode
                        final Geometry mouseGeo = helper.mousePositionToGeometry(e.getX(), e.getY());
                        if(mouseGeo.intersects(geometry)){
                            draggingAll = true;
                        }
                    } catch (NoninvertibleTransformException ex) {
                        Logger.getLogger(MultiPolygonCreationDelegate.class.getName()).log(Level.WARNING, null, ex);
                    }
                    return;
                }
                break;
            case NODE_MOVE:
                if(this.geometry != null && e.getButton()==BUTTON1){
                    //start dragging mode
                    selection = helper.grabGeometryNode(geometry, e.getX(), e.getY());
                    return;
                }
                break;
            case SUB_MOVE:
                if(this.geometry != null && e.getButton()==BUTTON1){
                    //start dragging mode
                    selection = helper.grabGeometryNode(geometry, e.getX(), e.getY());
                    return;
                }
                break;
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

        switch(currentAction){
            case GEOM_MOVE:
                if(draggingAll && pressed==BUTTON1){
                    int currentX = e.getX();
                    int currentY = e.getY();

                    helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
                    decoration.setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
            case NODE_MOVE:
                if(selection[1] >= 0 && e.getButton()==BUTTON1){
                    //we were dragging a node
                    final Coordinate mouseCoord = helper.toCoord(e.getX(), e.getY());
                    final Geometry sub = geometry.getGeometryN(selection[0]);
                    final Coordinate[] coords = sub.getCoordinates();
                    coords[selection[1]].setCoordinate(mouseCoord);
                    coords[selection[2]].setCoordinate(mouseCoord);
                    sub.geometryChanged();
                    geometry.geometryChanged();
                    decoration.setGeometries(Collections.singleton(geometry));
                    modified = true;
                    return;
                }
                break;
            case SUB_MOVE:
                if(selection[0] >= 0 && pressed==BUTTON1){
                    modified = true;
                    int currentX = e.getX();
                    int currentY = e.getY();

                    helper.moveSubGeometry(geometry,selection[0], currentX-lastX, currentY-lastY);
                    decoration.setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        switch(currentAction){
            case GEOM_MOVE:
                if(draggingAll && pressed==BUTTON1){
                    int currentX = e.getX();
                    int currentY = e.getY();
                    
                    helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
                    decoration.setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
            case NODE_MOVE:
                if(selection[1] >= 0 && pressed==BUTTON1){
                    final Coordinate mouseCoord = helper.toCoord(e.getX(), e.getY());
                    final Geometry sub = geometry.getGeometryN(selection[0]);
                    final Coordinate[] coords = sub.getCoordinates();
                    coords[selection[1]].setCoordinate(mouseCoord);
                    coords[selection[2]].setCoordinate(mouseCoord);
                    sub.geometryChanged();
                    geometry.geometryChanged();
                    decoration.setGeometries(Collections.singleton(geometry));
                    modified = true;
                    return;
                }
                break;
            case SUB_MOVE:
                if(selection[0] >= 0 && pressed==BUTTON1){
                    modified = true;
                    int currentX = e.getX();
                    int currentY = e.getY();

                    helper.moveSubGeometry(geometry,selection[0], currentX-lastX, currentY-lastY);
                    decoration.setGeometries(Collections.singleton(geometry));

                    lastX = currentX;
                    lastY = currentY;
                    return;
                }
                break;
        }
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        switch(currentAction){
            case SUB_ADD :
            case GEOM_ADD :
                if(coords.size() > 2){
                    if(justCreated){
                        coords.remove(coords.size()-1);
                        coords.remove(coords.size()-1);
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                    }else{
                        coords.remove(coords.size()-1);
                        coords.add(helper.toCoord(e.getX(), e.getY()));
                    }
                    Geometry candidate = EditionHelper.createPolygon(coords);
                    if (subGeometries.size() > 0) {
                        subGeometries.remove(subGeometries.size() - 1);
                    }
                    subGeometries.add(candidate);
                    geometry = EditionHelper.createMultiPolygon(subGeometries);
                    added = true;
                    decoration.setGeometries(Collections.singleton(geometry));
                    return;
                }
                break;
        }
        super.mouseMoved(e);
    }

}
