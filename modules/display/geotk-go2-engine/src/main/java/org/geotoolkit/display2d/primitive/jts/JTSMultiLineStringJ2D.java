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

import com.vividsolutions.jts.geom.MultiLineString;


/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module pending
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
    public JTSMultiLineStringJ2D(final MultiLineString geom, final AffineTransform trs) {
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

        final AffineTransform concat;
        if(at == null){
            concat = transform;
        }else{
            concat = (AffineTransform) transform.clone();
            concat.preConcatenate(at);
        }

        iterator.setTransform(concat);
        return iterator;
    }

}
