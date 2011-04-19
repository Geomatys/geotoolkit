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


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;

import static org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionDecoration.*;

/**
 * Geometry moving tool.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class GeometryNodeDelegate extends AbstractFeatureEditionDelegate {

    private Feature feature = null;
    private final EditionHelper.EditionGeometry selection = new EditionHelper.EditionGeometry();
    private boolean modified = false;

    private int pressed = -1;

    public GeometryNodeDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        selection.reset();
        decoration.setGeometries(null);
        decoration.setNodeSelection(null);
    }

    private void refreshDecoration(){
        decoration.setGeometries(Collections.singleton(this.selection.geometry));
        decoration.setNodeSelection(this.selection);
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.selection.geometry = helper.toObjectiveCRS(feature);
        }else{
            this.selection.geometry = null;
        }
        refreshDecoration();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            if(selection.geometry == null){
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }else if(e.getClickCount() >= 2){
                //double click = add a node
                final Geometry result;
                if(selection.geometry instanceof LineString){
                    result = helper.insertNode((LineString)selection.geometry, e.getX(), e.getY());
                }else if(selection.geometry instanceof Polygon){
                    result = helper.insertNode((Polygon)selection.geometry, e.getX(), e.getY());
                }else if(selection.geometry instanceof GeometryCollection){
                    result = helper.insertNode((GeometryCollection)selection.geometry, e.getX(), e.getY());
                }else{
                    result = selection.geometry;
                }
                modified = modified || result != selection.geometry;
                selection.geometry = result;
                decoration.setGeometries(Collections.singleton(selection.geometry));
            }else if(e.getClickCount() == 1){
                //single click with a geometry = select a node
                helper.grabGeometryNode(e.getX(), e.getY(), selection);
                decoration.setNodeSelection(selection);
            }
        }else if(button == MouseEvent.BUTTON3){
            helper.sourceModifyFeature(feature, selection.geometry);
            reset();
        }

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();

        if(pressed == MouseEvent.BUTTON1){
            if(selection.geometry == null){
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }else if(e.getClickCount() == 1){
                //single click with a geometry = select a node
                helper.grabGeometryNode(e.getX(), e.getY(), selection);
                decoration.setNodeSelection(selection);
            }
        }

        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if(pressed == MouseEvent.BUTTON1 && selection != null){
            //dragging node
            selection.moveSelectedNode(helper.toCoord(e.getX(), e.getY()));
            refreshDecoration();
            modified = true;
            return;
        }
        
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(KeyEvent.VK_DELETE == e.getKeyCode() && selection != null){
            //delete node
            selection.deleteSelectedNode();
            refreshDecoration();
            modified = true;
            return;
        }
    }

}
