/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.wmts.map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;

import org.opengis.display.canvas.Canvas;

/**
 * Render WMTS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMTSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{
                    
    /**
     * One instance for all WMTS map layers. Object is concurrent.
     */
    static final WMTSGraphicBuilder INSTANCE = new WMTSGraphicBuilder();
    
    private WMTSGraphicBuilder(){};
    
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof WMTSMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new WMTSGraphic((J2DCanvas)canvas, (WMTSMapLayer)layer));
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(final MapLayer layer) throws PortrayalException {
        final WMTSMapLayer wmtsLayer = (WMTSMapLayer) layer;
        
        //TODO : how could we generate a proper legend for this layer ...
        final BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

}
