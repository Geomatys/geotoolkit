/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods for threads.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Static
public final class Threads {
    /**
     * Do not allow instantiation of this class.
     */
    private Threads() {
    }

    /**
     * The parent of all thread groups defined in this class.
     * This is the root of our tree of thread groups.
     */
    static final ThreadGroup PARENT = new ThreadGroup("Geotoolkit.org");

    /**
     * The group of shutdown hooks.
     */
    public static final ThreadGroup SHUTDOWN_HOOKS = new ThreadGroup(PARENT, "ShutdownHooks");

    /*
     * Other ThreadGroups are defined in:
     *
     *   - SwingUtilities
     *   - FactoryUtilities
     *   - ReferenceQueueConsumer
     *
     * They are left in their respective class in order to instantiate the group only on
     * class initialization. The shutdown group is defined here because needed soon anyway.
     */
}
