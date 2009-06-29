/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.opengis.display.container.ContainerEvent;
import org.opengis.display.container.GraphicsContainer;
import org.opengis.display.primitive.Graphic;

/**
 * Default implementation of ContainerEvent.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultContainerEvent extends ContainerEvent{
    private static final long serialVersionUID = -5439770733913262681L;

    /**
     * graphics concerned by the container event.
     */
    private final Collection<Graphic> graphics;
    
    /**
     * Create a Container Event with a collection of graphic objects.
     * 
     * @param container : the container who generate this event
     * @param graphics : graphics concerned by this event
     */
    DefaultContainerEvent(final GraphicsContainer container, final Collection<Graphic> graphics){
        super(container);
        this.graphics = new ArrayList<Graphic>(graphics);
    }
    
    /**
     * Create a container event with a single graphic object.
     * 
     * @param container : the container who generate this event
     * @param graphic : graphics concerned by this event
     */
    DefaultContainerEvent(final GraphicsContainer container, final Graphic graphic){
        super(container);
        this.graphics = Collections.singleton(graphic);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Graphic> getGraphics() {
        return graphics;
    }

}
