/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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


/**
 * Simple tree table node for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
@SuppressWarnings("serial")
final strictfp class TestNode extends NamedTreeNode implements TreeTableNode {
    /**
     * The column values.
     */
    private final String[] columns;

    /**
     * Creates a new test node with the given column values.
     */
    TestNode(final String... columns) {
        super(Arrays.toString(columns), columns);
        this.columns = columns;
    }

    @Override public int      getColumnCount()           {return columns.length;}
    @Override public Class<?> getColumnClass(int column) {return String.class;}
    @Override public boolean  isEditable    (int column) {return true;}
    @Override public Object   getValueAt    (int column) {return columns[column];}
    @Override public void     setValueAt(Object value, int column) {columns[column] = (String) value;}
}
