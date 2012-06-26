/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.Objects;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.Format;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.AbstractTableModel;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;

import javax.media.jai.util.Range;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.PerspectiveTransform;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RegistryElementDescriptor;

import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.gui.swing.image.KernelEditor;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.internal.image.Adapters;
import org.geotoolkit.resources.Vocabulary;

import static java.awt.GridBagConstraints.*;
import static org.geotoolkit.util.converter.Numbers.*;


/**
 * An editor for arbitrary parameter object. The parameter value can be any {@link Object}.
 * The editor content will changes according the parameter class. For example, the content
 * will be a {@link KernelEditor} if the parameter is an instance of {@link KernelJAI}.
 * Currently supported parameter type includes:
 * <p>
 * <ul>
 *   <li>Individual {@linkplain String string}, {@linkplain Number number}, {@linkplain Date date}
 *       or {@linkplain Angle angle}.</li>
 *   <li>Table of any primitive type ({@code int[]}, {@code float[]}, etc.).</li>
 *   <li>Matrix of any primitive type ({@code int[][]}, {@code float[][]}, etc.).</li>
 *   <li>JAI {@linkplain LookupTableJAI lookup table}, which are display in tabular format.</li>
 *   <li>{@linkplain AffineTransform Affine transform} and {@linkplain PerspectiveTransform
 *       perspective transform}, which are display like a matrix.</li>
 *   <li>Convolution {@linkplain KernelJAI kernel}, which are display in a {@link KernelEditor}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.12
 *
 * @see org.geotoolkit.gui.swing.image.KernelEditor
 * @see org.geotoolkit.gui.swing.image.ImageProperties
 * @see org.geotoolkit.gui.swing.image.OperationTreeBrowser
 *
 * @since 2.0
 * @module
 *
 * @todo This class do not yet support the edition of parameter value.
 */
@SuppressWarnings("serial")
public class ParameterEditor extends JComponent {
    /** Key for {@link String} node.    */  private static final String STRING  = "String";
    /** Key for {@link Boolean} node.   */  private static final String BOOLEAN = "Boolean";
    /** Key for {@link Number} node.    */  private static final String NUMBER  = "Number";
    /** Key for {@link Angle} node.     */  private static final String ANGLE   = "Angle";
    /** Key for {@link Date} node.      */  private static final String DATE    = "Date";
    /** Key for {@link KernelJAI} node. */  private static final String KERNEL  = "Kernel";
    /** Key for any kind of table node. */  private static final String TABLE   = "Table";
    /** Key for unrecognized types.     */  private static final String DEFAULT = "Default";

    /**
     * The set of {@linkplain Component component} editors created up to date.
     */
    private final Map<String,Component> editors = new HashMap<>();

    /**
     * The properties panel for parameters. The content for this panel
     * depends on the selected item, but usually includes the following:
     * <p>
     * <ul>
     *   <li>A {@link JTextField} for simple parameters (numbers, string, etc.)</li>
     *   <li>A {@link JList} for enumerated parameters.</li>
     *   <li>A {@link JTable} for any kind of array parameter and {@link LookupTableJAI}.</li>
     *   <li>A {@link KernelEditor} for {@link KernelJAI} parameters.</li>
     * </ul>
     */
    private final Container cards = new JPanel(new CardLayout());

    /**
     * The label for parameter or image description.
     * Usually displayed on top of parameter editor.
     */
    private final JLabel description = new JLabel(" ", JLabel.CENTER);

    /**
     * The current value in the process of being edited. This object is usually an instance of
     * {@link Number}, {@link KernelJAI}, {@link LookupTableJAI} or some other parameter object.
     *
     * @see #setParameterValue
     */
    private Object value;

    /**
     * The editor widget currently in use.
     *
     * @see #setParameterValue
     * @see #getEditor
     */
    private Component editor;

    /**
     * The editor model currently in use. This is often the model used by the editor widget.
     */
    private Editor model;

    /**
     * {@code true} if this widget is editable.
     */
    private static final boolean editable = false;

    /**
     * Constructs an initially empty parameter editor.
     */
    public ParameterEditor() {
        setLayout(new BorderLayout());
        description.setBorder(
                BorderFactory.createCompoundBorder(description.getBorder(),
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 9, 6, 9),
                BorderFactory.createLineBorder(description.getForeground())),
                BorderFactory.createEmptyBorder(6, 0, 6, 0))));
        add(description, BorderLayout.NORTH );
        add(cards,       BorderLayout.CENTER);
        setPreferredSize(new Dimension(400,250));
    }

    /**
     * Returns the parameter value currently edited, or {@code null} if none.
     *
     * @return the parameter value currently edited, or {@code null}.
     */
    public Object getParameterValue() {
        return (model != null) ? model.getValue() : value;
    }

    /**
     * Sets the value to edit. The editor content will be updated according the value type.
     * For example if the value is an instance of {@link KernelJAI}, then the editor content
     * will be changed to a {@link KernelEditor}.
     *
     * @param value The value to edit. This object is usually an instance of {@link Number},
     *              {@link KernelJAI}, {@link LookupTableJAI} or some other parameter object.
     */
    public void setParameterValue(final Object value) {
        final Object oldValue = this.value;
        if (!Objects.deepEquals(value, oldValue)) {
            this.value = value;
            updateEditor();
            firePropertyChange("value", oldValue, value);
        }
    }

    /**
     * Returns the description currently shown, or {@code null} if none. This is usually a short
     * description of the parameter being edited. The string may contain simple HTML tags.
     *
     * @return The description currently shown, or {@code null}.
     */
    public String getDescription() {
        String text = description.getText();
        if (text != null) {
            text = text.trim();
            if (text.isEmpty()) {
                text = null;
            }
        }
        return text;
    }

    /**
     * Sets the description to shown. This is usually a short description of the parameter
     * being edited. Simple HTML tags are allowed since the description will be rendered
     * with {@link JLabel}, which allows such tags.
     *
     * @param description The description to be shown.
     */
    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            description = " ";
        }
        this.description.setText(description);
        if (model != null) {
            model.setValueRange(null, null);
        }
    }

    /**
     * Convenience method for setting the parameter description from a JAI operation node. This
     * method fetches the description from the {@linkplain OperationDescriptor operation descriptor}
     * associated with the given node, if any. Then it invokes {@link #setDescription(String)} with
     * the description found, or a description built from other informations like the parameter type
     * if no explicit description was found.
     *
     * @param operation The operation node for the current parameter.
     * @param index     The parameter index, or {@code -1} if unknown.
     *
     * @since 2.3
     */
    public void setDescription(final OperationNode operation, final int index) {
        String   description = null;
        Class<?> type        = null;
        Range    range       = null;
        if (operation != null) {
            final String name, mode;
            final RegistryElementDescriptor element;
            final ParameterListDescriptor param;
            name    = operation.getOperationName();
            mode    = operation.getRegistryModeName();
            element = operation.getRegistry().getDescriptor(mode, name);
            param   = element.getParameterListDescriptor(mode);
            /*
             * If a parameter is specified, gets the parameter type and its range of valid
             * values.
             */
            String pname = null;
            if (index >= 0 && index < param.getNumParameters()) {
                pname = param.getParamNames()[index];
                type  = param.getParamClasses()[index];
                range = param.getParamValueRange(param.getParamNames()[index]);
            }
            /*
             * If the descriptor is an operation, gets the localized operation
             * description or the parameter description.
             */
            if (element instanceof OperationDescriptor) {
                final String key;
                final OperationDescriptor descriptor = (OperationDescriptor) element;
                final ResourceBundle resources = descriptor.getResourceBundle(getLocale());
                if (index >= 0) {
                    key = "arg" + index + "Desc";
                } else {
                    key = "Description";
                }
                try {
                    description = resources.getString(key);
                } catch (MissingResourceException ignore) {
                    // No description for this parameter. Try a global description.
                    try {
                        description = resources.getString("Description");
                    } catch (MissingResourceException exception) {
                        /*
                         * No description at all for this operation. Not a big deal;
                         * just left the description empty. Log the exception with a
                         * low level, since this warning is not really important. The
                         * level is slightly higher than in 'RegisteredOperationBrowser'
                         * since we have tried the global operation description as well.
                         */
                        Logging.recoverableException(ParameterEditor.class, "setDescription", exception);
                    }
                }
            }
            /*
             * Concatenates the parameter name and description as a HTML string.
             * This block can be disabled if only the description as plain text is wanted.
             */
            if (true) {
                final StringBuilder html = new StringBuilder("<html><center><b>").append(name);
                if (pname != null) {
                    html.append(' ').append(pname);
                }
                html.append("</b>");
                if (description != null) {
                    html.append("<br>").append(description);
                }
                description = html.append("</center></html>").toString();
            }
        }
        setDescription(description);
        if (model != null) {
            model.setValueRange(type, range);
        }
    }

    /**
     * Returns the component used for editing the parameter. The component class depends on the
     * class of the value set by the last call to {@link #setParameterValue}. The editor may be
     * an instance of {@link KernelEditor}, {@link JTable}, {@link JTextField}, {@link JList} or
     * any other suitable component.
     *
     * @return The editor, or {@code null} if no value has been set.
     */
    public Component getEditor() {
        return editor;
    }

    /**
     * Returns the editor for the given name. If an editor is found, it will be bring
     * on top of the card layout (i.e. will become the visible editor). Otherwise, this
     * method returns {@code null}.
     *
     * @param  name The editor name. Should be one of {@link #NUMBER}, {@link #KERNEL} and
     *         similar constants.
     * @return The editor, or {@code null}.
     */
    private Component getEditor(final String name) {
        final Component panel = editors.get(name);
        ((CardLayout) cards.getLayout()).show(cards, name);
        return panel;
    }

    /**
     * Adds the specified editor. No editor must exists for the specified name prior to this
     * call. The editor will be bring on top of the card layout (i.e. will become the visible
     * panel).
     *
     * @param  name The editor name. Should be one of {@link #NUMBER}, {@link #KERNEL} and
     *         similar constants.
     * @param  editor The editor.
     * @param  scroll {@code true} if the editor should be wrapped into a {@link JScrollPane}
     *         prior its addition to the container.
     */
    private void addEditor(final String name, Component editor, final boolean scroll) {
        if (editors.put(name, editor) != null) {
            throw new IllegalStateException(name); // Should not happen.
        }
        if (scroll) {
            editor = new JScrollPane(editor);
        }
        cards.add(editor, name);
        ((CardLayout) cards.getLayout()).show(cards, name);
    }

    /**
     * Updates the editor according the current {@link #value}. If a suitable editors already
     * exists for the value class, it will be reused. Otherwise, a new editor will be created
     * on the fly.
     *
     * The {@link #editor} field will be set to the component used for editing the parameter.
     * This component may be an instance of {@link KernelEditor}, {@link JTable},
     * {@link JTextField}, {@link JList} or any other suitable component.
     *
     * The {@link #model} field will be set to the model used by the editor widget.
     */
    @SuppressWarnings("fallthrough")
    private void updateEditor() {
        Object value = this.value;
        /*
         * In the special case where the value is an array with only one element, extract
         * the element and use a specialized editor as if the element wasn't in an array.
         */
        while (value != null && value.getClass().isArray() && Array.getLength(value) == 1) {
            value = Array.get(value, 0);
        }
        /*
         * String  ---  Uses a JTextField editor.
         */
        if (value instanceof String) {
            Singleton editor = (Singleton) getEditor(STRING);
            if (editor == null) {
                editor = new Singleton(null);
                addEditor(STRING, editor, false);
            }
            editor.setValue(value);
            this.editor = editor.field;
            this.model  = editor;
            return;
        }
        /*
         * Boolean  ---  Uses a JTextField editor.
         */
        if (value instanceof Boolean) {
            Singleton editor = (Singleton) getEditor(BOOLEAN);
            if (editor == null) {
                editor = new Singleton(null); // TODO: we should define some kind of BooleanFormat.
                addEditor(BOOLEAN, editor, false);
            }
            editor.setValue(value);
            this.editor = editor.field;
            this.model  = editor;
            return;
        }
        /*
         * Number  ---  Uses a JFormattedTextField editor.
         */
        if (value instanceof Number) {
            Singleton editor = (Singleton) getEditor(NUMBER);
            if (editor == null) {
                editor = new Singleton(NumberFormat.getInstance(getLocale()));
                addEditor(NUMBER, editor, false);
            }
            editor.setValue(value);
            this.editor = editor.field;
            this.model  = editor;
            return;
        }
        /*
         * Date  ---  Uses a JFormattedTextField editor.
         */
        if (value instanceof Date) {
            Singleton editor = (Singleton) getEditor(DATE);
            if (editor == null) {
                editor = new Singleton(DateFormat.getDateTimeInstance(
                        DateFormat.LONG, DateFormat.LONG, getLocale()));
                addEditor(DATE, editor, false);
            }
            editor.setValue(value);
            this.editor = editor.field;
            this.model  = editor;
            return;
        }
        /*
         * Angle  ---  Uses a JFormattedTextField editor.
         */
        if (value instanceof Angle) {
            Singleton editor = (Singleton) getEditor(ANGLE);
            if (editor == null) {
                editor = new Singleton(AngleFormat.getInstance(getLocale()));
                addEditor(ANGLE, editor, false);
            }
            editor.setValue(value);
            this.editor = editor.field;
            this.model  = editor;
            return;
        }
        /*
         * AffineTransform  ---  converts to a matrix for processing by the general matrix case.
         */
        if (value instanceof AffineTransform) {
            final AffineTransform transform = (AffineTransform) value;
            value = new double[][] {
                {transform.getScaleX(), transform.getShearX(), transform.getTranslateX()},
                {transform.getShearY(), transform.getScaleY(), transform.getTranslateY()},
                {0, 0, 1}
            };
        }
        /*
         * PerspectiveTransform  ---  converts to a matrix for processing by the general matrix case.
         */
        if (value instanceof PerspectiveTransform) {
            final double[][] matrix = new double[3][3];
            ((PerspectiveTransform) value).getMatrix(matrix);
            value = matrix;
        }
        /*
         * Any table or matrix  ---  uses a JTable editor.
         */
        if (value != null) {
            final Class<?> elementClass = value.getClass().getComponentType();
            if (elementClass != null) {
                final TableModel model;
                if (elementClass.isArray()) {
                    model = new Matrix((Object[]) value);
                } else {
                    model = new Table(new Object[] {value}, 0, 0);
                }
                JTable editor = (JTable) getEditor(TABLE);
                if (editor == null) {
                    editor = new NumberedTable(model);
                    addEditor(TABLE, editor, true);
                } else {
                    editor.setModel(model);
                }
                this.editor = editor;
                this.model  = (Editor) model;
                return;
            }
        }
        /*
         * LookupTableJAI  ---  Uses a JTable editor.
         */
        if (value instanceof LookupTableJAI) {
            final LookupTableJAI table = (LookupTableJAI) value;
            final Object[] data;
            int mask = 0;
            switch (table.getDataType()) {
                case DataBuffer.TYPE_BYTE:   data = table.getByteData(); mask=0xFF; break;
                case DataBuffer.TYPE_USHORT: mask = 0xFFFF; // Fall through
                case DataBuffer.TYPE_SHORT:  data = table.getShortData();  break;
                case DataBuffer.TYPE_INT:    data = table.getIntData();    break;
                case DataBuffer.TYPE_FLOAT:  data = table.getFloatData();  break;
                case DataBuffer.TYPE_DOUBLE: data = table.getDoubleData(); break;
                default: this.editor=null; this.model=null; return;
            }
            final Table model = new Table(data, table.getOffset(), mask);
            JTable editor = (JTable) getEditor(TABLE);
            if (editor == null) {
                editor = new NumberedTable(model);
                addEditor(TABLE, editor, true);
            } else {
                editor.setModel(model);
            }
            this.editor = editor;
            this.model  = model;
            return;
        }
        /*
         * KernelJAI  ---  Uses a KernelEditor.
         */
        if (value instanceof KernelJAI) {
            KernelEditor editor = (KernelEditor) getEditor(KERNEL);
            if (editor == null) {
                editor = new KernelEditor();
                editor.addDefaultKernels();
                addEditor(KERNEL, editor, false);
            }
            editor.setKernel((KernelJAI) value);
            this.editor = editor;
            this.model  = null; // TODO: Set the editor.
            return;
        }
        /*
         * Default case  ---  Uses a JTextArea
         */
        JTextArea editor = (JTextArea) getEditor(DEFAULT);
        if (editor == null) {
            editor = new JTextArea();
            editor.setEditable(false);
            editor.setFont(Font.decode("Monospaced"));
            addEditor(DEFAULT, editor, true);
        }
        String text = String.valueOf(value);
        if (text.indexOf('\n') < 0 && text.indexOf('\r') < 0 && text.indexOf(' ') >= 0) {
            if (value instanceof ColorModel) {
                text = multilines(text);
            }
        }
        editor.setText(text);
        this.editor = editor;
        this.model  = null; // TODO: Set the editor.
    }

    /**
     * Transforms the given single line into a multilines text. This method expects a
     * string having the following pattern:
     *
     * {@preformat text
     *     <optional header:> key1 = value1 key2 = value2 key3 = value3 ...
     * }
     *
     * Each key-value pair will be formatted on its own line.
     */
    private static String multilines(final String text) {
        int splitAt = text.indexOf(':') + 1;
        final StringBuilder buffer = new StringBuilder(splitAt).append(text, 0, splitAt);
        final StringTokenizer tk = new StringTokenizer(text.substring(splitAt));
        int state = 0; // 0=new line, 1=key, 2=value.
        while (tk.hasMoreTokens()) {
            if (state == 0) {
                buffer.append("\n  ");
                state = 1;
            }
            final String token = tk.nextToken();
            buffer.append(' ').append(token);
            if (state == 2) {
                state = 0;
            } else if (token.equals("=")) {
                state = 2;
            }
        }
        return buffer.toString();
    }

    /**
     * The interface for editor capable to returns the edited value.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     *
     * @todo This interface should have a {@code setEditable(boolean)} method.
     */
    private interface Editor {
        /**
         * Returns the edited value.
         */
        Object getValue();

        /**
         * Sets the type and the range of valid values.
         */
        void setValueRange(final Class<?> type, final Range range);
    }

    /**
     * An editor panel for editing a single value. The value if usually an instance of
     * {@link Number}, {@link Date}, {@link Angle}, {@link Boolean} or {@link String}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     *
     * @todo This editor should use {@code JSpinner}, but we need to gets
     *       the minimum and maximum values first since spinner needs bounds.
     */
    @SuppressWarnings("serial")
    private static final class Singleton extends JComponent implements Editor {
        /**
         * The data type.
         */
        private final JLabel type = new JLabel();

        /**
         * The minimum allowed value.
         */
        private final JLabel range = new JLabel();

        /**
         * The text field for editing the value.
         */
        private final JTextField field;

        /**
         * Construct an editor for value using the specified format.
         */
        public Singleton(final Format format) {
            setLayout(new GridBagLayout());
            if (format != null) {
                field = new JFormattedTextField(format);
            } else {
                field = new JTextField();
            }
            field.setEditable(editable);
            final Vocabulary resources = Vocabulary.getResources(getLocale());
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx=0; c.gridwidth=1; c.insets.left=9; c.fill=HORIZONTAL;
            c.gridy=0; add(new JLabel(resources.getLabel(Vocabulary.Keys.TYPE )), c);
            c.gridy++; add(new JLabel(resources.getLabel(Vocabulary.Keys.RANGE)), c);
            c.gridy++; add(new JLabel(resources.getLabel(Vocabulary.Keys.VALUE)), c);
            c.gridx=1; c.weightx=1; c.insets.right=9;
            c.gridy=0; add(type,  c);
            c.gridy++; add(range, c);
            c.gridy++; add(field, c);
        }

        /**
         * Set the value to be edited.
         */
        public void setValue(final Object value) {
            if (field instanceof JFormattedTextField) {
                ((JFormattedTextField) field).setValue(value);
            } else {
                field.setText(String.valueOf(value));
            }
        }

        /**
         * Returns the edited value.
         */
        @Override
        public Object getValue() {
            if (field instanceof JFormattedTextField) {
                return ((JFormattedTextField) field).getValue();
            } else {
                return field.getText();
            }
        }

        /**
         * Sets the type and the range of valid values.
         */
        @Override
        public void setValueRange(Class<?> classe, final Range range) {
            String type = null;
            String rtxt = null;
            if (classe != null) {
                while (classe.isArray()) {
                    classe = classe.getComponentType();
                }
                type = Classes.getShortName(classe);
                classe = primitiveToWrapper(classe);
                boolean isInteger = false;
                if (isFloat(classe) || (isInteger = isInteger(classe)) == true) {
                    type = Vocabulary.format(isInteger ? Vocabulary.Keys.SIGNED_INTEGER_$1 :
                            Vocabulary.Keys.REAL_NUMBER_$1, primitiveBitCount(classe)) +
                            " (" + type + ')';
                }
            }
            if (range != null) {
                rtxt = Adapters.convert(range).toString();
            }
            this.type .setText(type);
            this.range.setText(rtxt);
        }
    }

    /**
     * The table for viewing data here the first column is row number (typically index of an array).
     * The only difference compared to standard {@link JTable} is that the first column is rendered
     * in a different color, providing that the underlying model is {@link Table}. Otherwise (in
     * particular if the underlying model is {@link Matrix}), this class does nothing special.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    @SuppressWarnings("serial")
    private static final class NumberedTable extends JTable {
        /**
         * The table cell renderer for the first column.
         */
        private final TableCellRenderer rowHeaders;

        /**
         * {@code true} if the first column should be rendered as row header.
         */
        private boolean hasHeaders;

        /**
         * Creates a new table initialized to the given model.
         */
        public NumberedTable(final TableModel model) {
            super(model);
            rowHeaders = SwingUtilities.setupAsRowHeader(this);
            hasHeaders = (model instanceof Table);
        }

        /**
         * Sets a new table model.
         */
        @Override
        public void setModel(final TableModel model) {
            hasHeaders = (model instanceof Table);
            super.setModel(model);
        }

        /**
         * Returns the cell renderer for the given row.
         */
        @Override
        public TableCellRenderer getCellRenderer(final int row, final int column) {
            if (hasHeaders && column == 0) {
                return rowHeaders;
            }
            return super.getCellRenderer(row, column);
        }
    }

    /**
     * Table model for table parameters (including {@link LookupTableJAI}.
     * Instance of this class are created by {@link #updateEditor} when first needed.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    @SuppressWarnings("serial")
    private static final class Table extends AbstractTableModel implements Editor {
        /**
         * The table (usually an instance of {@code double[][]}).
         */
        private final Object[] table;

        /**
         * The offset parameter (a {@link LookupTableJAI} property).
         */
        private final int offset;

        /**
         * The mask to apply on unsigned values, or 0 if the values are signed.
         */
        private final int mask;

        /**
         * Constructs a model for the given table.
         *
         * @param table  The table (usually an instance of {@code double[][]}).
         * @param offset The offset parameter (a {@link LookupTableJAI} property).
         * @param mask   The mask to apply on unsigned values, or 0 if the values are signed.
         */
        public Table(final Object[] table, final int offset, final int mask) {
            this.table  = table;
            this.offset = offset;
            this.mask   = mask;
        }

        /**
         * Returns the number of rows in the table.
         */
        @Override
        public int getRowCount() {
            int count = 0;
            for (int i=0; i<table.length; i++) {
                final int length = Array.getLength(table[i]);
                if (length > count) {
                    count = length;
                }
            }
            return count;
        }

        /**
         * Returns the number of columns in the model.
         */
        @Override
        public int getColumnCount() {
            return Array.getLength(table) + 1;
        }

        /**
         * Returns the name of the column at the specified index.
         */
        @Override
        public String getColumnName(final int index) {
            switch (index) {
                case 0:  return Vocabulary.format(Vocabulary.Keys.INDEX);
                default: return Vocabulary.format(Vocabulary.Keys.VALUE);
            }
        }

        /**
         * Returns the most specific superclass for all the cell values.
         */
        @Override
        public Class<?> getColumnClass(final int index) {
            if (index == 0) return String.class; // Row headers.
            if (mask  != 0) return Integer.class; // Type used for unsigned values.
            return primitiveToWrapper(table[index-1].getClass().getComponentType());
        }

        /**
         * Tells if the specified cell is editable.
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return editable && column != 0;
        }

        /**
         * Returns the value at the specified index.
         */
        @Override
        public Object getValueAt(final int row, final int column) {
            if (column == 0) {
                return (row + offset) + "  ";
            }
            final Object array = table[column-1];
            if (mask != 0) {
                return Integer.valueOf(Array.getInt(array, row) & mask);
            }
            return Array.get(array, row);
        }

        /**
         * Sets the value at the given index.
         */
        @Override
        public void setValueAt(final Object value, final int row, final int column) {
            Array.set(table[column-1], row, value);
        }

        /**
         * Returns the edited value.
         */
        @Override
        public Object getValue() {
            return table;
        }

        /**
         * Sets the type and the range of valid values.
         * The default implementation does nothing.
         */
        @Override
        public void setValueRange(final Class<?> type, final Range range) {
        }
    }

    /**
     * Table model for matrix parameters. Instance of this class
     * are created by {@link #updateEditor} when first needed.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    @SuppressWarnings("serial")
    private static final class Matrix extends AbstractTableModel implements Editor {
        /**
         * The matrix (usually an instance of {@code double[][]}).
         */
        private final Object[] matrix;

        /**
         * Construct a model for the given matrix.
         *
         * @param matrix The matrix (usually an instance of {@code double[][]}).
         */
        public Matrix(final Object[] matrix) {
            this.matrix = matrix;
        }

        /**
         * Returns the number of rows in the matrix.
         */
        @Override
        public int getRowCount() {
            return matrix.length;
        }

        /**
         * Returns the number of columns in the model. This is the length of the longest
         * row in the matrix.
         */
        @Override
        public int getColumnCount() {
            int count = 0;
            for (int i=0; i<matrix.length; i++) {
                final int length = Array.getLength(matrix[i]);
                if (length > count) {
                    count = length;
                }
            }
            return count;
        }

        /**
         * Returns the name of the column at the specified index.
         */
        @Override
        public String getColumnName(final int index) {
            return Integer.toString(index);
        }

        /**
         * Returns the most specific superclass for all the cell values.
         */
        @Override
        public Class<?> getColumnClass(final int index) {
            return primitiveToWrapper(matrix.getClass().getComponentType().getComponentType());
        }

        /**
         * Tells if the specified cell is editable.
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return editable;
        }

        /**
         * Returns the value at the specified index.
         */
        @Override
        public Object getValueAt(final int row, final int column) {
            final Object array = matrix[row];
            return (column < Array.getLength(array)) ? Array.get(array, column) : null;
        }

        /**
         * Sets the value at the given index.
         */
        @Override
        public void setValueAt(final Object value, final int row, final int column) {
            Array.set(matrix[row], column, value);
        }

        /**
         * Returns the edited value.
         */
        @Override
        public Object getValue() {
            return matrix;
        }

        /**
         * Sets the type and the range of valid values.
         * The default implementation does nothing.
         */
        @Override
        public void setValueRange(final Class<?> type, final Range range) {
        }
    }
}
