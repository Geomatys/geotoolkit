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
package org.geotoolkit.gui.swing.go2.control.information;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.logging.Logging;

/**
 * Panoramic handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class AreaHandler implements CanvasHandler {

    private static final Logger LOGGER = Logging.getLogger(AreaHandler.class);

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    public static final List<Unit> UNITS = new ArrayList<Unit>();

    static{
        UNITS.add(SI.SQUARE_METRE);
        UNITS.add(NonSI.ARE);
        UNITS.add(NonSI.HECTARE);
    }

    private final MouseListen mouseInputListener;

    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private final AreaDecoration deco = new AreaDecoration();
    private final JMap2D map;

    public AreaHandler(final JMap2D map) {
        this.map = map;
        mouseInputListener = new MouseListen();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final Component component) {
        mouseInputListener.install(component);
        map.addDecoration(0,deco);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final Component component) {
        mouseInputListener.uninstall(component);
        map.removeDecoration(deco);
    }

    private void updateGeometry(){
        final List<Geometry> geoms = new ArrayList<Geometry>();
        if(coords.size() == 1){
            //single point
            geoms.add(GEOMETRY_FACTORY.createPoint(coords.get(0)));
        }else if(coords.size() == 2){
            //line
            geoms.add(GEOMETRY_FACTORY.createLineString(coords.toArray(new Coordinate[coords.size()])));
        }else if(coords.size() > 2){
            //polygon
            final Coordinate[] ringCoords = coords.toArray(new Coordinate[coords.size()+1]);
            ringCoords[coords.size()] = coords.get(0);
            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(ringCoords);
            geoms.add(GEOMETRY_FACTORY.createPolygon(ring, new LinearRing[0]));
        }

        deco.setGeometries(geoms);
    }

    @Override
    public J2DCanvas getCanvas() {
        return map.getCanvas();
    }

    //---------------------PRIVATE CLASSES--------------------------------------
    private class MouseListen extends MouseNavigatonListener {

        MouseListen(){
            super(map);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {

            int mousebutton = e.getButton();
            if (mousebutton == MouseEvent.BUTTON1) {
                //add a coordinate
                final AffineTransform2D trs = map.getCanvas().getController().getTransform();
                try {
                    final AffineTransform dispToObj = trs.createInverse();
                    final double[] crds = new double[]{e.getX(),e.getY()};
                    dispToObj.transform(crds, 0, crds, 0, 1);
                    coords.add(new Coordinate(crds[0], crds[1]));
                    updateGeometry();
                } catch (NoninvertibleTransformException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }

            } else if (mousebutton == MouseEvent.BUTTON3) {
                //erase coordiantes
                coords.clear();
                updateGeometry();
            }

        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

    }
    
}
