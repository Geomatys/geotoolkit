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

/**
 * Defines {@code main} methods to be invoked from the {@code java} command in a shell.
 * By default data are read and results are sent to the {@linkplain java.io.Console console}.
 * If there is no console attached to the currently running JVM, then data are read from the
 * {@linkplain java.lang.System#in standard input} and results are sent to the
 * {@linkplain java.lang.System#out standard output} streams.
 * <p>
 * The commands pattern is as below:
 *
 * {@preformat text
 *     java -jar <geotk-module.jar> <options> <action> <parameters...>
 * }
 *
 * where {@code <options>} is a list of zero, one or more space-separated options of the
 * form {@code --option=value} (or {@code --option} alone for options that are boolean flags),
 * {@code <action>} is a single word and {@code <parameters...>} are values that depend on
 * the action being executed. All commands shall understand at least the {@code help} action,
 * which take no parameter.
 *
 * {@section For implementors}
 * Implementors create a subclass of {@link org.geotoolkit.console.CommandLine} with the
 * following elements:
 *
 * <ul>
 *   <li><p>Fields annotated by {@link org.geotoolkit.console.Option}. They are the options to be
 *   assigned before the action is executed. The fields can be of any type convertible by {@link
 *   org.apache.sis.util.ObjectConverters} from a {@link java.lang.String} to the field
 *   type. This include (but is not limited to) {@code boolean}, {@code int}, {@code double},
 *   {@link java.lang.String}, {@link java.io.File} and {@link java.net.URL}.</p></li>
 *
 *   <li><p>Methods annotated by {@link org.geotoolkit.console.Action}. At most one of those will be
 *   invoked after the fields have been assigned as a result of the action given by the user on the
 *   command line.</p></li>
 *
 *   <li><p>{@linkplain java.util.Properties} files in the same package and having the same name
 *   than the subclass containing the above-cited annotated elements. The properties files may be
 *   suffixed by country and language codes as specified by {@link java.util.ResourceBundle}. They
 *   must contain key-value pairs for each of the following keys:</p>
 *   <ul>
 *     <li>{@linkplain org.geotoolkit.console.Option#name Option names},
 *         or the field names when no option name is explicitly given.</li>
 *     <li>{@linkplain org.geotoolkit.console.Action#name Action names},
 *         or the method names when no action name is explicitly given.</li>
 *     <li>{@code Description} (note the upper-case {@code D}) for an overview
 *         of what the command does.</li>
 *   </ul>
 *   <p>Values must be localized descriptions to be printed when the
 *   {@linkplain org.geotoolkit.console.CommandLine#help() help} action is executed. See
 *   <a href="http://hg.geotoolkit.org/geotoolkit/file/tip/modules/referencing/geotk-referencing/src/main/resources/org/geotoolkit/console/ReferencingCommands.properties">here</a>
 *   for an example of such properties file.
 * </p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
package org.geotoolkit.console;
