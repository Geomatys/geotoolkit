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
package org.geotoolkit.image.jai;

import java.util.Map;
import java.util.Vector;
import java.util.Arrays;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;

import org.apache.sis.internal.util.Numerics;
import org.geotoolkit.image.TransfertRectIter;

import static javax.media.jai.ImageLayout.COLOR_MODEL_MASK;
import static javax.media.jai.ImageLayout.SAMPLE_MODEL_MASK;


/**
 * Applies a mask (typically a {@linkplain javax.media.jai.operator.BinarizeDescriptor binary
 * image}) on an source image. For every pixel in the source image there is a choice:
 * <p>
 * <ul>
 *   <li>If the corresponding pixel in the mask is 0, then the source pixel is copied unchanged
 *       to the destination image.</li>
 *   <li>Otherwise there is a choice:<ul>
 *       <li>If replacement values were explicitly given to this image operation, then those
 *           values are copied to the destination image.</li>
 *       <li>Otherwise the value from the mask is used as the replacement values.</li>
 *       </ul></li>
 * </ul>
 * <p>
 * Example:
 * <p>
 * <table align="center" cellpadding="15" border="1">
 * <tr><th>Input</th><th>output</th></tr><tr>
 * <td><img src="doc-files/sample-rgb.png" border="1"> ,
 *     <img src="doc-files/silhouette.png" border="1"></td>
 * <td><img src="doc-files/mask.png" border="1"></td>
 * </tr></table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class Mask extends PointOpImage {
    /**
     * The name of this operation in the JAI registry.
     * This is {@value}.
     */
    public static final String OPERATION_NAME = "org.geotoolkit.Mask";

    /**
     * The values to copy to the destination images for every masked pixels, or {@code null} for
     * using the mask values. If non-null, then the array length must be equals to the number of
     * bands in the destination image.
     */
    private final double[] newValues;

    /**
     * Constructs a new mask for the given image. While this constructor is public, it should
     * usually not be invoked directly. You should use {@linkplain javax.media.jai.JAI} factory
     * methods instead.
     *
     * @param source        The source image.
     * @param mask          The mask. If it has smaller bounds than the source image, then every
     *                      pixels outside the mask are treated as if they were zero.
     * @param layout        The image layout.
     * @param configuration The image properties and rendering hints.
     * @param newValues     The values to copy to the destination images for every masked pixels,
     *                      or {@code null} for using the mask values. If non-null, then the array
     *                      length must be equals to the number of bands in the destination image.
     */
    public Mask(final RenderedImage source, final RenderedImage mask, final ImageLayout layout,
            final Map<?,?> configuration, final double[] newValues)
    {
        super(source, mask, layout(source, layout), configuration, false);
        this.newValues = (newValues == null) ? null :
                Arrays.copyOf(newValues, source.getSampleModel().getNumBands());
        permitInPlaceOperation();
    }

    /**
     * If the user didn't specified explicitly a sample or a color model, creates default ones.
     * This method is actually a workaround for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     *
     * @param  layout The user-supplied layout.
     * @return A layout with at least a color model.
     */
    private static ImageLayout layout(final RenderedImage source, ImageLayout layout) {
        if (layout == null) {
            layout = new ImageLayout();
        } else if ((layout.getValidMask() & (SAMPLE_MODEL_MASK | COLOR_MODEL_MASK)) == 0) {
            layout = (ImageLayout) layout.clone();
        } else {
            return layout;
        }
        return layout.setSampleModel(source.getSampleModel()).setColorModel(source.getColorModel());
    }

    /**
     * Returns the source images.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<RenderedImage> getSources() {
        return super.getSources();
    }

    /**
     * Computes a rectangle of outputs.
     *
     * @param sources  The source images. Should be an array of length 2.
     * @param dest     The raster to be filled in.
     * @param destRect The region within the raster to be filled.
     */
    @Override
    protected void computeRect(final PlanarImage[] sources, final WritableRaster dest,
            final Rectangle destRect)
    {
        assert sources.length == 2;
        final PlanarImage source = sources[0];
        final PlanarImage mask   = sources[1];
        final RectIter mit = RectIterFactory.create(mask, destRect);
        final WritableRectIter iter = TransfertRectIter.create(source, dest, destRect);
        final boolean needCopy = (iter instanceof TransfertRectIter);
        if (iter.finishedLines()) {
            return;
        }
        /*
         * Below is a copy-and-paste of the same code for the 3 transfer types. While the
         * textual code look identical except for the 2 first lines, many methods that are
         * actually invoked are different because of method overloading.
         */
        final int numBands = getNumBands();
        switch (sampleModel.getDataType()) {
            /*
             * Every integer types.
             */
            default: {
                final int[] replacement = Numerics.copyAsInts(newValues);
                final int[] buffer = new int[numBands];
                do {
                    mit.startPixels();
                    iter.startPixels();
                    if (!iter.finishedPixels()) {
                        do {
                            final int m = mit.getSample();
                            mit.nextPixel();
                            if (m != 0) {
                                // Found a value to mask.
                                final int[] samples;
                                if (replacement != null) {
                                    samples = replacement;
                                } else {
                                    Arrays.fill(samples = buffer, m);
                                }
                                iter.setPixel(samples);
                                continue;
                            }
                            // Found a unmasked value to copy.
                            if (!needCopy) continue;
                            iter.setPixel(iter.getPixel(buffer));
                        } while (!iter.nextPixelDone());
                    }
                    mit.nextLine();
                } while (!iter.nextLineDone());
            }
        }
    }
}
