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

package org.geotoolkit.display2d.primitive;

import java.awt.Shape;

import org.geotoolkit.display.primitive.DefaultSearchArea;

import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.geometry.Geometry;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSearchAreaJ2D extends DefaultSearchArea implements SearchAreaJ2D{
    
    private final com.vividsolutions.jts.geom.Geometry objectiveGeometryJTS;
    private final com.vividsolutions.jts.geom.Geometry displayGeometryJTS;
    private final Shape objectiveShape;
    private final Shape displayShape;

    public DefaultSearchAreaJ2D(final SearchArea search) {
        super(search);
        this.objectiveGeometryJTS   = null;
        this.displayGeometryJTS     = null;
        this.objectiveShape         = GO2Utilities.toJava2D(search.getObjectiveGeometry());
        this.displayShape           = GO2Utilities.toJava2D(search.getDisplayGeometry());
    }

    public DefaultSearchAreaJ2D(
            final Geometry objectiveGeometryISO, final Geometry displayGeometryISO,
            final com.vividsolutions.jts.geom.Geometry objectiveGeometryJTS,
            final com.vividsolutions.jts.geom.Geometry displayGeometryJTS,
            final Shape objectiveShape, final Shape displayShape) {
        super(objectiveGeometryISO,displayGeometryISO);
        this.objectiveGeometryJTS   = objectiveGeometryJTS;
        this.displayGeometryJTS     = displayGeometryJTS;
        this.objectiveShape         = objectiveShape;
        this.displayShape           = displayShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS() {
        return objectiveGeometryJTS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS() {
        return displayGeometryJTS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getObjectiveShape() {
        return objectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getDisplayShape() {
        return displayShape;
    }

}
