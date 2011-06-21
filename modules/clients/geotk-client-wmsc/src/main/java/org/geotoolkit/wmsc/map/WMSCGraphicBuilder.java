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
package org.geotoolkit.wmsc.map;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wms.map.WMSGraphicBuilder;

import org.opengis.display.canvas.Canvas;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCGraphicBuilder extends WMSGraphicBuilder {
        
    /**
     * One instance for all WMS-C map layers. Object is concurrent.
     */
    static final WMSCGraphicBuilder INSTANCE = new WMSCGraphicBuilder();
    
    protected WMSCGraphicBuilder(){        
    }
    
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof WMSCMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new WMSCGraphic((J2DCanvas)canvas, (WMSCMapLayer)layer));
        }else{
            return Collections.emptyList();
        }
    }
    
}
