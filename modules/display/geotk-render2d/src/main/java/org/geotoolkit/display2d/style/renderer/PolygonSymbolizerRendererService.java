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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;

/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PolygonSymbolizerRendererService extends AbstractSymbolizerRendererService<PolygonSymbolizer, CachedPolygonSymbolizer>{


    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

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
    public CachedPolygonSymbolizer createCachedSymbolizer(final PolygonSymbolizer symbol) {
        return new CachedPolygonSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedPolygonSymbolizer symbol, final RenderingContext2D context) {
        return new PolygonSymbolizerRenderer(this, symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rectangle, final CachedPolygonSymbolizer symbol, final MapLayer layer) {
        final AffineTransform affine = new AffineTransform(rectangle.getWidth(), 0, 0,
                rectangle.getHeight(), rectangle.getX(), rectangle.getY());

        g.setClip(rectangle);
        final TransformedShape shape = new TransformedShape();
        shape.setOriginalShape(GO2Utilities.GLYPH_POLYGON);
        shape.setTransform(affine);

        final Fill fill = symbol.getSource().getFill();
        final Stroke stroke = symbol.getSource().getStroke();
        if(fill != null){
            GO2Utilities.renderFill(shape, symbol.getSource().getFill(), g);
        }
        if(stroke != null){
            GO2Utilities.renderStroke(shape, symbol.getSource().getStroke(), symbol.getSource().getUnitOfMeasure(), g);
        }
    }

}
