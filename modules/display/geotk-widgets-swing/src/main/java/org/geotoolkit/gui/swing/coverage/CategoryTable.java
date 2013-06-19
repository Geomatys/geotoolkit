/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.awt.Component;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.opengis.metadata.content.TransferFunctionType;

import org.geotoolkit.util.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.gui.swing.ListTableModel;
import org.geotoolkit.gui.swing.image.PaletteComboBox;
import org.geotoolkit.internal.coverage.ColorPalette;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.lang.Debug;

import static org.geotoolkit.gui.swing.coverage.CategoryRecord.*;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * An editable table model for a list of {@link Category} items. Instances of this class
 * are typically created as below:
 *
 * {@preformat java
 *     GridSampleDimension band = ...;
 *     CategoryTable model = new CategoryTable(Locale.FRENCH);
 *     model.setCategories(band.getCategories());
 *
 *     JTable table = ...;
 *     model.configure(table);
 * }
 *
 * The default implementation provides the following columns (implementors can subclass this
 * model if they want to provides additional columns):
 * <p>
 * <ul>
 *   <li>The category name.</li>
 *   <li>Range of sample values:
 *   <ul>
 *     <li>The minimal sample value.</li>
 *     <li>The maximal sample value.</li>
 *   </ul></li>
 *   <li>Range of geophysics values:
 *   <ul>
 *     <li>The minimal geophysics value.</li>
 *     <li>The maximal geophysics value.</li>
 *   </ul></li>
 *   <li>Transfert function:
 *   <ul>
 *     <li>The {@linkplain TransferFunctionType transfer function type}.</li>
 *     <li>The offset term in the transfer function.</li>
 *     <li>The scale term in the transfer function.</li>
 *   </ul></li>
 *   <li>The color palette name, or a single color.</li>
 * </ul>
 * <p>
 * Some of the above-cited columns are inter-dependents. For example if the value of the
 * offset or scale factor is modified, then the minimal and maximal geophysics values will
 * be automatically recomputed. Or conversely, if the minimal or maximal geophysics values
 * is modified, then the offset and scale factors will be recomputed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.13
 * @module
 */
public class CategoryTable extends ListTableModel<CategoryRecord> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7923217651480496097L;

    /**
     * Sets to {@code true} for sending debugging information to the console output.
     */
    @Debug
    private static final boolean DEBUG = false;

    /**
     * The row height used by {@link #configure(JTable)}. The {@link JTable} default value
     * is 16 pixels, but we use a higher value in order to have room for superscripts in
     * exponential notation.
     */
    private static final int ROW_HEIGHT = 20;

    /**
     * Columns index.
     */
    private static final int NAME=0, SAMPLE_MIN=1, SAMPLE_MAX=2,
            MINIMUM=3, MAXIMUM=4, TYPE=5, OFFSET=6, SCALE=7, COLORS=8;

    /**
     * The type of the columns.
     */
    private static final Class<?>[] TYPES = new Class<?>[9];
    static {
        for (int i=0; i<TYPES.length; i++) {
            final Class<?> type;
            switch (i) {
                case COLORS:     // A palette name as a String.
                case NAME:       type = String.class; break;
                case SAMPLE_MIN:
                case SAMPLE_MAX: type = Integer.class; break;
                case TYPE:       type = TransferFunctionType.class; break;
                case OFFSET:
                case SCALE:
                case MINIMUM:
                case MAXIMUM:    type = Double.class; break;
                default:         type = Object.class; break;
            }
            TYPES[i] = type;
        }
    };

    /**
     * The locale to use for column headers and category descriptions.
     */
    final Locale locale;

    /**
     * The column headers.
     */
    private final String[] headers;

    /**
     * {@code true} if the table is editable.
     * Every {@code CategoryTable} instances are editable by default.
     */
    private boolean editable = true;

    /**
     * The factory to use for creating color palettes, or {@code null} for the default one.
     */
    final PaletteFactory paletteFactory;

    /**
     * The collection of {@link org.geotoolkit.internal.swing.table.ColorRampChoice}s.
     * We opportunistly keep this information after {@link #configure(JTable)} has been
     * invoked, in order leverage the cached colors.
     * <p>
     * This collection depends only on {@link #paletteFactory}, which is final. So it is
     * not a big deal if the field is computed more than once. It can also be {@code null},
     * in which case a temporary list will be created when needed (may be costly).
     */
    private transient ComboBoxModel<ColorPalette> paletteChoices;

    /**
     * Creates a new, initially empty, table.
     *
     * @param locale The locale to use for the column headers.
     */
    public CategoryTable(final Locale locale) {
        this(locale, null);
    }

    /**
     * Creates a new, initially empty, table.
     *
     * @param locale The locale to use for the column headers.
     * @param paletteFactory The factory to use for loading colors from a palette name,
     *        or {@code null} for the {@linkplain PaletteFactory#getDefault() default}.
     */
    CategoryTable(final Locale locale, final PaletteFactory paletteFactory) {
        super(CategoryRecord.class);
        this.locale = locale;
        this.paletteFactory = paletteFactory;
        final Vocabulary resources = Vocabulary.getResources(locale);
        headers = new String[TYPES.length];
        for (int i=0; i<TYPES.length; i++) {
            final int key;
            switch (i) {
                case NAME:       key = Vocabulary.Keys.NAME;    break;
                case SAMPLE_MIN:
                case MINIMUM:    key = Vocabulary.Keys.MINIMUM; break;
                case SAMPLE_MAX:
                case MAXIMUM:    key = Vocabulary.Keys.MAXIMUM; break;
                case TYPE:       key = Vocabulary.Keys.TYPE;    break;
                case OFFSET:     key = Vocabulary.Keys.OFFSET;  break;
                case SCALE:      key = Vocabulary.Keys.SCALE;   break;
                case COLORS:     key = Vocabulary.Keys.COLORS;  break;
                default: throw new AssertionError(i);
            }
            headers[i] = resources.getString(key);
        }
    }

    /**
     * Creates a new table initialized to the value of the given table.
     *
     * @param table The table from which to copy the rows.
     */
    public CategoryTable(final CategoryTable table) {
        super(CategoryRecord.class);
        locale         = table.locale;
        headers        = table.headers;
        paletteFactory = table.paletteFactory;
        for (final CategoryRecord record : table.elements) {
            elements.add(record.clone());
        }
    }

    /**
     * Returns all categories currently defined in this table. This is a convenience
     * which invoke {@link CategoryRecord#getCategory()} for each element returned by
     * {@link #getElements()}.
     *
     * @return The categories, or an empty list if none.
     */
    public List<Category> getCategories() {
        final List<Category> categories = new ArrayList<>(elements.size());
        for (final CategoryRecord record : elements) {
            categories.add(record.getCategory(paletteFactory));
        }
        return categories;
    }

    /**
     * Sets the categories to be shown in the table. This method removes every rows from this
     * table, then adds all categories from the given list wrapped in {@link CategoryRecord}s.
     * <p>
     * Alternatively, user can create {@link CategoryRecord} themself and invoke one of the
     * {@link #add add} or {@link #insert insert} methods directly.
     *
     * @param categories The categories to show, or {@code null} for clearing the table.
     */
    public void setCategories(final List<Category> categories) {
        elements.clear();
        if (!isNullOrEmpty(categories)) {
            for (final Category category : categories) {
                elements.add(new CategoryRecord(category, locale, paletteFactory, paletteChoices));
            }
        }
        fireTableDataChanged();
    }

    /**
     * Returns the number of columns in the table.
     */
    @Override
    public int getColumnCount() {
        return headers.length;
    }

    /**
     * Returns the name of the given column.
     *
     * @param  column The index of the column being queried.
     * @return The column name, localized in the local given to the constructor.
     */
    @Override
    public String getColumnName(final int column) {
        return headers[column];
    }

    /**
     * Returns the type of the given column. The default implementation returns the class
     * of {@link String}, {@link Integer}, {@link Double}, {@link TransferFunctionType} or
     * {@link Object} depending on the argument value.
     *
     * @param  column The index of the column being queried.
     * @return The column type.
     */
    @Override
    public Class<?> getColumnClass(int column) {
        return TYPES[column];
    }

    /**
     * Returns the minimal value of the given range, or {@code null} if the range is null.
     */
    private static Object getMinValue(final NumberRange<?> range) {
        return (range != null) ? range.getMinValue() : null;
    }

    /**
     * Returns the minimal value of the given range, or {@code null} if the range is null.
     */
    private static Object getMaxValue(final NumberRange<?> range) {
        return (range != null) ? range.getMaxValue() : null;
    }

    /**
     * Returns the value in the given cell.
     *
     * @param  row The index of the row being queried.
     * @param  column The index of the column being queried.
     * @return The value in the given cell, or {@code null} if none.
     */
    @Override
    public Object getValueAt(final int row, final int column) {
        final CategoryRecord record = elements.get(row);
        switch (column) {
            case NAME:       return record.getName();
            case SAMPLE_MIN: return getMinValue(record.getSampleRange());
            case SAMPLE_MAX: return getMaxValue(record.getSampleRange());
            case MINIMUM:    return getMinValue(record.getValueRange());
            case MAXIMUM:    return getMaxValue(record.getValueRange());
            case TYPE:       return record.getTransferFunctionType();
            case OFFSET:     return record.getCoefficient(0);
            case SCALE:      return record.getCoefficient(1);
            case COLORS:     return record.getPaletteName();
            default:         return null;
        }
    }

    /**
     * Sets the value in the given cell.
     *
     * @param value The new value.
     * @param row The index of the row being modified.
     * @param column The index of the column being modified.
     */
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        final CategoryRecord record = elements.get(row);
        final boolean changed;
        switch (column) {
            case NAME:       changed = record.setName((String) value); break;
            case SAMPLE_MIN: changed = record.setSampleRange((Integer) value, null); break;
            case SAMPLE_MAX: changed = record.setSampleRange(null, (Integer) value); break;
            case MINIMUM:    changed = record.setValueRange((Number) value, null); break;
            case MAXIMUM:    changed = record.setValueRange(null, (Number) value); break;
            case TYPE:       changed = record.setTransferFunctionType((TransferFunctionType) value); break;
            case OFFSET:     changed = record.setCoefficient(0, (Number) value); break;
            case SCALE:      changed = record.setCoefficient(1, (Number) value); break;
            case COLORS:     changed = record.setPaletteName((String) value); break;
            default:         changed = false; break;
        }
        if (changed) {
            // Consider that the whole row has been updated, not only the cell,
            // because the change in one cell may impact the value in other cells.
            fireTableRowsUpdated(row, row);
            if (DEBUG) {
                System.out.println(record);
            }
        }
    }

    /**
     * Returns {@code true} if the given cell is editable. The default implementation
     * returns the same value than {@link #isEditable()} for every cells.
     *
     * @param  row    The index of the row being queried.
     * @param  column The index of the column being queried.
     * @return {@code true} if the given cell is editable.
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return editable;
    }

    /**
     * Returns {@code true} if this table is editable.
     * Every {@code CategoryTable} instances are editable by default.
     *
     * @return {@code true} if this table is editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets whatever edition should be allowed for any cell in this table.
     * Editions are enabled by default, like most <cite>Swing</cite> components.
     *
     * @param editable {@code false} for disabling edition, or {@code true} for re-enabling it.
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    /**
     * Configures the given model before to allow the edition of a number in a cell.
     *
     * {@note This method can configure the spinner for any column, but is currently used only
     * for the sample values. Spinners were used for the other columns in a older version. The
     * code has been kept just in case.}
     */
    @SuppressWarnings("fallthrough")
    final void configure(final SpinnerNumberModel model, final int row, final int column) {
        final CategoryRecord record = elements.get(row);
        Comparable<?> minimum = null;
        Comparable<?> maximum = null;
        Number step = null;
        int extremum = -1;
        switch (column) {
            case SAMPLE_MAX: extremum = +1; // Fall through
            case SAMPLE_MIN: {
                final NumberRange<Integer> range = record.getValidSamples(extremum);
                minimum = range.getMinValue();
                maximum = range.getMaxValue();
                // Fall through
            }
            case SCALE: {
                step = 1;
                break;
            }
            case MAXIMUM: extremum = +1; // Fall through
            case MINIMUM: {
                final NumberRange<Double> range = record.getValidValues(extremum);
                minimum = range.getMinValue();
                maximum = range.getMaxValue();
                break;
            }
        }
        model.setMinimum(minimum);
        model.setMaximum(maximum);
        if (step == null) {
            step = record.getCoefficient(1); // Use the scale factor as the step.
            if (step != null) {
                step = Math.abs(step.doubleValue());
            } else {
                step = 1;
            }
        }
        model.setStepSize(step);
    }

    /**
     * Configures the given formatter before to render or edit a cell.
     */
    final void configure(final NumberFormat format, final int row) {
        elements.get(row).configure(format);
    }

    /**
     * Configures the given {@link JTable} for use with this model. This method performs
     * the following steps:
     * <p>
     * <ul>
     *   <li>{@linkplain JTable#setDefaultRenderer Install the cell renderers}.</li>
     *   <li>{@linkplain JTable#setDefaultEditor   Install the cell editors}.</li>
     *   <li>{@linkplain JTable#setRowHeight(int)  Modify the row height}.</li>
     *   <li>{@linkplain TableColumn#setPreferredWidth(int) Modify the preferred column width}.</li>
     *   <li>{@linkplain JTable#setPreferredSize   Set the table preferred size}.</li>
     * </ul>
     *
     * @param table The table in which to install the cell renderer and editors.
     */
    @SuppressWarnings("unchecked")
    public void configure(final JTable table) {
        final NumberEditor numberEditor = new NumberEditor(false);
        final CellRenderer renderer     = new CellRenderer(numberEditor.getFormat());
        table.setDefaultRenderer(Double.class,               renderer);
        table.setDefaultRenderer(TransferFunctionType.class, renderer);
        table.setDefaultEditor  (TransferFunctionType.class, new FunctionEditor(renderer.functionLabels));
        table.setDefaultEditor  (Integer.class, new NumberEditor(true));
        table.setDefaultEditor  (Double.class,  numberEditor);

        TableColumn column = table.getColumnModel().getColumn(COLORS);
        final PaletteComboBox palettesChoice = new PaletteComboBox(paletteFactory);
        palettesChoice.addDefaultColors();
        palettesChoice.useAsTableCellEditor(column);
        try {
            paletteChoices = ((Callable<ComboBoxModel<ColorPalette>>) column.getCellEditor()).call();
        } catch (Exception e) {
            // Should never happen. If it happen anyway, this is not a fatal error.
            // But log a complete warning with full stack trace so we can fix.
            Logging.unexpectedException(CategoryTable.class, "configure", e);
        }

        table.setRowHeight(ROW_HEIGHT);
        final TableColumnModel columns = table.getColumnModel();
        final int n = columns.getColumnCount();
        int total = 0;
        for (int i=0; i<n; i++) {
            final int width;
            switch (i) {
                case NAME:   width = 120; break;
                case TYPE:   width = 110; break;
                case COLORS: width =  80; break;
                default:     width =  70; break;
            }
            column = columns.getColumn(i);
            column.setPreferredWidth(width);
            total += width;
        }
        table.setPreferredSize(new Dimension(total, ROW_HEIGHT*4));
    }




    /**
     * A cell renderer for the enclosing {@link CategoryTable}. This renderer replaces the
     * {@link TransferFunctionType} enumeration by its formula.
     * <p>
     * <b>Note:</b> If the formulas are modified, then the {@link CategoryRecord#getValueRange()}
     * method (and related methods) implementation should be modified accordingly.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.13
     *
     * @since 3.13
     * @module
     */
    @SuppressWarnings("serial")
    private final class CellRenderer extends DefaultTableCellRenderer {
        /**
         * The choices to display in the function type combo box.
         */
        final String[] functionLabels;

        /**
         * The format to use for formatting real numbers (not integers). This is the same format,
         * than the one used by {@link NumberEditor}, in order to use the same format pattern.
         */
        private final NumberFormat format;

        /**
         * Creates a new editor for the given locale.
         *
         * @param format The format to use for formatting real numbers (not integers).
         */
        CellRenderer(final NumberFormat format) {
            functionLabels = new String[4];
            functionLabels[NONE]        = Vocabulary.getResources(locale).getString(Vocabulary.Keys.NONE);
            functionLabels[LINEAR]      = "<html><var>y</var> = A + B&middot;<var>x</var></html>";
            functionLabels[LOGARITHMIC] = "<html><var>y</var> = A + B&middot;log(<var>x</var>)</html>";
            functionLabels[EXPONENTIAL] = "<html><var>y</var> = 10<sup>A + B&middot;<var>x</var></sup></html>";
            this.format = format;
        }

        /**
         * Returns the cell renderer to use for rendering the given cell value. This method
         * modifies the value argument (if needed) before to pass them to the default renderer.
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, Object value,
                final boolean isSelected, final boolean hasFocus, final int row, final int column)
        {
            final int alignment;
            switch (column) {
                case TYPE: {
                    int code;
                    if (TransferFunctionType.LINEAR.equals(value)) {
                        code = LINEAR;
                    } else if (TransferFunctionType.LOGARITHMIC.equals(value)) {
                        code = LOGARITHMIC;
                    } else if (TransferFunctionType.EXPONENTIAL.equals(value)) {
                        code = EXPONENTIAL;
                    } else {
                        code = NONE;
                    }
                    value = functionLabels[code];
                    alignment = CENTER;
                    break;
                }
                case MINIMUM:
                case MAXIMUM:
                case OFFSET:
                case SCALE: {
                    if (value != null) {
                        configure(format, row);
                        value = format.format(((Number) value).doubleValue());
                    }
                    alignment = TRAILING;
                    break;
                }
                default: {
                    alignment = LEADING;
                    break;
                }
            }
            setHorizontalAlignment(alignment);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * A cell editor for the transfer function in the enclosing {@link CategoryTable}.
     * This editor uses a {@link JComboBox} with the same choices than the ones created
     * by {@link CellRenderer}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.13
     *
     * @since 3.13
     * @module
     */
    @SuppressWarnings("serial")
    private static final class FunctionEditor extends DefaultCellEditor {
        /**
         * The choices to display in the function type combo box.
         * This is the same array than {@link CellRenderer#functionLabels}.
         */
        private final String[] functionLabels;

        /**
         * Creates a new editor for the given labels.
         */
        FunctionEditor(final String[] functionLabels) {
            super(new JComboBox<>(functionLabels));
            this.functionLabels = functionLabels;
        }

        /**
         * Converts the given value from {@link TransferFunctionType} to {@link String}
         * before to set that value in the combo box.
         */
        @Override
        public Component getTableCellEditorComponent(final JTable table, Object value,
                final boolean isSelected, final int row, final int column)
        {
            final int code;
            if (TransferFunctionType.LINEAR.equals(value)) {
                code = LINEAR;
            } else if (TransferFunctionType.LOGARITHMIC.equals(value)) {
                code = LOGARITHMIC;
            } else if (TransferFunctionType.EXPONENTIAL.equals(value)) {
                code = EXPONENTIAL;
            } else {
                code = NONE;
            }
            value = functionLabels[code];
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        /**
         * Gets the selected value from the {@link JComboBox}, and converts
         * it from {@link String} to {@link TransferFunctionType}.
         */
        @Override
        public Object getCellEditorValue() {
            Object value = super.getCellEditorValue();
            for (int i=0; i<functionLabels.length; i++) {
                if (functionLabels[i].equals(value)) {
                    switch (i) {
                        case LINEAR:      return TransferFunctionType.LINEAR;
                        case LOGARITHMIC: return TransferFunctionType.LOGARITHMIC;
                        case EXPONENTIAL: return TransferFunctionType.EXPONENTIAL;
                        default: break;
                    }
                }
            }
            return null;
        }
    }

    /**
     * A cell editor for the numbers in the enclosing {@link CategoryTable}. This editor uses
     * a {@link JSpinner} for sample values, and {@link JFormattedTextField} for other values.
     *
     * {@note An older version used a <code>JSpinner</code> for every values, but the spinners have
     * been replaced by text fields since the spinners were consuming too much space in the table.}
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.15
     *
     * @since 3.13
     * @module
     */
    @SuppressWarnings("serial")
    private final class NumberEditor extends org.geotoolkit.internal.swing.table.NumberEditor implements ChangeListener {
        /**
         * The row and column currently in process of being edited.
         */
        private transient int row, column;

        /**
         * Creates a new editor.
         */
        NumberEditor(final boolean sampleValues) {
            super(locale, sampleValues);
        }

        /**
         * Sets the value together with the minimal and maximal allowed values for the
         * {@link JSpinner}, then return that component.
         */
        @Override
        public Component getTableCellEditorComponent(final JTable table, Object value,
                final boolean isSelected, final int row, final int column)
        {
            if (editorComponent instanceof JSpinner) {
                final SpinnerNumberModel model = (SpinnerNumberModel) ((JSpinner) editorComponent).getModel();
                model.removeChangeListener(this);
                this.row    = row;
                this.column = column;
                configure(model, row, column);
                if (value == null) {
                    value = Double.valueOf(column == SCALE ? 1 : 0);
                }
                model.setValue(value);
                model.addChangeListener(this);
            } else {
                ((JFormattedTextField) editorComponent).setValue(value);
                configure(getFormat(), row); // Must be after setValue.
            }
            return editorComponent;
        }

        /**
         * Invoked when the {@link JSpinner} value changed.
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            setValueAt(getCellEditorValue(), row, column);
        }
    }
}
