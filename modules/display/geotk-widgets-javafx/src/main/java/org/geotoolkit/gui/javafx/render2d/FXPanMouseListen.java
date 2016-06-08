/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import java.awt.geom.Point2D;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.javafx.render2d.navigation.AbstractMouseHandler;

/**
 * Mouse handler which allows to move on map using drag and drop.
 * Also contains processes for zoom on mouse wheel.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class FXPanMouseListen extends AbstractMouseHandler {

    private static final double DEFAULT_ZOOM_FACTOR = 2;

    private double startX;
    private double startY;
    private double lastX;
    private double lastY;
    
    protected MouseButton mousebutton = null;

    protected final AbstractNavigationHandler owner;
    private final double zoomFactor;

    public FXPanMouseListen(final AbstractNavigationHandler owner) {
        this(owner, DEFAULT_ZOOM_FACTOR);
    }

    public FXPanMouseListen(final AbstractNavigationHandler owner, final double zoomFactor) {
        ArgumentChecks.ensureNonNull("Parent map handler", owner);
        this.owner = owner;
        if (zoomFactor >= 0 && zoomFactor <= Double.MAX_VALUE) {
            this.zoomFactor = zoomFactor;
        } else {
            this.zoomFactor = DEFAULT_ZOOM_FACTOR;
        }
    }

    public void handle(Event event) {
        super.handle(event);
        if(event instanceof KeyEvent){
            final KeyEvent ke = (KeyEvent) event;
            if(KeyEvent.KEY_PRESSED.equals(ke.getEventType())){
                keyPressed(ke);
            }else if(KeyEvent.KEY_RELEASED.equals(ke.getEventType())){
                keyReleased(ke);
            }else if(KeyEvent.KEY_TYPED.equals(ke.getEventType())){
                keyTyped(ke);
            }
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = 0;
        lastY = 0;
        mousebutton = e.getButton();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = 0;
        lastY = 0;
        mousebutton = e.getButton();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        double endX = e.getX();
        double endY = e.getY();

        if (!owner.isStateFull()) {

            if ((lastX != 0 || lastY != 0) && 
                    (mousebutton == MouseButton.PRIMARY || mousebutton == MouseButton.SECONDARY)) {
                owner.processDrag(startX, startY, endX, endY);
            }
            
            // Release decoration only when drag is finished, so user will still 
            // see part of the map during repaint.
            owner.decorationPane.setFill(false);
            owner.decorationPane.setCoord(-10, -10, -10, -10, false);
            owner.decorationPane.setBuffer(null);
        }

        lastX = 0;
        lastY = 0;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        owner.decorationPane.setFill(false);
        owner.decorationPane.setCoord(-10, -10, -10, -10, true);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if ((lastX != 0) || (lastY != 0)) {
            
            double dx = lastX - startX;
            double dy = lastY - startY;

            if (owner.isStateFull()) {
                if (mousebutton == MouseButton.PRIMARY || mousebutton == MouseButton.SECONDARY) {
                    owner.processDrag(lastX, lastY, x, y);
                }
            } else {
                if (owner.decorationPane.getBuffer() == null) {
                    owner.decorationPane.setBuffer(owner.map.getCanvas().getSnapShot());
                }                
                owner.decorationPane.setFill(true);
                owner.decorationPane.setCoord(dx, dy, owner.map.getWidth() + dx, owner.map.getHeight() + dy, true);
            }
        }

        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseWheelMoved(final ScrollEvent e) {
        startX = e.getX();
        startY = e.getY();
        double rotate = -e.getDeltaY();
        if (rotate < 0) {
            owner.scale(new Point2D.Double(startX, startY), zoomFactor);
        } else if (rotate > 0) {
            owner.scale(new Point2D.Double(startX, startY), 1d / zoomFactor);
        }
    }

    public void keyPressed(final KeyEvent e) {

    }

    public void keyTyped(final KeyEvent e) {

    }

    public void keyReleased(final KeyEvent e) {

    }

}
