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
package org.geotoolkit.gui.swing.go2.control.navigation;

import org.geotoolkit.gui.swing.go2.CanvasHandler;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.map.map2d.decoration.InformationDecoration.LEVEL;

/**
 * Abstract handler who handle several navigation methods and
 * a Zoom-Pan decoration.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractNavigationHandler implements CanvasHandler{

    protected final Map2D map;
    protected final ZoomDecoration decorationPane = new ZoomDecoration();

    public AbstractNavigationHandler(Map2D map) {
        this.map = map;
    }

    /**
     * Make a zoom on the map at the given point
     */
    protected void scale(final Point2D center, final double zoom){
        try {
            map.getCanvas().getController().scale(zoom, center);
        } catch (NoninvertibleTransformException ex) {
            map.getInformationDecoration().displayMessage(ex.getLocalizedMessage(), 3000, LEVEL.ERROR);
        }
    }

    /**
     * Zoom on the given rectangle coordinates.
     */
    protected void zoom(int startx,int starty, int endx, int endy){

        if(startx > endx){
            final int n = endx;
            endx = startx;
            startx = n;
        }
        if(starty > endy){
            final int n = endy;
            endy = starty;
            starty = n;
        }

        final Rectangle2D rect = new Rectangle(startx,starty,endx-startx,endy-starty);
        map.getCanvas().getController().setDisplayVisibleArea(rect);
    }

    /**
     * Draw a rectangle on the ZoomPan decoration.
     */
    protected void drawRectangle(final int startX, final int startY,
                                 final int lastX, final int lastY,
                                 final boolean view, final boolean fill) {
        final int left = Math.min(startX, lastX);
        final int right = Math.max(startX, lastX);
        final int top = Math.max(startY, lastY);
        final int bottom = Math.min(startY, lastY);
        final int width = right - left;
        final int height = top - bottom;
        decorationPane.setFill(fill);
        decorationPane.setCoord(left, bottom, width, height, view);
    }

    /**
     * Drag the map from coordinate 1 to coordinate 2.
     */
    protected void processDrag(int x1, int y1, int x2, int y2) {
        try {
            map.getCanvas().getController().translateDisplay(x2 - x1, y2 - y1);
        } catch (NoninvertibleTransformException ex) {
            map.getInformationDecoration().displayMessage(ex.getLocalizedMessage(), 3000, LEVEL.ERROR);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public J2DCanvas getCanvas() {
        return map.getCanvas();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        map.addDecoration(0,decorationPane);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        map.removeDecoration(decorationPane);
    }

}
