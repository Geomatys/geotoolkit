/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.image.io;

import java.util.Locale;
import java.awt.Rectangle;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.util.converter.Classes;


/**
 * Default parameters for {@link SpatialImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.05 (derived from 2.4)
 * @module
 */
public class SpatialImageReadParam extends ImageReadParam {
    /**
     * The name of the default color palette to apply when none was explicitly specified.
     *
     * @see #getPaletteName()
     * @see #setPaletteName(String)
     */
    public static final String DEFAULT_PALETTE_NAME = "grayscale";

    /**
     * The name of the color palette.
     */
    private String palette;

    /**
     * The band to display.
     */
    private int visibleBand;

    /**
     * The locale for formatting error messages. Will be inferred from
     * the {@linkplain ImageReader image reader} at construction time.
     */
    private final Locale locale;

    /**
     * Creates a new, initially empty, set of parameters.
     *
     * @param reader The reader for which this parameter block is created
     */
    public SpatialImageReadParam(final ImageReader reader) {
        locale = (reader != null) ? reader.getLocale() : null;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    private IndexedResourceBundle getErrorResources() {
        return Errors.getResources(locale);
    }

    /**
     * Ensures that the specified band number is valid.
     */
    private void ensureValidBand(final int band) throws IllegalArgumentException {
        if (band < 0) {
            throw new IllegalArgumentException(getErrorResources().getString(
                    Errors.Keys.BAD_BAND_NUMBER_$1, band));
        }
    }

    /**
     * Returns the band to display in the target image. In theory, images backed by
     * {@linkplain java.awt.image.IndexColorModel index color model} should have only
     * ony band. But sometime we want to load additional bands as numerical data, in
     * order to perform computations. In such case, we need to specify which band in
     * the destination image will be used as an index for displaying the colors. The
     * default value is 0.
     *
     * @return The band to display in the target image.
     */
    public int getVisibleBand() {
        return visibleBand;
    }

    /**
     * Sets the band to make visible in the destination image.
     *
     * @param  visibleBand The band to make visible.
     * @throws IllegalArgumentException if the specified band index is invalid.
     */
    public void setVisibleBand(final int visibleBand) throws IllegalArgumentException {
        ensureValidBand(visibleBand);
        this.visibleBand = visibleBand;
    }

    /**
     * Returns a name of the color palette, or a {@linkplain #DEFAULT_PALETTE_NAME default name}
     * if none were explicitly specified.
     */
    final String getNonNullPaletteName() {
        final String palette = getPaletteName();
        return (palette != null) ? palette : DEFAULT_PALETTE_NAME;
    }

    /**
     * Returns the name of the color palette to apply when creating an
     * {@linkplain java.awt.image.IndexColorModel index color model}.
     * This is the name specified by the last call to {@link #setPaletteName(String)}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @return The name of the color palette to apply.
     */
    public String getPaletteName() {
        return palette;
    }

    /**
     * Sets the color palette as one of the {@linkplain PaletteFactory#getAvailableNames available
     * names} provided by the {@linkplain PaletteFactory#getDefault default palette factory}. This
     * name will be given by the {@link SpatialImageReader} default implementation to the
     * {@linkplain PaletteFactory#getDefault default palette factory} for creating a
     * {@linkplain javax.imageio.ImageTypeSpecifier image type specifier}.
     * <p>
     * For a table of available palette names in the default Geotk installation,
     * see the {@link PaletteFactory} class javadoc.
     *
     * @param palette The name of the color palette to apply.
     *
     * @see PaletteFactory#getAvailableNames
     */
    public void setPaletteName(final String palette) {
        this.palette = palette;
    }

    /**
     * Returns a string representation of this block of parameters.
     */
    @Override
    public String toString() {
        final int[]     sourceBands  = this.sourceBands;
        final Rectangle sourceRegion = this.sourceRegion;
        final int sourceXSubsampling = this.sourceXSubsampling;
        final int sourceYSubsampling = this.sourceYSubsampling;
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        buffer.append('[');
        if (sourceRegion != null) {
            buffer.append("sourceRegion=(").
                   append(sourceRegion.x).append(':').append(sourceRegion.x + sourceRegion.width).append(',').
                   append(sourceRegion.y).append(':').append(sourceRegion.y + sourceRegion.height).append("), ");
        }
        if (sourceXSubsampling != 1 || sourceYSubsampling != 1) {
            buffer.append("subsampling=(").append(sourceXSubsampling).append(',').
                    append(sourceYSubsampling).append("), ");
        }
        if (sourceBands != null) {
            buffer.append("sourceBands={");
            for (int i=0; i<sourceBands.length; i++) {
                if (i != 0) {
                    buffer.append(',');
                }
                buffer.append(sourceBands[i]);
            }
            buffer.append("}, ");
        }
        return buffer.append("palette=\"").append(palette).append("\", ").append(']').toString();
    }
}
