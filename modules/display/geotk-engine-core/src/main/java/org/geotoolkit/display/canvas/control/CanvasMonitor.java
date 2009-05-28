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

import java.util.logging.Level;

/**
 * Monitor used for the canvas, this object have the control of the rendering process
 * and may stop it at any time.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CanvasMonitor {

    /**
     * Inovked by the canvas when the rendering process started.
     */
    void renderingStarted();

    /**
     * Invoked by the canvas when the rendering process finished.
     */
    void renderingFinished();

    /**
     * Returns {@code true} if the canvas in currently rendering.
     * This method should be usable in multithread environment.
     * @return true if the rendering is in progress.
     */
    boolean isRendering();

    /**
     * Stops the rendering process if it is running.
     * The rendering may not stop at this exact moment.
     */
    void stopRendering();

    /**
     * Returns {@code true} if a stop request has been send.
     */
    boolean stopRequested();

    /**
     * Used by the canv or the graphic objects when an exception occured.
     * The monitor may stop the rendering process depending on his behavior and the error type.
     */
    void exceptionOccured(Exception ex, Level level);

}
