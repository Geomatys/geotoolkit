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

import java.util.Map;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * The result of a query on a {@link CategoryTable} object. This is also opportunistically
 * reused as the result of a query on a {@link SampleDimensionTable} object.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
final class CategoryEntry {
    /**
     * The name of the color palette, or {@code null} if none. If more than one color
     * palettes are found, then the one for the largest range of values is used.
     * <p>
     * This is used for initializing the {@link FormatEntry#paletteName} attribute,
     * which is used by {@link GridCoverageLoader}. We retain only one palette name
     * because there is typically only one visible band in an index color model, so
     * {@code GridCoverageLoader} wants only one palette.
     */
    final String paletteName;

    /**
     * The categories for each sample dimensions in a given format.
     * The keys are band numbers, where the first band is numbered 1.
     * Values are the categories for that band.
     */
    final Map<Integer,Category[]> categories;

    /**
     * The sample dimensions built from the {@link #categories} map. This field is initially
     * {@code null} and is initialized by {@link SampleDimensionTable#getSampleDimensions(String)}.
     */
    GridSampleDimension[] sampleDimensions;

    /**
     * Creates a new entry.
     */
    CategoryEntry(final Map<Integer,Category[]> categories, final String paletteName) {
        this.categories  = categories;
        this.paletteName = paletteName;
    }
}
