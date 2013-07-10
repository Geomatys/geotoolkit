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
import java.text.*;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.vecmath.MismatchedSizeException;

import org.geotoolkit.io.X364;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.internal.InternalUtilities;

import static org.geotoolkit.io.X364.*;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Base class for applications executing instructions from the {@linkplain Console console}.
 * If there is no console attached to the current JVM, then this class will read instructions from
 * {@linkplain System#in standard input stream} and sent the results to the {@linkplain System#out
 * standard output stream}. But different streams can be explicitly specified at construction time.
 * <p>
 * Every empty lines, and every lines beginning with {@code "//"} or {@code "#"} characters,
 * are ignored. Other lines are sent to the {@link #execute execute} method, which subclasses
 * should override.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public abstract class InteractiveConsole implements Runnable {
    /**
     * The command lines, or {@code null} if none. If non-null, the actions defined
     * in this object will be available for execution by the {@link #execute} method.
     */
    private final CommandLine commands;

    /**
     * The console, or {@code null} if none.
     */
    private final Console console;

    /**
     * The input stream, or {@code null} if input should be read from the console.
     */
    private final LineNumberReader in;

    /**
     * The output stream.
     */
    protected final PrintWriter out;

    /**
     * The error stream.
     */
    protected final PrintWriter err;

    /**
     * The system default line separator.
     */
    private final String lineSeparator;

    /**
     * {@code true} if X3.64 colors are allowed on the output streams.
     */
    final boolean colors;

    /**
     * The locale. The default value is not necessarily the system default, since this locale is
     * used also for parsing numbers, dates, <i>etc</i>. The policy for the default value
     * may change in future version, so it is safer to not make this field public for now.
     */
    final Locale locale;

    /**
     * The format to use for reading and writing numbers.
     */
    final NumberFormat numberFormat;

    /**
     * The number separator in vectors. Usually {@code ,}, but could also be {@code ;}
     * if the coma is already used as the decimal separator.
     */
    final String numberSeparator;

    /**
     * The command-line prompt.
     */
    private String prompt;

    /**
     * The last command read, or {@code null} if none. The command is
     * typically a single line, but may also span more than one line.
     */
    private transient String command;

    /**
     * The character used for quoting texts, or 0 if none.
     */
    private char quote = 0;

    /**
     * List of caracters acceptable as opening bracket. The closing bracket must
     * be the character in the {@link #closingBrackets} array at the same index
     * than the opening bracket.
     */
    private char[] openingBrackets = ArraysExt.EMPTY_CHAR;

    /**
     * List of caracters acceptable as closing bracket.
     */
    private char[] closingBrackets = openingBrackets;

    /**
     * The last error that occurred while processing an instruction.
     * Used in order to print the stack trace on request.
     */
    private transient Exception lastError;

    /**
     * Set to {@code true} if {@link #stop()} was invoked.
     */
    private transient volatile boolean stop;

    /**
     * Creates a new instance using the {@linkplain Console console} if available, or the
     * parameters from the command line otherwise. If no command line is given, then this
     * constructor fallback on the {@linkplain System#in standard input stream}, {@linkplain
     * System#out standard output stream} and {@linkplain System#err error output stream}
     *
     * @param commands The command line, or {@code null} if none.
     *
     * @since 3.00
     */
    protected InteractiveConsole(final CommandLine commands) {
        this.commands = commands;
        console = System.console();
        if (console != null) {
            in  = null;
            out = err = console.writer();
            prompt = "Geotk \u25B6 ";
        } else if (commands != null) {
            in  = new LineNumberReader(commands.in);
            out = commands.out;
            err = commands.err;
        } else {
            in  = new LineNumberReader(new InputStreamReader(System.in));
            out = new PrintWriter(System.out, true);
            err = new PrintWriter(System.err, true);
        }
        if (commands != null) {
            locale = commands.locale;
            colors = Boolean.TRUE.equals(commands.colors);
        } else {
            locale = Locale.CANADA;
            colors = X364.isSupported();
        }
        lineSeparator   = System.lineSeparator();
        numberFormat    = NumberFormat.getNumberInstance(locale);
        numberSeparator = getNumberSeparator(numberFormat);
        ensureValid();
    }

    /**
     * Creates a new instance using the specified input stream.
     * The output and error stream still the system ones.
     *
     * @param in  The input stream (can not be null).
     *
     * @since 3.00
     */
    protected InteractiveConsole(final LineNumberReader in) {
        this(in, new PrintWriter(System.out, true));
    }

    /**
     * Creates a new instance using the specified input and output streams.
     * The error stream still the system one.
     *
     * @param in  The input stream (can not be null).
     * @param out The output stream (can not be null).
     *
     * @since 3.00
     */
    protected InteractiveConsole(final LineNumberReader in, final PrintWriter out) {
        commands = null;
        console  = null;
        this.in  = in;
        this.out = out;
        this.err = new PrintWriter(System.err, true);
        colors          = false;
        locale          = Locale.CANADA;
        lineSeparator   = System.lineSeparator();
        numberFormat    = NumberFormat.getNumberInstance(locale);
        numberSeparator = getNumberSeparator(numberFormat);
        ensureValid();
    }

    /**
     * Returns the character to use as a number separator. As a side effect,
     * this method also adjust the minimum and maximum digits.
     */
    private static String getNumberSeparator(final NumberFormat numberFormat) {
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(6);
        numberFormat.setMaximumFractionDigits(6);
        return String.valueOf(InternalUtilities.getSeparator(numberFormat)).intern();
    }

    /**
     * Ensures that the required fields are non-null.
     */
    private void ensureValid() {
        if (console == null) {
            ensureNonNull("in",  in);
        }
        ensureNonNull("out", out);
        ensureNonNull("err", err);
        ensureNonNull("lineSeparator", lineSeparator);
    }

    /**
     * Gives some hints about the expected syntax of commands. By default this class performs
     * no analysis on the input strings. If this method is given non-empty lists of bracket
     * characters, then the behavior of this class regarding input lines is modified as below:
     * <p>
     * <ul>
     *   <li>If a line has an opening bracket but no matching closing bracket, then this class
     *       will ask for more lines until all brackets are closed.</li>
     *   <li>The above rule do not apply to occurrences of brackets inside quoted texts.</li>
     * </ul>
     *
     * @param openingBrackets List of caracters acceptable as opening bracket. The closing bracket
     *        must be the character in the {@code closingBrackets} array at the same index than the
     *        opening bracket.
     * @param closingBrackets List of caracters acceptable as closing bracket.
     * @param quote The character used for quoting texts, or 0 if none.
     *
     * @since 3.00
     */
    protected void setSymbols(char[] openingBrackets, char[] closingBrackets, char quote) {
        ensureNonNull("openingBrackets", openingBrackets);
        ensureNonNull("closingBrackets", closingBrackets);
        if (openingBrackets.length != closingBrackets.length) {
            throw new MismatchedSizeException(Errors.format(
                    Errors.Keys.MISMATCHED_ARRAY_LENGTH_2, "openingBrackets", "closingBrackets"));
        }
        this.openingBrackets = openingBrackets.clone();
        this.closingBrackets = closingBrackets.clone();
        this.quote = quote;
    }

    /**
     * Sets the command-line prompt, which may be an empty string. This method does nothing
     * if this class is not using the {@linkplain Console console} attached to the JVM.
     *
     * @param prompt The new command-line prompt.
     */
    public void setPrompt(final String prompt) {
        if (console != null) {
            ensureNonNull("prompt", prompt);
            this.prompt = prompt;
        }
    }

    /**
     * Returns the command-line prompt, or {@code null} if there is no console. This method returns
     * a non-null value only if this class is using the {@linkplain Console console} attached to the
     * JVM.
     *
     * @return The command-line prompt, or {@code null} if there is no attached console.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Reads the next command. Empty lines and comment lines are skipped. If there
     * is no more line to read, then this method set the command to {@code null}.
     *
     * @return The value of the {@link #stop} flag.
     */
    private boolean read() {
        final char quote = this.quote; // Protect from changes.
        final char[] openingBrackets = this.openingBrackets;
        final char[] closingBrackets = this.closingBrackets;
        final int[] bracketCount = new int[Math.min(openingBrackets.length, closingBrackets.length)];
        int needClosingBracket = -1;
        boolean isQuoting = false;
        StringBuilder buffer = null;
        String line = null;
        boolean stop;
        while (!(stop = this.stop)) {
            if (console != null) {
                String p = "%d %c\u2026%c \u25B6 ";
                X364 color = FOREGROUND_CYAN;
                final Object[] args;
                if (isQuoting) {
                    args = new Object[] {1, quote, quote};
                } else if (needClosingBracket >= 0) {
                    args = new Object[] {
                        bracketCount[needClosingBracket],
                        openingBrackets[needClosingBracket],
                        closingBrackets[needClosingBracket]
                    };
                } else {
                    p = prompt;
                    args = new Object[0];
                    color = FOREGROUND_GREEN;
                }
                if (colors) {
                    p = color.sequence() + p + FOREGROUND_DEFAULT.sequence();
                }
                line = console.readLine(p, args);
            } else try {
                out.flush();
                line = in.readLine();
            } catch (IOException e) {
                throw new IOError(e);
            }
            if (line == null) {
                break;
            }
            line = line.trim();
            final int length = line.length();
            if (length == 0 || line.startsWith("//") || line.startsWith("#")) {
                // Empty or comment line. Ignore and ask for the next line.
                continue;
            }
            /*
             * At this point we have a line which is not commented-out. Now scan this line,
             * looking for quotes, opening or closing brackets. We will use this information
             * in order to determine if we need to read some additional lines.
             */
            for (int i=0; i<length; i++) {
                final char c = line.charAt(i);
                if (quote != 0 && c == quote) {
                    isQuoting = !isQuoting;
                    continue;
                }
                if (!isQuoting) {
                    for (int j=0; j<bracketCount.length; j++) {
                        if (bracketCount[j] != 0 && closingBrackets[j] == c) {
                            bracketCount[j]--;
                            break;
                        }
                        if (openingBrackets[j] == c) {
                            bracketCount[j]++;
                            break;
                        }
                    }
                }
            }
            int max = 0;
            needClosingBracket = -1;
            for (int i=0; i<bracketCount.length; i++) {
                if (bracketCount[i] > max) {
                    max = bracketCount[i];
                    needClosingBracket = i;
                }
            }
            if (!isQuoting && needClosingBracket < 0) {
                break;
            }
            /*
             * At this point we known that we need to read more lines. Put the line
             * that we have just read in a buffer before to go to the next line.
             */
            if (buffer == null) {
                buffer = new StringBuilder(line);
            } else {
                buffer.append(lineSeparator).append(line);
            }
        }
        if (buffer != null) {
            line = buffer.append(lineSeparator).append(line).toString();
        }
        command = line;
        return stop;
    }

    /**
     * Stops the {@link #run} method. This method can been invoked from any thread.
     * If a line is in process, it will be finished before the {@link #run} method
     * stops.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Executes all instructions from the console or the input stream. Lines are read until the
     * end of stream is reached ({@code [Ctrl-Z]} for input from the keyboard), or until the
     * {@link #stop()} method is invoked. Empty and comment lines are ignored.
     * <p>
     * Multilines may be concatenated in a single instruction if a bracket is open on a line
     * and closed only a few lines later. Once an instruction is assembled, it is given to the
     * {@link #execute execute} method. If an exception occurs during the call to {@code execute},
     * then the {@link #reportError reportError} method is invoked. The later can stop the execution
     * either by invoking the {@link #stop} method or by throwing an exception.
     */
    @Override
    public void run() {
        while (!read()) {
            if (command == null) {
                break;
            }
            try {
                execute(command);
            } catch (Exception exception) {
                reportError(exception);
            }
        }
        out.flush();
    }

    /**
     * Executes the given instruction. This method is invoked by {@link #run} for each line
     * (or group of lines) read fron the console or the input stream.
     * <p>
     * The default implementation recognizes the {@code stacktrace} and {@code exit}, and
     * prints an error message for all other instructions. Subclasses should override this
     * method is order to provide more functionalities.
     *
     * @param  instruction The instruction to execute.
     * @throws Exception if the instruction failed.
     */
    protected void execute(String instruction) throws Exception {
        if (instruction.equalsIgnoreCase("exit") || instruction.equalsIgnoreCase("quit")) {
            stop();
            return;
        }
        if (instruction.equalsIgnoreCase("stacktrace")) {
            if (lastError != null) {
                lastError.printStackTrace(err);
            }
            return;
        }
        /*
         * Unrecognized command. Before to give up, if we were executing this console
         * from the command line, ask the command-line tools to execute this command.
         */
        if (commands != null) {
            final StringTokenizer tokens = new StringTokenizer(instruction);
            final String[] arguments = new String[tokens.countTokens()];
            for (int i=0; tokens.hasMoreTokens(); i++) {
                arguments[i] = tokens.nextToken();
            }
            final String[] old = commands.arguments;
            try {
                commands.consoleRunning = true;
                commands.arguments = arguments;
                commands.run();
            } finally {
                commands.arguments = old;
                commands.consoleRunning = false;
            }
            return;
        }
        /*
         * At this point we really assume that the instruction has not been recognized.
         * Note that we don't reach this point if we delegated to the command line,
         * because the later already provided an error message.
         */
        throw unexpectedArgument(Errors.Keys.ILLEGAL_INSTRUCTION_1, instruction);
    }

    /**
     * Invoked by {@link #run} if an exception occurred in the {@link #execute execute} method.
     * The default implementation prints a message to the {@linkplain #err error stream}. The
     * error message includes the line number if this information is available.
     *
     * @param exception The exception to report.
     * @todo Localize
     */
    protected void reportError(final Exception exception) {
        out.flush();
        if (in != null) {
            print(BACKGROUND_RED);
            err.print(Classes.getShortClassName(exception));
            err.print(" at line ");
            err.println(in.getLineNumber());
            print(BACKGROUND_DEFAULT);
        }
        String message = exception.getLocalizedMessage();
        err.println(message != null ? message : Classes.getShortClassName(exception));
        Throwable cause = exception;
        while ((cause = cause.getCause()) != null) {
            print(FAINT);
            err.print("  Caused by ");
            err.print(Classes.getShortClassName(cause));
            message = cause.getLocalizedMessage();
            if (message != null) {
                err.print(": ");
                err.print(message);
            }
            print(NORMAL);
            err.println();
        }
        lastError = exception;
    }

    /**
     * Sends the given X3.64 instruction if colors are enabled.
     */
    private void print(final X364 color) {
        if (colors) {
            err.print(color.sequence());
        }
    }




    ///////////////////////////////////////////////////////////
    ////////                                           ////////
    ////////        H E L P E R   M E T H O D S        ////////
    ////////                                           ////////
    ///////////////////////////////////////////////////////////

    /**
     * Throws an exception with the given error message.
     */
    final IllegalArgumentException unexpectedArgument(final int key, String instruction) {
        if (colors) {
            instruction = FOREGROUND_YELLOW.sequence() + instruction +
                          FOREGROUND_DEFAULT.sequence();
        }
        return new IllegalArgumentException(Errors.format(key, instruction));
    }

    /**
     * Parses a sequence of numbers separated by a locale-dependent number separator.
     * Those sequences are typically (but are not restricted to) ordinate values in a
     * single coordinate. Example:
     *
     * {@preformat text
     *     (46.69439222, 13.91405611, 41.21)
     * }
     *
     * @param  text The vector to parse.
     * @param  nullAllowed {@code true} if {@code "null"} is allowed.
     * @return The vector as floating point numbers.
     * @throws ParseException if a number can't be parsed.
     */
    final double[] parseVector(String text, final boolean nullAllowed) throws ParseException {
        text = removeDelimitors(text, '(', ')');
        if (nullAllowed && text.equalsIgnoreCase("null")) {
            return null;
        }
        final StringTokenizer st = new StringTokenizer(text, numberSeparator);
        final double[] values = new double[st.countTokens()];
        for (int i=0; i<values.length; i++) {
            // Note: we need to convert the number to upper-case because
            //       NumberParser seems to accepts "1E-10" but not "1e-10".
            final String token = st.nextToken().trim().toUpperCase(locale);
            final ParsePosition position = new ParsePosition(0);
            final Number result = numberFormat.parse(token, position);
            if (position.getIndex() != token.length()) {
                throw new ParseException(Errors.format(
                        Errors.Keys.UNPARSABLE_NUMBER_1, token), position.getErrorIndex());
            }
            values[i] = result.doubleValue();
        }
        return values;
    }

    /**
     * If the given delimiters are presents, returns only the part between those delimiters
     * and discards the rest of the string. This is a lazy way to ignore comments that appear
     * after some coordinate points in test scripts.
     *
     * @param text  The string to check.
     * @param start The delimitor required at the string beginning.
     * @param end   The delimitor required at the string end.
     */
    private static String removeDelimitors(String text, final char start, final char end) {
        int count = 0;
        int lower = 0;
        final int length = text.length();
        for (int i=0; i<length; i++) {
            final char c = text.charAt(i);
            if (c == start) {
                if (count++ == 0) {
                    lower = i+1;
                }
            } else if (c == end) {
                if (--count <= 0) {
                    text = text.substring(lower, i);
                    break;
                }
            }
        }
        return text.trim();
    }
}
