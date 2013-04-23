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
package org.geotoolkit.console;

import java.io.*;
import java.util.*;

import org.opengis.util.FactoryException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.wkt.Symbols;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.crs.AbstractCRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.io.wkt.Colors;
import org.geotoolkit.io.X364;


/**
 * A console for executing CRS operations from the command line. This class understands
 * the following set of instructions:
 * <p>
 * <table border="1" cellpadding="6">
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code set} <var>name</var> {@code =} <var>wkt</var></td><td>
 *   Set the specified <var>name</var> as a shortcut for the specified (<var>wkt</var>) (Well
 *   Know Text). This WKT can contains other shortcuts defined previously.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code load} <var>filename</var></td><td>
 *   Load the specified file, which is expected to be <var>name</var>=<var>key</var> pairs.
 *   The effect is the same as if the above {@code set} command was invoked for every lines
 *   in the given file.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code transform = } <var>wkt</var></td><td>
 *   Set explicitly a {@linkplain MathTransform math transform} to use for coordinate
 *   transformations. This instruction is a more direct alternative to the usage of
 *   {@code source crs} and {@code target crs} instruction.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code source crs = } <var>wkt</var></td><td>
 *   Set the source {@linkplain CoordinateReferenceSystem coordinate reference system} to the
 *   specified object. This object can be specified as a Well Know Text (<var>wkt</var>) or as
 *   a shortcut previously set.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code target crs = } <var>wkt</var></td><td>
 *   Set the target {@linkplain CoordinateReferenceSystem coordinate reference system} to the
 *   specified object. This object can be specified as a Well Know Text (<var>wkt</var>) or as
 *   a shortcut previously set. Once both source and target CRS are specified a
 *   {@linkplain MathTransform math transform} from source to target CRS is automatically inferred.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code source pt = } <var>coord</var></td><td>
 *   Transforms the specified coordinates from source CRS to target CRS and prints the result.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code target pt = } <var>coord</var></td><td>
 *   Inverse transforms the specified coordinates from target CRS to source CRS
 *   and prints the result.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code tolerance forward = } <var>vector</var></td><td>
 *   Set the maximum difference between the transformed source point and the target point. Once this
 *   value is set, every occurrence of the {@code target pt} instruction will trig this comparison.
 *   If a greater difference is found, an exception is thrown or a message is printed to the error
 *   stream.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code tolerance inverse = } <var>vector</var></td><td>
 *   Same as {@code tolerance forward}, but performs the test on the result of the inverse transform.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code print set}</td><td>
 *   Prints the set of shortcuts defined in previous calls to {@code SET} instruction.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code print crs}</td><td>
 *   Prints the source and target {@linkplain CoordinateReferenceSystem coordinate reference system}
 *   as Well Know Text (wkt).</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code print mt}</td><td>
 *   Prints the {@linkplain MathTransform math transform} and its inverse as Well Know Text (wkt).</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code print pts}</td><td>
 *   Prints the source and target points, their transformed points, and the distance between
 *   them.</td></tr>
 *
 *   <tr><td nowrap valign="top" bgcolor="#EEEEFF">{@code exit}</td><td>
 *   Quit the console.</td></tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class ReferencingConsole extends InteractiveConsole {
    /**
     * The coordinate operation factory to use.
     */
    private final CoordinateOperationFactory factory = FactoryFinder.getCoordinateOperationFactory(null);

    /**
     * The object to use for parsing and formatting WKT.
     */
    private final WKTFormat parser;

    /**
     * The source and target CRS, or {@code null} if not yet determined.
     */
    private CoordinateReferenceSystem sourceCRS, targetCRS;

    /**
     * Source and target coordinate points, or {@code null} if not yet determined.
     */
    private DirectPosition sourcePosition, targetPosition;

    /**
     * The math transform, or {@code null} if not yet determined.
     */
    private MathTransform transform;

    /**
     * The tolerance value. If non-null, the difference between the computed and the specified
     * target point will be compared against this tolerance threshold. If it is greater, a message
     * will be printed.
     */
    private double[] tolerance, toleranceInverse;

    /**
     * Creates a new instance using the console if available,
     * or the parameters from the command line otherwise.
     */
    ReferencingConsole(final ReferencingCommands commands) {
        super(commands);
        final WKTFormat format = new WKTFormat();
        final String authority = commands.authority; // NOSONAR: field is initialized by reflection.
        if (authority != null) {
            format.setAuthority(Citations.fromName(authority));
        }
        if (Boolean.TRUE.equals(commands.colors)) {
            format.setColors(Colors.DEFAULT);
        }
        format.setIndentation(commands.indent);
        parser = format;
        initialize();
    }

    /**
     * Creates a new instance using the specified input stream.
     *
     * @param in The input stream.
     */
    protected ReferencingConsole(final LineNumberReader in) {
        super(in);
        parser = new WKTFormat();
        initialize();
    }

    /**
     * Completes the initialization of this instance.
     */
    private void initialize() {
        super.setPrompt("geotk-ct \u25B6 ");
        final Symbols symbols = parser.getSymbols();
        super.setSymbols(symbols.getOpeningBrackets(),
                         symbols.getClosingBrackets(),
                         symbols.getQuote());
    }

    /**
     * Loads all definitions from the specified stream. Definitions are key-value pairs in the form
     * "{@code name = wkt}" (without the "{@code set}" instruction). The result is the same than
     * invoking the "{@code set}" instruction for each line in the specified stream. This method
     * can be used for loading predefined objects like the database used by
     * {@link org.geotoolkit.referencing.factory.PropertyAuthorityFactory}.
     *
     * @param  in The input stream.
     * @throws IOException if an input operation failed.
     */
    private void loadDefinitions(final BufferedReader in) throws IOException {
        final Map<String,String> definitions = parser.definitions();
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                continue;
            }
            String name=line, value=null;
            final int i = line.indexOf('=');
            if (i >= 0) {
                name  = line.substring(0,i).trim();
                value = line.substring(i+1).trim();
            }
            try {
                definitions.put(name, value);
            } catch (IllegalArgumentException e) {
                throw new ContentFormatException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Prints to the {@linkplain #out output stream} a table of all definitions.
     */
    private void printDefinitions() {
        try {
            parser.printDefinitions(out);
        } catch (IOException e) {
            // Should never happen since we are writing to a PrintWriter.
            throw new IOError(e);
        }
    }

    /**
     * Updates the internal state after a change, before to apply transformation.
     * The most important change is to update the math transform, if needed.
     */
    private void update() throws FactoryException {
        if (transform==null && sourceCRS!=null && targetCRS!=null) {
            transform = factory.createOperation(sourceCRS, targetCRS).getMathTransform();
        }
    }

    /**
     * Executes the given instruction.
     *
     * @param  instruction The instruction to execute.
     * @throws Exception if the instruction failed.
     */
    @Override
    protected void execute(final String instruction) throws Exception {
        final String command, value;
        int i = instruction.indexOf('=');
        if (i >= 0) {
            command = instruction.substring(0,i).trim();
            // We need to compute the index of the first non-white char; it will be needed later.
            final int length = instruction.length();
            while (++i<length && Character.isWhitespace(instruction.charAt(i)));
            value = instruction.substring(i).trim();
        } else {
            command = instruction;
            value   = null;
        }
        final StringTokenizer keywords = new StringTokenizer(command);
        if (keywords.hasMoreTokens()) {
            final String key0 = keywords.nextToken();
            if (!keywords.hasMoreTokens()) {
                // -------------------------------
                //   transform = <the transform>
                // -------------------------------
                if (key0.equalsIgnoreCase("transform")) {
                    transform = parser.parse(instruction, i, MathTransform.class);
                    sourceCRS = null;
                    targetCRS = null;
                    return;
                }
            } else {
                final String key1 = keywords.nextToken();
                if (!keywords.hasMoreTokens()) {
                    // -------------------------------
                    //   print set|crs|mt|points
                    // -------------------------------
                    if (key0.equalsIgnoreCase("print")) {
                        if (value != null) {
                            throw unexpectedArgument(Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_1, "print");
                        }
                        if (key1.equalsIgnoreCase("set")) {
                            printDefinitions();
                            return;
                        }
                        if (key1.equalsIgnoreCase("crs")) {
                            printCRS();
                            return;
                        }
                        if (key1.equalsIgnoreCase("mt")) {
                            printTransforms();
                            return;
                        }
                        if (key1.equalsIgnoreCase("pts")) {
                            printPts();
                            return;
                        }
                        throw unexpectedArgument(Errors.Keys.ILLEGAL_INSTRUCTION_1, key1);
                    }
                    // -------------------------------
                    //   load <filename>
                    // -------------------------------
                    if (key0.equalsIgnoreCase("load")) {
                        if (value != null) {
                            throw unexpectedArgument(Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_1, "load");
                        }
                        final BufferedReader in = new BufferedReader(new FileReader(key1));
                        try {
                            loadDefinitions(in);
                        } finally {
                            in.close();
                        }
                        return;
                    }
                    // -------------------------------
                    //   set <name> = <wkt>
                    // -------------------------------
                    if (key0.equalsIgnoreCase("set")) {
                        parser.definitions().put(key1, value);
                        return;
                    }
                    // -------------------------------
                    //   test tolerance = <vector>
                    // -------------------------------
                    if (key0.equalsIgnoreCase("tolerance")) {
                        if (key1.equalsIgnoreCase("forward")) {
                            tolerance = parseVector(value, true);
                            return;
                        }
                        if (key1.equalsIgnoreCase("inverse")) {
                            toleranceInverse = parseVector(value, true);
                            return;
                        }
                        throw unexpectedArgument(Errors.Keys.ILLEGAL_INSTRUCTION_1, key1);
                    }
                    // -------------------------------
                    //   source|target crs = <wkt>
                    // -------------------------------
                    if (key1.equalsIgnoreCase("crs")) {
                        if (key0.equalsIgnoreCase("source")) {
                            sourceCRS = parser.parse(instruction, i, CoordinateReferenceSystem.class);
                            transform = null;
                            return;
                        }
                        if (key0.equalsIgnoreCase("target")) {
                            targetCRS = parser.parse(instruction, i, CoordinateReferenceSystem.class);
                            transform = null;
                            return;
                        }
                    }
                    // -------------------------------
                    //   source|target pt = <coords>
                    // -------------------------------
                    if (key1.equalsIgnoreCase("pt")) {
                        if (key0.equalsIgnoreCase("source")) {
                            sourcePosition = new GeneralDirectPosition(parseVector(value, false));
                            return;
                        }
                        if (key0.equalsIgnoreCase("target")) {
                            targetPosition = new GeneralDirectPosition(parseVector(value, false));
                            if ((tolerance!=null || toleranceInverse!=null) && sourcePosition!=null) {
                                update();
                                if (transform != null) {
                                    test();
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
        super.execute(instruction);
    }

    /**
     * Prints the given text, using X3.64 bold sequence if colors are enabled.
     */
    private void header(final Writer out, final String text) throws IOException {
        if (colors) out.write(X364.BOLD.sequence());
        out.write(text);
        if (colors) out.write(X364.NORMAL.sequence());
    }

    /**
     * Executes the "{@code print crs}" instruction.
     */
    private void printCRS() throws FactoryException, IOException {
        if (sourceCRS != null || targetCRS != null) {
            final Vocabulary resources = Vocabulary.getResources(locale);
            final TableWriter table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
            table.setMultiLinesCells(true);
            table.writeHorizontalSeparator();
            if (sourceCRS != null) {
                header(table, resources.getString(Vocabulary.Keys.SOURCE_CRS));
                table.writeHorizontalSeparator();
                table.write(parser.format(sourceCRS));
                if (targetCRS != null) {
                    table.nextLine();
                    table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
                }
            }
            if (targetCRS != null) {
                header(table, resources.getString(Vocabulary.Keys.TARGET_CRS));
                table.writeHorizontalSeparator();
                table.write(parser.format(targetCRS));
            }
            table.writeHorizontalSeparator();
            table.flush();
        }
    }

    /**
     * Formats the math transform and its inverse, if any.
     */
    private void printTransforms() throws FactoryException, IOException {
        update();
        if (transform != null) {
            final Vocabulary resources = Vocabulary.getResources(locale);
            final TableWriter table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
            table.setMultiLinesCells(true);
            table.writeHorizontalSeparator();
            header(table, resources.getString(Vocabulary.Keys.MATH_TRANSFORM));
            table.nextColumn();
            header(table, resources.getString(Vocabulary.Keys.INVERSE_TRANSFORM));
            table.nextLine();
            table.writeHorizontalSeparator();
            table.write(parser.format(transform));
            table.nextColumn();
            try {
                table.write(parser.format(transform.inverse()));
            } catch (NoninvertibleTransformException exception) {
                table.write(exception.getLocalizedMessage());
            }
            table.writeHorizontalSeparator();
            table.flush();
        }
    }

    /**
     * Prints the source and target point, and their transforms.
     *
     * @throws FactoryException if the transform can't be computed.
     * @throws TransformException if a transform failed.
     * @throws IOException if an error occurred while writing to the output stream.
     */
    private void printPts() throws FactoryException, TransformException, IOException {
        update();
        DirectPosition transformedSource = null;
        DirectPosition transformedTarget = null;
        String         targetException   = null;
        if (transform != null) {
            if (sourcePosition != null) {
                transformedSource = transform.transform(sourcePosition, null);
            }
            if (targetPosition != null) try {
                transformedTarget = transform.inverse().transform(targetPosition, null);
            } catch (NoninvertibleTransformException exception) {
                targetException = exception.getLocalizedMessage();
                if (sourcePosition != null) {
                    final GeneralDirectPosition p;
                    transformedTarget = p = new GeneralDirectPosition(sourcePosition.getDimension());
                    Arrays.fill(p.ordinates, Double.NaN);
                }
            }
        }
        final Locale locale = null;
        final Vocabulary resources = Vocabulary.getResources(locale);
        final TableWriter table = new TableWriter(out, 0);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        table.setAlignment(TableWriter.ALIGN_RIGHT);
        if (sourcePosition != null) {
            table.write(resources.getLabel(Vocabulary.Keys.SOURCE_POINT));
            print(sourcePosition,    table);
            print(transformedSource, table);
            table.nextLine();
        }
        if (targetPosition != null) {
            table.write(resources.getLabel(Vocabulary.Keys.TARGET_POINT));
            print(transformedTarget, table);
            print(targetPosition,    table);
            table.nextLine();
        }
        if (sourceCRS!=null && targetCRS!=null) {
            table.write(resources.getLabel(Vocabulary.Keys.DISTANCE));
            printDistance(sourceCRS, sourcePosition, transformedTarget, table);
            printDistance(targetCRS, targetPosition, transformedSource, table);
            table.nextLine();
        }
        table.writeHorizontalSeparator();
        table.flush();
        if (targetException != null) {
            out.println(targetException);
        }
    }

    /**
     * Prints the specified point to the specified table.
     * This helper method is for use by {@link #printPts}.
     *
     * @param  point The point to print, or {@code null} if none.
     * @throws IOException if an error occurred while writing to the output stream.
     */
    private void print(final DirectPosition point, final TableWriter table) throws IOException {
        if (point != null) {
            table.nextColumn();
            table.write("  (");
            final double[] coords = point.getCoordinate();
            for (int i=0; i<coords.length; i++) {
                if (i != 0) {
                    table.write(numberSeparator);
                    table.write(' ');
                }
                table.nextColumn();
                table.write(numberFormat.format(coords[i]));
            }
            table.write(')');
        }
    }

    /**
     * Prints the distance between two points using the specified CRS.
     */
    private void printDistance(final CoordinateReferenceSystem crs,
                               final DirectPosition position1,
                               final DirectPosition position2,
                               final TableWriter table)
            throws IOException
    {
        if (position1 == null) {
            // Note: 'position2' is checked below, *after* blank columns insertion.
            return;
        }
        for (int i=crs.getCoordinateSystem().getDimension(); --i>=0;) {
            table.nextColumn();
        }
        if (position2 != null) {
            if (crs instanceof AbstractCRS) try {
                final Measure distance;
                distance = ((AbstractCRS) crs).distance(position1.getCoordinate(),
                                                        position2.getCoordinate());
                table.setAlignment(TableWriter.ALIGN_RIGHT);
                table.write(numberFormat.format(distance.doubleValue()));
                table.write("  ");
                table.nextColumn();
                table.write(String.valueOf(distance.getUnit()));
                table.setAlignment(TableWriter.ALIGN_LEFT);
                return;
            } catch (UnsupportedOperationException ignore) {
                /*
                 * Underlying CRS do not supports distance computation.
                 * Left the column blank.
                 */
            }
        }
        table.nextColumn();
    }

    /**
     * Invoked automatically when the {@code target pt} instruction was executed and a
     * {@code test tolerance} was previously set. The default implementation compares
     * the transformed source point with the expected target point. If a mismatch greater
     * than the tolerance error is found, an exception is thrown. Subclasses may overrides
     * this method in order to performs more tests.
     *
     * @throws TransformException if the source point can't be transformed, or a mistmatch is found.
     * @throws MismatchedDimensionException if the transformed source point doesn't have the
     *         expected dimension.
     */
    protected void test() throws TransformException, MismatchedDimensionException {
        boolean inverse = false;
        do {
            final double[] tolerance = (inverse) ? toleranceInverse : this.tolerance;
            if (tolerance != null) {
                final CoordinateReferenceSystem crs;
                final DirectPosition source, target;
                MathTransform transform = this.transform;
                if (!inverse) {
                    crs    = targetCRS;
                    source = sourcePosition;
                    target = targetPosition;
                } else {
                    crs    = sourceCRS;
                    source = targetPosition;
                    target = sourcePosition;
                    transform = transform.inverse();
                }
                /*
                 * Get the coordinate system, which will be used in order to detect when to tolerate
                 * warp around (mostly 360° longitude rotations). If the CRS is unknown, we will try
                 * to infer the coordinate system from the math transform.
                 */
                CoordinateSystem cs = (crs != null) ? crs.getCoordinateSystem() : null;
                if (cs == null && transform instanceof AbstractMathTransform) {
                    final String name = ((AbstractMathTransform) transform).getName();
                    if (name != null && (name.contains("Molodensky") || name.contains("Molodenski"))) {
                        cs = DefaultEllipsoidalCS.GEODETIC_3D;
                    }
                }
                /*
                 * Check the dimensions of actual and expected coordinate, which most be the same.
                 */
                final DirectPosition transformed = transform.transform(source, null);
                final int actualDim   = transformed.getDimension();
                final int expectedDim = target.getDimension();
                if (actualDim != expectedDim) {
                    throw new MismatchedDimensionException(Errors.format(
                            Errors.Keys.MISMATCHED_DIMENSION_2, actualDim, expectedDim));
                }
                /*
                 * Check the ordinate values.
                 */
                for (int i=0; i<actualDim; i++) {
                    final double expected = target.getOrdinate(i);
                    final double actual = transformed.getOrdinate(i);
                    double delta = Math.abs(actual - expected);
                    if (cs != null && i < cs.getDimension()) {
                        final CoordinateSystemAxis axis = cs.getAxis(i);
                        if (RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
                            final double span = axis.getMaximumValue() - axis.getMinimumValue();
                            if ((delta %= span) > 0.5*span) {
                                delta = span - delta;
                            }
                        }
                    }
                    if (!(delta <= tolerance[Math.min(i, tolerance.length-1)])) { // Use '!' for catching NaN.
                        throw new TransformException(Errors.format(Errors.Keys.UNEXPECTED_TRANSFORM_RESULT_5,
                                new Number[] {expected, actual, delta, i, inverse ? 1 : 0}));
                    }
                }
            }
        } while ((inverse = !inverse) == true);
    }
}
