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
package org.geotoolkit.display2d.container.stateless;

import java.util.Collection;
import java.util.List;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;

import org.opengis.display.primitive.Graphic;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module pending
 */
public class StatelessDynamicLayerJ2D extends AbstractLayerJ2D<MapLayer>{
    
    
    public StatelessDynamicLayerJ2D(final ReferencedCanvas2D canvas, final MapLayer layer){
        super(canvas, layer);
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D renderingContext) {
                
        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;

        final GraphicBuilder<? extends GraphicJ2D> gb = layer.getGraphicBuilder(GraphicJ2D.class);

        if(gb != null){
            final Collection<? extends GraphicJ2D> graphics = gb.createGraphics(layer, canvas);
            for(GraphicJ2D g : graphics){
                g.paint(renderingContext);
            }
        }
    }
        
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {

        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(layer, canvas);
            for(final GraphicJ2D gra : gras){
                graphics = gra.getGraphicAt(context, mask, filter,graphics);
            }
            return graphics;
        }else{
            //since this is a custom layer, we have no way to find a child graphic.
            graphics.add(this);
            return graphics;
        }
    }

}
