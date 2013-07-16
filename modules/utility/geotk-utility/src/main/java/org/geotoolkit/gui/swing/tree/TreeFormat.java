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
import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.ParseException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel; // Really the Swing implementation, not the Geotk one.

import org.geotoolkit.io.LineReader;
import org.geotoolkit.io.LineReaders;
import org.geotoolkit.io.LineWriter;
import org.geotoolkit.io.ExpandedTabWriter;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.io.TableWriter;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.resources.Errors;


/**
 * A parser and formatter for a tree of nodes. This class can format any of the
 * following types:
 * <p>
 * <ul>
 *   <li>{@link TreeModel} ({@linkplain #format(TreeModel, Appendable) more info})</li>
 *   <li>{@link TreeNode} as the root node of a tree ({@linkplain #format(TreeNode, Appendable) more info})</li>
 *   <li>{@link Iterable} as a collection of children nodes (not including the root)
 *        ({@linkplain #format(Iterable, Appendable) more info})</li>
 * </ul>
 * <p>
 * The result is a tree in {@link String} form like the example below:
 *
 * {@preformat text
 *   Node #1
 *   ├───Node #2
 *   │   └───Node #4
 *   └───Node #3
 * }
 *
 * If the {@linkplain #isTableFormatEnabled() table format is enabled} and the tree nodes are
 * instances of {@link TreeTableNode}, then the output format will be like the example below:
 *
 * {@preformat text
 *   Node #1……………………… More #1
 *   ├───Node #2…………… More #2
 *   │   └───Node #4… More #4
 *   └───Node #3…………… More #3
 * }
 *
 * This representation can be printed to the {@linkplain System#out standard output stream}
 * (for example) if it uses a monospaced font and supports unicode. Indentation and position
 * of the vertical line can be modified by calls to the setter methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 3.18 (derived from 2.0)
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
public class TreeFormat extends Format {
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
     * Creates a tree from the given string representation, or returns {@code null} if an
     * error occurred while parsing the tree.
     * <p>
     * The default implementation delegates to {@link #parseObject(String)}.
     *
     * @param  text The string representation of the tree to parse.
     * @param  pos  The position when to start the parsing.
     * @return The root of the parsed tree, or {@code null} if the given tree can not be parsed.
     */
    @Override
    public MutableTreeNode parseObject(String text, final ParsePosition pos) {
        final int base = pos.getIndex();
        text = text.substring(base);
        try {
            final MutableTreeNode tree = parseObject(text);
            pos.setIndex(base + text.length());
            return tree;
        } catch (ParseException e) {
            pos.setErrorIndex(base + e.getErrorOffset());
            return null;
        }
    }

    /**
     * Creates a tree from the given string representation. This method can parse the trees
     * created by the {@code format(...)} methods defined in this class.
     * <p>
     * The default implementation delegates to {@link #parse(LineReader)}.
     *
     * @param  text The string representation of the tree to parse.
     * @return The root of the parsed tree.
     * @throws ParseException If an error occurred while parsing the tree.
     */
    @Override
    public MutableTreeNode parseObject(final String text) throws ParseException {
        try {
            return parse(LineReaders.wrap(text));
        } catch (IOException e) {
            // Only ContentFormatException should occur here.
            throw new ParseException(e.getLocalizedMessage(), 0);
        }
    }

    /**
     * Creates a tree from the lines read from the given input. This method can parse the trees
     * created by the {@code format(...)} methods defined in this class.
     *
     * {@section Parsing rules}
     * Each node must have at least one {@code '─'} character (unicode 2500) in front of it.
     * The number of spaces and drawing characters ({@code '│'}, {@code '├'} or {@code '└'})
     * before the node determines its indentation, and the indentation determines the parent
     * of each node.
     *
     * @param  input A {@code LineReader} for reading the lines.
     * @return The root of the parsed tree.
     * @throws IOException If an error occurred while parsing the tree.
     */
    public MutableTreeNode parse(final LineReader input) throws IOException {
        /*
         * 'indentation' is the number of spaces (ignoring drawing characters) for each level.
         * 'level' is the current indentation level. 'lastNode' is the last node parsed up to
         * date. It has 'indentation[level]' spaces or drawing characters before its content.
         */
        int level = 0;
        int[] indentation = new int[16];
        DefaultMutableTreeNode root = null;
        DefaultMutableTreeNode lastNode = null;
        String line;
        while ((line = input.readLine()) != null) {
            boolean hasChar = false;
            final int length = line.length();
            int i; // The indentation of current line.
            for (i=0; i<length; i++) {
                char c = line.charAt(i);
                if (!Character.isSpaceChar(c)) {
                    hasChar = true;
                    if ("\u2500\u2502\u2514\u251C".indexOf(c) < 0) {
                        break;
                    }
                }
            }
            if (!hasChar) {
                continue; // The line contains only whitespaces.
            }
            /*
             * Go back to the fist non-space character (should be '─'). This is in case the
             * user puts some spaces in the node text, since we don't want those user-spaces
             * to interfer with the calculation of indentation.
             */
            while (i != 0 && Character.isSpaceChar(line.charAt(i-1))) i--;
            /*
             * Found the first character which is not part of the indentation.
             * If this is the first node created so far, it will be the root.
             */
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(line.substring(i).trim());
            if (root == null) {
                indentation[0] = i;
                root = node;
            } else {
                int p;
                while (i < (p = indentation[level])) {
                    /*
                     * Lower indentation level: go up in the tree until we found the new parent.
                     * Note that lastNode.getParent() should never return null, since only the
                     * node at 'level == 0' has a null parent and we checked this case.
                     */
                    if (--level < 0) {
                        throw new ContentFormatException(Errors.format(Errors.Keys.NODE_HAS_NO_PARENT_1, node));
                    }
                    lastNode = (DefaultMutableTreeNode) lastNode.getParent();
                }
                if (i == p) {
                    /*
                     * The node we just created is a sibling of the previous node. This is
                     * illegal if level==0, in which case we have no parent. Otherwise adds
                     * the sibling to the common parent and let the indentation level unchanged.
                     */
                    final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) lastNode.getParent();
                    if (parent == null) {
                        throw new ContentFormatException(Errors.format(Errors.Keys.NODE_HAS_NO_PARENT_1, node));
                    }
                    parent.add(node);
                } else if (i > p) {
                    /*
                     * The node we just created is a child of the previous node.
                     * Add a new indentation level.
                     */
                    lastNode.add(node);
                    if (++level == indentation.length) {
                        indentation = Arrays.copyOf(indentation, level*2);
                    }
                    indentation[level] = i;
                }
            }
            lastNode = node;
        }
        return root;
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
        if ((node instanceof TreeTableNode) && (toAppendTo instanceof TableWriter)) {
            final TreeTableNode tableNode = (TreeTableNode) node;
            final int columnCount = tableNode.getColumnCount();
            for (int i=0; i<columnCount; i++) {
                if (i != 0) {
                    ((TableWriter) toAppendTo.append(columnSeparator)).nextColumn(columnSeparator);
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
                out = new TableWriter(new LineWriter(writer, lineSeparator), " ");
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
    @Override
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
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_CLASS_3,
                        "tree", tree.getClass(), TreeModel.class));
            }
        } catch (IOException e) {
            // Should never happen when writing into a StringBuffer.
            throw new AssertionError(e);
        }
        return toAppendTo;
    }
}
