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
package org.geotoolkit.gui.swing.tree;

import java.util.Locale;
import java.util.Arrays;
import java.util.AbstractMap;
import org.geotoolkit.resources.Vocabulary;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link Trees} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 */
public final strictfp class TreesTest {
    /**
     * Tests the formatting as a tree.
     */
    @Test
    public void testToString() {
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
     * Tests the formatting of an {@link java.lang.Iterable} as a tree.
     */
    @Test
    public void testToStringIterable() {
        String tree = Trees.toString("Leaf", Arrays.asList("Node #1", "Node #2", "Node #3"));
        tree = Trees.toString("Root", Arrays.asList(tree, "Median node", tree));
        assertMultilinesEquals(
                "Root\n" +
                "├───Leaf\n" +
                "│   ├───Node #1\n" +
                "│   ├───Node #2\n" +
                "│   └───Node #3\n" +
                "├───Median node\n" +
                "└───Leaf\n" +
                "    ├───Node #1\n" +
                "    ├───Node #2\n" +
                "    └───Node #3\n", tree);
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
            new AbstractMap.SimpleEntry<>("Node #3", "Dummy")
        };
        final MutableTreeNode root = Trees.objectToSwing(object);
        assertMultilinesEquals(
                "Array\n" +
                "├───List\n" +
                "│   ├───Node #1\n" +
                "│   └───Node #2\n" +
                "└───Node #3\n", Trees.toString(root));
    }

    /**
     * Tests the formatting of localized labels.
     *
     * @since 3.17
     */
    @Test
    public void testLocalized() {
        final LocalizedTreeNode root = new LocalizedTreeNode(Vocabulary.formatInternational(Vocabulary.Keys.Undefined));
        final NamedTreeNode child = new NamedTreeNode(Vocabulary.formatInternational(Vocabulary.Keys.Unknown));
        root.add(child);

        root.locale = Locale.ENGLISH;
        assertEquals("Undefined", root.toString());
        assertEquals("Unknown",  child.toString());

        root.locale = Locale.FRENCH;
        assertEquals("Indéfini", root.toString());
        assertEquals("Inconnu", child.toString());
    }
}
