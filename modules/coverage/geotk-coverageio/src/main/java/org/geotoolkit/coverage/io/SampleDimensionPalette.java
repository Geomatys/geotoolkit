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
package org.geotoolkit.coverage.io;

import java.io.IOException;
import java.awt.image.ColorModel;
import javax.imageio.ImageTypeSpecifier;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.image.io.Palette;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * A palette which build its {@link IndexColorModel} from a {@link GridSampleDimension}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@Immutable
final class SampleDimensionPalette extends Palette {
    /**
     * The singleton factory.
     */
    static final PaletteFactory FACTORY = new Factory();

    /**
     * The sample dimension to use for creating the color model.
     */
    private final GridSampleDimension band;

    /**
     * Creates a new palette for the given band.
     *
     * @param factory The originating factory.
     * @param name    The palette name (actually ignored).
     */
    private SampleDimensionPalette(final PaletteFactory factory, final String name,
            final GridSampleDimension band, final int numBands, final int visibleBand)
    {
        super(factory, name, numBands, visibleBand);
        this.band = band;
    }

    /**
     * Creates a new image type specifier.
     */
    @Override
    protected ImageTypeSpecifier createImageTypeSpecifier() throws IOException {
        final ColorModel cm = band.getColorModel(visibleBand, numBands);
        return new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
    }

    /**
     * Returns a hash value for this palette.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + 31*band.hashCode();
    }

    /**
     * Compares this palette with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final SampleDimensionPalette that = (SampleDimensionPalette) object;
            return Utilities.equals(band, that.band);
        }
        return false;
    }

    /**
     * Returns a string representation of this palette. Used for debugging purpose only.
     */
    @Override
    public String toString() {
        return "Palette[" + band.getDescription() + ']';
    }

    /**
     * Workaround for passing the bands argument to {@link Factory#getPalette}.
     */
    static final ThreadLocal<GridSampleDimension[]> USE_BANDS = new ThreadLocal<GridSampleDimension[]>();

    /**
     * The factory for creating {@link SampleDimensionPalette} instances.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.11
     *
     * @since 3.11
     * @module
     */
    private static final class Factory extends PaletteFactory {
        /**
         * Default constructor for the singleton only.
         */
        Factory() {
        }

        /**
         * This is the method invoked by {@link org.geotoolkit.image.io.SpatialImageReader}.
         */
        @Override
        public Palette getPalette(final String name, final int lower, final int upper, final int size,
                                  final int numBands, final int visibleBand)
        {
            final GridSampleDimension[] bands = USE_BANDS.get();
            final GridSampleDimension band = bands[Math.min(visibleBand, bands.length-1)];
            Palette palette = new SampleDimensionPalette(this, name, band, numBands, visibleBand);
            palette = palettes.unique(palette);
            return palette;
        }
    }
}
