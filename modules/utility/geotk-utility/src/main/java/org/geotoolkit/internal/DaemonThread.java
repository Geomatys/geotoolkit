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
package org.geotoolkit.internal;


/**
 * Base class for daemon thread in the Geotk library. This class provides a
 * {@link #killRequested} flag which should be tested by the daemon thread
 * on a regular basis.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
public class DaemonThread extends Thread {
    /**
     * Set to {@code true} when the {@link #kill()} method has been invoked.
     * Subclasses should test this flag on a regular basis.
     */
    private volatile boolean killRequested;

    /**
     * Creates a new daemon thread. This constructor sets the daemon flag to {@code true}.
     *
     * @param group The thread group.
     * @param name  The thread name.
     */
    protected DaemonThread(final ThreadGroup group, final String name) {
        super(group, name);
        super.setDaemon(true);
    }

    /**
     * Creates a new daemon thread for the given task.
     * This constructor sets the daemon flag to {@code true}.
     *
     * @param group The thread group.
     * @param task  The task to execute.
     * @param name  The thread name.
     */
    public DaemonThread(final ThreadGroup group, final Runnable task, final String name) {
        super(group, task, name);
        super.setDaemon(true);
    }

    /**
     * Kills this daemon.
     */
    public final void kill() {
        killRequested = true;
        interrupt();
    }

    /**
     * Returns {@code true} if {@link #kill()} has been invoked.
     * Subclasses should test this flag on a regular basis.
     *
     * @return {@code true} if {@link #kill()} has been invoked.
     */
    public final boolean isKillRequested() {
        return killRequested;
    }
}
