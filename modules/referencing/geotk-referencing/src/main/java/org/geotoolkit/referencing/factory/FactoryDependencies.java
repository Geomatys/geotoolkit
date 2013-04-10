/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;

import org.opengis.util.Factory;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.io.X364;
import org.geotoolkit.lang.Buffered;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeNode;
import org.geotoolkit.gui.swing.tree.NamedTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.internal.io.IOUtilities;

import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;


/**
 * Build a tree of factory dependencies, usually for printing to the console. This is a
 * convenience utility for inspecting the dependencies between Geotk referencing factories.
 * For example the following code will prints the full set of factories used by the
 * {@link org.geotoolkit.referencing.CRS#decode(String)} method:
 *
 * {@preformat java
 *   FactoryDependencies report = new FactoryDependencies(CRS.getAuthorityFactory(null));
 *   report.setColorEnabled(true); // Use only if the output console is ANSI X3.64 compliant.
 *   report.setAbridged(true);
 *   report.print();
 * }
 *
 * The output will looks like the tree below (actual output may vary depending the plugins
 * available on the classpath). The "{@code …⬏}" suffix means that the factory has already
 * been defined in a previous line and its dependencies are not repeated.
 *
 * {@preformat text
 * DefaultAuthorityFactory["All"]
 * └───AllAuthoritiesFactory["All"]
 *     ├───ThreadedEpsgFactory["EPSG"]
 *     │   └───AnsiDialectEpsgFactory["EPSG"]
 *     │       ├───ReferencingObjectFactory[objects]
 *     │       └───DatumAliases[objects]
 *     ├───AutoCRSFactory["AUTO2", "AUTO"]
 *     │   ├───ReferencingObjectFactory[objects] …⬏
 *     │   └───DatumAliases[objects] …⬏
 *     ├───WebCRSFactory["CRS", "OGC"]
 *     │   ├───ReferencingObjectFactory[objects] …⬏
 *     │   └───DatumAliases[objects] …⬏
 *     ├───URN_AuthorityFactory["urn:ogc:def", "urn:x-ogc:def"]
 *     │   └───AllAuthoritiesFactory["All"]
 *     │       ├───ThreadedEpsgFactory["EPSG"] …⬏
 *     │       ├───AutoCRSFactory["AUTO2"] …⬏
 *     │       └───WebCRSFactory["CRS"] …⬏
 *     └───HTTP_AuthorityFactory["http://www.opengis.net"]
 *         └───AllAuthoritiesFactory["All"] …⬏
 * }
 *
 * For example if an {@value org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory#FILENAME}
 * file is provided on the classpath, then the above code snippet is useful for verifying that
 * {@code PropertyEpsgFactory} appears as expected. It should be visible below
 * {@code ThreadedEpsgFactory} in a fallback chain.
 * <p>
 * An other way to gather information about the factories available at runtime is to set the
 * logging level of {@code org.geotoolkit} loggers to {@code CONFIG} or a finer level.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public class FactoryDependencies {
    /**
     * A list of interfaces or annotations that may be implemented by this class.
     * Used for the properties printed between parenthesis by {@link #createTree()}.
     */
    private static final Class<?>[] TYPES = {
        CRSFactory                          .class,
        CRSAuthorityFactory                 .class,
        CSFactory                           .class,
        CSAuthorityFactory                  .class,
        DatumFactory                        .class,
        DatumAuthorityFactory               .class,
        CoordinateOperationFactory          .class,
        CoordinateOperationAuthorityFactory .class,
        Buffered                            .class,
        Factory                             .class  // Processed in a special way.
    };

    /**
     * Labels for {@link #TYPES}.
     */
    private static final String[] TYPE_LABELS = {
        "crs", "crs", "cs", "cs", "datum", "datum", "operation", "operation",
        "buffered", "registered"
    };

    /**
     * The number of elements in {@link #TYPES} which are referencing factories.
     * They are printed in a different color than the last elements.
     */
    private static final int FACTORY_COUNT = TYPES.length - 2;

    /**
     * The factory to format.
     */
    private final Factory factory;

    /**
     * {@code true} for applying colors on a ANSI X3.64 (aka ECMA-48 and ISO/IEC 6429)
     * compliant output device.
     */
    private boolean colorEnabled;

    /**
     * {@code true} for printing attributes {@link #TYPE_LABELS} between parenthesis
     * after the factory name.
     */
    private boolean attributes;

    /**
     * If {@code true}, only the first node of duplicated factories will be reported.
     */
    private boolean abridged;

    /**
     * Creates a new dependency tree for the specified factory.
     *
     * @param factory The factory for which to build a dependency tree.
     */
    public FactoryDependencies(final Factory factory) {
        this.factory = factory;
    }

    /**
     * Returns {@code true} if syntax coloring is enabled.
     * Syntax coloring is disabled by default.
     *
     * @return {@code true} if syntax coloring is enabled.
     */
    public boolean isColorEnabled() {
        return colorEnabled;
    }

    /**
     * Enables or disables syntax coloring on ANSI X3.64 (aka ECMA-48 and ISO/IEC 6429)
     * compatible terminal. By default, syntax coloring is disabled.
     *
     * @param enabled {@code true} for enabling syntax coloring.
     */
    public void setColorEnabled(final boolean enabled) {
        colorEnabled = enabled;
    }

    /**
     * Returns {@code true} if attributes are to be printed.
     * By default, attributes are not printed.
     *
     * @return {@code true} if attributes will be printed.
     */
    public boolean isAttributeEnabled() {
        return attributes;
    }

    /**
     * Enables or disables the addition of attributes after factory names. Attributes
     * are labels like "{@code crs}", "{@code datum}", <i>etc.</i> put between
     * parenthesis. They give indications on the services implemented by the factory
     * (e.g. {@link CRSAuthorityFactory}, {@link DatumAuthorityFactory}, <i>etc.</i>).
     *
     * @param enabled {@code true} for printing attributes.
     */
    public void setAttributeEnabled(final boolean enabled) {
        attributes = enabled;
    }

    /**
     * Returns {@code true} if only the first node of duplicated factories will be reported.
     * The default value is {@code false}, which means that the full branch will be expanded
     * on every occurrence of a factory in the dependency graph.
     *
     * @return {@code true} if only the first node of duplicated factories will be reported,
     *         except for the first occurrence.
     *
     * @since 3.00
     */
    public boolean isAbridged() {
        return abridged;
    }

    /**
     * Sets whetever the tree should be abridged. If {@code true}, only the first node of
     * duplicated factories will be reported (except the first occurrence which is expanded
     * like usual).
     *
     * @param abridged {@code true} for an abridged tree, or {@code false} for expanding
     *        every branches unconditionally.
     *
     * @since 3.00
     */
    public void setAbridged(final boolean abridged) {
        this.abridged = abridged;
    }

    /**
     * Prints the dependencies as a tree to the {@linkplain System#out standard output stream}.
     *
     * @since 3.00
     */
    public void print() {
        final PrintWriter out = IOUtilities.standardPrintWriter();
        print(out);
        out.flush();
    }

    /**
     * Prints the dependencies as a tree to the specified printer.
     *
     * @param out Where to print the dependencies tree.
     */
    public void print(final PrintWriter out) {
        out.print(Trees.toString(asTree()));
    }

    /**
     * Prints the dependencies as a tree to the specified writer.
     *
     * @param  out Where to write the dependencies tree.
     * @throws IOException if an error occurred while writing to the stream.
     */
    public void print(final Appendable out) throws IOException {
        out.append(Trees.toString(asTree()));
    }

    /**
     * Returns the dependencies as a tree.
     *
     * @return The dependencies as a tree.
     */
    public TreeNode asTree() {
        return createTree(factory, new IdentityHashMap<Factory,Integer>());
    }

    /**
     * Returns the dependencies for the specified factory.
     *
     * @param  factory The factory for which to create a tree.
     * @param  flags Take trace of factories that are already printed.
     * @return The created tree.
     */
    private MutableTreeNode createTree(final Factory factory, final Map<Factory,Integer> flags) {
        final int PROGRESS=1, DONE=2; // Bit flags for this method only.
        final int bits = intValue(flags.get(factory));
        final boolean isFirst = (bits & DONE) == 0;
        final DefaultMutableTreeNode root = createNode(factory, isFirst);
        flags.put(factory, bits | DONE);
        if (factory instanceof ReferencingFactory && (isFirst || !abridged)) {
            final Collection<?> dep = ((ReferencingFactory) factory).dependencies();
            if (dep != null) {
                for (final Object element : dep) {
                    final MutableTreeNode child;
                    if (element instanceof Factory) {
                        final Factory candidate = (Factory) element;
                        int bc = intValue(flags.get(candidate));
                        if ((bc & PROGRESS) != 0) {
                            continue;
                        }
                        flags.put(candidate, bc | PROGRESS);
                        child = createTree(candidate, flags);
                        bc = intValue(flags.get(candidate));
                        flags.put(candidate, bc & ~PROGRESS);
                    } else {
                        child = new DefaultMutableTreeNode(element, false);
                    }
                    root.add(child);
                }
            }
        }
        return root;
    }

    /**
     * Creates a single node for the specified factory.
     *
     * @param  factory The factory for which to create a node.
     * @param  isFirst {@code true} if this is the first occurrence of this factory.
     * @return The created node.
     */
    private DefaultMutableTreeNode createNode(final Factory factory, final boolean isFirst) {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(factory)).append('[');
        if (factory instanceof AuthorityFactory) {
            final Citation authority = ((AuthorityFactory) factory).getAuthority();
            if (authority != null) {
                final Collection<? extends Identifier> identifiers = authority.getIdentifiers();
                if (!isNullOrEmpty(identifiers)) {
                    boolean next = false;
                    for (final Identifier id : identifiers) {
                        if (next) buffer.append(", ");
                        appendIdentifier(buffer, id.getCode(), isFirst);
                        if (!isFirst) break;
                        next = true;
                    }
                } else {
                    appendIdentifier(buffer, authority.getTitle(), isFirst);
                }
            }
        } else {
            appendColor(buffer, isFirst ? X364.FOREGROUND_RED : X364.FOREGROUND_YELLOW, "objects");
        }
        buffer.append(']');
        if (!isFirst) {
            appendColor(buffer, X364.FOREGROUND_YELLOW, " \u2026\u2B0F");
        } else if (attributes) {
            boolean hasFound = false;
            for (int i=0; i<TYPES.length; i++) {
                final Class<?> type = TYPES[i];
                if (Annotation.class.isAssignableFrom(type)) {
                    if (!factory.getClass().isAnnotationPresent(type.asSubclass(Annotation.class))) {
                        continue;
                    }
                } else {
                    if (!type.isInstance(factory)) {
                        continue;
                    }
                    if (type.equals(Factory.class)) { // Special case.
                        if (!FactoryFinder.isRegistered(factory)) {
                            continue;
                        }
                    }
                }
                buffer.append(hasFound ? ", " : " (");
                appendColor(buffer, i < FACTORY_COUNT ? X364.FOREGROUND_GREEN : X364.FOREGROUND_CYAN, TYPE_LABELS[i]);
                hasFound = true;
            }
            if (hasFound) {
                buffer.append(')');
            }
        }
        return new NamedTreeNode(buffer.toString(), factory);
    }

    /**
     * Appends an identifier to the specified buffer.
     */
    private void appendIdentifier(final StringBuilder buffer, final CharSequence identifier, final boolean isFirst) {
        appendColor(buffer, isFirst ? X364.FOREGROUND_MAGENTA : X364.FOREGROUND_YELLOW);
        buffer.append('"').append(identifier).append('"');
        appendColor(buffer, X364.FOREGROUND_DEFAULT);
    }

    /**
     * Appends the given color to the given buffer if colors are enabled,
     * followed by the given text, followed by the default foreground.
     */
    private void appendColor(final StringBuilder buffer, final X364 color, final String text) {
        appendColor(buffer, color);
        buffer.append(text);
        appendColor(buffer, X364.FOREGROUND_DEFAULT);
    }

    /**
     * Appends the given color to the given buffer if colors are enabled.
     */
    private void appendColor(final StringBuilder buffer, final X364 color) {
        if (colorEnabled) {
            buffer.append(color.sequence());
        }
    }

    /**
     * Returns the primitive value of the given integer, treating null value as zero.
     */
    private static int intValue(final Integer value) {
        return (value != null) ? value.intValue() : 0;
    }

    /**
     * Returns the string representation of the dependencies tree.
     *
     * @since 3.10
     */
    @Override
    public String toString() {
        return Trees.toString(asTree());
    }
}
