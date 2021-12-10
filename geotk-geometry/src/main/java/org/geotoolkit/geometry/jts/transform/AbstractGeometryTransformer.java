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

import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.CoordinateSequence;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractGeometryTransformer implements GeometryTransformer{

    protected final GeometryFactory gf;
    protected final CoordinateSequenceFactory csf;

    public AbstractGeometryTransformer(){
        this((CoordinateSequenceFactory)null);
    }

    public AbstractGeometryTransformer(final CoordinateSequenceFactory csf){
        if(csf == null){
            this.gf = org.geotoolkit.geometry.jts.JTS.getFactory();
            this.csf = gf.getCoordinateSequenceFactory();
        }else{
            this.csf = csf;
            this.gf = new GeometryFactory(csf);
        }

    }

    public AbstractGeometryTransformer(final GeometryFactory gf){
        if(gf == null){
            this.gf = org.geotoolkit.geometry.jts.JTS.getFactory();
            this.csf = gf.getCoordinateSequenceFactory();
        }else{
            this.csf = gf.getCoordinateSequenceFactory();
            this.gf = gf;
        }
    }

    @Override
    public Geometry transform(final Geometry geom) throws TransformException {
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
        }else if(geom instanceof GeometryCollection){
            return transform((GeometryCollection)geom);
        }else{
            throw new IllegalArgumentException("Geometry type is unknowed or null : " + geom);
        }
    }

    protected Point transform(final Point geom) throws TransformException{
        CoordinateSequence coord = geom.getCoordinateSequence();
        return gf.createPoint(transform(coord, 1));
    }

    protected MultiPoint transform(final MultiPoint geom) throws TransformException{
        final int nbGeom = geom.getNumGeometries();

        final Point[] subs = new Point[geom.getNumGeometries()];
        for(int i = 0; i<nbGeom ; i++){
            subs[i] = transform((Point)geom.getGeometryN(i));
        }
        return gf.createMultiPoint(subs);
    }

    protected LineString transform(final LineString geom) throws TransformException{
        final CoordinateSequence seq = transform(geom.getCoordinateSequence(),2);
        return gf.createLineString(seq);
    }

    protected LinearRing transform(final LinearRing geom) throws TransformException{
        final CoordinateSequence seq = transform(geom.getCoordinateSequence(),4);
        return gf.createLinearRing(seq);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected MultiLineString transform(final MultiLineString geom) throws TransformException{
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
    protected Polygon transform(final Polygon geom) throws TransformException{
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
    protected MultiPolygon transform(final MultiPolygon geom) throws TransformException{
        final Polygon[] subs = new Polygon[geom.getNumGeometries()];
        for(int i=0; i<subs.length; i++){
            subs[i] = transform((Polygon)geom.getGeometryN(i));
        }
        return gf.createMultiPolygon(subs);
    }

    /**
     * Sub classes may override this method is they wish to remove some of the sub geometries
     * if they are too small.
     */
    protected GeometryCollection transform(final GeometryCollection geom) throws TransformException{
        final Geometry[] subs = new Geometry[geom.getNumGeometries()];
        for(int i=0; i<subs.length; i++){
            subs[i] = transform(geom.getGeometryN(i));
        }
        return gf.createGeometryCollection(subs);
    }

}
