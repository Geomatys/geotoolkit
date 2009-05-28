/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control.selection;

import com.vividsolutions.jts.geom.GeometryFactory;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.gui.swing.go.CanvasHandler;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

/**
 * Selection handler
 * 
 * @author Johann Sorel
 */
public class DefaultSelectionHandler implements CanvasHandler {

    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    
    
    private final MouseInputListener mouseInputListener;
    private final DefaultSelectionDecoration selectionPane = new DefaultSelectionDecoration();
    private final GraphicVisitor visitor = new AbstractGraphicVisitor() {

        private final Map<MapLayer,Set<FeatureId>> selection = new HashMap<MapLayer, Set<FeatureId>>();

        @Override
        public void startVisit() {
            super.startVisit();
            selection.clear();
        }

        @Override
        public void endVisit() {
            super.endVisit();

            for(final MapLayer layer : selection.keySet()){
                Filter f = layer.getSelectionFilter();
                f = FF.id(selection.get(layer));

                layer.setSelectionFilter(f);
            }

            selection.clear();
        }

        @Override
        public void visit(ProjectedFeature feature, Shape queryArea) {

            final FeatureMapLayer layer = feature.getSource();
            Set<FeatureId> ids = selection.get(layer);

            if(ids == null){
                ids = new HashSet<FeatureId>();
                selection.put(layer, ids);
            }

            ids.add(feature.getFeatureId());
        }

        @Override
        public void visit(GraphicCoverageJ2D coverage, Shape queryArea) {
        }
    };
    private boolean squareArea;
    private boolean withinArea;
    private boolean geographicArea;
    private GoMap2D map2D;


    public DefaultSelectionHandler() {
        mouseInputListener = new MouseListen();
    }

    public boolean isGeographicArea() {
        return geographicArea;
    }

    public boolean isSquareArea() {
        return squareArea;
    }

    public boolean isWithinArea() {
        return withinArea;
    }

    public void setGeographicArea(boolean geographicArea) {
        this.geographicArea = geographicArea;
    }

    public void setSquareArea(boolean squareArea) {
        this.squareArea = squareArea;
    }

    public void setWithinArea(boolean withinArea) {
        this.withinArea = withinArea;
    }

    public GoMap2D getMap() {
        return map2D;
    }

    public void setMap(GoMap2D map2D) {
        this.map2D = map2D;
    }
    
    private void doSelection(List<Point> points) {

        if (points.size() > 2) {

            final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(points.get(0).x, points.get(0).y);


            for(int i=1;i<points.size();i++){
                Point p = points.get(i);
                path.lineTo(p.x, p.y);
            }

            map2D.getCanvas().getGraphicsIn(path, visitor, (withinArea) ? VisitFilter.WITHIN : VisitFilter.INTERSECTS);
            map2D.getCanvas().getController().repaint();
        }

    }

    @Override
    public ReferencedCanvas2D getCanvas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void install(Component component) {
        map2D.addDecoration(selectionPane);
        map2D.getComponent().addMouseListener(mouseInputListener);
        map2D.getComponent().addMouseMotionListener(mouseInputListener);
    }

    @Override
    public void uninstall(Component component) {
        map2D.removeDecoration(selectionPane);
        map2D.getComponent().removeMouseListener(mouseInputListener);
        map2D.getComponent().removeMouseMotionListener(mouseInputListener);
    }

    private class MouseListen implements MouseInputListener {

        Point lastValid = null;
        final List<Point> points = new ArrayList<Point>();
        private int startX = 0;
        private int startY = 0;

        @Override
        public void mouseClicked(MouseEvent e) {
            final Point point = e.getPoint();
            points.clear();
            points.add(new Point(point.x-1, point.y-1));
            points.add(new Point(point.x-1, point.y+1));
            points.add(new Point(point.x+1, point.y+1));
            points.add(new Point(point.x+1, point.y-1));
            doSelection(points);
            points.clear();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            lastValid = e.getPoint();
            points.clear();
            if(squareArea){
                startX = lastValid.x;
                startY = lastValid.y;
            }else{
                points.add(lastValid);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            final Point lastPoint = e.getPoint();
            if(squareArea){
                points.clear();
                points.add(lastPoint);
                points.add(new Point(lastPoint.x, startY));
                points.add(new Point(startX, startY));
                points.add(new Point(startX, lastPoint.y));
            }else{
                points.add(lastPoint);
            }
            doSelection(points);
            selectionPane.setPoints(null);
            points.clear();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map2D.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point eventPoint = e.getPoint();
            if(squareArea){
                points.clear();
                points.add(eventPoint);
                points.add(new Point(eventPoint.x, startY));
                points.add(new Point(startX, startY));
                points.add(new Point(startX, eventPoint.y));
                points.add(eventPoint);
                selectionPane.setPoints(new ArrayList<Point>(points));
            }else{
                if(eventPoint.distance(lastValid) > 6){
                    lastValid = eventPoint;
                    points.add(new Point(e.getX(), e.getY()));
                    selectionPane.setPoints(new ArrayList<Point>(points));
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

}
