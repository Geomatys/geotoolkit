/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.io.IOException;
import java.text.NumberFormat;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.parameter.ParameterWriter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.FactoryDependencies;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.geotoolkit.referencing.factory.FallbackAuthorityFactory;
import static org.geotoolkit.referencing.AbstractIdentifiedObject.NAME_COMPARATOR;


/**
 * Describe the given referencing object.  This tool can display in Well Known Text
 * format a Coordinate Reference System identified by a given code  (typically, but
 * not limited to, EPSG namespace). It can also describe the parameters expected by
 * a given Operation Method, list the available authority codes, <cite>etc.</cite>
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
 * @version 3.00
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
    private String authority;

    /**
     * The indentation to use for WKT formatting.
     */
    @Option
    private int indent = FormattableObject.getDefaultIndentation();

    /**
     * Whatever to force "longitude first" axis order.
     */
    @Option
    private boolean forcexy;

    /**
     * The object to use for parsing and formatting WKT.
     * Will be created when first needed.
     *
     * @see #getWktFormat
     */
    private transient WKTFormat format;

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
        final Map<String,String> examples = new LinkedHashMap<String,String>();
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
     * Returns the object to use for parsing and formatting WKT.
     *
     * @return The WKT parser and formatter.
     */
    final WKTFormat getWktFormat() {
        if (format == null) {
            format = new WKTFormat();
            if (authority != null) {
                format.setAuthority(Citations.fromName(authority));
            }
            if (Boolean.TRUE.equals(colors)) {
                format.setColors(Colors.DEFAULT);
            }
            format.setIndentation(indent);
        }
        return format;
    }

    /**
     * Returns the CRS authority factory to use.
     */
    private CRSAuthorityFactory getCRSAuthorityFactory() {
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(forcexy);
        if (authority != null) {
            final CRSAuthorityFactory first;
            final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, forcexy);
            try {
                first = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, hints);
            } catch (FactoryNotFoundException e) {
                err.println(e.getLocalizedMessage());
                err.println();
                err.flush();
                return factory;
            }
            factory = FallbackAuthorityFactory.create(CRSAuthorityFactory.class, first, factory);
        }
        return factory;
    }

    /**
     * The separator to put between WKT.
     */
    private static char[] getSeparator() {
        final char[] separator = new char[79];
        Arrays.fill(separator, '\u2500');
        return separator;
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
        final CRSAuthorityFactory factory = getCRSAuthorityFactory();
        char[] separator = null;
        for (int i=0; i<arguments.length; i++) {
            if (separator == null) {
                separator = getSeparator();
            } else {
                out.println(separator);
            }
            final WKTFormat formatter = getWktFormat();
            final IdentifiedObject object;
            try {
                object = factory.createObject(arguments[i]);
            } catch (FactoryException e) {
                printException(e);
                exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                return;
            }
            out.println(formatter.format(object));
            final String warning = formatter.getWarning();
            if (warning != null) {
                out.println();
                out.print(Vocabulary.getResources(locale).getString(Vocabulary.Keys.WARNING));
                out.print(": ");
                out.println(warning);
            }
            out.flush();
        }
    }

    /**
     * Invoked when the user asked the {@code "list authorities"} sub-action.
     * The default implementation lists all CRS authority factories.
     */
    private void listAuthorities() {
        final Vocabulary resources = Vocabulary.getResources(locale);
        final Set<Citation> done  = new HashSet<Citation>();
        final TableWriter   table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
        final TableWriter   notes = new TableWriter(out, " ");
        int noteCount = 0;
        notes.setMultiLinesCells(true);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        table.write(bold(resources.getString(Vocabulary.Keys.AUTHORITY)));
        table.nextColumn();
        table.write(bold(resources.getString(Vocabulary.Keys.DESCRIPTION)));
        table.nextColumn();
        table.write(bold(resources.getString(Vocabulary.Keys.NOTE)));
        table.writeHorizontalSeparator();
        for (AuthorityFactory factory : AuthorityFactoryFinder.getCRSAuthorityFactories(HINTS)) {
            final Citation authority = factory.getAuthority();
            final Iterator<? extends Identifier> identifiers = authority.getIdentifiers().iterator();
            if (!identifiers.hasNext()) {
                // No identifier. Scan next authorities.
                continue;
            }
            if (!done.add(authority)) {
                // Already done. Scans next authorities.
                continue;
            }
            table.write(identifiers.next().getCode());
            table.nextColumn();
            table.write(authority.getTitle().toString().trim());
            if (factory instanceof AbstractAuthorityFactory) {
                String description;
                try {
                    description = ((AbstractAuthorityFactory) factory).getBackingStoreDescription();
                } catch (FactoryException e) {
                    description = e.getLocalizedMessage();
                }
                if (description != null) {
                    final String n = String.valueOf(++noteCount);
                    table.nextColumn();
                    table.write('('); table.write(n); table.write(')');
                    notes.write('('); notes.write(n); notes.write(')');
                    notes.nextColumn();
                    notes.write(description.trim());
                    notes.nextLine();
                }
            }
            table.nextLine();
        }
        table.writeHorizontalSeparator();
        try {
            table.flush();
            notes.flush();
        } catch (IOException e) {
            // Should never happen, since we are backed by PrintWriter.
            throw new AssertionError(e);
        }
    }

    /**
     * Invoked when the user asked the {@code "list codes"} sub-action.
     * The default implementation lists all CRS codes.
     */
    private void listCodes() {
        final CRSAuthorityFactory factory;
        if (authority != null) {
            factory = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, HINTS);
        } else {
            factory = CRS.getAuthorityFactory(forcexy);
        }
        final Vocabulary resources = Vocabulary.getResources(locale);
        final TableWriter table = new TableWriter(out);
        table.writeHorizontalSeparator();
        table.write(bold(resources.getString(Vocabulary.Keys.CODE)));
        table.nextColumn();
        table.write(bold(resources.getString(Vocabulary.Keys.DESCRIPTION)));
        table.writeHorizontalSeparator();
        try {
            final Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
            for (final String code : codes) {
                table.write(code);
                table.nextColumn();
                try {
                    final InternationalString description = factory.getDescriptionText(code);
                    if (description != null) {
                        table.write(description.toString(locale));
                    }
                } catch (NoSuchAuthorityCodeException e) {
                    // Ignore. We will let the cell blank.
                }
                table.nextLine();
            }
        } catch (FactoryException e) {
            printException(e);
            exit(INTERNAL_ERROR_EXIT_CODE);
        }
        table.writeHorizontalSeparator();
        try {
            table.flush();
        } catch (IOException e) {
            printException(e);
            exit(IO_EXCEPTION_EXIT_CODE);
        }
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
        final String list = arguments[0];
        final Class<? extends Operation> type;
        if (list.equalsIgnoreCase("authorities")) {
            listAuthorities();
            return;
        } else if (list.equalsIgnoreCase("codes")) {
            listCodes();
            return;
        } else if (list.equalsIgnoreCase("operations")) {
            type = Operation.class;
        } else if (list.equalsIgnoreCase("conversions")) {
            type = Conversion.class;
        } else if (list.equalsIgnoreCase("projections")) {
            type = Projection.class;
        } else {
            final Errors resources = Errors.getResources(locale);
            err.println(resources.getString(Errors.Keys.ILLEGAL_ARGUMENT_$1, "list"));
            err.println(resources.getString(Errors.Keys.UNKNOW_TYPE_$1, list));
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
            return;
        }
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(HINTS);
        final Set<OperationMethod> methods = new TreeSet<OperationMethod>(NAME_COMPARATOR);
        methods.addAll(factory.getAvailableMethods(type));
        final ParameterWriter writer = new ParameterWriter(out);
        writer.setLocale(locale);
        writer.setColorEnabled(colors);
        writer.setAuthorities("EPSG:#", (authority != null) ? authority : "Geotoolkit");
        try {
            writer.summary(methods);
        } catch (IOException exception) {
            printException(exception);
            exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
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
        final ParameterWriter writer = new ParameterWriter(out);
        writer.setLocale(locale);
        writer.setColorEnabled(colors);
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
            try {
                writer.format(value.getDescriptor());
            } catch (IOException exception) {
                printException(exception);
                exit(IO_EXCEPTION_EXIT_CODE);
                return;
            }
        }
    }

    /**
     * Invoked when the user asked the {@code "bursawolfs"} action. The default implementation
     * lists the Bursa-Wolf parameters for the specified CRS ou datum objects.
     */
    @Action(minimalArgumentCount = 1, examples = {"EPSG:4230"})
    protected void bursawolfs() {
        final Vocabulary resources = Vocabulary.getResources(locale);
        final NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMinimumFractionDigits(3);
        nf.setMaximumFractionDigits(3);
        final TableWriter table = new TableWriter(out);
        table.writeHorizontalSeparator();
        final String[] titles = {
            resources.getString(Vocabulary.Keys.TARGET),
            "dx", "dy", "dz", "ex", "ey", "ez", "ppm"
        };
        for (int i=0; i<titles.length; i++) {
            table.write(bold(titles[i]));
            table.nextColumn();
            table.setAlignment(TableWriter.ALIGN_CENTER);
        }
        table.writeHorizontalSeparator();
        final CRSAuthorityFactory factory = getCRSAuthorityFactory();
        for (int i=0; i<arguments.length; i++) {
            IdentifiedObject object;
            try {
                object = factory.createObject(arguments[i]);
            } catch (FactoryException e) {
                printException(e);
                exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                return;
            }
            if (object instanceof CoordinateReferenceSystem) {
                object = CRSUtilities.getDatum((CoordinateReferenceSystem) object);
            }
            if (object instanceof DefaultGeodeticDatum) {
                final BursaWolfParameters[] params =
                        ((DefaultGeodeticDatum) object).getBursaWolfParameters();
                for (int j=0; j<params.length; j++) {
                    final BursaWolfParameters p = params[j];
                    table.setAlignment(TableWriter.ALIGN_LEFT);
                    table.write(p.targetDatum.getName().getCode());
                    table.nextColumn();
                    table.setAlignment(TableWriter.ALIGN_RIGHT);
                    double v;
                    for (int k=0; k<7; k++) {
                        switch (k) {
                            case 0: v = p.dx;  break;
                            case 1: v = p.dy;  break;
                            case 2: v = p.dz;  break;
                            case 3: v = p.ex;  break;
                            case 4: v = p.ey;  break;
                            case 5: v = p.ez;  break;
                            case 6: v = p.ppm; break;
                            default: throw new AssertionError(k);
                        }
                        table.write(nf.format(v));
                        table.nextColumn();
                    }
                    table.nextLine();
                }
                table.writeHorizontalSeparator();
            }
        }
        try {
            table.flush();
        } catch (IOException e) {
            // Should never happen, since we are backed by PrintWriter
            throw new AssertionError(e);
        }
    }

    /**
     * Invoked when the user asked the {@code "operations"} action. The default implementation
     * prints the operations between every pairs of the specified authority code.
     */
    @Action(minimalArgumentCount = 2, examples = {"EPSG:4230", "EPSG:4326"})
    protected void operations() {
        final CoordinateOperationAuthorityFactory factory =
                AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory(authority, HINTS);
        char[] separator = null;
        for (int i=0; i<arguments.length; i++) {
            for (int j=i+1; j<arguments.length; j++) {
                final Set<CoordinateOperation> op;
                try {
                    op = factory.createFromCoordinateReferenceSystemCodes(arguments[i], arguments[j]);
                } catch (FactoryException e) {
                    printException(e);
                    exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                    return;
                }
                for (final CoordinateOperation operation : op) {
                    if (separator == null) {
                        separator = getSeparator();
                    } else {
                        out.println(separator);
                    }
                    final WKTFormat formatter = getWktFormat();
                    out.println(formatter.format(operation));
                }
            }
        }
    }

    /**
     * Invoked when the user asked the {@code "transform"} action. The default implementation
     * prints the math transforms between every pairs of the specified authority code.
     */
    @Action(minimalArgumentCount = 2, examples = {"EPSG:4230", "EPSG:4326"})
    protected void transform() {
        final CRSAuthorityFactory factory = getCRSAuthorityFactory();
        final CoordinateOperationFactory opFactory =
                AuthorityFactoryFinder.getCoordinateOperationFactory(HINTS);
        char[] separator = null;
        for (int i=0; i<arguments.length; i++) try {
            final CoordinateReferenceSystem crs1 = factory.createCoordinateReferenceSystem(arguments[i]);
            for (int j=i+1; j<arguments.length; j++) {
                final CoordinateReferenceSystem crs2 = factory.createCoordinateReferenceSystem(arguments[j]);
                final CoordinateOperation op;
                try {
                    op = opFactory.createOperation(crs1, crs2);
                } catch (OperationNotFoundException exception) {
                    out.println(exception.getLocalizedMessage());
                    continue;
                }
                if (separator == null) {
                    separator = getSeparator();
                } else {
                    out.println(separator);
                }
                final WKTFormat formatter = getWktFormat();
                out.println(formatter.format(op.getMathTransform()));
            }
        } catch (FactoryException e) {
            printException(e);
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user asked the {@code "reformat"} action. The default implementation
     * reads WKT strings from the {@linkplain System#in standard input stream} and reformats
     * them to the {@linkplain System#out standard output stream}. The input is read until it
     * reach the end-of-file ({@code [Ctrl-Z]} if reading from the keyboard).
     */
    @Action(maximalArgumentCount = 0)
    protected void reformat() {
        final WKTFormat format = getWktFormat();
        try {
            format.reformat(in, out, err);
        } catch (IOException exception) {
            printException(exception);
            exit(IO_EXCEPTION_EXIT_CODE);
        }
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
    @SuppressWarnings("fallthrough")
    protected void test() {
        final String test = arguments[0];
        final int code;
        if      (test.equalsIgnoreCase("all"))        code = 0;
        else if (test.equalsIgnoreCase("creates"))    code = 1;
        else if (test.equalsIgnoreCase("duplicates")) code = 2;
        else {
            final Errors resources = Errors.getResources(locale);
            err.println(resources.getString(Errors.Keys.ILLEGAL_ARGUMENT_$2, "test", test));
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
            return;
        }
        final Vocabulary resources = Vocabulary.getResources(locale);
        final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class);
        for (final CRSAuthorityFactory factory : AuthorityFactoryFinder.getCRSAuthorityFactories(hints)) {
            if (!(factory instanceof PropertyEpsgFactory)) {
                continue;
            }
            final PropertyEpsgFactory pf = (PropertyEpsgFactory) factory;
            try {
                switch (code) {
                    case 0:
                    case 1: {
                        out.println("CRS failures:");
                        if (pf.reportInstantiationFailures(out).isEmpty()) {
                            out.println(resources.getString(Vocabulary.Keys.NONE));
                        }
                        out.println();
                        if (code != 0) break;
                    }
                    case 2: {
                        out.println("CRS duplicates:");
                        if (pf.reportDuplicates(out).isEmpty()) {
                            out.println(resources.getString(Vocabulary.Keys.NO_DUPLICATION_FOUND));
                        }
                        out.println();
                        if (code != 0) break;
                    }
                    // Add other kind of tests here.
                }
            } catch (FactoryException e) {
                e.printStackTrace(err);
            }
        }
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
