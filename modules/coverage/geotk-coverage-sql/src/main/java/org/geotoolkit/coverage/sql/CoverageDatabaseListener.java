/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.EventListener;


/**
 * A listener notified when a {@linkplain Layer Layer} or a {@linkplain GridCoverageReference
 * Grid Coverage Reference} is about to be added or removed. The kind of event (whatever the
 * listener is invoked <cite>before</cite> or <cite>after</cite> the change, and whatever the
 * change is a <cite>add</cite> or <cite>remove</cite> operation) is described by the
 * {@link CoverageDatabaseEvent} argument.
 * <p>
 * Listeners can veto the change when they are invoked {@linkplain CoverageDatabaseEvent#isBefore()
 * before} the change.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
public interface CoverageDatabaseListener extends EventListener {
    /**
     * Invoked before or after a {@linkplain Layer Layer} is added or removed.
     * Implementations can veto the change if this method is invoked
     * {@linkplain CoverageDatabaseEvent#isBefore() before} the change.
     *
     * @param  event The kind of event.
     * @param  name The name of the layer.
     * @return {@code true} if the layer can be added, or {@code false} if the operation should
     *         be vetoed. The returned value is ignored if this method is invoked after the change.
     */
    boolean layerChange(CoverageDatabaseEvent event, String name);
    // The method name is consistent with java.beans.PropertyChangeListener.propertyChange(...)

    /**
     * Invoked before or after a {@linkplain GridCoverageReference Grid Coverage Reference}
     * is added. Implementations can veto the change if this method is invoked
     * {@linkplain CoverageDatabaseEvent#isBefore() before} the change.
     * <p>
     * Implementations can modify in-place the field values of the {@code reference} argument.
     * The changes will be honored if they are applied {@linkplain CoverageDatabaseEvent#isBefore()
     * before} the new entry is added to the database, and ignored if the change are applied after.
     *
     * @param  event The kind of event.
     * @param  reference Information about the coverage reference.
     * @return {@code true} if the reference can be added, or {@code false} if the operation should
     *         be vetoed. The returned value is ignored if this method is invoked after the change.
     */
    boolean coverageChange(CoverageDatabaseEvent event, NewGridCoverageReference reference);
    // The method name is consistent with java.beans.PropertyChangeListener.propertyChange(...)
}
