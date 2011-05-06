/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Johann Sorel
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.go2.control.edition;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.Feature;
import com.vividsolutions.jts.geom.Coordinate;
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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class EditionHelper {

    private static final Logger LOGGER = Logging.getLogger(EditionHelper.class);
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
                                                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final Coordinate[] EMPTY_COORDINATE_ARRAY = new Coordinate[0];

    public static class EditionContext{
        public Feature feature = null;
        public Geometry geometry = null;
        public final List<Geometry> subGeometries =  new ArrayList<Geometry>();
        public int subGeometryIndex = -1;
        public int[] nodes = null;
        public final List<Coordinate> coords = new ArrayList<Coordinate>();
        public boolean modified = false;
        public boolean added = false;

        public void reset(){
            feature = null;
            geometry = null;
            subGeometries.clear();
            subGeometryIndex = -1;
            nodes = null;
            modified = false;
            added = false;
            coords.clear();
        }
    }

    public static class EditionGeometry{

        public Geometry geometry = null;
        public int numSubGeom = -1;
        public int numHole = -1;
        public final int[] selectedNode = new int[]{-1,-1};

        public void reset() {
            geometry = null;
            numSubGeom = -1;
            numHole = -1;
            selectedNode[0] = -1;
            selectedNode[1] = -1;
        }

        public void moveSelectedNode(final Coordinate newCoordinate){
            if(geometry == null) return;
            if(numSubGeom < 0) return;
            if(selectedNode[0] < 0) return;

            final Geometry geo = geometry.getGeometryN(numSubGeom);
            if(numHole < 0){
                final Coordinate[] coords = geo.getCoordinates();
                coords[selectedNode[0]].setCoordinate(newCoordinate);
                coords[selectedNode[1]].setCoordinate(newCoordinate);
                geo.geometryChanged();
            }else{
                throw new UnsupportedOperationException("not yet implemented");
            }

            geo.geometryChanged();
            geometry.geometryChanged();
        }

        public void deleteSelectedNode() {
            if(geometry == null) return;
            if(numSubGeom < 0) return;
            if(selectedNode[0] < 0) return;

            final Geometry geo = geometry.getGeometryN(numSubGeom);

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

                if(geometry instanceof Point){
                    //can not delete node from a Point

                }else if(geometry instanceof MultiPoint){
                    //we have deleted the given subgeometry
                    final MultiPoint mp = (MultiPoint) geometry;
                    final List<Geometry> parts = new ArrayList<Geometry>();
                    for(int i=0,n=mp.getNumGeometries();i<n;i++){
                        parts.add(mp.getGeometryN(i));
                    }
                    if(parts.size()>1){
                        //remove only if we have more then one point
                        parts.remove(numSubGeom);
                        geometry = createMultiPoint(parts);
                    }

                }else if(geometry instanceof LineString){
                    geometry = createLine(newCoords);

                }else if(geometry instanceof MultiLineString){
                    final MultiLineString ml = (MultiLineString) geometry;
                    final List<Geometry> strs = new ArrayList<Geometry>();
                    for(int i=0,n=ml.getNumGeometries();i<n;i++){
                        if(i==numSubGeom){
                            final LineString str = createLine(newCoords);
                            strs.add(str);
                        }else{
                            strs.add(ml.getGeometryN(i));
                        }
                    }
                    geometry = createMultiLine(strs);

                }else if(geometry instanceof Polygon){
                    final Polygon poly = (Polygon) geometry;
                    final List<LinearRing> holes = new ArrayList<LinearRing>();
                    for(int i=0,n=poly.getNumInteriorRing();i<n;i++){
                        holes.add((LinearRing) poly.getInteriorRingN(i));
                    }
                    geometry = createPolygon(newCoords, holes.toArray(new LinearRing[holes.size()]));

                }else if(geometry instanceof MultiPolygon){
                    final MultiPolygon mp = (MultiPolygon) geometry;
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
                    geometry = createMultiPolygon(polys);
                }else{
                    throw new IllegalArgumentException("Unexpected geometry type :" + geometry.getClass());
                }

                selectedNode[0] = selectedNode[1] = -1;
            }else{
                throw new UnsupportedOperationException("not yet implemented");
            }

        }

        @Override
        public String toString() {
            return "Selection\n"+ StringUtilities.toStringTree(geometry,numSubGeom,numHole,selectedNode[0]);
        }

    }


    private final JMap2D map;
    private final FeatureMapLayer editedLayer;

    EditionHelper(final JMap2D map, final FeatureMapLayer editedLayer) {
        this.map = map;
        this.editedLayer = editedLayer;
    }

    /**
     * transform a mouse coordinate in JTS Geometry using the CRS of the map context
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    public Polygon mousePositionToGeometry(final int mx, final int my) throws NoninvertibleTransformException {
        final Coordinate[] coord = new Coordinate[5];
        int taille = 4;

        coord[0] = toCoord(mx - taille, my - taille);
        coord[1] = toCoord(mx - taille, my + taille);
        coord[2] = toCoord(mx + taille, my + taille);
        coord[3] = toCoord(mx + taille, my - taille);
        coord[4] = coord[0];

        final LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    public Point toJTS(final int x, final int y){
        final Coordinate coord = toCoord(x, y);
        final Point geom = GEOMETRY_FACTORY.createPoint(coord);
        return geom;
    }

    public Coordinate toCoord(final int x, final int y){
        final AffineTransform2D trs = map.getCanvas().getController().getTransform();
        AffineTransform dispToObj;
        try {
            dispToObj = trs.createInverse();
        } catch (NoninvertibleTransformException ex) {
            dispToObj = new AffineTransform();
            LOGGER.log(Level.WARNING, null, ex);
        }
        final double[] crds = new double[]{x,y};
        dispToObj.transform(crds, 0, crds, 0, 1);
        return new Coordinate(crds[0], crds[1]);
    }

    public Feature grabFeature(final int mx, final int my, final boolean style) {

        if(editedLayer == null) return null;

        Feature candidate = null;

        FeatureCollection<Feature> editgeoms = null;
        FeatureIterator<Feature> fi = null;
        try {
            final Polygon geo = mousePositionToGeometry(mx, my);
            final Filter flt = toFilter(geo, editedLayer);
            editgeoms = (FeatureCollection<Feature>) editedLayer.getCollection().subCollection(
                    QueryBuilder.filtered(editedLayer.getCollection().getFeatureType().getName(), flt));

            fi = editgeoms.iterator();
            if (fi.hasNext()) {
                Feature sf = fi.next();
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

    public boolean grabGeometrynode(final Point pt, final int mx, final int my){
        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            return pt.intersects(mouseGeo);
        }catch(Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
            return false;
        }
    }

    public void grabGeometryNode(final int mx, final int my,final EditionGeometry edited) {
        
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

        for (int i=0,n=edited.geometry.getNumGeometries(); i<n; i++) {
            final Geometry subgeo = edited.geometry.getGeometryN(i);

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

    /**
     * grab a node in the given geometry.
     * int[0] == subgeometry index
     * int[1] == grabbed coordinate index
     * int[2] == grabbed coordinate index
     * there might be two coordinate grab in the case of polygon last point
     */
    public int[] grabGeometryNode(final Geometry geo, final int mx, final int my) {
        final int[] indexes = new int[]{-1,-1,-1};

        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    //this geometry intersect the mouse
                    indexes[0] = i;

                    final Coordinate[] coos = subgeo.getCoordinates();
                    for (int j=0,m=coos.length; j<m; j++) {
                        final Coordinate coo = coos[j];
                        final Point p = createPoint(coo);
                        if (p.intersects(mouseGeo)) {

                            if ((j==0 || j==m-1) && (geo instanceof Polygon || geo instanceof MultiPolygon)) {
                                //first and last coordinate index are the same point
                                indexes[1] = 0;
                                indexes[2] = m-1;
                            } else {
                                //coordinate is in the middle of the geometry
                                indexes[1] = j;
                                indexes[2] = j;
                            }
                        }
                    }
                    break;
                }

            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return indexes;
    }

    public void dragGeometryNode(final EditionContext context, final int mx, final int my) {
        final Coordinate mouseCoord = toCoord(mx, my);

        final Geometry subgeo = context.subGeometries.get(0);
        if(subgeo == null) return;

        final int[] nodeIndexes = context.nodes;
        if(nodeIndexes == null) return;

        context.modified = true;

        if(context.geometry instanceof Point){
            Point pt = (Point) context.geometry;
            pt.getCoordinate().x = mouseCoord.x;
            pt.getCoordinate().y = mouseCoord.y;
        }else{
            for (int index : nodeIndexes) {
                subgeo.getCoordinates()[index].x = mouseCoord.x;
                subgeo.getCoordinates()[index].y = mouseCoord.y;
            }
        }


        subgeo.geometryChanged();
        context.geometry.geometryChanged();
    }

    public void moveGeometry(final Geometry geo, final int dx, final int dy) {

        try{
            final Point2D pt0 = map.getCanvas().getController().getTransform().inverseTransform(new Point2D.Double(0, 0), null);
            final Point2D pt = map.getCanvas().getController().getTransform().inverseTransform(new Point2D.Double(dx, dy), null);
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

    public void moveSubGeometry(final Geometry geo, final int indice, final int dx, final int dy) {

        try{
            final Point2D pt0 = map.getCanvas().getController().getTransform().inverseTransform(new Point2D.Double(0, 0), null);
            final Point2D pt = map.getCanvas().getController().getTransform().inverseTransform(new Point2D.Double(dx, dy), null);
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


    public Geometry insertNode(final Polygon geo, final int mx, final int my) {
        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            final Geometry mousePoint = toJTS(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length-1; j<m; j++) {
                    //find the segment that intersect
                    final Coordinate coo1 = coos[j];
                    final Coordinate coo2 = coos[j+1];
                    final Geometry segment = createLine(coo1,coo2);

                    if(mouseGeo.intersects(segment) && segment.getEnvelope().intersects(mousePoint)){
                        //we must add the new node on this segment

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,p=coos.length; d<p; d++) {
                            ncs.add(coos[d]);
                            if(d==j){
                                //we must add the new node here
                                ncs.add(mousePoint.getCoordinate());
                            }
                         }

                        return createPolygon(ncs);
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }

    public Geometry insertNode(final LineString geo, final int mx, final int my) {
        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            final Geometry mousePoint = toJTS(mx, my);

            if (geo.intersects(mouseGeo)) {
                //this geometry intersect the mouse

                final Coordinate[] coos = geo.getCoordinates();
                for (int j=0,m=coos.length-1; j<m; j++) {
                    //find the segment that intersect
                    final Coordinate coo1 = coos[j];
                    final Coordinate coo2 = coos[j+1];
                    final Geometry segment = createLine(coo1,coo2);

                    if(mouseGeo.intersects(segment) && segment.getEnvelope().intersects(mousePoint)){
                        //we must add the new node on this segment

                        final List<Coordinate> ncs = new ArrayList<Coordinate>();
                        for (int d=0,p=coos.length; d<p; d++) {
                            ncs.add(coos[d]);
                            if(d==j){
                                //we must add the new node here
                                ncs.add(mousePoint.getCoordinate());
                            }
                         }

                        return createLine(ncs);
                    }
                }
            }
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }

        return geo;
    }


    public Geometry insertNode(final GeometryCollection geo, final int mx, final int my) {
        try{
            //transform our mouse in a geometry
            final Geometry mouseGeo = mousePositionToGeometry(mx, my);
            final Geometry mousePoint = toJTS(mx, my);

            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
                final Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    //this geometry intersect the mouse

                    final Coordinate[] coos = subgeo.getCoordinates();
                    for (int j=0,m=coos.length-1; j<m; j++) {
                        //find the segment that intersect
                        final Coordinate coo1 = coos[j];
                        final Coordinate coo2 = coos[j+1];
                        final Geometry segment = createLine(coo1,coo2);

                        if(mouseGeo.intersects(segment) && segment.getEnvelope().intersects(mousePoint)){
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
                                 return createMultiLine(subs);
                            }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                                 return createMultiPolygon(subs);
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

    public Geometry deleteNode(final Polygon geo, final int mx, final int my) {

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
                            return createPolygon(ncs);
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

    public Geometry deleteNode(final LineString geo, final int mx, final int my) {

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
                            return createLine(ncs);
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


    public Geometry deleteNode(final GeometryCollection geo, final int mx, final int my) {

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
                                 return createMultiLine(subs);
                            }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                                 return createMultiPolygon(subs);
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

    public Geometry deleteSubGeometry(final GeometryCollection geo, final int mx, final int my) {

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
                         return createMultiLine(subs);
                    }else if(geo instanceof MultiPolygon && !subs.isEmpty()){
                         return createMultiPolygon(subs);
                    }else if(geo instanceof MultiPoint && !subs.isEmpty()){
                         return createMultiPoint(subs);
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

    
    public Geometry deleteHole(final Geometry geo, final int mx, final int my) {
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
                        return GEOMETRY_FACTORY.createPolygon((LinearRing)subgeo.getExteriorRing(), 
                                holes.toArray(new LinearRing[holes.size()]));
                    }else if(geo instanceof MultiPolygon){
                        //modify the subgeometry
                        final Polygon poly = GEOMETRY_FACTORY.createPolygon((LinearRing)subgeo.getExteriorRing(), 
                                holes.toArray(new LinearRing[holes.size()]));
                        subGeometries.set(i, poly);
                        //recreate the multipolygon
                        return createMultiPolygon(subGeometries);
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
        final Object obj = sf.getDefaultGeometryProperty().getValue();

        if (obj instanceof Geometry) {
            return toObjectiveCRS((Geometry)obj);
        }
        return null;
    }
    
    public Geometry toObjectiveCRS(Geometry geom){
        try{
            final MathTransform trs = CRS.findMathTransform(
                    editedLayer.getCollection().getFeatureType().getCoordinateReferenceSystem(),
                    map.getCanvas().getObjectiveCRS(), true);

            geom = JTS.transform(geom, trs);
            return geom;
        }catch(final Exception ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
        }
        return null;
    }
    

    /**
     *
     * @param geom : in canvas objective CRS
     * @param layer : target layer filter
     * @return geometry filter
     */
    public Filter toFilter(final Geometry poly, final FeatureMapLayer fl) throws FactoryException, MismatchedDimensionException, TransformException{

        final String geoStr = fl.getCollection().getFeatureType().getGeometryDescriptor().getLocalName();
        final Expression geomField = FF.property(geoStr);

        final CoordinateReferenceSystem dataCrs = fl.getCollection().getFeatureType().getCoordinateReferenceSystem();

        final Geometry dataPoly = JTS.transform(poly, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
        dataPoly.setSRID(SRIDGenerator.toSRID(dataCrs, SRIDGenerator.Version.V1));

        final Expression geomData = FF.literal(dataPoly);
        final Filter f = FF.intersects(geomField, geomData);

        return f;
    }

    
    //manipulating the feature source, transaction -----------------------------

    public void sourceAddGeometry(Geometry geom) {

        if (editedLayer != null) {

            final FeatureType featureType = (FeatureType) editedLayer.getCollection().getFeatureType();
            final CoordinateReferenceSystem dataCrs = featureType.getCoordinateReferenceSystem();
            final Feature feature = FeatureUtilities.defaultFeature(featureType, UUID.randomUUID().toString());

            try {
                geom = JTS.transform(geom, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs, true));
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

            feature.getDefaultGeometryProperty().setValue(geom);
            JFeatureOutLine.show(feature);

            if(editedLayer.getCollection().isWritable()){
                try {
                    ((FeatureCollection)editedLayer.getCollection()).add(feature);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }

            map.getCanvas().getController().repaint();
        }

    }

    public void sourceModifyFeature(final Feature feature, final Geometry geo, boolean reprojectToDataCRS){

        final String ID = feature.getIdentifier().getID();

        ensureNonNull("geometry", geo);
        ensureNonNull("id", ID);

        if (editedLayer != null && editedLayer.getCollection().isWritable()) {

            final Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));
            final FeatureType featureType = editedLayer.getCollection().getFeatureType();
            final AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
            final CoordinateReferenceSystem dataCrs = featureType.getCoordinateReferenceSystem();

            try {
                final Geometry geom;
                if(reprojectToDataCRS){
                    geom = JTS.transform(geo, 
                            CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
                }else{
                    geom = geo;
                }
                
                editedLayer.getCollection().update(filter, geomAttribut,geom);
            } catch (final Exception ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
            } finally {
                map.getCanvas().getController().repaint();
            }

        }

    }
    
    public void sourceModifyFeature(final Feature feature, final Geometry geo){
        sourceModifyFeature(feature, geo, true);
    }

    public void sourceRemoveFeature(final Feature feature){
        sourceRemoveFeature(feature.getIdentifier().getID());
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

            map.getCanvas().getController().repaint();
        }

    }


    //staic helper methods -----------------------------------------------------

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
