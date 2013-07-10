/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.geometry.Envelopes;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.geotoolkit.geometry.TransformedDirectPosition;


/**
 * A table of {@linkplain DirectPosition direct positions}. All coordinates contained in this
 * table have the same {@linkplain CoordinateReferenceSystem coordinate reference system}, which
 * is specified at construction time.
 * <p>
 * This table model provides a way to display invalid coordinates in a different color.
 * <cite>Invalid coordinates</cite> are defined here as coordinates outside the CRS
 * {@linkplain CoordinateReferenceSystem#getDomainOfValidity() domain of validity}.
 * This color display can be enabled by the following code:
 *
 * {@preformat java
 *     CoordinateTableModel model = new CoordinateTableModel(crs);
 *     JTable view = new JTable(model);
 *     TableCellRenderer renderer = new CellRenderer();
 *     view.setDefaultRenderer(Double.class, renderer);
 * }
 *
 * @author Cédric Briançon (Geomatys)
 * @author Hoa Nguyen (Geomatys)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class CoordinateTableModel extends AbstractTableModel {
    /**
     * The CRS for all coordinates in this table. This is specified by the user
     * at construction time.
     */
    private final CoordinateReferenceSystem crs ;

    /**
     * The columns table names. They are inferred from the table CRS specified
     * at construction time.
     */
    private final String[] columnNames;

    /**
     * The direct positions to display in the table.
     */
    private final List<DirectPosition> positions = new ArrayList<>();

    /**
     * An unmodifiable view of the positions list. This is the view returned by public accessors.
     * We do not allow addition or removal of positions through this list because such changes
     * would not invoke the proper {@code fire} method.
     */
    private final List<DirectPosition> unmodifiablePositions = Collections.unmodifiableList(positions);

    /**
     * The CRS valid area.
     */
    private final ImmutableEnvelope validArea;

    /**
     * For transformation from the table CRS to WGS84.
     */
    private final TransformedDirectPosition toWGS84 = new TransformedDirectPosition();

    /**
     * Creates an initially empty table model using the specified coordinate reference system.
     *
     * @param crs The Coordinate Reference System of all coordinates to appear in this model.
     */
    public CoordinateTableModel(final CoordinateReferenceSystem crs) {
        this.crs = crs;
        final CoordinateSystem cs = crs.getCoordinateSystem();
        columnNames = new String[cs.getDimension()];
        for (int i=0; i<columnNames.length; i++){
            columnNames[i] = crs.getCoordinateSystem().getAxis(i).getName().getCode();
        }
        validArea = new ImmutableEnvelope(Envelopes.getDomainOfValidity(crs));
    }

    /**
     * Returns the CRS for this table model
     *
     * @return The Coordinate Reference System of all coordinates to appear in this model.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Returns all direct positions in this table. The returned list is unmodifiable.
     *
     * @return All direct positions in this table.
     *
     * @see #add(DirectPosition)
     * @see #add(Collection)
     */
    public List<DirectPosition> getPositions() {
        return unmodifiablePositions;
    }

    /**
     * Returns the number of rows in the table.
     */
    @Override
    public int getRowCount() {
        return positions.size();
    }

    /**
     * Returns the number of columns in the table.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name for the specified column. The default implementation
     * returns the name of the corresponding axis in the table CRS.
     */
    @Override
    public String getColumnName(final int columnIndex){
        if (columnIndex >= 0 && columnIndex < columnNames.length) {
            return columnNames[columnIndex];
        } else {
            return super.getColumnName(columnIndex);
        }
    }

    /**
     * Returns tye type of data for the specified column. For coordinate table,
     * this is always {@code Double.class}.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Double.class;
    }

    /**
     * Adds a direct position to this table. The position is not cloned. Any cell edited in this
     * table will write its change directly into the corresponding {@code DirectPosition} object.
     *
     * @param newPosition The position to add to this table.
     */
    public void add(final DirectPosition newPosition) {
        final int index = positions.size();
        positions.add(newPosition);
        fireTableRowsInserted(index, index);
    }

    /**
     * Adds a collection of direct positions to this table. The position is not cloned.
     * Any cell edited in this table will write its change directly into the corresponding
     * {@code DirectPosition} object.
     *
     * @param newPositions The positions to add to this table.
     */
    public void add(final Collection<? extends DirectPosition> newPositions) {
        final int lower = positions.size();
        positions.addAll(newPositions);
        final int upper = positions.size();
        fireTableRowsInserted(lower, upper-1);
    }

    /**
     * Returns the value in the table at the specified postion.
     *
     * @param  rowIndex     Cell row number.
     * @param  columnIndex  Cell column number.
     * @return The ordinate value, or {@code null} if no value is available for the specified cell.
     */
    @Override
    public Number getValueAt(final int rowIndex, final int columnIndex) {
        if (rowIndex >= 0 && rowIndex < positions.size()) {
            final DirectPosition position = positions.get(rowIndex);
            if (position != null && columnIndex >= 0 && columnIndex < position.getDimension()) {
                final double ordinate = position.getOrdinate(columnIndex);
                if (!Double.isNaN(ordinate)) {
                    return ordinate;
                }
            }
        }
        return null;
    }

    /**
     * Sets the value for the specified cell.
     *
     * @param value         The new value for the cell.
     * @param rowIndex      Row number of the cell modified.
     * @param columnIndex   Column number of the cell modified.
     */
    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        final double ordinate = ((Number) value).doubleValue();
        positions.get(rowIndex).setOrdinate(columnIndex, ordinate);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Tells that the user can edit every rows in the table.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * Returns {@code true} if the position at the specified row is inside the CRS
     * {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain of validity}.
     * This method is invoked by {@link CellRenderer} in order to determine if this
     * row should be colorized.
     *
     * @param  rowIndex The index of the coordinate to test for validity.
     * @return {@code true} if the coordinate at the given index is valid.
     */
    public boolean isValidCoordinate(final int rowIndex) {
        final DirectPosition position = positions.get(rowIndex);
        try {
            toWGS84.transform(position);
        } catch (TransformException e) {
            /*
             * If the coordinate can't be transformed, then there is good chances
             * that the the coordinate is outside the CRS valid area.
             */
            return false;
        }
        return validArea.contains(toWGS84);
    }

    /**
     * Returns a string representation of this table. The default implementation
     * list all coordinates.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        final int size = positions.size();
        for (int i=0; i<size; i++) {
            buffer.append(positions.get(i)).append(lineSeparator);
        }
        return buffer.toString();
    }

    /**
     * A cell renderer for the {@linkplain CoordinateTableModel coordinate table model}.
     * This cell renderer can display in a different color coordinates outside the CRS
     * {@linkplain CoordinateReferenceSystem#getDomainOfValidity domain of validity}.
     * Coordinate validity is determined by invoking {@link CoordinateTableModel#isValidCoordinate}.
     *
     * @author Cédric Briançon (Geomatys)
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    public static class CellRenderer extends DefaultTableCellRenderer {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -2485722823332168812L;

        /**
         * The default text and background color.
         */
        private Color foreground, background;

        /**
         * The text and background color for invalid coordinates.
         */
        private Color invalidForeground = Color.RED, invalidBackground;

        /**
         * Creates a default cell renderer for {@link CoordinateTableModel}.
         */
        public CellRenderer() {
            super();
            foreground = super.getForeground();
            background = super.getBackground();
        }

        /**
         * Specifies the text color for valid coordinates.
         */
        @Override
        public void setForeground(final Color c) {
            this.foreground = c;
            super.setForeground(c);
        }

        /**
         * Specifies the background color for valid coordinates.
         */
        @Override
        public void setBackground(final Color c) {
            this.background = c;
            super.setBackground(c);
        }

        /**
         * Specified the text and background colors for invalid coordinates,
         * or {@code null} for the same color than valid coordinates.
         *
         * @param foreground The foreground color to use for invalid coordinates, or {@code null}.
         * @param background The background color to use for invalid coordinates, or {@code null}.
         */
        public void setInvalidColor(final Color foreground, final Color background) {
            this.invalidForeground = foreground;
            this.invalidBackground = background;
        }

        /**
         * Returns the component for cell rendering.
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                final boolean isSelected, final boolean hasFocus, final int row, final int column)
        {
            Color foreground = this.foreground;
            Color background = this.background;
            final TableModel candidate = table.getModel();
            if (candidate instanceof CoordinateTableModel) {
                final CoordinateTableModel model = (CoordinateTableModel) candidate;
                if (!model.isValidCoordinate(row)) {
                    if (invalidForeground != null) foreground = invalidForeground;
                    if (invalidBackground != null) background = invalidBackground;
                }
            }
            super.setBackground(background);
            super.setForeground(foreground);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
