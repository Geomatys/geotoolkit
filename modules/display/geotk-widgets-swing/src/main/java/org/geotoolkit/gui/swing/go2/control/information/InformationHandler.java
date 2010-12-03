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
package org.geotoolkit.gui.swing.go2.control.information;

import java.awt.Cursor;
import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.List;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.extractor.MapContextExtractor;
import org.geotoolkit.gui.swing.go2.control.navigation.AbstractNavigationHandler;

/**
 * Information handler.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class InformationHandler extends AbstractNavigationHandler {

    private final MouseListen mouseInputListener = new MouseListen();
    private final InformationDecoration infoPane = new InformationDecoration();
    private final MapContextExtractor extractor = new MapContextExtractor();
    private double zoomFactor = 2;

    public InformationHandler(JMap2D map) {
        super(map);
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        super.install(component);
        map.addDecoration(infoPane);
        component.addMouseListener(mouseInputListener);
        component.addMouseMotionListener(mouseInputListener);
        component.addMouseWheelListener(mouseInputListener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        super.uninstall(component);
        map.removeDecoration(infoPane);
        component.removeMouseListener(mouseInputListener);
        component.removeMouseMotionListener(mouseInputListener);
        component.removeMouseWheelListener(mouseInputListener);
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
        }

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;

            mousebutton = e.getButton();
            if (mousebutton == MouseEvent.BUTTON1) {
                System.out.println("Start search");
                final Area searchArea = new Area(new Rectangle(e.getPoint().x-2,e.getPoint().y-2,4,4));
                map.getCanvas().getGraphicsIn(searchArea, extractor, VisitFilter.INTERSECTS);
                final List<String> infos = extractor.getDescriptions();
                System.out.println("End search");

                if(!infos.isEmpty()){
                    infoPane.drawText(infos.toArray(new String[infos.size()]), e.getPoint());
                }else{
                    infoPane.drawText(null, null);
                }
                
            } else if (mousebutton == MouseEvent.BUTTON3) {
                decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();
            
            //right mouse button : pan action
            if (mousebutton == MouseEvent.BUTTON3) {
                decorationPane.setFill(false);
                decorationPane.setCoord(-10, -10,-10, -10, false);
                processDrag(startX, startY, endX, endY);
            }
            
            lastX = 0;
            lastY = 0;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map.getComponent().setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (mousebutton == MouseEvent.BUTTON3) {
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
