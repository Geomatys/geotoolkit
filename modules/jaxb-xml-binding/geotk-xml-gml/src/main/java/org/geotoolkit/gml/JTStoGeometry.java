/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml;

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

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometricAggregateType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.AbstractRingType;
import org.geotoolkit.gml.xml.v311.DirectPositionListType;
import org.geotoolkit.gml.xml.v311.LineStringPropertyType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.LinearRingType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonPropertyType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Set of converter from JTS Geometry to GML Geometry.
 * @author Quentin Boileau
 * @module pending
 */
public class JTStoGeometry {
    private JTStoGeometry(){}
    
    /**
     * Transform A JTS geometry into GML geometry
     *
     * @param AbstractGeometryType A GML geometry.
     *
     * @return A JTS Polygon
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.util.FactoryException
     */
     public static AbstractGeometryType toGML(final Geometry jts) throws NoSuchAuthorityCodeException, FactoryException{

         final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(jts);
         
        if (jts instanceof Point){
            return toGML((Point)jts,crs);
        } else if(jts instanceof LineString){
            return toGML((LineString)jts,crs);
        } else if(jts instanceof Polygon){
            return toGML((Polygon)jts,crs);
        }else if(jts instanceof LinearRing){
            return toGML((LinearRing)jts,crs);
        
        } else if(jts instanceof GeometryCollection){
            return toGML((GeometryCollection)jts,crs);

        } else if(jts instanceof MultiPoint){
            return toGML((MultiPoint)jts,crs);
            
        } else if(jts instanceof MultiLineString){
            return toGML((MultiLineString)jts,crs);
            
        } else if(jts instanceof MultiPolygon){
            return toGML((MultiPolygon)jts,crs);
        
        } else {
            throw new IllegalArgumentException("Unssupported geometry type : " + jts);
        }

    }
    
    /**
      * Try to convert a JTS GeometryCollection to a GML AbstractGeometricAggregateType
      * @param jtsGeom
      * @param crs Coordinate Reference System
      * @return AbstractGeometricAggregateType
      * @throws NoSuchAuthorityCodeException
      * @throws FactoryException 
      */
    public static AbstractGeometricAggregateType toGML(final GeometryCollection jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
                
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        //Get th e class of the first geometry in the GeometryCollection
        Class buffer = null;
        if(jtsGeom.getNumGeometries() > 0){
            final Geometry geom = jtsGeom.getGeometryN(0);
            if(geom.getClass().isAssignableFrom(Polygon.class) || geom.getClass().isAssignableFrom(Point.class) 
                    || geom.getClass().isAssignableFrom(LineString.class)){
                buffer = geom.getClass();
            }
        }
        //Verify if all other geometries contained by the GeometryCollection is from the same class
        boolean isSupported = true; 
        for(int i=0; i<jtsGeom.getNumGeometries(); i++){
            if(!(jtsGeom.getGeometryN(i).getClass().isAssignableFrom(buffer))){
                isSupported = false;
                break;
            }
        }
        
        if(isSupported){
            final GeometryFactory gf = new GeometryFactory();
            //Convert to a MultiPoint 
            if(buffer.equals(Point.class)){
                List<Point> ptList = new ArrayList<Point>();
                for(int i=0; i<jtsGeom.getNumGeometries(); i++){
                   ptList.add((Point) jtsGeom.getGeometryN(i));
                }
                final MultiPoint mutlPt = gf.createMultiPoint(ptList.toArray(new Point[ptList.size()]));
                JTS.setCRS(mutlPt, crs);
                return toGML(mutlPt,crs);
                
            //Convert to a MultiLineString
            }else if(buffer.equals(LineString.class)){
                List<LineString> lsList = new ArrayList<LineString>();
                for(int i=0; i<jtsGeom.getNumGeometries(); i++){
                   lsList.add((LineString) jtsGeom.getGeometryN(i));
                }
                final MultiLineString multLineString = gf.createMultiLineString(lsList.toArray(new LineString[lsList.size()]));
                JTS.setCRS(multLineString, crs);
                return toGML(multLineString,crs);
                
            }else if(buffer.equals(Polygon.class)){
                List<Polygon> polyList = new ArrayList<Polygon>();
                for(int i=0; i<jtsGeom.getNumGeometries(); i++){
                   polyList.add((Polygon) jtsGeom.getGeometryN(i));
                }
                final MultiPolygon multPoly = gf.createMultiPolygon(polyList.toArray(new Polygon[polyList.size()]));
                JTS.setCRS(multPoly, crs);
                return toGML(multPoly,crs);
            }else{
                throw new IllegalArgumentException("Unssupported geometry type : " + jtsGeom);
            }
        }else{
            throw new IllegalArgumentException("Unssupported geometry type : " + jtsGeom);
        }
       
    }  
     
    /**
     * Convert JTS MultiPoint to GML MultiPointType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiPointType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static MultiPointType toGML(final MultiPoint jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
       //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        final List<PointPropertyType> pointList = new ArrayList<PointPropertyType>();
        for(int i=0; i<jtsGeom.getNumGeometries(); i++){
            final PointType gmlPt = toGML((Point)jtsGeom.getGeometryN(i),crs);
            pointList.add(new PointPropertyType(gmlPt));
        }
       
        final String srs = getSRS(crs);
        final MultiPointType gmlMultPt = new MultiPointType(srs, pointList);
        
        return gmlMultPt;
    }  
     
    /**
     * Convert JTS MultiLineString to GML MultiLineStringType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiLineStringType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static MultiLineStringType toGML(final MultiLineString jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        final List<LineStringPropertyType> lineList = new ArrayList<LineStringPropertyType>();
        for(int i=0; i<jtsGeom.getNumGeometries(); i++){
            
            final LineStringPropertyType gmlLineStr = new LineStringPropertyType();
            gmlLineStr.setLineString(toGML((LineString)jtsGeom.getGeometryN(i),crs));
            lineList.add(gmlLineStr);
        }
       
        final String srs = getSRS(crs);
        final MultiLineStringType gmlMultLine = new MultiLineStringType();
        gmlMultLine.setSrsName(srs);
        gmlMultLine.setLineStringMember(lineList);
        
        return gmlMultLine;
    }
    
    /**
     * Convert JTS MultiPolygon to GML MultiPolygonType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiPolygonType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static MultiPolygonType toGML(final MultiPolygon jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        final List<PolygonPropertyType> polyList = new ArrayList<PolygonPropertyType>();
        for(int i=0; i<jtsGeom.getNumGeometries(); i++){
            
            final PolygonPropertyType gmlPoly = new PolygonPropertyType();
            gmlPoly.setPolygon(toGML((Polygon)jtsGeom.getGeometryN(i),crs));
            polyList.add(gmlPoly);
        }
       
        final String srs = getSRS(crs);
        final MultiPolygonType gmlMultPoly = new MultiPolygonType();
        gmlMultPoly.setSrsName(srs);
        gmlMultPoly.setPolygonMember(polyList);
        
        return gmlMultPoly;
    }
    
    /**
     * Convert JTS Polygon to GML PolygonType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return PolygonType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static PolygonType toGML(final Polygon jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        //get exterior ring
        final AbstractRingType gmlExterior = toGML((LinearRing)jtsGeom.getExteriorRing(),crs);
        //get interiors ring
        final List<AbstractRingType> gmlInterior = new ArrayList<AbstractRingType>();
        for(int i=0; i<jtsGeom.getNumInteriorRing(); i++){
            gmlInterior.add(toGML((LinearRing)jtsGeom.getInteriorRingN(i),crs));
        }
       
       
        final PolygonType gmlPoly = new PolygonType(gmlExterior,gmlInterior);
        gmlPoly.setSrsName(getSRS(crs));
        
        return gmlPoly;
    } 
     
    /**
     * Convert JTS LineString to GML LineStringType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return LineStringType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static LineStringType toGML(final LineString jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
       //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final Coordinate[] jtsCoord = jtsGeom.getCoordinates();
       
        final List<DirectPosition> dpList = new ArrayList<DirectPosition>();
        
        for(Coordinate c : jtsCoord){
           dpList.add(coordinateToDirectPosition(c, crs));
        }
        
        final LineStringType gmlString = new LineStringType(dpList);
        gmlString.setSrsName(getSRS(crs));
        
        return gmlString;
    }
     
    /**
     * Convert JTS LinearRing to GML LinearRingType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return LinearRingType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static LinearRingType toGML(final LinearRing jtsGeom, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
        
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final Coordinate[] jtsCoord = jtsGeom.getCoordinates();
       
        final List<Double> coordList = new ArrayList<Double>();
        
        for(Coordinate c : jtsCoord){
            coordList.add(c.x);
            coordList.add(c.y);
        }
        
        final DirectPositionListType dpList = new DirectPositionListType();
        dpList.setValue(coordList);
        
        final LinearRingType gmlRing = new LinearRingType();
        gmlRing.setPosList(dpList);
        gmlRing.setSrsName(getSRS(crs));
        
        return gmlRing;
    }
    
    /**
     * Convert JTS Point to GML PointType
     * @param jtsPoint
     * @param crs Coordinate Reference System
     * @return PointType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException 
     */
    public static PointType toGML(final Point jtsPoint, final CoordinateReferenceSystem crs) 
            throws NoSuchAuthorityCodeException, FactoryException{
   
        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        
        final PointType gmlPoint = new PointType(coordinateToDirectPosition(jtsPoint.getCoordinate(), crs));
        
        gmlPoint.setSrsName(getSRS(crs));
        return gmlPoint;
    }
    
    /**
     * Convert Coordinate to DirectPosition only in 2D
     * @param coord
     * @param crs
     * @return DirectPostion with x and y
     * @throws IllegalArgumentException if isn't a 2D Geometry
     */
    private static DirectPosition coordinateToDirectPosition(final Coordinate coord, final CoordinateReferenceSystem crs){
        if(coord.z != Double.NaN){
            //throw new IllegalArgumentException("This service support only 2D coordinate.");
        }
        
        return new DirectPosition2D(crs, coord.x, coord.y);
    }
    
    /**
     * Return the Coordinate Reference System of the Geometry.
     * If the geometry CRS isn't define use <code>JTS.setCrs(geometry,CRS)</code> before call a GML conversion.
     * @param jtsGeom
     * @return the crs if valid geometry
     * @throws NoSuchAuthorityCodeException in case of unknow authority
     * @throws FactoryException in case of unknow factory
     * @throws IllegalArgumentException in case of null CRS
     */
    private static CoordinateReferenceSystem getCRS(final Geometry jtsGeom) throws NoSuchAuthorityCodeException, FactoryException{
              
        //get JTS CRS
        final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(jtsGeom);
        if(crs == null){
            throw new IllegalArgumentException("JTS geometry must specify a Coordinate Reference System.");
        }
        
        return crs;
    }
    
    /**
     * Check if a geometry is only a 2D geometry
     * @param crs
     * @return <code>true</code> for valid Geometry, <code>false</code> else. 
     */
    private static void isValideGeometry(final CoordinateReferenceSystem crs){
        if( crs.getCoordinateSystem().getDimension() != 2){
            throw new IllegalArgumentException("This service support only 2D JTS Geometry.");
        }
    }
    /**
     * Extract Identifier form a Coordinate Reference System
     * @param crs
     * @return CRS identifier
     * @throws FactoryException 
     */
    private static String getSRS(final CoordinateReferenceSystem crs) throws FactoryException{
        String srs = null;
        final String method1 = CRS.lookupIdentifier(Citations.URN_OGC, crs, false);
        
        if(method1 != null){
            srs = method1;
        }else {
            //Try to use the deprecated methode
            final String method2 = CRS.getDeclaredIdentifier(crs);
            if(method2 != null){
                srs = method2;
            }else{
                 throw new IllegalArgumentException("Can't get Coordinate Reference System identifier.");
            }
        }
        
        return srs;
    }
}
