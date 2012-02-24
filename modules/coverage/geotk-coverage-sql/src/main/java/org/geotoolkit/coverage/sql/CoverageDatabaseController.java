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

import java.util.List;
import java.util.Collection;


/**
 * To be implemented by objects that can determine the settings of a {@link NewGridCoverageReference}.
 * The setting can be determined either by putting up a GUI to obtain values from a user, or by
 * other means.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see Layer#addCoverageReferences(Collection, CoverageDatabaseController)
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
    // Method signature must be keept compatible with CoverageDatabaseListener.coverageAdding.

    /**
     * Invoked before {@link #coverageAdding coverageAdding} when the coverage contains more than
     * one image. This method gives an opportunities to select which image to process. When there
     * is no controller, the default {@link Layer#addCoverageReferences Layer.addCoverageReferences(...)}
     * behavior is to select the first image.
     *
     * @param  images The names of images found in the files. The index of each element
     *         in this list is the index of the corresponding image.
     * @param  multiSelectionAllowed {@code true} if multi-selection is allowed,
     *         or {@code false} if the user shall select exactly one element.
     * @return The name of images to insert. For example if only the image at index 0 is wanted,
     *         then this method shall return a singleton containing {@code images.get(0)}.
     * @throws DatabaseVetoException if the recipient vetos against the operation.
     *
     * @since 3.15
     */
    Collection<String> filterImages(List<String> images, boolean multiSelectionAllowed) throws DatabaseVetoException;
}
