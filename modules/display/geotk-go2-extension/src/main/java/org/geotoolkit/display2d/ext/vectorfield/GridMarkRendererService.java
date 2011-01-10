/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.ext.vectorfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.map.MapLayer;


/**
 * Renderer for vector field arrows.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GridMarkRendererService extends AbstractSymbolizerRendererService<VectorFieldSymbolizer,CachedVectorFieldSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<VectorFieldSymbolizer> getSymbolizerClass() {
        return VectorFieldSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedVectorFieldSymbolizer> getCachedSymbolizerClass() {
        return CachedVectorFieldSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedVectorFieldSymbolizer createCachedSymbolizer(final VectorFieldSymbolizer symbol) {
        return new CachedVectorFieldSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedVectorFieldSymbolizer symbol, final RenderingContext2D context) {
        return new GridMarkRenderer(symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(final CachedVectorFieldSymbolizer symbol, final MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 25, 25);
    }

    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rect, final CachedVectorFieldSymbolizer symbol, final MapLayer layer) {
        Shape arrow = new Arrow2D(rect.getMinX(), rect.getMinY(), rect.getWidth()-1, rect.getHeight()-1);
        g.setColor(Color.GRAY);
        g.fill(arrow);
    }

}
