/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.internal.referencing;

import org.opengis.metadata.extent.GeographicBoundingBox;


/**
 * The context in which a coordinate operation is applied. Current version contains only the area of interest.
 * Future version may be extended with time.
 *
 * @todo Members are static for now, but may become non-static in a future version.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.0-M2
 *
 * @since 4.0-M2
 * @module
 */
public final class OperationContext {
    /**
     * The area of interest.
     */
    private static final ThreadLocal<GeographicBoundingBox> AREA = new ThreadLocal<>();

    /**
     * Do not allow (for now) instantiation of this class.
     */
    private OperationContext() {
    }

    /**
     * Returns the area of interest, or {@code null} if none.
     *
     * @return The area of interest, or {@code null} if none.
     */
    public static GeographicBoundingBox getAreaOfInterest() {
        return AREA.get();
    }

    /**
     * Sets the area of interest.
     *
     * @param domain The area of interest.
     */
    public static void setAreaOfInterest(final GeographicBoundingBox domain) {
        AREA.set(domain);
    }

    /**
     * Resets the operation context to its initial state.
     */
    public static void clear() {
        AREA.remove();
    }
}
