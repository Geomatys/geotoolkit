/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import org.geotoolkit.console.Action;
import org.geotoolkit.console.Option;
import org.geotoolkit.console.CommandLine;
import org.geotoolkit.coverage.io.CoverageStoreException;


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
     */
    @Option
    private Integer minSize;

    /**
     * The maximal size (in pixels) or random queries.
     */
    @Option
    private Integer maxSize;

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
     * Creates a new instance of {@code Main}.
     *
     * @param arguments The command-line arguments.
     */
    private Main(final String[] arguments) {
        super("java -jar geotk-stress.jar", arguments);
        minSize    = 100;
        maxSize    = 2000;
        numThreads = Runtime.getRuntime().availableProcessors() + 1;
        duration   = 10;
    }

    /**
     * Tests read and write operations on coverages. This action expects exactly one argument,
     * which is the image input to use (typically as the filename of a serialized Java Object).
     *
     * @throws CoverageStoreException If an error occurred while creating a coverage reader.
     */
    @Action(minimalArgumentCount=1, maximalArgumentCount=1)
    public void coverages() throws CoverageStoreException {
        final Object input = CoverageReadWriteStressor.createReaderInput(new File(arguments[0]));
        final StressorGroup stressors = new StressorGroup(duration * 1000, out);
        for (int i=numThreads; --i>=0;) {
            final CoverageReadWriteStressor stressor = new CoverageReadWriteStressor(input);
            int[] size = stressor.getMinimalGridSize();
            Arrays.fill(size, minSize);
            stressor.setMinimalGridSize(size);
            size = stressor.getMaximalGridSize();
            Arrays.fill(size, maxSize);
            stressor.setMaximalGridSize(size);
            stressors.add(stressor);
        }
        stressors.run();
    }

    /**
     * Creates a new instance of {@code Main} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        final Main console = new Main(arguments);
        console.run();
    }
}
