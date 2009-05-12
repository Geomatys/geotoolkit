/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.math.Vector;
import org.geotoolkit.math.VectorPair;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.gui.swing.Plot2D;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicProfiler;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;


/**
 * A plot of the estimated cost of loading tiles using a given mosaic. Given a {@link TileManager},
 * this method computes an estimation of the cost of loading tiles at different subsampling levels.
 * The details of the cost calculation is documented in the {@link MosaicProfiler} class. Users can
 * also provide their own pre-configured {@code MosaicProfiler} instance.
 * <p>
 * The calculations are performed by the {@code plotCostEstimation} methods, which may take a
 * while. Consequently those methods should be invoked from a background thread rather than the
 * <cite>Swing</cite> thread. As a convenience, {@code plotLater} methods are provided for
 * delegating those calculation to a background thread.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
public class MosaicPerformanceGraph extends Plot2D implements Dialog {
    /**
     * Small tolerance factor for the comparaison of floating point numbers.
     */
    private static final double EPS = 1E-6;

    /**
     * The seed to use for the random number generator.
     */
    private final long seed;

    /**
     * Number of samplings to performs per subsampling.
     */
    private int imagesPerSubsampling = 100;

    /**
     * Creates a default graph.
     */
    public MosaicPerformanceGraph() {
        super(true, false);
        seed = System.currentTimeMillis();
        setPreferredSize(new Dimension(600, 400));
    }

    /**
     * Returns the number of images to be requested for each subsampling level.
     *
     * @return The current number of image loadings to be done or similated per subsampling level.
     */
    public int getImagesPerSubsampling() {
        return imagesPerSubsampling;
    }

    /**
     * Sets the number of images to be requested for each subsampling level.
     *
     * @param n The new number of image loadings to be done or simulated per subsampling level.
     */
    public void setImagesPerSubsampling(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NOT_GREATER_THAN_ZERO_$1, n));
        }
        final int old = imagesPerSubsampling;
        imagesPerSubsampling = n;
        firePropertyChange("imagesPerSubsampling", old, n);
    }

    /**
     * Replaces NaN values by the previous value in the given array.
     */
    private static void replaceNaN(final float[] array) {
        float last = Float.NaN;
        for (int i=0; i<array.length; i++) {
            final float value = array[i];
            if (Float.isNaN(value)) {
                array[i] = last;
            } else {
                last = value;
            }
        }
    }

    /**
     * Adds a plot calculated using the given profiler. This method will run the profiler for every
     * uniform subsampling values ranging from the {@linkplain MosaicProfiler#getMinSubsampling
     * minimum} to the {@linkplain MosaicProfiler#getMaxSubsampling maximum} subsampling value,
     * inclusives.
     * <p>
     * This method can be invoked from any thread - it doesn't need to be the <cite>Swing</cite>
     * one. Since this method may take a while, it is recommanded to invoke it from a background
     * thread.
     *
     * @param  name The name to given to the plot, or {@code null} if none.
     * @param  profiler The profiler to use for estimating the cost of loading images.
     * @throws IOException if an I/O operation was required and failed.
     */
    public void plotCostEstimation(final String name, final MosaicProfiler profiler) throws IOException {
        final Dimension minSubsampling = profiler.getMinSubsampling();
        final Dimension maxSubsampling = profiler.getMaxSubsampling();
        final int ms = Math.max(minSubsampling.width, minSubsampling.height);
        final int ns = Math.min(maxSubsampling.width, maxSubsampling.height) - ms + 1;
        profiler.setMaxSubsampling(ms);
        final float[] cost = new float[ns];
        final float[] high = new float[ns];
        final float[] low  = new float[ns];
        for (int i=0; i<ns; i++) {
            profiler.setSeed(seed); // Use the same random values for each subsamplings.
            profiler.setMinSubsampling(i+ms);
            final Statistics stats = profiler.costSampling(imagesPerSubsampling);
            final double c = stats.mean();
            final double stdv = stats.standardDeviation(false);
            cost[i] = (float) c;
            low [i] = (float) Math.max(c - stdv, stats.minimum());
            high[i] = (float) Math.min(c + stdv, stats.maximum());
        }
        // Reset the profiler to its initial state.
        profiler.setMinSubsampling(minSubsampling);
        profiler.setMaxSubsampling(maxSubsampling);
        // Workaroud the points calculated with only 1 value.
        replaceNaN(low);
        replaceNaN(high);
        /*
         * Computes the main line, which a "stepwise" visual effect
         * for emphasing the discontinuous nature of subsampling.
         * Computes also the standard deviation to paint around the main line.
         */
        final Vector x  = Vector.createSequence(ms,     1, 1+ns);
        final Vector xm = Vector.createSequence(ms-0.5, 1, 1+ns);
        final VectorPair upper = new VectorPair(x,  Vector.create(high));
        final VectorPair main  = new VectorPair(xm, Vector.create(cost));
        final VectorPair lower = new VectorPair(x,  Vector.create(low ));
        upper.makeStepwise(+2);
        main .makeStepwise( 0);
        lower.makeStepwise(-2);
        upper.omitColinearPoints(EPS, EPS);
        main .omitColinearPoints(EPS, EPS);
        lower.omitColinearPoints(EPS, EPS);
        final Vector xs = upper.getX().concatenate(lower.getX().reverse());
        final Vector ys = upper.getY().concatenate(lower.getY().reverse());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                final int ns = getSeries().size();
                final Color color = DEFAULT_COLORS.get((ns/2) % DEFAULT_COLORS.size());
                final Color trans = new Color(color.getRGB() & 0x20FFFFFF, true);
                final Map<String,Object> properties = new HashMap<String,Object>(4);
                properties.put("Name", name);
                properties.put("Paint", trans);
                properties.put("Fill", Boolean.TRUE);
                if (ns == 0) {
                    final Vocabulary resources = Vocabulary.getResources(getLocale());
                    addXAxis(resources.getString(Vocabulary.Keys.SUBSAMPLING));
                    addYAxis(resources.getString(Vocabulary.Keys.COST_ESTIMATION));
                }
                addSeries(properties, xs, ys);
                properties.remove("Fill");
                properties.put("Paint", color);
                addSeries(properties, main.getX(), main.getY());
                if (ns == 0) {
                    reset();
                }
            }
        });
    }

    /**
     * Adds a plot calculated for the given mosaic. This method will create a default
     * profiler and invokes {@link #plotCostEstimation(String, MosaicProfiler)}.
     * <p>
     * This method can be invoked from any thread - it doesn't need to be the <cite>Swing</cite>
     * one. Since this method may take a while, it is recommanded to invoke it from a background
     * thread.
     *
     * @param  name The name to given to the plot, or {@code null} if none.
     * @param  tiles The mosaic for which to plot the estimated cost of loading images.
     * @throws IOException if an I/O operation was required and failed.
     */
    public void plotCostEstimation(final String name, final TileManager tiles) throws IOException {
        final MosaicProfiler profiler = new MosaicProfiler(tiles);
        profiler.setSubsamplingChangeAllowed(true);
        plotCostEstimation(name, profiler);
    }

    /**
     * Adds a plot for the mosaic created by the given builder.
     * <p>
     * This method can be invoked from any thread - it doesn't need to be the <cite>Swing</cite>
     * one. Since this method may take a while, it is recommanded to invoke it from a background
     * thread.
     *
     * @param  name The name to given to the plot, or {@code null} if none.
     * @param  builder The mosaic builder to use for creating a {@link TileManager}.
     * @throws IOException if an I/O operation was required and failed.
     */
    public void plotCostEstimation(final String name, final MosaicBuilder builder) throws IOException {
        plotCostEstimation(name, builder.createTileManager());
    }

    /**
     * Adds a plot for the mosaic created by the given builder. This method can be invoked from
     * any thread and returns immediately. The actual plot calculation is delegated to a background
     * thread.
     *
     * @param name The name to given to the plot, or {@code null} if none.
     * @param builder The mosaic builder to use for creating a {@link TileManager}.
     * @param delay How long to wait (in milliseconds) before to perform the calculation.
     *
     * @todo The delayed calculation is not yet implemented.
     */
    public void plotLater(final String name, final MosaicBuilder builder, final long delay) {
        try {
            plotCostEstimation(name, builder.createTileManager());
        } catch (Exception e) {
            /*
             * The IOException should not happen since if they were some I/O operation to perform,
             * they have usually been done before we reach this point. However a RuntimeException
             * may happen if some parameters given to the MosaicBuilder are incomplete or invalid.
             */
            ExceptionMonitor.show(this, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showOptionDialog(owner, this, title);
    }
}
