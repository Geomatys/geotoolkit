/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Arrays;
import java.text.ParseException;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link TreeFormat} implementation.
 * Note that {@link Trees} also perform indirectly more tests..
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 3.00)
 */
public final class TreeFormatTest {
    /**
     * Tests the formatting as a tree, with control on the indentation.
     */
    @Test
    public void testFormat() {
        final NamedTreeNode root = new NamedTreeNode("Node #1", 1);
        final NamedTreeNode branch = new NamedTreeNode("Node #2", 2);
        root.add(branch);
        root.add(new NamedTreeNode("Node #3", 3));
        branch.add(new NamedTreeNode("Node #4", 4));

        final TreeFormat tf = new TreeFormat();
        tf.setVerticalLinePosition(2);
        assertEquals(Integer.valueOf(1), root.getUserObject());
        assertEquals(Integer.valueOf(2), branch.getUserObject());
        assertMultilinesEquals(
                "Node #1\n" +
                "  ├─Node #2\n" +
                "  │   └─Node #4\n" +
                "  └─Node #3\n", tf.format(root));
    }

    /**
     * Tests the {@code format} variable which expect an {@link java.lang.Iterable}.
     */
    @Test
    public void testFormatIterable() {
        final TreeFormat tf = new TreeFormat();
        tf.setVerticalLinePosition(2);
        String tree = "Leaf" + tf.getLineSeparator() + tf.format(Arrays.asList("Node #1", "Node #2", "Node #3"));
        tree = "Root" + tf.getLineSeparator() + tf.format(Arrays.asList(tree, "Median node", tree));
        assertMultilinesEquals(
                "Root\n" +
                "  ├─Leaf\n" +
                "  │   ├─Node #1\n" +
                "  │   ├─Node #2\n" +
                "  │   └─Node #3\n" +
                "  ├─Median node\n" +
                "  └─Leaf\n" +
                "      ├─Node #1\n" +
                "      ├─Node #2\n" +
                "      └─Node #3\n", tree);
    }

    /**
     * Tests the parsing of a tree. This method parses and reformats a tree,
     * and performs its check on the assumption that the tree formatting is
     * accurate.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testParsing() throws ParseException {
        final TreeFormat tf = new TreeFormat();
        final String text =
                "Node #1\n" +
                "├───Node #2\n" +
                "│   └───Node #4\n" +
                "└───Node #3\n";
        final TreeNode root = tf.parseObject(text);
        assertMultilinesEquals(text, tf.format(root));
    }
}
