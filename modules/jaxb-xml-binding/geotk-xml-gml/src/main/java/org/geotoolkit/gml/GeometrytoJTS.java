/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-1010, Geomatys
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
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;

import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
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
import org.geotoolkit.gml.xml.v311.CoordinatesType;
import org.geotoolkit.gml.xml.v311.CubicSplineType;
import org.geotoolkit.gml.xml.v311.CurvePropertyType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.DirectPositionListType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.GeodesicStringType;
import org.geotoolkit.gml.xml.v311.LineStringPropertyType;
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.LinearRingType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.MultiSurfaceType;
import org.geotoolkit.gml.xml.v311.OffsetCurveType;
import org.geotoolkit.gml.xml.v311.OrientableCurveType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonPropertyType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.geotoolkit.gml.xml.v311.SurfacePropertyType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class GeometrytoJTS {

    private static final GeometryFactory GF = new GeometryFactory();

    private GeometrytoJTS(){}


    /**
     * Transform A GML envelope into JTS Polygon
     *
     * @param GMLenvelope A GML envelope.
     *
     * @return A JTS Polygon
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public static com.vividsolutions.jts.geom.Polygon toJTS(EnvelopeEntry gmlEnvelope)
            throws NoSuchAuthorityCodeException, FactoryException {
        final String crsName = gmlEnvelope.getSrsName();
        if (crsName == null) {
            throw new IllegalArgumentException("An operator BBOX must specified a CRS (coordinate Reference system) for the envelope.");
        }

        final List<Double> lmin = gmlEnvelope.getLowerCorner().getValue();
        final double[] min      = new double[lmin.size()];
        for (int i = 0; i < min.length; i++) {
            min[i] = lmin.get(i);
        }

        final List<Double> lmax = gmlEnvelope.getUpperCorner().getValue();
        final double[] max = new double[lmax.size()];
        for (int i = 0; i < min.length; i++) {
            max[i] = lmax.get(i);
        }

        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(min[0], min[1]),
            new Coordinate(min[0], max[1]),
            new Coordinate(max[0], max[1]),
            new Coordinate(max[0], min[1]),
            new Coordinate(min[0], min[1])
        });
        final com.vividsolutions.jts.geom.Polygon polygon = GF.createPolygon(ring, new LinearRing[0]);
        final CoordinateReferenceSystem crs = CRS.decode(crsName, true);
        final int srid = SRIDGenerator.toSRID(crs, Version.V1);
        polygon.setSRID(srid);

        return polygon;
    }

    public static Geometry toJTS(AbstractGeometryType gml)
            throws NoSuchAuthorityCodeException, FactoryException{

        if (gml instanceof PointType){
            return toJTS((PointType)gml, -1);
        } else if(gml instanceof LineStringType){
            return toJTS((LineStringType)gml);
        } else if(gml instanceof PolygonType){
            return toJTS((PolygonType)gml);
        

        } else if(gml instanceof MultiPointType){
            return toJTS((MultiPointType)gml);
        } else if(gml instanceof MultiLineStringType){
            return toJTS((MultiLineStringType)gml);
        } else if(gml instanceof MultiPolygonType){
            return toJTS((MultiPolygonType)gml);
        } else if(gml instanceof MultiSurfaceType){
            return toJTS((MultiSurfaceType)gml);
        }

        else{
            Logging.getLogger(GeometrytoJTS.class).log(Level.SEVERE, "Unssupported geometry type : " + gml);
            return GF.createPoint(new Coordinate(0, 0));
        }

    }

    public static Point toJTS(PointType gmlPoint, int parentSrid) throws NoSuchAuthorityCodeException, FactoryException{
        final String crsName;
        if (parentSrid == -1) {
            crsName = gmlPoint.getSrsName();

            if (crsName == null) {
                throw new IllegalArgumentException("A GML point must specify Coordinate Reference System.");
            }
        } else {
            crsName = null;
        }

        //we get the coordinate of the point (if they are present)
        if (gmlPoint.getCoordinates() == null && gmlPoint.getPos() == null) {
            throw new IllegalArgumentException("A GML point must specify coordinates or direct position.");
        }

        final double[] coordinates = new double[2];
        if (gmlPoint.getCoordinates() != null) {
            final String coord = gmlPoint.getCoordinates().getValue();

            final StringTokenizer tokens = new StringTokenizer(coord, " ");
            int index = 0;
            while (tokens.hasMoreTokens()) {
                final double value = parseDouble(tokens.nextToken());
                if (index >= coordinates.length) {
                    throw new IllegalArgumentException("This service support only 2D point.");
                }
                coordinates[index++] = value;
            }
        } else if (gmlPoint.getPos().getValue() != null && gmlPoint.getPos().getValue().size() == 2){
            coordinates[0] = gmlPoint.getPos().getValue().get(0);
            coordinates[0] = gmlPoint.getPos().getValue().get(1);
        } else {
            throw new IllegalArgumentException("The GML point is malformed.");
        }

        final int srid;
        if (crsName != null) {
            final CoordinateReferenceSystem crs = CRS.decode(crsName, true);
            srid = SRIDGenerator.toSRID(crs, Version.V1);
        } else {
            srid = parentSrid;
        }

        final com.vividsolutions.jts.geom.Point pt =
                GF.createPoint(new Coordinate(coordinates[0], coordinates[1]));
        pt.setSRID(srid);

        return pt;
    }
    
    public static Polygon toJTS(PolygonType gml) throws FactoryException{
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

    public static LineString toJTS(LineStringType gmlLine) throws FactoryException{
        final String crsName = gmlLine.getSrsName();
        if (crsName == null) {
            throw new FactoryException("A CRS (coordinate Reference system) must be specified for the line.");
        }

        final com.vividsolutions.jts.geom.LineString ls;
        final CoordinatesType coord = gmlLine.getCoordinates();
        if(coord != null){
            String s = coord.getValue();

            double x1 = Double.parseDouble(s.substring(0, s.indexOf(coord.getCs())));
            s = s.substring(s.indexOf(coord.getCs()) + 1);
            double y1 = Double.parseDouble(s.substring(0, s.indexOf(coord.getTs())));
            s = s.substring(s.indexOf(coord.getTs()) + 1);
            double x2 = Double.parseDouble(s.substring(0, s.indexOf(coord.getCs())));
            s = s.substring(s.indexOf(coord.getCs()) + 1);
            double y2 = Double.parseDouble(s);

            final int srid = SRIDGenerator.toSRID(crsName, Version.V1);
            ls = GF.createLineString(new Coordinate[]{
                new Coordinate(x1,y1),
                new Coordinate(x2,y2)
            });
            ls.setSRID(srid);
        }else{
            final DirectPositionListType dplt = gmlLine.getPosList();
            final int dim = gmlLine.getCoordinateDimension();
            final List<Coordinate> coords = toJTSCoords(dplt, dim);

            final int srid = SRIDGenerator.toSRID(crsName, Version.V1);
            ls = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
            ls.setSRID(srid);

        }


        return ls;
    }

    public static List<Coordinate> toJTSCoords(DirectPositionListType lst, int dim){
        final List<Double> values = lst.getValue();
        final List<Coordinate> coords = new ArrayList<Coordinate>();

        for(int i=0,n=values.size();i<n;i+=dim){
            coords.add( new Coordinate(values.get(i), values.get(i+1)) );
        }
        return coords;
    }

    public static MultiPoint toJTS(MultiPointType gml) throws NoSuchAuthorityCodeException, FactoryException{
        final List<PointPropertyType> pos = gml.getPointMember();
        final Point[] members = new Point[pos.size()];

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        final int srid;
        if (crs != null){
            srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
        } else {
            srid = -1;
        }

        for(int i=0,n=pos.size(); i<n; i++){
            members[i] = toJTS(pos.get(i).getPoint(), srid);
        }
        
        final MultiPoint geom = GF.createMultiPoint(members);
        if (srid != -1){
            geom.setSRID(srid);
        }

        return geom;
    }

    public static MultiLineString toJTS(MultiLineStringType gml) throws FactoryException{
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

    public static MultiPolygon toJTS(MultiPolygonType gml) throws FactoryException{
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

    public static MultiPolygon toJTS(MultiSurfaceType gml) throws FactoryException{
        final List<SurfacePropertyType> pos = gml.getSurfaceMember();
        final Polygon[] members = new Polygon[pos.size()];

        for(int i=0,n=pos.size(); i<n; i++){
            members[i] = toJTS((PolygonType)pos.get(i).getRealAbstractSurface());
        }

        final MultiPolygon geom = GF.createMultiPolygon(members);

        final CoordinateReferenceSystem crs = gml.getCoordinateReferenceSystem();
        if(crs != null){
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            geom.setSRID(srid);
        }

        return geom;
    }

    public static LinearRing toJTS(AbstractRingType gml) throws FactoryException{
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

    public static LinearRing toJTS(RingType gml) throws FactoryException{

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


    /**
     * Parses a value as a floating point.
     *
     * @throws CstlServiceException if the value can't be parsed.
     */
    private static double parseDouble(String value) {
        value = value.trim();
        return Double.parseDouble(value);
    }

}
