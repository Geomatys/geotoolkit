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
package org.geotoolkit.internal.image.io;

import javax.imageio.metadata.IIOMetadata;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.geotoolkit.image.io.metadata.MetadataNodeAccessor;


/**
 * Convenience methods for nodes related to the {@code "DiscoveryMetadata"} node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 * @module
 */
public class DiscoveryAccessor {
    /**
     * The {@code value} path.
     */
    public static final String ROOT = "DiscoveryMetadata";

    /**
     * The {@code value} path.
     */
    public static final String GEOGRAPHIC_ELEMENT = ROOT + "/Extent/GeographicElement";

    /**
     * The metadata where to write.
     */
    private final IIOMetadata metadata;

    /**
     * Creates a new accessor for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the
     *        {@link org.geotoolkit.image.io.metadata.SpatialMetadata}
     *        sub-class is recommended, but not mandatory.
     */
    public DiscoveryAccessor(final IIOMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Sets the attribute values of the {@value #GEOGRAPHIC_ELEMENT} path.
     * This method does nothing if the given box is null.
     *
     * @param box The geographic bounding box, or {@code null}.
     */
    public void setGeographicElement(final GeographicBoundingBox box) {
        if (box != null) {
            final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, GEOGRAPHIC_ELEMENT);
            accessor.setAttribute("westBoundLongitude", nice(box.getWestBoundLongitude()));
            accessor.setAttribute("eastBoundLongitude", nice(box.getEastBoundLongitude()));
            accessor.setAttribute("southBoundLatitude", nice(box.getSouthBoundLatitude()));
            accessor.setAttribute("northBoundLatitude", nice(box.getNorthBoundLatitude()));
            accessor.setAttribute("inclusion",          box.getInclusion());
        }
    }

    /**
     * Optionally replaces the given number by something nicer to display.
     * The default implementation returns the number unchanged. Subclasses
     * can override this method if they want to display a slightly different value.
     *
     * @param  value The attribute value to be written.
     * @return The value to write.
     */
    protected double nice(final double value) {
        return value;
    }
}
