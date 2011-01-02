/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
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

/**
 * Workaround for the missing {@code javax.swing.tree.TreeNode.getUserObject()}
 * method. Sun seems to have forgotten this method in the first <cite>Swing</cite>
 * draft. Unfortunately, since {@link javax.swing.tree.TreeNode} is an interface,
 * Sun can't fix it without breaking compatibility. We have to fix it ourselves,
 * which is the main purpose of this package.
 * <p>
 * This package provides also a few additional functionalities listed below. Those methods
 * work on Swing tree models. Despite the fact that they are defined in a <cite>Swing</cite>
 * package, the tree model can be used as a generic model for arbitrary applications
 * (not limited to <cite>Swing</cite> widgets).
 * <p>
 * <ul>
 *   <li>A set of static methods in the {@link org.geotoolkit.gui.swing.tree.Trees} class.</li>
 *   <li>A {@link org.geotoolkit.gui.swing.tree.TreeTableNode} interface used as a bridge
 *       toward <cite>Swingx</cite> {@link org.jdesktop.swingx.JXTreeTable}.</li>
 *   <li>A {@link org.geotoolkit.gui.swing.tree.TreeNodeFilter} interface for copying a portion
 *       of a tree.</li>
 *   <li>Implementation of {@link org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode#toString()} can
 *       format {@link org.opengis.util.InternationalString} according a {@link java.util.Locale}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.17
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.gui.swing.tree;
