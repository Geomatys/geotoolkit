/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.process;

/**
 * Monitor informed of the process progression.
 *
 * @author johann Sorel (Geomatys)
 * @module pending
 */
public interface ProcessMonitor {

    /**
     * Called when thr process starts.
     */
    void started(ProcessEvent event);

    /**
     * Called when process progress.
     */
    void progressing(ProcessEvent event);

    /**
     * Called when thr process ends.
     */
    void ended(ProcessEvent event);

    /**
     * Called when thr process fails.
     * the cause can be found on the processevent
     */
    void failed(ProcessEvent event);

}
