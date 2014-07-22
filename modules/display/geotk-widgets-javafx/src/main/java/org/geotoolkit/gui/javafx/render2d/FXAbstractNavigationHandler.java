/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.render2d;

import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomDecoration;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.geotoolkit.display2d.canvas.J2DCanvas;


/**
 * Abstract handler who handle several navigation methods and
 * a Zoom-Pan decoration.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXAbstractNavigationHandler implements FXCanvasHandler{

    protected final FXMap map;
    protected final FXZoomDecoration decorationPane = new FXZoomDecoration();

    public FXAbstractNavigationHandler(final FXMap map) {
        this.map = map;
    }

    protected boolean isStateFull(){
        if(map == null) return false;
        return map.isStatefull();
    }

    /**
     * Make a zoom on the map at the given point
     */
    protected void scale(final Point2D center, final double zoom){
        try {
            map.getCanvas().scale(zoom, center);
        } catch (NoninvertibleTransformException ex) {
            map.getInformationDecoration().displayMessage(ex.getLocalizedMessage(), 3000, FXInformationDecoration.LEVEL.ERROR);
        }
    }

    /**
     * Zoom on the given rectangle coordinates.
     */
    protected void zoom(double startx, double starty, double endx, double endy){

        if(startx > endx){
            final double n = endx;
            endx = startx;
            startx = n;
        }
        if(starty > endy){
            final double n = endy;
            endy = starty;
            starty = n;
        }

        final Rectangle2D rect = new Rectangle2D.Double(startx,starty,endx-startx,endy-starty);
        map.getCanvas().setDisplayVisibleArea(rect);
    }

    /**
     * Draw a rectangle on the ZoomPan decoration.
     */
    protected void drawRectangle(final double startX, final double startY,
                                 final double lastX, final double lastY,
                                 final boolean view, final boolean fill) {
        decorationPane.setFill(fill);
        decorationPane.setCoord(startX, startY, lastX, lastY, view);
    }

    /**
     * Drag the map from coordinate 1 to coordinate 2.
     */
    protected void processDrag(final double x1, final double y1, final double x2, final double y2) {
        try {
            map.getCanvas().translateDisplay(x2 - x1, y2 - y1);
        } catch (NoninvertibleTransformException ex) {
            map.getInformationDecoration().displayMessage(ex.getLocalizedMessage(), 3000, FXInformationDecoration.LEVEL.ERROR);
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
    public void install(final FXMap component) {
        map.addDecoration(0,decorationPane);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final FXMap component) {
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
