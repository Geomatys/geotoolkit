/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * An utilies class containing methods to calculate distance between geometric object.
 * @author Guilhem Legal
 * @module pending
 */
public class GeometricUtilities {
    
    /**
     *  WGS 1984 ellipsoid with axis in {@linkplain SI#METRE metres}.
     */
    private static final DefaultEllipsoid DE = DefaultEllipsoid.WGS84;

    private GeometricUtilities() {}
    /**
     * Return the shortest orthodromic distance between a boundingBox and a point.
     * if the boundingBox contains the point it return 0.
     * 
     * @param boundingBox A bounding box
     * @param point       A point in the same CRS that the boundingBox.
     * @param units       The units in which the distance will be expressed.
     * 
     * @return the shortest distance between the bounding box and the point.
     */
    public static double bboxToPointDistance(final GeneralEnvelope boundingBox, final GeneralDirectPosition point, final String units) {
        if (boundingBox.contains(point))
            return 0;
        
        final List<Line2D> border = getBorder(boundingBox);
        double distance     = Double.MAX_VALUE;
        
        for (Line2D l: border) {
            
             final double tempDistance = lineToPointDistance(l, point, units);
             if (tempDistance < distance)
                 distance = tempDistance;
        }
        return distance;
    }
    
    /**
     * Return the shortest distance between a line and a point
     * 
     * @param line  A line2D. 
     * @param point A point in the same CRS than the line.
     * @param units The units in which the distance will be expressed.
     * 
     * @return the shortest distance between the bounding box and the point.
     */
    public static double lineToPointDistance(final Line2D line, final GeneralDirectPosition point, final String units) {

        final Point2D pt = ShapeUtilities.nearestColinearPoint(line, new Point2D.Double(point.getOrdinate(0), point.getOrdinate(1)));
                
        return getOrthodromicDistance(point.getOrdinate(0), point.getOrdinate(1),
                                      pt.getX(), pt.getY(), units);
    }
   
    /**
     * Return the shortest distance between two segment.
     * 
     * @param line1 The first segment.
     * @param line2 The second segment.
     * @param units The units in which the distance will be expressed.
     * 
     * @return the shortest distance between the two segment.
     */
    public static double lineTolineDistance(final Line2D line1, final Line2D line2, final String units) {
        double distance = Double.MAX_VALUE;
        if (line1.intersectsLine(line2)) {
            return 0;
        }
        
        Point2D pt = ShapeUtilities.nearestColinearPoint(line2, line1.getP1());
        double tempDistance = getOrthodromicDistance(pt.getX(), pt.getY(), line1.getX1(), line1.getY1(), units);
        if (tempDistance < distance)
            distance = tempDistance;
        
        pt = ShapeUtilities.nearestColinearPoint(line2, line1.getP2());
        tempDistance = getOrthodromicDistance(pt.getX(), pt.getY(), line1.getX2(), line1.getY2(), units);
        if (tempDistance < distance)
            distance = tempDistance;
        
        pt = ShapeUtilities.nearestColinearPoint(line1, line2.getP1());
        tempDistance = getOrthodromicDistance(pt.getX(), pt.getY(), line2.getX1(), line2.getY1(), units);
        if (tempDistance < distance)
            distance = tempDistance;
        
        pt = ShapeUtilities.nearestColinearPoint(line1, line2.getP2());
        tempDistance = getOrthodromicDistance(pt.getX(), pt.getY(), line2.getX2(), line2.getY2(), units);
        if (tempDistance < distance)
            distance = tempDistance;
        return distance;
    }
    
    /**
     * Return the shortest distance between a line and a boundingBox.
     * If the line intersect or is contains by the bounding box it return 0;
     * 
     * @param line        A line2D.
     * @param boundingBox A bounding box in the same CRS than the line
     * @param units       The units in which the distance will be expressed.
     * @return The shortest distance between the bounding box and the line.
     */
    public static double lineToBBoxDistance(final Line2D line, final GeneralEnvelope boundingBox, final String units) {
        
        final CoordinateReferenceSystem crs = boundingBox.getCoordinateReferenceSystem();
        final GeneralDirectPosition tempPoint1 = new GeneralDirectPosition(line.getX1(), line.getY1());
        tempPoint1.setCoordinateReferenceSystem(crs);
        final GeneralDirectPosition tempPoint2 = new GeneralDirectPosition(line.getX2(), line.getY2());
        tempPoint2.setCoordinateReferenceSystem(crs);
        
        if (boundingBox.contains(tempPoint1) || boundingBox.contains(tempPoint2))
            return 0;
        
        final List<Line2D> border = getBorder(boundingBox);
        double distance     = Double.MAX_VALUE;
        
        for (Line2D l: border) {
            if (l.intersectsLine(line))
                return 0;
             double tempDistance = lineTolineDistance(l, line, units);
             if (tempDistance < distance)
                 distance = tempDistance;
        }
        return distance;
    }
    
    /**
     * Return the shortest distance between two bounding box.
     * if the two Bounding box intersect it return 0.
     * 
     * @param boundingBox1 The first bounding box.
     * @param boundingBox2 The second bounding box.
     * @param units        The units in which the distance will be expressed.
     * 
     * @return The shortest distance between the bounding box and the line.
     */
     public static double bboxToBBoxDistance(final GeneralEnvelope boundingBox1, final GeneralEnvelope boundingBox2, final String units) {
        if (boundingBox1.intersects(boundingBox2, false))
            return 0;
        List<Line2D> border = getBorder(boundingBox1);
        double distance     = Double.MAX_VALUE;
        for (Line2D l: border) {
            
             double tempDistance = lineToBBoxDistance(l, boundingBox2, units);
             if (tempDistance < distance)
                 distance = tempDistance;
        }
        return distance;
     }
            
    /**
     * Return the orthodromic distance between two point in the specified units.
     * 
     * @param x1 latitude  for point 1.
     * @param y1 longitude for point 1.
     * @param x2 latitude  for point 2.
     * @param y2 longitude for point 2.
     * @param units the units on which you want the distance be expressed.
     *              supported units are: meters, kilometers, miles.
     * 
     * @return The distance between 2 points on earth expressed in the specified units
     */
    public static double getOrthodromicDistance(final double x1, final double y1, final double x2, final double y2, final String units) {
        final double result = DE.orthodromicDistance(y1, x1, y2, x2);
        if (units.equals("meters") || units.equals("m"))
            return result;
        
        else if (units.equals("kilometers") || units.equals("km")) 
            return result/1000;
        
        else if (units.equals("centimeters") || units.equals("cm")) 
            return result * 100;
        
        else if (units.equals("milimeters") || units.equals("mm")) 
            return result * 1000;
        
        else if (units.equals("miles") || units.equals("mi"))  
            return (result*0.621371) /1000;
        
        else
            throw new IllegalArgumentException("unknow distance units");
    }
    
    /**
     * return a list of Line2D composing the border of the specified geometry.
     * 
     * @param geometry a geomtry object. supported one are: GeneralEnvelope
     * @return a list ordered like following: height-left border, width-top border, height-right border, width-bottom border.
     */
    public static List<Line2D> getBorder(final Object geometry) {
        final List<Line2D> result = new ArrayList<Line2D>();
        if (geometry instanceof GeneralEnvelope) {
            final GeneralEnvelope env = (GeneralEnvelope) geometry;
            final Line2D heightLeft  = new Line2D.Double(env.getMinimum(0), env.getMaximum(1), env.getMinimum(0), env.getMinimum(1));
            result.add(heightLeft);
            final Line2D widthTop    = new Line2D.Double(env.getMinimum(0), env.getMaximum(1), env.getMaximum(0), env.getMaximum(1));
            result.add(widthTop);
            final Line2D heightRight = new Line2D.Double(env.getMaximum(0), env.getMaximum(1), env.getMaximum(0), env.getMinimum(1));
            result.add(heightRight);
            final Line2D widthBottom = new Line2D.Double(env.getMinimum(0), env.getMinimum(1), env.getMaximum(0), env.getMinimum(1));
            result.add(widthBottom);
        }
        
        return result;
    }
    
    /**
     * return a list of Point2D composing the corner of the specified geometry.
     * 
     * @param geometry a geometry object. supported one are: GeneralEnvelope
     * 
     * @return a list ordered like following: upper-left corner, upper-right corner, bottom-right corner, bottom-left corner.
     */
    public static List<Point2D> getCorner(final Object geometry) {
        final List<Point2D> result = new ArrayList<Point2D>();
        if (geometry instanceof GeneralEnvelope) {
            final GeneralEnvelope env = (GeneralEnvelope) geometry;
            final Point2D upperLeft   = new Point2D.Double(env.getMinimum(0), env.getMaximum(1));
            result.add(upperLeft);
            final Point2D upperRight  = new Point2D.Double(env.getMaximum(0), env.getMaximum(1));
            result.add(upperRight);
            final Point2D bottomRight = new Point2D.Double(env.getMaximum(0), env.getMinimum(1));
            result.add(bottomRight);
            final Point2D bottomLeft  = new Point2D.Double(env.getMinimum(0), env.getMinimum(1));
            result.add(bottomLeft);
        }
        
        return result;
    }
    
    /**
     * Return true if the specified point is located on the border of the specified envelope.
     * if one of the parameters is null it return false.
     * 
     * @param boundingBox A GeneralEnvelope.
     * @param point       A GeneralDirectPosition
     * 
     * @return true if the point is on the border of the envelope.
     */
    public static boolean touches(final GeneralEnvelope boundingBox, final GeneralDirectPosition point) {
        if (point == null || boundingBox == null)
            return false;
        
        final Line2D pointLine = new Line2D.Double(point.getOrdinate(0), point.getOrdinate(1),
                                             point.getOrdinate(0), point.getOrdinate(1));
        final List<Line2D> border = getBorder(boundingBox);
        for (Line2D l: border) {
            if (l.intersectsLine(pointLine))
                return true;
        }
        return false;
    }
    
    /**
     * Return true if the two line are in touche relation.
     * i.e. one of the extremity of a line is a point of the other.
     *
     * if one of the parameters is null it return false.
     * 
     * @param line1 The first line2D.
     * @param line2 The second line2D.
     * 
     * @return true if one of the extremity of a line is a point of the other.
     */
    public static boolean touches(final Line2D line1, final Line2D line2) {
        if (line1 == null || line2 == null)
            return false;
        
        if (line1.intersectsLine(new Line2D.Double(line2.getX1(), line2.getY1(), line2.getX1(), line2.getY1())) || 
            line1.intersectsLine(new Line2D.Double(line2.getX2(), line2.getY2(), line2.getX2(), line2.getY2())) ||
            line2.intersectsLine(new Line2D.Double(line1.getX1(), line1.getY1(), line1.getX1(), line1.getY1())) ||
            line2.intersectsLine(new Line2D.Double(line1.getX2(), line1.getY2(), line1.getX2(), line1.getY2())))
              return true;
        
        return false;
    }
    
    /**
     * Return true if the specified line and the envelope are in touche relation.
     * i.e. one of the extremity of the line is a point of the border of the envelope.
     *
     * if one of the parameters is null it return false.
     * 
     * @param boundingBox The bounding box.
     * @param line The line2D.
     * 
     * @return boolean
     */
    public static boolean touches(final GeneralEnvelope boundingBox, final Line2D line) {
        if (line == null || boundingBox == null)
            return false;
        
        final List<Line2D> border = getBorder(boundingBox);
        for (Line2D l: border) {
            if (l.intersectsLine(line.getX1(), line.getY1(), line.getX1(), line.getY1()) ||
                l.intersectsLine(line.getX2(), line.getY2(), line.getX2(), line.getY2()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Return true if the envelopes are in touche relation.
     * i.e. TODO fill
     *
     * if one of the parameters is null it return false.
     * 
     * @param boundingBox1 The first envelope.
     * @param boundingBox2 The second envelope.
     * 
     * @return boolean
     */
    public static boolean touches(final GeneralEnvelope boundingBox1, final GeneralEnvelope boundingBox2) {
        if (boundingBox1 == null || boundingBox2 == null)
            return false;
        
        boolean touches = false;

        // we look if one of the corner of the second bounding Box touch a border of the first box
        final List<Line2D> bboxBorder1 = getBorder(boundingBox1);
        for (Line2D l : bboxBorder1) {
            for (Point2D p : getCorner(boundingBox2)) {
                if (l.intersectsLine(p.getX(), p.getY(), p.getX(), p.getY())) {
                    touches = true;
                }
            }
        }

        // then we look if one of the corner of the first bounding box touch one border of the second box
        final List<Point2D> filterBoxCorner = GeometricUtilities.getCorner(boundingBox1);
        for (Line2D l : getBorder(boundingBox2)) {
            for (Point2D p : filterBoxCorner) {
                if (l.intersectsLine(p.getX(), p.getY(), p.getX(), p.getY())) {
                    touches = true;
                }
            }
        }
        return touches;
    }
    
    /**
     * Return true if the intersection between the envelope and the line is empty.
     * 
     * @param boundingBox An envelope.
     * @param line        A line2D
     * 
     * @return True f the intersection between the envelope and the point is empty.
     */
    public static boolean disjoint(final GeneralEnvelope boundingBox, final Line2D line) {
        final GeneralDirectPosition tempPoint1 = new GeneralDirectPosition(line.getX1(), line.getY1());
        final GeneralDirectPosition tempPoint2 = new GeneralDirectPosition(line.getX2(), line.getY2());
            
        if (!boundingBox.contains(tempPoint1) && !boundingBox.contains(tempPoint2)) {
            final List<Line2D> border = getBorder(boundingBox);
            for (Line2D l : border) {
                if (l.intersectsLine(line)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Return true if the intersection between the envelope and the line is not empty.
     * 
     * @param boundingBox An envelope.
     * @param line        A line2D
     * 
     * @return True f the intersection between the envelope and the point is empty.
     */
    public static boolean intersect(final GeneralEnvelope boundingBox, final Line2D line) {
        final GeneralDirectPosition tempPoint1 = new GeneralDirectPosition(line.getX1(), line.getY1());
        final GeneralDirectPosition tempPoint2 = new GeneralDirectPosition(line.getX2(), line.getY2());
        if (boundingBox.contains(tempPoint1) || boundingBox.contains(tempPoint2))
            return true;
        
        for (Line2D l: getBorder(boundingBox)) {
            if (l.intersectsLine(line))
                return true;
        }
        return false;
    }
    
    /**
     * Return true if the line crosses the envelope.
     * i.e. one point is inside the box, one is outside.
     * 
     * @param boundingBox An envelope.
     * @param line        A line2D
     * 
     * @return True if the line crosses the envelope.
     */
    public static boolean crosses(GeneralEnvelope boundingBox, Line2D line) {
        final CoordinateReferenceSystem crs = boundingBox.getCoordinateReferenceSystem();
        final GeneralDirectPosition tempPoint1 = new GeneralDirectPosition(line.getX1(), line.getY1());
        tempPoint1.setCoordinateReferenceSystem(crs);
        final GeneralDirectPosition tempPoint2 = new GeneralDirectPosition(line.getX2(), line.getY2());
        tempPoint2.setCoordinateReferenceSystem(crs);
        
        // for this case we look if the line have a point inside and a point outside
        if ((boundingBox.contains(tempPoint1) && !boundingBox.contains(tempPoint2)) || 
            (!boundingBox.contains(tempPoint1) && boundingBox.contains(tempPoint2)) )
            return true;
        return false;
    }
    
    /**
     * Return true if the point crosses the envelope.
     * i.e. the point is on the border of the envelope.
     * 
     * @param boundingBox An envelope.
     * @param point       A GeneralDirectPosition.
     * 
     * @return True if the point crosses the envelope.
     */
    public static boolean crosses(GeneralEnvelope boundingBox, GeneralDirectPosition point) {
        for (Line2D l : getBorder(boundingBox)) {
            if (l.intersectsLine(point.getOrdinate(0), point.getOrdinate(1), point.getOrdinate(0), point.getOrdinate(1))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return true if the bounding box contain the line.
     * i.e. the two point of the line are inside the box.
     * 
     * @param boundingBox An envelope.
     * @param line        A line2D
     * 
     * @return True if the envelope contain the line.
     */
    public static boolean contains(GeneralEnvelope boundingBox, Line2D line) {
        final CoordinateReferenceSystem crs = boundingBox.getCoordinateReferenceSystem();
        final GeneralDirectPosition tempPoint1 = new GeneralDirectPosition(line.getX1(), line.getY1());
        tempPoint1.setCoordinateReferenceSystem(crs);
        final GeneralDirectPosition tempPoint2 = new GeneralDirectPosition(line.getX2(), line.getY2());
        tempPoint2.setCoordinateReferenceSystem(crs);
        if ((boundingBox.contains(tempPoint1) && boundingBox.contains(tempPoint2)))
            return true;
        return false;
    }
    
    /**
     * Return true if the two bounding box are in overlaps relation.
     * i.e. the two box intersect but are not contained in each other.
     * 
     * @param boundingBox1 An envelope.
     * @param boundingBox2 An envelope.
     * 
     * @return True if the two envelope are overlaping.
     */
    public static boolean overlaps(GeneralEnvelope boundingBox1, GeneralEnvelope boundingBox2) {
        
        if ((boundingBox1.contains(boundingBox2, true) || boundingBox2.contains(boundingBox1, true)))
            return false;
        if (boundingBox1.intersects(boundingBox2, true))
            return true;
        
        return false;
    }
    
    /**
     * Utilities method witch compare two Line2D (replacing Line2D.equals())
     * 
     * @param l1 The first line to compare.
     * @param l2 The second line two compare.
     * 
     * @return true if the coordinate of the two line are equals, or if the two line are null.
     */
    public static boolean equalsLine(final Line2D l1, final Line2D l2) {
        if (l1 == null && l2 == null) {
            return true;
        } else if (l1 == null || l2 == null) {
            return false;
        } else {
            return l1.getX1() == l2.getX1() &&
                   l1.getX2() == l2.getX2() &&
                   l1.getY1() == l2.getY1() &&
                   l1.getY2() == l2.getY2();
        }
    }
    
    /**
     * Utilities method which write a line2D in a string replacing the Line2D.toString method.
     * 
     * @param line A line to describe.
     * 
     * @return A String representation of the line in the following format: x1=... y1=... x2=... y2=... 
     */
    public static String logLine2D(final Line2D line) {
        if (line == null) {
            return "null";
        }
        return "x1=" + line.getX1() + " y1=" + line.getY1() + " x2=" + line.getX2() + " y2=" + line.getY2();
    }
    
    /**
     * Reproject the specified geometric object From the sourceCRS to the targetCRS.
     * 
     * @param targetCRSName The coordinate reference system in which we have to reproject the geometry. 
     * @param sourceCRSName The current coordinate reference system of the geometry.
     * @param geometry the geometric object to reproject.
     * 
     * @return The geometric object reprojected in the target coordinate reference system.
     * 
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static Object reprojectGeometry(final String targetCRSName, final String sourceCRSName, Object geometry) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        final CoordinateReferenceSystem targetCRS = CRS.decode(targetCRSName,true);
        final CoordinateReferenceSystem sourceCRS = CRS.decode(sourceCRSName,true);
        
        if (geometry instanceof GeneralEnvelope) {
            final GeneralEnvelope env = (GeneralEnvelope) geometry;
            if (env.getCoordinateReferenceSystem() == null) {
                env.setCoordinateReferenceSystem(sourceCRS);
            }
            return CRS.transform((GeneralEnvelope) geometry, targetCRS);

        } else if (geometry instanceof GeneralDirectPosition) {
            final CoordinateOperationFactory factory = CRS.getCoordinateOperationFactory(true);
            final CoordinateOperation operation;
            try {
                operation = factory.createOperation(sourceCRS, targetCRS);
            } catch (FactoryException exception) {
                throw new TransformException("transform exception: " + exception.getMessage());
            }

            final MathTransform mt = operation.getMathTransform();
            mt.transform((GeneralDirectPosition) geometry, (GeneralDirectPosition) geometry);
            return geometry;

        } else if (geometry instanceof Line2D) {
            final Line2D line = (Line2D) geometry;
            final GeneralDirectPosition pt1 = new GeneralDirectPosition(line.getX1(), line.getY1());
            final GeneralDirectPosition pt2 = new GeneralDirectPosition(line.getX2(), line.getY2());

            final CoordinateOperationFactory factory = CRS.getCoordinateOperationFactory(true);
            final CoordinateOperation operation;
            try {
                operation = factory.createOperation(sourceCRS, targetCRS);
            } catch (FactoryException exception) {
                throw new TransformException("transform exception: " + exception.getMessage());
            }

            final MathTransform mt = operation.getMathTransform();
            mt.transform(pt1, pt1);
            mt.transform(pt2, pt2);
            return new Line2D.Double(pt1.getOrdinate(0), pt1.getOrdinate(1), pt2.getOrdinate(0), pt2.getOrdinate(1));
        } else {
            throw new IllegalArgumentException("Unknow geometry types: allowed ones are: GeneralEnvelope, Line2D, GeneralDirectPosition");
        }
    }
     /**
     * Reproject the specified bbox string From the sourceCRS to the targetCRS.
     * 
     * @param targetCRSName The coordinate reference system in which we have to reproject the geometry. 
     * @param sourceCRSName The current coordinate reference system of the geometry.
     * @param boundingBox the bbox object to reproject as a string.
     * 
     * @return The geometric object reprojected in the target coordinate reference system.
     * 
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static Object reprojectBbox2DString(final String sourceCRSName, final String targetCRSName,  String boundingBox) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
      
      final String[] bbox = boundingBox.split(",");
      double[] lowerCorner ={Double.parseDouble(bbox[1]),Double.parseDouble(bbox[0])};
      double[] upperCorner ={Double.parseDouble(bbox[3]),Double.parseDouble(bbox[2])};  
      GeneralEnvelope env = new GeneralEnvelope(lowerCorner,upperCorner);
      env.setCoordinateReferenceSystem( CRS.decode(sourceCRSName,true));
      env = (GeneralEnvelope) reprojectGeometry(targetCRSName, sourceCRSName, env);
      lowerCorner = env.getLowerCorner().getCoordinate();
      upperCorner = env.getUpperCorner().getCoordinate();
      return lowerCorner[0]+","+lowerCorner[1]+","+upperCorner[0]+","+upperCorner[1];
    }

}
