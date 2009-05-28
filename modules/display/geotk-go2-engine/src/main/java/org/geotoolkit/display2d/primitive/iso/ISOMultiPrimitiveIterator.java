/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.primitive.iso;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;

/**
 * Simple and efficient path iterator for ISO MultiPrimitive.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ISOMultiPrimitiveIterator extends ISOGeometryIterator<MultiPrimitive> {

    private int currentGeom;
    private PathIterator currentIterator;
    private boolean done = false;
    private final List<Primitive> primitives = new ArrayList<Primitive>();

    public ISOMultiPrimitiveIterator(MultiPrimitive gc, AffineTransform trs) {
        super(gc,trs);
        primitives.addAll(gc.getElements());
        reset();
    }

    private void reset(){
        currentGeom = 0;
        done = false;
        currentIterator = getIterator(primitives.get(0));
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     *
     * @return the specific iterator for the geometry passed.
     */
    private ISOGeometryIterator getIterator(Geometry candidate) {
        ISOGeometryIterator iterator = null;

//        if (candidate.isEmpty()) {
//            iterator = new ISOEmptyIterator();
//        }else
        if (candidate instanceof Point) {
            iterator = new ISOPointIterator((Point)candidate, transform);
        } else if (candidate instanceof PolyhedralSurface) {
            iterator = new ISOPolyhedralSurfaceIterator((PolyhedralSurface)candidate, transform);
        } else if (candidate instanceof Curve) {
            iterator = new ISOCurveIterator((Curve)candidate, transform);
        } else if (candidate instanceof MultiPrimitive) {
            iterator = new ISOMultiPrimitiveIterator((MultiPrimitive)candidate, transform);
        }

        return iterator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        return currentIterator.currentSegment(coords);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(float[] coords) {
        return currentIterator.currentSegment(coords);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDone() {
        if(done){
            reset();
            return true;
        }else{
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void next() {
        if (currentIterator.isDone()) {
            if (currentGeom < (primitives.size() - 1)) {
                currentGeom++;
                currentIterator = getIterator(primitives.get(currentGeom));
            } else {
                done = true;
            }
        } else {
            currentIterator.next();
        }
    }
    
}
