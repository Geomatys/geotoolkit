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
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 * Panoramic handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class PanHandler extends AbstractNavigationHandler {

    private final Cursor CUR_ZOOM_PAN;
    private final MouseListen mouseInputListener = new MouseListen();
    private double zoomFactor = 2;

    public PanHandler(final JMap2D map) {
        super(map);
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final ImageIcon ico_zoomPan = IconBundle.getIcon("16_zoom_pan");
        final BufferedImage img3 = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        img3.getGraphics().drawImage(ico_zoomPan.getImage(), 0, 0, null);
        CUR_ZOOM_PAN = tk.createCustomCursor(img3, new Point(1, 1), "in");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final Component component) {
        super.install(component);
        component.addMouseListener(mouseInputListener);
        component.addMouseMotionListener(mouseInputListener);
        component.addMouseWheelListener(mouseInputListener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final Component component) {
        super.uninstall(component);
        component.removeMouseListener(mouseInputListener);
        component.removeMouseMotionListener(mouseInputListener);
        component.removeMouseWheelListener(mouseInputListener);
    }
    
    //---------------------PRIVATE CLASSES--------------------------------------
    private class MouseListen implements MouseInputListener, MouseWheelListener {

        private int startX;
        private int startY;
        private int lastX;
        private int lastY;
        private int mousebutton = 0;

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

            decorationPane.setBuffer(map.getCanvas().getSnapShot());
            mousebutton = e.getButton();
            decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();

            decorationPane.setBuffer(null);

            if (mousebutton == MouseEvent.BUTTON1) {
                decorationPane.setFill(false);
                decorationPane.setCoord(-10, -10,-10, -10, false);
                processDrag(startX, startY, endX, endY);

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
        public void mouseEntered(final MouseEvent e) {
            map.getComponent().setCursor(CUR_ZOOM_PAN);
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

            if ((lastX > 0) && (lastY > 0)) {
                int dx = lastX - startX;
                int dy = lastY - startY;
                decorationPane.setFill(true);
                decorationPane.setCoord(dx, dy, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }

            lastX = x;
            lastY = y;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            int rotate = e.getWheelRotation();

            if(rotate<0){
                scale(e.getPoint(),zoomFactor);
            }else if(rotate>0){
                scale(e.getPoint(),1d/zoomFactor);
            }
        }
    }
    
}
