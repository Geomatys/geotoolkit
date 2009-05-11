/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.assertMultilinesEquals;


/**
 * Tests the {@link Trees} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
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
}
