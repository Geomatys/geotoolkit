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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import org.apache.sis.internal.map.coverage.RenderingWorkaround;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PointPresentation extends Grid2DPresentation {

    public AlphaComposite composite = GO2Utilities.ALPHA_COMPOSITE_1F;
    public AffineTransform displayTransform;
    public RenderedImage image;

    public PointPresentation(MapLayer layer, Resource resource, Feature feature) {
        super(layer, resource, feature);
    }

    @Override
    public boolean paint(RenderingContext2D renderingContext) {
        renderingContext.switchToDisplayCRS();
        Graphics2D g2d = renderingContext.getGraphics();
        g2d.setComposite(composite);
        g2d.drawRenderedImage(RenderingWorkaround.wrap(image), displayTransform);
        return true;
    }

    @Override
    public boolean hit(RenderingContext2D renderingContext, SearchAreaJ2D search) {
        final Shape mask = search.getDisplayShape();
        final Area area = new Area(mask);

        final Rectangle2D rect = new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
        final Shape shp = displayTransform.createTransformedShape(rect);
        final Area area2 = new Area(shp);

        area.intersect(area2);
        return !area.isEmpty();
    }
}
