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
package org.geotoolkit.test.stress;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.Setup;
import org.geotoolkit.console.Action;
import org.geotoolkit.console.Option;
import org.geotoolkit.console.CommandLine;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.referencing.CRS;


/**
 * Runs the stressor from the command-line.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
public final class Main extends CommandLine {
    /**
     * The minimal size (in pixels) or random queries.
     * The default value is 100 pixels.
     */
    @Option
    private Integer minSize;

    /**
     * The maximal size (in pixels) or random queries.
     * The default value is 2000 pixels.
     */
    @Option
    private Integer maxSize;

    /**
     * The maximal scale of random queries, relative to the source data. For example a value of 10
     * means that the request will ask for a resolution at most 10 time larger than the resolution
     * of source data. The default is computed in such a way that the requested images at the
     * largest resolution are not smaller than the minimal grid size.
     */
    @Option
    private Double maxScale;

    /**
     * The number of threads to create for running the tests.
     * The default value is the number of processors plus 1.
     */
    @Option
    private Integer numThreads;

    /**
     * The test duration, in seconds.
     */
    @Option
    private Integer duration;

    /**
     * If specified, write the request result in an image of the given format and read it back.
     * The format name can optionally be concatenated with {@code "(native)"} or {@code "(standard)"}
     * suffix for forcing explicitely the native (from JAI-Image I/O library) or standard codec.
     */
    @Option
    private String outputFormat;

    /**
     * The CRS of output images, or {@code null} if none.
     */
    @Option
    private String outputCRS;

    /**
     * The seed to use for random number generators. This should be set only when desirable
     * to run the stressor many time with exactly the same requests. Note that different
     * threads will still use different requests.
     */
    @Option
    private Long randomSeed;

    /**
     * If {@code true}, reports more information during the stress.
     */
    @Option
    private boolean verbose;

    /**
     * {@code true} if the results shall be shown in windows.
     */
    @Option
    private boolean view;

    /**
     * Creates a new instance of {@code Main}.
     *
     * @param arguments The command-line arguments.
     */
    private Main(final String[] arguments) {
        super("java -jar geotk-stress.jar", arguments);
        numThreads = Runtime.getRuntime().availableProcessors() + 1;
        duration   = 10;
    }

    /**
     * Initializes the given stressors before to run them.
     */
    private void init(final StressorGroup<?> stressors) {
        if (randomSeed != null) {
            final Random random = new Random(randomSeed);
            for (final Stressor stressor : stressors.getStressors()) {
                stressor.random.setSeed(random.nextLong());
            }
        }
        if (verbose) {
            for (final Stressor stressor : stressors.getStressors()) {
                stressor.setLogLevel(Level.INFO);
            }
        }
    }

    /**
     * Tests read and write operations on coverages. This action expects exactly one argument,
     * which is the image input to use (typically as the filename of a serialized Java Object).
     *
     * @throws CoverageStoreException If an error occurred while creating a coverage reader.
     * @throws FactoryException If the output CRS is unknown.
     */
    @Action(minimalArgumentCount=1, maximalArgumentCount=1)
    public void coverages() throws CoverageStoreException, FactoryException {
        CoordinateReferenceSystem crs = null;
        if (outputCRS != null) {
            crs = CRS.decode(outputCRS);
        }
        final Object input = CoverageReadWriteStressor.createReaderInput(new File(arguments[0]));
        final StressorGroup<CoverageReadWriteStressor> stressors =
                new StressorGroup<>(duration * 1000L, out, err);
        /*
         * Creates a new stressor for each thread, and configures it to the minimal and
         * maximal size, maximal scale and locale given as parameters on the command line.
         */
        for (int i=numThreads; --i>=0;) {
            final CoverageReadWriteStressor stressor = new CoverageReadWriteStressor(input);
            if (minSize != null) {
                final int[] size = stressor.getMinimalGridSize();
                Arrays.fill(size, minSize);
                stressor.setMinimalGridSize(size);
            }
            if (maxSize != null) {
                final int[] size = stressor.getMaximalGridSize();
                Arrays.fill(size, maxSize);
                stressor.setMaximalGridSize(size);
            }
            if (maxScale != null) {
                stressor.setMaximumScale(maxScale);
            }
            stressor.outputCRS = crs;
            stressor.setLocale(locale);
            stressors.add(stressor);
        }
        /*
         * Creates the frame only after all stressors has been created,
         * since the frame layout needs to know the number of stressors.
         */
        if (view) {
            stressors.createFrame();
        }
        /*
         * Set the codec preferences only after the frame has been created, since creating
         * a java.awt.Frame have the side-effect of regitering a standard TIFF image reader.
         */
        Registry.setDefaultCodecPreferences();
        /*
         * Process the (native) or (standard) suffix if any (which may change the codec
         * preference settings performed above), and set the format name of each stressor.
         */
        if (outputFormat != null) {
            final String format = CoverageReadWriteStressor.processFormatName(outputFormat);
            for (final CoverageReadWriteStressor stressor : stressors.getStressors()) {
                stressor.outputFormat = format;
            }
        }
        init(stressors);
        stressors.run();
    }

    /**
     * Creates a new instance of {@code Main} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        Setup.initialize(null);
        final Main console = new Main(arguments);
        console.run();
    }
}
