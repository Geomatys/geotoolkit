/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.tree;

import java.io.Writer;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Enumeration;
import java.text.FieldPosition;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel; // Really the Swing implementation, not the Geotk one.
import org.apache.sis.io.TableAppender;

import org.geotoolkit.io.LineWriter;
import org.geotoolkit.io.ExpandedTabWriter;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.resources.Errors;


/**
 * A formatter for a tree of nodes.
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
final class TreeFormat {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4476366905386037025L;

    /**
     * The number of spaces to add on the left margin for each indentation level.
     * The default value is 4.
     */
    private int indentation;

    /**
     * The position of the vertical line, relative to the position of the root label.
     * The default value is 0, which means that the vertical line is drawn below the
     * first letter of the root label.
     */
    private int verticalLinePosition;

    /**
     * The line separator to use for formatting the tree.
     */
    private String lineSeparator;

    /**
     * The column separator to use if the table format is enabled.
     *
     * @since 3.19
     */
    private char columnSeparator = '…';

    /**
     * {@code true} for enabling the formating of tree tables. The default value is {@code false}.
     *
     * @since 3.19
     */
    private boolean isTableFormatEnabled;

    /**
     * The tree symbols to write in the left margin, or {@code null} if not yet computed.
     * The default symbols are as below:
     * <p>
     * <ul>
     *   <li>{@link #treeBlank} = {@code "    "}</li>
     *   <li>{@link #treeLine}  = {@code "│   "}</li>
     *   <li>{@link #treeCross} = {@code "├───"}</li>
     *   <li>{@link #treeEnd}   = {@code "└───"}</li>
     * </ul>
     */
    private transient String treeBlank, treeLine, treeCross, treeEnd;

    /**
     * The writer to use for formatting a possible multi-line text to a monoline text.
     * Will be created only when first needed.
     *
     * @since 3.19
     */
    private transient Writer lineWriter;

    /**
     * The buffer which is backing {@link #lineWriter}.
     *
     * @since 3.19
     */
    private transient StringBuffer lineBuffer;

    /**
     * Creates a new format.
     */
    public TreeFormat() {
        indentation = 4;
        lineSeparator = System.lineSeparator();
    }

    /**
     * Clears the symbols used when writing the tree.
     * They will be computed again when first needed.
     */
    private void clearTreeSymbols() {
        treeBlank = null;
        treeLine  = null;
        treeCross = null;
        treeEnd   = null;
    }

    /**
     * Returns the number of spaces to add on the left margin for each indentation level.
     * The default value is 4.
     *
     * @return The current indentation.
     */
    public int getIndentation() {
        return indentation;
    }

    /**
     * Sets the number of spaces to add on the left margin for each indentation level.
     * If the new indentation is smaller than the {@linkplain #getVerticalLinePosition()
     * vertical line position}, then the later is also set to the given indentation value.
     *
     * @param indentation The new indentation.
     * @throws IllegalArgumentException If the given value is negative.
     */
    public void setIndentation(final int indentation) throws IllegalArgumentException {
        ArgumentChecks.ensurePositive("indentation", indentation);
        this.indentation = indentation;
        if (verticalLinePosition > indentation) {
            verticalLinePosition = indentation;
        }
        clearTreeSymbols();
    }

    /**
     * Returns the position of the vertical line, relative to the position of the root label.
     * The default value is 0, which means that the vertical line is drawn below the first
     * letter of the root label.
     *
     * @return The current vertical line position.
     */
    public int getVerticalLinePosition() {
        return verticalLinePosition;
    }

    /**
     * Sets the position of the vertical line, relative to the position of the root label.
     * The given value can not be greater than the {@linkplain #getIndentation() indentation}.
     *
     * @param verticalLinePosition The new vertical line position.
     * @throws IllegalArgumentException If the given value is negative or greater than the indentation.
     */
    public void setVerticalLinePosition(final int verticalLinePosition) throws IllegalArgumentException {
        ArgumentChecks.ensureBetween("verticalLinePosition", 0, indentation, verticalLinePosition);
        this.verticalLinePosition = verticalLinePosition;
        clearTreeSymbols();
    }

    /**
     * Returns the current line separator. The default value is system-dependent.
     *
     * @return The current line separator.
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Sets the line separator.
     *
     * @param separator The new line separator.
     */
    public void setLineSeparator(final String separator) {
        ArgumentChecks.ensureNonNull("separator", separator);
        lineSeparator = separator;
    }

    /**
     * Returns the character used as column separator. This character will be inserted between the
     * columns only if the {@linkplain #isTableFormatEnabled() table format is enabled} and the
     * tree to format contains {@link TreeTableNode} elements.
     * <p>
     * The default value if <code>'&hellip;'</code>.
     *
     * @return The column separator.
     *
     * @since 3.19
     */
    public char getColumnSeparator() {
        return columnSeparator;
    }

    /**
     * Sets the column character to insert between the columns in a {@link TreeTableNode}.
     *
     * @param separator The column separator.
     *
     * @since 3.19
     */
    public void setColumnSeparator(final char separator) {
        columnSeparator = separator;
    }

    /**
     * Returns {@code true} if this {@code TreeFormat} is allowed to format the tree using many
     * columns. The default value is {@code false}. Setting this property to {@code true} is
     * useful only if the tree to format contains {@link TreeTableNode} elements.
     *
     * @return {@code true} if this {@code TreeFormat} object is allowed to format many columns.
     *
     * @since 3.19
     */
    public boolean isTableFormatEnabled() {
        return isTableFormatEnabled;
    }

    /**
     * Sets whatever this {@code TreeFormat} is allowed to format the tree as a table.
     * <p>
     * <b>NOTE:</b> parsing of table format is not yet implemented.
     *
     * @param enabled {@code true} for enabling the table format.
     *
     * @since 3.19
     */
    public void setTableFormatEnabled(final boolean enabled) {
        isTableFormatEnabled = enabled;
    }

    /**
     * Returns {@code true} if the given tree should be formatted as a tree table.
     * The current implementation returns {@code true} if the given tree contains
     * at least one node of kind {@link TreeTableNode}.
     *
     * @param  node The root node to inspect.
     * @return {@code false} if at least one tree table node was found.
     */
    private static boolean hasTreeTableNode(final TreeNode node) {
        if (node instanceof TreeTableNode) {
            return true;
        }
        @SuppressWarnings("unchecked")
        final Enumeration<TreeNode> e = node.children();
        if (e != null) while (e.hasMoreElements()) {
            if (hasTreeTableNode(e.nextElement())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Computes the {@code tree*} fields, if needed.
     * This is done only when first needed.
     */
    private void ensureInitialized() {
        if (treeBlank == null) {
            final int indentation = this.indentation;
            final int verticalLinePosition = this.verticalLinePosition;
            final char[] buffer = new char[indentation];
            for (int k=0; k<4; k++) {
                final char vc, hc;
                if ((k & 2) == 0) {
                    // No horizontal line
                    vc = (k & 1) == 0 ? '\u00A0' : '\u2502';
                    hc = '\u00A0';
                } else {
                    // With a horizontal line
                    vc = (k & 1) == 0 ? '\u2514' : '\u251C';
                    hc = '\u2500';
                }
                Arrays.fill(buffer, 0, verticalLinePosition, '\u00A0');
                buffer[verticalLinePosition] = vc;
                Arrays.fill(buffer, verticalLinePosition + 1, indentation, hc);
                final String symbols = String.valueOf(buffer);
                switch (k) {
                    case 0: treeBlank = symbols; break;
                    case 1: treeLine  = symbols; break;
                    case 2: treeEnd   = symbols; break;
                    case 3: treeCross = symbols; break;
                    default: throw new AssertionError(k);
                }
            }
        }
    }

    /**
     * Returns a string representation of the given value, or {@code null} if the value is
     * null. Tabulations are replaced by a single space, and line feeds are replaced by the
     * Unicode "carriage return" character. This is necessary in order to avoid conflict with
     * the characters expected by {@link TableWriter}.
     */
    private String toString(final Object value) throws IOException {
        Writer writer = lineWriter;
        if (writer == null) {
            final StringWriter buffer = new StringWriter();
            lineWriter = writer = new ExpandedTabWriter(new LineWriter(buffer, " \u00B6 "));
            lineBuffer = buffer.getBuffer();
        }
        writer.write(String.valueOf(value));
        writer.flush();
        final String text = lineBuffer.toString();
        writer.write('\n'); // Reset tabulation count and discart trailing spaces.
        lineBuffer.setLength(0);
        return text;
    }

    /**
     * Appends to the given buffer the string representation of the given node and all
     * its children. This method invokes itself recursively.
     *
     * @param model  The tree to format.
     * @param node   The node of the tree to format.
     * @param toAppendTo Where to write the string representation.
     * @param level  Indentation level. The first level is 0.
     * @param last   {@code true} if the previous levels are writing the last node.
     * @return       The {@code last} array, or a copy of that array if it was necessary
     *               to increase its length.
     */
    private boolean[] format(final TreeModel model, final Object node,
            final Appendable toAppendTo, final int level, boolean[] last) throws IOException
    {
        for (int i=0; i<level; i++) {
            final boolean isLast = last[i];
            toAppendTo.append((i != level-1)
                    ? (isLast ? treeBlank : treeLine)
                    : (isLast ? treeEnd   : treeCross));
        }
        if ((node instanceof TreeTableNode) && (toAppendTo instanceof TableAppender)) {
            final TreeTableNode tableNode = (TreeTableNode) node;
            final int columnCount = tableNode.getColumnCount();
            for (int i=0; i<columnCount; i++) {
                if (i != 0) {
                    ((TableAppender) toAppendTo.append(columnSeparator)).nextColumn(columnSeparator);
                }
                final Object value = tableNode.getValueAt(i);
                if (value != null) {
                    toAppendTo.append(toString(value));
                }
            }
        } else {
            toAppendTo.append(toString(node));
        }
        toAppendTo.append(lineSeparator);
        if (level >= last.length) {
            last = Arrays.copyOf(last, level*2);
        }
        final int count = model.getChildCount(node);
        for (int i=0; i<count; i++) {
            last[level] = (i == count-1);
            last = format(model, model.getChild(node, i), toAppendTo, level+1, last);
        }
        return last;
    }

    /**
     * Writes a graphical representation of the specified tree model in the given buffer.
     * This method iterates recursively over all children. Each children is fetched by a
     * call to {@link TreeModel#getChild(Object, int)} and its string representation
     * (expected to uses a single line) is created by a call to {@link String#valueOf(Object)}.
     *
     * @param  tree        The tree to format.
     * @param  toAppendTo  Where to format the tree.
     * @throws IOException If an error occurred while writing in the given appender.
     *
     * @see Trees#toString(TreeModel)
     */
    public void format(final TreeModel tree, final Appendable toAppendTo) throws IOException {
        final Object root = tree.getRoot();
        if (root != null) {
            Writer buffer = null;
            Appendable out = toAppendTo;
            if (isTableFormatEnabled && (root instanceof TreeNode) && hasTreeTableNode((TreeNode) root)) {
                final Writer writer;
                if (toAppendTo instanceof Writer) {
                    writer = (Writer) toAppendTo;
                } else {
                    writer = buffer = new StringWriter();
                }
                out = new TableAppender(new LineWriter(writer, lineSeparator), " ");
            }
            ensureInitialized();
            format(tree, root, out, 0, new boolean[64]);
            if (out != toAppendTo) {
                ((Flushable) out).flush(); // Needed for writing the table content.
            }
            if (buffer != null) {
                toAppendTo.append(buffer.toString());
            }
        }
    }

    /**
     * Convenience method which delegate to the above {@link #format(TreeModel, Appendable)}
     * method, but without throwing {@link IOException}. The I/O exception should never occur
     * since we are writing in a {@link StringBuilder}.
     *
     * {@note Strictly speaking, an <code>IOException</code> could still occur in the user
     * overrides the above <code>format</code> method and performs some I/O operation outside
     * the given <code>StringBuilder</code>. However this is not the intended usage of this
     * class and implementors should avoid such unexpected I/O operation.}
     *
     * @param  tree       The tree to format.
     * @param  toAppendTo Where to format the tree.
     */
    public final void format(final TreeModel tree, final StringBuilder toAppendTo) {
        try {
            format(tree, (Appendable) toAppendTo);
        } catch (IOException e) {
            // Should never occur, unless the user overriden the above 'format'
            // method in a weird way. This is why we don't use AssertionError.
            throw new BackingStoreException(e);
        }
    }

    /**
     * Writes a graphical representation of the specified tree in the given buffer.
     * The default implementation delegates to {@link #format(TreeModel, Appendable)}.
     *
     * @param  node        The root node of the tree to format.
     * @param  toAppendTo  Where to format the tree.
     * @throws IOException If an error occurred while writing in the given appender.
     *
     * @see Trees#toString(TreeNode)
     */
    public void format(final TreeNode node, final Appendable toAppendTo) throws IOException {
        // Use the Swing implementation in order to avoid recursivity
        // in the debugguer if the tree model is formatted as a tree.
        format(new DefaultTreeModel(node, true), toAppendTo);
    }

    /**
     * Convenience method which delegate to the above {@link #format(TreeNode, Appendable)}
     * method, but without throwing {@link IOException}. The I/O exception should never occur
     * since we are writing in a {@link StringBuilder}.
     *
     * {@note Strictly speaking, an <code>IOException</code> could still occur in the user
     * overrides the above <code>format</code> method and performs some I/O operation outside
     * the given <code>StringBuilder</code>. However this is not the intended usage of this
     * class and implementors should avoid such unexpected I/O operation.}
     *
     * @param  node        The root node of the tree to format.
     * @param  toAppendTo Where to format the tree.
     */
    public final void format(final TreeNode node, final StringBuilder toAppendTo) {
        try {
            format(node, (Appendable) toAppendTo);
        } catch (IOException e) {
            // Should never occur, unless the user overriden the above 'format'
            // method in a weird way. This is why we don't use AssertionError.
            throw new BackingStoreException(e);
        }
    }

    /**
     * Writes a graphical representation of the specified elements in the given buffer. This method
     * iterates over the given collection and invokes the {@link String#valueOf(Object)} method for
     * each element. The {@code String} value can span multiple lines.
     *
     * {@section Root label}
     * This method formats only the given child elements. It does not format anything for the
     * root. It is up to the caller to format a root label on its own line before to invoke
     * this method.
     *
     * {@section Recursivity}
     * This method does not perform any check on the element types. In particular, elements of type
     * {@link TreeModel}, {@link TreeNode} or inner {@link Iterable} are not processed recursively.
     * It is up to the {@code toString()} implementation of each element to invoke this
     * {@code format} method recursively if they wish (this method is safe for this purpose).
     *
     * @param  nodes A collection of nodes to format.
     * @param  toAppendTo  Where to format the tree.
     * @throws IOException If an error occurred while writing in the given appender.
     *
     * @see Trees#toString(String, Iterable)
     */
    public void format(final Iterable<?> nodes, final Appendable toAppendTo) throws IOException {
        ensureInitialized();
        final Iterator<?> it = nodes.iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final CharSequence[] lines = CharSequences.splitOnEOL(String.valueOf(it.next()));
            hasNext = it.hasNext();
            final String next;
            String margin;
            if (hasNext) {
                margin = treeCross;
                next   = treeLine;
            } else {
                margin = treeEnd;
                next   = treeBlank;
            }
            for (final CharSequence line : lines) {
                if (line.length() != 0) {
                    toAppendTo.append(margin).append(line).append(lineSeparator);
                    margin = next;
                }
            }
        }
    }

    /**
     * Convenience method which delegate to the above {@link #format(Iterable, Appendable)}
     * method, but without throwing {@link IOException}. The I/O exception should never occur
     * since we are writing in a {@link StringBuilder}.
     *
     * {@note Strictly speaking, an <code>IOException</code> could still occur in the user
     * overrides the above <code>format</code> method and performs some I/O operation outside
     * the given <code>StringBuilder</code>. However this is not the intended usage of this
     * class and implementors should avoid such unexpected I/O operation.}
     *
     * @param  nodes A collection of nodes to format.
     * @param  toAppendTo Where to format the tree.
     */
    public final void format(final Iterable<?> nodes, final StringBuilder toAppendTo) {
        try {
            format(nodes, (Appendable) toAppendTo);
        } catch (IOException e) {
            // Should never occur, unless the user overriden the above 'format'
            // method in a weird way. This is why we don't use AssertionError.
            throw new BackingStoreException(e);
        }
    }

    /**
     * Writes a graphical representation of the specified tree in the given buffer.
     * The default implementation delegates to one of the following method depending
     * on the type of the given tree:
     * <p>
     * <ul>
     *   <li>{@link #format(TreeModel, Appendable)}</li>
     *   <li>{@link #format(TreeNode, Appendable)}</li>
     *   <li>{@link #format(Iterable, Appendable)}</li>
     * </ul>
     *
     * @param  tree        The tree to format.
     * @param  toAppendTo  Where to format the tree.
     * @param  pos         Ignored in current implementation.
     * @return             The given buffer, returned for convenience.
     */
    public StringBuffer format(final Object tree, final StringBuffer toAppendTo, final FieldPosition pos) {
        ArgumentChecks.ensureNonNull("tree", tree);
        try {
            if (tree instanceof TreeModel) {
                format((TreeModel) tree, toAppendTo);
            } else if (tree instanceof TreeNode) {
                format((TreeNode) tree, toAppendTo);
            } else if (tree instanceof Iterable<?>) {
                format((Iterable<?>) tree, toAppendTo);
            } else {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgumentClass_3,
                        "tree", tree.getClass(), TreeModel.class));
            }
        } catch (IOException e) {
            // Should never happen when writing into a StringBuffer.
            throw new AssertionError(e);
        }
        return toAppendTo;
    }
}
