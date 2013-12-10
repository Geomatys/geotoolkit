/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.display.container;

import org.geotoolkit.map.MapContext;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface MapContextContainer {

    /**
     * Set the mapcontext to render.
     * this will remove all previous graphics builded with the context.
     * <b>Caution</b> this should not remove graphics unrelated to the context.
     *
     * @param context : MapContext to render
     */
    public void setContext(MapContext context);

    /**
     * Returns the currently renderered map context
     *
     * @return MapContext or null
     */
    public MapContext getContext();

}
