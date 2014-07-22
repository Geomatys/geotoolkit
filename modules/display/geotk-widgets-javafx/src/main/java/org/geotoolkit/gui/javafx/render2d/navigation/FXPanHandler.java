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
package org.geotoolkit.gui.javafx.render2d.navigation;

import org.geotoolkit.gui.javafx.render2d.FXAbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;

/**
 * Panoramic handler
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXPanHandler extends FXAbstractNavigationHandler {

    //we could use this cursor, but java do not handle translucent cursor correctly on every platform
    private static final Cursor CUR_ZOOM_PAN = Cursor.MOVE;
    private final MouseListen mouseInputListener = new MouseListen();
    private final double zoomFactor = 2;
    private final boolean infoOnRightClick;
    
    
    public FXPanHandler(final FXMap map, boolean infoOnRightClick) {
        super(map);
        this.infoOnRightClick= infoOnRightClick;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final FXMap component) {
        super.install(component);
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        map.setCursor(CUR_ZOOM_PAN);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
//        map.setCursor(null);
    }
    
    //---------------------PRIVATE CLASSES--------------------------------------
    private class MouseListen extends AbstractMouseHandler {

        private double startX;
        private double startY;
        private double lastX;
        private double lastY;
        private MouseButton mousebutton = null;
        
        @Override
        public void mouseClicked(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = startX;
            lastY = startY;
            
//            if (infoOnRightClick && MouseButton.SECONDARY == e.getButton()) {
//                final Area searchArea = new Area(new Rectangle2D.Double(startX - 2, startY - 2, 4, 4));
//                final InformationVisitor visitor = new InformationVisitor();
//                map.getCanvas().getGraphicsIn(searchArea, visitor, VisitFilter.INTERSECTS);
//
//                if (!visitor.graphics.isEmpty()) {
////                    final JInformationDialog dialog = new JInformationDialog(map);
////                    dialog.display(visitor.graphics, presenter, e.getLocationOnScreen(), visitor.ctx, visitor.area);
//                }
//            }
            
        }
        
        @Override
        public void mousePressed(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;
            mousebutton = e.getButton();

            if(!isStateFull()){
                decorationPane.setBuffer(map.getCanvas().getSnapShot());
                decorationPane.setCoord(0, 0, map.getWidth(), map.getHeight(), true);
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            double endX = e.getX();
            double endY = e.getY();

            if(!isStateFull()){
                decorationPane.setBuffer(null);

                if (mousebutton == MouseButton.PRIMARY) {
                    decorationPane.setFill(false);
                    decorationPane.setCoord(-10, -10,-10, -10, false);
                    processDrag(startX, startY, endX, endY);

                } //right mouse button : pan action
                else if (mousebutton == MouseButton.SECONDARY) {
                    decorationPane.setFill(false);
                    decorationPane.setCoord(-10, -10,-10, -10, false);
                    processDrag(startX, startY, endX, endY);
                }
            }

            lastX = 0;
            lastY = 0;
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            decorationPane.setFill(false);
            decorationPane.setCoord(-10, -10,-10, -10, true);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            double x = e.getX();
            double y = e.getY();

            if ((lastX > 0) && (lastY > 0)) {
                double dx = lastX - startX;
                double dy = lastY - startY;
                
                if(isStateFull()){
                    if (mousebutton == MouseButton.PRIMARY) {
                        processDrag(lastX, lastY, x, y);

                    } //right mouse button : pan action
                    else if (mousebutton == MouseButton.SECONDARY) {
                        processDrag(lastX, lastY, x, y);
                    }
                }else{
                    decorationPane.setFill(true);
                    decorationPane.setCoord(dx, dy, map.getWidth()+dx, map.getHeight()+dy, true);
                }
            }

            lastX = x;
            lastY = y;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = startX;
            lastY = startY;
        }
        
        @Override
        public void mouseWheelMoved(final ScrollEvent e) {
            double rotate = -e.getDeltaY();

            if(rotate<0){
                scale(new Point2D.Double(startX, startY),zoomFactor);
            }else if(rotate>0){
                scale(new Point2D.Double(startX, startY),1d/zoomFactor);
            }
        }

    }
    
    
    private static class InformationVisitor implements GraphicVisitor {

        private final List<org.opengis.display.primitive.Graphic> graphics = new ArrayList<>();
        private RenderingContext2D ctx = null;
        private SearchAreaJ2D area = null;

        @Override
        public void startVisit() {
        }

        @Override
        public void endVisit() {
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }

        @Override
        public void visit(org.opengis.display.primitive.Graphic graphic, RenderingContext context, SearchArea area) {
            this.graphics.add(graphic);
            this.ctx = (RenderingContext2D) context;
            this.area = (SearchAreaJ2D) area;
        }
    }
    
    
}
