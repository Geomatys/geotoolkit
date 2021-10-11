/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.geometry.jts.awt;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.referencing.operation.MathTransform;


/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module
 */
public class JTSGeometryJ2D extends AbstractJTSGeometryJ2D<Geometry> {

    protected JTSGeometryIterator<? extends Geometry> iterator = null;

    public JTSGeometryJ2D(final Geometry geom) {
        super(geom);
    }

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public JTSGeometryJ2D(final Geometry geom, final MathTransform trs) {
        super(geom, trs);
    }

    /**
     * Sets the geometry contained in this lite shape. Convenient to reuse this
     * object instead of creating it again and again during rendering
     *
     * @param g
     */
    @Override
    public void setGeometry(final Geometry g) {
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
    public PathIterator getPathIterator(final AffineTransform at) {

        final MathTransform concat;
        if(at == null){
            concat = transform;
        }else{
            concat = MathTransforms.concatenate(transform, new AffineTransform2D(at));
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

    public static AbstractJTSGeometryJ2D best(final Class clazz, final MathTransform trs){
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
