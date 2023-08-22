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
package org.geotoolkit.display2d.canvas;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.map.Presentation;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.opengis.feature.Feature;

/**
 * A visitor which can be applied to the
 * {@link org.geotoolkit.display.primitive.Graphic} objects of a scene and through
 * the {@code Graphic} objects, to the underlying
 * {@link org.opengis.feature.Feature} or
 * {@link GridCoverage}.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractGraphicVisitor implements GraphicVisitor {

    public abstract void visit(Feature feature, RenderingContext2D context, SearchAreaJ2D area);

    public abstract void visit(GridCoverageResource coverage, RenderingContext2D context, SearchAreaJ2D area);

    /**
     * {@inheritDoc }
     */
    @Override
    public void startVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void visit(final Presentation graphic, final RenderingContext context, final SearchArea area) {

        if (graphic == null ) return;

        final Feature feature = (Feature) graphic.getCandidate();
        final MapLayer layer = graphic.getLayer();
        final Resource resource = layer == null ? null : layer.getData();

        if (feature != null) {
            visit(feature, (RenderingContext2D) context, (SearchAreaJ2D) area);
        } else if (resource instanceof GridCoverageResource) {
            visit((GridCoverageResource) resource, (RenderingContext2D) context, (SearchAreaJ2D) area);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStopRequested() {
        return false;
    }
}
