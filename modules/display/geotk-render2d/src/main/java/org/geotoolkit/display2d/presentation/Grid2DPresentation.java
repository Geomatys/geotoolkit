/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.presentation;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.renderer.AbstractPresentation;
import org.opengis.feature.Feature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Grid2D presentation is a presentation used in a 2D view expected to
 * be rendered on a regular grid.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Grid2DPresentation extends AbstractPresentation {

    public CoordinateReferenceSystem objectiveCrs;
    public CoordinateReferenceSystem displayCrs;
    public MathTransform objToDisplay;

    public Grid2DPresentation(MapLayer layer, Feature feature) {
        super(layer, feature);
    }

    public void forGrid(RenderingContext2D ctx) {
        objectiveCrs = ctx.getObjectiveCRS2D();
        displayCrs = ctx.getDisplayCRS();
        objToDisplay = ctx.getObjectiveToDisplay();
    }

}
