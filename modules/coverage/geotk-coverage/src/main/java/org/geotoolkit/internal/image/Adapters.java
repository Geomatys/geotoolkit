/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.image;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Range;


/**
 * Performs conversions between Geotk and JAI classes
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
public final class Adapters extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Adapters() {
    }

    /**
     * Converts a Geotk range to a JAI range.
     *
     * @param  range The Geotk range.
     * @return The JAI range.
     */
    public static javax.media.jai.util.Range convert(final Range<?> range) {
        return new javax.media.jai.util.Range(range.getElementType(),
                range.getMinValue(), range.isMinIncluded(),
                range.getMaxValue(), range.isMaxIncluded());
    }

    /**
     * Converts a JAI range to a Geotk range.
     *
     * @param  range The JAI range.
     * @return The Geotk range.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static Range<?> convert(final javax.media.jai.util.Range range) {
        return new Range(range.getElementClass(),
                range.getMinValue(), range.isMinIncluded(),
                range.getMaxValue(), range.isMaxIncluded());
    }
}
