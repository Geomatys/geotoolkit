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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.map.MapLayer;

import org.opengis.filter.expression.Expression;
import org.opengis.style.TextSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTextSymbolizerRendererService extends AbstractSymbolizerRendererService<TextSymbolizer, CachedTextSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<TextSymbolizer> getSymbolizerClass() {
        return TextSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedTextSymbolizer> getCachedSymbolizerClass() {
        return CachedTextSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedTextSymbolizer createCachedSymbolizer(final TextSymbolizer symbol) {
        return new CachedTextSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(final CachedTextSymbolizer symbol, final RenderingContext2D context) {
        return new DefaultTextSymbolizerRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rectangle, final CachedTextSymbolizer symbol, final MapLayer layer) {
        g.setClip(rectangle);

        final String family;
        if(!symbol.getSource().getFont().getFamily().isEmpty()){
            family = symbol.getSource().getFont().getFamily().get(0).toString();
        }else{
            family = "Dialog";
        }

        final Font font = new Font(family, Font.PLAIN, (int)rectangle.getHeight()/2);
        final FontRenderContext frc = g.getFontRenderContext();
        final GlyphVector glyphVector = font.createGlyphVector(frc, "T");
        final Shape shape = glyphVector.getOutline();

        g.translate(rectangle.getMinX()+3, rectangle.getMaxY()-3);

        if(symbol.getHalo() != null){

            Paint paint = null;
            float width = 1;

            if(GO2Utilities.isStatic(symbol.getSource().getHalo().getFill().getColor())){
                paint = symbol.getSource().getHalo().getFill().getColor().evaluate(null, Color.class);
            }

            if(paint == null){
                paint = Color.WHITE;
            }

            final Expression expWidth = symbol.getSource().getHalo().getRadius();
            if(GO2Utilities.isStatic(expWidth)){
                width = expWidth.evaluate(null, Number.class).floatValue();
            }else{
                width = 1;
            }

            if(width > 0){
                g.setStroke(new BasicStroke(width*2+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setPaint(paint);
                g.draw(shape);
            }
        }


        Paint paint = null;
        if(GO2Utilities.isStatic(symbol.getSource().getFill().getColor())){
            paint = symbol.getSource().getFill().getColor().evaluate(null, Color.class);
        }

        if(paint == null){
            paint = Color.BLACK;
        }

        g.setPaint(paint);
        g.setFont(font);
        g.drawString("T", 0, 0);
    }

}
