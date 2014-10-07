/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.geotoolkit.display.canvas.control;

import org.apache.sis.util.ArgumentChecks;

/**
 * A monitor which will query for cancellation if the input thread has been interrupted.
 *
 * @author Alexis Manin (Geomatys)
 */
public class StopOnInterruptionMonitor extends NeverFailMonitor {

    private final Thread currentThread;

    public StopOnInterruptionMonitor(final Thread toWatch) {
        ArgumentChecks.ensureNonNull("Input thread.", toWatch);
        currentThread = toWatch;
    }

    @Override
    public boolean stopRequested() {
        return (super.stopRequested() || currentThread.isInterrupted());
    }
}
