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


/**
 * To be implemented by objects that can determine the settings of a {@link NewGridCoverageReference}.
 * Thes setting can be determined either by putting up a GUI to obtain values from a user, or by
 * other means.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
public interface CoverageDatabaseController {
    /**
     * Invoked before a {@linkplain GridCoverageReference Grid Coverage Reference} is added.
     * Implementations can modify in-place the field values of the {@code reference} argument
     * as below:
     *
     * {@preformat java
     *     public void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) {
     *         if (event.isBefore()) {
     *             reference.format = "MyPredefinedFormat";
     *             // etc.
     *         }
     *     }
     * }
     *
     * @param  event Reference to the {@linkplain CoverageDatabaseEvent#getSource() source} database.
     * @param  reference Information about the coverage reference to be added.
     * @throws DatabaseVetoException if the recipient vetos against the operation.
     */
    void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) throws DatabaseVetoException;
    // The method name is consistent with java.awt.event.WindowListener.windowClosing(...)
}
