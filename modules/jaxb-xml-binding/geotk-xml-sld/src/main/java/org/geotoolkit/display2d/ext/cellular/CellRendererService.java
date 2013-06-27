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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.Rule;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellRendererService extends AbstractSymbolizerRendererService<CellSymbolizer,CachedCellSymbolizer>{

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
    public void glyph(Graphics2D g, Rectangle2D rect, CachedCellSymbolizer symbol, MapLayer layer) {
        final double halfwidth = rect.getWidth()/2;
        final double halfheight = rect.getHeight()/2;
        
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
        
        glyphBlock(g, new Rectangle.Double(rect.getX(),           rect.getY(),            halfwidth, halfheight), symbol, layer);
        glyphBlock(g, new Rectangle.Double(rect.getX(),           rect.getY()+halfheight, halfwidth, halfheight), symbol, layer);
        glyphBlock(g, new Rectangle.Double(rect.getX()+halfwidth, rect.getY(),            halfwidth, halfheight), symbol, layer);
        glyphBlock(g, new Rectangle.Double(rect.getX()+halfwidth, rect.getY()+halfheight, halfwidth, halfheight), symbol, layer);
    }
    
    private void glyphBlock(Graphics2D g, Rectangle2D rect, CachedCellSymbolizer symbol, MapLayer layer){
        for(CachedRule r : symbol.getCachedRules()){
            DefaultGlyphService.render(r.getSource(), rect, g, layer);
        }
    }
    
}
