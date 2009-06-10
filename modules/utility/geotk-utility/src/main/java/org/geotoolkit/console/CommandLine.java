/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;

import org.geotoolkit.io.X364;
import org.geotoolkit.io.LineWriter;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.LineWrapWriter;
import org.geotoolkit.io.IndentedLineWriter;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;


/**
 * Base class for command line tools. Subclasses shall define fields annotated with {@link Option}
 * and/or methods annotated with {@link Action}. The annotated fields will be initialized by the
 * {@link #run()} method.
 * <p>
 * The following actions are recognized by this class:
 * <p>
 * <table border="1" cellpadding="3">
 *   <tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code help}</b></td>
 *     <td>Print the {@linkplain #help() help} summary.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code version}</b></td>
 *     <td>Print version number and system information.</td>
 *   </tr>
 * </table>
 * <p>
 * The following options are recognized by this class:
 * <p>
 * <table border="1" cellpadding="3">
 *   <tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --colors}</b>=on|off</td>
 *     <td>Turn on or off syntax coloring on <A HREF="http://en.wikipedia.org/wiki/ANSI_escape_code">ANSI
 *       X3.64</A> (aka ECMA-48 and ISO/IEC 6429) compatible terminal.</p>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --debug}</b></td>
 *     <td>Print full stack trace in case of error.</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --encoding}</b>=<var>cp</var></td>
 *     <td>Sets the console encoding ({@code "UTF-8"}, {@code "ISO-8859-1"}, <cite>etc.</cite>)
 *         for application input and output. This value has no impact on data, but may improve
 *         the output quality. This is not needed on Linux terminal using UTF-8 encoding (tip:
 *         the <cite>terminus font</cite> gives good results).</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --locale}</b>=<var>lc</var></td>
 *     <td>Set the locale for string, number and date formatting
 *         ({@code "fr"} for French, <cite>etc.</cite>).</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.01
 *
 * @since 2.5
 * @module
 */
public abstract class CommandLine implements Runnable {
    /*
     * NOTE: There is no clear convention on exit code, except 0 == SUCCESS.
     * However a typical usage is to use higher values for more sever causes.
     */

    /**
     * The code given to {@link System#exit} when the program failed because of an illegal
     * user argument.
     */
    public static final int ILLEGAL_ARGUMENT_EXIT_CODE = 1;

    /**
     * The code given to {@link System#exit} when the program aborted at user request.
     */
    public static final int ABORT_EXIT_CODE = 2;

    /**
     * The code given to {@link System#exit} when the program failed because of bad
     * content in a file.
     */
    public static final int BAD_CONTENT_EXIT_CODE = 3;

    /**
     * The code given to {@link System#exit} when the program failed because of an
     * {@link java.io.IOException}.
     */
    public static final int IO_EXCEPTION_EXIT_CODE = 100;

    /**
     * The code given to {@link System#exit} when the program failed because of a
     * {@link java.sql.SQLException}.
     */
    public static final int SQL_EXCEPTION_EXIT_CODE = 101;

    /**
     * The code given to {@link System#exit} when the program failed because the
     * system is in a state that does not allow the execution of the program.
     *
     * @since 3.00
     */
    public static final int ILLEGAL_STATE_EXIT_CODE = 190;

    /**
     * The code given to {@link System#exit} when the program failed because of an
     * internal error.
     *
     * @since 3.00
     */
    public static final int INTERNAL_ERROR_EXIT_CODE = 200;

    /**
     * The prefix to prepend to option names.
     */
    private static final String OPTION_PREFIX = "--";

    /**
     * {@code true} if the {@code --debug} option has been passed on the command line.
     *
     * @since 3.00
     */
    @Option
    protected boolean debug;

    /**
     * The locale specified by the {@code "--locale"} option. If no such option was
     * provided, then this field is set to the {@linkplain Locale#getDefault default locale}.
     */
    @Option(examples={"fr", "fr_CA", "US"})
    protected Locale locale;

    /**
     * The encoding specified by the {@code "--encoding"} option. If no such option was provided,
     * then this field is set to the {@linkplain Charset#defaultCharset() default charset}.
     */
    @Option(examples={"UTF-8", "ISO-8859-1"})
    protected Charset encoding;

    /**
     * {@code true} if colors can be applied for ANSI X3.64 compliant terminal.
     * This is the value specified by the {@code --colors} arguments if present,
     * or a value inferred from the system otherwise.
     *
     * @since 3.00
     */
    @Option
    protected Boolean colors;

    /**
     * The line length. {@link LineWrapWriter} will be given this value
     * minus the width of the margin if there is one.
     */
    private int lineLength = 80;

    /**
     * Strings containing the "Examples" word in current {@linkplain #locale}.
     * This is set to a non-null value only if at least one example has been
     * printed as part of {@link #help()} command.
     */
    private String examples;

    /**
     * Input stream from the console. This is often a {@link BufferedReader} instance.
     * This input stream use the encoding specified by the {@code "--encoding"} argument,
     * if presents.
     *
     * @since 3.00
     */
    protected Reader in;

    /**
     * Output stream to the console. This output stream use the encoding
     * specified by the {@code "--encoding"} argument, if presents.
     */
    protected PrintWriter out;

    /**
     * Error stream to the console.
     */
    protected PrintWriter err;

    /**
     * The platform-specific line separator.
     */
    String lineSeparator;

    /**
     * The remaining arguments after all option values have been assigned to the fields.
     */
    protected String[] arguments;

    /**
     * The command used for launching the application.
     */
    private final String command;

    /**
     * {@code true} if the {@link #run} method is invoked recursively from {@link InteractiveConsole}.
     */
    transient boolean consoleRunning;

    /**
     * Creates a new {@code CommandLine} instance. This constructor keep a reference to
     * the given arguments, but does not parse them yet. The arguments are parsed when
     * {@link #run()} is invoked.
     *
     * @param command The command entered on the command line for launching the application.
     *        If {@code null}, default to {@code "java <classname>"}.
     * @param arguments The command-line arguments specified after the command.
     *
     * @since 3.00
     */
    protected CommandLine(String command, final String[] arguments) {
        this.arguments = arguments;
        if (command == null) {
            command = "java " + getClass().getName();
        }
        this.command = command;
        /*
         * Tries to get the number of columns in the terminal windows. Note: Unix users
         * may need to do "export COLUMNS" in their shell for getting this code to work.
         */
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
    }

    /**
     * Converts the given value to an object of the given type. The default implementation
     * delegates the work to the {@linkplain ConverterRegistry#system() system converter}.
     * Subclasses can override this method if they need to convert values in a particular
     * way.
     *
     * @param  <T>   The destinatipn type.
     * @param  value The string value to convert.
     * @param  type  The destination type.
     * @return The converted value.
     * @throws NonconvertibleObjectException if the value can't be converted.
     *
     * @since 3.00
     */
    protected <T> T convert(final String value, final Class<T> type) throws NonconvertibleObjectException {
        return ConverterRegistry.system().converter(String.class, type).convert(value);
    }

    /**
     * Sets the value of fields annotated with {@link Option} from the command-lines arguments. If
     * this method fails because of some user error (e.g. if a mandatory option is not provided)
     * or some other external conditions (e.g. an {@link IOException}), then it prints a short
     * error message and invokes {@link #exit} with one of the {@code EXIT_CODE} constants.
     */
    final void initialize() {
        if (lineSeparator == null) {
            lineSeparator = System.getProperty("line.separator", "\n");
        }
        if (arguments != null) {
            arguments = arguments.clone();
        } else {
            arguments = new String[0];
        }
        Exception status = assignValues(getClass());
        /*
         * At this point, all fields should have been assigned. We are now able to create the
         * writers using the given encoding, if any. We assing only the reader and writers if
         * they are null, since the subclass constructor may have customized its input/output
         * by providing explicit instances of Reader/Writer.
         */
        final Console console = System.console();
        if (console != null && encoding == null) {
            if (in  == null) in  = console.reader();
            if (out == null) out = console.writer();
            if (err == null) err = console.writer();
        } else {
            if (in == null) {
                final InputStreamReader is;
                if (encoding != null) {
                    is = new InputStreamReader(System.in, encoding);
                } else {
                    is = new InputStreamReader(System.in);
                }
                in = new LineNumberReader(is);
            }
            if (out == null) out = writer(System.out);
            if (err == null) err = writer(System.err);
        }
        if (colors == null) {
            colors = (console != null) && X364.isSupported();
        }
        if (encoding == null) {
            encoding = Charset.defaultCharset();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        /*
         * Arguments consumed have been set to null. Now pack the remaining arguments
         * and ensure that none of them starts with the prefix used for options.
         */
        int count = 0;
        for (int i=0; i<arguments.length; i++) {
            String arg = arguments[i];
            if (arg != null) {
                arguments[count++] = arg;
                if (status == null) {
                    arg = arg.trim();
                    if (arg.startsWith(OPTION_PREFIX)) {
                        status = new IllegalArgumentException(error(
                                Errors.Keys.UNKNOW_PARAMETER_$1, arg));
                    }
                }
            }
        }
        arguments = XArrays.resize(arguments, count);
        Logging.GEOTOOLKIT.forceMonolineConsoleOutput(debug ? Level.FINER : null);
        /*
         * At this point we are done. If we got an error in the process, print an error
         * message and invoke exit. We finished the object construction before to invoke
         * exit in case the subclass overrides the exit method and choose to inspect the
         * fields (for example in order to print a better diagnostic).
         */
        if (status != null) {
            printException(status);
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
    }

    /**
     * Wraps the given stream in a {@link PrintWriter} using the user-specified
     * {@linkplain #encoding}.
     *
     * @param stream The stream to wrap.
     * @return The writer.
     */
    private PrintWriter writer(final OutputStream stream) {
        if (encoding != null) {
            return new PrintWriter(new OutputStreamWriter(stream, encoding));
        } else {
            return new PrintWriter(stream);
        }
    }

    /**
     * Convenience method for formatting a localized sentence.
     */
    private String vocabulary(final int key, final Object param) {
        return Vocabulary.getResources(locale).getString(key, param);
    }

    /**
     * Convenience method for formatting a localized error message.
     */
    private String error(final int key, final Object param) {
        return Errors.getResources(locale).getString(key, param);
    }

    /**
     * Convenience method for formatting a localized error message.
     */
    private String error(final int key, final Object param1, final Object param2) {
        return Errors.getResources(locale).getString(key, param1, param2);
    }

    /**
     * Assigns values to every fields declared in the given class. Fields in parent classes
     * are assigned first. In case of failure, all remainding fields are still processed (so
     * we can hopefully build a output stream with the user's encoding) before to report the
     * error.
     *
     * @param  classe The class in which to look for declared fields.
     * @return A non-null exception in case of error, or {@code null} on success.
     */
    private Exception assignValues(final Class<?> classe) {
        Exception status = null;
        final Class<?> parent = classe.getSuperclass();
        if (CommandLine.class.isAssignableFrom(parent)) {
            final Exception s = assignValues(parent);
            if (status == null) {
                status = s;
            }
        }
        /*
         * At this point, the fields have been set for all parent classes. Now set the
         * field values for the class given in argument to this method.
         */
        for (final Field field : classe.getDeclaredFields()) {
            final Option option = field.getAnnotation(Option.class);
            if (option == null) {
                continue;
            }
            String name = option.name().trim();
            if (name.length() == 0) {
                name = field.getName();
            }
            name = OPTION_PREFIX + name;
            /*
             * At this point the name is final. Now get the associated value. In case of
             * failure, we take note of the failure cause but continue to initialize other
             * fields. The error will be reported only after we are done, so we can report
             * the error in appropriate locale and subclass can prints more advanced diagnostic.
             */
            final Object value;
            Class<?> type = field.getType();
            if (Boolean.TYPE.equals(type)) {
                value = isEnabled(name);
            } else {
                type = Classes.primitiveToWrapper(type);
                final String text;
                try {
                    text = valueOf(name);
                } catch (IllegalArgumentException exception) {
                    if (status == null) {
                        status = exception;
                    }
                    continue;
                }
                if (text == null && option.mandatory() && status == null) {
                    status = new IllegalArgumentException(error(
                            Errors.Keys.MISSING_PARAMETER_$1, name));
                    continue;
                }
                if (type.isAssignableFrom(String.class)) {
                    value = text;
                } else try {
                    value = convert(text, type);
                } catch (NonconvertibleObjectException exception) {
                    if (status == null) {
                        status = exception;
                    }
                    continue;
                }
            }
            /*
             * The value has been calculated. Now try to set it. If we fail, we consider
             * that as a programing error so we thrown an exception rather than build a
             * status object.
             */
            if (value != null) {
                field.setAccessible(true);
                try {
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    throw new UnsupportedOperationException(e);
                }
            }
        }
        return status;
    }

    /**
     * Returns {@code true} if the specified flag is set on the command line. This method
     * should be called exactly once for each flag. Second invocation for the same flag will
     * returns {@code false}, unless the same flag appears many times on the command line.
     *
     * @param  name The flag name.
     * @return {@code true} if this flag appears on the command line, or {@code false} otherwise.
     */
    private boolean isEnabled(final String name) {
        for (int i=0; i<arguments.length; i++) {
            String arg = arguments[i];
            if (arg!=null) {
                arg = arg.trim();
                if (arg.equalsIgnoreCase(name)) {
                    arguments[i] = null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an optional string value from the command line. This method should be called
     * exactly once for each parameter. Second invocation for the same parameter will returns
     * {@code null}, unless the same parameter appears many times on the command line.
     * <p>
     * Paramaters may be instructions like "-encoding cp850" or "-encoding=cp850".
     * Both forms (with or without "=") are accepted. Spaces around the '=' character,
     * if any, are ignored.
     *
     * @param  name The parameter name (e.g. {@code "-encoding"}). Name are case-insensitive.
     * @return The parameter value, of {@code null} if there is no parameter given for the
     *         specified name.
     */
    private String valueOf(final String name) {
        for (int i=0; i<arguments.length; i++) {
            String arg = arguments[i];
            if (arg != null) {
                arg = arg.trim();
                String value = "";
                int split = arg.indexOf('=');
                if (split >= 0) {
                    value = arg.substring(split+1).trim();
                    arg = arg.substring(0, split).trim();
                }
                if (arg.equalsIgnoreCase(name)) {
                    arguments[i] = null;
                    if (value.length() != 0) {
                        return value;
                    }
                    while (++i < arguments.length) {
                        value = arguments[i];
                        arguments[i] = null;
                        if (value == null) {
                            break;
                        }
                        value = value.trim();
                        if (split >= 0) {
                            return value;
                        }
                        if (!value.equals("=")) {
                            return value.startsWith("=") ? value.substring(1).trim() : value;
                        }
                        split = 0;
                    }
                    throw new IllegalArgumentException(error(
                            Errors.Keys.MISSING_PARAMETER_VALUE_$1, arg));
                }
            }
        }
        return null;
    }

    /**
     * Runs the command line. The default implementation search for a no-argument method annotated
     * with {@link Action} and having a name matching the first argument which is not an option.
     * If no action is given or if it was not recognized, then {@link #unknownAction(String)}
     * method is invoked.
     * <p>
     * This method should be invoked by the {@code main} method of subclasses as below:
     *
     * {@preformat java
     *     public static void main(String[] arguments) {
     *         CommandLine cmd = new MyCommands(arguments);
     *         cmd.run();
     *     }
     * }
     */
    @Override
    public void run() {
        initialize();
        if (arguments == null || arguments.length == 0) {
            unknownAction(null);
            return;
        }
        final String[] old = arguments;
        final String action = old[0].trim();
        arguments = XArrays.remove(old, 0, 1);
        Class<?> classe = getClass();
        do {
            for (final Method method : classe.getDeclaredMethods()) {
                final Action candidate = method.getAnnotation(Action.class);
                if (candidate == null) {
                    continue;
                }
                String name = candidate.name().trim();
                if (name.length() == 0) {
                    name = method.getName();
                }
                if (!action.equalsIgnoreCase(name)) {
                    continue;
                }
                /*
                 * Found the method. Check if the number of remaining arguments is inside the
                 * expected range. If so run the method immediately and return. Otherwise print
                 * an error message and exit. In any case we are not going to continue the loop.
                 */
                final int count = arguments.length;
                int limit = candidate.minimalArgumentCount();
                if (count < limit) {
                    err.println(error(Errors.Keys.TOO_FEW_ARGUMENTS_$2, limit, count));
                    exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                    return;
                }
                limit = candidate.maximalArgumentCount();
                if (count > limit) {
                    err.println(error(Errors.Keys.TOO_MANY_ARGUMENTS_$2, limit, count));
                    exit(ILLEGAL_ARGUMENT_EXIT_CODE);
                    return;
                }
                method.setAccessible(true);
                try {
                    method.invoke(this, (Object[]) null);
                } catch (IllegalAccessException e) {
                    // Should not happen since we have invoked setAccessible(true).
                    printException(e);
                    exit(INTERNAL_ERROR_EXIT_CODE);
                } catch (InvocationTargetException e) {
                    final int code;
                    final Throwable cause = e.getCause();
                    if (cause instanceof IOException) {
                        code = IO_EXCEPTION_EXIT_CODE;
                    } else if (cause instanceof SQLException) {
                        code = SQL_EXCEPTION_EXIT_CODE;
                    } else {
                        code = INTERNAL_ERROR_EXIT_CODE;
                    }
                    printException(cause);
                    exit(code);
                }
                if (out != null) out.flush();
                if (err != null) err.flush();
                return;
            }
        } while (CommandLine.class.isAssignableFrom(classe = classe.getSuperclass()));
        arguments = old;
        unknownAction(action);
    }

    /**
     * Invoked when the {@link #run()} method didn't recognized the action given by the user.
     * If the user didn't provided any action at all, then {@code action} is {@code null}.
     * Otherwise {@code action} is the user-provided action which was not recognized.
     * <p>
     * The default implementation prints a summary if {@code action} is null, or an error
     * message if non-null, then {@linkplain #exit exit}.
     *
     * @param action The unrecognized action, or {@code null} if the user didn't supplied
     *        any action.
     *
     * @since 3.00
     */
    protected void unknownAction(final String action) {
        if (action == null) {
            summary();
            exit(0);
        } else {
            err.println(error(Errors.Keys.UNKNOW_COMMAND_$1, action));
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user didn't asked for any action. The default implemention prints
     * a summary of available {@linkplain Action actions} and {@linkplain Option options}.
     * Subclasses can override this method if they want to print different informations.
     *
     * @since 3.00
     */
    protected void summary() {
        final Descriptions resources = Descriptions.getResources(locale);
        out.println(resources.getString(Descriptions.Keys.COMMAND_USAGE_$1, command));
        final Vocabulary vocabulary = Vocabulary.getResources(locale);
        final Set<String> options = new TreeSet<String>();
        boolean action = true;
        do {
            final Class<? extends Annotation> at = (action) ? Action.class : Option.class;
            Class<?> c = getClass();
            do for (AccessibleObject member : action ? c.getDeclaredMethods() : c.getDeclaredFields()) {
                final Annotation option = member.getAnnotation(at);
                if (option != null) {
                    String name = action ? ((Action) option).name() : ((Option) option).name();
                    name = name.trim();
                    if (name.length() == 0) {
                        name = ((Member) member).getName();
                    }
                    options.add(name);
                }
            } while (CommandLine.class.isAssignableFrom(c = c.getSuperclass()));
            out.print(vocabulary.getString(action ? Vocabulary.Keys.COMMANDS : Vocabulary.Keys.OPTIONS));
            String separator = ": ";
            String next = action ? " | " : ", ";
            for (final String option : options) {
                out.print(separator);
                if (!action) {
                    out.print(OPTION_PREFIX);
                }
                out.print(option);
                separator = next;
            }
            options.clear();
            out.println();
        } while ((action = !action) == false);
        out.println(resources.getString(Descriptions.Keys.USE_HELP_COMMAND));
    }

    /**
     * Invoked when the user asked the {@code "version"} action. The default implementation
     * prints version number and system informations. Subclasses can override this method
     * if they want to print more informations.
     */
    @Action(maximalArgumentCount=0)
    protected void version() {
        final String bold, faint, normal;
        if (colors) {
            bold   = X364.BOLD.sequence();
            faint  = X364.FAINT.sequence();
            normal = X364.NORMAL.sequence();
        } else {
            bold = faint = normal = "";
        }
        final PrintWriter out = this.out;
        out.print("Geotoolkit.org ");
        out.println(Version.GEOTOOLKIT);
        out.print(vocabulary(Vocabulary.Keys.JAVA_VERSION_$1, System.getProperty("java.version")));
        out.print(faint);
        out.print(" (");
        out.print(vocabulary(Vocabulary.Keys.JAVA_VENDOR_$1, System.getProperty("java.vendor")));
        out.print(')');
        out.println(normal);
        out.print(vocabulary(Vocabulary.Keys.OS_NAME_$1, System.getProperty("os.name")));
        out.print(faint);
        out.print(" (");
        out.print(vocabulary(Vocabulary.Keys.OS_VERSION_$2, new String[] {
            System.getProperty("os.version"), System.getProperty("os.arch")
        }));
        out.print(')');
        out.println(normal);
        /*
         * Test for the presence of extensions for which the class may not be on the classpath:
         * JavaDB, JAI, Image I/O extensions for JAI.
         */
        out.println();
        out.print(bold);
        out.print("Extensions:");
        out.println(normal);
        out.flush(); // For allowing user to see what we have done so far while he is waiting.
        final Vocabulary resources = Vocabulary.getResources(locale);
        for (int i=0; i<3; i++) {
            String header = null;
            Object result = null; // String on success, Throwable on error.
            try {
                switch (i) {
                    default: {
                        throw new AssertionError(i);
                    }
                    case 0: {
                        header = "Embedded Database";
                        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                        final Driver d = DriverManager.getDriver("jdbc:derby:");
                        result = Loggings.getResources(locale).getString(
                                Loggings.Keys.JDBC_DRIVER_VERSION_$3, "Derby",
                                d.getMajorVersion(), d.getMinorVersion());
                        break;
                    }
                    case 1: {
                        header = "Java Advanced Imaging";
                        result = String.valueOf(Class.forName("javax.media.jai.JAI")
                                .getMethod("getBuildVersion").invoke(null, (Object[]) null));
                        break;
                    }
                    case 2: {
                        header = "Image I/O extensions";
                        final Package p = Package.getPackage("com.sun.media.jai.operator");
                        if (p != null) {
                            result = resources.getString(Vocabulary.Keys.VERSION_$1,
                                    p.getImplementationVersion());
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                result = e;
            }
            out.print(header);
            out.print(':');
            out.print(Utilities.spaces(22 - header.length()));
            if (result instanceof String) {
                out.print(faint);
                out.print((String) result);
                out.println(normal);
            } else {
                out.print(resources.getString(Vocabulary.Keys.NOT_INSTALLED));
                if (result != null) {
                    out.print(faint);
                    out.print(" (");
                    out.print(Classes.getShortClassName(result));
                    out.print(')');
                    out.print(normal);
                }
                out.println();
            }
        }
    }

    /**
     * Invoked when the user asked the {@code "help"} action. The default implementation prints
     * a description of this command and all arguments to the {@linkplain #out standard output}.
     * The description is read from a {@linkplain Properties properties} file of the same name
     * than the subclass, using {@link ResourceBundle} with the current {@linkplain #locale}.
     * Then this class prints the following:
     * <p>
     * <ul>
     *   <li>The value of the {@code Description} key.</li>
     *   <li>The name of every {@link Action}s found in the subclass and the
     *       corresponding value obtained from the properties file.</li>
     *   <li>The name of every {@link Option}s found in the subclass and the
     *       corresponding value obtained from the properties file.</li>
     * </ul>
     */
    @Action(maximalArgumentCount=0)
    protected void help() {
        out.println(bold(Descriptions.getResources(locale).getString(
                    Descriptions.Keys.COMMAND_USAGE_$1, command)));
        out.println();

        // Writer to be used for the remainding of this method, which trim trailing spaces.
        final Writer out = new LineWriter(this.out, lineSeparator);
        final TableWriter table = new TableWriter(out, "");
        table.setMultiLinesCells(true);

        // Lists of actions and options to be collected before to be printed.
        final Map<String,Map.Entry<String,String>> actions, options;
        actions = new TreeMap<String,Map.Entry<String,String>>();
        options = new TreeMap<String,Map.Entry<String,String>>();

        // Cached objects.
        final StringBuilder buffer = new StringBuilder();
        boolean descriptionDone = false;
        String mandatory = null;

        Class<?> classe = getClass();
        do {
            final ResourceBundle resources = ResourceBundle.getBundle(classe.getName(), locale);
            if (!descriptionDone) try {
                final LineWrapWriter wrapping = new LineWrapWriter(out, lineLength);
                wrapping.write(getString(resources, "Description", buffer));
                wrapping.write(lineSeparator);
                wrapping.write(lineSeparator);
                descriptionDone = true;
            } catch (IOException e) {
                printException(e);
                exit(IO_EXCEPTION_EXIT_CODE);
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
                if (name.length() == 0) {
                    name = method.getName();
                }
                if (actions.containsKey(name)) {
                    continue;
                }
                String label = name;
                buffer.setLength(0);
                color(buffer, X364.BOLD);
                color(buffer, X364.FOREGROUND_GREEN);
                buffer.append(label);
                color(buffer, X364.RESET);
                label = buffer.toString();
                final String description = description(resources, name, action.examples(), buffer);
                actions.put(name, new AbstractMap.SimpleEntry<String,String>(label, description));
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
                if (name.length() == 0) {
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
                color(buffer, X364.BOLD);
                color(buffer, X364.FOREGROUND_GREEN);
                buffer.append(OPTION_PREFIX).append(label);
                color(buffer, X364.NORMAL);
                Class<?> type = field.getType();
                if (!Boolean.TYPE.isAssignableFrom(type)) {
                    type = Classes.primitiveToWrapper(type);
                    buffer.append('=');
                    if (Boolean.class.isAssignableFrom(type)) {
                        buffer.append("on|off");
                    } else {
                        buffer.append(Number.class.isAssignableFrom(type) ? 'N' : 'S');
                    }
                }
                color(buffer, X364.FOREGROUND_DEFAULT);
                if (option.mandatory()) {
                    if (mandatory == null) {
                        mandatory = Vocabulary.getResources(locale)
                                .getString(Vocabulary.Keys.MANDATORY).toLowerCase(locale);
                    }
                    buffer.append("\n  ");
                    color(buffer, X364.FOREGROUND_GREEN);
                    buffer.append('(').append(mandatory).append(')');
                    color(buffer, X364.FOREGROUND_DEFAULT);
                }
                label = buffer.toString();
                /*
                 * At this point, the label is final. Build the description
                 * string and save the (name,description) pair in the map.
                 */
                final String description = description(resources, name, option.examples(), buffer);
                options.put(name, new AbstractMap.SimpleEntry<String,String>(label, description));
            }
        } while (CommandLine.class.isAssignableFrom(classe = classe.getSuperclass()));
        /*
         * At this point the maps are built and sorted.
         * Now print them, followed by examples.
         */
        final Vocabulary resources = Vocabulary.getResources(locale);
        try {
            if (!actions.isEmpty()) {
                print(table, resources.getLabel(Vocabulary.Keys.COMMANDS), actions.values());
            }
            if (!options.isEmpty()) {
                print(table, resources.getLabel(Vocabulary.Keys.OPTIONS), options.values());
            }
            table.flush();
        } catch (IOException e) {
            // Should never happen since we are flushing to a PrintWriter.
            printException(e);
            exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
        examples(buffer);
    }

    /**
     * Formats the description of an action or an option. The description is completed
     * with the examples, if any.
     *
     * @param  resources The resource bundle to use for fetching the description.
     * @param  name      The action name or the option name.
     * @param  examples  The examples, or an empty array if none.
     * @param  buffer    Temporary working buffer.
     * @return The description.
     */
    private String description(final ResourceBundle resources, final String name,
                               final String[] examples, final StringBuilder buffer)
    {
        String description = getString(resources, name, buffer);
        if (examples.length != 0) {
            buffer.setLength(0);
            buffer.append(description).append(lineSeparator);
            color(buffer, X364.FAINT);
            buffer.append(examplesLabel()).append('"');
            for (int i=0; i<examples.length; i++) {
                if (i != 0) {
                    buffer.append("\", \"");
                }
                buffer.append(examples[i]);
            }
            buffer.append("\".");
            color(buffer, X364.NORMAL);
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
     * @param  buffer    Temporary working buffer.
     * @return The localized resources.
     */
    private String getString(final ResourceBundle bundle, final String key, final StringBuilder buffer) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            buffer.setLength(0);
            color(buffer, X364.BACKGROUND_RED);
            buffer.append(e.getLocalizedMessage());
            color(buffer, X364.BACKGROUND_DEFAULT);
            return buffer.toString();
        }
    }

    /**
     * Applies the given color to the specified buffer only if colors are enabled.
     *
     * @param buffer The buffer where to add the color.
     * @param color  The color to add.
     */
    private void color(final StringBuilder buffer, final X364 color) {
        if (Boolean.TRUE.equals(colors)) {
            buffer.append(color.sequence());
        }
    }

    /**
     * Returns the given text as bold characters, if colors are enabled.
     *
     * @param  text The text to get in bold characters.
     * @return The given text in bold characters, or {@code text} unchanged if
     *         colors are not enabled.
     */
    final String bold(String text) {
        if (Boolean.TRUE.equals(colors)) {
            text = X364.BOLD.sequence() + text + X364.NORMAL.sequence();
        }
        return text;
    }

    /**
     * Prints the specified options to the standard output stream.
     *
     * @param  table   The table to write to.
     * @param  title   The title to print before the arguments.
     * @param  options The optional or mandatory arguments to list.
     * @throws IOException if an error occurs while writting to the output stream.
     */
    private void print(final TableWriter table, String title,
            final Collection<Map.Entry<String,String>> options) throws IOException
    {
        title = bold(title.trim());
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
            examples = Vocabulary.getResources(locale).getLabel(Vocabulary.Keys.EXAMPLES);
        }
        return examples;
    }

    /**
     * Prints examples.
     *
     * @param buffer A temporary buffer.
     */
    private final void examples(final StringBuilder buffer) {
        final Map<String,String> examples = examples();
        if (examples == null || examples.isEmpty()) {
            return;
        }
        /*
         * There is at least one example to print. Gets the list of ResourceBundles that may
         * contain their descriptions. Note that we should always found at least one bundle,
         * the CommandLine.properties one. If we really found none of them, prints the first
         * exception and terminate this method.
         */
        final List<ResourceBundle> resources = new ArrayList<ResourceBundle>();
        MissingResourceException failure = null;
        for (Class<?> c=getClass(); CommandLine.class.isAssignableFrom(c); c=c.getSuperclass()) {
            try {
                resources.add(ResourceBundle.getBundle(c.getName(), locale));
            } catch (MissingResourceException e) {
                if (failure == null) {
                    failure = e;
                }
            }
        }
        if (resources.isEmpty()) {
            printException(failure);
            return;
        }
        /*
         * Now prints the examples.
         */
        out.println(bold(examplesLabel().trim()));
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
                description = getString(resources.get(0), key, buffer);
            }
            buffer.setLength(0);
            color(buffer, X364.FOREGROUND_GREEN);
            buffer.append(command).append(' ').append(bold(entry.getValue()));
            color(buffer, X364.FOREGROUND_DEFAULT);
            try {
                indented.write(lineSeparator);
                indented.write(buffer.toString());
                indented.write(lineSeparator);
                wrapping.write(description);
                wrapping.write(lineSeparator);
            } catch (IOException e) {
                printException(e);
                exit(IO_EXCEPTION_EXIT_CODE);
                return;
            }
        }
    }

    /**
     * Returns a set of examples to be displayed after the help screen. This method is
     * invoked by {@link #help}. Values in the returned map are examples of parameters
     * to be given on the command line. The corresponding keys must also be keys in the
     * same {@linkplain Properties properties} file than the one used by the {@code help()}
     * method (see its javadoc for details).
     * <p>
     * The default implementation returns an empty map. Subclasses should override this
     * method if they can provide a set of examples.
     *
     * @return A set of examples to be printed after the help screen.
     *
     * @since 3.00
     */
    Map<String,String> examples() {
        return Collections.emptyMap();
    }

    /**
     * Invoked when an exception occured because of user's error. The default implementation
     * prints only a summary of the given exception, except if {@link #debug} is {@code true}
     * in which case the full stack trace is printed.
     * <p>
     * The exception is expected to be a user's error, not a programming error (the later are
     * propagated like ordinary exceptions; they do not pass through this method). For example
     * this method is invoked if a {@link java.io.FileNotFoundException} occured while trying
     * to open a file given on the command line. Callers are expected to invoke {@link #exit}
     * after this method.
     *
     * @param exception The exception that forced the exit.
     *
     * @since 3.00
     */
    protected void printException(final Throwable exception) {
        out.flush();
        err.flush();
        if (debug) {
            exception.printStackTrace(err);
        } else {
            final StringBuilder buffer = new StringBuilder();
            final String type = Classes.getShortClassName(exception);
            String message = exception.getLocalizedMessage();
            if (message == null) {
                message = vocabulary(Vocabulary.Keys.NO_DETAILS_$1, type);
            } else {
                color(buffer, X364.FOREGROUND_RED);
                color(buffer, X364.BOLD);
                buffer.append(type).append(": ");
                color(buffer, X364.RESET);
            }
            err.println(buffer.append(message));
        }
    }

    /**
     * Invoked when an error occured while processing the command-line arguments or
     * during action execution. The default implementation flushs the streams and invokes
     * {@link System#exit}. Subclasses can override this method if they want to perform
     * some other action.
     * <p>
     * Callers should not assume that JVM will stop execution after this method call, because
     * the default behavior may be overriden in some cases. Callers should exit their method
     * (usually with a {@code return} statement) immediately after the call to this method.
     * <p>
     * Note that this method may be invoked at any time, including construction time.
     * It should not assume that every fields have been correctly assigned.
     *
     * @param code One of the {@code EXIT_CODE} constants.
     *
     * @since 3.00
     */
    protected void exit(final int code) {
        if (out != null) out.flush();
        if (err != null) err.flush();
        if (!consoleRunning) {
            System.exit(code);
        }
    }
}
