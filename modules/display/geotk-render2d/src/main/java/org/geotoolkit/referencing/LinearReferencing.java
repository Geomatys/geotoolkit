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
package org.geotoolkit.referencing;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.vecmath.Vector2d;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.jts.JTSLineIterator;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.math.XMath;

/**
 * Linear referencing utilities.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class LinearReferencing extends Static{

    public static final class SegmentInfo{
        public int startCoordIndex;
        public double startDistance;
        public double endDistance;
        public double length;
        public Coordinate[] segmentCoords;
        public LineString geometry;
        public Vector2d forward;
        public Vector2d right;
        
        public Coordinate getPoint(double distanceAlongLinear, double distancePerpendicular){
            final Vector2d tempForward = new Vector2d();
            final Vector2d tempPerpendicular = new Vector2d();
            tempForward.scale(distanceAlongLinear, forward);
            tempPerpendicular.scale(distancePerpendicular, right);
            return new Coordinate(
                segmentCoords[0].x+tempForward.x+tempPerpendicular.x, 
                segmentCoords[0].y+tempForward.y+tempPerpendicular.y);
        }
        
    }
    
    /**
     * Information about a point after its reprojection on a linear object.
     */
    public static final class ProjectedPoint {
        /** Source point, before projection. */
        public Point reference;
        
        /** 
         * Index of the segment in source segment array used for projection. Be 
         * careful, this information remains valid only while you keep source 
         * segment array unchanged.
         */
        public int segmentIndex = -1;
        
        /** Segment on which the point has been projected. */
        public SegmentInfo segment;
        
        /** Coordinate of the point after projection. */
        public Coordinate projected;
        
        /**
         * Distance from the start of the origin linear object (not just current segment)
         * and the point after projection on this linear.
         */
        public double distanceAlongLinear;
    }
    
    /**
     * Compute geographic / projected position from linear information.
     * 
     * @param geom Reference linear. If it's not a {@link LineString}, it will be 
     * converted as specified by {@link #asLineString(com.vividsolutions.jts.geom.Geometry) } method.
     * @param reference A point on reference linear.
     * @param distanceAlongLinear A distance (can be negative to rewind on linear)
     * from reference point along input linear.
     * @param distancePerpendicular distance from the linear (perpendicularly to it) 
     * for output point.
     * @return position Found point, whose projection is thee same as input linear.
     */
    public static Point computeCoordinate(Geometry geom, Point reference, 
            double distanceAlongLinear, double distancePerpendicular){
        ArgumentChecks.ensureNonNull("linear", geom);
        ArgumentChecks.ensureNonNull("reference", reference);
        return computeCoordinate(buildSegments(asLineString(geom)), reference, distanceAlongLinear, distancePerpendicular);
    }
    
    /**
     * Compute geographic / projected position from linear information.
     * 
     * @param segments Reference linear, cut as a succession of segments.
     * @param reference A point on reference linear.
     * @param distanceAlongLinear A distance (can be negative to rewind on linear)
     * from reference point along input linear.
     * @param distancePerpendicular distance from the linear (perpendicularly to it) 
     * for output point.
     * @return A geographic or projected (depends on input linear CRS) point defined 
     * by input parameters.
     */
    public static Point computeCoordinate(final SegmentInfo[] segments, Point reference, 
            double distanceAlongLinear, double distancePerpendicular) {
        ArgumentChecks.ensureNonNull("linear", segments);
        ArgumentChecks.ensureNonNull("reference", reference);
        //project reference
        final ProjectedPoint projection = projectReference(segments, reference);
                
        //find segment at given distance
        final double distanceFinal = projection.distanceAlongLinear + distanceAlongLinear;
        final SegmentInfo segment = getSegment(segments, distanceFinal);
        
        final Point pt = GO2Utilities.JTS_FACTORY.createPoint(segment.getPoint(
                distanceFinal-segment.startDistance, distancePerpendicular));
        pt.setSRID(segment.geometry.getSRID());
        pt.setUserData(segment.geometry.getUserData());
        return pt;
    }
    
    /**
     * 
     * @param geom linear geometry
     * @param references positions
     * @param position
     * @return Entry : Key = index of the closest reference point
     *                 Value = distance along the linear
     */
    public static Entry<Integer, Double> computeRelative(Geometry geom, Point[] references, Point position) {
        ArgumentChecks.ensureNonNull("linear", geom);
        final LineString linear = asLineString(geom);
        final SegmentInfo[] segments = buildSegments(linear);
        return computeRelative(segments, references, position);
    }
    
    /**
     * 
     * @param segments The list of segments which compose source linear.
     * 
     * @param references positions
     * @param position
     * @return Entry : Key = index of the closest reference point
     *                 Value = distance along the linear
     */
    public static Entry<Integer, Double> computeRelative(final SegmentInfo[] segments, Point[] references, Point position){
        ArgumentChecks.ensureNonNull("linear", segments);
        ArgumentChecks.ensureNonNull("position", position);
        ArgumentChecks.ensureNonNull("references", references);
        ArgumentChecks.ensurePositive("references", references.length);
                
        //project target
        final ProjectedPoint positionProj = projectReference(segments, position);
        
        //project references and find nearest
        double distanceAlongLinear = Double.MAX_VALUE;
        int index = 0;
        
        for(int i=0;i<references.length;i++){
            final ProjectedPoint projection = projectReference(segments, references[i]);
            final double candidateDistance = positionProj.distanceAlongLinear-projection.distanceAlongLinear;
            if(Math.abs(candidateDistance) < Math.abs(distanceAlongLinear)){
                index = i;
                distanceAlongLinear = candidateDistance;
            }
        }
        
        return new AbstractMap.SimpleImmutableEntry<>(index, distanceAlongLinear);        
    }
    
    /**
     * Project a point on the linear and obtain all related information.
     * 
     * @param segments linear segments
     * @param reference position
     * @return ProjectedReference Never null.
     */
    public static ProjectedPoint projectReference(SegmentInfo[] segments, Point reference){
        final ProjectedPoint projection = new ProjectedPoint();
        projection.reference = reference;
        
        //find the nearest segment
        double minDistance = Double.MAX_VALUE;
        
        SegmentInfo segment;
        for (int i = 0 ; i < segments.length ; i++) {
            segment = segments[i];
            final Coordinate[] candidateNearests = DistanceOp.nearestPoints(segment.geometry, reference);
            final double candidateDistance = candidateNearests[0].distance(candidateNearests[1]);
            
            if(candidateDistance<minDistance){
                minDistance = candidateDistance;
                projection.projected = candidateNearests[0];
                projection.segment = segment;
                projection.segmentIndex = i;
                projection.distanceAlongLinear = segment.startDistance + 
                        segment.segmentCoords[0].distance(projection.projected);
            }
        }
        
        return projection;
    }
    
    /**
     * Split the geometry in a serie of line strings.
     * 
     * @param linear geometry
     * @return SegmentInfo[] linear segments
     */
    public static SegmentInfo[] buildSegments(final LineString linear){
        
        final Coordinate[] coords = linear.getCoordinates();
        final SegmentInfo[] segments = new SegmentInfo[coords.length-1];
        
        final int srid = linear.getSRID();
        final Object userData = linear.getUserData();
        
        double cumulativeDistance = 0;        
        for (int i = 0; i < coords.length - 1; i++) {
            
            final SegmentInfo segment = new SegmentInfo();
            segment.startCoordIndex = i;
            segment.segmentCoords = new Coordinate[]{coords[i],coords[i+1]};
            segment.geometry = GO2Utilities.JTS_FACTORY.createLineString(segment.segmentCoords);
            segment.geometry.setSRID(srid);
            segment.geometry.setUserData(userData);
            segment.length = segment.segmentCoords[0].distance(segment.segmentCoords[1]);
            segment.startDistance = cumulativeDistance;
            cumulativeDistance += segment.length;
            segment.endDistance = cumulativeDistance;
            
            //calculate direction vectors
            segment.forward = new Vector2d(
                    segment.segmentCoords[1].x-segment.segmentCoords[0].x, 
                    segment.segmentCoords[1].y-segment.segmentCoords[0].y);
            segment.forward.normalize();
            segment.right = new Vector2d(segment.forward.y,-segment.forward.x);
            
            segments[i] = segment;
        }
                
        return segments;
    }
    
    /**
     * Find nearest segment to given distance.
     * 
     * @param segments
     * @param distance
     * @return SegmentInfo
     */
    public static SegmentInfo getSegment(SegmentInfo[] segments, double distance){
        SegmentInfo segment = segments[0];
        for(int i=1;i<segments.length;i++){
            
            if(segments[i].startDistance < distance){
                segment = segments[i];
            }else{
                break;
            }
        }
        return segment;
    }
    
    /**
     * Cast or convert a given geometry into a {@link LineString}. If input geometry
     * is :
     * - A linear, it's returned as is. 
     * - A multipoint, a line composed of all points, in their order in the collection, 
     * is returned. 
     * - A geometry collections (other than multipoint), we will convert its first geometry.
     * - A polygon, its exterior ring is used as line string.
     * - A point, we return a line string which is a segment whose start and end points
     *  are one and the same. 
     * 
     * Otherwise a null value is returned.
     * 
     * @param candidate The geometry to convert.
     * @return The resulting linear, or null.
     */
    public static LineString asLineString(Geometry candidate) {
        LineString linear = null;
        if (candidate instanceof LineString) {
            linear = (LineString) candidate;
        } else if (candidate instanceof Polygon) {
            linear = ((Polygon)candidate).getExteriorRing();
        } else if (candidate instanceof Point) {
            Coordinate coordinate = candidate.getCoordinate();
            return GO2Utilities.JTS_FACTORY.createLineString(new Coordinate[]{coordinate, coordinate});
        } else if (candidate instanceof MultiPoint) {
            return GO2Utilities.JTS_FACTORY.createLineString(((candidate).getCoordinates()));
        } else if (candidate instanceof GeometryCollection) {
            final GeometryCollection gc = (GeometryCollection) candidate;
            final int nb = gc.getNumGeometries();
            if(nb>0){
                linear = asLineString(gc.getGeometryN(0));
            }
        }
        return linear;
    }
    
    public static LineString cut(LineString linear, double distanceDebut, double distanceFin){
        
        //ensure we don't go out of the linear
        final double linearLength = linear.getLength();
        distanceDebut = XMath.clamp(distanceDebut, 0, linearLength);
        distanceFin = XMath.clamp(distanceFin, 0, linearLength);

        //cut linear at given distances
        final PathIterator ite = new JTSLineIterator(linear, null);
        final PathWalker walker = new PathWalker(ite);
        walker.walk((float)distanceDebut);
        float remain = (float) (distanceFin-distanceDebut);

        final List<Coordinate> structureCoords = new ArrayList<>();
        Point2D point = walker.getPosition(null);
        structureCoords.add(new Coordinate(point.getX(), point.getY()));

        while(!walker.isFinished() && remain>0){
            final float advance = Math.min(walker.getSegmentLengthRemaining(), remain);
            remain -= advance;
            walker.walk(advance);
            point = walker.getPosition(point);
            structureCoords.add(new Coordinate(point.getX(), point.getY()));
        }

        if(structureCoords.size()==1){
            //we need at least 2 points
            structureCoords.add(new Coordinate(structureCoords.get(0)));
        }

        final LineString geom = GO2Utilities.JTS_FACTORY.createLineString(structureCoords.toArray(new Coordinate[structureCoords.size()]));
        geom.setSRID(linear.getSRID());
        geom.setUserData(linear.getUserData());
        return geom;
    }
    
}
