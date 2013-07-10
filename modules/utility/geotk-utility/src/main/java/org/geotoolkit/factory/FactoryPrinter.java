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
package org.geotoolkit.factory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Arrays;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;

import org.opengis.util.Factory;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.io.TableWriter;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;


/**
 * Prints a list of factory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
@Debug
final class FactoryPrinter implements Comparator<Class<?>> {
    /**
     * The factory registries to format.
     */
    private final Collection<FactoryRegistry> registries;

    /**
     * Constructs a default instance of this printer.
     *
     * @param registry Where the factories are registered.
     */
    public FactoryPrinter(final FactoryRegistry registry) {
        this.registries = Collections.singleton(registry);
    }

    /**
     * Constructs a default instance of this printer.
     *
     * @param registries Where the factories are registered.
     */
    public FactoryPrinter(final Collection<FactoryRegistry> registries) {
        this.registries = registries;
    }

    /**
     * Compares two categories for order. This is used for sorting out the services
     * before to display them.
     */
    @Override
    public int compare(final Class<?> factory1, final Class<?> factory2) {
        return Classes.getShortName(factory1).compareToIgnoreCase(
               Classes.getShortName(factory2));
    }

    /**
     * Lists all available factory implementations in a tabular format. For each factory interface,
     * the first implementation listed is the default one. This method provides a way to check the
     * state of a system, usually for debugging purpose.
     *
     * @param  out The output stream where to format the list.
     * @param  locale The locale for the list, or {@code null}.
     * @throws IOException if an error occurs while writing to {@code out}.
     */
    public void list(final Writer out, final Locale locale) throws IOException {
        /*
         * Gets the categories in some sorted order.
         */
        final Map<Class<?>,FactoryRegistry> categories = new HashMap<>();
        for (final FactoryRegistry registry : registries) {
            for (final Iterator<Class<?>> it=registry.getCategories(); it.hasNext();) {
                categories.put(it.next(), registry);
            }
        }
        final Class<?>[] sorted = categories.keySet().toArray(new Class<?>[categories.size()]);
        Arrays.sort(sorted, this);
        /*
         * Prints the table header.
         */
        final Vocabulary resources = Vocabulary.getResources(locale);
        final TableWriter table  = new TableWriter(out);
        table.setMultiLinesCells(true);
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        table.write(resources.getString(Vocabulary.Keys.SERVICE));
        table.nextColumn();
        table.write(resources.getString(Vocabulary.Keys.IMPLEMENTATIONS));
        table.nextColumn();
        table.write(resources.getString(Vocabulary.Keys.VENDOR));
        table.nextColumn();
        table.write(resources.getString(Vocabulary.Keys.AUTHORITY));
        table.nextLine();
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        final StringBuilder vendors     = new StringBuilder();
        final StringBuilder authorities = new StringBuilder();
        int categoryCount = 0;
        for (final Class<?> category : sorted) {
            if (categoryCount++ != 0) {
                table.writeHorizontalSeparator();
            }
            /*
             * Writes the category name (CRSFactory, DatumFactory, etc.)
             */
            table.write(Classes.getShortName(category));
            table.nextColumn();
            /*
             * Writes the implementation in a single cell. We will do the same for vendors and
             * authorities, but those ones need to be stored in a temporary buffer for now.
             */
            final FactoryRegistry registry = categories.get(category);
            final Iterator<?> providers = registry.getServiceProviders(category, null, null, null);
            int implementationsCount = 0;
            while (providers.hasNext()) {
                if (implementationsCount++ != 0) {
                    table      .write ('\n');
                    vendors    .append('\n');
                    authorities.append('\n');
                }
                final Object provider = providers.next();
                table.write(Classes.getShortClassName(provider));
                if (provider instanceof Factory) {
                    final Citation vendor = ((Factory) provider).getVendor();
                    vendors.append(vendor.getTitle().toString(locale));
                }
                if (provider instanceof AuthorityFactory) {
                    final Citation authority = ((AuthorityFactory) provider).getAuthority();
                    final Iterator<? extends Identifier> identifiers =
                            authority.getIdentifiers().iterator();
                    final String identifier = identifiers.hasNext() ?
                            identifiers.next().getCode() : authority.getTitle().toString(locale);

                    authorities.append(identifier);
                }
            }
            /*
             * Writes all columns (vendors and authorities) that were buffered in the aboved loop.
             */
            table.nextColumn();
            table.write(vendors.toString());
            vendors.setLength(0);
            table.nextColumn();
            table.write(authorities.toString());
            authorities.setLength(0);
            table.nextLine();
        }
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        table.flush();
    }

    /**
     * Returns the list of available factory implementations in a string.
     *
     * @param  registry Where the factories are registered.
     * @return A string representation of the factories table.
     */
    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        try {
            list(out, null);
        } catch (IOException e) {
            // Should never happen since we are writing to a StringWriter,
            throw new AssertionError(e);
        }
        return out.toString();
    }
}
