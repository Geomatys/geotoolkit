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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.map.MapLayer;

import org.opengis.style.PointSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPointSymbolizerRendererService extends AbstractSymbolizerRendererService<PointSymbolizer, CachedPointSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<PointSymbolizer> getSymbolizerClass() {
        return PointSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedPointSymbolizer> getCachedSymbolizerClass() {
        return CachedPointSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedPointSymbolizer createCachedSymbolizer(final PointSymbolizer symbol) {
        return new CachedPointSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedPointSymbolizer symbol, final RenderingContext2D context) {
        return new DefaultPointSymbolizerRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(final Graphics2D g2d, final Rectangle2D rectangle, final CachedPointSymbolizer symbol, final MapLayer layer) {
        g2d.setClip(rectangle);
        
        final Object feature = null;
        final float coeff = 1;
        final BufferedImage img = symbol.getImage(feature, coeff, null);
        final float[] disps = new float[]{0,0};
        final float[] anchor = new float[]{0.5f,0.5f};
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final int x = (int) (-img.getWidth()*anchor[0] + rectangle.getCenterX() + disps[0]);
        final int y = (int) (-img.getHeight()*anchor[1] + rectangle.getCenterY() - disps[1]);
        g2d.drawImage(img, x, y, null);
    }

}
