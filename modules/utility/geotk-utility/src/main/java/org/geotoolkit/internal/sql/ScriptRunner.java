/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.sql;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.Reader;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

import org.geotoolkit.util.Strings;
import org.apache.sis.util.Version;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;


/**
 * Run SQL scripts. The script is expected to use a standardized syntax, where the
 * {@value #QUOTE} character is used for quoting text, the {@value #IDENTIFIER_QUOTE}
 * character is used for quoting identifier and the {@value #END_OF_STATEMENT} character
 * is used at the end for every SQL statement.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.00
 * @module
 */
public class ScriptRunner implements FilenameFilter {
    /**
     * The sequence for SQL comments. Leading lines starting by those characters
     * will be ignored.
     *
     * @since 3.16
     */
    private static final String COMMENT = "--";

    /**
     * The character at the end of statements.
     */
    public static final char END_OF_STATEMENT = ';';

    /**
     * The quote character expected to be found in the SQL script.
     */
    public static final char QUOTE = '\'';

    /**
     * The quote character for identifiers expected to be found in the SQL script.
     */
    public static final char IDENTIFIER_QUOTE = '"';

    /**
     * The character for escaping a portion of the SQL script. This is used by
     * PostgreSQL for the definition of triggers.
     */
    private static final String[] ESCAPES = {"$$", "$BODY$"};

    /**
     * The quote character for identifiers actually used in the database.
     */
    protected final String identifierQuote;

    /**
     * The encoding of SQL scripts, or {@code null} for the platform default.
     */
    private String encoding;

    /**
     * The expected variable part of suffixes in files, in the order to be run.
     * For example the EPSG scripts are made of the following files:
     *
     * {@preformat text
     *     EPSG_v6_14.mdb_Data_PostgreSQL.sql
     *     EPSG_v6_14.mdb_FKeys_PostgreSQL.sql
     *     EPSG_v6_14.mdb_Tables_PostgreSQL.sql
     * }
     *
     * In such cases, the list shall contains {@code "Tables"}, {@code "Data"} and
     * {@code "FKeys"} in that order, because this is the order that the script files
     * are expected to be run.
     */
    protected final List<String> suffixes = new ArrayList<>();

    /**
     * The presumed dialect spoken by the database.
     */
    protected final Dialect dialect;

    /**
     * A mapping of words to replace. The replacement is performed only if the word is not
     * found in an identifier or a string. The default implementation let this map empty,
     * but subclasses may fill it at construction time.
     * <p>
     * This is used for example in order to map the table names in the EPSG scripts to table
     * names as they were in the MS-Access flavor of EPSG database. It may also contains the
     * mapping between SQL keywords used in the SQL scripts to SQL keywords understood by the
     * database (for example Derby does not support the {@code TEXT} data type, which need to
     * be replaced by {@code VARCHAR}).
     */
    protected final Map<String,String> replacements = new HashMap<>();

    /**
     * The statement created from a connection to the database.
     */
    private final Statement statement;

    /**
     * The file being read.
     */
    private File currentFile;

    /**
     * The line number being executed. The first line in a file is numbered 1.
     * This number is meanless if {@link #currentFile} is null.
     */
    private int currentLine;

    /**
     * The SQL statement being executed.
     */
    private String currentSQL;

    /**
     * Creates a new runner which will execute the statements using the given connection.
     *
     * @param connection The connection to the database, or {@code null} if none.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    public ScriptRunner(final Connection connection) throws SQLException {
        if (connection != null) {
            final DatabaseMetaData metadata = connection.getMetaData();
            dialect = Dialect.guess(metadata);
            identifierQuote = metadata.getIdentifierQuoteString();
            statement = connection.createStatement();
        } else {
            dialect = Dialect.ANSI;
            identifierQuote = "\"";
            statement = null;
        }
    }

    /**
     * Returns the connection.
     *
     * @return The connection, or {@code null} if none.
     * @throws SQLException If the connection can not be obtained.
     *
     * @since 3.11
     */
    protected Connection getConnection() throws SQLException {
        return (statement != null) ? statement.getConnection() : null;
    }

    /**
     * Returns the encoding of SQL scripts, or {@code null} for the platform default.
     * The default value is {@code null}.
     *
     * @return The encoding of SQL scripts.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding of SQL scripts. Typical values are {@code "UTF-8"} and
     * {@code "ISO-8859-1"}. For SQL scripts provided by the EPSG, the encoding
     * shall be {@code "ISO-8859-1"}.
     *
     * @param encoding The encoding of SQL scripts, or {@code null} for the platform default.
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Returns {@code true} if the file of the given name is a SQL script. The default
     * implementation returns {@code true} if the filename ends with the {@code ".sql"}
     * extension and if it does not start with {@code "."} (which is for hidden files on
     * Unix system).
     * <p>
     * Subclasses can override this method if they need to filter the files differently.
     *
     * @param  directory The directory that contains the file, or {@code null} if none
     *         (for example if the file is an entry in a ZIP file).
     * @param  name The name of the file to test.
     * @return {@code true} if a file of the given name is likely to be a SQL script.
     */
    @Override
    public boolean accept(final File directory, final String name) {
        final int e = name.lastIndexOf('.');
        return (e > 0) && (name.charAt(0) != '.') && name.regionMatches(true, e+1, "sql", 0, 3);
    }

    /**
     * Run the SQL script read from the given file or directory. If the argument is a file, then
     * it is read directly using the {@linkplain #getEncoding() current encoding}. Otherwise if
     * it is a directory, then the directory content is read and filtered by the {@link #accept
     * accept} method.
     *
     * @param  file The file or directory of the script(s) to run.
     * @return The number of rows added or modified as a result of the script(s) execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    public int run(final File file) throws IOException, SQLException {
        if (file.isDirectory()) {
            return run(file, file.list(this));
        } else {
            return runFile(file);
        }
    }

    /**
     * Runs the SQL scripts read from the given files in the given directory.
     *
     * @param  directory The directory of the script(s) to run.
     * @param  files The filename of the script(s) to run in the above directory.
     * @return The number of rows added or modified as a result of the script(s) execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    final int run(final File directory, String[] files) throws IOException, SQLException {
        if (files.length == 0) {
            return 0;
        }
        /*
         * Find the prefix and suffix that are common to every files.
         */
        String prefix = null, suffix = null;
        for (final String file : files) {
            prefix = Strings.commonPrefix(prefix, file);
            suffix = Strings.commonSuffix(suffix, file);
        }
        final int pl = prefix.length();
        final int sl = suffix.length();
        /*
         * Assuming that the part between the prefix and suffix contains the version number,
         * get the version of every files. We will then select one version, by default the
         * one having the highest major/minor version numbers.
         */
        final Set<String> uniques = new LinkedHashSet<>();
        final Map<String,Integer> order = new HashMap<>();
        final String[] versions = new String[files.length];
        for (int i=0; i<files.length; i++) {
            final String file = files[i];
            String version = file.substring(pl, file.length() - sl);
            final int size = suffixes.size();
            for (int j=0; j<size; j++) {
                final String s = suffixes.get(j);
                if (version.endsWith(s)) {
                    version = version.substring(0, version.length() - s.length());
                    order.put(file, j);
                    break;
                }
            }
            versions[i] = version;
            uniques.add(version);
        }
        final String select = selectVersion(uniques.toArray(new String[uniques.size()]));
        /*
         * Filters the files, keeping only those having the expected version number.
         * Then sorts those files in the order declared in the suffixes list.
         */
        int count = 0;
        for (int i=0; i<files.length; i++) {
            if (select.equals(versions[i])) {
                files[count++] = files[i];
            }
        }
        files = ArraysExt.resize(files, count);
        Arrays.sort(files, new Comparator<String>() {
            @Override public int compare(final String o1, final String o2) {
                final Integer i1 = order.get(o1);
                final Integer i2 = order.get(o2);
                if (i1 == null) return +1;
                if (i2 == null) return -1;
                return i1 - i2;
            }
        });
        /*
         * Now run the selected files.
         */
        count = 0;
        for (final String file : files) {
            count += runFile(new File(directory, file));
        }
        return count;
    }

    /**
     * Run the SQL script from a single file.
     *
     * @param  file The file of the script to run.
     * @return The number of rows added or modified as a result of the script execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    int runFile(final File file) throws IOException, SQLException {
        final String encoding = this.encoding;
        final Reader reader;
        if (encoding == null) {
            reader = new FileReader(file);
        } else {
            reader = new InputStreamReader(new FileInputStream(file), encoding);
        }
        final int count;
        try (LineNumberReader in = new LineNumberReader(reader)) {
            currentFile = file; count = run(in);
            currentFile = null; // Clear on success only.
        }
        return count;
    }

    /**
     * Run the script from the given input stream. Lines are read and grouped up to the
     * terminal {@value #END_OF_STATEMENT} character, then sent to the database.
     *
     * @param  in The stream to read. <strong>This stream will be closed</strong> at the end.
     * @return The number of rows added or modified as a result of the script execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    public final int run(final InputStream in) throws IOException, SQLException {
        final Reader reader;
        if (encoding == null) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, encoding);
        }
        try (LineNumberReader lr = new LineNumberReader(reader)) {
            return run(lr);
        }
    }

    /**
     * Run the script from the given reader. Lines are read and grouped up to the
     * terminal {@value #END_OF_STATEMENT} character, then sent to the database.
     *
     * @param  in The stream to read. <strong>This stream will be closed</strong> at the end.
     * @return The number of rows added or modified as a result of the script execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    public final int run(final LineNumberReader in) throws IOException, SQLException {
        // Determine once for ever if it is worth to look for SQL keyword replacements,
        // and if we need to take the trouble to look two words ahead (e.g. "CREATE TABLE").
        final boolean noReplace = replacements.isEmpty();
        boolean replaceTwoWords = false;
        for (final String replace : replacements.keySet()) {
            if (replace.indexOf(' ') >= 0) {
                replaceTwoWords = true;
                break;
            }
        }
        // Variables which will change during the iterations.
        int count = 0;
        final StringBuilder buffer = new StringBuilder();
        String line;
        boolean insideText = false;
        boolean insideIdentifier = false;
        while ((line = in.readLine()) != null) {
            int i = buffer.length();
            if (i == 0) {
                final String trimed = line.trim();
                if (trimed.isEmpty() || trimed.startsWith(COMMENT)) {
                    // Ignore empty lines and comment lines, but only if they appear before any
                    // other kind of line (i.e. before the begining of the real SQL statement).
                    continue;
                }
                currentLine = in.getLineNumber();
            } else {
                i++;
                buffer.append('\n');
            }
            /*
             * If we find the "$$" string, copy verbatism (without any attempt to parse the
             * lines) until the next occurrence of "$$". This simple algorithm does not allow
             * more than one block of "$$ ... $$" on the same line.
             */
            for (final String escape : ESCAPES) {
                int pos = line.indexOf(escape);
                if (pos >= 0) {
                    pos += escape.length();
                    while ((pos = line.indexOf(escape, pos)) < 0) {
                        buffer.append(line).append('\n');
                        line = in.readLine();
                        if (line == null) {
                            throw new EOFException();
                        }
                        pos = 0;
                    }
                    pos += escape.length();
                    buffer.append(line.substring(0, pos));
                    i = buffer.length(); // Will resume the parsing from that position.
                    line = line.substring(pos);
                    break;
                }
            }
            buffer.append(line);
            int length = buffer.length();
scanLine:   for (; i<length; i++) {
                final char c = buffer.charAt(i);
                switch (c) {
                    /*
                     * Found a character for an identifier like "Coordinate Operations".
                     * Check if we have found the opening or the closing character. Then
                     * replace the standard quote character by the database-specific one.
                     */
                    case IDENTIFIER_QUOTE: {
                        if (!insideText) {
                            insideIdentifier = !insideIdentifier;
                            buffer.replace(i, i+1, identifierQuote);
                            i += identifierQuote.length() - 1;
                        }
                        continue;
                    }
                    /*
                     * Found a character for a text like 'This is a value'. Check if we have
                     * found the opening or closing character, ignoring the '' escape sequence.
                     */
                    case QUOTE: {
                        if (!insideIdentifier) {
                            if (!insideText) {
                                insideText = true;
                            } else if (i+1 == length || buffer.charAt(i+1) != QUOTE) {
                                insideText = false;
                            } else {
                                // Found a double ' character, which means to escape it.
                                i++;
                            }
                        }
                        continue;
                    }
                    /*
                     * Found the end of statement. Remove that character if it is the last non-white
                     * character, since SQL statement in JDBC are not expected to contain it.
                     */
                    case END_OF_STATEMENT: {
                        if (!insideText && !insideIdentifier) {
                            int stop = i;
                            while (++i < length) {
                                if (!Character.isSpaceChar(buffer.charAt(i))) {
                                    stop = length;
                                    break;
                                }
                            }
                            buffer.setLength(stop);
                            count += execute(buffer);
                            buffer.setLength(0);
                            break scanLine;
                        }
                        continue;
                    }
                }
                /*
                 * Any other kind of character. If we find the beginning of an identifier
                 * (arbitrarily assuming similar syntax than Java identifier rules), check
                 * for the end of the word and replace if needed.
                 */
                if (!noReplace && !insideText && !insideIdentifier && Character.isJavaIdentifierStart(c)) {
                    final int start = i;
                    while (++i < length && Character.isJavaIdentifierPart(buffer.charAt(i)));
                    String word = buffer.substring(start, i);
                    String replace = replacements.get(word);
                    if (replaceTwoWords && replace == null && i<length && Character.isSpaceChar(buffer.charAt(i))) {
                        // A single word is not sufficient. Try with two words. This is needed in
                        // order to catch "CREATE TABLE", which needs replacement by geotk-epsg.
                        final int mark = i;
                        if (++i < length && Character.isJavaIdentifierStart(buffer.charAt(i))) {
                            while (++i < length && Character.isJavaIdentifierPart(buffer.charAt(i)));
                            word = buffer.substring(start, i);
                            replace = replacements.get(word);
                        }
                        if (replace == null) {
                            i = mark;
                        }
                    }
                    if (replace != null) {
                        buffer.replace(start, i, replace);
                        i = start + replace.length();
                        length = buffer.length();
                    }
                    i--;
                }
            }
        }
        in.close();
        line = buffer.toString().trim();
        if (!line.isEmpty() && !line.startsWith(COMMENT)) {
            throw new EOFException(Errors.format(Errors.Keys.MISSING_CHARACTER_$1, END_OF_STATEMENT));
        }
        return count;
    }

    /**
     * Convenience method invoking {@link #run(LineNumberReader)} for the given SQL statement.
     *
     * @param  statement The SQL statement to execute.
     * @return The number of rows added or modified as a result of the statement execution.
     * @throws IOException If an error occurred while reading the input (should never happen).
     * @throws SQLException If an error occurred while executing a SQL statement.
     *
     * @since 3.16
     */
    public final int run(final String statement) throws IOException, SQLException {
        return run(new LineNumberReader(new StringReader(statement)));
    }

    /**
     * If there is many versions of the SQL script, select the version to run. The default
     * implementation tries to parse the version number and to select the greatest one.
     *
     * @param  versions The version found in a directory.
     * @return The version to run.
     */
    protected String selectVersion(final String[] versions) {
        final StringBuilder buffer = new StringBuilder();
        Version max = null;
        String selected = versions[versions.length - 1];
        for (final String version : versions) {
            final int length = version.length();
            for (int i=0; i<length; i++) {
                char c = version.charAt(i);
                if (c >= '0' && c <= '9') {
                    buffer.setLength(0);
                    while (i < length) {
                        c = version.charAt(i++);
                        if (!Character.isLetterOrDigit(c)) {
                            c = '.';
                        }
                        buffer.append(c);
                    }
                    Version candidate = new Version(buffer.toString());
                    if (max == null || max.compareTo(candidate) <= 0) {
                        max = candidate;
                        selected = version;
                    }
                }
            }
        }
        return selected;
    }

    /**
     * Executes the given SQL statement. The implementation can freely edit the
     * {@link StringBuilder} content.
     *
     * @param  sql The SQL statement to execute.
     * @return The number of rows added or modified as a result of the statement execution.
     * @throws SQLException If an error occurred while executing the SQL statement.
     * @throws IOException If an I/O operation was required and failed.
     */
    protected int execute(final StringBuilder sql) throws SQLException, IOException {
        if (statement == null) {
            return 0;
        }
        currentSQL = sql.toString();
        final int count;
        /*
         * The scripts usually don't contain any SELECT statement. One exception is the creation
         * of geometry columns in a PostGIS database, which use "SELECT AddGeometryColumn(...)".
         */
        if (currentSQL.startsWith("SELECT ")) {
            statement.executeQuery(currentSQL).close();
            count = 0;
        } else {
            count = statement.executeUpdate(currentSQL);
        }
        currentSQL = null; // Clear on success only.
        return count;
    }

    /**
     * Closes the statement used by this runner. Note that this method does not close
     * the connection given to the constructor; this connection still needs to be closed
     * explicitly by the caller.
     * <p>
     * This method does not shutdown the database. For database shutdown, see driver-specific
     * methods like {@link HSQL#shutdown(Connection, boolean)}.
     *
     * @param  vacuum {@code true} for performing a database vacuum (PostgreSQL).
     * @throws SQLException If an error occurred while closing the statement.
     */
    public void close(final boolean vacuum) throws SQLException {
        if (statement != null) {
            switch (dialect) {
                case POSTGRESQL: {
                    if (vacuum) {
                        statement.executeUpdate("VACUUM FULL ANALYZE");
                    }
                    break;
                }
            }
            statement.close();
        }
    }

    /**
     * Returns the current position (current file and current line in that file). The returned
     * string may also contains the SQL statement under execution. The main purpose of this
     * method is to provides informations on the position where an exception occurred.
     *
     * @return A string representation of the current position.
     */
    public String getCurrentPosition() {
        String position = null;
        if (currentFile != null) {
            position = Vocabulary.format(Vocabulary.Keys.FILE_POSITION_$2, currentFile, currentLine);
        }
        if (currentSQL != null) {
            final StringBuilder buffer = new StringBuilder();
            if (position != null) {
                buffer.append(position).append('\n');
            }
            position = buffer.append("SQL: ").append(currentSQL).toString();
        }
        return position;
    }

    /**
     * Returns a string representation of this runner for debugging purpose. Current implementation
     * returns the current position in the script being executed, and the SQL statement. This method
     * may be invoked after a {@link SQLException} occurred in order to determine the line in the SQL
     * script that caused the error.
     *
     * @return The current position in the script being executed.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName()).append('[');
        if (currentFile != null) {
            buffer.append(currentFile.getName()).append(" : ").append(currentLine);
        }
        buffer.append(']');
        if (currentSQL != null) {
            buffer.append('\n').append(currentSQL);
        }
        return buffer.toString();
    }
}
