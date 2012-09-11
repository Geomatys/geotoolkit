/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Johann Sorel
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
package org.geotoolkit.display3d.container;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.*;
import org.opengis.display.canvas.Canvas;

/**
 * Default graphic builder for maplayer objects.
 * Created graphics will try to create a 3d representation of the layer style.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultMapLayerGraphicBuilder implements GraphicBuilder<A3DGraphic> {

    @Override
    public Collection<A3DGraphic> createGraphics(final MapLayer layer, final Canvas cvs) {
        if(!(cvs instanceof A3DCanvas)){
            return Collections.emptyList();
        }
        
        final Collection<A3DGraphic> graphics = new ArrayList<A3DGraphic>();
        final A3DCanvas canvas = (A3DCanvas) cvs;
        
        final A3DGraphic graphic;
        if (layer instanceof FeatureMapLayer){
            graphic = new FeatureLayerNode(canvas, (FeatureMapLayer)layer);
        }else if (layer instanceof CollectionMapLayer){
            graphic = new CollectionLayerNode(canvas, (CollectionMapLayer)layer);
        }else if (layer instanceof CoverageMapLayer){
            graphic = new CoverageLayerNode(canvas, (CoverageMapLayer)layer);
        }else {
            graphic = new MapLayerNode(canvas, (MapLayer)layer);
        }
        graphics.add(graphic);
        
        return graphics;
    }

    @Override
    public Class<A3DGraphic> getGraphicType() {
        return A3DGraphic.class;
    }

    @Override
    public Image getLegend(MapLayer layer) throws PortrayalException {
        return null;
    }
    
}
