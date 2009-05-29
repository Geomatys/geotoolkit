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
package org.geotoolkit.display.primitive;

import org.opengis.geometry.Geometry;

/**
 * Default implementation of search area.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSearchArea implements SearchArea{

    public final Geometry objectiveGeometryISO;
    public final Geometry displayGeometryISO;

    public DefaultSearchArea(SearchArea search) {
        this(search.getObjectiveGeometry(), search.getDisplayGeometry());
    }

    public DefaultSearchArea(Geometry objectiveGeometryISO, Geometry displayGeometryISO) {
        this.objectiveGeometryISO = objectiveGeometryISO;
        this.displayGeometryISO = displayGeometryISO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getDisplayGeometry() {
        return displayGeometryISO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getObjectiveGeometry() {
        return objectiveGeometryISO;
    }

}
