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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import org.apache.sis.map.MapLayer;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShapePresentation extends Grid2DPresentation {

    public AlphaComposite fillComposite = GO2Utilities.ALPHA_COMPOSITE_1F;
    public AlphaComposite strokeComposite = GO2Utilities.ALPHA_COMPOSITE_1F;
    /**
     * Shape are in display crs
     */
    public Shape shape;
    public Paint fillPaint;
    public Paint strokePaint;
    public Stroke stroke;

    public ShapePresentation(MapLayer layer, Resource resource, Feature feature) {
        super(layer, resource, feature);
    }

    @Override
    public boolean paint(RenderingContext2D renderingContext) {
        if (shape == null) return false;

        renderingContext.switchToDisplayCRS();
        Graphics2D g2d = renderingContext.getGraphics();

        if (fillPaint != null) {
            g2d.setComposite(fillComposite);
            g2d.setPaint(fillPaint);
            g2d.fill(shape);
        }
        if (stroke != null) {
            g2d.setComposite(strokeComposite);
            g2d.setPaint(strokePaint);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }
        return true;
    }

    @Override
    public boolean hit(RenderingContext2D renderingContext, SearchAreaJ2D search) {
        if (shape == null || !shape.intersects(search.getDisplayShape().getBounds2D())) {
            return false;
        }
        final Shape mask = search.getDisplayShape();
        final Area area = new Area(mask);

        if (fillPaint != null) {
            final Area area2 = new Area(shape);
            area.intersect(area2);
            return !area.isEmpty();
        } else if (strokePaint != null) {
            final Area area2 = new Area(stroke.createStrokedShape(shape));
            area.intersect(area2);
            return !area.isEmpty();
        }
        return false;
    }

}
