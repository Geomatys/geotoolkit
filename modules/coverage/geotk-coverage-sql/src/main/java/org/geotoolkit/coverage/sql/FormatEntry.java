/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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

import java.util.List;
import java.util.Locale;
import java.sql.SQLException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.Utilities;

import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.CatalogException;


/**
 * Information about an image format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatEntry extends Entry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8790032968708208057L;

    /**
     * The database from which this entries has been created.
     * Will be set to {@code null} when no longer needed.
     */
    private transient Database database;

    /**
     * The image format name as declared in the database. This value shall be a name
     * useable in calls to {@link javax.imageio.ImageIO#getImageReadersByFormatName}.
     * <p>
     * For compatibility reason, the user should be prepared to handle MIME type
     * (as understood by {@link javax.imageio.ImageIO#getImageReadersByMIMEType}).
     * as well. As a heuristic rule, we can consider this value as a MIME type if
     * it contains the {@code '/'} character.
     */
    public final String imageFormat;

    /**
     * The sample dimensions for coverages encoded with this format.
     * This is computed by {@link #getSampleDimensions()} when first needed.
     */
    private List<GridSampleDimension> sampleDimensions;

    /**
     * {@code true} if coverage to be read are already geophysics values.
     */
    private final boolean geophysics;

    /**
     * Creates a new entry for this format.
     *
     * @param name       An identifier for this entry.
     * @param formatName Format name (i.e. the plugin to use).
     * @param extension  Filename extension, excluding dot separator (example: {@code "png"}).
     * @param geophysics {@code true} if coverage to be read are already geophysics values.
     */
    protected FormatEntry(final Database database, final String name, final String formatName, final boolean geophysics) {
        super(name, null);
        this.database    = database;
        this.imageFormat = formatName.trim();
        this.geophysics  = geophysics;
    }

    /**
     * Returns the name of this layer.
     *
     * @return The name of this layer.
     */
    public String getIdentifier() {
        return (String) identifier;
    }

    /**
     * Returns the sample dimensions for coverages encoded with this format.
     * The array length is equals to the expected number of bands.
     * <p>
     * The sample dimensions specify how to convert pixel values to geophysics values,
     * or conversely. Their type (geophysics or not) is format depedent. For example
     * coverages read from PNG files will typically store their data as integer values
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(false)</code>),
     * while coverages read from ASCII files will often store their pixel values as real numbers
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(true)</code>).
     *
     * @return The sample dimensions.
     * @throws CatalogException if an inconsistent record is found in the database.
     * @throws SQLException if an error occured while reading the database.
     */
    public synchronized List<GridSampleDimension> getSampleDimensions()
            throws CatalogException, SQLException
    {
        if (sampleDimensions == null) {
            final SampleDimensionTable  table = database.getTable(SampleDimensionTable.class);
            final GridSampleDimension[] bands = table.getSampleDimensions(getIdentifier());
            for (int i=0; i<bands.length; i++) {
                bands[i] = bands[i].geophysics(geophysics);
            }
            sampleDimensions = UnmodifiableArrayList.wrap(bands);
            database = null;
        }
        return sampleDimensions;
    }

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The range are always expressed in <cite>geophysics</cite> values.
     */
    final MeasurementRange<Double>[] getSampleValueRanges() throws CatalogException, SQLException {
        final List<GridSampleDimension> bands = getSampleDimensions();
        @SuppressWarnings({"unchecked","rawtypes"})  // Generic array creation.
        final MeasurementRange<Double>[] ranges = new MeasurementRange[bands.size()];
        for (int i=0; i<ranges.length; i++) {
            final GridSampleDimension band = bands.get(i).geophysics(true);
            ranges[i] = MeasurementRange.create(band.getMinimumValue(), band.getMaximumValue(), band.getUnits());
        }
        return ranges;
    }

    /**
     * Returns a tree representation of this format, including
     * {@linkplain SampleDimension sample dimensions} and {@linkplain Category categories}.
     *
     * @param  locale The locale to use for formatting labels in the tree.
     * @return The tree root.
     */
    public MutableTreeNode getTree(final Locale locale) {
        final DefaultMutableTreeNode root = new TreeNode(this);
        for (final GridSampleDimension band : sampleDimensions) {
            final List<Category> categories = band.getCategories();
            final int categoryCount = categories.size();
            final DefaultMutableTreeNode node = new TreeNode(band, locale);
            for (int j=0; j<categoryCount; j++) {
                node.add(new TreeNode(categories.get(j), locale));
            }
            root.add(node);
        }
        return root;
    }

    /**
     * Node appearing as a tree structure of formats and their bands.
     * This node redefines the method {@link #toString} to return a string
     * formatted better than <code>{@link #getUserObject}.toString()</code>.
     *
     * @version $Id$
     * @author Martin Desruisseaux
     */
    private static final class TreeNode extends DefaultMutableTreeNode {
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
        public TreeNode(final FormatEntry entry) {
            super(entry);
            text = entry.toString();
        }

        /**
         * Construct a node for the specified list. The constructor does not
         * scan the categories containes in the specified list.
         */
        public TreeNode(final GridSampleDimension band, final Locale locale) {
            super(band);
            text = band.getDescription().toString(locale);
        }

        /**
         * Constructs a node for the specified category.
         */
        public TreeNode(final Category category, final Locale locale) {
            super(category, false);
            final StringBuilder buffer = new StringBuilder();
            final NumberRange<?> range = category.geophysics(false).getRange();
            buffer.append('[');  append(buffer, range.getMinValue());
            buffer.append(".."); append(buffer, range.getMaxValue()); // Inclusive
            buffer.append("] ").append(category.getName());
            text = buffer.toString();
        }

        /**
         * Add a whole number using at least 3 digits (for example 007).
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

    /**
     * Overriden as a safety, but should not be necessary since identifiers are supposed
     * to be unique in a given database. We don't compare the sample dimensions because
     * it may be costly.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final FormatEntry that = (FormatEntry) object;
            return Utilities.equals(this.imageFormat, that.imageFormat) &&
                                    this.geophysics == that.geophysics;
        }
        return false;
    }

    /**
     * Invoked before serialization in order to ensure that we will serialize the
     * sample dimensions, not the database.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            getSampleDimensions();
        } catch (Exception e) {
            final InvalidObjectException ex = new InvalidObjectException(e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        }
        out.defaultWriteObject();
    }
}
