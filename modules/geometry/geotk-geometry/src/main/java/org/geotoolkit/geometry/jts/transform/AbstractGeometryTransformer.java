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

package org.geotoolkit.geometry.jts.transform;

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
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGeometryTransformer implements GeometryTransformer{

    protected final GeometryFactory gf;
    protected final CoordinateSequenceFactory csf;

    public AbstractGeometryTransformer(){
        this((CoordinateSequenceFactory)null);
    }

    public AbstractGeometryTransformer(CoordinateSequenceFactory csf){
        if(csf == null){
            this.gf = new GeometryFactory();
            this.csf = gf.getCoordinateSequenceFactory();
        }else{
            this.csf = csf;
            this.gf = new GeometryFactory(csf);
        }
        
    }

    public AbstractGeometryTransformer(GeometryFactory gf){
        if(gf == null){
            this.gf = new GeometryFactory();
            this.csf = gf.getCoordinateSequenceFactory();
        }else{
            this.csf = gf.getCoordinateSequenceFactory();
            this.gf = gf;
        }
    }

    @Override
    public Geometry transform(Geometry geom) throws TransformException {
        if(geom instanceof Point){
            return transform((Point)geom);
        }else if(geom instanceof MultiPoint){
            return transform((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return transform((LineString)geom);
        }else if(geom instanceof LinearRing){
            return transform((LinearRing)geom);
        }else if(geom instanceof MultiLineString){
            return transform((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return transform((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return transform((MultiPolygon)geom);
        }else{
            throw new IllegalArgumentException("Geometry type is unknowed or null : " + geom);
        }
    }

    protected Point transform(Point geom) throws TransformException{
        //nothing to decimate
        return geom;
    }

    protected MultiPoint transform(MultiPoint geom) throws TransformException{
        final int nbGeom = geom.getNumGeometries();

        if(nbGeom == 1){
            //nothing to decimate
            return geom;
        }else{
            final LiteCoordinateSequence cs = new LiteCoordinateSequence(nbGeom, 2);
            for(int i=0;i<nbGeom;i++){
                final Coordinate coord = geom.getGeometryN(i).getCoordinate();
                cs.setX(i, coord.x);
                cs.setY(i, coord.y);
            }
            return gf.createMultiPoint(transform(cs,1));
        }
    }

    protected LineString transform(LineString geom) throws TransformException{
        final CoordinateSequence seq = transform(geom.getCoordinateSequence(),2);
        return gf.createLineString(seq);
    }

    protected LinearRing transform(LinearRing geom) throws TransformException{
        final CoordinateSequence seq = transform(geom.getCoordinateSequence(),4);
        return gf.createLinearRing(seq);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected MultiLineString transform(MultiLineString geom) throws TransformException{
        final LineString[] subs = new LineString[geom.getNumGeometries()];
        //todo we could remove sub geometries in some cases
        for(int i=0; i<subs.length; i++){
            subs[i] = transform((LineString)geom.getGeometryN(i));
        }
        return gf.createMultiLineString(subs);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected Polygon transform(Polygon geom) throws TransformException{
        final LinearRing exterior = transform((LinearRing)geom.getExteriorRing());
        final LinearRing[] holes = new LinearRing[geom.getNumInteriorRing()];
        for(int i=0; i<holes.length; i++){
            holes[i] = transform((LinearRing)geom.getInteriorRingN(i));
        }
        return gf.createPolygon(exterior, holes);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected MultiPolygon transform(MultiPolygon geom) throws TransformException{
        final Polygon[] subs = new Polygon[geom.getNumGeometries()];
        for(int i=0; i<subs.length; i++){
            subs[i] = transform((Polygon)geom.getGeometryN(i));
        }
        return gf.createMultiPolygon(subs);
    }

}
