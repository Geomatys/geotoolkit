/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.exception.PortrayalException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author olivier terral (Geomatys)
 * @module pending
 */
public interface DynamicMapLayer extends MapLayer{

    /**
     * Query the distant layer and retour the result Image.
     * This method may take time since the server may be long to answer.
     * 
     * @return should be one of those : File(to image file), URL(to image file),
     * BufferedImage, RenderedImage.
     * The result Image will be painted at coordinate 0,0 in display CRS
     */
    public Object query(RenderingContext context) throws PortrayalException;
    
    /**
     * Ask the distant layer to paint directly on the canvas.
     * This can avoid creating unnecessary buffers if there is only one layer to portray.
     */
    public void portray(RenderingContext context) throws PortrayalException;
    
    /**
     * Ask the distant layer to provide a legend.
     * It might not be always possible but at least we can try.
     * 
     * @return image legend or null.
     * @throws PortrayalException
     */
    public Image getLegend() throws PortrayalException;

}
