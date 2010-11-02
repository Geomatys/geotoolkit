/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.map;

import java.awt.Image;
import java.util.Collection;

import org.geotoolkit.display.exception.PortrayalException;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;

/**
 * A graphic builder is a convinient way to build the same datas in a different
 * collection of graphic object, with different rendering and behavior.
 * 
 * For exemple a maplayer may provide a graphic builder for Java2D graphics
 * and another one for 3D rendering.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GraphicBuilder<T extends Graphic> {

    /**
     * Build a collection of graphics from the provided layer and canvas.
     * 
     * @param layer : Maplayer data source
     * @param canvas : Rendering canvas
     * @return Collection<Graphic> may not be null but can be empty.
     */
    Collection<T> createGraphics(MapLayer layer, Canvas canvas);
    
    /**
     * Returns the graphic type of this builder.
     * return class extends Graphic.
     */
    Class<T> getGraphicType();

    /**
     * Ask the distant layer to provide a legend.
     * It might not be always possible but at least we can try.
     *
     * @return image legend or null.
     * @throws PortrayalException
     */
    public Image getLegend(MapLayer layer) throws PortrayalException;

}
