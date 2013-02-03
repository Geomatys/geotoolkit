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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;

import org.geotoolkit.io.X364;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Debug;


/**
 * Base class for command line tools. Subclasses shall define fields annotated with {@link Option}
 * and/or methods annotated with {@link Action}. The annotated fields will be initialized by the
 * {@link #version()} method.
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
 *     <td>Sets the console encoding ({@code "UTF-8"}, {@code "ISO-8859-1"}, <i>etc.</i>)
 *         for application input and output. This value has no impact on data, but may improve
 *         the output quality. This is not needed on Linux terminal using UTF-8 encoding (tip:
 *         the <cite>terminus font</cite> gives good results).</td>
 *   </tr><tr>
 *     <td nowrap bgcolor="#EEEEFF"><b>{@code --locale}</b>=<var>lc</var></td>
 *     <td>Set the locale for string, number and date formatting
 *         ({@code "fr"} for French, <i>etc.</i>).</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.15
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
    static final String OPTION_PREFIX = "--";

    /**
     * {@code true} if the {@code --debug} option has been passed on the command line.
     *
     * @since 3.00
     */
    @Debug
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
     * The remaining arguments after all option values have been assigned to the fields.
     */
    protected String[] arguments;

    /**
     * The command used for launching the application.
     */
    private final String command;

    /**
     * Do not cause a failure if mandatory options are missing. This is a
     * special case which occurs only if the action is "help" or "version".
     */
    transient boolean ignoreMandatoryOption;

    /**
     * {@code true} if the {@link #version} method is invoked recursively from {@link InteractiveConsole}.
     */
    transient boolean consoleRunning;

    /**
     * Creates a new {@code CommandLine} instance. This constructor keep a reference to
     * the given arguments, but does not parse them yet. The arguments are parsed when
     * {@link #version()} is invoked.
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
            command = "java " + getClass().getCanonicalName();
        }
        this.command = command;
    }

    /**
     * Converts the given value to an object of the given type. The default implementation
     * delegates the work to the {@linkplain ConverterRegistry#system() system converter}.
     * Subclasses can override this method if they need to convert values in a particular
     * way.
     *
     * @param  <T>   The destination type.
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
        if (arguments != null) {
            arguments = arguments.clone();
        } else {
            arguments = Strings.EMPTY;
        }
        Exception status = assignValues(getClass());
        /*
         * At this point, all fields should have been assigned. We are now able to create the
         * writers using the given encoding, if any. We assing only the reader and writers if
         * they are null, since the subclass constructor may have customized its input/output
         * by providing explicit instances of Reader/Writer.
         */
        final boolean explicitEncoding = (encoding != null);
        final Console console = System.console();
        if (!explicitEncoding && console != null) {
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
        /*
         * Set unassigned fields to default values.
         */
        if (colors == null) {
            colors = (console != null) && X364.isSupported();
        }
        if (encoding == null) {
            encoding = Charset.defaultCharset();
        }
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.DISPLAY);
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
                                Errors.Keys.UNKNOWN_PARAMETER_$1, arg));
                    }
                }
            }
        }
        arguments = ArraysExt.resize(arguments, count);
        Logging.GEOTOOLKIT.forceMonolineConsoleOutput(debug ? Level.FINER : null);
        if (explicitEncoding) {
            for (final Handler handler : Logging.getLogger(Logging.GEOTOOLKIT.name).getHandlers()) {
                if (handler.getClass() == ConsoleHandler.class) try {
                    ((ConsoleHandler) handler).setEncoding(encoding.name());
                } catch (UnsupportedEncodingException e) {
                    // Should not happen.
                    Logging.unexpectedException(CommandLine.class, "initialize", e);
                }
            }
        }
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
            return new PrintWriter(new OutputStreamWriter(stream, encoding), true);
        } else {
            return new PrintWriter(stream, true);
        }
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
     * are assigned first. In case of failure, all remanding fields are still processed (so
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
            status = assignValues(parent);
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
            if (name.isEmpty()) {
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
                type = Numbers.primitiveToWrapper(type);
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
                    if (!ignoreMandatoryOption) {
                        status = new IllegalArgumentException(error(
                                Errors.Keys.NO_PARAMETER_$1, name));
                    }
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
                    if (!value.isEmpty()) {
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
                            Errors.Keys.NO_PARAMETER_VALUE_$1, arg));
                }
            }
        }
        return null;
    }

    /**
     * Runs the command line. The default implementation searches for a no-argument method annotated
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
        /*
         * Special case performed before to parse the arguments, because we want
         * those actions to work even if mandatory parameters are not provided.
         */
        if (arguments == null || arguments.length == 0) {
            ignoreMandatoryOption = true;
        } else if (arguments.length == 1) {
            final String action = arguments[0];
            if (action != null) {
                if (action.equalsIgnoreCase("help") || action.equalsIgnoreCase("version")) {
                    ignoreMandatoryOption = true;
                }
            }
        }
        /*
         * General case: parses the options (throwing an exception if some argument are
         * invalid, or if a mandatory argument is missing), then execute the action.
         */
        initialize();
        if (arguments == null || arguments.length == 0) {
            unknownAction(null);
            return;
        }
        final String[] old = arguments;
        final String action = old[0].trim();
        arguments = ArraysExt.remove(old, 0, 1);
        Class<?> classe = getClass();
        do {
            for (final Method method : classe.getDeclaredMethods()) {
                final Action candidate = method.getAnnotation(Action.class);
                if (candidate == null) {
                    continue;
                }
                String name = candidate.name().trim();
                if (name.isEmpty()) {
                    name = method.getName();
                }
                if (!action.equalsIgnoreCase(name)) {
                    continue;
                }
                /*
                 * Found the method. Check if the number of remaining arguments is inside the
                 * expected range. If so version the method immediately and return. Otherwise print
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
     * Invoked when the {@link #version()} method didn't recognized the action given by the user.
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
            err.println(error(Errors.Keys.UNKNOWN_COMMAND_$1, action));
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
    }

    /**
     * Invoked when the user didn't asked for any action. The default implementation prints
     * a summary of available {@linkplain Action actions} and {@linkplain Option options}.
     * Subclasses can override this method if they want to print different informations.
     *
     * @since 3.00
     */
    protected void summary() {
        final PrintWriter out = this.out;
        final Descriptions resources = Descriptions.getResources(locale);
        out.println(resources.getString(Descriptions.Keys.COMMAND_USAGE_$1, command));
        final Vocabulary vocabulary = Vocabulary.getResources(locale);
        final Set<String> options = new TreeSet<>();
        boolean action = true;
        do {
            final Class<? extends Annotation> at = (action) ? Action.class : Option.class;
            Class<?> c = getClass();
            do for (AccessibleObject member : action ? c.getDeclaredMethods() : c.getDeclaredFields()) {
                final Annotation option = member.getAnnotation(at);
                if (option != null) {
                    String name = action ? ((Action) option).name() : ((Option) option).name();
                    name = name.trim();
                    if (name.isEmpty()) {
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
        VersionAction.version(out, colors, locale);
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
        new HelpAction(this).help(command);
    }

    /**
     * Applies the given color to the specified buffer only if colors are enabled.
     *
     * @param buffer The buffer where to add the color.
     * @param color  The color to add.
     */
    final void color(final StringBuilder buffer, final X364 color) {
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
     * Invoked when an exception occurred because of user's error. The default implementation
     * prints only a summary of the given exception, except if {@link #debug} is {@code true}
     * in which case the full stack trace is printed.
     * <p>
     * The exception is expected to be a user's error, not a programming error (the later are
     * propagated like ordinary exceptions; they do not pass through this method). For example
     * this method is invoked if a {@link java.io.FileNotFoundException} occurred while trying
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
                message = Vocabulary.getResources(locale).getString(Vocabulary.Keys.NO_DETAILS_$1, type);
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
     * Invoked when an error occurred while processing the command-line arguments or
     * during action execution. The default implementation flushs the streams and invokes
     * {@link System#exit}. Subclasses can override this method if they want to perform
     * some other action.
     * <p>
     * Callers should not assume that JVM will stop execution after this method call, because
     * the default behavior may be overridden in some cases. Callers should exit their method
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
