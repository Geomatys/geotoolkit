/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.ext.pie;

import java.awt.*;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 * Pie symbolizer service.
 *
 * @author Johann Sorel (Geomays)
 * @author Cédric Briançon (Geomatys)
 */
public class PieSymbolizerRendererService extends AbstractSymbolizerRendererService<PieSymbolizer, CachedPieSymbolizer> {

    @Override
    public boolean isGroupSymbolizer() {
        return true;
    }

    @Override
    public Class<PieSymbolizer> getSymbolizerClass() {
        return PieSymbolizer.class;
    }

    @Override
    public Class<CachedPieSymbolizer> getCachedSymbolizerClass() {
        return CachedPieSymbolizer.class;
    }

    @Override
    public CachedPieSymbolizer createCachedSymbolizer(PieSymbolizer symbol) {
        return new CachedPieSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedPieSymbolizer symbol, RenderingContext2D context) {
        return new PieSymbolizerRenderer(this,symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedPieSymbolizer symbolizer, MapLayer layer) {
        final List<PieSymbolizer.ColorQuarter> colorQuarters = symbolizer.getSource().getColorQuarters();
        final int height = (colorQuarters.size() + 1) * 20;
        return new Rectangle2D.Double(0, 0, 150, height);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedPieSymbolizer symbolizer, MapLayer layer) {
        //defensive copy
        if(symbolizer==null) return;
        g.setClip(rectangle);

        final List<PieSymbolizer.ColorQuarter> colorQuarters = symbolizer.getSource().getColorQuarters();

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Font font = new Font("Monospaced", Font.BOLD, 10);
        g2d.setFont(font);
        final FontMetrics fm = g2d.getFontMetrics(font);
        final int fabove = fm.getAscent();

        if(colorQuarters.isEmpty()) return;

        int line = 1;
        for (final PieSymbolizer.ColorQuarter colorQuarter : colorQuarters) {
            g.setStroke(new BasicStroke(1));
            g.setPaint(Color.BLACK);
            final Rectangle2D rect = new Rectangle2D.Double(5, line * (fabove + 5), 30, fabove);
            g.draw(rect);

            g.setPaint(colorQuarter.getColor().evaluate(null, Color.class));
            g.fill(rect);

            g.setPaint(Color.BLACK);
            g.drawString(colorQuarter.getQuarter().evaluate(null, String.class), 45, line * (fabove + 5) + fabove);

            line++;
        }

        g.setStroke(new BasicStroke(1));
        g.setPaint(Color.BLACK);
        final Rectangle2D rect = new Rectangle2D.Double(5, line * (fabove + 5), 30, fabove);
        g.draw(rect);

        g.setPaint(Color.GRAY);
        g.fill(rect);

        g.setPaint(Color.BLACK);
        g.drawString("Others", 45, line * (fabove + 5) + fabove);
    }

}
