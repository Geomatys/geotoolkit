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
    
    public static final class ProjectedReference{
        public Point reference;
        public SegmentInfo segment;
        public Coordinate[] nearests;
        public double distancePerpendicularAbs;
        public double distancePerpendicular;
        public double distanceAlongLinear;
        public boolean perpendicularProjection;
    }
    
    /**
     * Calcul de coordonée en fonction d'une position relative
     * 
     * @param geom linear geometry
     * @param reference position 
     * @param distanceAlongLinear distance along the linear
     * @param distancePerpendicular distance from the linear
     * @return position 
     */
    public static Point calculateCoordinate(Geometry geom, Point reference, 
            double distanceAlongLinear, double distancePerpendicular){
        ArgumentChecks.ensureNonNull("linear", geom);
        ArgumentChecks.ensureNonNull("reference", reference);
                
        //project reference 
        final LineString linear = asLineString(geom);
        final SegmentInfo[] segments = buildSegments(linear);
        final ProjectedReference projection = projectReference(segments, reference);
                
        //find segment at given distance
        final double distanceFinal = projection.distanceAlongLinear + distanceAlongLinear;
        final SegmentInfo segment = getSegment(segments, distanceFinal);
        
        final Point pt = GO2Utilities.JTS_FACTORY.createPoint(segment.getPoint(
                distanceFinal-segment.startDistance, distancePerpendicular));
        pt.setSRID(geom.getSRID());
        pt.setUserData(geom.getUserData());
        return pt;
    }
    
    /**
     * 
     * @param geom linear geometry
     * @param references positions
     * @param position
     * @return Entry : index of the closest reference point
     *       double[0] = distance alogn the linear
     *       double[1] = distance aside from the linear
     */
    public static Entry<Integer,double[]> calculateRelative(Geometry geom, Point[] references, Point position) {
        ArgumentChecks.ensureNonNull("linear", geom);
        final LineString linear = asLineString(geom);
        final SegmentInfo[] segments = buildSegments(linear);
        return calculateRelative(segments, references, position);
    }
    
    /**
     * 
     * @param segments The list of segments which compose source linear.
     * 
     * @param references positions
     * @param position
     * @return Entry : index of the closest reference point
     *       double[0] = distance alogn the linear
     *       double[1] = distance aside from the linear
     */
    public static Entry<Integer,double[]> calculateRelative(final SegmentInfo[] segments, Point[] references, Point position){
        ArgumentChecks.ensureNonNull("linear", segments);
        ArgumentChecks.ensureNonNull("position", position);
        ArgumentChecks.ensureNonNull("references", references);
        ArgumentChecks.ensurePositive("references", references.length);
                
        //project target
        final ProjectedReference positionProj = projectReference(segments, position);
        
        //project references and find nearest
        double distanceAlongLinear = Double.MAX_VALUE;
        int index = 0;
        
        for(int i=0;i<references.length;i++){
            final ProjectedReference projection = projectReference(segments, references[i]);
            final double candidateDistance = positionProj.distanceAlongLinear-projection.distanceAlongLinear;
            if(Math.abs(candidateDistance) < Math.abs(distanceAlongLinear)){
                index = i;
                distanceAlongLinear = candidateDistance;
            }
        }
        
        return new AbstractMap.SimpleImmutableEntry<>(index, new double[]{
            distanceAlongLinear, positionProj.distancePerpendicular});        
    }
    
    /**
     * Project a point on the linear and obtain all related information.
     * 
     * @param segments linear segments
     * @param reference position
     * @return ProjectedReference
     */
    public static ProjectedReference projectReference(SegmentInfo[] segments, Point reference){
        final ProjectedReference projection = new ProjectedReference();
        projection.reference = reference;
        
        //find the nearest segment
        projection.distancePerpendicularAbs = Double.MAX_VALUE;
        projection.distancePerpendicular = Double.MAX_VALUE;
        
        for(SegmentInfo segment : segments){
            final Coordinate[] candidateNearests = DistanceOp.nearestPoints(segment.geometry, reference);
            final double candidateDistance = candidateNearests[0].distance(candidateNearests[1]);
            if(candidateDistance<projection.distancePerpendicularAbs){
                final double side = -lineSide(segment, candidateNearests[1]);
                projection.distancePerpendicularAbs = candidateDistance;
                projection.distancePerpendicular = candidateDistance * Math.signum(side);
                projection.nearests = candidateNearests;
                projection.segment = segment;
                projection.distanceAlongLinear = segment.startDistance + 
                        segment.segmentCoords[0].distance(candidateNearests[0]);
                
                //find if point projects on segment
                projection.perpendicularProjection = projectsPerpendicular(
                        segment.segmentCoords[0], segment.segmentCoords[1], reference.getCoordinate());
                
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
                
        double cumulativeDistance = 0;
        
        for(int i=0;i<coords.length-1;i++){
            
            final SegmentInfo segment = new SegmentInfo();
            segment.startCoordIndex = i;
            segment.segmentCoords = new Coordinate[]{coords[i],coords[i+1]};
            segment.geometry = GO2Utilities.JTS_FACTORY.createLineString(segment.segmentCoords);
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
     * Test the side of a point compare to a line.
     *
     * @param SegmentInfo segment
     * @param c to test
     * @return > 0 if point is on the left side
     *          = 0 if point is on the line
     *          < 0 if point is on the right side
     */
    public static double lineSide(SegmentInfo segment, Coordinate c) {
        return (segment.segmentCoords[1].x-segment.segmentCoords[0].x) * (c.y-segment.segmentCoords[0].y) - 
               (c.x-segment.segmentCoords[0].x) * (segment.segmentCoords[1].y-segment.segmentCoords[0].y);
    }
    
    public static boolean projectsPerpendicular(final Coordinate segmentStart, final Coordinate segmentEnd, final Coordinate point){
        final Coordinate ab = subtract(segmentEnd, segmentStart,null);
        final Coordinate ac = subtract(point,segmentStart,null);
        final double e = dot(ac, ab);
        // cases where point is outside segment
        if (e <= 0.0f) return false;
        final double f = dot(ab, ab);
        if (e >= f) return false;
        // cases where point projects onto segment
        return true;
    }
    
    private static double dot(final Coordinate vector, final Coordinate other){
        double dot = 0;
        for(int i=0;i<2;i++){
            dot += vector.getOrdinate(i)*other.getOrdinate(i);
        }
        return dot;
    }
        
    private static Coordinate subtract(final Coordinate vector, Coordinate other, Coordinate buffer){
        if( buffer == null ){
            buffer = new Coordinate();
        }
        for(int i=0;i<2;i++){
            buffer.setOrdinate(i, vector.getOrdinate(i)-other.getOrdinate(i));
        }
        return buffer;
    }
    
    public static LineString asLineString(Geometry candidate) {
        LineString linear = null;
        if (candidate instanceof LineString) {
            linear = (LineString) candidate;
        } else if (candidate instanceof Polygon) {
            linear = ((Polygon)candidate).getExteriorRing();
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
