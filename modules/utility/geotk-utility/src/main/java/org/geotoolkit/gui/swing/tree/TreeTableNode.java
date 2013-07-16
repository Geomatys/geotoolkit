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
package org.geotoolkit.gui.swing.tree;


/**
 * Defines the requirements for an object that can be used as a tree node in a
 * {@link org.jdesktop.swingx.JXTreeTable}. This interface is used as a
 * placeholder for the {@link org.jdesktop.swingx.treetable.TreeTableNode} interface
 * defined in <a href="http://swingx.dev.java.net/">SwingX</a>, in order to identify
 * the code where we would use the later interface if we were allowed to introduce
 * <cite>SwingX</cite> dependencies.
 * <p>
 * The first dependency to <cite>SwingX</cite> appears in the
 * <a href="http://www.geotoolkit.org/modules/display/geotk-widgets-swing/">geotk-widgets-swing</a>
 * module, which provides an {@linkplain org.geotoolkit.gui.swing.TreeTableModelAdapter adapter} from
 * this interface to the SwingX {@linkplain org.jdesktop.swingx.treetable.TreeTableModel tree table model}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @see org.jdesktop.swingx.JXTreeTable
 * @see org.geotoolkit.gui.swing.TreeTableModelAdapter
 *
 * @since 3.04
 * @module
 *
 * @deprecated The {@linkplain org.apache.sis.util.collection.TreeTable tree model in Apache SIS}
 *             is no longer based on Swing tree interfaces. Swing dependencies will be phased out
 *             since Swing itself is likely to be replaced by JavaFX in future JDK versions.
 */
@Deprecated
public interface TreeTableNode extends TreeNode {
    /**
     * Returns the number of columns supported by this {@code TreeTableNode}.
     *
     * @return The number of columns this node supports.
     */
    int getColumnCount();

    /**
     * Returns the most specific superclass of values that can be stored in the given column.
     *
     * @param  column The column to query.
     * @return The most specific superclass of legal values in the queried column.
     * @throws IndexOutOfBoundsException If the given column is not a valid column index.
     */
    Class<?> getColumnClass(int column);

    /**
     * Gets the value for this node that corresponds to a particular tabular column.
     *
     * @param  column The column to query.
     * @return The value for the queried column.
     * @throws IndexOutOfBoundsException If the given column is not a valid column index.
     */
    Object getValueAt(int column);

    /**
     * Sets the value for the given column.
     *
     * @param  value  The value to set.
     * @param  column The column to set the value on.
     * @throws IndexOutOfBoundsException If the given column is not a valid column index.
     */
    void setValueAt(Object value, int column);

    /**
     * Determines whether the specified column is editable.
     *
     * @param  column The column to query.
     * @return {@code true} if the column is editable, false otherwise.
     * @throws IndexOutOfBoundsException If the given column is not a valid column index.
     */
    boolean isEditable(int column);
}
