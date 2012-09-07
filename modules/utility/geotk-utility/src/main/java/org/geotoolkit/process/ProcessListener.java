/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
 * start, completion or failure. For any {@linkplain Process#addListener registered listeners},
 * the {@link Process#call()} method shall invoke at least the {@link #started started} and
 * {@link #completed completed} or {@link #failed failed} methods (mandatory). Calls to the
 * {@link #progressing progressing} method are optional but recommended.
 *
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
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
     * may invoke this method an arbitrary number of time. In addition to the source,
     * task name and progress indicator, the event argument may contain the following
     * optional information:
     * <p>
     * <ul>
     *   <li>{@link ProcessEvent#getOutput()}, if non-null, indicates an intermediate calculation value.</li>
     *   <li>{@link ProcessEvent#getException()}, if non-null, indicates a non-fatal warning.</li>
     * </ul>
     *
     * @param event The progress event.
     */
    void progressing(ProcessEvent event);

    /**
     * Reports that a process has been paused. In addition to the
     * source and task name, the event argument may contain the following optional information:
     * <p>
     * <ul>
     *   <li>{@link ProcessEvent#getOutput()}, if non-null, indicates the calculation state before
     *       the pause.</li>
     * </ul>
     *
     * @param event The progress event.
     */
    void paused(ProcessEvent event);

    /**
     * Reports that a process has been resumed after a pause.
     *
     * @param event The progress event.
     */
    void resumed(ProcessEvent event);

    /**
     * Reports that a process has completed successfully. {@link Process} implementations
     * are required to call either this method or {@link #failed(ProcessEvent)} exactly once.
     * In addition to the source and task name, the event argument shall contain the following
     * information:
     * <p>
     * <ul>
     *   <li>{@link ProcessEvent#getOutput()} is the final result returned by {@link Process#call()}.</li>
     * </ul>
     *
     * @param event The progress event.
     */
    void completed(ProcessEvent event);

    /**
     * Reports that a process has failed. {@link Process} implementations are required to call
     * either this method or {@link #completed(ProcessEvent)} exactly once. In addition to the
     * source and task name, the event argument may contain the following optional information:
     * <p>
     * <ul>
     *   <li>{@link ProcessEvent#getException()}, if non-null, indicates the failure cause.</li>
     *   <li>{@link ProcessEvent#getOutput()}, if non-null, indicates the calculation state before
     *       the failure.</li>
     * </ul>
     *
     * @param event The progress event.
     */
    void failed(ProcessEvent event);
}
