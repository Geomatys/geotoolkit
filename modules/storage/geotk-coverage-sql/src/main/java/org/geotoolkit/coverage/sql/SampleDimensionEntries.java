/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
import java.util.List;
import java.util.Arrays;
import org.opengis.util.NameFactory;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.internal.util.UnmodifiableArrayList;


/**
 * The result of a query on a {@link SampleDimensionTable} object.
 * This object actually contains the information of many rows.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class SampleDimensionEntries extends Entry {
    /**
     * The name of the color palette, or {@code null} if none. If more than one color
     * palettes are found, then the one for the largest range of values is used.
     *
     * <p>This is used for initializing the {@link FormatEntry#paletteName} attribute,
     * which is used by {@link GridCoverageLoader}. We retain only one palette name
     * because there is typically only one visible band in an index color model, so
     * {@code GridCoverageLoader} wants only one palette.</p>
     */
    final String paletteName;

    /**
     * The categories for each sample dimensions in a given format.
     * The keys are band numbers, where the first band is numbered 1.
     * Values are the categories for that band in arbitrary order.
     */
    private final Map<Integer,Category[]> categories;

    /**
     * The sample dimensions built from the {@link #categories} map. This field is initially
     * {@code null} and is initialized by {@link SampleDimensionTable#getSampleDimensions(String)}.
     */
    private SampleDimension[] sampleDimensions;

    /**
     * Reference to an entry in the {@code metadata.SampleDimension} table, or {@code null} if none.
     * A non-null array may contain {@code null} elements.
     *
     * @todo stored but not yet used.
     */
    private String[] metadata;

    /**
     * Creates a partially initialized instance.
     */
    SampleDimensionEntries(final Map<Integer,Category[]> categories, final String paletteName) {
        this.categories  = categories;
        this.paletteName = paletteName;
    }

    /**
     * Completes the construction of this instance. For each array given in argument to this method,
     * only the first {@code numSampleDimensions} elements are taken in account.
     *
     * @param  factory  the factor to use for creating sample dimension names.
     * @param  names    the names of all sample dimensions.
     * @param  packs    {@code true} if sample values are packed, or {@code false} if they are real values.
     * @param  mdDim    Reference to dimension metadata, or {@code null} if none.
     * @param  count    number of valid sample dimensions.
     * @throws IllegalArgumentException if a sample dimension can not be created.
     */
    void complete(final NameFactory factory, final String[] names, boolean[] packs, final String[] mdDim, final int count) {
        sampleDimensions = new SampleDimension[count];
        for (int i=0; i<count; i++) {
            sampleDimensions[i] = new SampleDimension(factory.createLocalName(null, names[i]),
                    null, Arrays.asList(categories.remove(i+1))).forConvertedValues(!packs[i]);
        }
        metadata = ArraysExt.resize(mdDim, count);
    }

    /**
     * Returns all sample dimensions.
     */
    final List<SampleDimension> getSampleDimensions() {
        return UnmodifiableArrayList.wrap(sampleDimensions);
    }
}
