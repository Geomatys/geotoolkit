/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
 *    (C) 2011, Geomatys
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

import com.vividsolutions.jts.geom.Point;

import java.util.Collections;
import java.awt.event.MouseEvent;

import org.opengis.feature.Feature;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.map.FeatureMapLayer;

import static org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionDecoration.*;
import static java.awt.event.MouseEvent.*;

/**
 * Point creation delegate.
 *
 * @author Johann Sorel
 * @module pending
 */
public class PointCreationDelegate extends AbstractFeatureEditionDelegate{


    public PointCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private enum ACTION{
        ADD,
        MOVE
    }

    private ACTION currentAction = ACTION.MOVE;
    private Feature feature = null;
    private Point geometry = null;
    private boolean modified = false;
    private boolean coordSelected = false;

    private void reset(){
        feature = null;
        geometry = null;
        modified = false;
        coordSelected = false;
        decoration.setGeometries(null);
    }

    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = (Point)helper.toObjectiveCRS(feature);
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
                case ADD:
                    if(this.geometry == null){
                        final Point geo = helper.toJTS(e.getX(), e.getY());
                        helper.sourceAddGeometry(geo);
                    }
                    break;
                case MOVE:
                    setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){
            //save changes if we had some
            if(this.modified){
                helper.sourceModifyFeature(this.feature, this.geometry);
                decoration.setGeometries(null);
            }
            reset();
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
                    coordSelected = helper.grabGeometrynode(geometry, e.getX(), e.getY());
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
                    this.geometry = helper.toJTS(e.getX(), e.getY());
                    decoration.setGeometries(Collections.singleton(this.geometry));
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
                    this.geometry = helper.toJTS(e.getX(), e.getY());
                    decoration.setGeometries(Collections.singleton(this.geometry));
                    return;
                }

        }
        super.mouseDragged(e);
    }


}
