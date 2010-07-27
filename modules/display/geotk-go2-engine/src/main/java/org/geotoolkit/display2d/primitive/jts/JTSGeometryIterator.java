/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
 * @module pending
 * @since 2.9
 */
public abstract class JTSGeometryIterator<T extends Geometry> implements PathIterator {

    static final AffineTransform IDENTITY = new AffineTransform();

    protected AffineTransform transform;
    protected T geometry;

    protected JTSGeometryIterator(AffineTransform trs){
        this(null,trs);
    }

    protected JTSGeometryIterator(T geometry, AffineTransform trs){
        this.transform = (trs == null) ? IDENTITY : trs;
        this.geometry = geometry;
    }

    public void setGeometry(T geom){
        this.geometry = geom;
    }

    public void setTransform(AffineTransform trs){
        this.transform = (trs == null) ? IDENTITY : trs;
        reset();
    }

    public AffineTransform getTransform(){
        return transform;
    }

    public T getGeometry(){
        return geometry;
    }

    public abstract void reset();

}
