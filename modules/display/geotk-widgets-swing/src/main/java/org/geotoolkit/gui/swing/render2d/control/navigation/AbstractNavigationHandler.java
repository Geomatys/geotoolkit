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
package org.geotoolkit.gui.swing.render2d.control.navigation;

import java.awt.Color;
import org.geotoolkit.gui.swing.render2d.CanvasHandler;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.J2DCanvasSwing;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.decoration.InformationDecoration.LEVEL;

/**
 * Abstract handler who handle several navigation methods and
 * a Zoom-Pan decoration.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class AbstractNavigationHandler implements CanvasHandler{

    protected final JMap2D map;
    protected final ZoomDecoration decorationPane = new ZoomDecoration();

    public AbstractNavigationHandler(final JMap2D map) {
        this.map = map;
    }

    protected boolean isStateFull(){
        if(map == null) return false;
        return map.getCanvas() instanceof J2DCanvasSwing;
    }

    /**
     * Make a zoom on the map at the given point
     */
    protected void scale(final Point2D center, final double zoom){
        try {
            map.getCanvas().scale(zoom, center);
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
        map.getCanvas().setDisplayVisibleArea(rect);
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
    protected void processDrag(final int x1, final int y1, final int x2, final int y2) {
        try {
            map.getCanvas().translateDisplay(x2 - x1, y2 - y1);
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
    public void install(final Component component) {
        map.addDecoration(0,decorationPane);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final Component component) {
        map.removeDecoration(decorationPane);
    }

    public static Cursor cleanCursor(Image icon, Point focusPoint, String cursorname){
        final int width = icon.getWidth(null);
        final int height = icon.getHeight(null);

        final BufferedImage bufferARGB = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = bufferARGB.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.drawImage(icon, new AffineTransform(), null);

          for (int y = 0 ; y < height ; y++) {
            for (int x = 0 ; x < width ; x++) {

                int rgb = bufferARGB.getRGB(x, y);

                int blue = rgb & 0xff;
                int green = (rgb & 0xff00) >> 8;
                int red = (rgb & 0xff0000) >> 16;
                //int alpha = (rgb & 0xff000000) >> 24;

                if (red == 255 && green == 255 && blue == 255) {
                    // make white transparent
                    bufferARGB.setRGB(x, y, 0);
                }

            }
        }

        return Toolkit.getDefaultToolkit().createCustomCursor(bufferARGB,focusPoint,cursorname);
    }

}
