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
package org.geotoolkit.gui.javafx.render2d.navigation;


import org.geotoolkit.gui.javafx.render2d.FXAbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import java.awt.geom.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Zoom in Handler for GoMap2D.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class FXZoomOutHandler extends FXAbstractNavigationHandler {

    //we could use this cursor, but java do not handle translucent cursor correctly on every platform
    //private static final Cursor CUR_ZOOM_OUT = cleanCursor(ZoomOutAction.ICON.getImage(),new Point(0,0),"zoomout");
    private static  final Cursor CUR_ZOOM_OUT = Cursor.CROSSHAIR;
    private final MouseListen mouseInputListener = new MouseListen();
    private double zoomFactor = 2;

    public FXZoomOutHandler(final FXMap map) {
        super(map);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final FXMap component) {
        super.install(component);
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        map.setCursor(CUR_ZOOM_OUT);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
        map.setCursor(null);
    }

    private class MouseListen extends AbstractMouseHandler {

        private double startX;
        private double startY;
        private MouseButton mousebutton;

        @Override
        public void mouseClicked(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            mousebutton = e.getButton();

            // left mouse button
            if (mousebutton == MouseButton.PRIMARY) {
                scale(new Point2D.Double(startX, startY), 1d/zoomFactor);
            }

        }

        @Override
        public void mousePressed(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();

            mousebutton = e.getButton();
            if (mousebutton == MouseButton.SECONDARY) {
                if(!isStateFull()){
                    decorationPane.setBuffer(map.getCanvas().getSnapShot());
                    decorationPane.setCoord(0, 0, map.getWidth(), map.getHeight(), true);
                }
            }

        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            double endX = e.getX();
            double endY = e.getY();

            decorationPane.setBuffer(null);
            
            //right mouse button : pan action
            if (mousebutton == MouseButton.SECONDARY) {
                
                if(!isStateFull()){
                    decorationPane.setBuffer(null);
                    decorationPane.setFill(false);
                    decorationPane.setCoord(-10, -10,-10, -10, false);
                    processDrag(startX, startY, endX, endY);
                }
            }
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            decorationPane.setFill(false);
            decorationPane.setCoord(-10, -10,-10, -10, true);
        }

        @Override
        public void mouseMoved(final MouseEvent e){
            startX = e.getX();
            startY = e.getY();
        }
        
        @Override
        public void mouseWheelMoved(final ScrollEvent e) {
            final double rotate = -e.getDeltaY();

            if(rotate<0){
                scale(new Point2D.Double(startX, startY),zoomFactor);
            }else if(rotate>0){
                scale(new Point2D.Double(startX, startY),1d/zoomFactor);
            }

        }
    }

}
