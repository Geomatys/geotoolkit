/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Locale;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;


/**
 * Node appearing as a tree structure of formats and their bands.
 * This node redefines the method {@link #toString} to return a string
 * formatted better than <code>{@link #getUserObject}.toString()</code>.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatTreeNode extends DefaultMutableTreeNode {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 9030373781984474394L;

    /**
     * The text returned by {@link #toString}.
     */
    private final String text;

    /**
     * Construct a node for the specified entries.
     */
    public FormatTreeNode(final FormatEntry entry) {
        super(entry);
        text = entry.toString();
    }

    /**
     * Constructs a node for the specified list. The constructor does not
     * scan the categories containes in the specified list.
     */
    public FormatTreeNode(final GridSampleDimension band, final Locale locale) {
        super(band);
        text = band.getDescription().toString(locale);
    }

    /**
     * Constructs a node for the specified category.
     */
    public FormatTreeNode(final Category category, final Locale locale) {
        super(category, false);
        final StringBuilder buffer = new StringBuilder();
        final NumberRange<?> range = category.geophysics(false).getRange();
        buffer.append('[');  append(buffer, range.getMinValue());
        buffer.append(" \u2026 "); append(buffer, range.getMaxValue()); // Inclusive
        buffer.append("] ").append(category.getName());
        text = buffer.toString();
    }

    /**
     * Adds a whole number using at least 3 digits (for example 007).
     */
    private static void append(final StringBuilder buffer, final Comparable<?> value) {
        final String number = String.valueOf(value);
        for (int i=3-number.length(); --i>=0;) {
            buffer.append('0');
        }
        buffer.append(number);
    }

    /**
     * Returns the text of this node.
     */
    @Override
    public String toString() {
        return text;
    }
}
