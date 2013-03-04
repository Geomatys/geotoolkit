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
package org.geotoolkit.gui.swing.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.text.NumberFormat;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import org.geotoolkit.math.Vector;
import org.geotoolkit.math.VectorPair;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.Plot2D;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicProfiler;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.logging.Logging;

import static org.apache.sis.util.ArgumentChecks.ensurePositive;


/**
 * A plot of the estimated efficiency of loading tiles using a given mosaic. Given a {@link TileManager},
 * this method computes an estimation of the efficiency of loading tiles at different subsampling levels.
 * The details of the calculation is documented in the {@link MosaicProfiler} class.
 * <p>
 * The calculations are performed by the {@link #plotEfficiency plotEfficiency} method,
 * which may require a long execution time. Consequently this method should be invoked from a
 * background thread rather than the <cite>Swing</cite> thread. As a convenience, the
 * {@link #plotLater plotLater} method is provided for delegating the calculation to a
 * background thread.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public class MosaicPerformanceGraph extends Plot2D implements Dialog {
    /**
     * Small tolerance factor for the comparison of floating point numbers.
     */
    private static final double EPS = 1E-6;

    /**
     * The seed to use for the random number generator.
     */
    private final long seed;

    /**
     * If {@code true}, automatically clears the plot before to add the result of a new computation.
     */
    private boolean clearBeforePlot = true;

    /**
     * Number of samplings to performs per subsampling.
     */
    private int imagesPerSubsampling = 100;

    /**
     * The worker which will compute the graph in a background thread, or {@code null} if
     * no worker is about to be executed. This field is used in order to allow changes to
     * the {@code Delayed} argument before the execution really start.
     */
    private transient volatile Worker worker;

    /**
     * The progress bar to inform of lengthly operation, or {@code null} if none.
     */
    private JProgressBar progressBar;

    /**
     * Creates a default graph.
     */
    public MosaicPerformanceGraph() {
        super(true, false);
        seed = System.currentTimeMillis();
        setPreferredSize(new Dimension(600, 400));
    }

    /**
     * Creates a profiler for the given mosaic. This method is invoked automatically
     * by {@link #plotEfficiency(String, TileManager)} when a new plot has been
     * requested. The default implementation creates a profiler with {@linkplain
     * MosaicProfiler#setSubsamplingChangeAllowed subsampling change allowed}.
     * Subclasses can overwrite this method for configuring the profiler in a different way.
     *
     * @param  tiles The mosaic to profile.
     * @return A profiler for the given mosaic.
     * @throws IOException if an I/O operation was required and failed.
     */
    protected MosaicProfiler createProfiler(final TileManager tiles) throws IOException {
        final MosaicProfiler profiler = new MosaicProfiler(tiles);
        profiler.setSubsamplingChangeAllowed(true);
        return profiler;
    }

    /**
     * Returns the number of images to be requested for each subsampling level.
     *
     * @return The current number of image loadings to be done or simulated per subsampling level.
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
        ensurePositive("n", n);
        final int old = imagesPerSubsampling;
        imagesPerSubsampling = n;
        firePropertyChange("imagesPerSubsampling", old, n);
    }

    /**
     * Returns the progress bar given to the last to {@link #setProgressBar(JProgressBar)}.
     * The default value is {@code null}, i.e. this widget do not provides any progress bar
     * by itself.
     *
     * @return The progress bar given to the last to {@code setProgressBar}.
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Sets the progress bar to inform of lengthly operation, or {@code null} if none.
     * The given progress bar is expected to accept a range of values from 0 to 100 inclusive.
     * If the progress bar accepts a wider range of values, only the [0 &hellip; 100] range
     * will be used.
     * <p>
     * The given progress bar is not displayed in this widget. It is caller
     * responsibility to provide a progress bar visible in his own widget.
     *
     * @param bar The progress bar, or {@code null} if none.
     */
    public void setProgressBar(final JProgressBar bar) {
        final JProgressBar old = progressBar;
        progressBar = bar;
        firePropertyChange("progressBar", old, bar);
    }

    /**
     * If {@code true}, any call to {@code plotEfficiency} will clear the previous plot
     * before to add the result of a new calculation. If {@code false}, then the result of
     * {@code plotEfficiency} will be a new series added to the existing ones.
     * <p>
     * The default value is {@code true}.
     *
     * @return Whatever the result of {@code plotEfficiency} will replace any previous plot.
     */
    public boolean getClearBeforePlot() {
        return clearBeforePlot;
    }

    /**
     * Sets whatever the result of {@code plotEfficiency} should replace any previous plot.
     *
     * @param clear Whatever the result of {@code plotEfficiency} should replace any previous plot.
     */
    public void setClearBeforePlot(final boolean clear) {
        final boolean old = clearBeforePlot;
        clearBeforePlot = clear;
        firePropertyChange("clearBeforePlot", old, clear);
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
     * Adds a plot calculated for the given mosaic. This method will run the profiler for every
     * uniform subsampling values ranging from the {@linkplain MosaicProfiler#getMinSubsampling
     * minimum} to the {@linkplain MosaicProfiler#getMaxSubsampling maximum} subsampling value,
     * inclusives.
     * <p>
     * This method can be invoked from any thread - it doesn't need to be the <cite>Swing</cite>
     * one. Since this method may take a while, it is recommended to invoke it from a background
     * thread.
     *
     * @param  name The name to given to the plot, or {@code null} if none.
     * @param  tiles The mosaic for which to plot the estimated cost of loading images.
     * @throws IOException if an I/O operation was required and failed.
     */
    public final void plotEfficiency(final String name, final TileManager tiles) throws IOException {
        /*
         * We do not allow the user to override this method (it is final) because it is hard to
         * make plotLater to invoke it, so the user could be confused to see his implementation
         * ignored despite what our javadoc said.  PlotLater needs to invoke the private method
         * below with an explicit Worker argument. We can't take the value of this.worker field
         * because it could have changed during the window of vulnerability between assignation
         * of this.worker and execution of plotEfficiency.  Furthermore we don't want to be
         * confused if a worker and someone else invoke plotEfficiency in same time.
         */
        plotEfficiency(name, tiles, null);
    }

    /**
     * Implementation of {@code plotEfficiency} callable from a worker. This method is
     * not public because we don't want to expose the worker in public API.
     *
     * @param  name The name to given to the plot, or {@code null} if none.
     * @param  tiles The mosaic for which to plot the estimated cost of loading images.
     * @param  worker The worker that invoked this method, or {@code null} if none.
     * @return The mosaic which has been profiled, or {@code null} if the operation has
     *         been canceled before completion.
     * @throws IOException if an I/O operation was required and failed.
     */
    private TileManager plotEfficiency(final String name, final TileManager tiles, final Worker worker)
            throws IOException
    {
        final MosaicProfiler profiler  = createProfiler(tiles);
        final Dimension minSubsampling = profiler.getMinSubsampling();
        final Dimension maxSubsampling = profiler.getMaxSubsampling();
        final int ms = Math.max(minSubsampling.width, minSubsampling.height);
        final int ns = Math.min(maxSubsampling.width, maxSubsampling.height) - ms + 1;
        profiler.setMaxSubsampling(ms);
        final float[] cost = new float[ns];
        final float[] high = new float[ns];
        final float[] low  = new float[ns];
        for (int i=0; i<ns; i++) {
            if (worker != null) {
                if (worker.isCancelled()) {
                    return null;
                }
                worker.progress(i*100 / ns);
            }
            profiler.setSeed(seed); // Use the same random values for each subsamplings.
            profiler.setMinSubsampling(i+ms);
            final Statistics stats = profiler.estimateEfficiency(imagesPerSubsampling);
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
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (getClearBeforePlot()) {
                    clear();
                }
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
                    addYAxis(resources.getString(Vocabulary.Keys.EFFICIENCY));
                }
                final Series series = addSeries(properties, xs, ys);
                properties.remove("Fill");
                properties.put("Paint", color);
                addSeries(properties, main.getX(), main.getY());
                if (ns == 0) {
                    final NumberGraduation grad = (NumberGraduation) getAxes(series)[1].getGraduation();
                    grad.setFormat(NumberFormat.getPercentInstance(grad.getLocale()));
                }
            }
        });
        return profiler.mosaic;
    }

    /**
     * Specifies a mosaic to be profiled in a background thread. An instance of this interface
     * can be given to the {@link MosaicPerformanceGraph#plotLater(String, Delayed, long)} method.
     * The methods defined in this interface will be called in a background thread some time after
     * {@code plotLater}. The workflow is as below:
     *
     * <ol>
     *   <li><p>An instance of {@code Delayed} is given to the {@code plotLater} method.</p></li>
     *   <li><p>A background thread will sleep the specified amount of time. If {@code plotLater}
     *       is invoked again while the background thread is sleeping, then the old {@code Delayed}
     *       instance is discarded and replaced by the new one.</p></li>
     *   <li><p>After the above delay, the {@link #getTileManager()} method is
     *       invoked in the background thread. The returned mosaic is given to
     *       {@link MosaicPerformanceGraph#plotEfficiency(String, TileManager)}.</p></li>
     *   <li><p>After the {@code plotEfficiency} method terminated, exactly one of the
     *       following methods is invoked in the <cite>Swing</cite> thread:
     *       <ul>
     *         <li>{@link #done(TileManager)} on success.</li>
     *         <li>{@link #failed(Throwable)} on failure.</li>
     *       </ul></p></li>
     * </ol>
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    public interface Delayed {
        /**
         * Returns the mosaic to profile. This method is invoked in a background
         * thread before the profiling begin.
         *
         * @return The mosaic as a tile manager.
         * @throws IOException if an I/O operation was required and failed.
         */
        TileManager getTileManager() throws IOException;

        /**
         * Invoked on the <cite>Swing</cite> thread after the profiling has been completed.
         * This is usually the instance returned by {@link #getTileManager()}.
         *
         * @param mosaic The mosaic which has been profiled.
         */
        void done(TileManager mosaic);

        /**
         * Invoked on the <cite>Swing</cite> thread if an exception occurred during the profiling.
         *
         * @param exception The exception which occurred.
         */
        void failed(Throwable exception);
    }

    /**
     * Adds a plot for the mosaic created by a given builder. This method can be invoked from any
     * thread and returns immediately. The actual plot calculation is delegated to a background
     * thread.
     *
     * @param name The name to given to the plot, or {@code null} if none.
     * @param delayed Provides the {@link TileManager} and the methods to callback on success or failure.
     * @param delay How long to wait (in milliseconds) before to perform the calculation.
     */
    public void plotLater(final String name, final Delayed delayed, final long delay) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                Worker worker = MosaicPerformanceGraph.this.worker;
                if (worker == null || !worker.schedule(name, delayed, delay)) {
                    worker = new Worker();
                    if (!worker.schedule(name, delayed, delay)) {
                        throw new AssertionError();
                    }
                    worker.execute();
                }
                MosaicPerformanceGraph.this.worker = worker;
                final JProgressBar progress = getProgressBar();
                if (progress != null) {
                    progress.setEnabled(true);
                    progress.setIndeterminate(true);
                }
            }
        });
    }

    /**
     * The worker which will computes the graph in background. An instance of this class will
     * be created everytime needed, but will wait an arbitrary amount of time before to begin
     * its job in case the builder configuration change.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private final class Worker extends SwingWorker<TileManager,Object> implements PropertyChangeListener {
        /**
         * The name of the plot to add.
         */
        private String name;

        /**
         * Provides the mosaic builder for which to plot the performance graph.
         */
        private Delayed delayed;

        /**
         * When to starts computation, in milliseconds since January 1st, 1970.
         */
        private long time;

        /**
         * {@code true} if this worker started its job.
         */
        private boolean running;

        /**
         * Creates a new worker.
         */
        Worker() {
            addPropertyChangeListener(this);
        }

        /**
         * Schedule a plot for the given builder. Returns {@code true} on success, or
         * {@code false} if an other instance of {@code Worker} needs to be created.
         */
        synchronized boolean schedule(final String name, final Delayed delayed, final long delay) {
            if (running) {
                cancel(false);
                return false;
            }
            this.name    = name;
            this.delayed = delayed;
            this.time    = delay + System.currentTimeMillis();
            return true;
        }

        /**
         * Waits for the delay, then plots the graph.
         */
        @Override
        protected TileManager doInBackground() throws IOException {
            final String name;
            final Delayed delayed;
            synchronized (this) {
                long delay;
                while ((delay = time - System.currentTimeMillis()) > 1) {
                    try {
                        wait(delay);
                    } catch (InterruptedException ex) {
                        // Ignore and go back to work.
                    }
                }
                name    = this.name;
                delayed = this.delayed;
                running = true;
            }
            return plotEfficiency(name, delayed.getTileManager(), this);
        }

        /**
         * Invoked by {@code plotEfficiency} for setting the progress. Defined here because
         * {@code setProgress} has protected access.
         *
         * @param p The progress as a value in the [0 ... 100] range.
         */
        public void progress(final int p) {
            if (!isDone()) {
                setProgress(p);
            }
        }

        /**
         * Invoked from the event dispatch thread after a call to {@link #progress}.
         * Performs the actual update of the progress bar.
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if (event.getPropertyName().equals("progress")) {
                final JProgressBar progress = getProgressBar();
                if (progress != null) {
                    progress.setIndeterminate(false);
                    progress.setValue(getProgress());
                }
            }
        }

        /**
         * Executed from the event dispatch thread. Discards the reference to the worker if it
         * was this instance. This is needed for letting {@link MosaicPerformanceGraph#plotLater}
         * know that it can set the state of the progress bar when a new plot is requested.
         */
        @Override
        protected void done() {
            if (worker == this) {
                worker = null;
                if (!isCancelled()) {
                    /*
                     * Note: get() below should not return null even if plotEfficiency(...)
                     * returned null, because the later call should occur only when the plot has
                     * been cancelled and we checked in the above line that this is not the case.
                     */
                    try {
                        delayed.done(get());
                    } catch (ExecutionException e) {
                        delayed.failed(e.getCause());
                    } catch (InterruptedException e) {
                        // Should never happen, since the task is completed.
                        Logging.unexpectedException(MosaicPerformanceGraph.class, "plotEfficiency", e);
                    }
                    final JProgressBar progress = getProgressBar();
                    if (progress != null) {
                        progress.setIndeterminate(false);
                        progress.setValue(100);
                        progress.setEnabled(false);
                    }
                }
            }
        }
    }

    /**
     * Forces the current values to be taken from the editable fields and set them as the
     * current values. The default implementation does nothing since there is no editable
     * fields in this widget.
     *
     * @since 3.12
     */
    @Override
    public void commitEdit() throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showDialog(owner, this, title);
    }
}
