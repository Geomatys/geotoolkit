/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Simple abstract path iterator for JTS Geometry.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @since 2.9
 */
public abstract class GeometryIterator<T extends Geometry> implements PathIterator {

    private static final AffineTransform IDENTITY = new AffineTransform();

	protected double[] dcoords = new double[2];
    protected AffineTransform transform;
    protected final T geometry;

    protected GeometryIterator(T geometry, AffineTransform trs){
        this.geometry = geometry;
        this.transform = (trs == null) ? IDENTITY : trs;
    }

	/**
     * {@inheritDoc }
     */
    @Override
	public int currentSegment(float[] coords) {
		int result = currentSegment(dcoords);
		coords[0] = (float) dcoords[0];
		coords[1] = (float) dcoords[1];
		return result;
	}

    public void setTransform(AffineTransform trs){
        this.transform = (trs == null) ? IDENTITY : trs;
    }

    public AffineTransform getTransform(){
        return transform;
    }

    public T getGeometry(){
        return geometry;
    }

}
