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
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.go2.Map2D;

/**
 * Zoom in Handler for GoMap2D.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class ZoomInHandler extends AbstractNavigationHandler {

    private final Cursor CUR_ZOOM_IN;
    private final MouseListen mouseInputListener = new MouseListen();
    private double zoomFactor = 2;

    public ZoomInHandler(Map2D map) {
        super(map);
        
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final ImageIcon ico_zoomIn = IconBundle.getInstance().getIcon("16_zoom_in");
        final BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(ico_zoomIn.getImage(), 0, 0, null);
        CUR_ZOOM_IN = tk.createCustomCursor(img, new Point(1, 1), "in");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        super.install(component);
        map.getComponent().addMouseListener(mouseInputListener);
        map.getComponent().addMouseMotionListener(mouseInputListener);
        map.getComponent().addMouseWheelListener(mouseInputListener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        super.uninstall(component);
        map.getComponent().removeMouseListener(mouseInputListener);
        map.getComponent().removeMouseMotionListener(mouseInputListener);
        map.getComponent().removeMouseWheelListener(mouseInputListener);
    }

    private class MouseListen implements MouseInputListener, MouseWheelListener {

        private int startX;
        private int startY;
        private int lastX;
        private int lastY;
        private int mousebutton = 0;

        @Override
        public void mouseClicked(MouseEvent e) {

            mousebutton = e.getButton();

            // left mouse button
            if (e.getButton() == MouseEvent.BUTTON1) {
                scale(e.getPoint(), zoomFactor);
            }

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

                if(startX != endX && startY != endY){
                    zoom(startX,startY,endX,endY);
                }

                decorationPane.setBuffer(null);
                decorationPane.setFill(false);
                decorationPane.setCoord(-10, -10,-10, -10, false);
                
//                int width = map.getComponent().getWidth() / 2;
//                int height = map.getComponent().getHeight() / 2;
//                int left = e.getX() - (width / 2);
//                int bottom = e.getY() - (height / 2);
//                decorationPane.setFill(false);
//                decorationPane.setCoord(left, bottom, width, height, true);

            } //right mouse button : pan action
            else if (mousebutton == MouseEvent.BUTTON3) {
                decorationPane.setFill(false);
                decorationPane.setCoord(-10, -10,-10, -10, false);
                processDrag(startX, startY, endX, endY);
            }

            lastX = 0;
            lastY = 0;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map.getComponent().setCursor(CUR_ZOOM_IN);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            decorationPane.setFill(false);
            decorationPane.setCoord(-10, -10,-10, -10, true);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();


            // left mouse button
            if (mousebutton == MouseEvent.BUTTON1) {

                if ((lastX > 0) && (lastY > 0)) {
                    drawRectangle(startX,startY,lastX,lastY,true, true);
                }

                // draw new box
                lastX = x;
                lastY = y;
                drawRectangle(startX,startY,lastX,lastY,true, true);

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

//            int width = map.getComponent().getWidth() / 2;
//            int height = map.getComponent().getHeight() / 2;
//
//            int left = e.getX() - (width / 2);
//            int bottom = e.getY() - (height / 2);
//
//            decorationPane.setFill(false);
//            decorationPane.setCoord(left, bottom, width, height, true);

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int rotate = e.getWheelRotation();

            if(rotate<0){
                scale(e.getPoint(),zoomFactor);
            }else if(rotate>0){
                scale(e.getPoint(),1d/zoomFactor);
            }

        }
    }

}
