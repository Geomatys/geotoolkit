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
package org.geotoolkit.gui.swing.image;

import java.awt.Component;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.image.io.metadata.MetadataTreeNode;
import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.internal.swing.table.BooleanRenderer;
import org.geotoolkit.internal.swing.table.IdentifiedObjectRenderer;
import org.geotoolkit.internal.swing.table.JTables;
import org.opengis.referencing.IdentifiedObject;

import static org.geotoolkit.image.io.metadata.MetadataTreeTable.COLUMN_COUNT;
import static org.geotoolkit.image.io.metadata.MetadataTreeTable.VALUE_COLUMN;


/**
 * The {@code TreeTable} implementation for {@link IIOMetadataPanel}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @see MetadataTreeTable
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
final class IIOMetadataTreeTable extends JXTreeTable implements StringValue {
    /**
     * The currently selected tree node. This is read and set by {@link IIMetadataPanel} in
     * order to remember the selected item of that particular view when we switch between
     * different metadata views.
     */
    MetadataTreeNode selectedNode;

    /**
     * Renderers for special kind of values.
     */
    private final TableCellRenderer booleanRenderer, identifiedObjectRenderer;

    /**
     * Creates a new table for the given table. The given root <strong>must</strong> be
     * the value returned by {@link MetadataTreeTable#getRootNode()}, or something having
     * the same structure.
     *
     * @param root The output of {@link MetadataTreeTable#getRootNode()}.
     * @param visibleTable The table which was show prior the invocation of this constructor, or
     *        {@code null} if none. This is used for copying some properties like the columns
     *        positions.
     */
    IIOMetadataTreeTable(final TreeTableNode root, final IIOMetadataTreeTable visibleTable) {
        super(new Model(root));
        ((Model) getTreeTableModel()).owner = this;
        setRootVisible(false);
        setColumnControlVisible(true);
        setDefaultRenderer(Class.class, new DefaultTableRenderer(this));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booleanRenderer = new BooleanRenderer();
        identifiedObjectRenderer = new IdentifiedObjectRenderer();
        /*
         * Assign programmatic identifiers to the columns, for handling by JTables static
         * methods. We need to remove the "value" column if there is no such column.
         */
        final TableColumnModel columns = getColumnModel();
        int c = columns.getColumnCount();
        String[] identifiers = Model.IDENTIFIERS;
        if (c != COLUMN_COUNT) {
            identifiers = ArraysExt.remove(identifiers, VALUE_COLUMN, 1);
        }
        JTables.setIdentifiers(columns, (Object[]) identifiers);
        /*
         * Hide every columns except the one for the names and only one additional column:
         *
         *   - For IIOMetadataFormat, the type
         *   - For IIOMetadata, the value.
         *
         * Note that the "default value" column is empty most of the time anyway. The only
         * non-null values are usually "false" for the boolean type. The user can select
         * columns to be made visible with the icon in the upper-right corner.
         */
        if (visibleTable == null) {
            final int keep = (c == COLUMN_COUNT) ? VALUE_COLUMN : 2;
            while (--c >= 1) {
                if (c != keep) {
                    ((TableColumnExt) columns.getColumn(c)).setVisible(false);
                }
            }
            /*
             * --- MAINTENANCE NOTE: --------------------------------------
             *
             *     If the preferred colum width below is modified, consider
             *     updating the preferred panel width in IIOMetadataPanel.
             */
            columns.getColumn(0).setPreferredWidth(240);
        } else {
            /*
             * Just replicate the settings of the previous table.
             */
            JTables.copyConfiguration(visibleTable.getColumnModel(), columns);
        }
    }

    /**
     * The table model for the {@link javax.imageio.metadata.IIOMetadata}
     * or {@link javax.imageio.metadata.IIOMetadataFormat}. The columns
     * are documented in the {@link MetadataTreeTable} javadoc.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    private static final class Model extends org.geotoolkit.gui.swing.TreeTableModelAdapter {
        /**
         * Columns identifiers for programmatic purpose.
         * Shall be consistent with {@link #getColumnName}.
         */
        static final String[] IDENTIFIERS = {
            "name", "description", "type", "occurrence", "value", "default", "validValues"
        };

        /**
         * The component which own this model.
         * This is used only for fetching the locale.
         */
        Component owner;

        /**
         * Creates a model for the given root. The given root <strong>must</strong> be
         * the value returned by {@link MetadataTreeTable#getRootNode()}, or something
         * having the same structure.
         */
        Model(final TreeTableNode root) {
            super(root);
        }

        /**
         * Returns the name of the given column. The columns shall
         * matches the ones documented in {@link MetadataTreeTable}.
         */
        @Override
        public String getColumnName(int column) {
            final short key;
            if (column >= VALUE_COLUMN) {
                column += COLUMN_COUNT - getColumnCount();
                // Skip the "values" column if it doesn't exist.
            }
            switch (column) {
                case 0:            key = Vocabulary.Keys.NAME;         break;
                case 1:            key = Vocabulary.Keys.DESCRIPTION;  break;
                case 2:            key = Vocabulary.Keys.TYPE;         break;
                case 3:            key = Vocabulary.Keys.OCCURRENCE;   break;
                case VALUE_COLUMN: key = Vocabulary.Keys.VALUE;        break;
                case 5:            key = Vocabulary.Keys.DEFAULT;      break;
                case 6:            key = Vocabulary.Keys.VALID_VALUES; break;
                case COLUMN_COUNT:
                // The later is added only for making sure at compile-time that
                // we are not declaring more columns than the expected number.
                default: return super.getColumnName(column);
            }
            final Component owner = this.owner;
            return Vocabulary.getResources(owner != null ? owner.getLocale() : null).getString(key);
        }
    }

    /**
     * Returns the string representation of a few types to be handled especially. This
     * is used only when the whole column has the same type, otherwise we need to use
     * {@link #getCellRenderer(int, int)}.
     */
    @Override
    public String getString(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Class<?>) {
            return ((Class<?>) value).getSimpleName();
        }
        return value.toString();
    }

    /**
     * Returns the renderer for the given cell. This method returns a special
     * renderer for the {@link Boolean} type.
     */
    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {
        final Object value = getValueAt(row, column);
        if (value instanceof Boolean) {
            return booleanRenderer;
        }
        if (value instanceof IdentifiedObject) {
            return identifiedObjectRenderer;
        }
        return super.getCellRenderer(row, column);
    }
}
