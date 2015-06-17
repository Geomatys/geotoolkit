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
package org.geotoolkit.internal.swing.table;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.resources.Errors;


/**
 * Utilities that apply to {@link JTable} and related classes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.05
 * @module
 */
public final class JTables extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private JTables() {
    }

    /**
     * Sets the alignments of header labels to {@link JLabel#CENTER}, if possible.
     *
     * @param table The table for which to set the alignment of header label.
     *
     * @since 3.13
     */
    public static void setHeaderCenterAlignment(final JTable table) {
        final TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        if (renderer instanceof JLabel) {
            ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
        }
    }

    /**
     * Sets the identifiers of every visible columns in the given model, in the order
     * they are shown in the {@link JTable}. Hidden column, if any, are not included.
     * <p>
     * Because the affected columns depend on user actions (he may have moved the
     * columns around), this method is more useful immediately after the creation
     * of a {@link JTable}, when we still known the column order.
     *
     * @param model The columns on which to set the identifiers.
     * @param identifiers The identifiers to set.
     */
    public static void setIdentifiers(final TableColumnModel model, final Object... identifiers) {
        for (int i=0; i<identifiers.length; i++) {
            model.getColumn(i).setIdentifier(identifiers[i]);
        }
    }

    /**
     * Returns every columns in the given model, in the order they are shown in the {@link JTable}.
     * <p>
     * <b>HACK:</b> This method contains a workaround for a {@link TableColumnModelExt}
     * behavior, which returns the columns in a different order depending on the value
     * of {@code includeHidden}. This hack may be removed in a future version if Swingx
     * change this behavior (see {@link TableColumnModelExt#getColumns(boolean)} javadoc).
     *
     * @param  model The model from which to get the columns.
     * @param  includeHidden {@code true} if invisible columns should be included.
     * @return The columns (never {@code null}).
     */
    public static TableColumn[] getColumns(final TableColumnModel model, final boolean includeHidden) {
        TableColumn[] columns = null;
        if (model instanceof TableColumnModelExt) {
            final TableColumnModelExt ext = (TableColumnModelExt) model;
            final List<TableColumn> list = ext.getColumns(includeHidden);
            columns = list.toArray(new TableColumn[list.size()]);
            /*
             * Swingx documentation said that we have the view order if hidden columns
             * are not included. This is the order that we want.
             */
            if (!includeHidden) {
                return columns;
            }
        }
        final TableColumn[] visibleColumns = new TableColumn[model.getColumnCount()];
        for (int i=0; i<visibleColumns.length; i++) {
            visibleColumns[i] = model.getColumn(i);
        }
        if (columns == null) {
            return visibleColumns;
        }
        /*
         * The columns are in insertion order, will we wanted view order.
         * Re-order them now with visible columns first in the same order
         * then 'visibleColumns', and hidden columns last.
         */
        Arrays.sort(columns, new Comparator<TableColumn>() {
            @Workaround(library="Swingx", version="1.6")
            @Override public int compare(final TableColumn c1, final TableColumn c2) {
                if (c1 != c2) {
                    for (int i=0; i<visibleColumns.length; i++) {
                        final TableColumn c = visibleColumns[i];
                        if (c == c1) return -1;
                        if (c == c2) return +1;
                    }
                }
                return 0;
            }
        });
        return columns;
    }

    /**
     * Copies the configuration of the given source model to the given target model.
     * Every columns declared in the source model, including invisible ones, must be
     * present in the target model. Extra columns in the target model are ignored.
     * <p>
     * All target columns shall be visible before the call to this method.
     * The identifiers in the source and target models must match.
     * <p>
     * See the documentation in {@link #copyConfiguration(TableColumn, TableColumn)}
     * for more information on the property that are copied (not all of them are copied).
     *
     * @param  source The source model. This model will not be modified.
     * @param  target The model to modify.
     * @throws IllegalArgumentException If a column in {@code target} has not been found
     *         in {@code source}.
     */
    public static void copyConfiguration(final TableColumnModel source, final TableColumnModel target) {
        final TableColumn[] sourceColumns = getColumns(source, true);
        /*
         * We move the target columns before we copy the configuration,
         * because the later may turn some columns into invisible ones
         * and thus make them inacessible from the getColumn(int) method.
         */
        for (int i=0; i<sourceColumns.length; i++) {
            final TableColumn sourceColumn = sourceColumns[i];
            final Object identifier = sourceColumn.getIdentifier();
            final int position = target.getColumnIndex(identifier);
            if (position < i) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.DuplicatedValuesForKey_1, identifier));
            }
            target.moveColumn(position, i);
        }
        /*
         * Configures the columns in reverse order for avoiding the
         * change in index numbers caused by the column visibility changes.
         */
        for (int i=sourceColumns.length; --i>=0;) {
            final TableColumn src = sourceColumns[i];
            final TableColumn tgt = target.getColumn(i);
            assert tgt.getIdentifier().equals(src.getIdentifier());
            copyConfiguration(src, tgt);
        }
    }

    /**
     * Copies the configuration of the given source column to the given target column.
     * Current implementation copies only the configuration which is typically modified
     * by the user, like the column width and the visibility status. Future versions
     * may expand on that.
     *
     * @param source The source column. This column will not be modified.
     * @param target The column to modify.
     */
    public static void copyConfiguration(final TableColumn source, final TableColumn target) {
        /*
         * Note on set order: we set "max" first on the assumption that "min == 0", in
         * which case setting "max" should never fail.  Next we set the "min" with the
         * confidence that 0 <= min <= max. Finally we set the preferred width which
         * shall be between min and max. The width must be last, because setting the
         * preferred width invalidate it.
         */
        target.setMaxWidth(source.getMaxWidth());
        target.setMinWidth(source.getMinWidth());
        target.setPreferredWidth(source.getPreferredWidth());
        target.setWidth(source.getWidth());
        if (source instanceof TableColumnExt && target instanceof TableColumnExt) {
            final TableColumnExt se = (TableColumnExt) source;
            final TableColumnExt te = (TableColumnExt) target;
            te.setVisible(se.isVisible());
        }
    }
}
