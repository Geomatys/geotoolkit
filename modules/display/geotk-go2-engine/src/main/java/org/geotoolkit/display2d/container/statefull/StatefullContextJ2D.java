/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.display2d.container.statefull;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.stateless.StatelessMapItemJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;

/**
 * Extend stateless map item j2d and create statefull childs when possible.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullContextJ2D extends StatelessMapItemJ2D<MapContext> {

    public StatefullContextJ2D(final J2DCanvas canvas, final MapContext context) {
        super(canvas, context);
    }
   
    @Override
    protected GraphicJ2D parseChild(final MapItem item, int index){

        if (item instanceof FeatureMapLayer){
            final StatefullFeatureLayerJ2D g2d = new StatefullFeatureLayerJ2D(getCanvas(), (FeatureMapLayer)item);
            g2d.setParent(this);
            g2d.setZOrderHint(index);
            return g2d;
        }else if (item instanceof CoverageMapLayer){
            final StatefullCoverageLayerJ2D g2d = new StatefullCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)item);
            g2d.setParent(this);
            g2d.setZOrderHint(index);
            return g2d;
        }
        return super.parseChild(item, index);
    }

}
