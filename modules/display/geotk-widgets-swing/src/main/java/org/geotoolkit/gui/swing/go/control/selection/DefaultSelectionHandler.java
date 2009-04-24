/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control.selection;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Default selection handler
 * 
 * @author Johann Sorel
 */
public class DefaultSelectionHandler implements SelectionHandler {

    
    private static final ImageIcon ICON = IconBundle.getInstance().getIcon("16_select_default");
    private static final String title = ResourceBundle.getBundle("org/geotools/gui/swing/map/map2d/handler/Bundle").getString("default");
    
    protected final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private final MouseInputListener mouseInputListener = new MouseListen();
    private final DefaultSelectionDecoration selectionPane = new DefaultSelectionDecoration();
    private GoMap2D map2D = null;
    private boolean installed = false;
    protected Cursor CUR_SELECT;

    public DefaultSelectionHandler() {
        buildCursors();
    }

    private void buildCursors() {
//        Toolkit tk = Toolkit.getDefaultToolkit();
//        ImageIcon ico_select = IconBundle.getResource().getIcon("16_select");
//
//        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
//        img.getGraphics().drawImage(ico_select.getImage(), 0, 0, null);
//        CUR_SELECT = tk.createCustomCursor(img, new java.awt.Point(1, 1), "select");

    }

    private void doMouseSelection(int mx, int my) {

        Geometry geometry = mousePositionToGeometry(mx, my);
        if (geometry != null) {
//            map2D.doSelection(geometry);
        }
    }

    /**
     *  transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    private Geometry mousePositionToGeometry(int mx, int my) {
        return null;
//        Coordinate[] coord = new Coordinate[5];
//        int taille = 4;
//
//        StreamingStrategy strategy = map2D.getRenderingStrategy();
//        coord[0] = strategy.toMapCoord(mx - taille, my - taille);
//        coord[1] = strategy.toMapCoord(mx - taille, my + taille);
//        coord[2] = strategy.toMapCoord(mx + taille, my + taille);
//        coord[3] = strategy.toMapCoord(mx + taille, my - taille);
//        coord[4] = coord[0];
//
//        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    private void doMouseSelection(int mx, int my, int ex, int ey) {
//        Coordinate[] coord = new Coordinate[5];
//
//        StreamingStrategy strategy = map2D.getRenderingStrategy();
//        coord[0] = strategy.toMapCoord(mx, my);
//        coord[1] = strategy.toMapCoord(mx, ey);
//        coord[2] = strategy.toMapCoord(ex, ey);
//        coord[3] = strategy.toMapCoord(ex, my);
//        coord[4] = coord[0];
//
//        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//        Geometry geometry = GEOMETRY_FACTORY.createPolygon(lr1, null);
//
//        map2D.doSelection(geometry);
    }

    public void install(GoMap2D map) {
//        installed = true;
//        map2D = map;
//        map2D.addDecoration(selectionPane);
//        map2D.getComponent().addMouseListener(mouseInputListener);
//        map2D.getComponent().addMouseMotionListener(mouseInputListener);
    }

    public void uninstall() {
        map2D.removeDecoration(selectionPane);
        map2D.getComponent().removeMouseListener(mouseInputListener);
        map2D.getComponent().removeMouseMotionListener(mouseInputListener);
        map2D = null;
        installed = false;
    }
    
    public boolean isInstalled() {
        return installed;
    }
    

    private class MouseListen implements MouseInputListener {

        int startX = 0;
        int startY = 0;
        int lastX = 0;
        int lastY = 0;

        private void drawRectangle(boolean view, boolean fill) {
            int left = Math.min(startX, lastX);
            int right = Math.max(startX, lastX);
            int top = Math.max(startY, lastY);
            int bottom = Math.min(startY, lastY);
            int width = right - left;
            int height = top - bottom;
            selectionPane.setFill(fill);
            selectionPane.setCoord(left, bottom, width, height, view);
        //graphics.drawRect(left, bottom, width, height);
        }

        public void mouseClicked(MouseEvent e) {
            doMouseSelection(e.getX(), e.getY());
        }

        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;

        }

        public void mouseReleased(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
            drawRectangle(false, true);
            doMouseSelection(startX, startY, lastX, lastY);
        }

        public void mouseEntered(MouseEvent e) {
            map2D.getComponent().setCursor(CUR_SELECT);

        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
            drawRectangle(true, true);
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    public String getTitle() {
        return title;
    }

    public ImageIcon getIcon() {
        return ICON;
    }

    
}
