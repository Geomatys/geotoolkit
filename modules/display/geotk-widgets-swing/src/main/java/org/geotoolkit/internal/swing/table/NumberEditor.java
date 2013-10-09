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
package org.geotoolkit.internal.swing.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.EventObject;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.InternationalFormatter;


/**
 * Table cell editor for formatting numbers, either using {@link JSpinner} or
 * {@link JFormattedTextField}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15 (derived from 3.13)
 * @module
 */
@SuppressWarnings("serial")
public class NumberEditor extends AbstractCellEditor implements TableCellEditor {
    /**
     * The component used for editing the value, either a {@link JSpinner} or
     * a {@link JFormattedTextField}.
     */
    protected final JComponent editorComponent;

    /**
     * Creates a new editor.
     *
     * @param locale  The locale to use for creating a number format.
     * @param spinner {@code true} for creating a {@link JSpinner}.
     */
    public NumberEditor(final Locale locale, final boolean spinner) {
        if (spinner) {
            editorComponent = new Spinner();
        } else {
            editorComponent = new FormattedTextField(NumberFormat.getNumberInstance(locale));
        }
        editorComponent.setBorder(null);
    }

    /**
     * Creates a new editor using a specific format.
     *
     * @param format {@link Format} used in the FormattedTextField.
     */
    public NumberEditor(Format format) {
        editorComponent = new FormattedTextField(format);
        editorComponent.setBorder(null);
    }

    /**
     * Returns the format used by the spinner or the formatted text field.
     *
     * @return The format used by the editor.
     */
    public final NumberFormat getFormat() {
        if (editorComponent instanceof JSpinner) {
            return ((JSpinner.NumberEditor) ((JSpinner) editorComponent).getEditor()).getFormat();
        } else {
            return (NumberFormat) ((InternationalFormatter) ((JFormattedTextField) editorComponent).getFormatter()).getFormat();
        }
    }

    /**
     * Sets the value for the {@link JSpinner} or {@link JFormattedTextField},
     * then return that component.
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value,
            final boolean isSelected, final int row, final int column)
    {
        if (editorComponent instanceof JSpinner) {
            ((SpinnerNumberModel) ((JSpinner) editorComponent).getModel()).setValue(value);
        } else {
            ((JFormattedTextField) editorComponent).setValue(value);
        }
        return editorComponent;
    }

    /**
     * Returns {@code true} if the cell is editable as a result of the given event.
     * If the event is a mouse event, we require a double-click.
     *
     * @param  event The mouse event.
     * @return {@code true} if the cell is editable.
     */
    @Override
    public boolean isCellEditable(final EventObject event) {
        if (event instanceof MouseEvent) {
            return ((MouseEvent) event).getClickCount() >= 2;
        }
        return super.isCellEditable(event);
    }

    /**
     * Returns the edited value.
     */
    @Override
    public Object getCellEditorValue() {
        if (editorComponent instanceof JSpinner) {
            return ((JSpinner) editorComponent).getValue();
        } else {
            return ((JFormattedTextField) editorComponent).getValue();
        }
    }

    /**
     * Invoked when the editing stopped. This method commit the value
     * in the {@link JFormattedTextField}.
     *
     * @return {@code true} if the value is valid.
     */
    @Override
    public boolean stopCellEditing() {
        try {
            if (editorComponent instanceof JSpinner) {
                ((JSpinner) editorComponent).commitEdit();
            } else {
                ((JFormattedTextField) editorComponent).commitEdit();
            }
        } catch (ParseException e) {
            return false;
        }
        return super.stopCellEditing();
    }
}
