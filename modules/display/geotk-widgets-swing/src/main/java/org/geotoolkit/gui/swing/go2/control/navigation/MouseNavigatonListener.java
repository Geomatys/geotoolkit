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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.MouseInputListener;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.InformationDecoration.LEVEL;

/**
 * Listener to handle mouse drag, move zoom.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class MouseNavigatonListener implements MouseInputListener, MouseWheelListener {

    
    private final ZoomDecoration decorationPane = new ZoomDecoration();
    private double zoomFactor = 2;
    private Map2D map;

    private int startX;
    private int startY;
    private int lastX;
    private int lastY;
    private int mousebutton = 0;

    public MouseNavigatonListener(Map2D map) {
        this.map = map;
    }

    public void setMap(Map2D map) {
        this.map = map;
    }

    public Map2D getMap() {
        return map;
    }

    public void install(Component component){
        map.addDecoration(0,decorationPane);
        map.getComponent().addMouseListener(this);
        map.getComponent().addMouseMotionListener(this);
        map.getComponent().addMouseWheelListener(this);
    }

    public void uninstall(Component component){
        map.getComponent().removeMouseListener(this);
        map.getComponent().removeMouseMotionListener(this);
        map.getComponent().removeMouseWheelListener(this);
        map.removeDecoration(decorationPane);
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




    @Override
    public void mouseClicked(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = startX;
        lastY = startY;


    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = 0;
        lastY = 0;

        mousebutton = e.getButton();
        if (mousebutton == MouseEvent.BUTTON1) {
        } else if (mousebutton == MouseEvent.BUTTON3) {
            //pan action on right mouse button
            decorationPane.setBuffer(map.getCanvas().getSnapShot());
            decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int endX = e.getX();
        int endY = e.getY();

        decorationPane.setBuffer(null);

        if (mousebutton == MouseEvent.BUTTON1) {
        } else if (mousebutton == MouseEvent.BUTTON3) {
            //right mouse button : pan action
            decorationPane.setFill(false);
            decorationPane.setCoord(-5, -5, -4, -4, false);
            processDrag(startX, startY, endX, endY);
        }

        lastX = 0;
        lastY = 0;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        decorationPane.setFill(false);
        decorationPane.setCoord(-5, -5, -4, -4, true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();


        // left mouse button
        if (mousebutton == MouseEvent.BUTTON1) {

            lastX = x;
            lastY = y;

        } //right mouse button : pan action
        else if (mousebutton == MouseEvent.BUTTON3) {
            if ((lastX > 0) && (lastY > 0)) {
                int dx = lastX - startX;
                int dy = lastY - startY;
                decorationPane.setFill(false);
                decorationPane.setCoord(dx, dy, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }
            lastX = x;
            lastY = y;

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Rectangle rect = new Rectangle(e.getPoint());
        rect.x -= 10;
        rect.y -= 10;
        rect.width = 10;
        rect.height = 10;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotate = e.getWheelRotation();

        if (rotate < 0) {
            scale(e.getPoint(), zoomFactor);
        } else if (rotate > 0) {
            scale(e.getPoint(), 1d / zoomFactor);
        }
    }
}
