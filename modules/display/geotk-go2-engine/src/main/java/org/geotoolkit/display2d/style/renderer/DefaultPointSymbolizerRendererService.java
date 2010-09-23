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


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.map.MapLayer;

import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
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
    public CachedPointSymbolizer createCachedSymbolizer(PointSymbolizer symbol) {
        return new CachedPointSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SymbolizerRenderer createRenderer(CachedPointSymbolizer symbol, RenderingContext2D context) {
        return new DefaultPointSymbolizerRenderer(symbol, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(Graphics2D target, Rectangle2D rectangle, CachedPointSymbolizer symbol, MapLayer layer) {
        target.setClip(rectangle);
        
        final Expression expOpa = symbol.getSource().getGraphic().getOpacity();
        final Expression expRotation = symbol.getSource().getGraphic().getRotation();
        final Expression expSize = symbol.getSource().getGraphic().getSize();
        
        final float opacity;
        final float rotation;
        float size;
        
        if(GO2Utilities.isStatic(expOpa)){
            opacity = expOpa.evaluate(null, Number.class).floatValue();
        }else{
            opacity = 0.6f;
        }
                
        if(GO2Utilities.isStatic(expRotation)){
            rotation = expRotation.evaluate(null, Number.class).floatValue();
        }else{
            rotation = 0f;
        }
        
        if(GO2Utilities.isStatic(expSize)){
            Number n = expSize.evaluate(null, Number.class);
            if(n != null){
                size = n.floatValue();
            }else{
                size = 8f;
            }
        }else{
            size = 8f;
        }
        
        if(size> rectangle.getHeight()){
            size = (float)rectangle.getHeight();
        }
        
        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.translate(rectangle.getCenterX(), rectangle.getCenterY());
                
        target.rotate(Math.toRadians(rotation), 0,0);
        
        for(final GraphicalSymbol graphic : symbol.getSource().getGraphic().graphicalSymbols()){
            if(graphic instanceof Mark){
                GO2Utilities.renderGraphic((Mark) graphic,size,target);
            }
        }
        
        target.rotate(-Math.toRadians(rotation), 0,0);

    }

}
