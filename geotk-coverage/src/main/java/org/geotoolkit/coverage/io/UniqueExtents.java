/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage.io;

import java.util.List;
import java.util.Collection;

import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.apache.sis.metadata.iso.extent.DefaultExtent;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * An ISO 19115 (@code Extent} object where each elements is a singleton.
 *
 * This class has been set public because otherwise it cause an exception during a copy of the metadata.
 * The Metadata copier need to access the empty constructor via reflection.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public final class UniqueExtents extends DefaultExtent {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7784229364828123287L;

    /**
     * Creates a uninitialized instance.
     */
    public UniqueExtents() {
    }

    /**
     * Creates an instance initialized to the values of the given existing object.
     */
    public UniqueExtents(final Extent copy) {
        super(copy);
    }

    /**
     * If the given list does not contains any extent with a vertical, temporal or bounding box,
     * returns a copy of that instance and modify the list to contain that copy. Otherwise,
     * returns {@code null}.
     * <p>
     * This method is invoked by {@link GridCoverageReader#getMetadata()} in order to determine
     * if there is missing information in the plugin-provided {@code DefaultMetadata} instance
     * that we could complete with the information inferred from the grid geometry.
     *
     * @param  extents The list where to search for an extent to complete.
     *         This list will be modified in-place if such instance is found.
     * @return The extent to complete, or {@code null} if none.
     */
    static DefaultExtent getIncomplete(final List<Extent> extents) {
search: for (int i=extents.size(); --i>=0;) {
            final Extent extent = extents.get(i);
            if (isNullOrEmpty(extent.getVerticalElements()) &&
                isNullOrEmpty(extent.getTemporalElements()))
            {
                final Collection<? extends GeographicExtent> geo = extent.getGeographicElements();
                if (!isNullOrEmpty(geo)) {
                    for (final GeographicExtent e : geo) {
                        if (e instanceof GeographicBoundingBox) {
                            continue search;
                        }
                    }
                }
                // Found an existing instance that we can complete.
                final DefaultExtent replacement = new UniqueExtents(extent);
                extents.set(i, replacement);
                return replacement;
            }
        }
        return null;
    }
}
