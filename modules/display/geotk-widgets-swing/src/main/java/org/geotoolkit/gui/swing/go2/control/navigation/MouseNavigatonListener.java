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
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.InformationDecoration.LEVEL;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

/**
 * Listener to handle mouse drag, move zoom.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class MouseNavigatonListener implements MouseInputListener, MouseWheelListener {

    
    private final ZoomDecoration decorationPane = new ZoomDecoration();
    private double zoomFactor = 2;
    private JMap2D map;

    private int startX;
    private int startY;
    private int lastX;
    private int lastY;
    private int mousebutton = 0;
    private int panButton = MouseEvent.BUTTON3;

    public MouseNavigatonListener(final JMap2D map) {
        this.map = map;
    }

    public void setMap(final JMap2D map) {
        this.map = map;
    }

    public JMap2D getMap() {
        return map;
    }

    public void setPanButton(final int panButton) {
        this.panButton = panButton;
    }

    public int getPanButton() {
        return panButton;
    }

    protected MapDecoration getDecoration(){
        return decorationPane;
    }

    public void install(final Component component){
        map.addDecoration(0,decorationPane);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
    }

    public void uninstall(final Component component){
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeMouseWheelListener(this);
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
    protected void processDrag(final int x1, final int y1, final int x2, final int y2) {
        try {
            map.getCanvas().getController().translateDisplay(x2 - x1, y2 - y1);
        } catch (NoninvertibleTransformException ex) {
            map.getInformationDecoration().displayMessage(ex.getLocalizedMessage(), 3000, LEVEL.ERROR);
        }
    }




    @Override
    public void mouseClicked(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = startX;
        lastY = startY;


    }

    @Override
    public void mousePressed(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = 0;
        lastY = 0;

        mousebutton = e.getButton();
        if (mousebutton == panButton) {
            //pan action on right mouse button
            decorationPane.setBuffer(map.getCanvas().getSnapShot());
            decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
        }

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        int endX = e.getX();
        int endY = e.getY();

        decorationPane.setBuffer(null);

        if (mousebutton == panButton) {
            //right mouse button : pan action
            decorationPane.setFill(false);
            decorationPane.setCoord(-10, -10,-10, -10, false);
            processDrag(startX, startY, endX, endY);
        }

        lastX = 0;
        lastY = 0;
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        decorationPane.setFill(false);
        decorationPane.setCoord(-10, -10,-10, -10, true);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (mousebutton == panButton) {
            if ((lastX > 0) && (lastY > 0)) {
                int dx = lastX - startX;
                int dy = lastY - startY;
                decorationPane.setFill(false);
                decorationPane.setCoord(dx, dy, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }
            lastX = x;
            lastY = y;

        }else{
            lastX = x;
            lastY = y;
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        Rectangle rect = new Rectangle(e.getPoint());
        rect.x -= 10;
        rect.y -= 10;
        rect.width = 10;
        rect.height = 10;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        int rotate = e.getWheelRotation();

        if (rotate < 0) {
            scale(e.getPoint(), zoomFactor);
        } else if (rotate > 0) {
            scale(e.getPoint(), 1d / zoomFactor);
        }
    }
}
