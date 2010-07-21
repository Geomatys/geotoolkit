/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.primitive.jts;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module pending
 */
public class JTSGeometryJ2D extends AbstractJTSGeometryJ2D<Geometry> {

    protected JTSGeometryIterator<? extends Geometry> iterator = null;

    public JTSGeometryJ2D(Geometry geom) {
        super(geom);
    }

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public JTSGeometryJ2D(Geometry geom, AffineTransform trs) {
        super(geom, trs);
    }

    /**
     * Sets the geometry contained in this lite shape. Convenient to reuse this
     * object instead of creating it again and again during rendering
     *
     * @param g
     */
    @Override
    public void setGeometry(Geometry g) {
        super.setGeometry(g);

        //change iterator only if necessary
        if(iterator != null && geometry != null){
            if (this.geometry.isEmpty() && iterator instanceof JTSEmptyIterator) {
                //nothing to do
            }else if (this.geometry instanceof Point && iterator instanceof JTSPointIterator) {
                ((JTSPointIterator)iterator).setGeometry((Point)geometry);
            } else if (this.geometry instanceof Polygon && iterator instanceof JTSPolygonIterator) {
                ((JTSPolygonIterator)iterator).setGeometry((Polygon)geometry);
            } else if (this.geometry instanceof LineString && iterator instanceof JTSLineIterator) {
                ((JTSLineIterator)iterator).setGeometry((LineString)geometry);
            } else if (this.geometry instanceof GeometryCollection && iterator instanceof JTSGeomCollectionIterator) {
                ((JTSGeomCollectionIterator)iterator).setGeometry((GeometryCollection)geometry);
            }else{
                //iterator does not match the new geometry type
                iterator = null;
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at) {

        final AffineTransform concat;
        if(at == null){
            concat = transform;
        }else{
            concat = (AffineTransform) transform.clone();
            concat.preConcatenate(at);
        }

        if(iterator == null){
            if (this.geometry.isEmpty()) {
                iterator = JTSEmptyIterator.INSTANCE;
            }else if (this.geometry instanceof Point) {
                iterator = new JTSPointIterator((Point) geometry, concat);
            } else if (this.geometry instanceof Polygon) {
                iterator = new JTSPolygonIterator((Polygon) geometry, concat);
            } else if (this.geometry instanceof LineString) {
                iterator = new JTSLineIterator((LineString)geometry, concat);
            } else if (this.geometry instanceof GeometryCollection) {
                iterator = new JTSGeomCollectionIterator((GeometryCollection)geometry,concat);
            }
        }else{
            iterator.setTransform(concat);
        }

        return iterator;
    }

    @Override
    public AbstractJTSGeometryJ2D clone() {
        return new JTSGeometryJ2D(this.geometry,this.transform);
    }

    public static AbstractJTSGeometryJ2D best(Class clazz, AffineTransform trs){
        if(Point.class.isAssignableFrom(clazz)){
            return new JTSGeometryJ2D(null,trs);
        }else if(MultiPoint.class.isAssignableFrom(clazz)){
            return new JTSGeometryJ2D(null,trs);
        }else if(LineString.class.isAssignableFrom(clazz)){
            return new JTSGeometryJ2D(null,trs);
        }else if(MultiLineString.class.isAssignableFrom(clazz)){
            return new JTSMultiLineStringJ2D(null,trs);
        }else if(Polygon.class.isAssignableFrom(clazz)){
            return new JTSGeometryJ2D(null,trs);
        }else if(MultiPolygon.class.isAssignableFrom(clazz)){
            return new JTSGeometryJ2D(null,trs);
        }else if(Geometry.class.isAssignableFrom(clazz)){
            //undeterminated type
            return new JTSGeometryJ2D(null,trs);
        }

        throw new IllegalArgumentException("Unexpected geometry class : " + clazz);
    }

}
