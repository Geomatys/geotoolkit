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
package org.geotoolkit.gui.swing.go2.control.selection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.geometry.jts.JTS;

import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Selection handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class DefaultSelectionHandler implements CanvasHandler {

    private static final Logger LOGGER = Logging.getLogger(DefaultSelectionHandler.class);

    protected static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
                                                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    

    private final EventListener mouseInputListener;
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

            //disable auto repaint to avoid a repaint on each layer selection change
            map2D.getCanvas().getController().setAutoRepaint(false);

            MapContext context = ((ContextContainer2D)map2D.getCanvas().getContainer()).getContext();

            for(final MapLayer layer : context.layers()){
                if(layer instanceof FeatureMapLayer && layer.isSelectable()){
                    FeatureMapLayer fml = (FeatureMapLayer)layer;
                    Id f = fml.getSelectionFilter();
                    f = combine(f, selection.get(fml));
                    fml.setSelectionFilter(f);
                }
            }

            selection.clear();

            map2D.getCanvas().getController().setAutoRepaint(true);
        }

        @Override
        public void visit(ProjectedFeature feature, Shape queryArea) {

            final FeatureMapLayer layer = feature.getFeatureLayer();
            Set<FeatureId> ids = selection.get(layer);

            if(ids == null){
                ids = new HashSet<FeatureId>();
                selection.put(layer, ids);
            }

            ids.add(feature.getFeatureId());
        }

        @Override
        public void visit(ProjectedCoverage coverage, Shape queryArea) {
        }
    };
    private boolean squareArea;
    private boolean withinArea;
    private boolean geographicArea;
    private Map2D map2D;
    private int key = -1;


    public DefaultSelectionHandler() {
        mouseInputListener = new EventListener();
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

    public Map2D getMap() {
        return map2D;
    }

    public void setMap(Map2D map2D) {
        this.map2D = map2D;
    }

    private Id combine(Id original, Set<? extends Identifier> ids){
        final Id f;

        if(key == KeyEvent.VK_CONTROL){
            //add selection
            Set<Identifier> in = new HashSet<Identifier>(ids);
            in.addAll(((Id)original).getIdentifiers());
            f = FF.id(in);
        } else if (key == KeyEvent.VK_SHIFT){
            //remove the commun part selection
            Set<Identifier> in = new HashSet<Identifier>(((Id)original).getIdentifiers());
            if(ids != null){
                in.removeAll(ids);
            }
            f = FF.id(in);
        } else {
            if(ids != null){
                f = FF.id(ids);
            }else{
                f = null;
            }
        }

        return f;
    }

    private void doSelection(List<Point> points, int key) {
        this.key = key;

        if (points.size() > 2) {

            if(geographicArea){
                AbstractContainer2D container = map2D.getCanvas().getContainer();

                if(container instanceof ContextContainer2D){
                    final ContextContainer2D cc = (ContextContainer2D) container;
                    final MapContext context = cc.getContext();
                    
                    //make a geographic selection
                    final List<Coordinate> coords = new ArrayList<Coordinate>();
                    for(Point p : points){
                        coords.add(new Coordinate(p.x, p.y));
                    }
                    Point last = points.get(0);
                    coords.add(new Coordinate(last.x,last.y));

                    final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
                    final Polygon poly = GEOMETRY_FACTORY.createPolygon(ring, new LinearRing[0]);

                    final List<MapLayer> layers = new ArrayList<MapLayer>(context.layers());

                    for(MapLayer layer : layers){
                        if(layer instanceof FeatureMapLayer && layer.isSelectable() && layer.isVisible()){
                            FeatureMapLayer fml = (FeatureMapLayer)layer;
                            final Set<Identifier> ids = new HashSet<Identifier>();

                            final FeatureMapLayer fl = (FeatureMapLayer) layer;
                            final String geoStr = fl.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
                            final Expression geomField = FF.property(geoStr);

                            CoordinateReferenceSystem dataCrs = fl.getFeatureSource().getSchema().getCoordinateReferenceSystem();

                            try {
                                final Geometry dataPoly = JTS.transform(poly, CRS.findMathTransform(map2D.getCanvas().getDisplayCRS(), dataCrs,true));
                                
                                final Expression geomData = FF.literal(dataPoly);
                                final Filter f = (withinArea) ? FF.within(geomField, geomData) : FF.intersects(geomField, geomData);

                                final QueryBuilder builder = new QueryBuilder();
                                builder.setTypeName(fml.getFeatureSource().getSchema().getName());
                                builder.setFilter(f);
                                builder.setProperties(new String[]{geoStr});
                                final Query query = builder.buildQuery();
                                
                                FeatureCollection<SimpleFeatureType,SimpleFeature> fc = fl.getFeatureSource().getFeatures(query);
                                FeatureIterator<SimpleFeature> fi = fc.features();
                                while(fi.hasNext()){
                                    SimpleFeature fea = fi.next();
                                    ids.add(fea.getIdentifier());
                                }
                                fi.close();
                            } catch (Exception ex) {
                                LOGGER.log(Level.SEVERE, null, ex);
                            }

                            Id selection = combine(fml.getSelectionFilter(), ids);
                            fl.setSelectionFilter(selection);
                        }
                    }

                }

            }else{
                //make a graphic selection
                final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                path.moveTo(points.get(0).x, points.get(0).y);

                for(int i=1;i<points.size();i++){
                    Point p = points.get(i);
                    path.lineTo(p.x, p.y);
                }

                map2D.getCanvas().getGraphicsIn(path, visitor, (withinArea) ? VisitFilter.WITHIN : VisitFilter.INTERSECTS);
            }
            map2D.getCanvas().getController().repaint();
        }

    }

    @Override
    public J2DCanvas getCanvas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void install(Component component) {
        map2D.addDecoration(selectionPane);
        map2D.getComponent().addMouseListener(mouseInputListener);
        map2D.getComponent().addMouseMotionListener(mouseInputListener);
        map2D.getComponent().addKeyListener(mouseInputListener);
    }

    @Override
    public void uninstall(Component component) {
        map2D.removeDecoration(selectionPane);
        map2D.getComponent().removeMouseListener(mouseInputListener);
        map2D.getComponent().removeMouseMotionListener(mouseInputListener);
        map2D.getComponent().removeKeyListener(mouseInputListener);
    }

    private class EventListener implements MouseInputListener,KeyListener {

        private int key;

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
            doSelection(points,key);
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
            doSelection(points,key);
            selectionPane.setPoints(null);
            points.clear();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map2D.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            map2D.getComponent().requestFocus();
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

        @Override
        public void keyTyped(KeyEvent arg0) {
        }

        @Override
        public void keyPressed(KeyEvent arg0) {
            key = arg0.getKeyCode();
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
            key = -1;
        }
    }

}
