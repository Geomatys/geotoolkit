/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.io.Writer;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.NumberFormat;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.console.ReferencingConsole;


/**
 * A console for running test scripts. Most of the work is already done by the subclass.
 * {@code ScriptRunner} mostly adds statistics about the tests executed. This class is
 * used by {@link ScriptTest}. It can also be run from the command line for executing all
 * files specified in argument.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final strictfp class ScriptRunner extends ReferencingConsole {
    /**
     * Number of tests run and passed. Used for displaying statistics.
     */
    private int testRun, testPassed;

    /**
     * The first error that occurred, or {@code null} if none.
     */
    Exception firstError;

    /**
     * Creates a new instance using the specified input stream.
     *
     * @param in The input stream.
     */
    public ScriptRunner(final LineNumberReader in) {
        super(in);
    }

    /**
     * Invoked automatically when the {@code target pt} instruction were executed.
     *
     * @throws TransformException if the source point can't be transformed, or a mistmatch is found.
     * @throws MismatchedDimensionException if the transformed source point doesn't have the
     *         expected dimension.
     */
    @Override
    protected void test() throws TransformException, MismatchedDimensionException {
        testRun++;
        super.test();
        testPassed++;
    }

    /**
     * Invoked when an error occurred.
     */
    @Override
    protected void reportError(final Exception exception) {
        super.reportError(exception);
        if (firstError == null) {
            firstError = exception;
        }
    }

    /**
     * Prints the number of tests executed, the number of errors and the success rate.
     */
    final void printStatistics(final Writer out) throws IOException {
        NumberFormat f = NumberFormat.getNumberInstance();
        final TableWriter table = new TableWriter(out, 1);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        table.write("Tests:");
        table.nextColumn();
        table.setAlignment(TableWriter.ALIGN_RIGHT);
        table.write(f.format(testRun));
        table.nextLine();
        table.setAlignment(TableWriter.ALIGN_LEFT);
        table.write("Errors:");
        table.nextColumn();
        table.setAlignment(TableWriter.ALIGN_RIGHT);
        table.write(f.format(testRun - testPassed));
        table.nextLine();
        if (testRun != 0) {
            f = NumberFormat.getPercentInstance();
            table.setAlignment(TableWriter.ALIGN_LEFT);
            table.write("Success rate:");
            table.nextColumn();
            table.setAlignment(TableWriter.ALIGN_RIGHT);
            table.write(f.format((double)testPassed / (double)testRun));
            table.nextLine();
        }
        table.writeHorizontalSeparator();
        table.flush();
    }

    /**
     * Run all tests scripts specified on the command line.
     *
     * @param  args The list of script files to execute.
     * @throws IOException If an error occurred while executing the script.
     */
    public static void main(final String[] args) throws IOException {
        final String lineSeparator = System.lineSeparator();
        for (int i=0; i<args.length; i++) {
            final String filename = args[i];
            try (LineNumberReader in = new LineNumberReader(new FileReader(filename))) {
                final ScriptRunner test = new ScriptRunner(in);
                test.out.write("Running \"");
                test.out.write(filename);
                test.out.write('"');
                test.out.write(lineSeparator);
                test.out.flush();
                test.run();
                test.printStatistics(test.out);
                test.out.write(lineSeparator);
                test.out.flush();
            }
        }
    }
}
