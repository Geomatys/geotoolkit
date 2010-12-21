/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.AbstractMap;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link Trees} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 */
public final class TreesTest {
    /**
     * Tests the formatting as a tree.
     */
    @Test
    public void testFormat() {
        final NamedTreeNode root = new NamedTreeNode("Node #1", 1);
        final NamedTreeNode branch = new NamedTreeNode("Node #2", 2);
        root.add(branch);
        root.add(new NamedTreeNode("Node #3", 3));
        branch.add(new NamedTreeNode("Node #4", 4));

        assertEquals(Integer.valueOf(1), root.getUserObject());
        assertEquals(Integer.valueOf(2), branch.getUserObject());
        assertMultilinesEquals(
                "Node #1\n" +
                "├───Node #2\n" +
                "│   └───Node #4\n" +
                "└───Node #3\n", Trees.toString(root));
    }

    /**
     * Tests the parsing of a tree. This method parses and reformats a tree,
     * and perform its check on the assumption that the tree formatting is
     * accurate.
     *
     * @since 3.02
     */
    @Test
    public void testParsing() {
        final String text =
                "Node #1\n" +
                "├───Node #2\n" +
                "│   └───Node #4\n" +
                "└───Node #3\n";
        final TreeNode root = Trees.parse(text);
        assertMultilinesEquals(text, Trees.toString(root));
    }

    /**
     * Tests {@link Trees#objectToSwing(Object)}.
     *
     * @since 3.17
     */
    @Test
    public void testObjectToSwing() {
        final Object object = new Object[] {
            Arrays.asList("Node #1", "Node #2"),
            new AbstractMap.SimpleEntry<String,String>("Node #3", "Dummy")
        };
        final MutableTreeNode root = Trees.objectToSwing(object);
        assertMultilinesEquals(
                "Array\n" +
                "├───List\n" +
                "│   ├───Node #1\n" +
                "│   └───Node #2\n" +
                "└───Node #3\n", Trees.toString(root));
    }
}
