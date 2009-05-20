/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.isowrapper.geometries;

import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorel
 */
public class ISOJTSMultiPoint extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.MultiPoint> implements MultiPoint{

    public ISOJTSMultiPoint(com.vividsolutions.jts.geom.MultiPoint point) {
        super(point);
    }

    @Override
    public Set<Point> getElements() {
        Set<Point> points = new HashSet<Point>();
        int num = jtsGeometry.getNumGeometries();

        for(int i=0; i<num;i++){
            com.vividsolutions.jts.geom.Point p = (com.vividsolutions.jts.geom.Point) jtsGeometry.getGeometryN(i);
            points.add(new ISOJTSPoint(p));
        }

        return points;
    }

}
