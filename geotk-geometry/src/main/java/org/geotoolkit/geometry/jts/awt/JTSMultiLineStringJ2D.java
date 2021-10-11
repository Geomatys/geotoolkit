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

import org.locationtech.jts.geom.MultiLineString;
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
public class JTSMultiLineStringJ2D extends AbstractJTSGeometryJ2D<MultiLineString> {

    private final JTSMultiLineIterator iterator;

    public JTSMultiLineStringJ2D(final MultiLineString geom) {
        super(geom);
        iterator = new JTSMultiLineIterator(geom,null);
    }

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public JTSMultiLineStringJ2D(final MultiLineString geom, final MathTransform trs) {
        super(geom, trs);
        iterator = new JTSMultiLineIterator(geom,trs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setGeometry(final MultiLineString g) {
        super.setGeometry(g);
        iterator.setGeometry(g);
    }

    @Override
    public JTSMultiLineStringJ2D clone() {
        return new JTSMultiLineStringJ2D(this.geometry,this.transform);
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
            concat = MathTransforms.concatenate(transform,new AffineTransform2D(at));
        }

        iterator.setTransform(concat);
        return iterator;
    }

}
