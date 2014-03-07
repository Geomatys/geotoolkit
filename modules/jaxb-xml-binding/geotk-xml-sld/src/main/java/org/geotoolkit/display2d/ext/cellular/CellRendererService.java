/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display2d.ext.cellular;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.DefaultPointSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.style.PointSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellRendererService extends AbstractSymbolizerRendererService<CellSymbolizer,CachedCellSymbolizer>{

    private static final int HEADER_SIZE = 16;
    private static final Font HEADER_FONT = new Font("monospaced", Font.PLAIN, 11);
    private static final FontMetrics FM = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(HEADER_FONT);
    
    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<CellSymbolizer> getSymbolizerClass() {
        return CellSymbolizer.class;
    }

    @Override
    public Class<CachedCellSymbolizer> getCachedSymbolizerClass() {
        return CachedCellSymbolizer.class;
    }

    @Override
    public CachedCellSymbolizer createCachedSymbolizer(CellSymbolizer symbol) {
        return new CachedCellSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedCellSymbolizer symbol, RenderingContext2D context) {
        return new CellSymbolizerRenderer(this, symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedCellSymbolizer symbol, MapLayer layer) {
        
        //fake layer
        final FeatureMapLayer fakelayer = mimicCellLayer(layer);
        
        if(layer instanceof CoverageMapLayer && symbol.getSource().getPointSymbolizer() != null){
            //generate 4 arrows base on an approximate size
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final PointSymbolizer ps = symbol.getSource().getPointSymbolizer();
            final Expression exp = ps.getGraphic().getSize();
            String text = getTitle(ps);
            
            int width = FM.stringWidth(text);
            width = Math.max(100, width);
            
            Dimension dim = new Dimension(width,32*3+HEADER_SIZE);
            return new Rectangle2D.Double(0, 0, dim.width, dim.height);
            
        }else{
            Dimension dim = new Dimension(5,5);
            final CachedRule r = symbol.getCachedRule();
            dim = DefaultGlyphService.glyphPreferredSize(r.getSource(), dim, fakelayer);

            dim.width = dim.width*2;
            dim.height = dim.height*2;
            return new Rectangle2D.Double(0, 0, dim.width, dim.height);
        }
        
    }
    
    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedCellSymbolizer symbol, MapLayer layer) {
        final double halfwidth = rect.getWidth()/2;
        final double halfheight = rect.getHeight()/2;
        
        //fake layer
        final FeatureMapLayer fakelayer = mimicCellLayer(layer);
                
        if(layer instanceof CoverageMapLayer && symbol.getSource().getPointSymbolizer() != null){
            //generate 4 arrows base on an approximate size
            final PointSymbolizer ps = symbol.getSource().getPointSymbolizer();
            final String text = getTitle(ps);
            
            g.setColor(Color.BLACK);
            g.setFont(HEADER_FONT);
            g.drawString(text, (int)rect.getX()+2, HEADER_SIZE-5);
            
            final double best = (rect.getHeight() - HEADER_SIZE)/3 ;
            
            final DefaultPointSymbolizerRendererService srs = new DefaultPointSymbolizerRendererService();
            final CachedPointSymbolizer cps = symbol.getCachedPointSymbolizer();
            
            //first symbol at 1/1 size
            Rectangle.Double rectA = new Rectangle.Double(rect.getX(), rect.getY()+HEADER_SIZE, best, best);
            srs.glyph(g, rectA, cps, fakelayer, (float)best);
            g.setColor(Color.BLACK);
            g.drawString(NumberFormat.getNumberInstance().format(best), (int)rectA.getMaxX(), (int)rectA.getCenterY());
            
            //second symbol at 1/2 size
            rectA = new Rectangle.Double(rect.getX(), rect.getY()+HEADER_SIZE+best, best, best);
            srs.glyph(g, rectA, cps, fakelayer, (float)best*0.6f);
            g.setColor(Color.BLACK);
            g.drawString(NumberFormat.getNumberInstance().format(best*0.6), (int)rectA.getMaxX(), (int)rectA.getCenterY());
            
            //thrid symbol at 1/10 size
            rectA = new Rectangle.Double(rect.getX(), rect.getY()+HEADER_SIZE+best*2, best, best);
            srs.glyph(g, rectA, cps, fakelayer, (float)best*0.2f);
            g.setColor(Color.BLACK);
            g.drawString(NumberFormat.getNumberInstance().format(best*0.2), (int)rectA.getMaxX(), (int)rectA.getCenterY());
            
        }else{
            glyphBlock(g, new Rectangle.Double(rect.getX(),           rect.getY(),            halfwidth, halfheight), symbol, fakelayer);
            glyphBlock(g, new Rectangle.Double(rect.getX(),           rect.getY()+halfheight, halfwidth, halfheight), symbol, fakelayer);
            glyphBlock(g, new Rectangle.Double(rect.getX()+halfwidth, rect.getY(),            halfwidth, halfheight), symbol, fakelayer);
            glyphBlock(g, new Rectangle.Double(rect.getX()+halfwidth, rect.getY()+halfheight, halfwidth, halfheight), symbol, fakelayer);
        }
    }
    
    private void glyphBlock(Graphics2D g, Rectangle2D rect, CachedCellSymbolizer symbol, MapLayer layer){
        final CachedRule r = symbol.getCachedRule();
        DefaultGlyphService.render(r.getSource(), rect, g, layer);
    }
    
    private static FeatureMapLayer mimicCellLayer(MapLayer layer){
        //fake layer
        if(layer instanceof CoverageMapLayer){
            try {
                final SimpleFeatureType sft = CellSymbolizer.buildCellType((CoverageMapLayer)layer);
                layer = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection("", sft), GO2Utilities.STYLE_FACTORY.style());
            } catch (DataStoreException ex) {
                //not important
            }
        }else{
            layer = null;
        }
        return (FeatureMapLayer) layer;
    }
    
    private static String getTitle(PointSymbolizer ps){
        final StringBuilder sb = new StringBuilder();
        String title = ps.getDescription().getTitle().toString();
        if(title!=null && !title.isEmpty()){
            sb.append(sb);
        }
        String desc = ps.getDescription().getAbstract().toString();
        if(desc!=null && !desc.isEmpty()){
            sb.append(' ');
            if(title!=null && !title.isEmpty()) sb.append('(');
            sb.append(desc);
             if(title!=null && !title.isEmpty()) sb.append(')');
        }
        return sb.toString();
    }
    
}
