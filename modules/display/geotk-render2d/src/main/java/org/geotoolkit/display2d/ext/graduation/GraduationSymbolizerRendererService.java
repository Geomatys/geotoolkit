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
package org.geotoolkit.display2d.ext.graduation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GraduationSymbolizerRendererService extends AbstractSymbolizerRendererService<GraduationSymbolizer,CachedGraduationSymbolizer>{

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<GraduationSymbolizer> getSymbolizerClass() {
        return GraduationSymbolizer.class;
    }

    @Override
    public Class<CachedGraduationSymbolizer> getCachedSymbolizerClass() {
        return CachedGraduationSymbolizer.class;
    }

    @Override
    public CachedGraduationSymbolizer createCachedSymbolizer(GraduationSymbolizer symbol) {
        return new CachedGraduationSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedGraduationSymbolizer symbol, RenderingContext2D context) {
        return new GraduationSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedGraduationSymbolizer symbol, MapLayer layer) {

        final double stepMajor = rect.getWidth()/2;
        final double stepMinor = rect.getWidth()/10;
        final double minX = rect.getMinX();
        final double minY = rect.getMaxY();
        final double maxMajorY = minY - rect.getHeight()/2;
        final double maxMinorY = minY - rect.getHeight()/3;

        //draw minor ticks
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.DARK_GRAY);
        for(int i=0;i<11;i++){
            final int x = (int)(minX + i*stepMinor);
            g.drawLine(x, (int)minY, x, (int)maxMinorY);
        }

        //draw major ticks
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.RED);
        for(int i=0;i<3;i++){
            final int x = (int)(minX + i*stepMajor);
            g.drawLine(x, (int)minY, x, (int)maxMajorY);
        }
    }

}
