/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.util.EventListener;


/**
 * Receives general indications of {@linkplain Process process} progress and events indicating
 * start, completion or failure.
 *
 * @author Johann Sorel (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public interface ProcessListener extends EventListener {
    /**
     * Reports that a process is beginning. {@link Process} implementations
     * are required to call this method exactly once.
     *
     * @param event The progress event.
     */
    void started(ProcessEvent event);

    /**
     * Invoked at any time during process progression. {@link Process} implementations
     * may invoke this method an arbitrary number of time.
     *
     * @param event The progress event.
     */
    void progressing(ProcessEvent event);

    /**
     * Reports that a process has completed successfully. {@link Process} implementations
     * are required to call either this method or {@link #failed(ProcessEvent)} exactly once.
     *
     * @param event The progress event.
     */
    void ended(ProcessEvent event);

    /**
     * Reports that a process has failed. {@link Process} implementations are required to call
     * either this method or {@link #ended(ProcessEvent)} exactly once.
     *
     * @param event The progress event.
     */
    void failed(ProcessEvent event);
}
