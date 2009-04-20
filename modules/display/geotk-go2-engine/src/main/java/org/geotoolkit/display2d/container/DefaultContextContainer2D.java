/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.container;

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.container.statefull.StatefullContextGraphicBuilder;
import org.geotoolkit.display2d.container.stateless.StatelessContextGraphicBuilder;
import org.geotoolkit.map.MapContext;

/**
 * Default implementation of context renderer which uses different graphic context
 * builder if statefull or not.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultContextContainer2D extends ContextContainer2D{

    private final ContextGraphicBuilder builder;
    private final Collection<GraphicJ2D> contextGraphics = new ArrayList<GraphicJ2D>();
    private MapContext context = null;


    public DefaultContextContainer2D(final ReferencedCanvas2D canvas, final boolean statefull){
        super(canvas);

        if(statefull){
            builder = new StatefullContextGraphicBuilder();
        }else{
            builder = new StatelessContextGraphicBuilder();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setContext(final MapContext context) {

        if(this.context != null && context != null){
            if(this.context.equals(context)){
                //same context
                return;
            }
        }

        //dispose previous context graphics
        for(final GraphicJ2D graphic : contextGraphics){
            graphic.dispose();
            //remove graphic from the renderer
            remove(graphic);
        }
        contextGraphics.clear();

        this.context = context;

        if(this.context != null){
            //create the new graphics
            for(final GraphicJ2D graphic : builder.createGraphics(getCanvas(),context)){
                contextGraphics.add(graphic);
                //add graphic in the renderer
                add(graphic);
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapContext getContext() {
        return context;
    }

}
