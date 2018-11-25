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
package org.geotoolkit.display2d.style.labeling;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.referencing.operation.TransformException;


/**
 * A Label renderer is used to render labels on top of the image.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface LabelRenderer {

    /**
     * Set the renderingContext associated to this label renderer.
     */
    void setRenderingContext(RenderingContext2D context);

    /**
     * Create a label layer, that can remove labels "on the fly" if needed.
     * @return
     */
    LabelLayer createLabelLayer();

    /**
     * Get the renderingContext associated to this label renderer.
     */
    RenderingContext2D getRenderingContext();

    /**
     * Add a new label to render.
     */
    void append(LabelLayer layer);

    /**
     * Portray all labels in the rendering context.
     * This method shall be called by the renderer when after all graphics
     * have been painted.
     * @return true if some datas has been rendered
     */
    boolean portrayLabels() throws TransformException;

    /**
     * Portray the labelLayer immidiately.
     * This will disable any label placement algorithm that light have been apply in the
     * normal portrayLabels call
     */
    void portrayImmidiately(LabelLayer layer);

}
