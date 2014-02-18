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
package org.geotoolkit.referencing.operation.transform;

import java.io.*;
import java.util.StringTokenizer;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.ContentFormatException;
import static org.geotoolkit.referencing.operation.transform.EarthGravitationalModel.DEFAULT_ORDER;
import static org.geotoolkit.referencing.operation.transform.EarthGravitationalModel.locatingArray;


/**
 * Reads the ASCII files provided by the Earth-Info web site, and eventually write
 * it as a binary file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class Compiler {
    /**
     * The geopotential coefficients read from the ASCII file.
     * Those arrays are filled by the {@link #load} method.
     */
    final double[] cnmGeopCoef, snmGeopCoef;

    /**
     * Creates a new compiler.
     */
    Compiler() {
        final int geopCoefLength = locatingArray(DEFAULT_ORDER + 1);
        cnmGeopCoef = new double[geopCoefLength];
        snmGeopCoef = new double[geopCoefLength];
    }

    /**
     * Loads the coefficients from the specified ASCII file and initialize the internal
     * <cite>clenshaw arrays</cite>.
     *
     * @param  filename The filename, relative to this class directory.
     * @throws IOException if the file can't be read or has an invalid content.
     */
    final void load(final String filename) throws IOException {
        final InputStream stream = Compiler.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new FileNotFoundException(filename);
        }
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(stream, "ISO-8859-1"))) {
            String line;
            while ((line = in.readLine()) != null) {
                final StringTokenizer tokens = new StringTokenizer(line);
                try {
                    /*
                     * Note: we use 'parseShort' instead of 'parseInt' as an easy way to ensure that
                     *       the values are in some reasonable range. The range is typically [0..180].
                     *       We don't check that, but at least 'parseShort' disallows values greater
                     *       than 32767. Additional note: we real all lines in all cases even if we
                     *       discard some of them, in order to check the file format.
                     */
                    final int n = Short.parseShort (tokens.nextToken());
                    final int m = Short.parseShort (tokens.nextToken());
                    if (n <= 0 || m < 0 || m > n) {
                        throw new ContentFormatException("n="+ n +" and m=" + m);
                    }
                    final double cbar = Double.parseDouble(tokens.nextToken());
                    final double sbar = Double.parseDouble(tokens.nextToken());
                    final int ll = locatingArray(n) + m;
                    cnmGeopCoef[ll] = cbar;
                    snmGeopCoef[ll] = sbar;
                } catch (RuntimeException cause) {
                    /*
                     * Catch the following exceptions:
                     *   - NoSuchElementException      if a line has too few numbers.
                     *   - NumberFormatException       if a number can't be parsed.
                     *   - IndexOutOfBoundsException   if 'n' or 'm' values are illegal.
                     */
                    throw new IOException(Errors.format(Errors.Keys.ILLEGAL_LINE_IN_FILE_2,
                            filename, in.getLineNumber()), cause);
                }
            }
        }
    }

    /**
     * Writes the data in a binary form. The file must not exist.
     *
     * @param  file The destination file, relative to the current directory.
     * @throws IOException if the file can't be writin.
     */
    private void save(final File file) throws IOException {
        if (file.exists()) {
            throw new IOException("File already exists.");
        }
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            for (int i=0; i<cnmGeopCoef.length; i++) {
                out.writeDouble(cnmGeopCoef[i]);
                out.writeDouble(snmGeopCoef[i]);
            }
        }
    }

    /**
     * Reads the coefficients from the ASCII file and writes them to a binary file.
     * Runs this command with no argument for a summary of the expected arguments.
     *
     * @param  args The command-line arguments.
     * @throws IOException If an error occurred while reading the ASCII file or writing the binary file.
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Reads the coefficients from the ASCII file and writes them to a binary file.");
            System.out.println("This command tool expects two arguments:");
            System.out.println("  1) The input filename relative to the Compiler class (example: EGM180.nor)");
            System.out.println("  2) The output file relative to current directory.");
            return;
        }
        final Compiler compiler = new Compiler();
        compiler.load(args[0]);
        compiler.save(new File(args[1]));
    }
}
