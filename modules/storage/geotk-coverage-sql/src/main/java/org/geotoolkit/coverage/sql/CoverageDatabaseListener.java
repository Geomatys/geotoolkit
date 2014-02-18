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
package org.geotoolkit.coverage.sql;

import java.util.EventListener;


/**
 * A listener notified when a {@linkplain Layer Layer} or a {@linkplain GridCoverageReference
 * Grid Coverage Reference} is about to be added or removed. Whatever the listener is invoked
 * <em>before</em> or <em>after</em> the change, and whatever the change is a <em>add</em> or
 * <em>remove</em> operation, is described by the {@link CoverageDatabaseEvent} argument.
 * <p>
 * It is possible for this listener to be also a {@linkplain CoverageDatabaseController controller}.
 * In such cases, when a coverage is about to be {@linkplain Layer#addCoverageReferences added in a
 * layer}, implementations can modify the values to be added.
 * <p>
 * Listeners can veto the change when they are invoked {@linkplain CoverageDatabaseEvent#isBefore()
 * before} the change.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see CoverageDatabase#addListener(CoverageDatabaseListener)
 * @see CoverageDatabase#removeListener(CoverageDatabaseListener)
 *
 * @since 3.12
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
     * @throws DatabaseVetoException if the recipient vetos against the change. This exception
     *         will be logged at the {@link java.util.logging.Level#WARNING WARNING} level and
     *         otherwise ignored if it is thrown after the change.
     */
    void layerListChange(CoverageDatabaseEvent event, String name) throws DatabaseVetoException;
    // The method name is consistent with java.beans.PropertyChangeListener.propertyChange(...)

    /**
     * Invoked before or after a {@linkplain GridCoverageReference Grid Coverage Reference}
     * is added. Implementations can veto the change if this method is invoked
     * {@linkplain CoverageDatabaseEvent#isBefore() before} the change.
     * <p>
     * Implementations can modify in-place the field values of the {@code reference} argument.
     * The changes will be honored if they are applied {@linkplain CoverageDatabaseEvent#isBefore()
     * before} the new entry is added to the database, and ignored if the changes are applied after.
     *
     * @param  event The kind of event.
     * @param  reference Information about the coverage reference to be added.
     * @throws DatabaseVetoException if the recipient vetos against the change. This exception
     *         will be logged at the {@link java.util.logging.Level#WARNING WARNING} level and
     *         otherwise ignored if it is thrown after the change.
     */
    void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) throws DatabaseVetoException;
    // Method signature must be keept compatible with CoverageDatabaseController.coverageAdding.
}
