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

import com.vividsolutions.jts.geom.Coordinate;
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
 * laso selection handler
 * 
 * @author Johann Sorel
 */
public class LasoSelectionHandler implements CanvasHandler {

    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

//    private static final ImageIcon ICON = IconBundle.getInstance().getIcon("16_select_laso");
    
    private final MouseInputListener mouseInputListener = new MouseListen();
    private final LasoSelectionDecoration selectionPane = new LasoSelectionDecoration();
    private final GoMap2D map2D;
    protected Cursor CUR_SELECT;

    private final GraphicVisitor visitor = new AbstractGraphicVisitor() {

        private final Map<MapLayer,Set<FeatureId>> selection = new HashMap<MapLayer, Set<FeatureId>>();

        @Override
        public void startVisit() {
            super.startVisit();
            selection.clear();
            System.out.println("start");
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
            System.out.println("end");
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


    public LasoSelectionHandler(GoMap2D map2D) {
        this.map2D = map2D;
    }


    private void doMouseSelection(int mx, int my) {

//        Geometry geometry = mousePositionToGeometry(mx, my);
//        if (geometry != null) {
//            map2D.doSelection(geometry);
//        }
    }

    /**
     *  transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
//    private Geometry mousePositionToGeometry(int mx, int my) {
//        Coordinate[] coord = new Coordinate[5];
//        int taille = 4;
//        StreamingStrategy strategy = map2D.getRenderingStrategy();
//        coord[0] = strategy.toMapCoord(mx - taille, my - taille);
//        coord[1] = strategy.toMapCoord(mx - taille, my + taille);
//        coord[2] = strategy.toMapCoord(mx + taille, my + taille);
//        coord[3] = strategy.toMapCoord(mx + taille, my - taille);
//        coord[4] = coord[0];
//
//        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//        return GEOMETRY_FACTORY.createPolygon(lr1, null);
//    }

    private void doSelection(List<Point> points) {

        if (points.size() > 2) {

            final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(points.get(0).x, points.get(0).y);


            for(int i=1;i<points.size();i++){
                Point p = points.get(i);
                path.lineTo(p.x, p.y);
            }

            map2D.getCanvas().getGraphicsIn(path, visitor, VisitFilter.INTERSECTS);
            map2D.getCanvas().getController().repaint();


//            Coordinate[] coord = new Coordinate[lst.size() + 1];
//
//            int i = 0;
//            for (int n = lst.size(); i < n; i++) {
//                coord[i] = lst.get(i);
//            }
//
//            coord[i] = coord[0];
//
//            LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//            Geometry geometry = GEOMETRY_FACTORY.createPolygon(lr1, null);




//            map2D.doSelection(geometry);
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

        @Override
        public void mouseClicked(MouseEvent e) {
            doMouseSelection(e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            lastValid = e.getPoint();
            points.clear();
            points.add(lastValid);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            points.add(e.getPoint());

            doSelection(points);

            selectionPane.setPoints(null);

            points.clear();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map2D.getComponent().setCursor(CUR_SELECT);
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();

            if(p.distance(lastValid) > 6){
                lastValid = p;
                points.add(new Point(e.getX(), e.getY()));
                selectionPane.setPoints(new ArrayList<Point>(points));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

}
