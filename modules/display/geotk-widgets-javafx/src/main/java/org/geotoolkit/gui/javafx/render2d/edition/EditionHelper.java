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
package org.geotoolkit.gui.javafx.render2d.edition;

import org.opengis.feature.Feature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.geotoolkit.feature.FeatureExt;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.StringUtilities;
import org.apache.sis.util.logging.Logging;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.internal.Loggers;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel
 */
public class EditionHelper {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gui.javafx.render2d.edition");
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
                                                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final Coordinate[] EMPTY_COORDINATE_ARRAY = new Coordinate[0];

    public static class EditionGeometry{

        public final ObjectProperty<Geometry> geometry = new SimpleObjectProperty<>();
        public int numSubGeom = -1;
        public int numHole = -1;
        public final int[] selectedNode = new int[]{-1,-1};

        public void reset() {
            geometry.set(null);
            numSubGeom = -1;
            numHole = -1;
            selectedNode[0] = -1;
            selectedNode[1] = -1;
        }

        public void moveSelectedNode(final Coordinate newCoordinate){
            moveSelectedNode(newCoordinate, false);
        }

        public void moveSelectedNode(final Coordinate newCoordinate, boolean newGeometry){
            if(geometry == null) return;
            if(numSubGeom < 0) return;
            if(selectedNode[0] < 0) return;

            Geometry base = geometry.get();
            if(newGeometry) base = (Geometry) base.clone();
            final Geometry geo = base.getGeometryN(numSubGeom);
            if(numHole < 0){
                final Coordinate[] coords = geo.getCoordinates();
                coords[selectedNode[0]].setCoordinate(newCoordinate);
                coords[selectedNode[1]].setCoordinate(newCoordinate);
                geo.geometryChanged();
            }else{
                final Polygon p = (Polygon) geo;
                final LineString ls = p.getInteriorRingN(numHole);
                final Coordinate[] coords = ls.getCoordinates();
                coords[selectedNode[0]].setCoordinate(newCoordinate);
                coords[selectedNode[1]].setCoordinate(newCoordinate);
                geo.geometryChanged();
            }

            geo.geometryChanged();
            if(newGeometry){
                geometry.set(base);
            }else{
                geometry.get().geometryChanged();
            }
        }

        /**
         * Indicate if a node is currently selected.
         *
         * @return true if a node is selected on the current geometry
         */
        public boolean hasNodeSelected(){
            return !(geometry.get() == null
                    || (numSubGeom >= geometry.get().getNumGeometries())
                    || numSubGeom < 0
                    || selectedNode[0] < 0);
        }

        /**
         * Remove the selected node from the geometry.
         *
         */
        public void deleteSelectedNode() {
            if(!hasNodeSelected()) return;

            //save datas
            final int srid = geometry.get().getSRID();
            final Object userData = geometry.get().getUserData();

            final Geometry geo = geometry.get().getGeometryN(numSubGeom);

            if(numHole < 0){
                final List<Coordinate> newCoords = new ArrayList<Coordinate>();
                newCoords.addAll(Arrays.asList(geo.getCoordinates()));

                if(geo instanceof Polygon && selectedNode[0] == 0){
                    //remove first and last point
                    newCoords.remove(newCoords.size()-1);
                    newCoords.remove(0);
                }else{
                    newCoords.remove(selectedNode[0]);
                }

                if(geometry.get() instanceof Point){
                    //can not delete node from a Point

                }else if(geometry.get() instanceof MultiPoint){
                    //we have deleted the given subgeometry
                    final MultiPoint mp = (MultiPoint) geometry.get();
                    final List<Geometry> parts = new ArrayList<Geometry>();
                    for(int i=0,n=mp.getNumGeometries();i<n;i++){
                        parts.add(mp.getGeometryN(i));
                    }
                    if(parts.size()>1){
                        //remove only if we have more then one point
                        parts.remove(numSubGeom);
                        geometry.set( createMultiPoint(parts) );
                    }

                }else if(geometry.get() instanceof LineString){
                    geometry.set(createLine(newCoords));

                }else if(geometry.get() instanceof MultiLineString){
                    final MultiLineString ml = (MultiLineString) geometry.get();
                    final List<Geometry> strs = new ArrayList<Geometry>();
                    for(int i=0,n=ml.getNumGeometries();i<n;i++){
                        if(i==numSubGeom){
                            final LineString str = createLine(newCoords);
                            strs.add(str);
                        }else{
                            strs.add(ml.getGeometryN(i));
                        }
                    }
                    geometry.set( createMultiLine(strs) );

                }else if(geometry.get() instanceof Polygon){
                    final Polygon poly = (Polygon) geometry.get();
                    final List<LinearRing> holes = new ArrayList<LinearRing>();
                    for(int i=0,n=poly.getNumInteriorRing();i<n;i++){
                        holes.add((LinearRing) poly.getInteriorRingN(i));
                    }
                    geometry.set( createPolygon(newCoords, holes.toArray(new LinearRing[holes.size()])) );

                }else if(geometry.get() instanceof MultiPolygon){
                    final MultiPolygon mp = (MultiPolygon) geometry.get();
                    final List<Geometry> polys = new ArrayList<Geometry>();
                    for(int i=0,n=mp.getNumGeometries();i<n;i++){
                        Polygon poly = (Polygon) mp.getGeometryN(i);
                        if(i==numSubGeom){
                            final List<LinearRing> holes = new ArrayList<LinearRing>();
                            for(int j=0,k=poly.getNumInteriorRing();j<k;j++){
                                holes.add((LinearRing) poly.getInteriorRingN(j));
                            }
                            poly = createPolygon(newCoords, holes.toArray(new LinearRing[holes.size()]));
                        }
                        polys.add(poly);
                    }
                    geometry.set( createMultiPolygon(polys) );
                }else{
                    throw new IllegalArgumentException("Unexpected geometry type :" + geometry.getClass());
                }

                selectedNode[0] = selectedNode[1] = -1;
            }else{
                throw new UnsupportedOperationException("not yet implemented");
            }

            geometry.get().setSRID(srid);
            geometry.get().setUserData(userData);

        }

        @Override
        public String toString() {
            return "Selection\n"+ StringUtilities.toStringTree(geometry,numSubGeom,numHole,selectedNode[0]);
        }

    }


    private final FXMap map;
    private final FeatureMapLayer editedLayer;
    private Geometry constraint = null;
    private boolean showAtributeditor;
    private int mousePointerSize = 4;

    /**
     *
     * @param map source map
     * @param editedLayer edited layer
     */
    public EditionHelper(final FXMap map, final FeatureMapLayer editedLayer) {
        ArgumentChecks.ensureNonNull("map", map);
        ArgumentChecks.ensureNonNull("layer", editedLayer);
        this.map = map;
        this.editedLayer = editedLayer;
        this.showAtributeditor = true;
    }

    /**
     * The mouse pointer size is used when searching for node and geometries.
     * It defines the buffer size of the intersection area.
     * If size is too small picking will become impossible.
     * If size is too big picking won't select the most appropriate element.
     *
     * Recommended size range 3 - 10
     *
     * @param mousePointerSize on click distance tolerance
     */
    public void setMousePointerSize(int mousePointerSize) {
        this.mousePointerSize = mousePointerSize;
    }

    public int getMousePointerSize() {
        return mousePointerSize;
    }

    public boolean isShowAtributeditor() {
        return showAtributeditor;
    }

    public void setShowAtributeditor(boolean showAtributeditor) {
        this.showAtributeditor = showAtributeditor;
    }

    /**
     * Set geometry constraint.
     * This geometry will be used to restrict all coordinate creation.
     * The closest intersection point will be returned if mouse is outside the constraint
     * area.
     *
     * @param constraint geometry
     */
    public void setConstraint(Geometry constraint) {
        this.constraint = constraint;
    }

    /**
     *
     * @return constraint geometry
     */
    public Geometry getConstraint() {
        return constraint;
    }

    /**
     * transform a mouse coordinate in JTS Geometry using the CRS of the map context
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    public Polygon mousePositionToGeometry(final double mx, final double my) {
        final Coordinate[] coord = new Coordinate[5];
        coord[0] = toCoord(mx - mousePointerSize, my - mousePointerSize);
        coord[1] = toCoord(mx - mousePointerSize, my + mousePointerSize);
        coord[2] = toCoord(mx + mousePointerSize, my + mousePointerSize);
        coord[3] = toCoord(mx + mousePointerSize, my - mousePointerSize);
        coord[4] = coord[0];

        final LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    public Point toJTS(final double x, final double y){
        final Coordinate coord = toCoord(x, y);
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    public Coordinate toCoord(final double x, final double y){
        final AffineTransform2D trs = map.getCanvas().getObjectiveToDisplay();
        AffineTransform dispToObj;
        try {
            dispToObj = trs.createInverse();
        } catch (NoninvertibleTransformException ex) {
            dispToObj = new AffineTransform();
            LOGGER.log(Level.WARNING, null, ex);
        }
        final double[] crds = new double[]{x,y};
        dispToObj.transform(crds, 0, crds, 0, 1);

        final Coordinate coord = new Coordinate(crds[0], crds[1]);

        if(constraint!=null){
            final CoordinateReferenceSystem crs2d = map.getCanvas().getObjectiveCRS2D();
            try {
                final Geometry geom = JTS.transform(constraint, crs2d);

                final DistanceOp distOp = new DistanceOp(geom, GO2Utilities.JTS_FACTORY.createPoint(coord));
                final Coordinate[] nearest = distOp.nearestPoints();
                return nearest[0];

            } catch (Exception ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage());
            }
        }

        return coord;
    }

    /**
     * Get feature at given mouse coordinate.
     *
     * @param mx mouse x coordinate in display crs
     * @param my mouse y coordinate in display crs
     * @param style consider the style for selection.
     * @return Feature or null
     */
    public Feature grabFeature(final double mx, final double my, final boolean style) {

        if(editedLayer == null) return null;

        Feature candidate = null;

        FeatureCollection editgeoms = null;
        FeatureIterator fi = null;
        try {
            final Polygon geo = mousePositionToGeometry(mx, my);
            Filter flt = toFilter(geo, editedLayer);

            //concatenate with temporal range if needed ----------------------------
            for (final FeatureMapLayer.DimensionDef def : editedLayer.getExtraDimensions()) {
                final CoordinateReferenceSystem crs = def.getCrs();
                final org.opengis.geometry.Envelope canvasEnv = map.getCanvas().getVisibleEnvelope();
                final org.opengis.geometry.Envelope dimEnv;
                try {
                    dimEnv = Envelopes.transform(canvasEnv, crs);
                } catch (TransformException ex) {
                    continue;
                }

                final Filter dimFilter = FILTER_FACTORY.and(
                    FF.or(
                            FF.isNull(def.getLower()),
                            FF.lessOrEqual(def.getLower(), FF.literal(dimEnv.getMaximum(0)) )),
                    FF.or(
                            FF.isNull(def.getUpper()),
                            FF.greaterOrEqual(def.getUpper(), FF.literal(dimEnv.getMinimum(0)) ))
                );

                flt = FF.and(flt, dimFilter);
            }

            QueryBuilder qb = new QueryBuilder(editedLayer.getCollection().getType().getName().toString());
            //we filter in the map CRS
            qb.setCRS(map.getCanvas().getObjectiveCRS2D());
            editgeoms = (FeatureCollection) editedLayer.getCollection().subCollection(qb.buildQuery());

            //we filter ourself since we want the filter to occure after the reprojection
            editgeoms = FeatureStreams.filter(editgeoms, flt);

            fi = editgeoms.iterator();
            if (fi.hasNext()) {
                Feature sf = fi.next();

                //get the original, in it's data crs
                flt = FF.id(Collections.singleton(FeatureExt.getId(sf)));
                sf = null;
                fi.close();

                qb.reset();
                qb.setTypeName(editedLayer.getCollection().getType().getName());
                qb.setFilter(flt);
                editgeoms = (FeatureCollection) editedLayer.getCollection().subCollection(qb.buildQuery());
                fi = editgeoms.iterator();
                if (fi.hasNext()){
                    sf = fi.next();
                }

                return sf;
            }
        }catch(Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }finally{
            if(fi != null){
                fi.close();
            }
        }

        return candidate;
    }

    public boolean grabGeometrynode(final Point pt, final double mx, final double my){
        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            return pt.intersects(mouseGeo);
        }catch(Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
            return false;
        }
    }

    public void grabGeometryNode(final double mx, final double my,final EditionGeometry edited) {

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            grabGeometryNode(mouseGeo, edited);
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }
    }

    public void grabGeometryNode(final Geometry mouseGeo, final EditionGeometry edited) {

        //reset selection
        edited.numSubGeom = -1;
        edited.numHole = -1;
        edited.selectedNode[0] = -1;
        edited.selectedNode[1] = -1;

        for (int i=0,n=edited.geometry.get().getNumGeometries(); i<n; i++) {
            final Geometry subgeo = edited.geometry.get().getGeometryN(i);

            if (subgeo.intersects(mouseGeo)) {
                //this geometry intersect the mouse
                edited.numSubGeom = i;

                //this far it can only be a point, linestring or polygon
                if(subgeo instanceof Point || subgeo instanceof LineString){
                    simpleIntersect(mouseGeo, subgeo, edited.selectedNode);
                    break;
                }else if(subgeo instanceof Polygon){
                    final Polygon poly = (Polygon) subgeo;
                    LineString ring = poly.getExteriorRing();
                    simpleIntersect(mouseGeo, ring,edited.selectedNode);
                    if(edited.selectedNode[0] != -1){
                        break;
                    }

                    for(int j=0,k=poly.getNumInteriorRing(); j<k; j++){
                        ring = poly.getInteriorRingN(j);
                        simpleIntersect(mouseGeo, ring,edited.selectedNode);
                        if(edited.selectedNode[0] != -1){
                            edited.numHole = j;
                            break;
                        }
                    }

                }else{
                    throw new IllegalArgumentException("Was expecting a Point, LineString or Polygon, but was : " + subgeo.getClass());
                }

                break;
            }

        }

    }

    private void simpleIntersect(final Geometry mouseGeo, final Geometry geom, final int[] indexes){
        final Coordinate[] coos = geom.getCoordinates();
        for (int j=0,m=coos.length; j<m; j++) {
            final Coordinate coo = coos[j];
            final Point p = createPoint(coo);
            if (p.intersects(mouseGeo)) {
                if(geom instanceof LinearRing && (j==0 || j == m-1)){
                    indexes[0] = 0;
                    indexes[1] = m-1;
                }else{
                    indexes[0] = indexes[1] = j;
                }
                return;
            }
        }
    }

    public void moveGeometry(final Geometry geo, final double dx, final double dy) {

        try{
            final Point2D pt0 = map.getCanvas().getObjectiveToDisplay().inverseTransform(new Point2D.Double(0, 0), null);
            final Point2D pt = map.getCanvas().getObjectiveToDisplay().inverseTransform(new Point2D.Double(dx, dy), null);
            pt.setLocation(pt.getX()-pt0.getX(), pt.getY()-pt0.getY());

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);
                final Coordinate[] coos = subgeo.getCoordinates();

                for (int j=0,m=coos.length; j<m; j++) {
                    final Coordinate coo = coos[j];
                    coo.x += pt.getX();
                    coo.y += pt.getY();
                }
                subgeo.geometryChanged();
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }
        geo.geometryChanged();

    }

    public void moveSubGeometry(final Geometry geo, final int indice, final double dx, final double dy) {

        try{
            final Point2D pt0 = map.getCanvas().getObjectiveToDisplay().inverseTransform(new Point2D.Double(0, 0), null);
            final Point2D pt = map.getCanvas().getObjectiveToDisplay().inverseTransform(new Point2D.Double(dx, dy), null);
            pt.setLocation(pt.getX()-pt0.getX(), pt.getY()-pt0.getY());

            final Geometry subgeo = geo.getGeometryN(indice);
            final Coordinate[] coos = subgeo.getCoordinates();

            for (int j=0,m=coos.length; j<m; j++) {
                final Coordinate coo = coos[j];
                coo.x += pt.getX();
                coo.y += pt.getY();
            }
            subgeo.geometryChanged();

        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }
        geo.geometryChanged();

    }

    public Geometry insertNode(final Polygon geo, final double mx, final double my) {
        try{
            //transform our mouse in a geometry
            final Polygon mouseGeo = mousePositionToGeometry(mx, my);
            final Point mousePoint = toJTS(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length-1; j<m; j++) {
                    //find the segment that intersect
                    final Coordinate coo1 = coos[j];
                    final Coordinate coo2 = coos[j+1];
                    final LineString segment = createLine(coo1,coo2);

                    if(mouseGeo.intersects(segment) && isOnLine(mouseGeo,mousePoint,segment)){
                        //we must add the new node on this segment

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,p=coos.length; d<p; d++) {
                            ncs.add(coos[d]);
                            if(d==j){
                                //we must add the new node here
                                ncs.add(mousePoint.getCoordinate());
                            }
                         }

                        final Geometry ls = createPolygon(ncs);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    public Geometry insertNode(final LineString geo, final double mx, final double my) {
        try{
            //transform our mouse in a geometry
            final Polygon mouseGeo = mousePositionToGeometry(mx, my);
            final Point mousePoint = toJTS(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length-1; j<m; j++) {
                    //find the segment that intersect
                    final Coordinate coo1 = coos[j];
                    final Coordinate coo2 = coos[j+1];
                    final LineString segment = createLine(coo1,coo2);

                    if(mouseGeo.intersects(segment) && isOnLine(mouseGeo,mousePoint,segment)){
                        //we must add the new node on this segment

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,p=coos.length; d<p; d++) {
                            ncs.add(coos[d]);
                            if(d==j){
                                //we must add the new node here
                                ncs.add(mousePoint.getCoordinate());
                            }
                         }

                        final LineString ls = createLine(ncs);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    private static boolean isOnLine(final Polygon candidate, final Point center, final LineString line){
        final Envelope env = line.getEnvelopeInternal();
        final Coordinate coord = center.getCoordinate();

        if(env.contains(coord)){
            //get the nearest point on the line to avoid deformations
            final Coordinate[] cnds = DistanceOp.nearestPoints(line, center);
            coord.setCoordinate(cnds[0]);
            return true;
        }else{
            //make a more accurate test, envelope might have a width or hight
            //of zero which will return false on intersection wit the point
            final Polygon buffer = (Polygon) line.buffer(candidate.getEnvelopeInternal().getWidth()/2, 10, BufferParameters.CAP_FLAT);
            if(buffer.contains(center)){
                //get the nearest point on the line to avoid deformations
                final Coordinate[] cnds = DistanceOp.nearestPoints(line, center);
                coord.setCoordinate(cnds[0]);
                return true;
            }else{
                return false;
            }
        }

    }

    public Geometry insertNode(final GeometryCollection geo, final double mx, final double my) {
        try{
            //transform our mouse in a geometry
            final Polygon mouseGeo = mousePositionToGeometry(mx, my);
            final Point mousePoint = toJTS(mx, my);

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    //this geometry intersect the mouse

                    final Coordinate[] coos = subgeo.getCoordinates();
                    for (int j=0,m=coos.length-1; j<m; j++) {
                        //find the segment that intersect
                        final Coordinate coo1 = coos[j];
                        final Coordinate coo2 = coos[j+1];
                        final LineString segment = createLine(coo1,coo2);

                        if(mouseGeo.intersects(segment) && isOnLine(mouseGeo,mousePoint,segment)){
                            //we must add the new node on this segment

                            final List<Geometry> subs = new ArrayList<Geometry>();
                            for (int k=0,l=geo.getNumGeometries(); k<l; k++) {
                                if(k==i){
                                    //this subgeo must be changed
                                    final List<Coordinate> ncs = new ArrayList<Coordinate>();
                                    for (int d=0,p=coos.length; d<p; d++) {
                                        ncs.add(coos[d]);
                                        if(d==j){
                                            //we must add the new node here
                                            ncs.add(mousePoint.getCoordinate());
                                        }
                                    }

                                    if(geo instanceof MultiLineString && ncs.size() >1){
                                         subs.add(createLine(ncs));
                                    }else if(geo instanceof MultiPolygon && ncs.size() >2){
                                         subs.add(createPolygon(ncs));
                                    }
                                }else{
                                    subs.add(geo.getGeometryN(k));
                                }
                            }

                            if(geo instanceof MultiLineString && !subs.isEmpty()){
                                final Geometry ls = createMultiLine(subs);
                                ls.setSRID(geo.getSRID());
                                ls.setUserData(geo.getUserData());
                                return ls;
                            }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                                final Geometry ls = createMultiPolygon(subs);
                                ls.setSRID(geo.getSRID());
                                ls.setUserData(geo.getUserData());
                                return ls;
                            }else{
                                return null;
                            }
                        }

                    }
                    break;
                }

            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    public Geometry deleteNode(final Polygon geo, final double mx, final double my) {

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length; j<m; j++) {
                    final Coordinate coo = coos[j];
                    final Point p = createPoint(coo);
                    if (p.intersects(mouseGeo)) {
                        //delete this node

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,z=coos.length; d<z; d++) {
                            if(d!=j){
                                ncs.add(coos[d]);
                            }
                        }

                        if(ncs.size() > 2){
                            final Geometry ls = createPolygon(ncs);
                            ls.setSRID(geo.getSRID());
                            ls.setUserData(geo.getUserData());
                            return ls;
                        }
                        break;
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    public Geometry deleteNode(final LineString geo, final double mx, final double my) {

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length; j<m; j++) {
                    final Coordinate coo = coos[j];
                    final Point p = createPoint(coo);
                    if (p.intersects(mouseGeo)) {
                        //delete this node

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,z=coos.length; d<z; d++) {
                            if(d!=j){
                                ncs.add(coos[d]);
                            }
                        }

                        if(ncs.size() > 1){
                            final Geometry ls = createLine(ncs);
                            ls.setSRID(geo.getSRID());
                            ls.setUserData(geo.getUserData());
                            return ls;
                        }
                        break;
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }


    public Geometry deleteNode(final GeometryCollection geo, final double mx, final double my) {

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    //this geometry intersect the mouse

                    final Coordinate[] coos = subgeo.getCoordinates();
                    for (int j=0,m=coos.length; j<m; j++) {
                        final Coordinate coo = coos[j];
                        final Point p = createPoint(coo);
                        if (p.intersects(mouseGeo)) {
                            //delete this node

                            final List<Geometry> subs = new ArrayList<Geometry>();
                            for (int k=0,l=geo.getNumGeometries(); k<l; k++) {
                                if(k==i){
                                    //this subgeo must be changed
                                    final List<Coordinate> ncs = new ArrayList<Coordinate>();
                                    for (int d=0,z=coos.length; d<z; d++) {
                                        if(d!=j){
                                            ncs.add(coos[d]);
                                        }
                                    }
                                    if(geo instanceof MultiLineString && ncs.size() >1){
                                         subs.add(createLine(ncs));
                                    }else if(geo instanceof MultiPolygon && ncs.size() >2){
                                         subs.add(createPolygon(ncs));
                                    }
                                }else{
                                    subs.add(geo.getGeometryN(k));
                                }
                            }

                            if(geo instanceof MultiLineString && !subs.isEmpty()){
                                final Geometry ls = createMultiLine(subs);
                                ls.setSRID(geo.getSRID());
                                ls.setUserData(geo.getUserData());
                                return ls;
                            }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                                final Geometry ls = createMultiPolygon(subs);
                                ls.setSRID(geo.getSRID());
                                ls.setUserData(geo.getUserData());
                                return ls;
                            }else{
                                return null;
                            }

                        }
                    }
                    break;
                }

            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    public Geometry deleteSubGeometry(final GeometryCollection geo, final double mx, final double my) {

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    //this geometry intersect the mouse

                    final List<Geometry> subs = new ArrayList<Geometry>();
                    for (int k=0,l=geo.getNumGeometries(); k<l; k++) {
                        if(k!=i){
                            subs.add(geo.getGeometryN(k));
                        }
                    }

                    if(geo instanceof MultiLineString && !subs.isEmpty()){
                        final Geometry ls = createMultiLine(subs);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                        final Geometry ls = createMultiPolygon(subs);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }else if(geo instanceof MultiPoint && !subs.isEmpty()){
                        final Geometry ls = createMultiPoint(subs);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }else{
                        return null;
                    }
                }

            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }


    public Geometry deleteHole(final Geometry geo, final double mx, final double my) {
        if(!(geo instanceof Polygon || geo instanceof MultiPolygon)){
            //this method only works on polygon or multipolygon
            return geo;
        }

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            final List<Polygon> subGeometries = new ArrayList<Polygon>();
            final List<LinearRing> holes = new ArrayList<LinearRing>();

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                //subgeo is a Polygon
                subGeometries.add((Polygon) geo.getGeometryN(i));
            }

            for (int i=0,n=subGeometries.size();i<n;i++) {
                final Polygon subgeo = subGeometries.get(i);

                //find the hole to remove
                int toRemove = -1;
                holes.clear();
                for(int j=0,k=subgeo.getNumInteriorRing(); j<k; j++){
                    final LinearRing ring = (LinearRing) subgeo.getInteriorRingN(j);
                    holes.add(ring);
                    if(toRemove == -1 && ring.intersects(mouseGeo)){
                        toRemove = j;
                    }
                }

                if(toRemove != -1){
                    //remove this ring and return geometry
                    holes.remove(toRemove);

                    if(geo instanceof Polygon){
                        final Geometry ls = GEOMETRY_FACTORY.createPolygon((LinearRing)subgeo.getExteriorRing(),
                                holes.toArray(new LinearRing[holes.size()]));
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }else if(geo instanceof MultiPolygon){
                        //modify the subgeometry
                        final Polygon poly = GEOMETRY_FACTORY.createPolygon((LinearRing)subgeo.getExteriorRing(),
                                holes.toArray(new LinearRing[holes.size()]));
                        subGeometries.set(i, poly);
                        //recreate the multipolygon
                        final Geometry ls = createMultiPolygon(subGeometries);
                        ls.setSRID(geo.getSRID());
                        ls.setUserData(geo.getUserData());
                        return ls;
                    }else{
                        throw new IllegalStateException("Should not happen, expecting "
                                + "Polygon or MultiPolygon but was "+ geo.getClass());
                    }

                }

            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        //nothing to remove return original geometry
        return geo;
    }


    public Geometry toObjectiveCRS(final Feature sf){
        return FeatureExt.getDefaultGeometryValue(sf)
                .filter(Geometry.class::isInstance)
                .map(Geometry.class::cast)
                .map(this::toObjectiveCRS)
                .orElse(null);
    }

    public Geometry toObjectiveCRS(Geometry geom){
        try{
            final MathTransform trs = CRS.findOperation(
                    FeatureExt.getCRS(editedLayer.getCollection().getType()),
                    map.getCanvas().getObjectiveCRS2D(), null).getMathTransform();

            geom = JTS.transform(geom, trs);
            JTS.setCRS(geom, map.getCanvas().getObjectiveCRS2D());
            return geom;
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }
        return null;
    }


    /**
     *
     * @param poly : in canvas objective CRS
     * @param fl : target layer filter
     * @return geometry filter
     * @throws org.opengis.geometry.MismatchedDimensionException if crs dimensions do not match
     */
    public Filter toFilter(final Geometry poly, final FeatureMapLayer fl) throws MismatchedDimensionException {

        final PropertyType desc = FeatureExt.getDefaultGeometry(fl.getCollection().getType());
        final String geoStr = desc.getName().tip().toString();
        final Expression geomField = FF.property(geoStr);

        final Geometry dataPoly = poly;
        JTS.setCRS(dataPoly, map.getCanvas().getObjectiveCRS2D());

        final Expression geomData = FF.literal(dataPoly);
        final Filter f = FF.intersects(geomData,geomField);

        return f;
    }


    //manipulating the feature source, transaction -----------------------------

    public Feature sourceAddGeometry(Geometry geom) {

        if (editedLayer != null && geom != null) {

            final FeatureType featureType = (FeatureType) editedLayer.getCollection().getType();
            final CoordinateReferenceSystem dataCrs = FeatureExt.getCRS(featureType);
            final Feature feature = featureType.newInstance();

            try {
                geom = JTS.transform(geom, CRS.findOperation(map.getCanvas().getObjectiveCRS2D(), dataCrs, null).getMathTransform());
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(),geom);

            if(editedLayer.getCollection().isWritable()){
                try {
                    ((FeatureCollection)editedLayer.getCollection()).add(feature);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }

            map.getCanvas().repaint();

            return feature;
        }

        return null;

    }

    public void sourceModifyFeature(final Feature feature, final Geometry geo, boolean reprojectToDataCRS){

        if(feature == null || geo == null){
            //nothing to do
            return;
        }

        final String ID = FeatureExt.getId(feature).getID();

        ensureNonNull("geometry", geo);
        ensureNonNull("id", ID);

        if (editedLayer != null && editedLayer.getCollection().isWritable()) {

            final Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));
            final FeatureType featureType = editedLayer.getCollection().getType();
            final PropertyType geomAttribut = FeatureExt.getDefaultGeometry(featureType);
            final CoordinateReferenceSystem dataCrs = FeatureExt.getCRS(geomAttribut);

            try {
                final Geometry geom;
                if(reprojectToDataCRS){
                    geom = JTS.transform(geo,
                            CRS.findOperation(map.getCanvas().getObjectiveCRS(), dataCrs, null).getMathTransform());
                    JTS.setCRS(geom, dataCrs);
                }else{
                    geom = geo;
                }

                editedLayer.getCollection().update(filter, Collections.singletonMap(geomAttribut.getName().toString(), geom));
            } catch (final Exception ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
            } finally {
                map.getCanvas().repaint();
            }

        }

    }

    public void sourceRemoveFeature(final Feature feature){
        sourceRemoveFeature(FeatureExt.getId(feature).getID());
    }

    public void sourceRemoveFeature(final String ID) {
        ensureNonNull("id", ID);

        if (editedLayer != null && editedLayer.getCollection().isWritable()) {

            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));

            try {
                editedLayer.getCollection().remove(filter);
            } catch (final Exception ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
            }

            map.getCanvas().repaint();
        }

    }


    //static helper methods -----------------------------------------------------

    public static Iterator<EditionTool.Spi> getToolSpis(){
        final ServiceLoader<EditionTool.Spi> sl = ServiceLoader.load(EditionTool.Spi.class);
        return sl.iterator();
    }

    public static EditionTool.Spi getToolSpi(String name){
        final Iterator<EditionTool.Spi> ite = getToolSpis();
        while(ite.hasNext()){
            final EditionTool.Spi spi = ite.next();
            if(name.equals(spi.getName())){
                return spi;
            }
        }
        return null;
    }

    public static Geometry createGeometry(final List<Coordinate> coords) {
        int size = coords.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return createPoint(coords.get(0));
            case 2:
                return createLine(coords);
            default:
                return createLine(coords);
        }
    }

    public static Point createPoint(final Coordinate coord) {
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    public static MultiPoint createMultiPoint(final List<? extends Geometry> geoms) {
        List<Point> lst = new ArrayList<Point>();
        for (Geometry go : geoms) {
            if (go instanceof Point) {
                lst.add((Point) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiPoint(lst.toArray(new Point[lst.size()]));
    }

    public static LineString createLine(final Coordinate ... coords) {
        return GEOMETRY_FACTORY.createLineString(coords);
    }
    public static LineString createLine(final List<Coordinate> coords) {
        return GEOMETRY_FACTORY.createLineString(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    public static LinearRing createLinearRing(List<Coordinate> coords) {
        coords = new ArrayList<Coordinate>(coords);
        if (!(coords.get(0).equals2D(coords.get(coords.size() - 1)))) {
            Coordinate coo = new Coordinate(coords.get(0));
            coords.add(coo);
        }
        if(coords.size() == 3){
            Coordinate coo = new Coordinate(coords.get(0));
            coords.add(coo);
        }

        return GEOMETRY_FACTORY.createLinearRing(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    public static Polygon createPolygon(final List<Coordinate> coords, LinearRing ... holes) {
        LinearRing ring = createLinearRing(coords);
        return GEOMETRY_FACTORY.createPolygon(ring, holes);
    }

    public static MultiPolygon createMultiPolygon(final List<? extends Geometry> geoms) {
        List<Polygon> lst = new ArrayList<Polygon>();
        for (Geometry go : geoms) {
            if (go instanceof Polygon) {
                lst.add((Polygon) go);
            }else{
                throw new IllegalArgumentException("Found an unexpected geometry type while building multipolygon : " + go.getClass());
            }
        }
        return GEOMETRY_FACTORY.createMultiPolygon(lst.toArray(new Polygon[lst.size()]));
    }

    public static MultiLineString createMultiLine(final List<? extends Geometry> geoms) {
        List<LineString> lst = new ArrayList<LineString>();
        for (Geometry go : geoms) {
            if (go instanceof LineString) {
                lst.add((LineString) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiLineString(lst.toArray(new LineString[lst.size()]));
    }

}
