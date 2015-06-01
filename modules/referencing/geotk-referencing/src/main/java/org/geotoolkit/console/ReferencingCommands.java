/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.*;
import org.opengis.referencing.*;
import org.opengis.referencing.operation.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.FactoryDependencies;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.apache.sis.parameter.ParameterFormat;


/**
 * Describe the given referencing object.  This tool can display in Well Known Text
 * format a Coordinate Reference System identified by a given code  (typically, but
 * not limited to, EPSG namespace). It can also describe the parameters expected by
 * a given Operation Method, list the available authority codes, <i>etc.</i>
 * The syntax is:
 *
 * {@preformat text
 *     java -jar geotk-referencing <options> <action> <arguments...>
 * }
 *
 * The following actions are recognized by this class, in addition of the
 * {@linkplain CommandLine actions recognized by the super-class}:
 * <p>
 * <table border="1" cellpadding="3">
 *   <tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code bursawolfs}</b></td>
 *     <td>List the Bursa-Wolf parameters for the specified CRS ou datum objects. For some
 *         transformations, there is more than one set of Bursa-Wolf parameters available.
 *         The standard <cite>Well Known Text</cite> format prints only what look like the
 *         "main" one. This action displays all Bursa-Wolf parameters in a table for a given
 *         object.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code console}</b></td>
 *     <td>Launch the {@linkplain ReferencingConsole referencing console}
 *         for interactive transformation of coordinate points.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code factories}</b></td>
 *     <td>Print all {@linkplain AuthorityFactory authority factories} and their
 *         dependencies as a tree.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code list}</b> <var>type</var></td>
 *     <td>List available objects or operations of the given type. The type must be one of
 *         {@code "factories"}, {@code "codes"}, {@code "operations"}, {@code "conversions"}
 *          or {@code "projections"}.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code operations}</b> <var>codes...</var></td>
 *     <td>Print all available coordinate operations between a pair of CRS. This action
 *         prints only the operations explicitly defined in a database like EPSG. There
 *         is sometime many such operations, and sometime none (in which case this option
 *         prints nothing - it doesn't try to find an operation by itself).</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code parameters}</b> <var>names...</var></td>
 *     <td>List the parameters of the given operations. For example the name can be
 *         {@code "Affine"}, {@code "EPSG:9624"} or just {@code "9624"} for the affine
 *         transform method.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code reformat}</b></td>
 *     <td>Read WKT from the standard input stream and reformat them to
 *         the standard output stream.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code test}</b> <var>type</var></td>
 *     <td>Perform tests using factories found on the classpath. The type must be one of
 *         {@code "creates"}, {@code "duplicates"} or {@code "all"}.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code transform}</b> <var>codes...</var></td>
 *     <td>Print the preferred math transform between a pair of CRS. At the opposite of
 *         the {@code "operations"} action, this action picks up only one operation (usually
 *         the most accurate one), inferring it if none were explicitly specified in the
 *         database.</td>
 *   </tr>
 * </table>
 * <p>
 * The following options are recognized by this class, in addition of the
 * options recognized by the super-class:
 * <p>
 * <table border="1" cellpadding="3">
 *   <tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --authority}</b>=<var>name</var></td>
 *     <td>Use the projection and parameter names defined by the given authority when formatting
 *         WKT. Also use the authority name‐space as the default one when no namespace is explicitly
 *         given in object codes. The authority name can be any of the authorities listed by the
 *         {@code list authorities} option. If this option is not specified, then the default is
 *         all factories.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --forcexy}</b></td>
 *     <td>Force the X axis (or longitude) to appear before the Y axis (or latitude).</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --ident}</b>=<var>type</var></td>
 *     <td>Set the indentation to use for WKT formatting.</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
public class ReferencingCommands extends CommandLine {
    /**
     * The hints for the factory to fetch. Null for now, but may be different in a future version.
     */
    private static final Hints HINTS = null;

    /**
     * The authority to use for WKT formatting, or the namespace for referencing object codes.
     */
    @Option(examples={"OGC", "EPSG", "ESRI", "GeoTIFF"})
    String authority;

    /**
     * The indentation to use for WKT formatting.
     */
    @Option
    int indent = 2;

    /**
     * Whatever to force "longitude first" axis order.
     */
    @Option
    boolean forcexy;

    /**
     * Creates a new instance of {@code ReferencingCommands}.
     *
     * @param arguments The command-line arguments.
     */
    protected ReferencingCommands(final String[] arguments) {
        super("java -jar geotk-referencing.jar", arguments);
    }

    /**
     * Creates a new instance of {@code ReferencingCommands} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        final ReferencingCommands console = new ReferencingCommands(arguments);
        console.run();
    }

    /**
     * Returns a set of examples to be printed after the help screen.
     */
    @Override
    Map<String,String> examples() {
        final Map<String,String> examples = new LinkedHashMap<>();
        examples.put("BasicExample",      "EPSG:4181 EPSG:4326 CRS:84 AUTO:42001,30,0");
        examples.put("BasicEpsgExample",  "--authority=EPSG 4181 7411");
        examples.put("BursawolfsExample", "bursawolfs EPSG:4230");
        examples.put("OperationsExample", "operations --authority=EPSG 4230 4326");
        examples.put("TransformExample",  "transform EPSG:4230 EPSG:4326");
        examples.put("ListExample",       "list operations");
        examples.put("CodesExample",      "list codes | grep \"NTF\"");
        examples.put("ParametersExample", "parameters Mercator_1SP Mercator_2SP");
        return examples;
    }

    /**
     * Invoked when the user did not supplied an action. The default implementation assumes
     * that the arguments are authority codes of CRS objects to prints in <cite>Well Known
     * Text</cite> (WKT) format. This is the most common usage of this class.
     */
    @Override
    protected void unknownAction(final String action) {
        if (action == null) {
            // No argument at all. Prints the summary.
            super.unknownAction(action);
            return;
        }
        new ReferencingAction(this).printObjectsWKT(arguments);
    }

    /**
     * Invoked when the user asked the {@code "list"} action. The default implementation
     * lists the available factories, authority codes or coordinate operations. The argument
     * must be one of {@code "authorities"}, {@code "codes"}, {@code "operations"},
     * {@code "conversions"} or {@code "projections"}.
     */
    @Action(minimalArgumentCount=1, maximalArgumentCount=1,
            examples = {"authorities", "codes", "operations", "conversions", "projections"})
    protected void list() {
        new ReferencingAction(this).list(arguments);
    }

    /**
     * Invoked when the user asked the {@code "factories"} action. The default implementation
     * prints all {@linkplain AuthorityFactory authority factories} dependencies as a tree.
     */
    @Action(maximalArgumentCount = 0)
    protected void factories() {
        final FactoryDependencies printer = new FactoryDependencies(CRS.getAuthorityFactory(forcexy));
        printer.setAttributeEnabled(true);
        printer.setColorEnabled(colors);
        printer.setAbridged(!debug);
        printer.print(out);
        out.flush();
    }

    /**
     * Invoked when the user asked the {@code "parameters"} action. The default implementation
     * lists the parameters of all operations named on the command lines.
     */
    @Action(minimalArgumentCount = 1, examples = {"Affine", "EPSG:9624", "9624"})
    protected void parameters() {
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(HINTS);
        final ParameterFormat writer = new ParameterFormat(locale, null);
        for (int i=0; i<arguments.length; i++) {
            final ParameterValueGroup value;
            try {
                value = factory.getDefaultParameters(arguments[i]);
            } catch (NoSuchIdentifierException exception) {
                printException(exception);
                exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                return;
            }
            if (i != 0) {
                out.println();
            }
            out.println(writer.format(value.getDescriptor()));
        }
    }

    /**
     * Invoked when the user asked the {@code "bursawolfs"} action. The default implementation
     * lists the Bursa-Wolf parameters for the specified CRS ou datum objects.
     */
    @Action(minimalArgumentCount = 1, examples = {"EPSG:4230"})
    protected void bursawolfs() {
        new ReferencingAction(this).bursawolfs(arguments);
    }

    /**
     * Invoked when the user asked the {@code "operations"} action. The default implementation
     * prints the operations between every pairs of the specified authority code.
     */
    @Action(minimalArgumentCount = 2, examples = {"EPSG:4230", "EPSG:4326"})
    protected void operations() {
        new ReferencingAction(this).operations(arguments);
    }

    /**
     * Invoked when the user asked the {@code "transform"} action. The default implementation
     * prints the math transforms between every pairs of the specified authority code.
     */
    @Action(minimalArgumentCount = 2, examples = {"EPSG:4230", "EPSG:4326"})
    protected void transform() {
        new ReferencingAction(this).transform(arguments);
    }

    /**
     * Invoked when the user asked the {@code "reformat"} action. The default implementation
     * reads WKT strings from the {@linkplain System#in standard input stream} and reformats
     * them to the {@linkplain System#out standard output stream}. The input is read until it
     * reach the end-of-file ({@code [Ctrl-Z]} if reading from the keyboard).
     */
    @Action(maximalArgumentCount = 0)
    protected void reformat() {
        new ReferencingAction(this).reformat(in, out, err);
    }

    /**
     * Invoked when the user asked the {@code "test"} action. The default implementation
     * performs tests using factories found on the classpath. The test to perform is specified
     * by the argument following "{@code test}" on the command line. Supported values are:
     * <p>
     * <table cellpadding="3">
     *   <tr><td>{@code creates}</td>
     *   <td>Try to instantiate all CRS and report any failure to do so.</td></tr>
     *
     *   <tr><td>{@code duplicates}</td>
     *   <td>List all codes from {@link PropertyEpsgFactory} that are duplicating a code from
     *       the {@link org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory}.</td></tr>
     *
     *   <tr><td>{@code all}</td>
     *   <td>Perform all of the above tests.</td></tr>
     * </table>
     * <p>
     * The tests are implemented by calls to the following methods:
     * <p>
     * <ul>
     *   <li>{@link PropertyEpsgFactory#reportInstantiationFailures}</li>
     *   <li>{@link PropertyEpsgFactory#reportDuplicates}</li>
     * </ul>
     *
     * @todo Localize messages.
     */
    @Action(minimalArgumentCount=1, maximalArgumentCount=1,
            examples = {"creates", "duplicates", "all"})
    protected void test() {
        new ReferencingAction(this).test(arguments);
    }

    /**
     * Invoked when the user asked the {@code "console"} action. The default implementation
     * launchs the {@linkplain ReferencingConsole referencing console} for interactive
     * transformation of coordinate points.
     */
    @Action(maximalArgumentCount = 0)
    protected void console() {
        if (!consoleRunning) {
            final ReferencingConsole console = new ReferencingConsole(this);
            console.run();
        }
    }
}
