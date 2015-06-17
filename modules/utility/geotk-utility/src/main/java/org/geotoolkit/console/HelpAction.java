/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.geotoolkit.io.X364;
import org.geotoolkit.io.LineWriter;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.LineWrapWriter;
import org.geotoolkit.io.IndentedLineWriter;
import org.apache.sis.util.Numbers;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.console.CommandLine.*;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * The action run by {@link CommandLine#help()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 2.5
 * @module
 */
final class HelpAction {
    /**
     * The command line.
     */
    private final CommandLine cmd;

    /**
     * Output stream to the console. This output stream use the encoding
     * specified by the {@code "--encoding"} argument, if presents.
     */
    private final PrintWriter out;

    /**
     * The line length. {@link LineWrapWriter} will be given this value
     * minus the width of the margin if there is one.
     */
    private final int lineLength;

    /**
     * The platform-specific line separator.
     */
    private final String lineSeparator;

    /**
     * A temporary buffer.
     */
    private final StringBuilder buffer;

    /**
     * Strings containing the "Examples" word in current {@linkplain #locale}.
     * This is set to a non-null value only if at least one example has been
     * printed as part of {@link CommandLine#help()} command.
     */
    private String examples;

    /**
     * Creates a new {@code HelpAction}.
     *
     * @param cmd The command line for which to create a help report.
     */
    HelpAction(final CommandLine cmd) {
        this.cmd = cmd;
        this.out = cmd.out;
        lineSeparator = System.lineSeparator();
        /*
         * Tries to get the number of columns in the terminal windows. Note: Unix users
         * may need to do "export COLUMNS" in their shell for getting this code to work.
         */
        int lineLength = 80;
        String columns = null;
        try {
            columns = System.getenv("COLUMNS");
        } catch (SecurityException e) {
            // We don't have the permission to get the environment.
            // Keep the current default value unchanged.
        }
        if (columns != null) try {
            lineLength = Integer.parseInt(columns);
        } catch (NumberFormatException e) {
            // Not a parsable integer. We could log a warning, but for
            // now just keep silently the current default value.
        }
        this.lineLength = lineLength;
        buffer = new StringBuilder();
    }

    /**
     * Invoked when the user asked the {@code "help"} action.
     */
    void help(final String command) {
        final Locale locale = cmd.locale;
        out.println(cmd.bold(Descriptions.getResources(locale).getString(
                    Descriptions.Keys.CommandUsage_1, command)));
        out.println();

        // Writer to be used for the remainding of this method, which trim trailing spaces.
        final Writer out = new LineWriter(this.out, lineSeparator);
        final TableWriter table = new TableWriter(out, "");
        table.setMultiLinesCells(true);

        // Lists of actions and options to be collected before to be printed.
        final Map<String,Map.Entry<String,String>> actions, options;
        actions = new TreeMap<>();
        options = new TreeMap<>();

        // Cached objects.
        boolean descriptionDone = false;
        String mandatory = null;

        Class<?> classe = cmd.getClass();
        do {
            final ResourceBundle resources = ResourceBundle.getBundle(classe.getName(), locale);
            if (!descriptionDone) try {
                final LineWrapWriter wrapping = new LineWrapWriter(out, lineLength);
                wrapping.write(getString(resources, "Description"));
                wrapping.write(lineSeparator);
                wrapping.write(lineSeparator);
                descriptionDone = true;
            } catch (IOException e) {
                cmd.printException(e);
                cmd.exit(IO_EXCEPTION_EXIT_CODE);
                return;
            }
            /*
             * Scan the methods declared in the class, looking for the ones annotated with @Action.
             * Parent classes will be inspected in next iterations of the "do ... while" loop.
             * This loop is a simplified version of the next one, which work on fields. See the
             * later for more comments.
             */
            for (final Method method : classe.getDeclaredMethods()) {
                final Action action = method.getAnnotation(Action.class);
                if (action == null) {
                    continue;
                }
                String name = action.name().trim();
                if (name.isEmpty()) {
                    name = method.getName();
                }
                if (actions.containsKey(name)) {
                    continue;
                }
                String label = name;
                buffer.setLength(0);
                color(X364.BOLD);
                color(X364.FOREGROUND_GREEN);
                buffer.append(label);
                color(X364.RESET);
                label = buffer.toString();
                final String description = description(resources, name, action.examples());
                actions.put(name, new AbstractMap.SimpleEntry<>(label, description));
            }
            /*
             * Scan the fields declared in the class, looking for the ones annotated with @Option.
             * Parent classes will be inspected in next iterations of the "do ... while" loop.
             */
            for (final Field field : classe.getDeclaredFields()) {
                final Option option = field.getAnnotation(Option.class);
                if (option == null) {
                    continue;
                }
                /*
                 * Found an annotated field. If the name was already found in some sub-class,
                 * keep the sub-class definition untouched.
                 */
                String name = option.name().trim();
                if (name.isEmpty()) {
                    name = field.getName();
                }
                if (options.containsKey(name)) {
                    continue;
                }
                /*
                 * Build a label as a concatenation of the field name
                 * and the type (either a number or a string).
                 */
                String label = name;
                buffer.setLength(0);
                color(X364.BOLD);
                color(X364.FOREGROUND_GREEN);
                buffer.append(OPTION_PREFIX).append(label);
                color(X364.NORMAL);
                Class<?> type = field.getType();
                if (!Boolean.TYPE.isAssignableFrom(type)) {
                    type = Numbers.primitiveToWrapper(type);
                    buffer.append('=');
                    if (Boolean.class.isAssignableFrom(type)) {
                        buffer.append("on|off");
                    } else {
                        buffer.append(Number.class.isAssignableFrom(type) ? 'N' : 'S');
                    }
                }
                color(X364.FOREGROUND_DEFAULT);
                if (option.mandatory()) {
                    if (mandatory == null) {
                        mandatory = Vocabulary.getResources(locale)
                                .getString(Vocabulary.Keys.Mandatory).toLowerCase(locale);
                    }
                    buffer.append("\n  ");
                    color(X364.FOREGROUND_GREEN);
                    buffer.append('(').append(mandatory).append(')');
                    color(X364.FOREGROUND_DEFAULT);
                }
                label = buffer.toString();
                /*
                 * At this point, the label is final. Build the description
                 * string and save the (name,description) pair in the map.
                 */
                final String description = description(resources, name, option.examples());
                options.put(name, new AbstractMap.SimpleEntry<>(label, description));
            }
        } while (CommandLine.class.isAssignableFrom(classe = classe.getSuperclass()));
        /*
         * At this point the maps are built and sorted.
         * Now print them, followed by examples.
         */
        final Vocabulary resources = Vocabulary.getResources(locale);
        try {
            if (!actions.isEmpty()) {
                print(table, resources.getLabel(Vocabulary.Keys.Commands), actions.values());
            }
            if (!options.isEmpty()) {
                print(table, resources.getLabel(Vocabulary.Keys.Options), options.values());
            }
            table.flush();
        } catch (IOException e) {
            // Should never happen since we are flushing to a PrintWriter.
            cmd.printException(e);
            cmd.exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
        examples(command);
    }

    /**
     * Formats the description of an action or an option. The description is completed
     * with the examples, if any.
     *
     * @param  resources The resource bundle to use for fetching the description.
     * @param  name      The action name or the option name.
     * @param  examples  The examples, or an empty array if none.
     * @return The description.
     */
    private String description(final ResourceBundle resources, final String name, final String[] examples) {
        String description = getString(resources, name);
        if (examples.length != 0) {
            buffer.setLength(0);
            buffer.append(description).append(lineSeparator);
            color(X364.FAINT);
            buffer.append(examplesLabel()).append('"');
            for (int i=0; i<examples.length; i++) {
                if (i != 0) {
                    buffer.append("\", \"");
                }
                buffer.append(examples[i]);
            }
            buffer.append("\".");
            color(X364.NORMAL);
            description = buffer.toString();
        }
        return description;
    }

    /**
     * Returns a string value from the given resource bundle, replacing it by an
     * error message if the string was not found.
     * <p>
     * NOTE: Do not invoke {@link String#trim} on the returned value; it wipe out the X3.64
     * escape code.
     *
     * @param  resources The resource bundle to use for fetching the description.
     * @param  key       The key for the value to obtain.
     * @return The localized resources.
     */
    private String getString(final ResourceBundle bundle, final String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            buffer.setLength(0);
            color(X364.BACKGROUND_RED);
            buffer.append(e.getLocalizedMessage());
            color(X364.BACKGROUND_DEFAULT);
            return buffer.toString();
        }
    }

    /**
     * Applies the given color to the specified buffer only if colors are enabled.
     *
     * @param color  The color to add.
     */
    final void color(final X364 color) {
        cmd.color(buffer, color);
    }

    /**
     * Prints the specified options to the standard output stream.
     *
     * @param  table   The table to write to.
     * @param  title   The title to print before the arguments.
     * @param  options The optional or mandatory arguments to list.
     * @throws IOException if an error occurs while writing to the output stream.
     */
    private void print(final TableWriter table, String title,
            final Collection<Map.Entry<String,String>> options) throws IOException
    {
        title = cmd.bold(title.trim());
        table.write(title);
        table.nextLine();
        table.write(lineSeparator);
        table.nextLine();
        // The "18" margin below is determined empirically in order
        // to produce good result for ReferencingCommands. We don't
        // have at this time a way to predict the column width.
        final LineWrapWriter wrapping = new LineWrapWriter(table, lineLength - 18);
        for (final Map.Entry<String,String> entry : options) {
            table.write("  ");
            table.write(entry.getKey());
            table.write(' ');
            table.nextColumn();
            wrapping.write(entry.getValue());
            wrapping.write(lineSeparator);
            table.nextLine();
            table.write(lineSeparator);
            table.nextLine();
        }
    }

    /**
     * Returns {@code "Examples: "} localized in current {@linkplain #locale}.
     */
    private String examplesLabel() {
        if (examples == null) {
            examples = Vocabulary.getResources(cmd.locale).getLabel(Vocabulary.Keys.Examples);
        }
        return examples;
    }

    /**
     * Prints examples.
     */
    private void examples(final String command) {
        final Map<String,String> examples = cmd.examples();
        if (isNullOrEmpty(examples)) {
            return;
        }
        /*
         * There is at least one example to print. Gets the list of ResourceBundles that may
         * contain their descriptions. Note that we should always found at least one bundle,
         * the CommandLine.properties one. If we really found none of them, prints the first
         * exception and terminate this method.
         */
        final List<ResourceBundle> resources = new ArrayList<>();
        MissingResourceException failure = null;
        for (Class<?> c=cmd.getClass(); CommandLine.class.isAssignableFrom(c); c=c.getSuperclass()) {
            try {
                resources.add(ResourceBundle.getBundle(c.getName(), cmd.locale));
            } catch (MissingResourceException e) {
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        if (resources.isEmpty()) {
            cmd.printException(failure);
            return;
        }
        /*
         * Now prints the examples.
         */
        out.println(cmd.bold(examplesLabel().trim()));
        final Writer indented = new IndentedLineWriter(out, 4);
        final Writer wrapping = new LineWrapWriter(indented, lineLength - 4);
        for (final Map.Entry<String,String> entry : examples.entrySet()) {
            final String key = entry.getKey();
            String description = null;
            for (final ResourceBundle rb : resources) {
                try {
                    description = rb.getString(key);
                    break;
                } catch (MissingResourceException e) {
                    // Ignore - we will try other bundles.
                }
            }
            if (description == null) {
                description = getString(resources.get(0), key);
            }
            buffer.setLength(0);
            color(X364.FOREGROUND_GREEN);
            buffer.append(command).append(' ').append(cmd.bold(entry.getValue()));
            color(X364.FOREGROUND_DEFAULT);
            try {
                indented.write(lineSeparator);
                indented.write(buffer.toString());
                indented.write(lineSeparator);
                wrapping.write(description);
                wrapping.write(lineSeparator);
            } catch (IOException e) {
                cmd.printException(e);
                cmd.exit(IO_EXCEPTION_EXIT_CODE);
                return;
            }
        }
    }
}
