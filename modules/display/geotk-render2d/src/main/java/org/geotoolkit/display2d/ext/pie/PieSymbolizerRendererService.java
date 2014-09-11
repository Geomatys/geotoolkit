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

import com.vividsolutions.jts.geom.Geometry;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.NonSI;
import org.geotoolkit.display2d.GO2Utilities;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;

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
    public Rectangle2D glyphPreferredSize(CachedPieSymbolizer symbol, MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 30,30);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedPieSymbolizer symbolizer, MapLayer layer) {
        //defensive copy
        if(symbolizer==null) return;

        final java.util.List<PieSymbolizer.Group> groups = symbolizer.getSource().getGroups();

        double centerX = rect.getCenterX();
        double centerY = rect.getCenterY();

        final Dimension dim = rect.getBounds().getSize();
        final double pieSize = Math.min(dim.width, dim.height) - 6;
        if(pieSize<6){
            //too small
            return;
        }

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Font font = new Font("Monospaced", Font.BOLD, 12);
        g2d.setFont(font);
        final FontMetrics fm = g2d.getFontMetrics(font);
        final int fabove = fm.getAscent();

        if(groups.isEmpty()) return;

        final double degrees = 360.0 / groups.size();
        double startDegree = 90;
        for(int i=0,n=groups.size();i<n;i++){
            PieSymbolizer.Group group = groups.get(i);

            final Arc2D arc = new Arc2D.Double(centerX-pieSize/2, centerY-pieSize/2, pieSize, pieSize, startDegree, degrees, Arc2D.PIE);
            final Geometry arcGeo = JTS.shapeToGeometry(arc, PieSymbolizerRenderer.GF);
            final double arcCenterX = arcGeo.getCentroid().getX();
            final double arcCenterY = arcGeo.getCentroid().getY();

            final Fill fill = group.getFill();
            final org.opengis.style.Stroke stroke = group.getStroke();

            if(fill != null){
                GO2Utilities.renderFill(arc, fill, g2d);
            }
            if(stroke != null){
                GO2Utilities.renderStroke(arc, stroke, NonSI.PIXEL, g2d);
            }

            final Expression textfill = group.getText();
            Color color = Color.GRAY;
            if(textfill!=null){
                color = textfill.evaluate(null, Color.class);
            }

            if(color!=null){
                final String text = String.valueOf(i);
                final int textSize = fm.stringWidth(text);
                g2d.setColor(color);
                g2d.drawString(text, (int)(arcCenterX-textSize/2), (int)(arcCenterY+fabove/2) );
            }

            startDegree -= degrees;
        }
    }

}
