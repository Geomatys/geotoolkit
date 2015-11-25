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
package org.geotoolkit.coverage.io;

import java.util.Objects;
import java.io.IOException;
import java.awt.image.ColorModel;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;

import org.geotoolkit.coverage.GridSampleDimension;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import org.geotoolkit.image.palette.Palette;
import org.geotoolkit.image.palette.PaletteFactory;


/**
 * A palette which build its {@link IndexColorModel} from a {@link GridSampleDimension}.
 * Instances of this class are created only by the {@link #FACTORY} singleton. The factory
 * needs a {@link GridSampleDimension} argument, which is passed in the {@link #BANDS}
 * static variable. This is ugly, but there is no API in the current {@link PaletteFactory}
 * class for building a palette from a sample dimension.
 *
 * {@note The raison why <code>PaletteFactory</code> has no API for sample dimensions is that
 *        <code>PaletteFactory</code> is all about creating palettes from files of RGB codes,
 *        and intentionally avoid the coverage API since its package is about image I/O. On
 *        the contrary, this <code>SampleDimensionPalette</code> does not read any file, so
 *        it is a bit a departure compared to the usual factory. In addition, creating the
 *        required <code>GridSampleDimension</code> objects require a bit of non-trivial code
 *        (provided in <code>ImageCoverageReader</code>) which is out of scope of Image I/O.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.11
 * @module
 */
final class SampleDimensionPalette extends Palette {
    /**
     * The singleton factory. We override only the method which is known to be invoked by
     * {@link org.geotoolkit.image.io.SpatialImageReader#getImageType}. However before to
     * invoke any factory method, the {@link #BANDS} field must be set.
     */
    static final PaletteFactory FACTORY = new PaletteFactory(null, PaletteFactory.class, new File("colors"), ".pal",
            Charset.forName("ISO-8859-1"), Locale.US)
    {
        @Override
        public Palette getPalette(final String name, final int lower, final int upper,
                final int size, final int numBands, final int visibleBand)
        {
            final GridSampleDimension[] bands = BANDS.get();
            final GridSampleDimension band = bands[Math.min(visibleBand, bands.length-1)];
            if (isNullOrEmpty(band.getCategories())) {
                return super.getPalette(name, lower, upper, size, numBands, visibleBand);
            }
            Palette palette = new SampleDimensionPalette(this, name, band, numBands, visibleBand);
            palette = palettes.unique(palette);
            return palette;
        }
    };

    /**
     * Workaround for passing the bands argument to {@code FACTORY.getPalette(...)}.
     */
    static final ThreadLocal<GridSampleDimension[]> BANDS = new ThreadLocal<>();

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
     * Creates a new image type specifier with a color model inferred from the sample
     * dimension given to the constructor.
     */
    @Override
    protected ImageTypeSpecifier createImageTypeSpecifier() throws IOException {
        final ColorModel cm = band.getColorModel(visibleBand, numBands);
        // 'cm' should not be null because the above factory checked
        // that the GridSampleDimension has at least one category.
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
            return Objects.equals(band, that.band);
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
}
