/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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

import org.geotoolkit.gui.swing.go2.control.information.presenter.InformationPresenter;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.information.presenter.DefaultInformationPresenter;
import org.geotoolkit.gui.swing.go2.control.navigation.AbstractNavigationHandler;

import org.opengis.display.primitive.Graphic;

/**
 * Information handler.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class InformationHandler extends AbstractNavigationHandler {

    private final MouseListen mouseInputListener = new MouseListen();
    private final InformationDecoration infoPane = new InformationDecoration();
    private InformationPresenter presenter = new DefaultInformationPresenter();
    private double zoomFactor = 2;

    public InformationHandler(final JMap2D map) {
        super(map);
    }

    public InformationPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(final InformationPresenter presenter) {
        this.presenter = presenter;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final Component component) {
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
    public void uninstall(final Component component) {
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
        public void mouseClicked(final MouseEvent e) {
            mousebutton = e.getButton();
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;

            mousebutton = e.getButton();
            if (mousebutton == MouseEvent.BUTTON1) {
                final Area searchArea = new Area(new Rectangle(e.getPoint().x-2,e.getPoint().y-2,4,4));
                final InformationVisitor visitor = new InformationVisitor();
                map.getCanvas().getGraphicsIn(searchArea, visitor, VisitFilter.INTERSECTS);

                if(!visitor.graphics.isEmpty()){
                    infoPane.display(visitor.graphics, presenter, e.getPoint(), visitor.ctx, visitor.area);
                }else{
                    infoPane.display(null, null, null, null,null);
                }
                
            } else if (mousebutton == MouseEvent.BUTTON3) {
                decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }
            
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
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
        public void mouseEntered(final MouseEvent e) {
            map.getComponent().setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseExited(final MouseEvent e) {}

        @Override
        public void mouseDragged(final MouseEvent e) {
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


    private static class InformationVisitor implements GraphicVisitor{

        private final List<Graphic> graphics = new ArrayList<Graphic>();
        private RenderingContext2D ctx = null;
        private SearchAreaJ2D area = null;

        @Override
        public void startVisit() {
        }

        @Override
        public void endVisit() {
        }

        @Override
        public void visit(final Graphic graphic, final RenderingContext context, final SearchArea area) {
            this.graphics.add(graphic);
            this.ctx = (RenderingContext2D) context;
            this.area = (SearchAreaJ2D) area;
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }

    }
}
