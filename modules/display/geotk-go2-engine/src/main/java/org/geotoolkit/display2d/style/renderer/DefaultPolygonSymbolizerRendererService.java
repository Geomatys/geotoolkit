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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.map.MapLayer;

import org.opengis.style.PolygonSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPolygonSymbolizerRendererService extends AbstractSymbolizerRendererService<PolygonSymbolizer, CachedPolygonSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<PolygonSymbolizer> getSymbolizerClass() {
        return PolygonSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedPolygonSymbolizer> getCachedSymbolizerClass() {
        return CachedPolygonSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedPolygonSymbolizer createCachedSymbolizer(PolygonSymbolizer symbol) {
        return new CachedPolygonSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(CachedPolygonSymbolizer symbol, RenderingContext2D context) {
        return new DefaultPolygonSymbolizerRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedPolygonSymbolizer symbol, MapLayer layer) {
        final AffineTransform affine = new AffineTransform(rectangle.getWidth(), 0, 0,
                rectangle.getHeight(), rectangle.getX(), rectangle.getY());

        g.setClip(rectangle);
        final TransformedShape shape = new TransformedShape();
        shape.setOriginalShape(GO2Utilities.GLYPH_POLYGON);
        shape.setTransform(affine);

        GO2Utilities.renderFill(shape, symbol.getSource().getFill(), g);
        GO2Utilities.renderStroke(shape, symbol.getSource().getStroke(), symbol.getSource().getUnitOfMeasure(), g);
    }

}
