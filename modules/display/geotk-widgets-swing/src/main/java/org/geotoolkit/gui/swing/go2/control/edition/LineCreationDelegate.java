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

import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * line creation handler
 *
 * @author Johann Sorel
 * @module pending
 */
public class LineCreationDelegate extends AbstractFeatureEditionDelegate {

    private LineString geometry = null;
    private final List<Coordinate> coords = new ArrayList<Coordinate>();

    public LineCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }
    
    private void reset(){
        geometry = null;
        coords.clear();
        decoration.setGeometries(null);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            coords.add(helper.toCoord(e.getX(), e.getY()));
            if(coords.size() == 1){
                //this is the first point of the geometry we create
                //add another point that will be used when moving the mouse around
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }
            geometry = EditionHelper.createLine(coords);
            decoration.setGeometries(Collections.singleton(geometry));
                    
        }else if(button == MouseEvent.BUTTON3){            
            helper.sourceAddGeometry(geometry);
            reset();
            decoration.setGeometries(null);
            coords.clear();
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
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if(coords.size() > 1){
            coords.remove(coords.size()-1);
            coords.add(helper.toCoord(e.getX(), e.getY()));
            geometry = EditionHelper.createLine(coords);
            decoration.setGeometries(Collections.singleton(geometry));
            return;
        }
        super.mouseMoved(e);
    }

}
