/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;

/**
 * Circle creation handler.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CircleCreationDelegate extends AbstractFeatureEditionDelegate {

    private static final GeometryFactory GF = new GeometryFactory();

    private Geometry geometry = null;
    private Coordinate center = null;

    public CircleCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        geometry = null;
        center = null;
        decoration.setGeometries(null);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){

            if(center == null){
                center = helper.toCoord(e.getX(), e.getY());
            }else{
                helper.sourceAddGeometry(geometry);
                reset();
                decoration.setGeometries(null);
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
        if(center != null){
            final Coordinate point = helper.toCoord(e.getX(), e.getY());
            final Point centerPoint = GF.createPoint(center);
            final double distance = centerPoint.distance(GF.createPoint(point));

            geometry = centerPoint.buffer(distance);
            decoration.setGeometries(Collections.singleton(geometry));
        }

        super.mouseMoved(e);
    }

}
