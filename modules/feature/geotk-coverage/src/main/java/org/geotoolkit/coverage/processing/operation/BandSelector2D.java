/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.processing.operation;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import org.apache.sis.internal.raster.ColorModelFactory;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.processing.OperationJAI;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.opengis.parameter.ParameterValueGroup;


/**
 * A grid coverage containing a subset of an other grid coverage's sample dimensions,
 * and/or a different {@link ColorModel}. A common reason for changing the color model
 * is to select a different visible band. Consequently, the {@code "SelectSampleDimension"}
 * operation name still appropriate in this context.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Andrea Aime (TOPP)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
final class BandSelector2D extends GridCoverage2D {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6572017920456641730L;

    /**
     * The mapping to bands in the source grid coverage.
     * May be {@code null} if all bands were keept.
     */
    private final int[] bandIndices;

    /**
     * Constructs a new {@code BandSelect2D} grid coverage. This grid coverage will use
     * the same coordinate reference system and the same geometry than the source grid
     * coverage.
     *
     * @param source      The source coverage.
     * @param image       The image to use.
     * @param bands       The sample dimensions to use.
     * @param bandIndices The mapping to bands in {@code source}. Not used
     *                    by this constructor, but keept for futur reference.
     *
     * @todo It would be nice if we could use always the "BandSelect" operation
     *       without the "Null" one. But as of JAI-1.1.1, "BandSelect" does not
     *       detect by itself the case were no copy is required.
     */
    private BandSelector2D(final GridCoverage2D source,
                           final PlanarImage image,
                           final SampleDimension[] bands,
                           final int[] bandIndices,
                           final Hints hints)
    {
        super(source.getName(),               // The grid source name
              image,                          // The underlying data
              source.getGridGeometry(),       // The grid geometry (unchanged).
              bands,                          // The sample dimensions
              new GridCoverage2D[] {source},  // The source grid coverages.
              null, hints);                   // Properties

        this.bandIndices = bandIndices;
        assert bandIndices == null || bandIndices.length == bands.length;
    }

    /**
     * Applies the band select operation to a grid coverage.
     *
     * @param  parameters List of name value pairs for the parameters.
     * @param  A set of rendering hints, or {@code null} if none.
     * @return The result as a grid coverage.
     */
    static GridCoverage2D create(GridCoverage2D source, final ParameterValueGroup parameters, Hints hints) {
        /*
         * Fetches all parameters, clones them if needed. The "VisibleSampleDimension" parameter
         * is Geotk-specific and optional. We get it as an Integer both for catching null value,
         * and also because it is going to be stored as an image's property anyway.
         */
        int[] bandIndices = parameters.parameter("SampleDimensions").intValueList();
        if (bandIndices != null) {
            bandIndices = bandIndices.clone();
        }
        Integer visibleBand = (Integer) parameters.parameter("VisibleSampleDimension").getValue();
        /*
         * Prepares the informations needed for JAI's "BandSelect" operation. The loop below
         * should be executed only once, except if the source grid coverage is itself an instance
         * of an other BandSelect2D object, in which case the sources will be extracted
         * recursively until a non-BandSelect2D object is found.
         */
        int visibleSourceBand;
        int visibleTargetBand;
        SampleDimension[] sourceBands;
        SampleDimension[] targetBands;
        RenderedImage sourceImage;
        while (true) {
            sourceBands = source.getSampleDimensions().toArray(new SampleDimension[0]);
            targetBands = sourceBands;
            /*
             * Constructs an array of target bands.  If the 'bandIndices' parameter contains
             * only "identity" indices (0, 1, 2...), then we will work as if no band indices
             * were provided. It will allow us to use the "Null" operation rather than
             * "BandSelect", which make it possible to avoid to copy raster data.
             */
            if (bandIndices != null) {
                if (bandIndices.length != sourceBands.length || !isIdentity(bandIndices)) {
                    targetBands = new SampleDimension[bandIndices.length];
                    for (int i=0; i<bandIndices.length; i++) {
                        targetBands[i] = sourceBands[bandIndices[i]];
                    }
                } else {
                    bandIndices = null;
                }
            }
            sourceImage = source.getRenderedImage();
            visibleSourceBand = CoverageUtilities.getVisibleBand(sourceImage);
            if (visibleBand != null) {
                visibleTargetBand = mapSourceToTarget(visibleBand.intValue(), bandIndices);
                if (visibleSourceBand < 0) {
                    // TODO: localize
                    throw new IllegalArgumentException("Visible sample dimension is " +
                            "not among the ones specified in SampleDimensions param");
                }
            } else {
                // Try to keep the original one, if it hasn't been selected, fall
                // back on the first selected band.
                visibleTargetBand = mapSourceToTarget(visibleSourceBand, bandIndices);
                if (visibleTargetBand < 0) {
                    visibleTargetBand = 0;
                }
            }
            if (bandIndices == null && visibleSourceBand == visibleTargetBand) {
                return source;
            }
            if (!(source instanceof BandSelector2D)) {
                break;
            }
            /*
             * If the source coverage was the result of an other "BandSelect" operation, go up
             * the chain and checks if an existing GridCoverage could fit. We do that in order
             * to avoid to create new GridCoverage everytime the user is switching the visible
             * band. For example we could change the visible band from 0 to 1, and then come
             * back to 0 later.
             */
            final int[] parentIndices = ((BandSelector2D) source).bandIndices;
            if (parentIndices != null) {
                if (bandIndices != null) {
                    for (int i=0; i<bandIndices.length; i++) {
                        bandIndices[i] = parentIndices[bandIndices[i]];
                    }
                } else {
                    bandIndices = parentIndices.clone();
                }
            }
            assert source.getSources().size() == 1 : source;
            source = (GridCoverage2D) source.getSources().get(0);
        }
        /*
         * All required information are now know. Creates the GridCoverage resulting from the
         * operation. A color model will be defined only if the user didn't specify an explicit
         * one.
         */
        String operation = "Null";
        ImageLayout layout = null;
        if (hints != null) {
            layout = (ImageLayout) hints.get(JAI.KEY_IMAGE_LAYOUT);
        }
        if (layout == null) {
            layout = new ImageLayout();
        }
        if (visibleBand!=null || !layout.isValid(ImageLayout.COLOR_MODEL_MASK)) {
            ColorModel colors = sourceImage.getColorModel();
            if (colors instanceof IndexColorModel &&
                sourceBands[visibleSourceBand].equals(targetBands[visibleTargetBand]))
            {
                /*
                 * If the source color model was an instance of  IndexColorModel,  reuse
                 * its color mapping. It may not matches the category colors if the user
                 * provided its own color model. We are better to use what the user said.
                 */
                final IndexColorModel indexed = (IndexColorModel) colors;
                final int[] ARGB = new int[indexed.getMapSize()];
                indexed.getRGBs(ARGB);
                colors = ColorModelFactory.createIndexColorModel(ARGB, targetBands.length, visibleTargetBand, -1);
            } else {
                colors = SampleDimensionUtils.getColorModel(targetBands[visibleTargetBand], visibleTargetBand, targetBands.length);
            }
            /*
             * If we are not able to provide a color model because our sample dimensions
             * are very simple, let's JAI do its magic and figure out the best one for us.
             */
            if (colors != null) {
                layout.setColorModel(colors);
            }
            if (hints != null) {
                hints = hints.clone();
                hints.put(JAI.KEY_IMAGE_LAYOUT, layout);
            } else {
                hints = new Hints(JAI.KEY_IMAGE_LAYOUT, layout);
            }
        }
        if (visibleBand == null) {
            visibleBand = visibleTargetBand;
        }
        ParameterBlock params = new ParameterBlock().addSource(sourceImage);
        if (targetBands != sourceBands) {
            operation = "BandSelect";
            params = params.add(bandIndices);
        }
        final PlanarImage image = OperationJAI.getJAI(hints).createNS(operation, params, hints);
        image.setProperty("GC_VisibleBand", visibleBand);
        return new BandSelector2D(source, image, targetBands, bandIndices, hints);
    }

    /**
     * Maps the specified source band number to the target band index after the
     * selection/reordering process imposed by targetSampleDimensions is applied.
     *
     * @param  sourceBand  The index of a source band.
     * @param  bandIndices The indices of source bands to be retained for target, or {@code null}.
     * @return The target band indices, or {@code -1} if not found.
     */
    private static int mapSourceToTarget(final int sourceBand, final int[] bandIndices) {
        if (bandIndices == null) {
            return sourceBand;
        }
        for (int i=0; i<bandIndices.length; i++) {
            if (bandIndices[i] == sourceBand) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns {@code true} if the specified array contains increasing values 0, 1, 2...
     */
    private static boolean isIdentity(final int[] bands) {
        for (int i=0; i<bands.length; i++) {
            if (bands[i] != i) {
                return false;
            }
        }
        return true;
    }
}
