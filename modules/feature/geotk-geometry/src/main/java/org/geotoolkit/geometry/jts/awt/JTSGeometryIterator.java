/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.geometry.jts.awt;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import static org.geotoolkit.geometry.jts.awt.AbstractJTSGeometryJ2D.LOGGER;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Simple abstract path iterator for JTS Geometry.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public abstract class JTSGeometryIterator<T extends Geometry> implements PathIterator {

    static final AffineTransform2D IDENTITY = new AffineTransform2D(new AffineTransform());

    protected MathTransform transform;
    protected T geometry;

    protected JTSGeometryIterator(final MathTransform trs){
        this(null,trs);
    }

    protected JTSGeometryIterator(final T geometry, final MathTransform trs){
        this.transform = (trs == null) ? IDENTITY : trs;
        this.geometry = geometry;
    }

    public void setGeometry(final T geom){
        this.geometry = geom;
    }

    public void setTransform(final MathTransform trs){
        this.transform = (trs == null) ? IDENTITY : trs;
        reset();
    }

    public MathTransform getTransform(){
        return transform;
    }

    public T getGeometry(){
        return geometry;
    }

    public abstract void reset();

    protected void safeTransform(float[] in, int offset, float[] out, int outOffset, int nb) {
        try {
            transform.transform(in, offset, out, outOffset, nb);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            Arrays.fill(out, outOffset, outOffset+nb*2, Float.NaN);
        }
    }

    protected void safeTransform(double[] in, int offset, float[] out, int outOffset, int nb) {
        try {
            transform.transform(in, offset, out, outOffset, nb);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            Arrays.fill(out, outOffset, outOffset+nb*2, Float.NaN);
        }
    }

    protected void safeTransform(double[] in, int offset, double[] out, int outOffset, int nb) {
        try {
            transform.transform(in, offset, out, outOffset, nb);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            Arrays.fill(out, outOffset, outOffset+nb*2, Double.NaN);
        }
    }
}
