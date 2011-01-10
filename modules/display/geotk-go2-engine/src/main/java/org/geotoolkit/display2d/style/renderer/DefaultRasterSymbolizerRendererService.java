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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.map.MapLayer;

import org.opengis.style.RasterSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultRasterSymbolizerRendererService extends AbstractSymbolizerRendererService<RasterSymbolizer, CachedRasterSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<RasterSymbolizer> getSymbolizerClass() {
        return RasterSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedRasterSymbolizer> getCachedSymbolizerClass() {
        return CachedRasterSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedRasterSymbolizer createCachedSymbolizer(final RasterSymbolizer symbol) {
        return new CachedRasterSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedRasterSymbolizer symbol, final RenderingContext2D context) {
        return new DefaultRasterSymbolizerRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rectangle, final CachedRasterSymbolizer symbol, final MapLayer layer) {

        final float[] fractions = new float[3];
        final Color[] colors = new Color[3];
        final MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        final MultipleGradientPaint.ColorSpaceType colorSpace = MultipleGradientPaint.ColorSpaceType.SRGB;

        fractions[0] = 0.0f;
        fractions[1] = 0.5f;
        fractions[2] = 1f;

        colors[0] = Color.RED;
        colors[1] = Color.GREEN;
        colors[2] = Color.BLUE;

        final LinearGradientPaint paint = new LinearGradientPaint(
            new Point2D.Double(rectangle.getMinX(),rectangle.getMinY()),
            new Point2D.Double(rectangle.getMaxX(),rectangle.getMinY()),
            fractions,
            colors,
            cycleMethod
        );

        g.setPaint(paint);
        g.fill(rectangle);
    }

}
