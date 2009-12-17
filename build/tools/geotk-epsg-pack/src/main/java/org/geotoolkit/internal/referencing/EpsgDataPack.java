/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.referencing;

import java.io.*;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.internal.sql.ScriptRunner;


/**
 * Compacts {@code INSERT TO ...} SQL statements for smaller JAR file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class EpsgDataPack extends ScriptRunner {
    /**
     * The output file, or {@code null} if closed or not yet created.
     */
    private Writer out;

    /**
     * The {@code INSERT INTO "Table"} statement currently executing.
     * Used in order to detect when the script start inserting values
     * in a different table.
     */
    private String insertStatement;

    /**
     * {@code true} if insertions are currently done in the datum table.
     */
    private boolean insertDatum;

    /**
     * Creates a new instance.
     *
     * @throws SQLException Should never happen.
     */
    EpsgDataPack() throws SQLException {
        super(null);
        setEncoding("ISO-8859-1");
    }

    /**
     * Returns {@code true} if the given line should be omitted from the script.
     *
     * @param  line The line, without trailing {@code ';'}.
     * @return {@code true} if the line should be omitted.
     */
    private static boolean omit(final String line) {
        if (line.startsWith("UPDATE epsg_datum SET realization_epoch = replace(realization_epoch, CHAR(182), CHAR(10))")) {
            // We ommit this line because we changed the type from VARCHAR to SMALLINT.
            return true;
        }
        return false;
    }

    /**
     * Compacts the given file.
     *
     * @param  inputFile    The input file where to read the SQL statements to compact.
     * @param  outputFile   The output file where to write the compacted SQL statements.
     * @param  encoding     The character encoding for both input and output files.
     * @throws IOException  If an I/O operation failed.
     * @throws SQLException Should never happen.
     */
    final void run(final File inputFile, final File outputFile) throws SQLException, IOException {
        if (inputFile.equals(outputFile)) {
            throw new IllegalArgumentException("Input and output files are the same.");
        }
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), getEncoding()));
        try {
            run(inputFile);
        } finally {
            out.close();
            out = null;
        }
    }

    /**
     * "Executes" the given SQL statement. In the context of this {@code EpsgDataPack} class,
     * executing a SQL statement means compacting it and writting it to the output file.
     *
     * @param  sql The SQL statement to compact.
     * @return The number of rows added.
     * @throws IOException If an I/O operation failed.
     */
    @Override
    protected int execute(final StringBuilder sql) throws IOException {
        StringUtilities.removeLF(sql);
        String line = sql.toString().trim();
        for (int i=sql.length(); --i>=0;) {
            sql.setCharAt(i, Character.toUpperCase(sql.charAt(i)));
        }
        final String lnup = sql.toString();
        /*
         * The above code converted the SQL statement to upper cases with our own loop
         * instead than using String.toUpper(Locale.US) because we need to make sure that
         * every characters stay at the exact same position (String.toUpper() make a more
         * sophesticated conversion).
         */
        if (insertStatement != null) {
            if (lnup.startsWith(insertStatement)) {
                // The previous instruction was already an INSERT INTO the same table.
                line = line.substring(insertStatement.length()).trim();
                line = removeUselessExponents(line);
                if (insertDatum) {
                    line = removeRealizationEpochQuotes(line);
                }
                out.write(",\n"); // Really want Unix EOL, not the platform-specific one.
                writeValues(line);
                return 1;
            }
            // Previous instruction was the last INSERT INTO for a given table.
            // We now have a new instruction. Append the pending cariage return.
            out.write(";\n");
        }
        if (lnup.startsWith("INSERT INTO")) {
            insertDatum = lnup.startsWith("INSERT INTO EPSG_DATUM VALUES");
            int valuesStart = lnup.indexOf("VALUES", 11);
            if (valuesStart >= 0) {
                // We are begining insertions in a new table.
                valuesStart += 6; // Move to the end of "VALUES".
                insertStatement = lnup.substring(0, valuesStart).trim();
                final String pc = line.substring(0, valuesStart).trim();
                line = line.substring(insertStatement.length()).trim();
                line = removeUselessExponents(line);
                if (insertDatum) {
                    line = removeRealizationEpochQuotes(line);
                }
                out.write(pc);
                out.write('\n');
                writeValues(line);
                return 1;
            }
        }
        insertStatement = null;
        if (!omit(line)) {
            out.write(line);
            out.write(";\n");
        }
        return 0;
    }

    /**
     * Writes the values after an {@code INSERT INTO "Table" VALUES} expression.
     * This method tries to remove extra spaces before ( and after ) for producing
     * a more compact file.
     */
    private void writeValues(final String values) throws IOException {
        if (values.startsWith("(") && values.endsWith(")")) {
            out.write('(');
            out.write(values.substring(1, values.length()-1).trim());
            out.write(')');
        } else {
            out.write(values);
        }
    }

    /**
     * For private usage by the following method only.
     */
    private static final Pattern uselessExponentPattern =
            Pattern.compile("([\\(\\,]\\-?\\d+\\.\\d+)E[\\+\\-]?0+([\\,\\)])");

    /**
     * Removes the useless "E0" exponents after floating point numbers.
     */
    private static String removeUselessExponents(String line) {
        StringBuilder cleaned = null;
        final Matcher matcher = uselessExponentPattern.matcher(line);
        while (true) {
            int lastIndex = 0;
            while (matcher.find()) {
                // Make sure this is not a quoted text.
                boolean quoted = false;
                for (int i=matcher.start(); (i=line.lastIndexOf('\'', i-1)) >= 0;) {
                    if (i==0 || line.charAt(i-1)!='\\') {
                        quoted = !quoted;
                    }
                }
                if (!quoted) {
                    // Found a number outside quotes. Replace.
                    if (cleaned == null) {
                        cleaned = new StringBuilder();
                    }
                    cleaned.append(line.substring(lastIndex, matcher.end(1)));
                    lastIndex = matcher.end();
                    cleaned.append(line.substring(matcher.start(2), lastIndex));
                }
            }
            if (lastIndex == 0) {
                return line;
            }
            cleaned.append(line.substring(lastIndex));
            line = cleaned.toString();
            matcher.reset(line);
            cleaned.setLength(0);
        }
    }

    /**
     * Removes the quotes in REALIZATION_EPOCH column (i.e. change the type
     * from TEXT to INTEGER). This is the 5th column.
     */
    private static String removeRealizationEpochQuotes(final String line) {
        int index = getIndexForColumn(line, 5);
        if (line.charAt(index) != '\'') {
            return line;
        }
        final StringBuilder cleaned = new StringBuilder(line.substring(0, index));
        if (line.charAt(++index) == '\'') {
            cleaned.append("Null");
        } else do {
            cleaned.append(line.charAt(index));
        }
        while (line.charAt(++index) != '\'');
        cleaned.append(line.substring(index+1));
        return cleaned.toString();
    }

    /**
     * Returns the start index for the given column in the specified {@code VALUES} string.
     * Column numbers start at 1.
     */
    private static int getIndexForColumn(final String line, int column) {
        if (--column == 0) {
            return 0;
        }
        boolean quote = false;
        final int length = line.length();
        for (int index=0; index<length; index++) {
            switch (line.charAt(index)) {
                case '\'': {
                    if (index == 0 || line.charAt(index-1) != '\\') {
                        quote = !quote;
                    }
                    break;
                }
                case ',': {
                    if (!quote && --column==0) {
                        while (++index < length) {
                            if (!Character.isWhitespace(line.charAt(index))) {
                                break;
                            }
                        }
                        return index;
                    }
                    break;
                }
            }
        }
        return length;
    }
}
