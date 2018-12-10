/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.util.Map;
import java.util.LinkedHashMap;
import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.ColorModel;
import org.apache.sis.measure.NumberRange;

import org.geotoolkit.lang.Static;


/**
 * A factory for {@link ColorModel} objects built from a list of {@link Category} objects.
 * This factory provides only one public static method: {@link #getColorModel}.  Instances
 * of {@link ColorModel} are shared among all callers in the running virtual machine.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.1
 * @module
 */
final class ColorModelFactory extends Static {
    private ColorModelFactory() {
    }

    /**
     * Returns a color model for a category set. This method builds up the color model
     * from each category's colors (as returned by {@link Category#getColors}).
     *
     * @param  categories The set of categories.
     * @param  type The color model type. One of {@link DataBuffer#TYPE_BYTE},
     *         {@link DataBuffer#TYPE_USHORT}, {@link DataBuffer#TYPE_FLOAT} or
     *         {@link DataBuffer#TYPE_DOUBLE}.
     * @param  visibleBand The band to be made visible (usually 0). All other bands, if any
     *         will be ignored.
     * @param  numBands The number of bands for the color model (usually 1). The returned color
     *         model will renderer only the {@code visibleBand} and ignore the others, but
     *         the existence of all {@code numBands} will be at least tolerated. Supplemental
     *         bands, even invisible, are useful for processing with Java Advanced Imaging.
     * @return The requested color model, suitable for {@link java.awt.image.RenderedImage}
     *         objects with values in the <code>{@linkplain CategoryList#getRange}</code> range.
     */
    public static ColorModel getColorModel(final Category[] categories, final int type,
                                           final int visibleBand, final int numBands)
    {
        final Map<NumberRange<?>, Color[]> ranges = new LinkedHashMap<>();
        for (final Category category : categories) {
            ranges.put(category.getRange(), category.getColors());
        }
        return org.apache.sis.internal.raster.ColorModelFactory.createColorModel(ranges, visibleBand, numBands, type);
    }
}
