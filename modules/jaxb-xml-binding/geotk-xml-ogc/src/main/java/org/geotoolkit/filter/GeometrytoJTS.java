/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;

import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.gml.xml.v311.AbstractCurveSegmentType;
import org.geotoolkit.gml.xml.v311.AbstractCurveType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.AbstractRingPropertyType;
import org.geotoolkit.gml.xml.v311.AbstractRingType;
import org.geotoolkit.gml.xml.v311.ArcByCenterPointType;
import org.geotoolkit.gml.xml.v311.ArcStringByBulgeType;
import org.geotoolkit.gml.xml.v311.ArcStringType;
import org.geotoolkit.gml.xml.v311.BSplineType;
import org.geotoolkit.gml.xml.v311.ClothoidType;
import org.geotoolkit.gml.xml.v311.CubicSplineType;
import org.geotoolkit.gml.xml.v311.CurvePropertyType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.DirectPositionListType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.GeodesicStringType;
import org.geotoolkit.gml.xml.v311.LineStringPropertyType;
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.LinearRingType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.OffsetCurveType;
import org.geotoolkit.gml.xml.v311.OrientableCurveType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonPropertyType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.geotoolkit.util.logging.Logging;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class GeometrytoJTS {

    private static final GeometryFactory GF = new GeometryFactory();

    private GeometrytoJTS(){}

    public static Geometry toJTS(AbstractGeometryType gml){

        if(gml instanceof PointType){
            return toJTS((PointType)gml);
        }else if(gml instanceof LineStringType){
            return toJTS((LineStringType)gml);
        }else if(gml instanceof PolygonType){
            return toJTS((PolygonType)gml);
        }

        else if(gml instanceof MultiPointType){
            return toJTS((MultiPointType)gml);
        }else if(gml instanceof MultiLineStringType){
            return toJTS((MultiLineStringType)gml);
        }else if(gml instanceof MultiPolygonType){
            return toJTS((MultiPolygonType)gml);
        }

        else{
            Logging.getLogger(GeometrytoJTS.class).log(Level.SEVERE, "Unssupported geometry type : " + gml);
            return GF.createPoint(new Coordinate(0, 0));
        }

    }

    public static Point toJTS(PointType gml){
        final DirectPosition pos = gml.getDirectPosition();
        final double[] coords = pos.getCoordinate();
        
        final Point geom = GF.createPoint(new Coordinate(coords[0], coords[1]));

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            geom.setSRID(srid);
        }
        
        return geom;
    }
    
    public static Polygon toJTS(PolygonType gml){
        final AbstractRingPropertyType ext = gml.getExterior().getValue();
        final List<JAXBElement<AbstractRingPropertyType>> ints = gml.getInterior();

        final LinearRing exterior = toJTS(ext.getAbstractRing().getValue());
        final LinearRing[] holes = new LinearRing[ints.size()];
        for(int i=0;i<holes.length;i++){
            holes[i] = toJTS(ints.get(i).getValue().getAbstractRing().getValue());
        }

        final Polygon polygon = GF.createPolygon(exterior, holes);

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            polygon.setSRID(srid);
        }

        return polygon;
    }

    public static LineString toJTS(LineStringType gml){
        final DirectPositionListType lst = gml.getPosList();
        final int dim = gml.getCoordinateDimension();

        final List<Coordinate> coords = toJTSCoords(lst, dim);

        final LineString lineString = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            lineString.setSRID(srid);
        }

        return lineString;
    }

    public static List<Coordinate> toJTSCoords(DirectPositionListType lst, int dim){
        final List<Double> values = lst.getValue();
        final List<Coordinate> coords = new ArrayList<Coordinate>();

        for(int i=0,n=values.size();i<n;i+=dim){
            coords.add( new Coordinate(values.get(i), values.get(i+1)) );
        }
        return coords;
    }

    public static MultiPoint toJTS(MultiPointType gml){
        final List<PointPropertyType> pos = gml.getPointMember();
        final Point[] members = new Point[pos.size()];

        for(int i=0,n=pos.size(); i<n; i++){
            members[i] = toJTS(pos.get(i).getPoint());
        }
        
        final MultiPoint geom = GF.createMultiPoint(members);

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            geom.setSRID(srid);
        }
        
        return geom;
    }

    public static MultiLineString toJTS(MultiLineStringType gml){
        final List<LineStringPropertyType> pos = gml.getLineStringMember();
        final LineString[] members = new LineString[pos.size()];

        for(int i=0,n=pos.size(); i<n; i++){
            members[i] = toJTS(pos.get(i).getLineString());
        }

        final MultiLineString geom = GF.createMultiLineString(members);

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            geom.setSRID(srid);
        }

        return geom;
    }

    public static MultiPolygon toJTS(MultiPolygonType gml){
        final List<PolygonPropertyType> pos = gml.getPolygonMember();
        final Polygon[] members = new Polygon[pos.size()];

        for(int i=0,n=pos.size(); i<n; i++){
            members[i] = toJTS(pos.get(i).getPolygon());
        }

        final MultiPolygon geom = GF.createMultiPolygon(members);

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            geom.setSRID(srid);
        }

        return geom;
    }

    public static LinearRing toJTS(AbstractRingType gml){
        if(gml instanceof LinearRingType){
            return toJTS((LinearRingType)gml);
        }else if(gml instanceof RingType){
            return toJTS((RingType)gml);
        }else{
            Logging.getLogger(GeometrytoJTS.class).log(Level.SEVERE, "Unssupported geometry type : " + gml);
            return GF.createLinearRing(new Coordinate[]{new Coordinate(0, 0),new Coordinate(0, 0),
            new Coordinate(0, 0),new Coordinate(0, 0)});
        }

    }

    public static LinearRing toJTS(LinearRingType gml){
        final DirectPositionListType lst = gml.getPosList();
        final int dim = gml.getCoordinateDimension();

        final List<Double> values = lst.getValue();
        final List<Coordinate> coords = new ArrayList<Coordinate>();
        
        for(int i=0,n=values.size();i<n;i+=dim){
            coords.add( new Coordinate(values.get(i), values.get(i+1)) );
        }

        final LinearRing ring = GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            ring.setSRID(srid);
        }

        return ring;
    }

    public static LinearRing toJTS(RingType gml){

        final LinkedList<Coordinate> coords = new LinkedList<Coordinate>();

        for(CurvePropertyType cpt :gml.getCurveMember()){
            AbstractCurveType act = cpt.getAbstractCurve().getValue();

            if(act instanceof CurveType){
                final CurveType ct = (CurveType) act;
                for(JAXBElement<? extends AbstractCurveSegmentType> jax : ct.getSegments().getAbstractCurveSegment()){
                    AbstractCurveSegmentType acst = jax.getValue();

                    if(acst instanceof ClothoidType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof BSplineType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof CubicSplineType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof GeodesicStringType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof LineStringSegmentType){
                        final LineStringSegmentType lsst = (LineStringSegmentType)acst;

                        if(lsst.getPosList() != null){
                            final DirectPositionListType dpst = lsst.getPosList();
                            final int dim = ct.getCoordinateDimension();
                            final List<Coordinate> plots = toJTSCoords(dpst, dim);

                            if(coords.isEmpty()){
                                for(Coordinate c : plots){
                                    coords.add(c);
                                }
                            }else{
                                if( coords.getLast().equals(plots.get(0)) ){
                                    for(int i=1; i<plots.size();i++){
                                        coords.add(plots.get(i));
                                    }
                                }else{
                                    for(Coordinate c : plots){
                                        coords.add(c);
                                    }
                                }
                            }
                        }else{

                            if(coords.isEmpty()){
                                for(JAXBElement ele : lsst.getPosOrPointPropertyOrPointRep()){
                                    Object v = ele.getValue();
                                    if(v instanceof DirectPositionType){
                                        DirectPositionType pos = (DirectPositionType)v;
                                        coords.add(new Coordinate(pos.getOrdinate(0), pos.getOrdinate(1)));
                                    }else{
                                        throw new IllegalArgumentException("not supported yet");
                                    }
                                }
                            }else{
                                for(JAXBElement ele : lsst.getPosOrPointPropertyOrPointRep()){
                                    Object v = ele.getValue();
                                    if(v instanceof DirectPositionType){
                                        DirectPositionType pos = (DirectPositionType)v;

                                        Coordinate c = new Coordinate(pos.getOrdinate(0), pos.getOrdinate(1));

                                        if(!c.equals2D(coords.getLast())){
                                            coords.add(c);
                                        }
                                    }else{
                                        throw new IllegalArgumentException("not supported yet");
                                    }
                                }
                            }


                            

                        }


                    } else if (acst instanceof ArcByCenterPointType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof ArcStringType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof OffsetCurveType){
                        throw new IllegalArgumentException("not supported yet");
                    } else if (acst instanceof ArcStringByBulgeType){
                        throw new IllegalArgumentException("not supported yet");
                    } else{
                        throw new IllegalArgumentException("not supported yet");
                    }

                }
            }else if(act instanceof LineStringType){
                final LineString ls = toJTS((LineStringType)act);
                final Coordinate[] plots = ls.getCoordinates();

                if(coords.isEmpty()){
                    for(Coordinate c : plots){
                        coords.add(c);
                    }
                }else{
                    if( coords.getLast().equals(plots[0]) ){
                        for(int i=1; i<plots.length;i++){
                            coords.add(plots[i]);
                        }
                    }else{
                        for(Coordinate c : plots){
                            coords.add(c);
                        }
                    }
                }


            }else if(act instanceof OrientableCurveType){
                throw new IllegalArgumentException("not supported yet");
            }
        }


        final LinearRing ring = GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            ring.setSRID(srid);
        }

        return ring;
    }


}
