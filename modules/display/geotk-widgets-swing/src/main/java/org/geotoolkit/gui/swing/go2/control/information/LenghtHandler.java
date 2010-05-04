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
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.util.logging.Logging;

/**
 * Lenght mesure handler
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class LenghtHandler implements CanvasHandler {

    private static final Logger LOGGER = Logging.getLogger(LenghtHandler.class);

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    public static final List<Unit> UNITS = new ArrayList<Unit>();

    static{
        UNITS.add(SI.KILOMETRE);
        UNITS.add(SI.METRE);
        UNITS.add(NonSI.MILE);
        UNITS.add(NonSI.INCH);
    }

    private final MouseListen mouseInputListener;

    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private final LenghtDecoration deco = new LenghtDecoration();
    private final Map2D map;

    public LenghtHandler(Map2D map) {
        this.map = map;
        mouseInputListener = new MouseListen();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        mouseInputListener.install(component);
        map.addDecoration(0,deco);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        mouseInputListener.uninstall(component);
        map.removeDecoration(deco);
    }

    private void updateGeometry(){
        final List<Geometry> geoms = new ArrayList<Geometry>();
        if(coords.size() == 1){
            //single point
            geoms.add(GEOMETRY_FACTORY.createPoint(coords.get(0)));
        }else if(coords.size() > 1){
            //line
            geoms.add(GEOMETRY_FACTORY.createLineString(coords.toArray(new Coordinate[coords.size()])));
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
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            final int mousebutton = e.getButton();
            if (mousebutton == MouseEvent.BUTTON1) {
                //add a coordinate
                final AffineMatrix3 trs = map.getCanvas().getController().getTransform();
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
        public void mouseEntered(MouseEvent e) {
            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

    }
    
}
