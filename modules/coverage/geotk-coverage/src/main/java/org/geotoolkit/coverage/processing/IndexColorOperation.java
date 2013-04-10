/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.processing;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.IndexColorModel;

import javax.media.jai.OpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.NullOpImage;

import net.jcip.annotations.Immutable;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;


/**
 * Operation applied on the {@link IndexColorModel} of the image backing the source grid coverage.
 * The {@link #doOperation doOperation} method extracts the color map of the source coverage as an
 * array of ARGB values, passes that array to {@link #transformColormap transformColormap} and
 * creates a new grid coverage using the new colors.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.14
 *
 * @since 1.2
 * @module
 */
@Immutable
public abstract class IndexColorOperation extends Operation2D {
    /**
     * Constructs an operation. The operation name will be the same than the
     * parameter descriptor name.
     *
     * @param descriptor The parameters descriptor.
     */
    protected IndexColorOperation(final DefaultParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns {@link ViewType#RENDERED} as the preferred view for computation purpose.
     */
    @Override
    protected ViewType getComputationView(final ParameterValueGroup parameters) {
        return ViewType.RENDERED;
    }

    /**
     * Returns {@code true} if the given color model is a gray scale.
     * This method does not check the case of {@link IndexColorModel}.
     */
    private static boolean isGrayScale(final ColorModel cm) {
        return (cm instanceof ComponentColorModel) && (cm.getNumComponents() == 1) &&
                cm.getColorSpace().getType() == ColorSpace.TYPE_GRAY;
    }

    /**
     * Performs the color transformation. This method invokes the {@link #transformColormap
     * transformColormap(...)} method with the ARGB colors found in the source image, its
     * {@link GridSampleDimension} and the parameters supplied to this method. The new colors
     * returned by {@code transformColormap} are used for creating a grid coverage backed by
     * and image using a new {@link IndexColorModel}.
     *
     * @param  parameters The parameters.
     * @param  hints Rendering hints (ignored in this implementation).
     * @return The result as a coverage.
     * @throws IllegalArgumentException if the image do not use an {@link IndexColorModel}.
     */
    @Override
    protected Coverage doOperation(final ParameterValueGroup parameters, final Hints hints)
            throws IllegalArgumentException
    {
        final GridCoverage2D[]    sources = new GridCoverage2D[1];
        final ViewType         targetView = extractSources(parameters, sources);
        final GridCoverage2D       source = sources[0];
        final RenderedImage         image = source.getRenderedImage();
        final GridSampleDimension[] bands = source.getSampleDimensions();
        final int visibleBand = CoverageUtilities.getVisibleBand(image);
        ColorModel targetModel = image.getColorModel();
        boolean bandChanged = false;
        for (int i=0; i<bands.length; i++) {
            /*
             * Extracts the ARGB codes from the IndexColorModel and invokes the
             * transformColormap(...) method, which needs to be defined by subclasses.
             */
            GridSampleDimension band = bands[i];
            final ColorModel sourceModel = (i == visibleBand) ? image.getColorModel() : band.getColorModel();
            final IndexColorModel colors;
            final int[] ARGB;
            if (sourceModel instanceof IndexColorModel) {
                colors = (IndexColorModel) sourceModel;
                ARGB = new int[colors.getMapSize()];
                colors.getRGBs(ARGB);
            } else if (isGrayScale(sourceModel)) {
                colors = null;
                ARGB = new int[1 << sourceModel.getPixelSize()];
                for (int j=0; j<ARGB.length; j++) {
                    ARGB[j] = j;
                }
            } else {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_CLASS_$2,
                        Classes.getClass(sourceModel), IndexColorModel.class));
            }
            band = transformColormap(ARGB, i, band, parameters);
            /*
             * Checks if there is any change, either as a new GridSampleDimension instance or in
             * the ARGB array. Note that if the new GridSampleDimension is equals to the old one,
             * then the new one will be discarded since the old one is more likely to be a shared
             * instance.
             */
            if (!bands[i].equals(band)) {
                bands[i] = band;
                bandChanged = true;
            }
            boolean colorChanged = false;
            for (int j=0; j<ARGB.length; j++) {
                final int old = (colors != null) ? colors.getRGB(j) : j;
                if (ARGB[j] != old) {
                    colorChanged = true;
                    bandChanged  = true;
                    break;
                }
            }
            /*
             * If we changed the color of the visible band, then create immediately a new
             * color model for this band. The new color model will be given later to the
             * image operator.
             */
            if (colorChanged && (i == visibleBand)) {
                targetModel = ColorUtilities.getIndexColorModel(ARGB, bands.length, visibleBand, -1);
            }
        }
        if (!bandChanged) {
            return source.view(targetView);
        }
        /*
         * Gives the color model to the image layout and creates a new image using the Null
         * operation, which merely propagates its first source along the operation chain
         * unmodified (except for the ColorModel given in the layout in this case).
         */
        final ImageLayout layout = new ImageLayout().setColorModel(targetModel);
        final RenderedImage newImage = new NullOpImage(image, layout, null, OpImage.OP_COMPUTE_BOUND);
        final GridCoverage2D target = getFactory(hints).create(
                    source.getName(), newImage,
                    source.getCoordinateReferenceSystem(),
                    source.getGridGeometry().getGridToCRS(),
                    bands, new GridCoverage[] { source }, null);
        return target.view(targetView);
    }

    /**
     * Transforms the supplied RGB colors. This method is automatically invoked
     * by {@link #doOperation doOperation(...)} for each band in the source
     * {@link GridCoverage2D}. The {@code ARGB} array contains the ARGB values
     * from the current source and should be overridden with new ARGB values
     * for the destination image.
     * <p>
     * This method is usually invoked only once, since images backed by {@link IndexColorModel}
     * normally have only one band. However it may happen that an image contains additional
     * "invisible" bands, in which case this method will be invoked for those bands as well.
     *
     * @param ARGB            Alpha, Red, Green and Blue components to transform.
     * @param band            The band number, from 0 to the number of bands in the image -1.
     * @param sampleDimension The sample dimension of band {@code band}.
     * @param parameters      The user-supplied parameters.
     * @return A sample dimension identical to {@code sampleDimension} except for the
     *         colors. Subclasses may conservatively returns {@code sampleDimension}.
     */
    protected abstract GridSampleDimension transformColormap(final int[] ARGB, final int band,
            final GridSampleDimension sampleDimension, final ParameterValueGroup parameters);
}
