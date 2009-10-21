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
package org.geotoolkit.display.canvas.control;

/**
 * Abstract monitor.
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCanvasMonitor implements CanvasMonitor{
    
    /**
     * Thread safe value, volatile
     */
    protected volatile boolean isRendering = false;
    
    /**
     * Thread safe value, volatile
     */
    protected volatile boolean stopRequest = false;
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void renderingStarted() {
        isRendering = true;
        stopRequest = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void renderingFinished() {
        isRendering = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isRendering() {
        return isRendering;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void stopRendering() {
        stopRequest = true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean stopRequested() {
        return stopRequest;
    }

}
