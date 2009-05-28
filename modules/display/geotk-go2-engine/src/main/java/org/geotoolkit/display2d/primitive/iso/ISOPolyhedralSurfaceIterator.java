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


import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * Simple and efficient path iterator for ISO PolyhedralSurface.
 *
 * @author Johann Sorel (Geomatys)
 */
public final  class ISOPolyhedralSurfaceIterator extends ISOGeometryIterator<PolyhedralSurface> {

    /** The rings describing the polygon geometry */
    private final List<OrientableCurve> curves;

    /** The current ring during iteration */
    private int currentRing = 0;

    /** Current line coordinate */
    private int currentCoord = 0;

    /** The array of coordinates that represents the line geometry */
    private PointArray coordinate = null;

    /** True when the iteration is terminated */
    private boolean done = false;

    /** if the geometry is empty */
    private boolean empty = false;

    /**
     * Creates a new PolygonIterator object.
     *
     * @param p The polygon
     * @param trs The affine transform applied to coordinates during iteration
     */
    public ISOPolyhedralSurfaceIterator(PolyhedralSurface p, AffineTransform trs) {
        super(p,trs);

        curves = new ArrayList<OrientableCurve>();
        for(Polygon polygon : p.getPatches()){
            SurfaceBoundary boundary = polygon.getBoundary();
            curves.addAll(boundary.getExterior().getGenerators());
            for(Ring r : boundary.getInteriors()){
                curves.addAll(r.getGenerators());
            }
        }

        reset();
    }

    private void reset(){
        currentRing = 0;
        currentCoord = 0;
        coordinate = curves.get(0).getPrimitive().asLineString(0, 0).getControlPoints();
        empty = coordinate.isEmpty();
        done = empty;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        if(empty) return 0;
        
        // first make sure we're not at the last element, this prevents us from exceptions
        // in the case where coords.size() == 0
        if (currentCoord == this.coordinate.size()) {
            return SEG_CLOSE;
        } else if (currentCoord == 0) {
            DirectPosition pos = coordinate.get(0).getDirectPosition();
            coords[0] = pos.getOrdinate(0);
            coords[1] = pos.getOrdinate(1);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else {
            DirectPosition pos = coordinate.get(currentCoord).getDirectPosition();
            coords[0] = pos.getOrdinate(0);
            coords[1] = pos.getOrdinate(1);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int getWindingRule() {
        return (empty) ? WIND_NON_ZERO : WIND_EVEN_ODD;
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
        if (currentCoord == coordinate.size()) {
            if (currentRing < (curves.size() - 1)) {
                currentCoord = 0;
                currentRing++;
                coordinate = curves.get(currentRing).getPrimitive().asLineString(0, 0).getControlPoints();
            } else {
                done = true;
            }
        } else {
            currentCoord++;
        }
    }
    
}
