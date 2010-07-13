/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.geometry.jts.decimation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGeometryDecimator implements GeometryDecimator{

    protected final GeometryFactory gf;
    protected final CoordinateSequenceFactory csf;

    public AbstractGeometryDecimator(){
        this((CoordinateSequenceFactory)null);
    }

    public AbstractGeometryDecimator(CoordinateSequenceFactory csf){
        if(csf == null){
            this.csf = CoordinateArraySequenceFactory.instance();
        }else{
            this.csf = csf;
        }
        gf = new GeometryFactory(csf);
    }

    public AbstractGeometryDecimator(GeometryFactory gf){
        if(gf == null){
            this.csf = CoordinateArraySequenceFactory.instance();
            this.gf = new GeometryFactory(csf);
        }else{
            this.csf = gf.getCoordinateSequenceFactory();
            this.gf = gf;
        }
    }

    @Override
    public abstract CoordinateSequence decimate(CoordinateSequence sequence);

    @Override
    public abstract double[] decimate(double[] coords, int dimension);

    @Override
    public abstract Coordinate[] decimate(Coordinate[] coords);

    @Override
    public Geometry decimate(Geometry geom) {
        if(geom instanceof Point){
            return decimate((Point)geom);
        }else if(geom instanceof MultiPoint){
            return decimate((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return decimate((LineString)geom);
        }else if(geom instanceof LinearRing){
            return decimate((LinearRing)geom);
        }else if(geom instanceof MultiLineString){
            return decimate((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return decimate((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return decimate((MultiPolygon)geom);
        }else{
            throw new IllegalArgumentException("Geometry type is unknowed or null : " + geom);
        }
    }

    protected Point decimate(Point geom){
        //nothing to decimate
        return geom;
    }

    protected MultiPoint decimate(MultiPoint geom){
        final int nbGeom = geom.getNumGeometries();

        if(nbGeom == 1){
            //nothing to decimate
            return geom;
        }else{
            final Coordinate[] coords = decimate(geom.getCoordinates());
            return gf.createMultiPoint(coords);
        }
    }

    protected LineString decimate(LineString geom){
        final CoordinateSequence seq = decimate(geom.getCoordinateSequence());
        return gf.createLineString(seq);
    }

    protected LinearRing decimate(LinearRing geom){
        final CoordinateSequence seq = decimate(geom.getCoordinateSequence());
        return gf.createLinearRing(seq);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected MultiLineString decimate(MultiLineString geom){
        final LineString[] subs = new LineString[geom.getNumGeometries()];
        //todo we could remove sub geometries in some cases
        for(int i=0; i<subs.length; i++){
            subs[i] = decimate((LineString)geom.getGeometryN(i));
        }
        return gf.createMultiLineString(subs);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected Polygon decimate(Polygon geom){
        final LinearRing exterior = decimate((LinearRing)geom.getExteriorRing());
        final LinearRing[] holes = new LinearRing[geom.getNumInteriorRing()];
        for(int i=0; i<holes.length; i++){
            holes[i] = decimate((LinearRing)geom.getInteriorRingN(i));
        }
        return gf.createPolygon(exterior, holes);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected MultiPolygon decimate(MultiPolygon geom){
        final Polygon[] subs = new Polygon[geom.getNumGeometries()];
        for(int i=0; i<subs.length; i++){
            subs[i] = decimate((Polygon)geom.getGeometryN(i));
        }
        return gf.createMultiPolygon(subs);
    }

}
