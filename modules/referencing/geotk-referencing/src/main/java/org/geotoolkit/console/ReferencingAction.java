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
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.NumberFormat;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;

import org.geotoolkit.io.TableWriter;
import org.apache.sis.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.parameter.ParameterWriter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.geotoolkit.referencing.factory.FallbackAuthorityFactory;
import static org.geotoolkit.referencing.IdentifiedObjects.NAME_COMPARATOR;
import static org.geotoolkit.console.CommandLine.*;


/**
 * The actions run by {@link ReferencingCommands}.
 * Contains a few action which have been moved out of {@code ReferencingCommands} for making
 * it lighter, and faster to startup when the user just want the summary or the help screen.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
final class ReferencingAction {
    /**
     * The hints for the factory to fetch. Null for now, but may be different in a future version.
     */
    private static final Hints HINTS = null;

    /**
     * The command line.
     */
    private final ReferencingCommands cmd;

    /**
     * The resources.
     */
    private final Vocabulary resources;

    /**
     * The object to use for parsing and formatting WKT.
     * Will be created when first needed.
     *
     * @see #getWktFormat
     */
    private transient WKTFormat format;

    /**
     * Creates a new {@code ReferencingAction}.
     *
     * @param cmd The command line for which to create a help report.
     */
    ReferencingAction(final ReferencingCommands cmd) {
        this.cmd = cmd;
        resources = Vocabulary.getResources(cmd.locale);
    }

    /**
     * Formats the resources at the given key with bold characters.
     */
    private String bold(final short key) {
        return cmd.bold(resources.getString(key));
    }

    /**
     * Returns the object to use for parsing and formatting WKT.
     *
     * @return The WKT parser and formatter.
     */
    private WKTFormat getWktFormat() {
        if (format == null) {
            format = new WKTFormat();
            final String authority = cmd.authority;
            if (authority != null) {
                format.setNameAuthority(Citations.fromName(authority));
            }
            if (Boolean.TRUE.equals(cmd.colors)) {
                format.setColors(Colors.DEFAULT);
            }
            format.setIndentation(cmd.indent);
        }
        return format;
    }

    /**
     * Returns the CRS authority factory to use.
     */
    private CRSAuthorityFactory getCRSAuthorityFactory() {
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(cmd.forcexy);
        final String authority = cmd.authority;
        if (authority != null) {
            final CRSAuthorityFactory first;
            final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, cmd.forcexy);
            try {
                first = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, hints);
            } catch (FactoryNotFoundException e) {
                final PrintWriter err = cmd.err;
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
    public void printObjectsWKT(final String[] arguments) {
        final PrintWriter out = cmd.out;
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
                cmd.printException(e);
                cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                return;
            }
            out.println(formatter.format(object));
            final String warning = formatter.getWarning();
            if (warning != null) {
                out.println();
                out.print(resources.getString(Vocabulary.Keys.WARNING));
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
        final PrintWriter out = cmd.out;
        final Set<Citation> done  = new HashSet<>();
        final TableWriter   table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
        final TableWriter   notes = new TableWriter(out, " ");
        int noteCount = 0;
        notes.setMultiLinesCells(true);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        table.write(bold(Vocabulary.Keys.AUTHORITY));
        table.nextColumn();
        table.write(bold(Vocabulary.Keys.DESCRIPTION));
        table.nextColumn();
        table.write(bold(Vocabulary.Keys.NOTE));
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
        final String authority = cmd.authority;
        if (authority != null) {
            factory = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, HINTS);
        } else {
            factory = CRS.getAuthorityFactory(cmd.forcexy);
        }
        final TableWriter table = new TableWriter(cmd.out);
        table.writeHorizontalSeparator();
        table.write(bold(Vocabulary.Keys.CODE));
        table.nextColumn();
        table.write(bold(Vocabulary.Keys.DESCRIPTION));
        table.writeHorizontalSeparator();
        try {
            final Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
            for (final String code : codes) {
                table.write(code);
                table.nextColumn();
                try {
                    final InternationalString description = factory.getDescriptionText(code);
                    if (description != null) {
                        table.write(description.toString(cmd.locale));
                    }
                } catch (NoSuchAuthorityCodeException e) {
                    // Ignore. We will let the cell blank.
                }
                table.nextLine();
            }
        } catch (FactoryException e) {
            cmd.printException(e);
            cmd.exit(INTERNAL_ERROR_EXIT_CODE);
        }
        table.writeHorizontalSeparator();
        try {
            table.flush();
        } catch (IOException e) {
            cmd.printException(e);
            cmd.exit(IO_EXCEPTION_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user asked the {@code "list"} action. The default implementation
     * lists the available factories, authority codes or coordinate operations. The argument
     * must be one of {@code "authorities"}, {@code "codes"}, {@code "operations"},
     * {@code "conversions"} or {@code "projections"}.
     */
    public void list(final String[] arguments) {
        final String list = arguments[0];
        final Class<? extends SingleOperation> type;
        if (list.equalsIgnoreCase("authorities")) {
            listAuthorities();
            return;
        } else if (list.equalsIgnoreCase("codes")) {
            listCodes();
            return;
        } else if (list.equalsIgnoreCase("operations")) {
            type = SingleOperation.class;
        } else if (list.equalsIgnoreCase("conversions")) {
            type = Conversion.class;
        } else if (list.equalsIgnoreCase("projections")) {
            type = Projection.class;
        } else {
            final PrintWriter err = cmd.err;
            final Errors resources = Errors.getResources(cmd.locale);
            err.println(resources.getString(Errors.Keys.ILLEGAL_ARGUMENT_1, "list"));
            err.println(resources.getString(Errors.Keys.UNKNOWN_TYPE_1, list));
            cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
            return;
        }
        final String authority = cmd.authority;
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(HINTS);
        final Set<OperationMethod> methods = new TreeSet<>(NAME_COMPARATOR);
        methods.addAll(factory.getAvailableMethods(type));
        final ParameterWriter writer = new ParameterWriter(cmd.out);
        writer.setLocale(cmd.locale);
        writer.setColorEnabled(cmd.colors);
        writer.setAuthorities("EPSG:#", (authority != null) ? authority : "Geotk");
        try {
            writer.summary(methods);
        } catch (IOException exception) {
            cmd.printException(exception);
            cmd.exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
    }

    /**
     * Invoked when the user asked the {@code "bursawolfs"} action. The default implementation
     * lists the Bursa-Wolf parameters for the specified CRS ou datum objects.
     */
    public void bursawolfs(final String[] arguments) {
        final NumberFormat nf = NumberFormat.getNumberInstance(cmd.locale);
        nf.setMinimumFractionDigits(3);
        nf.setMaximumFractionDigits(3);
        final TableWriter table = new TableWriter(cmd.out);
        table.writeHorizontalSeparator();
        final String[] titles = {
            resources.getString(Vocabulary.Keys.TARGET),
            "dx", "dy", "dz", "ex", "ey", "ez", "ppm"
        };
        for (int i=0; i<titles.length; i++) {
            table.write(cmd.bold(titles[i]));
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
                cmd.printException(e);
                cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                return;
            }
            if (object instanceof CoordinateReferenceSystem) {
                object = CRS.getDatum((CoordinateReferenceSystem) object);
            }
            if (object instanceof DefaultGeodeticDatum) {
                final BursaWolfParameters[] params =
                        ((DefaultGeodeticDatum) object).getBursaWolfParameters();
                for (int j=0; j<params.length; j++) {
                    final BursaWolfParameters p = params[j];
                    table.setAlignment(TableWriter.ALIGN_LEFT);
                    table.write(p.getTargetDatum().getName().getCode());
                    table.nextColumn();
                    table.setAlignment(TableWriter.ALIGN_RIGHT);
                    double v;
                    for (int k=0; k<7; k++) {
                        switch (k) {
                            case 0: v = p.tX; break;
                            case 1: v = p.tY; break;
                            case 2: v = p.tZ; break;
                            case 3: v = p.rX; break;
                            case 4: v = p.rY; break;
                            case 5: v = p.rZ; break;
                            case 6: v = p.dS; break;
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
    public void operations(final String[] arguments) {
        final PrintWriter out = cmd.out;
        final CoordinateOperationAuthorityFactory factory =
                AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory(cmd.authority, HINTS);
        char[] separator = null;
        for (int i=0; i<arguments.length; i++) {
            for (int j=i+1; j<arguments.length; j++) {
                final Set<CoordinateOperation> op;
                try {
                    op = factory.createFromCoordinateReferenceSystemCodes(arguments[i], arguments[j]);
                } catch (FactoryException e) {
                    cmd.printException(e);
                    cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
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
    public void transform(final String[] arguments) {
        final PrintWriter out = cmd.out;
        final CRSAuthorityFactory factory = getCRSAuthorityFactory();
        final CoordinateOperationFactory opFactory =
                AuthorityFactoryFinder.getCoordinateOperationFactory(HINTS);
        char[] separator = null;
        try {
            for (int i=0; i<arguments.length; i++) {
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
            }
        } catch (FactoryException e) {
            cmd.printException(e);
            cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user asked the {@code "reformat"} action. The default implementation
     * reads WKT strings from the {@linkplain System#in standard input stream} and reformats
     * them to the {@linkplain System#out standard output stream}. The input is read until it
     * reach the end-of-file ({@code [Ctrl-Z]} if reading from the keyboard).
     */
    public void reformat(final Reader in, final Writer out, final PrintWriter err) {
        final WKTFormat format = getWktFormat();
        try {
            format.reformat(in, out, err);
        } catch (IOException exception) {
            cmd.printException(exception);
            cmd.exit(IO_EXCEPTION_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user asked the {@code "test"} action.
     */
    @SuppressWarnings("fallthrough")
    public void test(final String[] arguments) {
        final PrintWriter out = cmd.out;
        final String test = arguments[0];
        final int code;
        if      (test.equalsIgnoreCase("all"))        code = 0;
        else if (test.equalsIgnoreCase("creates"))    code = 1;
        else if (test.equalsIgnoreCase("duplicates")) code = 2;
        else {
            final Errors resources = Errors.getResources(cmd.locale);
            cmd.err.println(resources.getString(Errors.Keys.ILLEGAL_ARGUMENT_2, "test", test));
            cmd.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
            return;
        }
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
                e.printStackTrace(cmd.err);
            }
        }
    }
}
