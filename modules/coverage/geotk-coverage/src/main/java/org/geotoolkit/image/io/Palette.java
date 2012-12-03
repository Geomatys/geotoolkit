/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.awt.image.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.imageio.ImageTypeSpecifier;
import net.jcip.annotations.Immutable;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.NullArgumentException;


/**
 * A set of RGB colors created by a {@linkplain PaletteFactory palette factory} from
 * a {@linkplain #name}. A palette can creates a {@linkplain ColorModel color model}
 * (often {@linkplain IndexColorModel indexed}) or an {@linkplain ImageTypeSpecifier
 * image type specifier} from the RGB colors.
 *
 * {@section Sharing <code>IndexColorModel</code> instances}
 * The color model is retained by the palette as a {@linkplain WeakReference weak reference}
 * (<strong>not</strong> as a {@linkplain java.lang.ref.SoftReference soft reference}) because
 * it may consume up to 256 kilobytes. The purpose of the weak reference is to share existing
 * instances in order to reduce memory usage; the purpose is not to provide caching.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Antoine Hnawia (IRD)
 * @author Quentin Boileau (Geomatys)
 * @version 3.21
 *
 * @since 2.4
 * @module
 */
@Immutable
public abstract class Palette {
    /**
     * The originating factory.
     */
    final PaletteFactory factory;

    /**
     * The name of this palette.
     */
    protected final String name;

    /**
     * The number of bands in the {@linkplain ColorModel color model}.
     * The value is 1 in the vast majority of cases.
     */
    protected final int numBands;

    /**
     * The band to display, in the range 0 inclusive to {@link #numBands} exclusive.
     * This is used when an image contains more than one band but only one band can
     * be used for computing the colors to display. For example {@link IndexColorModel}
     * works on only one band.
     */
    protected final int visibleBand;

    /**
     * The sample model to be given to {@link ImageTypeSpecifier}.
     */
    private transient SampleModel samples;

    /**
     * A weak reference to the color model. This color model may consume a significant
     * amount of memory (up to 256 kb). Consequently, we will prefer {@link WeakReference}
     * over {@link java.lang.ref.SoftReference}. The purpose of this weak reference is to
     * share existing instances, not to cache it since it is cheap to rebuild.
     */
    private transient Reference<ColorModel> colors;

    /**
     * A weak reference to the image specifier to be returned by {@link #getImageTypeSpecifier}.
     * We use weak reference because the image specifier contains a reference to the color model
     * and we don't want to prevent it to be garbage collected. See {@link #colors} for an
     * explanation about why we use weak instead of soft references.
     */
    private transient Reference<ImageTypeSpecifier> specifier;

    /**
     * Creates a palette with the specified name.
     *
     * @param factory     The originating factory.
     * @param name        The palette name.
     * @param numBands    The number of bands (usually 1) to assign to {@link #numBands}.
     * @param visibleBand The visible band (usually 0) to assign to {@link #visibleBand}.
     */
    protected Palette(final PaletteFactory factory, final String name,
                      final int numBands, final int visibleBand)
    {
        ArgumentChecks.ensureNonNull("factory", factory); // Can't use factory.getErrorResources() here.
        if (name == null) {
            throw new NullArgumentException(factory.getErrorResources().getString(
                    Errors.Keys.NULL_ARGUMENT_$1, "name"));
        }
        ensureInsideBounds(numBands, 0, 255); // This maximal value is somewhat arbitrary.
        ensureInsideBounds(visibleBand, 0, numBands-1);
        this.factory     = factory;
        this.name        = name.trim();
        this.numBands    = numBands;
        this.visibleBand = visibleBand;
    }

    /**
     * Ensures that the specified values in inside the expected bounds (inclusives).
     *
     * @throws IllegalArgumentException if the specified values are outside the bounds.
     */
    final void ensureInsideBounds(final int value, final int min, final int max)
            throws IllegalArgumentException
    {
        if (value < min || value > max) {
            throw new IllegalArgumentException(factory.getErrorResources().getString(
                    Errors.Keys.VALUE_OUT_OF_BOUNDS_$3, value, min, max));
        }
    }

    /**
     * Returns the scale from <cite>normalized values</cite> (values in the range [0..1])
     * to values in the range of this palette.
     */
    double getScale() {
        return 1;
    }

    /**
     * Returns the offset from <cite>normalized values</cite> (values in the range [0..1])
     * to values in the range of this palette.
     */
    double getOffset() {
        return 0;
    }

    /**
     * Returns the color model for this palette. This method tries to reuse existing
     * color model if possible, since it may consume a significant amount of memory.
     *
     * @return  The color model for this palette.
     * @throws  FileNotFoundException If the RGB values need to be read from a file
     *          and this file (typically inferred from {@link #name}) is not found.
     * @throws  IOException  If an other kind of I/O error occurred.
     */
    public synchronized ColorModel getColorModel() throws FileNotFoundException, IOException {
        if (colors != null) {
            final ColorModel candidate = colors.get();
            if (candidate != null) {
                return candidate;
            }
        }
        return getImageTypeSpecifier().getColorModel();
    }

    /**
     * Returns the image type specifier for this palette. The default implementation first check
     * if the specified still in the cache. If not, then the {@link #createImageTypeSpecifier()}
     * method is invoked and its result is stored in the cache for future reuse.
     *
     * @return  The image type specified for this palette.
     * @throws  FileNotFoundException If the RGB values need to be read from a file
     *          and this file (typically inferred from {@link #name}) is not found.
     * @throws  IOException  If an other kind of I/O error occurred.
     */
    public synchronized ImageTypeSpecifier getImageTypeSpecifier() throws FileNotFoundException, IOException {
        if (specifier != null) {
            final ImageTypeSpecifier candidate = specifier.get();
            if (candidate != null) {
                return candidate;
            }
        }
        if (samples != null && colors != null) {
            final ColorModel candidate = colors.get();
            if (candidate != null) {
                final ImageTypeSpecifier its = new ImageTypeSpecifier(candidate, samples);
                specifier = new WeakReference<ImageTypeSpecifier>(its);
                return its;
            }
        }
        final ImageTypeSpecifier its = createImageTypeSpecifier();
        samples   = its.getSampleModel();
        colors    = new PaletteDisposer(this, its.getColorModel());
        specifier = new WeakReference<ImageTypeSpecifier>(its);
        return its;
    }

    /**
     * Creates a new image type specifier for this palette. This method is invoked by
     * {@link #getImageTypeSpecifier()} when the specifier is not present in the cache.
     *
     * @return  The image type specified for this palette.
     * @throws  FileNotFoundException If the RGB values need to be read from a file
     *          and this file (typically inferred from {@link #name}) is not found.
     * @throws  IOException  If an other kind of I/O error occurred.
     *
     * @since 3.11
     */
    protected abstract ImageTypeSpecifier createImageTypeSpecifier() throws FileNotFoundException, IOException;

    /**
     * Returns the color palette as an image of the specified size.
     * This is useful for looking visually at a color palette.
     * <p>
     * This method uses the color model created by {@link #getColorModel()} and does not write
     * any text in the image. Consequently the colors in the returned image should be identical
     * to the colors of the data rendered using this {@code Palette}.
     *
     * @param size The image size. The palette will be vertical if
     *        <code>size.{@linkplain Dimension#height height}</code> &gt;
     *        <code>size.{@linkplain Dimension#width  width }</code>
     * @return The color palette as an image of the given size.
     * @throws IOException if the color values can't be read.
     */
    public RenderedImage getImage(final Dimension size) throws IOException {
        final IndexColorModel colors = (IndexColorModel) getColorModel();
        final WritableRaster  raster = colors.createCompatibleWritableRaster(size.width, size.height);
        final BufferedImage   image  = new BufferedImage(colors, raster, false, null);
        int xmin   = raster.getMinX();
        int ymin   = raster.getMinY();
        int width  = raster.getWidth();
        int height = raster.getHeight();
        final boolean horizontal = size.width >= size.height;
        // Computation will be performed as if the image were horizontal.
        // If it is not, interchanges x and y values.
        if (!horizontal) {
            int tmp;
            tmp = xmin;  xmin  = ymin;   ymin   = tmp;
            tmp = width; width = height; height = tmp;
        }
        final int xmax = xmin + width;
        final int ymax = ymin + height;
        final double scale  = getScale() / width;
        final double offset = getOffset();
        for (int x=xmin; x<xmax; x++) {
            final double value = offset + scale*(x-xmin);
            for (int y=ymin; y<ymax; y++) {
                if (horizontal) {
                    raster.setSample(x, y, 0, value);
                } else {
                    raster.setSample(y, x, 0, value);
                }
            }
        }
        return image;
    }

    /**
     * Returns the color palette as a smoothed image of the specified size, together with the
     * palette name. The image returned by this method differs from {@link #getImage(Dimension)}
     * in three ways:
     * <p>
     * <ul>
     *   <li>The image returned by this method uses a different color model than the images to be
     *       produced by this {@code Palette}.</li>
     *   <li>The colors in this image are interpolated in order to produce a smooth image.
     *       Consequently, some colors in the returned image may not exist in the palette.</li>
     *   <li>The palette name is written in the returned image.</li>
     * </ul>
     *
     * @param size The image size.
     * @return The color palette as an image of the given size.
     * @throws IOException if the color values can't be read.
     *
     * @since 3.21
     */
    public RenderedImage getLegend(final Dimension size) throws IOException {
        final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) image.getGraphics();
        final Color[] colors = factory.getColors(name);
        final float[] fractions = new float[colors.length];
        for (int i=0; i<colors.length; i++) {
            fractions[i] = (float) i / (colors.length - 1);
        }
        final float centerY = size.height * 0.5f;
        g.setPaint(new LinearGradientPaint(
                0, centerY,          // The gradiant axis start in user space.
                size.width, centerY, // The gradient axis end in user space.
                fractions,           // Numbers ranging from 0 to 1 specifying the distribution of colors along the grandient.
                colors));            // Colors corresponding to each fractional values.
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.WHITE);
        final Font font = new Font("Dialog", Font.BOLD, 13);
        final Rectangle2D nameBounds = g.getFontMetrics(font).getStringBounds(name, g);
        g.setFont(font);
        g.drawString(name,
                (float) (size.width  - nameBounds.getWidth())  / 2,
                (float) (size.height + nameBounds.getHeight()) / 2);
        g.dispose();
        return image;
    }

    /**
     * Returns a hash value for this palette. See {@link #equals(Object)} for information
     * about which attributes can be used in the computation.
     */
    @Override
    public int hashCode() {
        return name.hashCode() + 31*numBands + visibleBand;
    }

    /**
     * Compares this palette with the specified object for equality. This method shall compare only
     * the values given to the constructor. It shall not trig the {@link ColorModel} construction,
     * because this {@code equals} method is used by {@link PaletteFactory#palettes} in order to
     * check if an existing {@code Palette} instance can be reused.
     *
     * @param  object The object to compare with this palette for equality.
     * @return {@code true} if the given object is equal to this palette.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && getClass() == object.getClass()) {
            final Palette that = (Palette) object;
            return this.numBands    == that.numBands    &&
                   this.visibleBand == that.visibleBand &&
                   Utilities.equals(this.name, that.name);
            /*
             * Note: we do not compare PaletteFactory on purpose, since two instances could be
             * identical except for the locale to use for formatting error messages.   Because
             * Palettes are used as keys in the PaletteFactory.palettes pool, we don't want to
             * get duplicated palettes only because they format error messages differently.
             */
        }
        return false;
    }
}
