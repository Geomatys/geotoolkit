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
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;

import javax.media.jai.OpImage;
import javax.media.jai.ImageLayout;

import org.geotoolkit.util.collection.IntegerList;
import org.geotoolkit.image.color.ColorUtilities;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * Performs the Flood Fill operation on the given raster.
 *
 * {@section Algorithm}
 * This class implements a <cite>Scan line flood fill</cite> algorithm as
 * <a href="http://en.wikipedia.org/wiki/Flood_fill">documented in Wikipedia</a>
 * on June 2009, section <cite>Alternative implementations</cite> (queue-based)
 * modified as described in <cite>Scanline fill</cite> section. The algorithm
 * has been modified in order to work properly with tiled images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 *
 * @todo This class is abstract for now because not yet implemented as a JAI operation.
 *       However the static methods are ready for use.
 */
public abstract class FloodFill extends OpImage {
    /**
     * The name of this operation in the JAI registry.
     * This is {@value}.
     */
    public static final String OPERATION_NAME = "org.geotoolkit.FloodFill";

    /**
     * The old values in the source image.
     */
    private final double[][] oldValues;

    /**
     * The new values in the source image. The array length must matches the number of bands.
     */
    private final double[] newValues;

    /**
     * Constructs a new Flood Fill for the given image. While this constructor is public, it
     * should usually not be invoked directly. You should use {@linkplain javax.media.jai.JAI}
     * factory methods instead.
     *
     * @param source        The source image.
     * @param layout        The image layout.
     * @param configuration The image properties and rendering hints.
     * @param oldValues     The old values in the source images.
     * @param newValues     The new values in the source images.
     */
    public FloodFill(final RenderedImage source, final ImageLayout layout,
            final Map<?,?> configuration, double[][] oldValues, double[] newValues)
    {
        super(new Vector<>(Collections.singleton(source)), layout, configuration, false);
        final int numBands = source.getSampleModel().getNumBands();
        this.newValues = newValues = newValues.clone();
        this.oldValues = oldValues = oldValues.clone();
        for (int i=0; i<oldValues.length; i++) {
            oldValues[i] = Arrays.copyOf(oldValues[i], numBands);
        }
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
     * Colors an area of connected pixels with the same set of color.
     * The fill is performed in place in the given image.
     * The operation is performed immediately; it is not deferred like usual JAI operations.
     *
     * @param image     The image in which to colors an area.
     * @param oldColors The colors to replace (usually only 1 color, but more are allowed).
     * @param newColors The new colors replacing the old ones.
     * @param points    The coordinate of the starting point. There is usually only one
     *                  such point, but more are allowed.
     */
    public static void fill(final WritableRenderedImage image, final Color[] oldColors,
            final Color newColors, final Point... points)
    {
        final int numBands = image.getSampleModel().getNumBands();
        final double[][] oldValues = new double[oldColors.length][];
        for (int i=0; i<oldValues.length; i++) {
            oldValues[i] = ColorUtilities.toDoubleValues(oldColors[i], numBands);
        }
        final double[] newValues = ColorUtilities.toDoubleValues(newColors, numBands);
        fill(image, oldValues, newValues, points);
    }

    /**
     * Colors an area of connected pixels with the same set of color.
     * The fill is performed in place in the given image.
     * The operation is performed immediately; it is not deferred like usual JAI operations.
     *
     * @param image     The image in which to colors an area.
     * @param oldValues The colors to replace (usually only 1 color, but more are allowed).
     * @param newValues The new colors replacing the old ones.
     * @param points    The coordinate of the starting point. There is usually only one
     *                  such point, but more are allowed.
     */
    public static void fill(final WritableRenderedImage image, final double[][] oldValues,
            final double[] newValues, final Point... points)
    {
        /*
         * Copies the old values in a set of SampleValues objects. The exact type of SampleValues
         * will depend on the transfer type.
         */
        final int transferType = image.getSampleModel().getTransferType();
        final Set<SampleValues> oldSamples = new HashSet<>(hashMapCapacity(oldValues.length));
        for (final double[] samples : oldValues) {
            oldSamples.add(SampleValues.getInstance(transferType, samples));
        }
        final SampleValues newSamples = SampleValues.getInstance(transferType, newValues);
        oldSamples.remove(newSamples); // Necessary for avoiding infinite loop.
        if (oldSamples.isEmpty()) {
            return;
        }
        /*
         * Copies the points coordinates in an IntegerList. The content of that list will be
         * (x,y) tupples translated in such a way that the upper left pixel is located at (0,0)
         * by definition (this is always the case with BufferedImage, but not necessarily with
         * other kind of RenderedImage). Later we will call the fill(WritableRaster...) method
         * in a loop as long as the list is not empty. Note that the fill(WritableRaster...)
         * method may itself push more points in that list (can occur only if the image is tiled).
         */
        final int xmin   = image.getMinX();
        final int ymin   = image.getMinY();
        final int width  = image.getWidth();
        final int height = image.getHeight();
        final Rectangle bounds = new Rectangle(xmin, ymin, width, height);
        if (bounds.isEmpty()) {
            return;
        }
        final IntegerList stack = new IntegerList(8, Math.max(width, height)-1);
        for (final Point point : points) {
            int x = point.x - xmin;
            int y = point.y - ymin;
            if (x < 0 || x >= width || y < 0 || y >= height) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.POINT_OUTSIDE_COVERAGE_1,
                        new StringBuilder().append(point.x).append(',').append(point.y).toString()));
            }
            stack.addInteger(x);
            stack.addInteger(y);
        }
        /*
         * Now process to the filling tile by tile.
         *
         * TODO: need to process together every points on the same tile, in order to avoid
         *       loading and flushing tiles too often.
         */
        final int tileXOff   = image.getTileGridXOffset();
        final int tileYOff   = image.getTileGridYOffset();
        final int tileWidth  = image.getTileWidth();
        final int tileHeight = image.getTileHeight();
        final int minTileY   = image.getMinTileY();
        final int maxTileY   = image.getNumXTiles() + minTileY - 1; // inclusive.
        while (!stack.isEmpty()) {
            final int y = stack.removeLast() + ymin;
            final int x = stack.removeLast() + xmin;
            final int tileX = XToTileX(x, tileXOff, tileWidth);
            final int tileY = YToTileY(y, tileYOff, tileHeight);
            final Raster top    = (tileY != minTileY) ? image.getTile(tileX, tileY-1) : null;
            final Raster bottom = (tileY != maxTileY) ? image.getTile(tileX, tileY+1) : null;
            final WritableRaster raster = image.getWritableTile(tileX, tileY);
            try {
                fill(raster, top, bottom, bounds, x, y, oldSamples, newSamples, stack);
            } finally {
                image.releaseWritableTile(tileX, tileY);
            }
        }
    }

    /**
     * Process to the Scan Line Flood Fill in one tile. This method will be invoked for
     * every tiles to process, and may be invoked more than once for the same tile.
     * <p>
     * Every access on {@code globalStack} will be performed in a synchronized block.
     * This allow multi-threading, using a different thread for different tiles.
     *
     * @param raster       The tile in which to apply the Scan Line Flood Fill.
     * @param rasterTop    The tile just above the given {@code raster}, or null if none.
     * @param rasterBottom The tile just below the given {@code raster}, or null if none.
     * @param imageBounds  The bounds of the whole image (encompassing every tiles).
     * @param x            The <var>x</var> ordinate of the starting point.
     * @param y            The <var>y</var> ordinate of the starting point.
     * @param oldValues    The set of old colors to replace.
     * @param newValues    The new value to give to the filled pixels.
     * @param globalStack  Where to push the point needed further examination by other tiles.
     */
    private static void fill(final WritableRaster raster, final Raster rasterTop,
            final Raster rasterBottom, final Rectangle imageBounds, int x, int y,
            final Set<SampleValues> oldValues, final SampleValues newValues,
            final IntegerList globalStack)
    {
        final SampleValues buffer = newValues.instance();
        if (!oldValues.contains(buffer.getPixel(raster, x, y))) {
            return;
        }
        final int width  = raster.getWidth();
        final int height = raster.getHeight();
        final int xmin   = raster.getMinX();
        final int ymin   = raster.getMinY();
        final int xmax   = xmin + width  - 1; // Inclusive
        final int ymax   = ymin + height - 1; // Inclusive
        /*
         * Prepares a stack of coordinates to be processed in successive passes of
         * the loop below. Note that the coordinates in this stack are relative to
         * the tile upper left corner, i.e. (xmin,ymin) must be subtracted.  This
         * is for allowing IntegerList to do its job (pack the data).
         */
        final IntegerList stack = new IntegerList(128, Math.max(width, height)-1);
        do {
            /*
             * Scans the current line toward the left. After this loop,
             * (x,y) will be the location of the leftmost pixel to replace.
             */
            assert x >= xmin && x <= xmax : x;
            assert y >= ymin && y <= ymax : y;
            do if (--x < xmin) {
                // We have reached the left border. The inspection will need to continue in
                // the tile at the left, if any. It will be caller's responsibility to use
                // the information that we put in 'globalStack'.
                if (imageBounds.contains(x,y)) {
                    synchronized (globalStack) {
                        globalStack.addInteger(x - imageBounds.x);
                        globalStack.addInteger(y - imageBounds.y);
                    }
                }
                break;
            } while (oldValues.contains(buffer.getPixel(raster, x, y)));
            x++;
            /*
             * The loop below scans toward the right as long as there is pixels to replace.
             */
            boolean omitTopCheck    = false;
            boolean omitBottomCheck = false;
            do {
                newValues.setPixel(raster, x, y);
                /*
                 * The do ... while loop below is executed exactly 2 times, for checking
                 * the pixel on top and on bottom of the (x,y) location.
                 *
                 *  1: (checkingTop == true ) checks the pixel at the (x, y-1) location.
                 *  2: (checkingTop == false) checks the pixel at the (x, y+1) location.
                 */
                boolean checkingTop = true;
                boolean omitCheck   = omitTopCheck;
                int     checkAt     = y - 1;
                Raster  border      = (y == ymin) ? rasterTop : raster;
                while (true) {
                    /*
                     * Checks the pixel at the top or the bottom (depending on 'checkingTop' value)
                     * of the current (x,y) location. For every sequences of consecutive pixels to
                     * replace, push in the stack the location of the first pixel in that sequence.
                     */
                    if (border != null && oldValues.contains(buffer.getPixel(border, x, checkAt)) != omitCheck) {
                        if ((omitCheck = !omitCheck) == true) {
                            if (border == raster) {
                                // Found a point which need further examination in this tile.
                                stack.addInteger(x       - xmin);
                                stack.addInteger(checkAt - ymin);
                            } else if (imageBounds.contains(x, checkAt)) {
                                // Found a point which need further examination in an other tile.
                                synchronized (globalStack) {
                                    globalStack.addInteger(x       - imageBounds.x);
                                    globalStack.addInteger(checkAt - imageBounds.y);
                                }
                            }
                        }
                    }
                    if (checkingTop) {
                        /*
                         * Just finished the first execution of the loop (checking the top pixel).
                         * Saves the "omitCheck" state and prepare the loop for the check of the
                         * bottom pixel.
                         */
                        checkingTop  = false;
                        omitTopCheck = omitCheck;
                        omitCheck    = omitBottomCheck;
                        checkAt      = y + 1;
                        border       = (y == ymax) ? rasterBottom : raster;
                        continue;
                    }
                    /*
                     * Just finished the second execution of the loop (checking the bottom pixel).
                     * Saves the "omitCheck" state and we are done.
                     */
                    omitBottomCheck = omitCheck;
                    break;
                }
                /*
                 * If we have not yet reached the right border, move one pixel to the right and
                 * continue the inspection of current row. If we have reached the right border,
                 * then the inspection will need to continue in the tile at the right, if any
                 * This is the same processing than we did for the left border.
                 */
                if (x++ == xmax) {
                    if (imageBounds.contains(x,y)) {
                        synchronized (globalStack) {
                            globalStack.addInteger(x - imageBounds.x);
                            globalStack.addInteger(y - imageBounds.y);
                        }
                    }
                    break;
                }
            } while (oldValues.contains(buffer.getPixel(raster, x, y)));
            /*
             * The above loop may have pushed additional points to process on the stack.
             * If this is the case, extract the point on the top of the stack an continue.
             * Otherwise we are done.
             */
            if (stack.isEmpty()) {
                break;
            }
            y = stack.removeLast() + ymin;
            x = stack.removeLast() + xmin;
        } while (true);
    }
}
