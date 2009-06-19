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

import java.util.List;
import java.util.logging.Level;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.DynamicMapLayer;

import org.opengis.display.primitive.Graphic;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 */
public class StatelessDynamicLayerJ2D extends AbstractLayerJ2D<DynamicMapLayer>{
    
    
    public StatelessDynamicLayerJ2D(final ReferencedCanvas2D canvas, final DynamicMapLayer layer){
        super(canvas, layer);
    }
    
    /**
     * We asume the visibility test is already done when you call this method
     * This method is made for use in mutlithread.
     */
    public Object query(final RenderingContext2D renderingContext) throws PortrayalException{
        //we do not handle dynamic layers, the distant server does it
        return layer.query(renderingContext);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D renderingContext) {
                
        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;        
        try {
            //we do not handle dynamic layers, the distant server does it
            layer.portray(renderingContext);
        } catch (PortrayalException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
        }
    }
        
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        //since this is a distant source, we have no way to find a child graphic.
        graphics.add(this);
        return graphics;
    }

}
