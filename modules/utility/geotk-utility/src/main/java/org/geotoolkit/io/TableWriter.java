/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io;

import java.io.IOException;
import java.io.Writer;
import javax.swing.text.StyleConstants;
import net.jcip.annotations.ThreadSafe;
import org.apache.sis.io.TableFormatter;
import org.apache.sis.io.IO;

import org.geotoolkit.util.Strings;
import org.geotoolkit.lang.Decorator;


/**
 * A character stream that can be used to format tables. Columns are separated by tabulations
 * ({@code '\t'}) and rows are separated by line terminators ({@code '\r'}, {@code '\n'} or
 * {@code "\r\n"}). Every table cells are stored in memory until {@link #flush()} is invoked.
 * When invoked, {@link #flush()} copy cell contents to the underlying stream while replacing
 * tabulations by some amount of spaces. The exact number of spaces is computed from cell widths.
 * {@code TableWriter} produces correct output when displayed with a monospaced font.
 * <p>
 * For example, the following code...
 *
 * {@preformat java
 *     TableWriter out = new TableWriter(new OutputStreamWriter(System.out), 3);
 *     out.write("Prénom\tNom\n");
 *     out.nextLine('-');
 *     out.write("Idéphonse\tLaporte\nSarah\tCoursi\nYvan\tDubois");
 *     out.flush();
 * }
 *
 * ...produces the following output:
 *
 * {@preformat text
 *      Prénom      Nom
 *      ---------   -------
 *      Idéphonse   Laporte
 *      Sarah       Coursi
 *      Yvan        Dubois
 * }
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link TableFormatter}.
 */
@Deprecated
@ThreadSafe
@Decorator(Writer.class)
public class TableWriter extends FilterWriter {
    /**
     * A possible value for cell alignment. This specifies that the text is aligned
     * to the left indent and extra whitespace should be placed on the right.
     */
    public static final int ALIGN_LEFT = StyleConstants.ALIGN_LEFT;

    /**
     * A possible value for cell alignment. This specifies that the text is aligned
     * to the right indent and extra whitespace should be placed on the left.
     */
    public static final int ALIGN_RIGHT = StyleConstants.ALIGN_RIGHT;

    /**
     * A possible value for cell alignment. This specifies that the text is aligned
     * to the center and extra whitespace should be placed equally on the left and right.
     */
    public static final int ALIGN_CENTER = StyleConstants.ALIGN_CENTER;

    /**
     * A column separator for {@linkplain #TableWriter(Writer,String) constructor}.
     *
     * @since 2.5
     */
    public static final String SINGLE_VERTICAL_LINE = " \u2502 ";

    /**
     * A column separator for {@linkplain #TableWriter(Writer,String) constructor}.
     *
     * @since 2.5
     */
    public static final String DOUBLE_VERTICAL_LINE = " \u2551 ";

    /**
     * A line separator for {@link #nextLine(char)}.
     *
     * @since 2.5
     */
    public static final char SINGLE_HORIZONTAL_LINE = '\u2500';

    /**
     * A line separator for {@link #nextLine(char)}.
     *
     * @since 2.5
     */
    public static final char DOUBLE_HORIZONTAL_LINE = '\u2550';

    /**
     * The Apache SIS formatter on which to delegate the work.
     */
    private final TableFormatter formatter;

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors")
     */
    private TableWriter(final TableFormatter formatter) {
        super(IO.asWriter(formatter));
        this.formatter = formatter;
    }

    /**
     * Creates a new table writer with a default column separator. The default is a double
     * vertical line for the left and right table borders, and a single horizontal line
     * between the columns.
     *
     * {@note This writer may produce bad output on Windows console, unless the underlying stream
     * use the correct codepage (e.g. <code>OutputStreamWriter(System.out, "Cp437")</code>). To
     * display the appropriate codepage for a Windows console, type <code>chcp</code> on the
     * command line.}
     *
     * @param out Writer object to provide the underlying stream, or {@code null} if there is no
     *        underlying stream. If {@code out} is null, then the {@link #toString} method is the
     *        only way to get the table content.
     */
    public TableWriter(final Writer out) {
        this(out != null ? new TableFormatter(out) : new TableFormatter());
    }

    /**
     * Creates a new table writer with the specified amount of spaces as column separator.
     *
     * @param out Writer object to provide the underlying stream, or {@code null} if there is no
     *        underlying stream. If {@code out} is null, then the {@link #toString} method is the
     *        only way to get the table content.
     * @param spaces Amount of white spaces to use as column separator.
     */
    public TableWriter(final Writer out, final int spaces) {
        this(out, Strings.spaces(spaces));
    }

    /**
     * Creates a new table writer with the specified column separator.
     *
     * @param out Writer object to provide the underlying stream, or {@code null} if there is no
     *        underlying stream. If {@code out} is null, then the {@link #toString} method is the
     *        only way to get the table content.
     * @param separator String to write between columns. Drawing box characters are treated
     *        specially. For example {@code " \\u2502 "} can be used for a single-line box.
     *
     * @see #SINGLE_VERTICAL_LINE
     * @see #DOUBLE_VERTICAL_LINE
     */
    public TableWriter(final Writer out, final String separator) {
        this(out != null ? new TableFormatter(out, separator) : new TableFormatter(separator));
    }

    /**
     * Sets the desired behavior for EOL and tabulations characters.
     * <ul>
     *   <li>If {@code true}, EOL (<code>'\r'</code>, <code>'\n'</code> or
     *       <code>"\r\n"</code>) and tabulations (<code>'\t'</code>) characters
     *       are copied straight into the current cell, which mean that next write
     *       operations will continue inside the same cell.</li>
     *   <li>If {@code false}, then tabulations move to next column and EOL move
     *       to the first cell of next row (i.e. tabulation and EOL are equivalent to
     *       {@link #nextColumn()} and {@link #nextLine()} calls respectively).</li>
     * </ul>
     * The default value is {@code false}.
     *
     * @param multiLines {@code true} true if EOL are used for line feeds inside
     *        current cells, or {@code false} if EOL move to the next row.
     */
    public void setMultiLinesCells(final boolean multiLines) {
        synchronized (lock) {
            formatter.setMultiLinesCells(multiLines);
        }
    }

    /**
     * Tells if EOL characters are used for line feeds inside current cells.
     *
     * @return {@code true} if EOL characters are to be write inside the cell.
     */
    public boolean isMultiLinesCells() {
        synchronized (lock) {
            return formatter.isMultiLinesCells();
        }
    }

    /**
     * Sets the alignment for all cells in the specified column. This method
     * overwrite the alignment for all previous cells in the specified column.
     *
     * @param column The 0-based column number.
     * @param alignment Cell alignment. Must be {@link #ALIGN_LEFT}
     *        {@link #ALIGN_RIGHT} or {@link #ALIGN_CENTER}.
     *
     * @deprecated Not effective anymore, because no equivalent method in SIS.
     */
    @Deprecated
    public void setColumnAlignment(final int column, final int alignment) {
        // No corresponding method is SIS.
    }

    /**
     * Sets the alignment for current and next cells. Change to the
     * alignment doesn't affect the alignment of previous cells and
     * previous rows. The default alignment is {@link #ALIGN_LEFT}.
     *
     * @param alignment Cell alignment. Must be {@link #ALIGN_LEFT}
     *        {@link #ALIGN_RIGHT} or {@link #ALIGN_CENTER}.
     */
    public void setAlignment(final int alignment) {
        final byte a;
        switch (alignment) {
            case ALIGN_LEFT:   a = TableFormatter.ALIGN_LEFT;   break;
            case ALIGN_RIGHT:  a = TableFormatter.ALIGN_RIGHT;  break;
            case ALIGN_CENTER: a = TableFormatter.ALIGN_CENTER; break;
            default: throw new IllegalArgumentException(String.valueOf(alignment));
        }
        synchronized (lock) {
            formatter.setCellAlignment(a);
        }
    }

    /**
     * Returns the alignment for current and next cells.
     *
     * @return Cell alignment: {@link #ALIGN_LEFT} (the default),
     *         {@link #ALIGN_RIGHT} or {@link #ALIGN_CENTER}.
     */
    public int getAlignment() {
        final byte alignment;
        synchronized (lock) {
            alignment = formatter.getCellAlignment();
        }
        switch (alignment) {
            case TableFormatter.ALIGN_LEFT:   return ALIGN_LEFT;
            case TableFormatter.ALIGN_RIGHT:  return ALIGN_RIGHT;
            case TableFormatter.ALIGN_CENTER: return ALIGN_CENTER;
            default: throw new IllegalArgumentException(String.valueOf(alignment));
        }
    }

    /**
     * Returns the number of rows in this table. This count is reset to 0 by {@link #flush}.
     *
     * @return The number of rows in this table.
     *
     * @since 2.5
     */
    public int getRowCount() {
        synchronized (lock) {
            return formatter.getRowCount();
        }
    }

    /**
     * Returns the number of columns in this table.
     *
     * @return The number of columns in this table.
     *
     * @since 2.5
     */
    public int getColumnCount() {
        synchronized (lock) {
            return formatter.getColumnCount();
        }
    }

    /**
     * Write a single character. If {@link #isMultiLinesCells()}
     * is {@code false} (which is the default), then:
     * <ul>
     *   <li>Tabulations (<code>'\t'</code>) are replaced by {@link #nextColumn()} invocations.</li>
     *   <li>Line separators (<code>'\r'</code>, <code>'\n'</code> or <code>"\r\n"</code>)
     *       are replaced by {@link #nextLine()} invocations.</li>
     * </ul>
     *
     * @param c Character to write.
     */
    @Override
    public void write(final int c) {
        try {
            super.write(c);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Writes a string. Tabulations and line separators are interpreted as by {@link #write(int)}.
     *
     * @param string String to write.
     */
    @Override
    public void write(final String string) {
        try {
            super.write(string);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Writes a portion of a string. Tabulations and line
     * separators are interpreted as by {@link #write(int)}.
     *
     * @param string String to write.
     * @param offset Offset from which to start writing characters.
     * @param length Number of characters to write.
     */
    @Override
    public void write(final String string, int offset, int length) {
        try {
            super.write(string, offset, length);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Writes an array of characters. Tabulations and line
     * separators are interpreted as by {@link #write(int)}.
     *
     * @param cbuf Array of characters to be written.
     */
    @Override
    public void write(final char[] cbuf) {
        try {
            super.write(cbuf);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Writes a portion of an array of characters. Tabulations and
     * line separators are interpreted as by {@link #write(int)}.
     *
     * @param cbuf   Array of characters.
     * @param offset Offset from which to start writing characters.
     * @param length Number of characters to write.
     */
    @Override
    public void write(final char[] cbuf, int offset, int length) {
        try {
            super.write(cbuf, offset, length);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Writes an horizontal separator.
     */
    public void writeHorizontalSeparator() {
        synchronized (lock) {
            formatter.writeHorizontalSeparator();
        }
    }

    /**
     * Moves one column to the right. Next write operations will occur in a new cell on the
     * same row.
     */
    public void nextColumn() {
        synchronized (lock) {
            formatter.nextColumn();
        }
    }

    /**
     * Moves one column to the right. Next write operations will occur in a new cell on the
     * same row. This method fill every remaining space in the current cell with the specified
     * character. For example calling {@code nextColumn('*')} from the first character of a cell
     * is a convenient way to put a pad value in this cell.
     *
     * @param fill Character filling the cell (default to whitespace).
     */
    public void nextColumn(final char fill) {
        synchronized (lock) {
            formatter.nextColumn(fill);
        }
    }

    /**
     * Moves to the first column on the next row.
     * Next write operations will occur on a new row.
     */
    public void nextLine() {
        synchronized (lock) {
            formatter.nextLine();
        }
    }

    /**
     * Moves to the first column on the next row. Next write operations will occur on a new
     * row. This method fill every remaining cell in the current row with the specified character.
     * Calling {@code nextLine('-')} from the first column of a row is a convenient way to fill
     * this row with a line separator.
     *
     * @param fill Character filling the rest of the line (default to whitespace).
     *             This character may be use as a row separator.
     *
     * @see #SINGLE_HORIZONTAL_LINE
     * @see #DOUBLE_HORIZONTAL_LINE
     */
    public void nextLine(final char fill) {
        synchronized (lock) {
            formatter.nextLine(fill);
        }
    }

    /**
     * Returns the table content as a string.
     */
    @Override
    public String toString() {
        synchronized (lock) {
            return formatter.toString();
        }
    }

    /**
     * For allowing other writers defined in this package to format the table content.
     */
    @Override
    final String content() {
        return toString();
    }
}
